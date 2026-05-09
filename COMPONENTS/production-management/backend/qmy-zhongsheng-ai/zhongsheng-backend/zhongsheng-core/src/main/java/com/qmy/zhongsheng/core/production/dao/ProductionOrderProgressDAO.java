package com.qmy.zhongsheng.core.production.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qmy.zhongsheng.core.production.model.entity.ProductionOrderProgressDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 生产总单进度 DAO。
 *
 * @author AI Coding
 */
@Mapper
public interface ProductionOrderProgressDAO extends BaseMapper<ProductionOrderProgressDO> {
}
