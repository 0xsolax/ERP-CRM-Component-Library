package com.qiaomoyun.mapper.pur.yt;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiaomoyun.entity.pur.yt.PurYtPurchaseItem;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 采购订单子表Mapper接口
 */
public interface PurYtPurchaseItemMapper extends BaseMapper<PurYtPurchaseItem> {

    /**
     * 根据条件查询采购订单子表列表
     * @param purYtPurchaseItem 查询条件
     * @return 采购订单子表列表
     */
    List<PurYtPurchaseItem> list(PurYtPurchaseItem purYtPurchaseItem);

    /**
     * 根据采购订单ID查询子表列表
     * @param purchaseId 采购订单ID
     * @return 采购订单子表列表
     */
    List<PurYtPurchaseItem> listByPurchaseId(Long purchaseId);

    /**
     * 根据采购订单ID计算采购总价
     * @param purchaseId 采购订单ID
     * @return 采购总价
     */
    BigDecimal calculateTotalAmountByPurchaseId(Long purchaseId);

    Integer calculateWaitEnterByPurchaseId(Long purchaseId);

    /**
     * 根据采购订单ID获取最小状态值
     * @param purchaseId 采购订单ID
     * @return 最小状态值
     */
    String getMinStatusByPurchaseId(Long purchaseId);

    /**
     * 批量插入采购订单子表
     * @param purchaseItemList 采购订单子表列表
     */
    void saveBatch(List<PurYtPurchaseItem> purchaseItemList);

    /**
     * 根据采购单ID、订单子ID、规格名称、产品code查询采购单产品列表
     * @param params 查询参数
     * @return 采购单产品列表
     */
    List<PurYtPurchaseItem> completedListByPurchaseAndProductParams(com.qiaomoyun.param.pur.yt.PurYtPurchaseProductQueryParams params);
    List<PurYtPurchaseItem> listByPurchaseAndProductParams(com.qiaomoyun.param.pur.yt.PurYtPurchaseProductQueryParams params);

    /**
     * 根据采购单ID、订单子ID、产品code查询采购单半成品列表（没有规格的产品）
     * @param params 查询参数
     * @return 采购单半成品列表
     */
    List<PurYtPurchaseItem> listSemiFinishedProductsByParams(com.qiaomoyun.param.pur.yt.PurYtPurchaseProductQueryParams params);

    PurYtPurchaseItem selectByOrderSubItemId(Long orderSubItemId);

    List<PurYtPurchaseItem> selectListByOrderSubItemId(Long orderSubItemId);

    List<Map<String, Object>> sumTemporaryNumberByOrderSubItemIds(@Param("orderSubItemIds") List<Long> orderSubItemIds);

    List<PurYtPurchaseItem> selectByConfirmId(Long confirmId);

    List<PurYtPurchaseItem> selectByPurchaseId(Long purchaseId);

    List<PurYtPurchaseItem> selectByPurchaseIdAndStatus(Long purchaseId, String status);

    List<PurYtPurchaseItem> selectCompletedByPurchaseIdAndStatus(Long purchaseId, String status);

    /**
     * 根据采购订单ID获取半成品状态map
     * @param purchaseId 采购订单ID
     * @return 半成品状态map，key为"已确认"或"未确认"，value为对应的数量
     */
    List<Map<String, Long>> getSemiFinishedProductStatusMapByPurchaseId(Long purchaseId);
}
