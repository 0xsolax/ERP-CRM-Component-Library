package com.qmy.zhongsheng.core.product.manager;

import com.qmy.zhongsheng.core.product.model.entity.ProductProcessPriceDO;

import java.util.List;
import java.util.Set;

/**
 * 产品工价 Manager。
 *
 * @author 单漪甜
 */
public interface ProductProcessPriceManager {

    /**
     * 根据产品 ID 列表查询工价列表。
     *
     * @param productIds 产品 ID 列表
     * @return 工价列表
     */
    List<ProductProcessPriceDO> listByProductIds(List<Long> productIds);

    /**
     * 保存或更新产品工价。
     *
     * @param processPriceDO 产品工价 DO
     * @return 主键 ID
     */
    Long saveOrUpdate(ProductProcessPriceDO processPriceDO);

    /**
     * 根据产品 ID 删除工价。
     *
     * @param productId 产品 ID
     * @return 是否删除成功
     */
    Boolean deleteByProductId(Long productId);
}
