package com.qmy.project.api.dto.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 列表查询：按基础树节点 id 集合过滤。
 * {@code nodeIds} 为 {@code null} 或空列表时不加该条件。
 *
 * @author AI Coding
 */
@Data
@Schema(description = "基础数据列表查询")
public class BaseDataListQueryDTO {

    @Schema(description = "基础树节点 id 列表")
    private List<Long> nodeIds;
}
