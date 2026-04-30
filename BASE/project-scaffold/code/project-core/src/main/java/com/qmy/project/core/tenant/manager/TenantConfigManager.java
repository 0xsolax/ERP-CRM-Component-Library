package com.qmy.project.core.tenant.manager;

import com.qmy.project.common.constants.TenantConfigCodeConstants;
import com.qmy.project.core.tenant.model.entity.TenantConfigDO;

import java.util.List;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 17:03
 */
public interface TenantConfigManager {

    /**
     * 租户配置查询条件（按 config_code 过滤）；codes 通常来自 {@link TenantConfigCodeConstants#getTenantFeishuAlertCodes()} 等。
     * @param codes 查询条件
     * @return 租户配置列表
     */
    List<TenantConfigDO> listByCodes(List<String> codes);

    /**
     * 获取当前租户的全部配置项（不做 code 过滤，用于中台展示等场景）。
     *
     * @return 全部配置行，按 id 升序；若无数据则返回空列表
     */
    List<TenantConfigDO> listCurrentTenantConfigEntries();

    /**
     * 按已知 {@link com.qmy.project.common.enums.TenantConfigCodeEnum} 更新或插入一行配置。
     */
    void upsertConfigValue(String configCode, String configValue);
}
