package com.qmy.zhongsheng.core.product.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.qmy.zhongsheng.core.product.dao.ProductPackagingDAO;
import com.qmy.zhongsheng.core.product.manager.ProductPackagingManager;
import com.qmy.zhongsheng.core.product.model.entity.ProductPackagingDO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 产品包材 Manager 实现类。
 *
 * @author 单漪甜
 */
@Component("productPackagingManager")
@RequiredArgsConstructor
public class ProductPackagingManagerImpl implements ProductPackagingManager {

    private final ProductPackagingDAO productPackagingDAO;

    @Override
    public List<ProductPackagingDO> listByProductIds(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<ProductPackagingDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ProductPackagingDO::getProductId, productIds)
                .eq(ProductPackagingDO::getIsDeleted, 0);
        return productPackagingDAO.selectList(wrapper);
    }

    @Override
    public Long saveOrUpdate(ProductPackagingDO packagingDO) {
        if (packagingDO.getId() == null) {
            productPackagingDAO.insert(packagingDO);
        } else {
            productPackagingDAO.updateById(packagingDO);
        }
        return packagingDO.getId();
    }

    @Override
    public Boolean deleteByProductId(Long productId) {
        return productPackagingDAO.update(Wrappers.<ProductPackagingDO>lambdaUpdate()
                .eq(ProductPackagingDO::getProductId, productId)
                .eq(ProductPackagingDO::getIsDeleted, 0)
                .set(ProductPackagingDO::getIsDeleted, 1)
                .set(ProductPackagingDO::getDeletedTime, LocalDateTime.now())) > 0;
    }
}
