package com.qmy.project.core.auth.service.strategy.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.qmy.project.core.auth.model.AuthSettings;
import com.qmy.project.core.user.model.vo.ThirdPartyUserIdentityVO;
import com.qmy.project.common.constants.TenantConfigCodeConstants;
import com.qmy.project.common.enums.ScanLoginTypeEnum;
import com.qmy.project.common.constants.AuthErrorCodeConstants;
import com.qmy.project.common.exception.ServiceException;
import com.qmy.project.common.exception.ServiceExceptionUtil;
import com.qmy.project.core.auth.service.strategy.ScanLoginStrategy;
import com.qmy.project.core.tenant.manager.TenantConfigManager;
import com.qmy.project.core.tenant.model.entity.TenantConfigDO;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.qmy.project.common.utils.ValidityUtils.isBlank;
import static com.qmy.project.common.utils.ValidityUtils.isNotBlank;

/**
 * @author shanyitian
 * @description 飞书扫码策略
 * @date 2026/03/29 22:49
 */
@Component
public class FeishuScanLoginStrategy implements ScanLoginStrategy {

    private static final String FEISHU_OPEN_API_BASE = "https://open.feishu.cn/open-apis/authen";

    private static final int HTTP_TIMEOUT_MS = 10000;

    private final AuthSettings authSettings;

    private final TenantConfigManager tenantConfigManager;

    public FeishuScanLoginStrategy(AuthSettings authSettings, TenantConfigManager tenantConfigManager) {
        this.authSettings = authSettings;
        this.tenantConfigManager = tenantConfigManager;
    }

    @Override
    public ScanLoginTypeEnum getType() {
        return ScanLoginTypeEnum.FEISHU;
    }

    @Override
    public ThirdPartyUserIdentityVO authenticate(String code, String referer) {
        AuthSettings.Provider feishuYaml = authSettings.getScan().getFeishu();
        Map<String, String> tenantCfg = feishuTenantConfigMap();
        String appId = firstNonBlank(tenantCfg.get(TenantConfigCodeConstants.FEISHU_APP_ID), feishuYaml.getAppId());
        String appSecret = firstNonBlank(tenantCfg.get(TenantConfigCodeConstants.FEISHU_APP_SECRET), feishuYaml.getAppSecret());
        if (isBlank(appId) || isBlank(appSecret)) {
            throw ServiceExceptionUtil.exception(AuthErrorCodeConstants.SCAN_LOGIN_NOT_READY);
        }
        String redirectUri = resolveRedirectUri(referer, feishuYaml.getRedirectUri());
        String accessToken = exchangeUserAccessToken(code, appId, appSecret, redirectUri);
        return buildIdentityFromUserInfo(accessToken);
    }

    private Map<String, String> feishuTenantConfigMap() {
        List<TenantConfigDO> rows = tenantConfigManager.listByCodes(List.of(
                TenantConfigCodeConstants.FEISHU_APP_ID,
                TenantConfigCodeConstants.FEISHU_APP_SECRET));
        return rows.stream()
                .filter(r -> r.getConfigCode() != null)
                .collect(Collectors.toMap(TenantConfigDO::getConfigCode, TenantConfigDO::getConfigValue, (a, b) -> b));
    }

    private static String firstNonBlank(String primary, String fallback) {
        if (isNotBlank(primary)) {
            return primary.trim();
        }
        return isBlank(fallback) ? null : fallback.trim();
    }

    /**
     * 优先从 {@code Referer} 解析当前页地址（去掉 query、fragment），与飞书授权回调页一致；
     * 否则回退 {@code auth.scan.feishu.redirect-uri}。
     */
    private String resolveRedirectUri(String refererHeader, String configuredRedirectUri) {
        String fromReferer = redirectUriFromReferer(refererHeader);
        if (isNotBlank(fromReferer)) {
            return fromReferer;
        }
        if (isNotBlank(configuredRedirectUri)) {
            return configuredRedirectUri.trim();
        }
        throw ServiceExceptionUtil.exception(AuthErrorCodeConstants.SCAN_LOGIN_NOT_READY);
    }

