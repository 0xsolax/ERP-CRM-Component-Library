package com.qmy.zhongsheng.core.product.manager;

import com.qmy.zhongsheng.core.product.model.entity.ProductPackagingDO;

import java.util.List;
import java.util.Set;

/**
 * 产品包材 Manager。
 *
 * @author 单漪甜
 */
public interface ProductPackagingManager {

    /**
     * 根据产品 ID 列表查询包材列表。
     *
     * @param productIds 产品 ID 列表
     * @return 包材列表
     */
    List<ProductPackagingDO> listByProductIds(List<Long> productIds);

    /**
     * 保存或更新产品包材。
     *
     * @param packagingDO 产品包材 DO
     * @return 主键 ID
     */
    Long saveOrUpdate(ProductPackagingDO packagingDO);

    /**
     * 根据产品 ID 删除包材。
     *
     * @param productId 产品 ID
     * @return 是否删除成功
     */
    Boolean deleteByProductId(Long productId);
}
