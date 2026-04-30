package com.qmy.zhongsheng.core.material.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 材料分类视图对象。
 *
 * @author AI Coding
 */
@Data
@Schema(description = "材料分类视图对象")
public class MaterialCategoryVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "排序号")
    private Integer sortNum;

    @Schema(description = "备注")
    private String remark;
}