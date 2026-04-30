package com.qmy.zhongsheng.core.product.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.qmy.zhongsheng.core.product.dao.ProductProcessPriceDAO;
import com.qmy.zhongsheng.core.product.manager.ProductProcessPriceManager;
import com.qmy.zhongsheng.core.product.model.entity.ProductProcessPriceDO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 产品工价 Manager 实现类。
 *
 * @author 单漪甜
 */
@Component("productProcessPriceManager")
@RequiredArgsConstructor
public class ProductProcessPriceManagerImpl implements ProductProcessPriceManager {

    private final ProductProcessPriceDAO productProcessPriceDAO;

    @Override
    public List<ProductProcessPriceDO> listByProductIds(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<ProductProcessPriceDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ProductProcessPriceDO::getProductId, productIds)
                .eq(ProductProcessPriceDO::getIsDeleted, 0);
        return productProcessPriceDAO.selectList(wrapper);
    }

    @Override
    public Long saveOrUpdate(ProductProcessPriceDO processPriceDO) {
        if (processPriceDO.getId() == null) {
            productProcessPriceDAO.insert(processPriceDO);
        } else {
            productProcessPriceDAO.updateById(processPriceDO);
        }
        return processPriceDO.getId();
    }

    @Override
    public Boolean deleteByProductId(Long productId) {
        return productProcessPriceDAO.update(Wrappers.<ProductProcessPriceDO>lambdaUpdate()
                .eq(ProductProcessPriceDO::getProductId, productId)
                .eq(ProductProcessPriceDO::getIsDeleted, 0)
                .set(ProductProcessPriceDO::getIsDeleted, 1)
                .set(ProductProcessPriceDO::getDeletedTime, LocalDateTime.now())) > 0;
    }
}
