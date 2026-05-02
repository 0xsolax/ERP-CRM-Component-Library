package com.qiaomoyun.mapper.sto.yt;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiaomoyun.entity.sto.yt.StoYtStoreOrder;
import com.qiaomoyun.param.sto.yt.StoYtStoreOrderQueryParams;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

/**
 * 出入库单Mapper接口
 */
public interface StoYtStoreOrderMapper extends BaseMapper<StoYtStoreOrder> {

    /**
     * 查询出入库单列表
     */
    List<StoYtStoreOrder> selectStoreOrderList(StoYtStoreOrderQueryParams params);

    StoYtStoreOrder selectByPurchaseItemId(Long id);

    HashMap<String, Object> selectOrderItemInfo(Long storeOrderId);

    /**
     * 找到关联指定orderSubItemId且还有剩余入库数量的入库单
     */
    StoYtStoreOrder selectByOrderSubItemIdInList(@Param("orderSubItemId") Long orderSubItemId, @Param("storeOrderIds") List<Long> storeOrderIds);
}
