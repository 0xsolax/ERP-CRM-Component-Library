package com.qmy.zhongsheng.core.product.manager;

import com.qmy.zhongsheng.core.product.model.entity.ProductFabricDO;

import java.util.List;
import java.util.Set;

/**
 * 产品面料 Manager。
 *
 * @author 单漪甜
 */
public interface ProductFabricManager {

    /**
     * 根据产品 ID 列表查询面料列表。
     *
     * @param productIds 产品 ID 列表
     * @return 面料列表
     */
    List<ProductFabricDO> listByProductIds(List<Long> productIds);

    /**
     * 保存或更新产品面料。
     *
     * @param fabricDO 产品面料 DO
     * @return 主键 ID
     */
    Long saveOrUpdate(ProductFabricDO fabricDO);

    /**
     * 根据产品 ID 删除面料。
     *
     * @param productId 产品 ID
     * @return 是否删除成功
     */
    Boolean deleteByProductId(Long productId);

    /**
     * 根据面料种类名称（快照）查询产品 ID 列表。
     *
     * @param typeName 面料种类名称（快照）
     * @return 产品 ID 列表
     */
    Set<Long> getProductIdsByTypeName(String typeName);
}
