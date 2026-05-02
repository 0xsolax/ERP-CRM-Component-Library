package com.qiaomoyun.entity.pur.yt;

import com.baomidou.mybatisplus.annotation.*;
import com.qiaomoyun.entity.BaseEntity;
import com.qiaomoyun.entity.pro.yt.ProYtProductFile;
import com.qiaomoyun.entity.pro.yt.ProYtProductSpecificationItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 采购申请实体类
 * 对应表: pur_yt_apply_purchase
 */
@Data
@TableName("pur_yt_apply_purchase")
public class PurYtApplyPurchase extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long productId;
    private Long productSpecificationId;
    private Long supplierId;
    private Long salesEmployeeId;
    private Long customerId;
    private Integer number;
    private Long categoryLabelId;
    private Long orderSubId;
    private Long orderSubItemId;
    private String orderRemark;

    //申购单新增参数
    @TableField(exist = false)
    private Integer orderNumber;
    @TableField(exist = false)
    private Integer occupyStore;
    @TableField(exist = false)
    private Integer occupyTransit;
    @TableField(exist = false)
    private Long orderId;

    @TableField(exist = false)
    private List<ProYtProductSpecificationItem> itemList;
    @TableField(exist = false)
    private List<ProYtProductFile> imageList;

    /**
     * 是否入库发货
     */
    @TableField(exist = false)
    private Boolean isInboundDelivery;
    @TableField(exist = false)
    private String productCode;
    @TableField(exist = false)
    private String customerName;
    @TableField(exist = false)
    private String supplierSpecification;
    @TableField(exist = false)
    private BigDecimal supplierPrice;
    @TableField(exist = false)
    private Integer minNumber;

    //申购单列表参数
    @TableField(exist = false)
    private String orderSubCode;
    @TableField(exist = false)
    private String salesEmployeeName;
    @TableField(exist = false)
    private String categoryLabelName;
    @TableField(exist = false)
    private LocalDateTime orderTime;
    @TableField(exist = false)
    private LocalDateTime deliveryTime;

    @TableField(exist = false)
    @Schema(description = "父订单订单编号")
    private String orderCode;

    @TableField(exist = false)
    @Schema(description = "订单备注")
    private String orderNote;

    @TableField(exist = false)
    @Schema(description = "产品层级")
    private String handProductLevel;
}