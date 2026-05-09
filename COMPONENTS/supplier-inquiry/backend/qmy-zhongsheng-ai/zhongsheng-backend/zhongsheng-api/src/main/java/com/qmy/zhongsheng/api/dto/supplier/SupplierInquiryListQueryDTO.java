package com.qmy.zhongsheng.api.dto.supplier;

import com.qmy.zhongsheng.api.request.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 供应商询价台账分页查询。
 *
 * @author AI Coding
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "供应商询价台账分页查询")
public class SupplierInquiryListQueryDTO extends BasePageQuery {

    @Schema(description = "关键词：供应商、询价对象、编号、规格或备注")
    private String keyword;

    @Schema(description = "供应商 ID")
    private Long supplierId;

    @Schema(description = "询价对象类型，支持系统预置类型或用户自定义类型")
    private String targetType;

    @Schema(description = "询价对象 ID")
    private Long targetId;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "报价日期起")
    private LocalDate quoteDateFrom;

    @Schema(description = "报价日期止")
    private LocalDate quoteDateTo;

    @Schema(description = "有效期状态：effective/expired")
    private String validStatus;

    @Schema(description = "最低单价")
    private BigDecimal priceMin;

    @Schema(description = "最高单价")
    private BigDecimal priceMax;
}
