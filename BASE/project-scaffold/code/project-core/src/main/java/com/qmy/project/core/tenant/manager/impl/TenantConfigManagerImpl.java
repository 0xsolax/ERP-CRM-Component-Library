package com.qmy.project.core.tenant.manager.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.qmy.project.common.enums.TenantConfigCodeEnum;
import com.qmy.project.core.tenant.dao.TenantConfigDAO;
import com.qmy.project.core.tenant.manager.TenantConfigManager;
import com.qmy.project.core.tenant.model.entity.TenantConfigDO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 17:03
 */
@Component
@RequiredArgsConstructor
public class TenantConfigManagerImpl implements TenantConfigManager {

    private final TenantConfigDAO tenantConfigDAO;

    @Override
    public List<TenantConfigDO> listByCodes(List<String> codes) {
        if (CollectionUtils.isEmpty(codes)) {
            return List.of();
        }
        return tenantConfigDAO.selectList(Wrappers.<TenantConfigDO>lambdaQuery()
                .in(TenantConfigDO::getConfigCode, codes)
                .orderByAsc(TenantConfigDO::getId));
    }

    @Override
    public List<TenantConfigDO> listCurrentTenantConfigEntries() {
        return tenantConfigDAO.selectList(Wrappers.<TenantConfigDO>lambdaQuery()
                .orderByAsc(TenantConfigDO::getId));
    }

    @Override
    public void upsertConfigValue(String configCode, String configValue) {
        TenantConfigCodeEnum def = TenantConfigCodeEnum.fromCode(configCode);
        if (def == null) {
            throw new IllegalArgumentException("Unknown tenant config code: " + configCode);
        }
        TenantConfigDO row = tenantConfigDAO.selectOne(Wrappers.<TenantConfigDO>lambdaQuery()
                .eq(TenantConfigDO::getConfigCode, configCode)
                .last("LIMIT 1"));
        if (row != null) {
            row.setConfigValue(configValue);
            tenantConfigDAO.updateById(row);
        } else {
            TenantConfigDO insert = new TenantConfigDO();
            insert.setConfigCode(def.getCode());
            insert.setConfigName(def.getName());
            insert.setConfigValue(configValue);
            insert.setConfigRemark(def.getRemark());
            tenantConfigDAO.insert(insert);
        }
    }
}
