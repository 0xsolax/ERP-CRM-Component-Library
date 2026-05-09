package com.qmy.zhongsheng.core.production.manager;

import com.qmy.zhongsheng.core.production.model.entity.ProductionOrderProgressDO;

import java.util.List;

/**
 * 生产总单进度 Manager。
 *
 * @author AI Coding
 */
public interface ProductionOrderProgressManager {

    Long saveOrUpdate(ProductionOrderProgressDO progress);

    ProductionOrderProgressDO getById(Long id);

    ProductionOrderProgressDO getByOrderLine(Long productionOrderId, String lineKey);

    List<ProductionOrderProgressDO> listByProductionOrderId(Long productionOrderId);
}
