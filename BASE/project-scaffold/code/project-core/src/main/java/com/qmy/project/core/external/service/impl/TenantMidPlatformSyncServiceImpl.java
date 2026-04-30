package com.qmy.project.core.external.service.impl;

import com.qmy.project.api.dto.file.SystemFileDTO;
import com.qmy.project.api.dto.midplatform.TenantMidPlatformSyncRequest;
import com.qmy.project.common.constants.TenantConfigCodeConstants;
import com.qmy.project.common.enums.ScanLoginTypeEnum;
import com.qmy.project.common.error.TenantErrorCodeConstants;
import com.qmy.project.common.exception.ServiceExceptionUtil;
import com.qmy.project.common.utils.BeanUtils;
import com.qmy.project.common.utils.ValidityUtils;
import com.qmy.project.core.external.service.TenantMidPlatformSyncService;
import com.qmy.project.core.file.enums.SystemFileMainTypeEnum;
import com.qmy.project.core.file.enums.SystemFileSubTypeEnum;
import com.qmy.project.core.file.manager.SystemFileManager;
import com.qmy.project.core.file.model.entity.SystemFileDO;
import com.qmy.project.core.tenant.manager.TenantConfigManager;
import com.qmy.project.core.tenant.model.entity.TenantConfigDO;
import com.qmy.project.core.user.manager.UserBindManager;
import com.qmy.project.core.user.manager.UserManager;
import com.qmy.project.core.user.model.entity.UserBindDO;
import com.qmy.project.core.user.model.entity.UserDO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * @author AI Coding
 */
@Service
@RequiredArgsConstructor
public class TenantMidPlatformSyncServiceImpl implements TenantMidPlatformSyncService {

    private final TenantConfigManager tenantConfigManager;

    private final SystemFileManager systemFileManager;

    private final UserManager userManager;

    private final UserBindManager userBindManager;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncFromMidPlatformRequest(TenantMidPlatformSyncRequest request) {
        // 参数校验
        validateRequest(request);
        // 保存租户基本信息
        syncTenantConfig(request);
        // 保存租户超级管理员信息
        syncSuperAdminUser(request);
        // 保存租户文件信息
        syncTenantSystemFiles(request);
    }

    /**
     * 参数校验
     *
     * @param request 请求
     */
    private void validateRequest(TenantMidPlatformSyncRequest request) {
        List<TenantConfigDO> idRows = tenantConfigManager.listByCodes(List.of(TenantConfigCodeConstants.TENANT_ID));
        if (idRows.isEmpty() || ValidityUtils.isBlank(idRows.getFirst().getConfigValue())) {
            throw ServiceExceptionUtil.exception(TenantErrorCodeConstants.TENANT_CONFIG_NOT_FOUND);
        }
        String configuredId = idRows.getFirst().getConfigValue().trim();
        if (!configuredId.equals(String.valueOf(request.getId()))) {
            throw ServiceExceptionUtil.exception(TenantErrorCodeConstants.TENANT_ID_MISMATCH);
        }
    }

    /**
     * 同步租户配置
     *
     * @param request 请求
     */
    private void syncTenantConfig(TenantMidPlatformSyncRequest request) {
        if (request.getName() != null) {
            tenantConfigManager.upsertConfigValue(TenantConfigCodeConstants.TENANT_NAME, request.getName());
        }
        if (request.getSlogan() != null) {
            tenantConfigManager.upsertConfigValue(TenantConfigCodeConstants.TENANT_SLOGAN, request.getSlogan());
        }
        if (request.getAccountSystemKey() != null) {
            tenantConfigManager.upsertConfigValue(TenantConfigCodeConstants.ACCOUNT_SYSTEM_KEY, request.getAccountSystemKey());
        }
        if (request.getDomainName() != null) {
            tenantConfigManager.upsertConfigValue(TenantConfigCodeConstants.DOMAIN_NAME, request.getDomainName());
        }
        if (request.getStatus() != null) {
            tenantConfigManager.upsertConfigValue(TenantConfigCodeConstants.TENANT_STATUS, request.getStatus());
        }
        if (request.getDingTalkAppKey() != null) {
            tenantConfigManager.upsertConfigValue(TenantConfigCodeConstants.DINGTALK_APP_KEY, request.getDingTalkAppKey());
        }
        if (request.getDingTalkAppSecret() != null) {
            tenantConfigManager.upsertConfigValue(TenantConfigCodeConstants.DINGTALK_APP_SECRET, request.getDingTalkAppSecret());
        }
    }

    /**
     * 传入的 {@link SystemFileDTO} 转为 {@link SystemFileDO} 后按 url 在对应次类型下保存或更新。
     *
     * @param request 请求
     */
    private void syncTenantSystemFiles(TenantMidPlatformSyncRequest request) {
        record FileSyncJob(SystemFileSubTypeEnum subType, List<SystemFileDTO> dtoList) {

        }
        for (FileSyncJob job : List.of(new FileSyncJob(SystemFileSubTypeEnum.LOGIN_LOGO, request.getLoginLogoFileList()), new FileSyncJob(SystemFileSubTypeEnum.MENU_COLLAPSED_LOGO, request.getMenuCollapsedLogoFileList()),
                new FileSyncJob(SystemFileSubTypeEnum.MENU_EXPANDED_LOGO, request.getMenuExpandedLogoFileList()), new FileSyncJob(SystemFileSubTypeEnum.BACKGROUND, request.getBackgroundFileList()))) {
            systemFileManager.saveOrUpdate(SystemFileMainTypeEnum.TENANT, job.subType(), BeanUtils.toBean(job.dtoList(), SystemFileDO.class));
        }
    }

    /**
     * 根据传入的第三方用户 ID（{@code thirdBindAdminUserId}）与 {@code accountSystemKey}，将对应本系统用户设为超级管理员；若无绑定则创建用户并写入 {@code user_bind}。
     * @param request 请求
     */
    private void syncSuperAdminUser(TenantMidPlatformSyncRequest request) {
        if (request.getThirdBindAdminUserId() == null || ValidityUtils.isBlank(request.getThirdBindAdminUserId())) {
            return;
        }
        ScanLoginTypeEnum scanType = ScanLoginTypeEnum.fromAccountSystemKey(request.getAccountSystemKey());
        if (scanType == null) {
            throw ServiceExceptionUtil.exception(TenantErrorCodeConstants.SUPER_ADMIN_ACCOUNT_SYSTEM_KEY_INVALID);
        }
        String platform = scanType.getCode();
        String thirdUserId = request.getThirdBindAdminUserId().trim();

        UserBindDO bind = userBindManager.findByPlatformAndThirdUserId(platform, thirdUserId);

        Long superAdminUserId;
        if (bind != null) {
            superAdminUserId = bind.getUserId();
        } else {
            UserDO draft = new UserDO();
            draft.setUserName(platform + "_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12));
            if (request.getSuperAdmin() != null && !ValidityUtils.isBlank(request.getSuperAdmin())) {
                draft.setNickName(request.getSuperAdmin().trim());
            }
            UserDO created = userManager.save(draft);
            UserBindDO row = new UserBindDO();
            row.setUserId(created.getId());
            row.setPlatform(platform);
            row.setUnionId(thirdUserId);
            row.setThirdUserId(thirdUserId);
            row.setThirdNickname(created.getNickName());
            userBindManager.save(row);
            superAdminUserId = created.getId();
        }
        userManager.assignExclusiveSuperAdmin(superAdminUserId);
    }
}
