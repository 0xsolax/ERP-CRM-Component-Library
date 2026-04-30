package com.qmy.project.infrastructure.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 09:49
 */
@Data
public abstract class BaseDO implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @TableField(value = "create_user", fill = FieldFill.INSERT)
    private Long createUser;

    @TableField(value = "update_user", fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(value = "is_deleted", fill = FieldFill.INSERT)
    private Integer isDeleted;

    @TableField(value = "deleted_time")
    private LocalDateTime deletedTime;
}

