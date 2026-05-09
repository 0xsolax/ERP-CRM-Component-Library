package com.qmy.zhongsheng.core.production.model.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 生产总单产品行进度 VO。
 *
 * @author AI Coding
 */
@Data
public class ProductionOrderProgressVO {

    private Long id;

    private Long productionOrderId;

    private Long orderId;

    private String orderCode;

    private String lineKey;

    private Long productId;

    private String productCode;

    private String productName;

    private BigDecimal orderQty;

    private BigDecimal plannedQty;

    private BigDecimal purchasedQty;

    private BigDecimal inboundQty;

    private BigDecimal producedQty;

    private BigDecimal deliveredQty;

    private BigDecimal releasedQty;

    private BigDecimal availablePlanQty;

    private BigDecimal remainingDeliveryQty;

    private String sourceSnapshotJson;

    private String progressStatus;

    private String remark;
}
