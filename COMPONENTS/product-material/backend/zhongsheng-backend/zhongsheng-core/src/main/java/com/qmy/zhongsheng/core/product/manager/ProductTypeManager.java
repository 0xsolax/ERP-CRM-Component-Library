package com.qmy.zhongsheng.core.product.manager;

import com.qmy.zhongsheng.core.product.model.entity.ProductTypeDO;

import java.util.List;
import java.util.Set;

/**
 * 产品类型关联 Manager。
 *
 * @author 单漪甜
 */
public interface ProductTypeManager {

    /**
     * 根据产品 ID 列表查询关联的类型。
     */
    List<ProductTypeDO> listByProductIds(List<Long> productIds);

    /**
     * 保存单条产品类型关联。
     */
    Long saveOrUpdate(ProductTypeDO productTypeDO);

    /**
     * 逻辑删除指定产品的所有类型关联。
     */
    Boolean deleteByProductId(Long productId);

    /**
     * 根据类型 ID 查询关联的产品 ID 集合（分页筛选用）。
     */
    Set<Long> getProductIdsByTypeId(Long typeId);

    /**
     * 按产品类型名称模糊匹配，返回关联的产品 ID 集合。
     *
     * @param keyword 关键词（非空由调用方保证）
     * @return 匹配的产品 ID，无重复
     */
    Set<Long> getProductIdsByTypeNameLike(String keyword);
}
