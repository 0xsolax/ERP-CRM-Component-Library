package com.qmy.zhongsheng.core.menu.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qmy.zhongsheng.infrastructure.model.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统菜单表 {@code system_menu}。
 *
 * @author 单漪甜
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("system_menu")
public class SystemMenuDO extends BaseDO {

    @TableField("name")
    private String name;

    @TableField("permission")
    private String permission;

    /**
     * 菜单类型：-1 未知，1-目录，2-菜单，3-按钮。
     */
    @TableField("type")
    private Integer type;

    @TableField("sort")
    private Integer sort;

    /**
     * 父级菜单ID，根节点为0。
     */
    @TableField("parent_id")
    private Long parentId;

    @TableField("path")
    private String path;

    @TableField("icon")
    private String icon;

    @TableField("component")
    private String component;

    /**
     * 状态：0-正常，1-禁用。
     */
    @TableField("status")
    private Integer status;

    /**
     * 是否可见：0-隐藏，1-显示。
     */
    @TableField("visible")
    private Integer visible;

    /**
     * 是否缓存页面：0-不缓存，1-缓存。
     */
    @TableField("keep_alive")
    private Integer keepAlive;
}