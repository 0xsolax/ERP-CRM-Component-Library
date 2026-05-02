package com.erp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.entity.Orders;

public interface OrdersService extends IService<Orders> {
    IPage<Orders> page(Page<Orders> page, String keyword);
}
