package com.qiaomoyun.mapper.sal.yt;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiaomoyun.entity.sal.yt.SalYtOrderSubItemOperation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 亚拓订单子订单商品项操作记录Mapper
 *
 * @author makejava
 * @since 2024-01-01 10:00:00
 */
@Mapper
public interface SalYtOrderSubItemOperationMapper extends BaseMapper<SalYtOrderSubItemOperation> {

    /**
     * 根据订单子订单商品项ID查询操作记录
     *
     * @param orderSubItemId 订单子订单商品项ID
     * @return 操作记录列表
     */
    List<SalYtOrderSubItemOperation> selectByOrderSubItemId(Long orderSubItemId);

    /**
     * 根据操作订单编号查询操作记录
     *
     * @param operationOrderCode 操作订单编号
     * @return 操作记录列表
     */
    List<SalYtOrderSubItemOperation> selectByOperationOrderCode(String operationOrderCode);

    /**
     * 批量插入操作记录
     *
     * @param operationList 操作记录列表
     * @return 插入数量
     */
    int batchInsert(List<SalYtOrderSubItemOperation> operationList);

    /**
     * 根据条件查询操作记录统计信息
     *
     * @param params 查询参数
     * @return 统计信息
     */
    List<Map<String, Object>> selectStatistics(Map<String, Object> params);

    /**
     * 根据采购单itemId或子订单项ID查询操作记录
     *
     * @param purchaseItemId 采购单itemId
     * @param orderSubItemId 子订单项ID
     * @return 操作记录列表
     */
    List<SalYtOrderSubItemOperation> selectByPurchaseItemIdOrOrderSubItemId(@Param("purchaseItemId") Long purchaseItemId, @Param("orderSubItemId") Long orderSubItemId);

}