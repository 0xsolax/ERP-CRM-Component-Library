package com.qmy.zhongsheng.core.production.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.core.production.dao.ProductionGroupDAO;
import com.qmy.zhongsheng.core.production.manager.ProductionGroupManager;
import com.qmy.zhongsheng.core.production.model.condition.ProductionGroupQueryCondition;
import com.qmy.zhongsheng.core.production.model.entity.ProductionGroupDO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 生产组 Manager 实现。
 *
 * @author AI Coding
 */
@Component
@RequiredArgsConstructor
public class ProductionGroupManagerImpl implements ProductionGroupManager {

    private final ProductionGroupDAO productionGroupDAO;

    @Override
    public Long saveOrUpdate(ProductionGroupDO group) {
        if (group.getId() == null) {
            productionGroupDAO.insert(group);
            return group.getId();
        }
        if (productionGroupDAO.selectById(group.getId()) == null) {
            productionGroupDAO.insert(group);
            return group.getId();
        }
        productionGroupDAO.updateById(group);
        return group.getId();
    }

    @Override
    public ProductionGroupDO getById(Long id) {
        if (id == null) {
            return null;
        }
        return productionGroupDAO.selectOne(Wrappers.<ProductionGroupDO>lambdaQuery()
                .eq(ProductionGroupDO::getId, id)
                .eq(ProductionGroupDO::getIsDeleted, 0)
                .last("LIMIT 1"));
    }

    @Override
    public boolean existsByCode(String code, Long excludeId) {
        if (!StringUtils.hasText(code)) {
            return false;
        }
        LambdaQueryWrapper<ProductionGroupDO> query = Wrappers.<ProductionGroupDO>lambdaQuery()
                .eq(ProductionGroupDO::getIsDeleted, 0)
                .eq(ProductionGroupDO::getCode, code.trim());
        query.ne(excludeId != null, ProductionGroupDO::getId, excludeId);
        return productionGroupDAO.selectCount(query) > 0;
    }

    @Override
    public Page<ProductionGroupDO> page(ProductionGroupQueryCondition condition) {
        return productionGroupDAO.selectPage(new Page<>(condition.getPageNum(), condition.getPageSize()), baseQuery(condition));
    }

    @Override
    public List<ProductionGroupDO> listOptions(String keyword) {
        ProductionGroupQueryCondition condition = new ProductionGroupQueryCondition();
        condition.setKeyword(keyword);
        condition.setStatus(1);
        return productionGroupDAO.selectList(baseQuery(condition).last("LIMIT 50"));
    }

    @Override
    public Boolean delete(Long id) {
        LambdaUpdateWrapper<ProductionGroupDO> update = Wrappers.<ProductionGroupDO>lambdaUpdate()
                .eq(ProductionGroupDO::getId, id)
                .eq(ProductionGroupDO::getIsDeleted, 0)
                .set(ProductionGroupDO::getIsDeleted, 1)
                .set(ProductionGroupDO::getDeletedTime, LocalDateTime.now());
        productionGroupDAO.update(update);
        return Boolean.TRUE;
    }

    private LambdaQueryWrapper<ProductionGroupDO> baseQuery(ProductionGroupQueryCondition condition) {
        LambdaQueryWrapper<ProductionGroupDO> query = Wrappers.<ProductionGroupDO>lambdaQuery()
                .eq(ProductionGroupDO::getIsDeleted, 0);
        if (condition != null && StringUtils.hasText(condition.getKeyword())) {
            String keyword = condition.getKeyword().trim();
            query.and(w -> w.like(ProductionGroupDO::getCode, keyword).or().like(ProductionGroupDO::getName, keyword));
        }
        if (condition != null && condition.getStatus() != null) {
            query.eq(ProductionGroupDO::getStatus, condition.getStatus());
        }
        query.orderByAsc(ProductionGroupDO::getCode).orderByDesc(ProductionGroupDO::getCreateTime);
        return query;
    }
}
