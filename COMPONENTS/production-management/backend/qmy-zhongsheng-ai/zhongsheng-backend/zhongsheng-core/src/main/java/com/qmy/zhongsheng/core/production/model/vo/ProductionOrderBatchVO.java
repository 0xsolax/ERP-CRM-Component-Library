package com.qmy.zhongsheng.core.production.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 生产分批安排 VO。
 *
 * @author AI Coding
 */
@Data
public class ProductionOrderBatchVO {

    private Long id;

    private Long productionOrderId;

    private Long progressId;

    private Long orderId;

    private String orderCode;

    private String lineKey;

    private Long productId;

    private String productCode;

    private String productName;

    private Long productionGroupId;

    private String productionGroupName;

    private BigDecimal batchQty;

    private LocalDate plannedDeliveryDate;

    private String status;

    private String remark;

    private LocalDateTime createTime;
}
