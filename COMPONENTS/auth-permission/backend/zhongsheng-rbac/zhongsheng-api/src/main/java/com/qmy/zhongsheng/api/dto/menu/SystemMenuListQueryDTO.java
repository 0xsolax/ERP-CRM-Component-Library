package com.qmy.zhongsheng.api.dto.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 菜单列表查询 DTO。
 *
 * @author 单漪甜
 */
@Data
@Schema(description = "菜单列表查询")
public class SystemMenuListQueryDTO {

    @Schema(description = "菜单名称，模糊匹配", example = "用户")
    private String name;

    @Schema(description = "权限标识，模糊匹配", example = "user")
    private String permission;

    @Schema(description = "菜单类型：-1 未知，1-目录，2-菜单，3-按钮", example = "1")
    private Integer type;

    @Schema(description = "父级菜单ID", example = "0")
    private Long parentId;

    @Schema(description = "状态：0-正常，1-禁用", example = "0")
    private Integer status;

    @Schema(description = "是否可见：0-隐藏，1-显示", example = "1")
    private Integer visible;
}