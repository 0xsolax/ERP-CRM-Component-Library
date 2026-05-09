package com.qmy.zhongsheng.core.production.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 生产总单 VO。
 *
 * @author AI Coding
 */
@Data
public class ProductionOrderVO {

    private Long id;

    private String code;

    private String baseCode;

    private String serialCode;

    private String orderType;

    private String masterOrderKey;

    private Long orderId;

    private String orderCode;

    private Long customerId;

    private String customerName;

    private LocalDate deliveryDate;

    private String status;

    private String lockState;

    private Boolean needsReconfirm;

    private String reconfirmScopeJson;

    private Long ownerId;

    private String ownerName;

    private String remark;

    private BigDecimal totalOrderQty;

    private BigDecimal totalPlannedQty;

    private BigDecimal totalInboundQty;

    private BigDecimal totalDeliveredQty;

    private BigDecimal totalRemainingDeliveryQty;

    private List<ProductionOrderProgressVO> progressRows;

    private List<ProductionOrderBatchVO> batches;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
