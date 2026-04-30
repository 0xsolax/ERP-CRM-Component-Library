package com.qmy.project.core.user.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qmy.project.infrastructure.model.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 09:49
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user")
public class UserDO extends BaseDO {

    @TableField("user_name")
    private String userName;

    @TableField("password_hash")
    private String passwordHash;

    @TableField("nick_name")
    private String nickName;

    @TableField("email")
    private String email;

    @TableField("mobile")
    private String mobile;

    @TableField("status")
    private Integer status;

    @TableField("gender")
    private Integer gender;

    /** 是否超级管理员：false/0 否，true/1 是（与 {@code user_bind} 钉钉 third_user_id 同步） */
    @TableField("admin_flag")
    private Boolean adminFlag;

    /** 头像文件，对应 {@code system_file.id}，访问地址见该行的 {@code url} */
    @TableField("avatar_file_id")
    private Long avatarFileId;
}
