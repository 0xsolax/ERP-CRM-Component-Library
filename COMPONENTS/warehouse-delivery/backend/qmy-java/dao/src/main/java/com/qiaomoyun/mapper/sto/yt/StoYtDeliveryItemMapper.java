package com.qiaomoyun.mapper.sto.yt;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiaomoyun.entity.sto.yt.StoYtDelivery;
import com.qiaomoyun.entity.sto.yt.StoYtDeliveryItem;
import com.qiaomoyun.param.sto.yt.StoYtDeliveryQueryParams;
import com.qiaomoyun.vo.sto.yt.StoYtDeliveryOrderVo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 发货单条目Mapper接口
 */
public interface StoYtDeliveryItemMapper extends BaseMapper<StoYtDeliveryItem> {

    /**
     * 根据条件查询发货单条目列表
     * @param stoYtDeliveryItem 查询条件
     * @return 发货单条目列表
     */
    List<StoYtDeliveryItem> list(StoYtDeliveryItem stoYtDeliveryItem);

    /**
     * 根据发货单ID查询条目列表
     * @param deliveryId 发货单ID
     * @return 发货单条目列表
     */
    List<StoYtDeliveryItem> listByDeliveryId(Long deliveryId);

    /**
     * 批量插入发货单条目
     * @param deliveryItemList 发货单条目列表
     */
    void saveBatch(List<StoYtDeliveryItem> deliveryItemList);

    List<StoYtDeliveryItem> listGroupItem(StoYtDeliveryQueryParams params);

    List<StoYtDeliveryItem> getDeliveryItemsByProduct(StoYtDeliveryQueryParams params);

    List<StoYtDeliveryItem> selectBySubItemId(Long orderSubItemId);

    List<StoYtDeliveryItem> selectOrderSubByDeliveryId(StoYtDeliveryQueryParams params);

    List<StoYtDeliveryItem> selectGroupOrderSubItemByDeliveryId(Long deliveryId);

    Integer selectPackageNumberByOrderSubItemId(Long orderSubItemId);
    Integer selectPackagedAndDeliveredNumberByOrderSubItemId(Long orderSubItemId);
    List<Map<String, Object>> sumWaitDeliveryNumberByOrderSubItemIds(@Param("orderSubItemIds") List<Long> orderSubItemIds);
    /**
     * 根据发货单ID按orderSubId分组查询产品数量
     * @param deliveryId 发货单ID
     * @return 按orderSubId分组的产品数量列表
     */
    List<Map<String, Object>> selectOrderSubIdQuantityByDeliveryId(Long deliveryId);
    List<Map<String, Object>> selectOrderIdQuantityByDeliveryId(Long deliveryId);

    /**
     * 根据发货单ID查询未收运费的子订单及其产品数量
     * @param deliveryId 发货单ID
     * @return 按orderSubId分组的产品数量列表
     */
    List<Map<String, Object>> selectReceiveOrderSubIdQuantityByDeliveryId(Long deliveryId);

    /**
     * 根据子订单ID查询相关发货单信息
     * @param subId 子订单ID
     * @return 发货单信息列表
     */
    List<StoYtDelivery> selectDeliveryByOrderSubId(Long subId);

    /**
     * 根据子订单ID查询相关发货单信息(去除打包状态的)
     * @param subId 子订单ID
     * @return 发货单信息列表
     */
    List<StoYtDelivery> selectDeliveryByOrderSubIdAndNotPackage(Long subId);

    /**
     * 根据订单ID列表查询发货单条目
     * @param orderIdList 订单ID列表
     * @return 发货单条目列表
     */
    List<StoYtDeliveryItem> selectByOrderIds(java.util.List<Long> orderIdList);

    List<Map<String, Object>> selectOrderByDeliveryId(StoYtDeliveryQueryParams params);

    /**
     * 根据发货单ID分组查询涉及的订单
     * @param deliveryId 发货单ID
     * @return 按订单分组的信息列表
     */
    List<StoYtDeliveryOrderVo> listGroupOrderByDeliveryId(Long deliveryId);

    /**
     * 根据子订单详情ID查询发货单条目
     * @param orderSubItemId
     * @return
     */
    StoYtDeliveryItem selectByOrderSubItemId(Long orderSubItemId);

    /**
     * 根据发货单ID查询发货单条目总数
     * @param
     * @return
     */
    BigDecimal selectTotalNumbers(Long deliveryId);

    /**
     * 根据发货单ID查询发货单条目总数(总条数不包括已收运费的订单)
     */
    BigDecimal selectTotalNumbersNotReceive(Long deliveryId);

    /**
     * 根据发货单ID和子订单ID查询发货单条目总数
     * @param deliveryId
     * @param orderSubId
     * @return
     */
    BigDecimal selectSubOrderTotalNumbers( @Param("deliveryId") Long deliveryId,@Param("orderSubId") Long orderSubId);
}
