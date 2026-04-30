package com.qmy.zhongsheng.core.product.manager;

import com.qmy.zhongsheng.core.product.model.entity.ProductMaterialDO;

import java.util.List;
import java.util.Set;

/**
 * 产品材料 Manager。
 *
 * @author 单漪甜
 */
public interface ProductMaterialManager {

    /**
     * 根据产品 ID 列表查询材料列表。
     *
     * @param productIds 产品 ID 列表
     * @return 材料列表
     */
    List<ProductMaterialDO> listByProductIds(List<Long> productIds);

    /**
     * 保存或更新产品材料。
     *
     * @param materialDO 产品材料 DO
     * @return 主键 ID
     */
    Long saveOrUpdate(ProductMaterialDO materialDO);

    /**
     * 根据产品 ID 删除材料。
     *
     * @param productId 产品 ID
     * @return 是否删除成功
     */
    Boolean deleteByProductId(Long productId);

    /**
     * 根据材料 ID 查询产品 ID 列表。
     *
     * @param materialId 材料 ID
     * @return 产品 ID 列表
     */
    Set<Long> getProductIdsByMaterialId(Long materialId);
}
