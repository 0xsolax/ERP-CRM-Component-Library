package com.qmy.zhongsheng.core.production.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qmy.zhongsheng.core.production.model.entity.ProductionOrderBatchDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 生产分批安排 DAO。
 *
 * @author AI Coding
 */
@Mapper
public interface ProductionOrderBatchDAO extends BaseMapper<ProductionOrderBatchDO> {
}
