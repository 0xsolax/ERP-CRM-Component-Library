package com.qmy.zhongsheng.core.production.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.core.production.dao.ProductionOrderDAO;
import com.qmy.zhongsheng.core.production.manager.ProductionOrderBatchManager;
import com.qmy.zhongsheng.core.production.manager.ProductionOrderManager;
import com.qmy.zhongsheng.core.production.model.condition.ProductionOrderQueryCondition;
import com.qmy.zhongsheng.core.production.model.entity.ProductionOrderBatchDO;
import com.qmy.zhongsheng.core.production.model.entity.ProductionOrderDO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 生产总单 Manager 实现。
 *
 * @author AI Coding
 */
@Component
@RequiredArgsConstructor
public class ProductionOrderManagerImpl implements ProductionOrderManager {

    private final ProductionOrderDAO productionOrderDAO;

    private final ProductionOrderBatchManager productionOrderBatchManager;

    @Override
    public Long saveOrUpdate(ProductionOrderDO order) {
        if (order.getId() == null) {
            productionOrderDAO.insert(order);
            return order.getId();
        }
        if (productionOrderDAO.selectById(order.getId()) == null) {
            productionOrderDAO.insert(order);
            return order.getId();
        }
        productionOrderDAO.updateById(order);
        return order.getId();
    }

    @Override
    public ProductionOrderDO getById(Long id) {
        if (id == null) {
            return null;
        }
        ProductionOrderQueryCondition condition = new ProductionOrderQueryCondition();
        condition.setAllVisible(true);
        return productionOrderDAO.selectOne(baseQuery(condition)
                .eq(ProductionOrderDO::getId, id)
                .last("LIMIT 1"));
    }

    @Override
    public ProductionOrderDO getByOrderId(Long orderId) {
        if (orderId == null) {
            return null;
        }
        ProductionOrderQueryCondition condition = new ProductionOrderQueryCondition();
        condition.setAllVisible(true);
        return productionOrderDAO.selectOne(baseQuery(condition)
                .eq(ProductionOrderDO::getOrderId, orderId)
                .eq(ProductionOrderDO::getOrderType, "master")
                .last("LIMIT 1"));
    }

    @Override
    public ProductionOrderDO getByCode(String code) {
        if (!StringUtils.hasText(code)) {
            return null;
        }
        ProductionOrderQueryCondition condition = new ProductionOrderQueryCondition();
        condition.setAllVisible(true);
        return productionOrderDAO.selectOne(baseQuery(condition)
                .eq(ProductionOrderDO::getCode, code.trim())
                .last("LIMIT 1"));
    }

    @Override
    public boolean existsByCode(String code, Long excludeId) {
        ProductionOrderDO order = getByCode(code);
        return order != null && !order.getId().equals(excludeId);
    }

    @Override
    public ProductionOrderDO getVisibleById(Long id, Long ownerId, boolean allVisible) {
        if (id == null) {
            return null;
        }
        ProductionOrderQueryCondition condition = new ProductionOrderQueryCondition();
        condition.setAllVisible(true);
        LambdaQueryWrapper<ProductionOrderDO> query = baseQuery(condition)
                .eq(ProductionOrderDO::getId, id)
                .last("LIMIT 1");
        if (!allVisible) {
            query.eq(ProductionOrderDO::getOwnerId, ownerId == null ? -1L : ownerId);
        }
        return productionOrderDAO.selectOne(query);
    }

    @Override
    public ProductionOrderDO getVisibleByOrderId(Long orderId, Long ownerId, boolean allVisible) {
        if (orderId == null) {
            return null;
        }
        ProductionOrderQueryCondition condition = new ProductionOrderQueryCondition();
        condition.setAllVisible(true);
        LambdaQueryWrapper<ProductionOrderDO> query = baseQuery(condition)
                .eq(ProductionOrderDO::getOrderId, orderId)
                .eq(ProductionOrderDO::getOrderType, "master")
                .last("LIMIT 1");
        if (!allVisible) {
            query.eq(ProductionOrderDO::getOwnerId, ownerId == null ? -1L : ownerId);
        }
        return productionOrderDAO.selectOne(query);
    }

    @Override
    public Page<ProductionOrderDO> page(ProductionOrderQueryCondition condition) {
        if (condition != null && !condition.isAllVisible() && condition.getOwnerId() == null) {
            return new Page<>(condition.getPageNum(), condition.getPageSize(), 0);
        }
        LambdaQueryWrapper<ProductionOrderDO> query = baseQuery(condition);
        if (condition.getProductionGroupId() != null) {
            Set<Long> ids = productionOrderBatchManager.listByProductionGroupId(condition.getProductionGroupId()).stream()
                    .map(ProductionOrderBatchDO::getProductionOrderId)
                    .collect(Collectors.toSet());
            if (ids.isEmpty()) {
                return new Page<>(condition.getPageNum(), condition.getPageSize(), 0);
            }
            query.in(ProductionOrderDO::getId, ids);
        }
        return productionOrderDAO.selectPage(new Page<>(condition.getPageNum(), condition.getPageSize()), query);
    }

    private LambdaQueryWrapper<ProductionOrderDO> baseQuery(ProductionOrderQueryCondition condition) {
        LambdaQueryWrapper<ProductionOrderDO> query = Wrappers.<ProductionOrderDO>lambdaQuery()
                .eq(ProductionOrderDO::getIsDeleted, 0);
        if (condition != null && !condition.isAllVisible() && condition.getOwnerId() != null) {
            query.eq(ProductionOrderDO::getOwnerId, condition.getOwnerId());
        }
        if (condition != null && StringUtils.hasText(condition.getKeyword())) {
            String keyword = condition.getKeyword().trim();
            query.and(w -> w.like(ProductionOrderDO::getCode, keyword)
                    .or().like(ProductionOrderDO::getBaseCode, keyword)
                    .or().like(ProductionOrderDO::getSerialCode, keyword)
                    .or().like(ProductionOrderDO::getOrderCode, keyword)
                    .or().like(ProductionOrderDO::getCustomerName, keyword)
                    .or().like(ProductionOrderDO::getRemark, keyword));
        }
        if (condition != null) {
            query.eq(condition.getOrderId() != null, ProductionOrderDO::getOrderId, condition.getOrderId());
            query.eq(StringUtils.hasText(condition.getStatus()), ProductionOrderDO::getStatus, condition.getStatus());
        }
        query.orderByDesc(ProductionOrderDO::getCreateTime).orderByDesc(ProductionOrderDO::getId);
        return query;
    }
}
