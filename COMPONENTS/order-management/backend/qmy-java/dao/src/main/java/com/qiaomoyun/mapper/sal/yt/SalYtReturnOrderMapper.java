package com.qiaomoyun.mapper.sal.yt;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiaomoyun.entity.sal.yt.SalYtReturnOrder;
import com.qiaomoyun.param.sal.yt.SalYtReturnOrderQueryParams;
import com.qiaomoyun.vo.sal.yt.SalYtReturnStatsVo;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 退货订单Mapper接口
 */
public interface SalYtReturnOrderMapper extends BaseMapper<SalYtReturnOrder> {

    /**
     * 根据子订单itemId查询退货记录
     * @param orderSubItemId 子订单itemId
     * @return 退货记录列表
     */
    List<SalYtReturnOrder> selectByOrderSubItemId(@Param("orderSubItemId") Long orderSubItemId);

    /**
     * 根据类型和子订单itemId查询退货记录
     * @param type 类型：1订单 2采购单
     * @param orderSubItemId 子订单itemId
     * @return 退货记录
     */
    List<SalYtReturnOrder> selectByTypeAndOrderSubId(@Param("type") Integer type, @Param("orderSubItemId") Long orderSubItemId);

    /**
     * 根据类型和采购项ID查询退货记录
     * @param type 类型：1订单 2采购单
     * @param purchaseItemId 采购项ID
     * @return 退货记录
     */
    List<SalYtReturnOrder> selectByTypeAndPurchaseItemId(@Param("type") Integer type, @Param("purchaseItemId") Long purchaseItemId);

    /**
     * 统计指定子订单item的总退货数量
     * @param orderSubItemId 子订单itemId
     * @return 总退货数量
     */
    Integer sumReturnNumberByOrderSubItemId(@Param("orderSubItemId") Long orderSubItemId);

    /**
     * 根据子订单ID查询所有规格的退货统计信息
     * @param orderSubItemId 子订单ID
     * @return 退货统计信息列表
     */
    List<SalYtReturnStatsVo> getReturnStatsByOrderSubItemId(@Param("orderSubItemId") Long orderSubItemId);

    /**
     * 根据子订单ID和规格ID查询退货记录
     * @param orderSubItemId 子订单ID
     * @param specificationId 规格ID
     * @return 退货记录列表
     */
    List<SalYtReturnOrder> getReturnOrdersByOrderSubItemAndSpec(@Param("orderSubItemId") Long orderSubItemId, @Param("specificationId") Long specificationId);

    /**
     * 根据查询参数获取退货统计信息
     * @param params 查询参数
     * @return 退货统计信息列表
     */
    List<SalYtReturnStatsVo> getReturnStats(@Param("params") SalYtReturnOrderQueryParams params);

    List<SalYtReturnStatsVo> getReturnStats1(@Param("params") SalYtReturnOrderQueryParams params);

    /**
     * 根据查询参数获取退货记录
     * @param params 查询参数
     * @return 退货记录列表
     */
    List<SalYtReturnOrder> getReturnOrdersByParams(@Param("params") SalYtReturnOrderQueryParams params);

    /**
     * 根据采购项ID查询所有规格的退货统计信息
     * @param purchaseItemId 采购项ID
     * @return 退货统计信息列表
     */
    List<SalYtReturnStatsVo> getReturnStatsByPurchaseItemId(@Param("purchaseItemId") Long purchaseItemId);

    /**
     * 根据采购单ID查询所有规格的退货统计信息
     * @param params 查询参数，包含采购单ID、规格名称、产品code、开始时间、结束时间等
     * @return 退货统计信息列表
     */
    List<SalYtReturnStatsVo> getReturnStatsByPurchaseId(@Param("params") SalYtReturnOrderQueryParams params);

    /**
     * 根据采购项ID和子订单项ID查询退货记录
     * @param purchaseItemId 采购项ID
     * @param orderSubItemId 子订单项ID
     * @return 退货记录列表，按创建时间排序
     */
    List<SalYtReturnOrder> selectByPurchaseItemIdOrOrderSubItemId(@Param("purchaseItemId") Long purchaseItemId, @Param("orderSubItemId") Long orderSubItemId);

    List<SalYtReturnOrder> getReturnByPurchaseId(@Param("returnOrderParams") SalYtReturnOrderQueryParams returnOrderParams);
}