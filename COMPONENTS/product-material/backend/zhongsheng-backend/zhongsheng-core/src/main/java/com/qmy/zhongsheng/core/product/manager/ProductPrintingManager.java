package com.qmy.zhongsheng.core.product.manager;

import com.qmy.zhongsheng.core.product.model.entity.ProductPrintingDO;

import java.util.List;
import java.util.Set;

/**
 * 产品印刷 Manager。
 *
 * @author 单漪甜
 */
public interface ProductPrintingManager {

    /**
     * 根据产品 ID 列表查询印刷列表。
     *
     * @param productIds 产品 ID 列表
     * @return 印刷列表
     */
    List<ProductPrintingDO> listByProductIds(List<Long> productIds);

    /**
     * 保存或更新产品印刷。
     *
     * @param printingDO 产品印刷 DO
     * @return 主键 ID
     */
    Long saveOrUpdate(ProductPrintingDO printingDO);

    /**
     * 根据产品 ID 删除印刷。
     *
     * @param productId 产品 ID
     * @return 是否删除成功
     */
    Boolean deleteByProductId(Long productId);

    /**
     * 根据印刷方式名称和对齐方式名称（快照）查询产品 ID 列表。
     *
     * @param printTypeName 印刷方式名称（快照）
     * @param alignmentTypeName 对齐方式名称（快照）
     * @return 产品 ID 列表
     */
    Set<Long> getProductIdsByPrintingNames(String printTypeName, String alignmentTypeName);
}
