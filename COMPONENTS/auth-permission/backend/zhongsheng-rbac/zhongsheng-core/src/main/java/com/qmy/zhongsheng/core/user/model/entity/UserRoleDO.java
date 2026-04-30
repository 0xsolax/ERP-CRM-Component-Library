package com.qmy.zhongsheng.core.user.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qmy.zhongsheng.infrastructure.model.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户与角色关联表 {@code user_role}。
 *
 * @author AI Coding
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_role")
public class UserRoleDO extends BaseDO {

    @TableField("user_id")
    private Long userId;

    @TableField("role_id")
    private Long roleId;
}
