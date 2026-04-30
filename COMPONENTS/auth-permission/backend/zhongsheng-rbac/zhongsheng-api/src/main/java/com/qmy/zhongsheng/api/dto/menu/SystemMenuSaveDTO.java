package com.qmy.zhongsheng.api.dto.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 菜单保存 DTO：{@code id} 为空表示新增；非空表示更新。
 *
 * @author 单漪甜
 */
@Data
@Schema(description = "菜单保存（新增或更新）")
public class SystemMenuSaveDTO {

    @Schema(description = "主键；为空表示新增")
    private Long id;

    @Schema(description = "菜单名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "用户管理")
    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 50, message = "菜单名称不能超过50个字符")
    private String name;

    @Schema(description = "权限标识", requiredMode = Schema.RequiredMode.REQUIRED, example = "user:list")
    @NotBlank(message = "权限标识不能为空")
    @Size(max = 100, message = "权限标识不能超过100个字符")
    private String permission;

    @Schema(description = "菜单类型：-1 未知，1-目录，2-菜单，3-按钮", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "菜单类型不能为空")
    private Integer type;

    @Schema(description = "排序值", example = "0")
    private Integer sort;

    @Schema(description = "父级菜单ID，根节点为0", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @NotNull(message = "父级菜单ID不能为空")
    private Long parentId;

    @Schema(description = "前端路由路径", example = "/user/list")
    @Size(max = 200, message = "路由路径不能超过200个字符")
    private String path;

    @Schema(description = "图标类名", example = "el-icon-user")
    @Size(max = 100, message = "图标类名不能超过100个字符")
    private String icon;

    @Schema(description = "前端组件路径", example = "views/user/list.vue")
    @Size(max = 255, message = "组件路径不能超过255个字符")
    private String component;

    @Schema(description = "状态：0-正常，1-禁用", example = "0")
    private Integer status;

    @Schema(description = "是否可见：0-隐藏，1-显示", example = "1")
    private Integer visible;

    @Schema(description = "是否缓存页面：0-不缓存，1-缓存", example = "1")
    private Integer keepAlive;
}