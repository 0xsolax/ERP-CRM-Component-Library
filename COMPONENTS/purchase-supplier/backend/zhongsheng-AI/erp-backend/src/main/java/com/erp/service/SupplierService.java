package com.erp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.entity.Supplier;

public interface SupplierService extends IService<Supplier> {
    IPage<Supplier> page(Page<Supplier> page, String keyword);
}
