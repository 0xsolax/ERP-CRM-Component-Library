package com.qmy.zhongsheng.core.user.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 角色分页行：含关联菜单的权限标识列表。
 *
 * @author AI Coding
 */
@Data
@Schema(description = "角色分页行")
public class RolePageVO {

    @Schema(description = "角色主键 id")
    private Long id;

    @Schema(description = "角色名称")
    private String name;

    @Schema(description = "角色描述")
    private String desc;

    @Schema(description = "是否启用：0-否，1-是")
    private Integer enabled;

    @Schema(description = "权限标识列表（来自已关联且未禁用的菜单）")
    private List<String> permissions;
}
