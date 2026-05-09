package com.qmy.zhongsheng.core.production.manager;

import com.qmy.zhongsheng.core.production.model.entity.ProductionOrderBatchDO;

import java.util.List;

/**
 * 生产分批安排 Manager。
 *
 * @author AI Coding
 */
public interface ProductionOrderBatchManager {

    Long save(ProductionOrderBatchDO batch);

    List<ProductionOrderBatchDO> listByProductionOrderId(Long productionOrderId);

    List<ProductionOrderBatchDO> listByProgressId(Long progressId);

    List<ProductionOrderBatchDO> listByProductionGroupId(Long productionGroupId);
}
