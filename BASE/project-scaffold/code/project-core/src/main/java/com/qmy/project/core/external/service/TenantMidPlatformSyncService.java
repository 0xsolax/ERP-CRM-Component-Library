package com.qmy.project.core.external.service;

import com.qmy.project.api.dto.midplatform.TenantMidPlatformSyncRequest;

/**
 * 中台系统同步租户配置与系统文件（对外集成用例，由 {@code core.external} 承载）。
 *
 * @author AI Coding
 */
public interface TenantMidPlatformSyncService {

    /**
     * 从中台同步租户配置与系统文件
     *
     * @param request 中台同步请求
     */
    void syncFromMidPlatformRequest(TenantMidPlatformSyncRequest request);
}
