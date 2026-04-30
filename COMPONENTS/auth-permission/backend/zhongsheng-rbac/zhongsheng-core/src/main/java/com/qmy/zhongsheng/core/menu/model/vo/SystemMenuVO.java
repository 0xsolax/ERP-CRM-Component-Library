package com.qmy.zhongsheng.core.menu.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 系统菜单 VO。
 *
 * @author 单漪甜
 */
@Data
@Schema(description = "系统菜单")
public class SystemMenuVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "菜单名称")
    private String name;

    @Schema(description = "权限标识")
    private String permission;

    @Schema(description = "菜单类型：-1 未知，1-目录，2-菜单，3-按钮")
    private Integer type;

    @Schema(description = "排序值")
    private Integer sort;

    @Schema(description = "父级菜单ID")
    private Long parentId;

    @Schema(description = "前端路由路径")
    private String path;

    @Schema(description = "图标类名")
    private String icon;

    @Schema(description = "前端组件路径")
    private String component;

    @Schema(description = "状态：0-正常，1-禁用")
    private Integer status;

    @Schema(description = "是否可见：0-隐藏，1-显示")
    private Integer visible;

    @Schema(description = "是否缓存页面：0-不缓存，1-缓存")
    private Integer keepAlive;
}