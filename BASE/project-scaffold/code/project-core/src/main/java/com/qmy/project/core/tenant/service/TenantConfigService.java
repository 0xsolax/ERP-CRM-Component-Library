package com.qmy.project.core.tenant.service;

import com.qmy.project.core.tenant.model.entity.TenantConfigDO;
import com.qmy.project.core.tenant.model.vo.TenantInfoVO;

import java.util.List;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 17:03
 */
public interface TenantConfigService {

    /**
     * 飞书告警场景拉取的配置行（仅含 webhook 相关 code）；无匹配行时返回空列表。
     * @return 租户配置列表
     */
    List<TenantConfigDO> listTenantConfigsForFeishuAlert();

    /**
     * 校验请求域名与 {@code tenant_config} 中配置的域名一致后，返回当前系统的租户必要信息。
     * @param domainName 租户域名
     * @return 租户配置列表
     */
    TenantInfoVO getByDomainName(String domainName);
}
