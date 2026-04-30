package com.qmy.zhongsheng.core.product.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.core.product.dao.ProductDAO;
import com.qmy.zhongsheng.core.product.manager.ProductManager;
import com.qmy.zhongsheng.core.product.model.condition.ProductQueryCondition;
import com.qmy.zhongsheng.core.product.model.entity.ProductDO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import static com.qmy.zhongsheng.common.utils.ValidityUtils.isNotBlank;
import static com.qmy.zhongsheng.common.utils.ValidityUtils.isNotEmpty;

/**
 * 产品 Manager 实现类（仅负责 product 主表）。
 *
 * @author 单漪甜
 */
@Component
@RequiredArgsConstructor
public class ProductManagerImpl implements ProductManager {

    private final ProductDAO productDAO;

    @Override
    public ProductDO getById(Long id) {
        return productDAO.selectById(id);
    }

    @Override
    public Long saveOrUpdate(ProductDO product) {
        if (product.getId() == null) {
            productDAO.insert(product);
        } else {
            productDAO.updateById(product);
        }
        return product.getId();
    }

    @Override
    public Page<ProductDO> page(ProductQueryCondition condition) {
        if (condition.getIds() != null && condition.getIds().isEmpty()) {
            return new Page<>(condition.getPageNum(), condition.getPageSize(), 0);
        }
        Page<ProductDO> page = new Page<>(condition.getPageNum(), condition.getPageSize());
        return productDAO.selectPage(page, getCommonWrapperByCondition(condition));
    }

    private LambdaQueryWrapper<ProductDO> getCommonWrapperByCondition(ProductQueryCondition condition) {
        LambdaQueryWrapper<ProductDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductDO::getIsDeleted, 0);
        if (isNotEmpty(condition.getIds())) {
            wrapper.in(ProductDO::getId, condition.getIds());
        }
        if (isNotBlank(condition.getKeywords())) {
            String kw = condition.getKeywords();
            wrapper.and(w -> w.like(ProductDO::getProductCode, kw)
                    .or().like(ProductDO::getDescriptionZh, kw)
                    // .or().like(ProductDO::getDescriptionEn, kw)
                    .or().exists("SELECT 1 FROM product_type pt WHERE pt.product_id = product.id "
                            + "AND pt.is_deleted = 0 AND pt.type_name LIKE CONCAT('%', {0}, '%')", kw));
        }
        wrapper.orderByDesc(ProductDO::getCreateTime);
        return wrapper;
    }

    @Override
    public Boolean deleted(Long id) {
        ProductDO product = getById(id);
        if (product == null) {
            return false;
        }
        product.setIsDeleted(1);
        return productDAO.updateById(product) > 0;
    }

    @Override
    public List<ProductDO> listByIds(Set<Long> ids) {
        LambdaQueryWrapper<ProductDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductDO::getIsDeleted, 0)
                .in(ProductDO::getId, ids)
                .orderByDesc(ProductDO::getCreateTime);
        return productDAO.selectList(wrapper);
    }
}
