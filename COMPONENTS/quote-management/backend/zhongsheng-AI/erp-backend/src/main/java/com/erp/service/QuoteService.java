package com.erp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.entity.Quote;

public interface QuoteService extends IService<Quote> {
    IPage<Quote> page(Page<Quote> page, String keyword);
}
