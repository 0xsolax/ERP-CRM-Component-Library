package com.qiaomoyun.mapper.sto.yt;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiaomoyun.entity.sto.yt.StoYtDeliveryReceive;
import com.qiaomoyun.param.fin.yt.FinYtReceiveQueryParams;

import java.math.BigDecimal;
import java.util.List;

/**
 * 一唐-发货回款Mapper接口
 */
public interface StoYtDeliveryReceiveMapper extends BaseMapper<StoYtDeliveryReceive> {

    /**
     * 根据条件查询发货回款列表
     * @param params 查询条件
     * @return 发货回款列表
     */
    List<StoYtDeliveryReceive> list(FinYtReceiveQueryParams params);

    /**
     * 根据发货单ID查询已回款金额总和
     * @param deliveryId 发货单ID
     * @return 已回款金额总和
     */
    BigDecimal getTotalReceiveByDeliveryId(Long deliveryId);

    /**
     * 根据发货单ID查询发货单回款信息
     * @param deliveryId
     * @return
     */
    List<StoYtDeliveryReceive> selectByDeliveryId(Long deliveryId);
}
