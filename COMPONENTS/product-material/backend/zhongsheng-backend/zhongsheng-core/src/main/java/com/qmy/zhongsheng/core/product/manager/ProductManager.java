package com.qmy.zhongsheng.core.product.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.core.product.model.condition.ProductQueryCondition;
import com.qmy.zhongsheng.core.product.model.entity.ProductDO;

import java.util.List;
import java.util.Set;

/**
 * 产品 Manager（仅负责 product 主表）。
 *
 * @author 单漪甜
 */
public interface ProductManager {

    /**
     * 根据 ID 获取产品。
     *
     * @param id 产品 ID
     * @return 产品 DO
     */
    ProductDO getById(Long id);

    /**
     * 保存或更新产品。
     *
     * @param product 产品 DO
     * @return 产品 ID
     */
    Long saveOrUpdate(ProductDO product);

    /**
     * 分页查询产品列表（仅主表；多表筛选由 Service 解析为 {@link ProductQueryCondition#getIds()}）。
     *
     * @param condition 分页与主表 ID 限制条件
     * @return 分页结果
     */
    Page<ProductDO> page(ProductQueryCondition condition);

    /**
     * 删除产品（逻辑删除）。
     *
     * @param id 产品 ID
     * @return 是否删除成功
     */
    Boolean deleted(Long id);

    /**
     * 根据 ID 列表批量获取产品。
     *
     * @param ids 产品 ID
     * @return 产品 DO
     */
    List<ProductDO> listByIds(Set<Long> ids);
}
