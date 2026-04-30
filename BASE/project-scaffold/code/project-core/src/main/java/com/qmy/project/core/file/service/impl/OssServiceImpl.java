package com.qmy.project.core.file.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.sts.model.v20150401.AssumeRoleRequest;
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse;
import com.qmy.project.common.error.OssErrorCodeConstants;
import com.qmy.project.common.exception.ServiceExceptionUtil;
import com.qmy.project.common.constants.TenantConfigCodeConstants;
import com.qmy.project.core.file.model.OssStsTokenInfo;
import com.qmy.project.core.file.model.vo.OssStsTokenVO;
import com.qmy.project.core.file.service.OssService;
import com.qmy.project.core.tenant.manager.TenantConfigManager;
import com.qmy.project.core.tenant.model.entity.TenantConfigDO;
import com.qmy.project.common.utils.BeanUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 09:49
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OssServiceImpl implements OssService {

    private final TenantConfigManager tenantConfigManager;

    @Override
    public OssStsTokenVO getOssToken() {
        List<TenantConfigDO> rows = tenantConfigManager.listByCodes(TenantConfigCodeConstants.getTenantOssStsCodes());
        Map<String, String> cfg = rows.stream().filter(r -> r.getConfigCode() != null).collect(Collectors.toMap(TenantConfigDO::getConfigCode, TenantConfigDO::getConfigValue, (a, b) -> b));
        validateStsConfig(cfg);
        try {
            DefaultProfile.addEndpoint(cfg.get(TenantConfigCodeConstants.OSS_STS_REGION_ID), "Sts", cfg.get(TenantConfigCodeConstants.OSS_STS_ENDPOINT));
            IClientProfile profile = DefaultProfile.getProfile(cfg.get(TenantConfigCodeConstants.OSS_STS_REGION_ID), cfg.get(TenantConfigCodeConstants.OSS_ACCESS_KEY_ID), cfg.get(TenantConfigCodeConstants.OSS_ACCESS_KEY_SECRET)
            );
            DefaultAcsClient client = new DefaultAcsClient(profile);

            AssumeRoleRequest request = new AssumeRoleRequest();
            request.setSysMethod(MethodType.POST);
            request.setRoleArn(cfg.get(TenantConfigCodeConstants.OSS_STS_ROLE_ARN));
            request.setRoleSessionName(cfg.get(TenantConfigCodeConstants.OSS_STS_ROLE_SESSION_NAME));
            request.setDurationSeconds(Long.parseLong(cfg.get(TenantConfigCodeConstants.OSS_STS_DURATION_SECONDS)));
            String policy = cfg.get(TenantConfigCodeConstants.OSS_STS_POLICY);
            if (StrUtil.isNotBlank(policy)) {
                request.setPolicy(policy);
            }

            AssumeRoleResponse response = client.getAcsResponse(request);
            OssStsTokenInfo tokenInfo = buildOssStsTokenInfo(response, cfg);
            return BeanUtils.toBean(tokenInfo, OssStsTokenVO.class);
        } catch (ClientException exception) {
            log.error("Get OSS STS token failed, code={}, message={}, requestId={}",
                    exception.getErrCode(), exception.getErrMsg(), exception.getRequestId(), exception);
            throw ServiceExceptionUtil.exception(
                    OssErrorCodeConstants.OSS_STS_TOKEN_ERROR.getCode(),
                    OssErrorCodeConstants.OSS_STS_TOKEN_ERROR.getMessage() + ":" + exception.getErrMsg()
            );
        }
    }

    private static OssStsTokenInfo buildOssStsTokenInfo(AssumeRoleResponse response, Map<String, String> cfg) {
        OssStsTokenInfo tokenInfo = new OssStsTokenInfo();
        tokenInfo.setExpiration(response.getCredentials().getExpiration());
        tokenInfo.setAccessKeyId(response.getCredentials().getAccessKeyId());
        tokenInfo.setAccessKeySecret(response.getCredentials().getAccessKeySecret());
        tokenInfo.setSecurityToken(response.getCredentials().getSecurityToken());
        tokenInfo.setRequestId(response.getRequestId());
        tokenInfo.setEndpoint(cfg.get(TenantConfigCodeConstants.OSS_ENDPOINT));
        tokenInfo.setBucketName(cfg.get(TenantConfigCodeConstants.OSS_BUCKET_NAME));
        return tokenInfo;
    }

    private void validateStsConfig(Map<String, String> cfg) {
        if (StrUtil.hasBlank(cfg.get(TenantConfigCodeConstants.OSS_ENDPOINT), cfg.get(TenantConfigCodeConstants.OSS_BUCKET_NAME), cfg.get(TenantConfigCodeConstants.OSS_ACCESS_KEY_ID),
                cfg.get(TenantConfigCodeConstants.OSS_ACCESS_KEY_SECRET), cfg.get(TenantConfigCodeConstants.OSS_STS_REGION_ID), cfg.get(TenantConfigCodeConstants.OSS_STS_ENDPOINT), cfg.get(TenantConfigCodeConstants.OSS_STS_ROLE_ARN),
                cfg.get(TenantConfigCodeConstants.OSS_STS_ROLE_SESSION_NAME), cfg.get(TenantConfigCodeConstants.OSS_STS_DURATION_SECONDS))) {
            throw ServiceExceptionUtil.exception(OssErrorCodeConstants.OSS_STS_CONFIG_MISSING);
        }
    }
}
