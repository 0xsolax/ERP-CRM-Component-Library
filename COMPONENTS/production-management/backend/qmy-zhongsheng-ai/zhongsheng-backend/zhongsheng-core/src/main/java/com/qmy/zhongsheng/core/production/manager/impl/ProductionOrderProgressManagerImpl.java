package com.qmy.zhongsheng.core.production.manager.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.qmy.zhongsheng.core.production.dao.ProductionOrderProgressDAO;
import com.qmy.zhongsheng.core.production.manager.ProductionOrderProgressManager;
import com.qmy.zhongsheng.core.production.model.entity.ProductionOrderProgressDO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 生产总单进度 Manager 实现。
 *
 * @author AI Coding
 */
@Component
@RequiredArgsConstructor
public class ProductionOrderProgressManagerImpl implements ProductionOrderProgressManager {

    private final ProductionOrderProgressDAO productionOrderProgressDAO;

    @Override
    public Long saveOrUpdate(ProductionOrderProgressDO progress) {
        if (progress.getId() == null) {
            productionOrderProgressDAO.insert(progress);
            return progress.getId();
        }
        if (productionOrderProgressDAO.selectById(progress.getId()) == null) {
            productionOrderProgressDAO.insert(progress);
            return progress.getId();
        }
        productionOrderProgressDAO.updateById(progress);
        return progress.getId();
    }

    @Override
    public ProductionOrderProgressDO getById(Long id) {
        if (id == null) {
            return null;
        }
        return productionOrderProgressDAO.selectOne(Wrappers.<ProductionOrderProgressDO>lambdaQuery()
                .eq(ProductionOrderProgressDO::getId, id)
                .eq(ProductionOrderProgressDO::getIsDeleted, 0)
                .last("LIMIT 1"));
    }

    @Override
    public ProductionOrderProgressDO getByOrderLine(Long productionOrderId, String lineKey) {
        if (productionOrderId == null || !StringUtils.hasText(lineKey)) {
            return null;
        }
        return productionOrderProgressDAO.selectOne(Wrappers.<ProductionOrderProgressDO>lambdaQuery()
                .eq(ProductionOrderProgressDO::getIsDeleted, 0)
                .eq(ProductionOrderProgressDO::getProductionOrderId, productionOrderId)
                .eq(ProductionOrderProgressDO::getLineKey, lineKey.trim())
                .last("LIMIT 1"));
    }

    @Override
    public List<ProductionOrderProgressDO> listByProductionOrderId(Long productionOrderId) {
        if (productionOrderId == null) {
            return List.of();
        }
        return productionOrderProgressDAO.selectList(Wrappers.<ProductionOrderProgressDO>lambdaQuery()
                .eq(ProductionOrderProgressDO::getIsDeleted, 0)
                .eq(ProductionOrderProgressDO::getProductionOrderId, productionOrderId)
                .orderByAsc(ProductionOrderProgressDO::getProductCode)
                .orderByAsc(ProductionOrderProgressDO::getLineKey)
                .orderByAsc(ProductionOrderProgressDO::getId));
    }
}
