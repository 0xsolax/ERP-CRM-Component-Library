package com.qmy.zhongsheng.core.supplier.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.api.dto.supplier.SupplierInquiryListQueryDTO;
import com.qmy.zhongsheng.core.supplier.model.entity.SupplierInquiryDO;

/**
 * 供应商询价台账 Manager。
 *
 * @author AI Coding
 */
public interface SupplierInquiryManager {

    Long saveOrUpdate(SupplierInquiryDO row);

    SupplierInquiryDO getById(Long id);

    Page<SupplierInquiryDO> page(SupplierInquiryListQueryDTO query);

    Boolean delete(Long id);
}
