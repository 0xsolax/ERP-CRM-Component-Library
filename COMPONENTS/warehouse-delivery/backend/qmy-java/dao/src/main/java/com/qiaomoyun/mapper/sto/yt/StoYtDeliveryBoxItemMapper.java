package com.qiaomoyun.mapper.sto.yt;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiaomoyun.entity.sto.yt.StoYtDeliveryBoxItem;

import java.util.List;

/**
 * 发货箱物品Mapper接口
 */
public interface StoYtDeliveryBoxItemMapper extends BaseMapper<StoYtDeliveryBoxItem> {

    /**
     * 根据发货箱ID查询包裹物品列表，包含产品code和库位名称
     * @param deliveryBoxId 发货箱ID
     * @return 包裹物品列表
     */
    List<StoYtDeliveryBoxItem> getItemsByBoxId(Long deliveryBoxId);

    /**
     * 根据订单ID列表查询发货箱物品
     * @param orderIdList 订单ID列表
     * @return 发货箱物品列表
     */
    List<StoYtDeliveryBoxItem> selectByOrderIds(java.util.List<Long> orderIdList);

    /**
     * 根据发货单ID查询打包后的物品
     * @param deliveryId 发货单ID
     * @return 发货箱物品列表
     */
    List<StoYtDeliveryBoxItem> selectByDeliveryId(Long deliveryId);

    /**
     * 根据发货单ID和订单ID查询打包后的物品
     * @param params 包含deliveryId和orderId的Map
     * @return 发货箱物品列表
     */
    List<StoYtDeliveryBoxItem> selectByDeliveryIdAndOrderId(java.util.Map<String, Long> params);

    /**
     * 根据发货单ID查询包裹中涉及的不同订单ID
     * @param deliveryId 发货单ID
     * @return 订单ID列表
     */
    List<Long> selectDistinctOrderIdsByDeliveryId(Long deliveryId);

    void deleteByDeliveryBoxId(Long id);

    void deleteByDeliveryId(Long deliveryId);
}
