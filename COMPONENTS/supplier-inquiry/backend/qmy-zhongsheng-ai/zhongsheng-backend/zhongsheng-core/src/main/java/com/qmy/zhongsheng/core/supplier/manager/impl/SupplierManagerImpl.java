package com.qmy.zhongsheng.core.supplier.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.core.supplier.dao.SupplierDAO;
import com.qmy.zhongsheng.core.supplier.manager.SupplierManager;
import com.qmy.zhongsheng.core.supplier.model.entity.SupplierDO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 供应商 Manager 实现。
 *
 * @author AI Coding
 */
@Component
@RequiredArgsConstructor
public class SupplierManagerImpl implements SupplierManager {

    private final SupplierDAO supplierDAO;

    @Override
    public Long saveOrUpdate(SupplierDO row) {
        if (row.getId() == null) {
            supplierDAO.insert(row);
            return row.getId();
        }
        supplierDAO.updateById(row);
        return row.getId();
    }

    @Override
    public SupplierDO getById(Long id) {
        if (id == null) {
            return null;
        }
        return supplierDAO.selectOne(Wrappers.<SupplierDO>lambdaQuery()
                .eq(SupplierDO::getId, id)
                .eq(SupplierDO::getIsDeleted, 0)
                .last("LIMIT 1"));
    }

    @Override
    public boolean existsByCode(String code, Long excludeId) {
        if (!StringUtils.hasText(code)) {
            return false;
        }
        return supplierDAO.exists(Wrappers.<SupplierDO>lambdaQuery()
                .eq(SupplierDO::getCode, code.trim())
                .eq(SupplierDO::getIsDeleted, 0)
                .ne(excludeId != null, SupplierDO::getId, excludeId));
    }

    @Override
    public Page<SupplierDO> page(String keyword, Integer status, Integer pageNum, Integer pageSize) {
        Page<SupplierDO> page = new Page<>(pageNum, pageSize);
        return supplierDAO.selectPage(page, baseQuery(keyword, status));
    }

    @Override
    public List<SupplierDO> listOptions(String keyword) {
        return supplierDAO.selectList(baseQuery(keyword, 1).last("LIMIT 1000"));
    }

    @Override
    public Boolean delete(Long id) {
        LambdaUpdateWrapper<SupplierDO> update = Wrappers.<SupplierDO>lambdaUpdate()
                .eq(SupplierDO::getId, id)
                .eq(SupplierDO::getIsDeleted, 0)
                .set(SupplierDO::getIsDeleted, 1)
                .set(SupplierDO::getDeletedTime, LocalDateTime.now());
        supplierDAO.update(update);
        return Boolean.TRUE;
    }

    private LambdaQueryWrapper<SupplierDO> baseQuery(String keyword, Integer status) {
        LambdaQueryWrapper<SupplierDO> query = Wrappers.<SupplierDO>lambdaQuery()
                .eq(SupplierDO::getIsDeleted, 0)
                .eq(status != null, SupplierDO::getStatus, status);
        if (StringUtils.hasText(keyword)) {
            String like = keyword.trim();
            query.and(wrapper -> wrapper.like(SupplierDO::getName, like)
                    .or().like(SupplierDO::getCode, like)
                    .or().like(SupplierDO::getContact, like));
        }
        return query.orderByDesc(SupplierDO::getCreateTime).orderByDesc(SupplierDO::getId);
    }
}
