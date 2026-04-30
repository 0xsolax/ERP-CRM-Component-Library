package com.qmy.zhongsheng.core.product.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.qmy.zhongsheng.core.product.dao.ProductUmbrellaFrameDAO;
import com.qmy.zhongsheng.core.product.manager.ProductUmbrellaFrameManager;
import com.qmy.zhongsheng.core.product.model.entity.ProductUmbrellaFrameDO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 产品伞架 Manager 实现类。
 *
 * @author 单漪甜
 */
@Component("productUmbrellaFrameManager")
@RequiredArgsConstructor
public class ProductUmbrellaFrameManagerImpl implements ProductUmbrellaFrameManager {

    private final ProductUmbrellaFrameDAO productUmbrellaFrameDAO;

    @Override
    public ProductUmbrellaFrameDO getByProductId(Long productId) {
        LambdaQueryWrapper<ProductUmbrellaFrameDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductUmbrellaFrameDO::getProductId, productId)
                .eq(ProductUmbrellaFrameDO::getIsDeleted, 0)
                .last("LIMIT 1");
        return productUmbrellaFrameDAO.selectOne(wrapper);
    }

    @Override
    public List<ProductUmbrellaFrameDO> listByProductIds(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<ProductUmbrellaFrameDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ProductUmbrellaFrameDO::getProductId, productIds)
                .eq(ProductUmbrellaFrameDO::getIsDeleted, 0);
        return productUmbrellaFrameDAO.selectList(wrapper);
    }

    @Override
    public Long saveOrUpdate(ProductUmbrellaFrameDO umbrellaFrameDO) {
        if (umbrellaFrameDO.getId() == null) {
            productUmbrellaFrameDAO.insert(umbrellaFrameDO);
        } else {
            productUmbrellaFrameDAO.updateById(umbrellaFrameDO);
        }
        return umbrellaFrameDO.getId();
    }

    @Override
    public Boolean deleteByProductId(Long productId) {
        return productUmbrellaFrameDAO.update(Wrappers.<ProductUmbrellaFrameDO>lambdaUpdate()
                .eq(ProductUmbrellaFrameDO::getProductId, productId)
                .eq(ProductUmbrellaFrameDO::getIsDeleted, 0)
                .set(ProductUmbrellaFrameDO::getIsDeleted, 1)
                .set(ProductUmbrellaFrameDO::getDeletedTime, LocalDateTime.now())) > 0;
    }

    @Override
    public Set<Long> getProductIdsByNames(String typeName, String lengthName,
                                           String functionName, String materialName) {
        LambdaQueryWrapper<ProductUmbrellaFrameDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductUmbrellaFrameDO::getIsDeleted, 0);

        if (typeName != null && !typeName.isEmpty()) {
            wrapper.eq(ProductUmbrellaFrameDO::getTypeName, typeName);
        }
        if (lengthName != null && !lengthName.isEmpty()) {
            wrapper.eq(ProductUmbrellaFrameDO::getLengthName, lengthName);
        }
        if (functionName != null && !functionName.isEmpty()) {
            wrapper.eq(ProductUmbrellaFrameDO::getFunctionName, functionName);
        }
        if (materialName != null && !materialName.isEmpty()) {
            wrapper.eq(ProductUmbrellaFrameDO::getMaterialName, materialName);
        }

        List<ProductUmbrellaFrameDO> list = productUmbrellaFrameDAO.selectList(wrapper);
        return list.stream()
                .map(ProductUmbrellaFrameDO::getProductId)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Long> getProductIdsByFrameSize(String lengthName, String diameterName, String ribCountName) {
        LambdaQueryWrapper<ProductUmbrellaFrameDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductUmbrellaFrameDO::getIsDeleted, 0);

        if (lengthName != null && !lengthName.isEmpty()) {
            wrapper.eq(ProductUmbrellaFrameDO::getLengthName, lengthName);
        }
        if (diameterName != null && !diameterName.isEmpty()) {
            wrapper.eq(ProductUmbrellaFrameDO::getDiameterName, diameterName);
        }
        if (ribCountName != null && !ribCountName.isEmpty()) {
            wrapper.eq(ProductUmbrellaFrameDO::getRibCountName, ribCountName);
        }

        List<ProductUmbrellaFrameDO> list = productUmbrellaFrameDAO.selectList(wrapper);
        return list.stream()
                .map(ProductUmbrellaFrameDO::getProductId)
                .collect(Collectors.toSet());
    }
}