    /**
     * 例如 {@code http://localhost:6802/zs/login?code=...} → {@code http://localhost:6802/zs/login}
     */
    private String redirectUriFromReferer(String referer) {
        if (isBlank(referer)) {
            return null;
        }
        try {
            URI u = URI.create(referer.trim());
            if (u.getScheme() == null || u.getHost() == null) {
                return null;
            }
            String path = u.getPath();
            if (path == null || path.isEmpty()) {
                path = "/";
            }
            return new URI(u.getScheme(), u.getAuthority(), path, null, null).toString();
        } catch (Exception e) {
            return null;
        }
    }

    private String exchangeUserAccessToken(String code, String appId, String appSecret, String redirectUri) {
        String url = FEISHU_OPEN_API_BASE + "/v2/oauth/token";
        String body = HttpRequest.post(url)
                .form("grant_type", "authorization_code")
                .form("code", code)
                .form("client_id", appId)
                .form("client_secret", appSecret)
                .form("redirect_uri", redirectUri)
                .timeout(HTTP_TIMEOUT_MS)
                .execute()
                .body();
        JSONObject json = JSONUtil.parseObj(body);
        Integer errCode = json.getInt("code");
        if (errCode != null && errCode != 0) {
            throw feishuFail("换取用户 access_token 失败", json);
        }
        String accessToken = json.getStr("access_token");
        if (isBlank(accessToken)) {
            throw feishuFail("换取用户 access_token 失败：返回为空", json);
        }
        return accessToken;
    }

    private ThirdPartyUserIdentityVO buildIdentityFromUserInfo(String userAccessToken) {
        String url = FEISHU_OPEN_API_BASE + "/v1/user_info";
        String body = HttpRequest.get(url)
                .header("Authorization", "Bearer " + userAccessToken)
                .timeout(HTTP_TIMEOUT_MS)
                .execute()
                .body();
        JSONObject json = JSONUtil.parseObj(body);
        Integer c = json.getInt("code");
        if (c == null || c != 0) {
            throw feishuFail("获取飞书用户信息失败", json);
        }
        JSONObject data = json.getJSONObject("data");
        if (data == null) {
            throw new ServiceException(
                    AuthErrorCodeConstants.FEISHU_SCAN_AUTH_FAILED.getCode(),
                    "获取飞书用户信息失败：data 为空");
        }
        String openId = data.getStr("open_id");
        if (isBlank(openId)) {
            throw new ServiceException(
                    AuthErrorCodeConstants.FEISHU_SCAN_AUTH_FAILED.getCode(),
                    "获取飞书用户 open_id 失败");
        }
        ThirdPartyUserIdentityVO identity = new ThirdPartyUserIdentityVO();
        identity.setPlatform(getType().getCode());
        identity.setOpenId(openId);
        String unionId = data.getStr("union_id");
        identity.setUnionId(isBlank(unionId) ? null : unionId);
        identity.setThirdUserId(openId);
        String name = data.getStr("name");
        if (isBlank(name)) {
            name = data.getStr("en_name");
        }
        identity.setNickName(name);
        String avatar = data.getStr("avatar_url");
        if (isBlank(avatar)) {
            avatar = data.getStr("avatar_big");
        }
        identity.setAvatarUrl(avatar);
        identity.setEmail(data.getStr("email"));
        identity.setMobile(data.getStr("mobile"));
        return identity;
    }

    private ServiceException feishuFail(String prefix, JSONObject json) {
        String msg = json.getStr("msg");
        if (isBlank(msg)) {
            msg = json.toString();
        }
        return new ServiceException(AuthErrorCodeConstants.FEISHU_SCAN_AUTH_FAILED.getCode(), prefix + "：" + msg);
    }
}
