package com.qmy.zhongsheng.core.user.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qmy.zhongsheng.infrastructure.model.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统角色表 {@code role}。
 *
 * @author AI Coding
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("role")
public class RoleDO extends BaseDO {

    @TableField("name")
    private String name;

    /**
     * 角色描述（对应列名 {@code desc}）。
     */
    @TableField("`desc`")
    private String desc;

    /**
     * 是否启用：0-否，1-是。
     */
    @TableField("enabled")
    private Integer enabled;
}
