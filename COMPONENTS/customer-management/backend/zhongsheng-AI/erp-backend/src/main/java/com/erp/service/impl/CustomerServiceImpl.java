package com.erp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.entity.Customer;
import com.erp.mapper.CustomerMapper;
import com.erp.service.CustomerService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {
    @Override
    public IPage<Customer> page(Page<Customer> page, String keyword, String ownerUsername) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Customer::getName, keyword)
                   .or().like(Customer::getCode, keyword)
                   .or().like(Customer::getContact, keyword));
        }
        if (StringUtils.hasText(ownerUsername)) {
            wrapper.eq(Customer::getOwner, ownerUsername);
        }
        wrapper.orderByDesc(Customer::getCreatedAt);
        return this.page(page, wrapper);
    }
}
