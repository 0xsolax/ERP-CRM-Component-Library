package com.qmy.zhongsheng.core.supplier.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.api.dto.supplier.SupplierInquiryHistoryQueryDTO;
import com.qmy.zhongsheng.api.dto.supplier.SupplierInquiryListQueryDTO;
import com.qmy.zhongsheng.api.dto.supplier.SupplierInquirySaveDTO;
import com.qmy.zhongsheng.core.supplier.model.vo.SupplierInquiryVO;

/**
 * 供应商询价台账服务。
 *
 * @author AI Coding
 */
public interface SupplierInquiryService {

    Long saveOrUpdate(SupplierInquirySaveDTO dto);

    Page<SupplierInquiryVO> page(SupplierInquiryListQueryDTO query);

    SupplierInquiryVO detail(Long id);

    Boolean delete(Long id);

    Page<SupplierInquiryVO> history(SupplierInquiryHistoryQueryDTO query);
}
