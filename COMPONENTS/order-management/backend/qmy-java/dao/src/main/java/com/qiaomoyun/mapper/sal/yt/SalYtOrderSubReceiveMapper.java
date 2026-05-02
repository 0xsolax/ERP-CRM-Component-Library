package com.qiaomoyun.mapper.sal.yt;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiaomoyun.entity.sal.yt.SalYtOrderSubReceive;
import com.qiaomoyun.param.fin.yt.FinYtReceiveQueryParams;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 一唐-子订单回款Mapper接口
 */
public interface SalYtOrderSubReceiveMapper extends BaseMapper<SalYtOrderSubReceive> {

    /**
     * 根据条件查询子订单回款列表
     * @param salYtOrderSubReceive 查询条件
     * @return 子订单回款列表
     */
    List<SalYtOrderSubReceive> list(SalYtOrderSubReceive salYtOrderSubReceive);

    /**
     * 根据子订单ID查询回款总金额
     * @param orderSubId 子订单ID
     * @return 回款总金额
     */
    BigDecimal getTotalReceiveAmountByOrderSubId(Long orderSubId);

    /**
     * 查询订单收款列表
     * @param params 查询参数
     * @return 订单收款列表
     */
    List<Map<String, Object>> selectOrderReceiveList(FinYtReceiveQueryParams params);

    /**
     * 根据条件查询子订单回款明细列表
     * @param params 查询参数
     * @return 子订单回款明细列表
     */
    List<SalYtOrderSubReceive> listByParams(FinYtReceiveQueryParams params);

    /**
     * 根据条件查询订单回款记录
     * @param orderId 订单ID
     * @param receiveAmount 回款金额
     * @param receiveTime 回款时间
     * @param currency 币种
     * @param createUser 创建人
     * @return 订单回款
     */
    SalYtOrderSubReceive selectBySalYtOrderSubReceive(@Param("orderId") Long orderId,@Param("receiveAmount") BigDecimal receiveAmount,@Param("receiveTime") LocalDateTime receiveTime,@Param("currency") Integer currency, @Param("createUser") Long createUser);

    /**
     * 根据主订单ID查询订单回款信息
     * @param orderId
     * @return
     */
    SalYtOrderSubReceive selectByOrderId(Long orderId);
}
