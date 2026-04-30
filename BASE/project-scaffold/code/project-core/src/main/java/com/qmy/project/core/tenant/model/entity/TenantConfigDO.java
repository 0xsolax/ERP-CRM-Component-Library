package com.qmy.project.core.tenant.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qmy.project.infrastructure.model.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 17:03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tenant_config")
public class TenantConfigDO extends BaseDO {

    @TableField("config_code")
    private String configCode;

    @TableField("config_name")
    private String configName;

    @TableField("config_value")
    private String configValue;

    @TableField("config_remark")
    private String configRemark;
}
