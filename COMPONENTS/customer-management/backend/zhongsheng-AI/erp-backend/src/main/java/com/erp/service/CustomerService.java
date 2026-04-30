package com.erp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.entity.Customer;

public interface CustomerService extends IService<Customer> {
    IPage<Customer> page(Page<Customer> page, String keyword, String ownerUsername);
}
