package com.qiaomoyun.mapper.sto.yt;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiaomoyun.entity.sto.yt.StoYtDeliveryBox;

import java.util.List;
import java.util.Set;

/**
 * 发货箱Mapper接口
 */
public interface StoYtDeliveryBoxMapper extends BaseMapper<StoYtDeliveryBox> {
    /**
     * 根据deliveryId集合查询发货箱
     * @param deliveryIds deliveryId集合
     * @return 发货箱列表
     */
    List<StoYtDeliveryBox> selectByDeliveryIds(Set<Long> deliveryIds);

    List<StoYtDeliveryBox> selectByDeliveryId(Long deliveryId);

    void deleteByDeliveryId(Long deliveryId);
}
