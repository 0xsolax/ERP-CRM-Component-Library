package com.qiaomoyun.mapper.sal.sed;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiaomoyun.entity.sal.sed.SalSedOrderDetail;
import com.qiaomoyun.vo.sal.sed.SalSedOrderDetailVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 订单明细Mapper接口
 */

public interface SalSedOrderDetailMapper extends BaseMapper<SalSedOrderDetail> {

    List<SalSedOrderDetailVo> getOrderDetailListWithInfo(Long orderId);

    /**
     * 根据报价单 ID 统计不重复的报价单 SKU 数量
     * @param quotationId 报价单 ID
     * @return 不重复的 SKU 数量
     */
    Long countDistinctQuotationSkuIdByQuotationId(Long quotationId);

    /**
     * 检查现有订单中是否存在相同的 productId + matchId + skuId 组合
     * @param productId 产品ID
     * @param matchId 搭配ID
     * @param skuId SKU ID
     * @return 存在的订单详情列表
     */
    List<SalSedOrderDetail> checkSkuExistsInOrders(@Param("productId") Long productId,
                                                     @Param("matchId") Long matchId,
                                                     @Param("skuId") Long skuId);

    /**
     * 查询指定 SKU 组合（productId + matchId + skuId）存在于哪些订单中
     * @param productId 产品ID
     * @param matchId 搭配ID
     * @param skuId SKU ID
     * @return 订单ID列表
     */
    List<Long> findOrderIdsBySkuTriplet(@Param("productId") Long productId,
                                         @Param("matchId") Long matchId,
                                         @Param("skuId") Long skuId);

    /**
     * 根据报价单SKU ID统计订单详情数量
     * @param quotationSkuId 报价单SKU ID
     * @return 订单详情数量
     */
    Long countByQuotationSkuId(Long quotationSkuId);

}