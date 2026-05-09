package com.qmy.zhongsheng.core.supplier.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.api.dto.supplier.SupplierListQueryDTO;
import com.qmy.zhongsheng.api.dto.supplier.SupplierSaveDTO;
import com.qmy.zhongsheng.core.supplier.model.vo.SupplierOptionVO;
import com.qmy.zhongsheng.core.supplier.model.vo.SupplierVO;

import java.util.List;

/**
 * 供应商服务。
 *
 * @author AI Coding
 */
public interface SupplierService {

    Long saveOrUpdate(SupplierSaveDTO dto);

    Page<SupplierVO> page(SupplierListQueryDTO query);

    List<SupplierOptionVO> listOptions(SupplierListQueryDTO query);

    SupplierVO detail(Long id);

    Boolean delete(Long id);
}
