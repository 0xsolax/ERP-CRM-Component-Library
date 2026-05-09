package com.qmy.zhongsheng.core.production.model.condition;

import lombok.Data;

/**
 * 生产总单查询条件。
 *
 * @author AI Coding
 */
@Data
public class ProductionOrderQueryCondition {

    private Integer pageNum = 1;

    private Integer pageSize = 20;

    private String keyword;

    private String status;

    private Long orderId;

    private Long productionGroupId;

    private Long ownerId;

    private boolean allVisible;
}
