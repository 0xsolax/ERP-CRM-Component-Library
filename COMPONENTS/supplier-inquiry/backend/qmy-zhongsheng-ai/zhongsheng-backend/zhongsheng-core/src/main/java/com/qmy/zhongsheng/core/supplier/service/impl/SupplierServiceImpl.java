package com.qmy.zhongsheng.core.supplier.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.api.dto.supplier.SupplierListQueryDTO;
import com.qmy.zhongsheng.api.dto.supplier.SupplierSaveDTO;
import com.qmy.zhongsheng.common.error.SupplierErrorCodeConstants;
import com.qmy.zhongsheng.common.exception.ServiceExceptionUtil;
import com.qmy.zhongsheng.common.utils.BeanUtils;
import com.qmy.zhongsheng.core.supplier.manager.SupplierManager;
import com.qmy.zhongsheng.core.supplier.model.entity.SupplierDO;
import com.qmy.zhongsheng.core.supplier.model.vo.SupplierOptionVO;
import com.qmy.zhongsheng.core.supplier.model.vo.SupplierVO;
import com.qmy.zhongsheng.core.supplier.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 供应商服务实现。
 *
 * @author AI Coding
 */
@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private static final int STATUS_ENABLED = 1;

    private final SupplierManager supplierManager;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveOrUpdate(SupplierSaveDTO dto) {
        normalize(dto);
        SupplierDO existing = null;
        if (dto.getId() != null) {
            existing = supplierManager.getById(dto.getId());
            if (existing == null) {
                throw ServiceExceptionUtil.exception(SupplierErrorCodeConstants.SUPPLIER_NOT_FOUND);
            }
        }
        if (!StringUtils.hasText(dto.getCode())) {
            dto.setCode(existing != null && StringUtils.hasText(existing.getCode()) ? existing.getCode()
                    : generateInternalCode());
        }
        if (!StringUtils.hasText(dto.getName())) {
            throw ServiceExceptionUtil.exception(SupplierErrorCodeConstants.SUPPLIER_NAME_REQUIRED);
        }
        if (supplierManager.existsByCode(dto.getCode(), dto.getId())) {
            throw ServiceExceptionUtil.exception(SupplierErrorCodeConstants.SUPPLIER_CODE_DUPLICATE);
        }
        SupplierDO row = BeanUtils.toBean(dto, SupplierDO.class);
        return supplierManager.saveOrUpdate(row);
    }

    @Override
    public Page<SupplierVO> page(SupplierListQueryDTO query) {
        if (query == null) {
            query = new SupplierListQueryDTO();
        }
        Page<SupplierDO> doPage = supplierManager.page(query.getKeyword(), query.getStatus(), query.getPageNum(),
                query.getPageSize());
        Page<SupplierVO> voPage = new Page<>(doPage.getCurrent(), doPage.getSize(), doPage.getTotal());
        voPage.setRecords(BeanUtils.toBean(doPage.getRecords(), SupplierVO.class));
        return voPage;
    }

    @Override
    public List<SupplierOptionVO> listOptions(SupplierListQueryDTO query) {
        if (query == null) {
            query = new SupplierListQueryDTO();
        }
        return BeanUtils.toBean(supplierManager.listOptions(query.getKeyword()), SupplierOptionVO.class);
    }

    @Override
    public SupplierVO detail(Long id) {
        SupplierDO row = supplierManager.getById(id);
        if (row == null) {
            throw ServiceExceptionUtil.exception(SupplierErrorCodeConstants.SUPPLIER_NOT_FOUND);
        }
        return BeanUtils.toBean(row, SupplierVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(Long id) {
        if (supplierManager.getById(id) == null) {
            throw ServiceExceptionUtil.exception(SupplierErrorCodeConstants.SUPPLIER_NOT_FOUND);
        }
        return supplierManager.delete(id);
    }

    private void normalize(SupplierSaveDTO dto) {
        dto.setCode(trimToNull(dto.getCode()));
        dto.setName(trimToNull(dto.getName()));
        dto.setContact(trimToNull(dto.getContact()));
        dto.setPhone(trimToNull(dto.getPhone()));
        dto.setEmail(trimToNull(dto.getEmail()));
        dto.setAddress(trimToNull(dto.getAddress()));
        dto.setRemark(trimToNull(dto.getRemark()));
        if (dto.getStatus() == null) {
            dto.setStatus(STATUS_ENABLED);
        }
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private String generateInternalCode() {
        return "SUP" + IdWorker.getIdStr();
    }
}
