package com.qmy.zhongsheng.core.product.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.qmy.zhongsheng.core.product.dao.ProductPrintingDAO;
import com.qmy.zhongsheng.core.product.manager.ProductPrintingManager;
import com.qmy.zhongsheng.core.product.model.entity.ProductPrintingDO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 产品印刷 Manager 实现类。
 *
 * @author 单漪甜
 */
@Component("productPrintingManager")
@RequiredArgsConstructor
public class ProductPrintingManagerImpl implements ProductPrintingManager {

    private final ProductPrintingDAO productPrintingDAO;

    @Override
    public List<ProductPrintingDO> listByProductIds(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<ProductPrintingDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ProductPrintingDO::getProductId, productIds)
                .eq(ProductPrintingDO::getIsDeleted, 0);
        return productPrintingDAO.selectList(wrapper);
    }

    @Override
    public Long saveOrUpdate(ProductPrintingDO printingDO) {
        if (printingDO.getId() == null) {
            productPrintingDAO.insert(printingDO);
        } else {
            productPrintingDAO.updateById(printingDO);
        }
        return printingDO.getId();
    }

    @Override
    public Boolean deleteByProductId(Long productId) {
        return productPrintingDAO.update(Wrappers.<ProductPrintingDO>lambdaUpdate()
                .eq(ProductPrintingDO::getProductId, productId)
                .eq(ProductPrintingDO::getIsDeleted, 0)
                .set(ProductPrintingDO::getIsDeleted, 1)
                .set(ProductPrintingDO::getDeletedTime, LocalDateTime.now())) > 0;
    }

    @Override
    public Set<Long> getProductIdsByPrintingNames(String printTypeName, String alignmentTypeName) {
        LambdaQueryWrapper<ProductPrintingDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductPrintingDO::getIsDeleted, 0);

        if (printTypeName != null && !printTypeName.isEmpty()) {
            wrapper.eq(ProductPrintingDO::getPrintTypeName, printTypeName);
        }
        if (alignmentTypeName != null && !alignmentTypeName.isEmpty()) {
            wrapper.eq(ProductPrintingDO::getAlignmentTypeName, alignmentTypeName);
        }

        List<ProductPrintingDO> list = productPrintingDAO.selectList(wrapper);
        return list.stream()
                .map(ProductPrintingDO::getProductId)
                .collect(Collectors.toSet());
    }
}
