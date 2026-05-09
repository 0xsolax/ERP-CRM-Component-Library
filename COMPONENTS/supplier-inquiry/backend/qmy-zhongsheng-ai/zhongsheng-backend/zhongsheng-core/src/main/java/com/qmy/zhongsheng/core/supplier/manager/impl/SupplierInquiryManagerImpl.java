package com.qmy.zhongsheng.core.supplier.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.api.dto.supplier.SupplierInquiryListQueryDTO;
import com.qmy.zhongsheng.core.supplier.dao.SupplierInquiryDAO;
import com.qmy.zhongsheng.core.supplier.manager.SupplierInquiryManager;
import com.qmy.zhongsheng.core.supplier.model.entity.SupplierInquiryDO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 供应商询价台账 Manager 实现。
 *
 * @author AI Coding
 */
@Component
@RequiredArgsConstructor
public class SupplierInquiryManagerImpl implements SupplierInquiryManager {

    private static final String VALID_STATUS_EFFECTIVE = "effective";

    private static final String VALID_STATUS_EXPIRED = "expired";

    private final SupplierInquiryDAO supplierInquiryDAO;

    @Override
    public Long saveOrUpdate(SupplierInquiryDO row) {
        if (row.getId() == null) {
            supplierInquiryDAO.insert(row);
            return row.getId();
        }
        supplierInquiryDAO.updateById(row);
        return row.getId();
    }

    @Override
    public SupplierInquiryDO getById(Long id) {
        if (id == null) {
            return null;
        }
        return supplierInquiryDAO.selectOne(Wrappers.<SupplierInquiryDO>lambdaQuery()
                .eq(SupplierInquiryDO::getId, id)
                .eq(SupplierInquiryDO::getIsDeleted, 0)
                .last("LIMIT 1"));
    }

    @Override
    public Page<SupplierInquiryDO> page(SupplierInquiryListQueryDTO query) {
        if (query == null) {
            query = new SupplierInquiryListQueryDTO();
        }
        Page<SupplierInquiryDO> page = new Page<>(query.getPageNum(), query.getPageSize());
        return supplierInquiryDAO.selectPage(page, buildQuery(query));
    }

    @Override
    public Boolean delete(Long id) {
        LambdaUpdateWrapper<SupplierInquiryDO> update = Wrappers.<SupplierInquiryDO>lambdaUpdate()
                .eq(SupplierInquiryDO::getId, id)
                .eq(SupplierInquiryDO::getIsDeleted, 0)
                .set(SupplierInquiryDO::getIsDeleted, 1)
                .set(SupplierInquiryDO::getDeletedTime, LocalDateTime.now());
        supplierInquiryDAO.update(update);
        return Boolean.TRUE;
    }

    private LambdaQueryWrapper<SupplierInquiryDO> buildQuery(SupplierInquiryListQueryDTO query) {
        LambdaQueryWrapper<SupplierInquiryDO> wrapper = Wrappers.<SupplierInquiryDO>lambdaQuery()
                .eq(SupplierInquiryDO::getIsDeleted, 0)
                .eq(query.getSupplierId() != null, SupplierInquiryDO::getSupplierId, query.getSupplierId())
                .eq(StringUtils.hasText(query.getTargetType()), SupplierInquiryDO::getTargetType, trimToNull(query.getTargetType()))
                .eq(query.getTargetId() != null, SupplierInquiryDO::getTargetId, query.getTargetId())
                .eq(StringUtils.hasText(query.getCurrency()), SupplierInquiryDO::getCurrency, trimUpper(query.getCurrency()))
                .ge(query.getQuoteDateFrom() != null, SupplierInquiryDO::getQuoteDate, query.getQuoteDateFrom())
                .le(query.getQuoteDateTo() != null, SupplierInquiryDO::getQuoteDate, query.getQuoteDateTo())
                .ge(query.getPriceMin() != null, SupplierInquiryDO::getPrice, query.getPriceMin())
                .le(query.getPriceMax() != null, SupplierInquiryDO::getPrice, query.getPriceMax());
        if (StringUtils.hasText(query.getKeyword())) {
            String like = query.getKeyword().trim();
            wrapper.and(w -> w.like(SupplierInquiryDO::getSupplierName, like)
                    .or().like(SupplierInquiryDO::getSupplierCode, like)
                    .or().like(SupplierInquiryDO::getTargetName, like)
                    .or().like(SupplierInquiryDO::getTargetCode, like)
                    .or().like(SupplierInquiryDO::getSpecification, like)
                    .or().like(SupplierInquiryDO::getRemark, like));
        }
        if (StringUtils.hasText(query.getValidStatus())) {
            String status = trimLower(query.getValidStatus());
            LocalDate today = LocalDate.now();
            if (VALID_STATUS_EFFECTIVE.equals(status)) {
                wrapper.and(w -> w.isNull(SupplierInquiryDO::getValidUntil).or().ge(SupplierInquiryDO::getValidUntil, today));
            } else if (VALID_STATUS_EXPIRED.equals(status)) {
                wrapper.lt(SupplierInquiryDO::getValidUntil, today);
            }
        }
        return wrapper.orderByDesc(SupplierInquiryDO::getQuoteDate)
                .orderByDesc(SupplierInquiryDO::getUpdateTime)
                .orderByDesc(SupplierInquiryDO::getId);
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private String trimUpper(String value) {
        return StringUtils.hasText(value) ? value.trim().toUpperCase() : null;
    }

    private String trimLower(String value) {
        return StringUtils.hasText(value) ? value.trim().toLowerCase() : null;
    }
}
