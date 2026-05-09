package com.qmy.zhongsheng.core.supplier.model.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qmy.zhongsheng.infrastructure.model.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 供应商询价台账 {@code supplier_inquiry}。
 *
 * @author AI Coding
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("supplier_inquiry")
public class SupplierInquiryDO extends BaseDO {

    @TableField("supplier_id")
    private Long supplierId;

    @TableField("supplier_code")
    private String supplierCode;

    @TableField("supplier_name")
    private String supplierName;

    @TableField("target_type")
    private String targetType;

    @TableField(value = "target_id", updateStrategy = FieldStrategy.ALWAYS)
    private Long targetId;

    @TableField(value = "target_code", updateStrategy = FieldStrategy.ALWAYS)
    private String targetCode;

    @TableField("target_name")
    private String targetName;

    @TableField(value = "specification", updateStrategy = FieldStrategy.ALWAYS)
    private String specification;

    @TableField(value = "unit", updateStrategy = FieldStrategy.ALWAYS)
    private String unit;

    @TableField("price")
    private BigDecimal price;

    @TableField("currency")
    private String currency;

    @TableField(value = "tax_rate", updateStrategy = FieldStrategy.ALWAYS)
    private BigDecimal taxRate;

    @TableField(value = "moq", updateStrategy = FieldStrategy.ALWAYS)
    private BigDecimal moq;

    @TableField(value = "delivery_days", updateStrategy = FieldStrategy.ALWAYS)
    private String deliveryDays;

    @TableField("quote_date")
    private LocalDate quoteDate;

    @TableField(value = "valid_until", updateStrategy = FieldStrategy.ALWAYS)
    private LocalDate validUntil;

    @TableField(value = "contact_name", updateStrategy = FieldStrategy.ALWAYS)
    private String contactName;

    @TableField(value = "contact_phone", updateStrategy = FieldStrategy.ALWAYS)
    private String contactPhone;

    @TableField(value = "owner_id", updateStrategy = FieldStrategy.ALWAYS)
    private Long ownerId;

    @TableField(value = "owner_name", updateStrategy = FieldStrategy.ALWAYS)
    private String ownerName;

    @TableField(value = "remark", updateStrategy = FieldStrategy.ALWAYS)
    private String remark;

    @TableField(value = "modification_log_json", updateStrategy = FieldStrategy.ALWAYS)
    private String modificationLogJson;
}
