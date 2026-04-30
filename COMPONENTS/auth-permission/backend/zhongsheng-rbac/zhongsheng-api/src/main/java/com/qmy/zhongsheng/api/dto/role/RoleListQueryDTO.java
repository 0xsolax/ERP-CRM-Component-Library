package com.qmy.zhongsheng.api.dto.role;

import com.qmy.zhongsheng.api.request.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色分页查询请求。
 *
 * @author AI Coding
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "角色分页查询请求")
public class RoleListQueryDTO extends BasePageQuery {

    @Schema(description = "角色名称，模糊匹配，可选")
    private String likeName;
}
