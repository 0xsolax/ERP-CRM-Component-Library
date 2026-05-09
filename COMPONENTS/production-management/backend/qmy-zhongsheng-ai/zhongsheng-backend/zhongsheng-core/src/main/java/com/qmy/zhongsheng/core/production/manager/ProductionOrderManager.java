package com.qmy.zhongsheng.core.production.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.core.production.model.condition.ProductionOrderQueryCondition;
import com.qmy.zhongsheng.core.production.model.entity.ProductionOrderDO;

/**
 * 生产总单 Manager。
 *
 * @author AI Coding
 */
public interface ProductionOrderManager {

    Long saveOrUpdate(ProductionOrderDO order);

    ProductionOrderDO getById(Long id);

    ProductionOrderDO getByOrderId(Long orderId);

    ProductionOrderDO getByCode(String code);

    boolean existsByCode(String code, Long excludeId);

    ProductionOrderDO getVisibleById(Long id, Long ownerId, boolean allVisible);

    ProductionOrderDO getVisibleByOrderId(Long orderId, Long ownerId, boolean allVisible);

    Page<ProductionOrderDO> page(ProductionOrderQueryCondition condition);
}
