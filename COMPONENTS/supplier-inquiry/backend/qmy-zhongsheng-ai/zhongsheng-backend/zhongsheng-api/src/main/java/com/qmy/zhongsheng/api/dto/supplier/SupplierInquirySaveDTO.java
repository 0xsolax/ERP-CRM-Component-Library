package com.qmy.zhongsheng.api.dto.supplier;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 供应商询价记录保存请求。
 *
 * @author AI Coding
 */
@Data
@Schema(description = "供应商询价记录保存请求")
public class SupplierInquirySaveDTO {

    @Schema(description = "主键；为空表示新增")
    private Long id;

    @Schema(description = "供应商 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long supplierId;

    @Schema(description = "询价对象类型", requiredMode = Schema.RequiredMode.REQUIRED)
    private String targetType;

    @Schema(description = "询价对象 ID；手工对象为空")
    private Long targetId;

    @Schema(description = "询价对象编号快照")
    private String targetCode;

    @Schema(description = "询价对象名称快照", requiredMode = Schema.RequiredMode.REQUIRED)
    private String targetName;

    @Schema(description = "规格")
    private String specification;

    @Schema(description = "单位")
    private String unit;

    @Schema(description = "报价单价", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal price;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "税率")
    private BigDecimal taxRate;

    @Schema(description = "起订量")
    private BigDecimal moq;

    @Schema(description = "交期天数或交期说明")
    private String deliveryDays;

    @Schema(description = "报价日期")
    private LocalDate quoteDate;

    @Schema(description = "有效期")
    private LocalDate validUntil;

    @Schema(description = "联系人")
    private String contactName;

    @Schema(description = "联系方式")
    private String contactPhone;

    @Schema(description = "备注")
    private String remark;
}
