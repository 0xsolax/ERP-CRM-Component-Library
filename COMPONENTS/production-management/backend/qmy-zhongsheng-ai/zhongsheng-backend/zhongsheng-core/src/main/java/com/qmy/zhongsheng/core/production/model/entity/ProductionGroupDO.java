package com.qmy.zhongsheng.core.production.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qmy.zhongsheng.infrastructure.model.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 生产组主档 DO。
 *
 * @author AI Coding
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("production_group")
public class ProductionGroupDO extends BaseDO {

    @TableField("code")
    private String code;

    @TableField("name")
    private String name;

    @TableField("status")
    private Integer status;

    @TableField("remark")
    private String remark;
}
