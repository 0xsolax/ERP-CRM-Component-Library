package com.qmy.zhongsheng.core.supplier.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 供应商询价台账视图对象。
 *
 * @author AI Coding
 */
@Data
@Schema(description = "供应商询价台账视图对象")
public class SupplierInquiryVO {

    @Schema(description = "询价记录 ID")
    private Long id;

    @Schema(description = "供应商 ID")
    private Long supplierId;

    @Schema(description = "供应商编号快照")
    private String supplierCode;

    @Schema(description = "供应商名称快照")
    private String supplierName;

    @Schema(description = "询价对象类型")
    private String targetType;

    @Schema(description = "询价对象 ID")
    private Long targetId;

    @Schema(description = "询价对象编号快照")
    private String targetCode;

    @Schema(description = "询价对象名称快照")
    private String targetName;

    @Schema(description = "规格")
    private String specification;

    @Schema(description = "单位")
    private String unit;

    @Schema(description = "报价单价")
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

    @Schema(description = "录入人 ID")
    private Long ownerId;

    @Schema(description = "录入人名称快照")
    private String ownerName;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "编辑日志")
    private List<ChangeLogVO> changeLogs;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Data
    @Schema(description = "供应商询价编辑日志")
    public static class ChangeLogVO {

        @Schema(description = "操作时间")
        private String time;

        @Schema(description = "操作人 ID")
        private Long operatorId;

        @Schema(description = "操作人名称")
        private String operatorName;

        @Schema(description = "变更内容")
        private List<FieldChangeVO> changes;
    }

    @Data
    @Schema(description = "供应商询价字段变更")
    public static class FieldChangeVO {

        @Schema(description = "字段名")
        private String field;

        @Schema(description = "变更前")
        private String before;

        @Schema(description = "变更后")
        private String after;
    }
}
