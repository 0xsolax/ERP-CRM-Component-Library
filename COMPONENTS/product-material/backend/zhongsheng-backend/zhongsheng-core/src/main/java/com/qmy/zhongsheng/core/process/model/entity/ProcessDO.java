package com.qmy.zhongsheng.core.process.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qmy.zhongsheng.infrastructure.model.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 工序表 {@code process}。
 *
 * @author AI Coding
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("process")
public class ProcessDO extends BaseDO {

    @TableField("name")
    private String name;
}