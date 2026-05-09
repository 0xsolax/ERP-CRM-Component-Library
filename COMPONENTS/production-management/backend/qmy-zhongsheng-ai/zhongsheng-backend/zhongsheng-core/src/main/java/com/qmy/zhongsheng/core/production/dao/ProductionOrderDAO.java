package com.qmy.zhongsheng.core.production.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qmy.zhongsheng.core.production.model.entity.ProductionOrderDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 生产总单 DAO。
 *
 * @author AI Coding
 */
@Mapper
public interface ProductionOrderDAO extends BaseMapper<ProductionOrderDO> {
}
