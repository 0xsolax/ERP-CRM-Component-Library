package com.qmy.zhongsheng.core.product.manager;

import com.qmy.zhongsheng.core.product.model.entity.ProductUmbrellaFrameDO;

import java.util.List;
import java.util.Set;

/**
 * 产品伞架 Manager。
 *
 * @author 单漪甜
 */
public interface ProductUmbrellaFrameManager {

    /**
     * 根据产品 ID 查询伞架。
     *
     * @param productId 产品 ID
     * @return 伞架 DO
     */
    ProductUmbrellaFrameDO getByProductId(Long productId);

    /**
     * 根据产品 ID 列表查询伞架列表。
     *
     * @param productIds 产品 ID 列表
     * @return 伞架列表
     */
    List<ProductUmbrellaFrameDO> listByProductIds(List<Long> productIds);

    /**
     * 保存或更新产品伞架。
     *
     * @param umbrellaFrameDO 产品伞架 DO
     * @return 主键 ID
     */
    Long saveOrUpdate(ProductUmbrellaFrameDO umbrellaFrameDO);

    /**
     * 根据产品 ID 删除伞架。
     *
     * @param productId 产品 ID
     * @return 是否删除成功
     */
    Boolean deleteByProductId(Long productId);

    /**
     * 根据伞架快照名称查询产品 ID 列表。
     *
     * @param typeName     类型名称（快照）
     * @param lengthName   伞架长度名称（快照）
     * @param functionName 功能名称（快照）
     * @param materialName 材料名称（快照）
     * @return 产品 ID 列表
     */
    Set<Long> getProductIdsByNames(String typeName, String lengthName,
                                   String functionName, String materialName);

    /**
     * 根据伞架尺寸三要素（长度、直径、骨数）查询产品 ID 列表。
     *
     * @param lengthName   伞架长度名称（快照）
     * @param diameterName 中棒直径名称（快照）
     * @param ribCountName 伞骨数量名称（快照）
     * @return 产品 ID 集合
     */
    Set<Long> getProductIdsByFrameSize(String lengthName, String diameterName, String ribCountName);
}
