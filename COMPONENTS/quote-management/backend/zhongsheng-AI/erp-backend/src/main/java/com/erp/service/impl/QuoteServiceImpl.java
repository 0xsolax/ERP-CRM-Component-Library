package com.erp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.entity.Quote;
import com.erp.mapper.QuoteMapper;
import com.erp.service.QuoteService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class QuoteServiceImpl extends ServiceImpl<QuoteMapper, Quote> implements QuoteService {
    @Override
    public IPage<Quote> page(Page<Quote> page, String keyword) {
        LambdaQueryWrapper<Quote> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Quote::getCode, keyword);
        }
        wrapper.orderByDesc(Quote::getCreatedAt);
        return this.page(page, wrapper);
    }
}
