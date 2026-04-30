package com.qmy.project.core.tenant.service.impl;

import com.qmy.project.common.constants.TenantConfigCodeConstants;
import com.qmy.project.common.error.GlobalErrorCodeConstants;
import com.qmy.project.common.error.TenantErrorCodeConstants;
import com.qmy.project.common.exception.ServiceExceptionUtil;
import com.qmy.project.common.utils.ValidityUtils;
import com.qmy.project.core.file.enums.SystemFileMainTypeEnum;
import com.qmy.project.core.file.enums.SystemFileSubTypeEnum;
import com.qmy.project.core.file.model.vo.FileVO;
import com.qmy.project.core.file.service.SystemFileService;
import com.qmy.project.core.tenant.manager.TenantConfigManager;
import com.qmy.project.core.tenant.model.entity.TenantConfigDO;
import com.qmy.project.core.tenant.model.vo.TenantInfoVO;
import com.qmy.project.core.tenant.service.TenantConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 17:03
 */
@Service
@RequiredArgsConstructor
public class TenantConfigServiceImpl implements TenantConfigService {

    private final TenantConfigManager tenantConfigManager;

    private final SystemFileService systemFileService;

    @Override
    public List<TenantConfigDO> listTenantConfigsForFeishuAlert() {
        return tenantConfigManager.listByCodes(TenantConfigCodeConstants.getTenantFeishuAlertCodes());
    }

    @Override
    public TenantInfoVO getByDomainName(String domainName) {
        if (ValidityUtils.isBlank(domainName)) {
            throw ServiceExceptionUtil.exception(GlobalErrorCodeConstants.NOT_FOUND);
        }
        List<TenantConfigDO> configRows = tenantConfigManager.listByCodes(TenantConfigCodeConstants.getTenantPublicProfileCodes());
        Map<String, String> cfg = configRows.stream()
                .filter(r -> r.getConfigCode() != null && r.getConfigValue() != null)
                .collect(Collectors.toMap(TenantConfigDO::getConfigCode, TenantConfigDO::getConfigValue, (a, b) -> b));
        String configuredDomain = cfg.get(TenantConfigCodeConstants.DOMAIN_NAME);
        if (configRows.isEmpty() || ValidityUtils.isBlank(configuredDomain)) {
            throw ServiceExceptionUtil.exception(TenantErrorCodeConstants.TENANT_CONFIG_NOT_FOUND);
        }
        if (!configuredDomain.trim().equalsIgnoreCase(domainName.trim())) {
            throw ServiceExceptionUtil.exception(TenantErrorCodeConstants.TENANT_DOMAIN_MISMATCH);
        }
        Map<SystemFileSubTypeEnum, List<FileVO>> grouped =
                systemFileService.listFilesGroupedByMainType(SystemFileMainTypeEnum.TENANT);
        TenantInfoVO vo = new TenantInfoVO();
        vo.setId(Long.parseLong(cfg.get(TenantConfigCodeConstants.TENANT_ID)));
        vo.setName(cfg.get(TenantConfigCodeConstants.TENANT_NAME));
        vo.setSlogan(cfg.get(TenantConfigCodeConstants.TENANT_SLOGAN));
        vo.setAccountSystemKey(cfg.get(TenantConfigCodeConstants.ACCOUNT_SYSTEM_KEY));
        vo.setFeiShuAppId(cfg.get(TenantConfigCodeConstants.FEISHU_APP_ID));
        vo.setDingTalkAppKey(cfg.get(TenantConfigCodeConstants.DINGTALK_APP_KEY));
        vo.setDingTalkAppSecret(cfg.get(TenantConfigCodeConstants.DINGTALK_APP_SECRET));
        vo.setBackgroundFileList(grouped.get(SystemFileSubTypeEnum.BACKGROUND));
        vo.setLoginLogoFileList(grouped.get(SystemFileSubTypeEnum.LOGIN_LOGO));
        vo.setMenuCollapsedLogoFileList(grouped.get(SystemFileSubTypeEnum.MENU_COLLAPSED_LOGO));
        vo.setMenuExpandedLogoFileList(grouped.get(SystemFileSubTypeEnum.MENU_EXPANDED_LOGO));
        return vo;
    }
}
