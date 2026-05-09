package com.qmy.zhongsheng.core.supplier.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.core.supplier.model.entity.SupplierDO;

import java.util.List;

/**
 * 供应商 Manager。
 *
 * @author AI Coding
 */
public interface SupplierManager {

    Long saveOrUpdate(SupplierDO row);

    SupplierDO getById(Long id);

    boolean existsByCode(String code, Long excludeId);

    Page<SupplierDO> page(String keyword, Integer status, Integer pageNum, Integer pageSize);

    List<SupplierDO> listOptions(String keyword);

    Boolean delete(Long id);
}
