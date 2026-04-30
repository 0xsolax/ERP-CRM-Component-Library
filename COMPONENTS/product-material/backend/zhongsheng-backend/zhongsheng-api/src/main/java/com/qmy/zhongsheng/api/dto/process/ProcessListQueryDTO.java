package com.qmy.zhongsheng.api.dto.process;

import com.qmy.zhongsheng.api.request.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 工序分页查询：支持模糊搜索工序名称。
 *
 * @author AI Coding
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "工序分页查询")
public class ProcessListQueryDTO extends BasePageQuery {

    @Schema(description = "工序名称（模糊匹配）", example = "裁剪")
    private String likeName;
}