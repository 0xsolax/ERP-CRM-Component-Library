package com.qmy.zhongsheng.core.product.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.qmy.zhongsheng.core.product.dao.ProductMaterialDAO;
import com.qmy.zhongsheng.core.product.manager.ProductMaterialManager;
import com.qmy.zhongsheng.core.product.model.entity.ProductMaterialDO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 产品材料 Manager 实现类。
 *
 * @author 单漪甜
 */
@Component("productMaterialManager")
@RequiredArgsConstructor
public class ProductMaterialManagerImpl implements ProductMaterialManager {

    private final ProductMaterialDAO productMaterialDAO;

    @Override
    public List<ProductMaterialDO> listByProductIds(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<ProductMaterialDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ProductMaterialDO::getProductId, productIds)
                .eq(ProductMaterialDO::getIsDeleted, 0);
        return productMaterialDAO.selectList(wrapper);
    }

    @Override
    public Long saveOrUpdate(ProductMaterialDO materialDO) {
        if (materialDO.getId() == null) {
            productMaterialDAO.insert(materialDO);
        } else {
            productMaterialDAO.updateById(materialDO);
        }
        return materialDO.getId();
    }

    @Override
    public Boolean deleteByProductId(Long productId) {
        return productMaterialDAO.update(Wrappers.<ProductMaterialDO>lambdaUpdate()
                .eq(ProductMaterialDO::getProductId, productId)
                .eq(ProductMaterialDO::getIsDeleted, 0)
                .set(ProductMaterialDO::getIsDeleted, 1)
                .set(ProductMaterialDO::getDeletedTime, LocalDateTime.now())) > 0;
    }

    @Override
    public Set<Long> getProductIdsByMaterialId(Long materialId) {
        if (materialId == null) {
            return Collections.emptySet();
        }
        LambdaQueryWrapper<ProductMaterialDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductMaterialDO::getIsDeleted, 0)
                .eq(ProductMaterialDO::getMaterialId, materialId);

        List<ProductMaterialDO> list = productMaterialDAO.selectList(wrapper);
        return list.stream()
                .map(ProductMaterialDO::getProductId)
                .collect(Collectors.toSet());
    }
}
