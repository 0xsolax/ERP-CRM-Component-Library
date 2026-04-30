package com.qmy.zhongsheng.core.base.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qmy.zhongsheng.infrastructure.model.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 通用基础数据表 {@code base_data}。
 *
 * @author AI Coding
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("base_data")
public class BaseDataDO extends BaseDO {

    @TableField("node_id")
    private Long nodeId;

    @TableField("value1")
    private String value1;

    @TableField("value2")
    private String value2;

    @TableField("value3")
    private String value3;

    @TableField("value4")
    private String value4;
}
