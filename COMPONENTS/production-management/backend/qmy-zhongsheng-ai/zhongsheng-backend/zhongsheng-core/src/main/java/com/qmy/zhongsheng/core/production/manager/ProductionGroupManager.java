package com.qmy.zhongsheng.core.production.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.core.production.model.condition.ProductionGroupQueryCondition;
import com.qmy.zhongsheng.core.production.model.entity.ProductionGroupDO;

import java.util.List;

/**
 * 生产组 Manager。
 *
 * @author AI Coding
 */
public interface ProductionGroupManager {

    Long saveOrUpdate(ProductionGroupDO group);

    ProductionGroupDO getById(Long id);

    boolean existsByCode(String code, Long excludeId);

    Page<ProductionGroupDO> page(ProductionGroupQueryCondition condition);

    List<ProductionGroupDO> listOptions(String keyword);

    Boolean delete(Long id);
}
