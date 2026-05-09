package com.qmy.zhongsheng.core.supplier.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 供应商下拉选项。
 *
 * @author AI Coding
 */
@Data
@Schema(description = "供应商下拉选项")
public class SupplierOptionVO {

    @Schema(description = "供应商 ID")
    private Long id;

    @Schema(description = "供应商编号")
    private String code;

    @Schema(description = "供应商名称")
    private String name;

    @Schema(description = "联系人")
    private String contact;

    @Schema(description = "电话")
    private String phone;
}
