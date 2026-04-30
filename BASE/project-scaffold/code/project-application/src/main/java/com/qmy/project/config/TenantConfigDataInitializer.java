package com.qmy.project.config;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.qmy.project.common.enums.TenantConfigCodeEnum;
import com.qmy.project.core.tenant.dao.TenantConfigDAO;
import com.qmy.project.core.tenant.model.entity.TenantConfigDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author AI Coding
 * @description 第一次运行租户对租户当前租户信息进行初始化，初始化完成后，需注释/删除掉该组件
 * ${@link TenantConfigCodeEnum}
 * @date 2026/03/20 17:44
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TenantConfigDataInitializer implements ApplicationRunner {

    private final TenantConfigDAO tenantConfigDAO;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void run(ApplicationArguments args) {
        List<TenantConfigDO> configList = tenantConfigDAO.selectList(Wrappers.<TenantConfigDO>lambdaQuery()
                .orderByAsc(TenantConfigDO::getId));
        Set<String> existingCodes = configList.stream()
                .map(TenantConfigDO::getConfigCode)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        int insertCount = 0;
        for (TenantConfigCodeEnum definition : TenantConfigCodeEnum.values()) {
            if (existingCodes.contains(definition.getCode())) {
                continue;
            }
            TenantConfigDO configDO = new TenantConfigDO();
            configDO.setConfigCode(definition.getCode());
            configDO.setConfigName(definition.getName());
            configDO.setConfigValue(definition.getDefaultValue());
            configDO.setConfigRemark(definition.getRemark());
            configDO.setIsDeleted(0);
            tenantConfigDAO.insert(configDO);
            insertCount++;
        }

        if (insertCount > 0) {
            log.info("tenant_config 初始化完成, insertCount={}", insertCount);
        }
    }
}
