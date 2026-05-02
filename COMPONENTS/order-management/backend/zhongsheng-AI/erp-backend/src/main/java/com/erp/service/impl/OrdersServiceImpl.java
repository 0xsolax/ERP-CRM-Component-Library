package com.erp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.entity.Orders;
import com.erp.mapper.OrdersMapper;
import com.erp.service.OrdersService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
    @Override
    public IPage<Orders> page(Page<Orders> page, String keyword) {
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Orders::getCode, keyword);
        }
        wrapper.orderByDesc(Orders::getCreatedAt);
        return this.page(page, wrapper);
    }
}
