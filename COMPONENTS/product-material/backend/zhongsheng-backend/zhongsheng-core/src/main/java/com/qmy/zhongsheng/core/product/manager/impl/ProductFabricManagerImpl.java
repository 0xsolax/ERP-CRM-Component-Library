package com.qmy.zhongsheng.core.product.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.qmy.zhongsheng.core.product.dao.ProductFabricDAO;
import com.qmy.zhongsheng.core.product.manager.ProductFabricManager;
import com.qmy.zhongsheng.core.product.model.entity.ProductFabricDO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 产品面料 Manager 实现类。
 *
 * @author 单漪甜
 */
@Component("productFabricManager")
@RequiredArgsConstructor
public class ProductFabricManagerImpl implements ProductFabricManager {

    private final ProductFabricDAO productFabricDAO;

    @Override
    public List<ProductFabricDO> listByProductIds(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<ProductFabricDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ProductFabricDO::getProductId, productIds)
                .eq(ProductFabricDO::getIsDeleted, 0);
        return productFabricDAO.selectList(wrapper);
    }

    @Override
    public Long saveOrUpdate(ProductFabricDO fabricDO) {
        if (fabricDO.getId() == null) {
            productFabricDAO.insert(fabricDO);
        } else {
            productFabricDAO.updateById(fabricDO);
        }
        return fabricDO.getId();
    }

    @Override
    public Boolean deleteByProductId(Long productId) {
        return productFabricDAO.update(Wrappers.<ProductFabricDO>lambdaUpdate()
                .eq(ProductFabricDO::getProductId, productId)
                .eq(ProductFabricDO::getIsDeleted, 0)
                .set(ProductFabricDO::getIsDeleted, 1)
                .set(ProductFabricDO::getDeletedTime, LocalDateTime.now())) > 0;
    }

    @Override
    public Set<Long> getProductIdsByTypeName(String typeName) {
        if (typeName == null || typeName.isEmpty()) {
            return Collections.emptySet();
        }
        LambdaQueryWrapper<ProductFabricDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductFabricDO::getIsDeleted, 0)
                .eq(ProductFabricDO::getTypeName, typeName);

        List<ProductFabricDO> list = productFabricDAO.selectList(wrapper);
        return list.stream()
                .map(ProductFabricDO::getProductId)
                .collect(Collectors.toSet());
    }
}
