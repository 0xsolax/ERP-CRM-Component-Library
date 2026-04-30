package com.qmy.zhongsheng.core.product.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.qmy.zhongsheng.core.product.dao.ProductTypeDAO;
import com.qmy.zhongsheng.core.product.manager.ProductTypeManager;
import com.qmy.zhongsheng.core.product.model.entity.ProductTypeDO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.qmy.zhongsheng.common.utils.ValidityUtils.isBlank;

/**
 * 产品类型关联 Manager 实现类。
 *
 * @author 单漪甜
 */
@Component
@RequiredArgsConstructor
public class ProductTypeManagerImpl implements ProductTypeManager {

    private final ProductTypeDAO productTypeDAO;

    @Override
    public List<ProductTypeDO> listByProductIds(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<ProductTypeDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ProductTypeDO::getProductId, productIds)
                .eq(ProductTypeDO::getIsDeleted, 0);
        return productTypeDAO.selectList(wrapper);
    }

    @Override
    public Long saveOrUpdate(ProductTypeDO productTypeDO) {
        if (productTypeDO.getId() == null) {
            productTypeDAO.insert(productTypeDO);
        } else {
            productTypeDAO.updateById(productTypeDO);
        }
        return productTypeDO.getId();
    }

    @Override
    public Boolean deleteByProductId(Long productId) {
        return productTypeDAO.update(Wrappers.<ProductTypeDO>lambdaUpdate()
                .eq(ProductTypeDO::getProductId, productId)
                .eq(ProductTypeDO::getIsDeleted, 0)
                .set(ProductTypeDO::getIsDeleted, 1)
                .set(ProductTypeDO::getDeletedTime, LocalDateTime.now())) > 0;
    }

    @Override
    public Set<Long> getProductIdsByTypeId(Long typeId) {
        if (typeId == null) {
            return Collections.emptySet();
        }
        LambdaQueryWrapper<ProductTypeDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductTypeDO::getIsDeleted, 0)
                .eq(ProductTypeDO::getTypeId, typeId);

        List<ProductTypeDO> list = productTypeDAO.selectList(wrapper);
        return list.stream()
                .map(ProductTypeDO::getProductId)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Long> getProductIdsByTypeNameLike(String keyword) {
        if (isBlank(keyword)) {
            return Collections.emptySet();
        }
        LambdaQueryWrapper<ProductTypeDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductTypeDO::getIsDeleted, 0)
                .like(ProductTypeDO::getTypeName, keyword)
                .select(ProductTypeDO::getProductId);
        return productTypeDAO.selectList(wrapper).stream()
                .map(ProductTypeDO::getProductId)
                .collect(Collectors.toSet());
    }
}
