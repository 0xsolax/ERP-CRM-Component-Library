package com.qiaomoyun.mapper.sto.yt;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiaomoyun.entity.sto.yt.StoYtDelivery;
import com.qiaomoyun.param.sal.yt.SalYtOrderDeliveryParams;
import com.qiaomoyun.param.sto.yt.StoYtDeliveryQueryParams;
import com.qiaomoyun.vo.sto.yt.StoYtDeliveryVo;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * 发货单Mapper接口
 */
public interface StoYtDeliveryMapper extends BaseMapper<StoYtDelivery> {

    /**
     * 根据条件查询发货单列表
     * @param params 查询参数
     * @return 发货单列表
     */
    List<StoYtDeliveryVo> list(StoYtDeliveryQueryParams params);

    /**
     * 根据条件查询发货单列表
     * @param params 订单物流信息查询参数
     * @return 发货单列表
     */
    List<StoYtDeliveryVo> list1(StoYtDeliveryQueryParams params);


    /**
     * 根据编号检查是否存在
     * @param code 发货单编号
     * @param excludeId 排除的ID
     * @return 是否存在
     */
    boolean existsByCode(String code, Long excludeId);

    HashMap<String, Object> selectMapByGroupOrderSub(Long id);

    HashMap<String, Object> selectMapByGroupOrder(Long id);

    List<String> selectOrderSubSales(Long id);

    /**
     * 根据订单物流信息查询参数查询发货单
     * @param params 订单物流信息查询参数
     * @return 发货单列表
     */
    List<StoYtDelivery> selectByDeliveryIdsAndStatus(SalYtOrderDeliveryParams params);

    /**
     * 根据子订单ID查询发货单
     * @param orderSubId
     * @return
     */
    List<StoYtDelivery> selectDeliveryListBySubOrderId(@Param("orderSubId") Long orderSubId,@Param("deliveryId") Long deliveryId);
}
