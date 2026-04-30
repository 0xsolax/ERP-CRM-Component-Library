package com.qmy.project.core.auth.service.strategy.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.qmy.project.common.constants.TenantConfigCodeConstants;
import com.qmy.project.common.enums.ScanLoginTypeEnum;
import com.qmy.project.common.exception.ServiceException;
import com.qmy.project.common.exception.ServiceExceptionUtil;
import com.qmy.project.core.auth.model.AuthSettings;
import com.qmy.project.core.auth.service.strategy.ScanLoginStrategy;
import com.qmy.project.common.constants.AuthErrorCodeConstants;
import com.qmy.project.core.tenant.manager.TenantConfigManager;
import com.qmy.project.core.tenant.model.entity.TenantConfigDO;
import com.qmy.project.core.user.model.vo.ThirdPartyUserIdentityVO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.qmy.project.common.utils.ValidityUtils.isBlank;
import static com.qmy.project.common.utils.ValidityUtils.isNotBlank;

/**
 * @author shanyitian
 * @description 钉钉扫码登录策略（钉钉开放平台 OAuth2 userAccessToken + 通讯录当前用户）
 * @date 2026/03/29 22:49
 */
@Component
public class DingtalkScanLoginStrategy implements ScanLoginStrategy {

    private static final String DINGTALK_OPEN_API_BASE = "https://api.dingtalk.com";

    private static final int HTTP_TIMEOUT_MS = 10000;

    private final AuthSettings authSettings;

    private final TenantConfigManager tenantConfigManager;

    public DingtalkScanLoginStrategy(AuthSettings authSettings, TenantConfigManager tenantConfigManager) {
        this.authSettings = authSettings;
        this.tenantConfigManager = tenantConfigManager;
    }

    @Override
    public ScanLoginTypeEnum getType() {
        return ScanLoginTypeEnum.DINGTALK;
    }

    @Override
    public ThirdPartyUserIdentityVO authenticate(String code, @SuppressWarnings("unused") String referer) {
        // 钉钉换票接口无需 redirectUri；referer 与 ScanLoginStrategy 签名对齐，供前端与飞书策略一致传参
        AuthSettings.Provider dingYaml = authSettings.getScan().getDingtalk();
        Map<String, String> tenantCfg = dingtalkTenantConfigMap();
        String clientId = firstNonBlank(tenantCfg.get(TenantConfigCodeConstants.DINGTALK_APP_KEY), dingYaml.getAppId());
        String clientSecret = firstNonBlank(tenantCfg.get(TenantConfigCodeConstants.DINGTALK_APP_SECRET), dingYaml.getAppSecret());
        if (isBlank(clientId) || isBlank(clientSecret)) {
            throw ServiceExceptionUtil.exception(AuthErrorCodeConstants.SCAN_LOGIN_NOT_READY);
        }
        if (isBlank(code)) {
            throw new ServiceException(
                    AuthErrorCodeConstants.DINGTALK_SCAN_AUTH_FAILED.getCode(),
                    "钉钉授权失败：授权码为空");
        }
        String userAccessToken = exchangeUserAccessToken(code.trim(), clientId.trim(), clientSecret.trim());
        return buildIdentityFromContactMe(userAccessToken);
    }

    private Map<String, String> dingtalkTenantConfigMap() {
        List<TenantConfigDO> rows = tenantConfigManager.listByCodes(List.of(
                TenantConfigCodeConstants.DINGTALK_APP_KEY,
                TenantConfigCodeConstants.DINGTALK_APP_SECRET));
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
     * 通过 OAuth 授权码换取用户 access_token（扫码登录用）
     *
     * @see <a href="https://open.dingtalk.com/document/orgapp/obtain-user-token">获取用户 token</a>
     */
    private String exchangeUserAccessToken(String authCode, String clientId, String clientSecret) {
        String url = DINGTALK_OPEN_API_BASE + "/v1.0/oauth2/userAccessToken";
        JSONObject body = JSONUtil.createObj()
                .set("clientId", clientId)
                .set("clientSecret", clientSecret)
                .set("code", authCode)
                .set("grantType", "authorization_code");
        String response = HttpRequest.post(url)
                .header("Content-Type", "application/json")
                .body(body.toString())
                .timeout(HTTP_TIMEOUT_MS)
                .execute()
                .body();
        if (isBlank(response)) {
            throw new ServiceException(
                    AuthErrorCodeConstants.DINGTALK_SCAN_AUTH_FAILED.getCode(),
                    "获取钉钉用户 token 失败：响应为空");
        }
        JSONObject result = JSONUtil.parseObj(response);
        String accessToken = result.getStr("accessToken");
        if (isBlank(accessToken)) {
            String errMsg = dingtalkErrorMessage(result);
            throw new ServiceException(
                    AuthErrorCodeConstants.DINGTALK_SCAN_AUTH_FAILED.getCode(),
                    "获取钉钉用户 token 失败：" + errMsg);
        }
        return accessToken;
    }

    /**
     * 使用用户 access_token 获取当前授权用户通讯录信息（含 openId、unionId 等）
     *
     * @see <a href="https://open.dingtalk.com/document/app/dingtalk-retrieve-user-information">获取用户通讯录个人信息</a>
     */
    private ThirdPartyUserIdentityVO buildIdentityFromContactMe(String userAccessToken) {
        String url = DINGTALK_OPEN_API_BASE + "/v1.0/contact/users/me";
        HttpResponse httpResponse = HttpRequest.get(url)
                .header("x-acs-dingtalk-access-token", userAccessToken)
                .timeout(HTTP_TIMEOUT_MS)
                .execute();
        String body = httpResponse.body();
        if (httpResponse.getStatus() != 200 || isBlank(body)) {
            throw new ServiceException(
                    AuthErrorCodeConstants.DINGTALK_SCAN_AUTH_FAILED.getCode(),
                    "获取钉钉用户信息失败：" + (isBlank(body) ? ("HTTP " + httpResponse.getStatus()) : body));
        }
        JSONObject json = JSONUtil.parseObj(body);
        String openId = json.getStr("openId");
        if (isBlank(openId)) {
            throw new ServiceException(
                    AuthErrorCodeConstants.DINGTALK_SCAN_AUTH_FAILED.getCode(),
                    "获取钉钉用户 openId 失败：" + dingtalkErrorMessage(json));
        }
        ThirdPartyUserIdentityVO identity = new ThirdPartyUserIdentityVO();
        identity.setPlatform(getType().getCode());
        identity.setOpenId(openId);
        String unionId = json.getStr("unionId");
        identity.setUnionId(isBlank(unionId) ? null : unionId);
        identity.setThirdUserId(unionId);
        identity.setNickName(json.getStr("nick"));
        identity.setAvatarUrl(json.getStr("avatarUrl"));
        identity.setEmail(json.getStr("email"));
        identity.setMobile(json.getStr("mobile"));
        return identity;
    }

    private static String dingtalkErrorMessage(JSONObject result) {
        String msg = result.getStr("message");
        if (isBlank(msg)) {
            msg = result.getStr("msg");
        }
        if (isBlank(msg)) {
            String code = result.getStr("code");
            if (isNotBlank(code)) {
                msg = code;
            } else {
                msg = result.toString();
            }
        }
        return msg;
    }
}
