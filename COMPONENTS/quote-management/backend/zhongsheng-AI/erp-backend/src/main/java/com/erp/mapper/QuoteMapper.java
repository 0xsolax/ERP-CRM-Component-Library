package com.erp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.erp.entity.Quote;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface QuoteMapper extends BaseMapper<Quote> {
}
