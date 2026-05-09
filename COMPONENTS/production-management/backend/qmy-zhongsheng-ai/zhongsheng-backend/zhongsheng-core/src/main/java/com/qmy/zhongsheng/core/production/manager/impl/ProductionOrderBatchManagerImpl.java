package com.qmy.zhongsheng.core.production.manager.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.qmy.zhongsheng.core.production.dao.ProductionOrderBatchDAO;
import com.qmy.zhongsheng.core.production.manager.ProductionOrderBatchManager;
import com.qmy.zhongsheng.core.production.model.entity.ProductionOrderBatchDO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 生产分批安排 Manager 实现。
 *
 * @author AI Coding
 */
@Component
@RequiredArgsConstructor
public class ProductionOrderBatchManagerImpl implements ProductionOrderBatchManager {

    private final ProductionOrderBatchDAO productionOrderBatchDAO;

    @Override
    public Long save(ProductionOrderBatchDO batch) {
        productionOrderBatchDAO.insert(batch);
        return batch.getId();
    }

    @Override
    public List<ProductionOrderBatchDO> listByProductionOrderId(Long productionOrderId) {
        if (productionOrderId == null) {
            return List.of();
        }
        return productionOrderBatchDAO.selectList(Wrappers.<ProductionOrderBatchDO>lambdaQuery()
                .eq(ProductionOrderBatchDO::getIsDeleted, 0)
                .eq(ProductionOrderBatchDO::getProductionOrderId, productionOrderId)
                .orderByAsc(ProductionOrderBatchDO::getPlannedDeliveryDate)
                .orderByAsc(ProductionOrderBatchDO::getProductCode)
                .orderByAsc(ProductionOrderBatchDO::getId));
    }

    @Override
    public List<ProductionOrderBatchDO> listByProgressId(Long progressId) {
        if (progressId == null) {
            return List.of();
        }
        return productionOrderBatchDAO.selectList(Wrappers.<ProductionOrderBatchDO>lambdaQuery()
                .eq(ProductionOrderBatchDO::getIsDeleted, 0)
                .eq(ProductionOrderBatchDO::getProgressId, progressId)
                .orderByAsc(ProductionOrderBatchDO::getPlannedDeliveryDate)
                .orderByAsc(ProductionOrderBatchDO::getId));
    }

    @Override
    public List<ProductionOrderBatchDO> listByProductionGroupId(Long productionGroupId) {
        if (productionGroupId == null) {
            return List.of();
        }
        return productionOrderBatchDAO.selectList(Wrappers.<ProductionOrderBatchDO>lambdaQuery()
                .eq(ProductionOrderBatchDO::getIsDeleted, 0)
                .eq(ProductionOrderBatchDO::getProductionGroupId, productionGroupId)
                .orderByDesc(ProductionOrderBatchDO::getCreateTime)
                .orderByDesc(ProductionOrderBatchDO::getId));
    }
}
