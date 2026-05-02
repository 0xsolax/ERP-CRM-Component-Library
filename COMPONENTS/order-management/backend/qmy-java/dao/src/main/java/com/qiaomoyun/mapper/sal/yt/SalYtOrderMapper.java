package com.qiaomoyun.mapper.sal.yt;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiaomoyun.entity.sal.yt.SalYtOrder;
import com.qiaomoyun.entity.sal.yt.SalYtOrderSub;
import com.qiaomoyun.param.sal.yt.SalYtOrderQueryParams;
import com.qiaomoyun.vo.sal.yt.SalYtOrderVo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 订单主表Mapper接口
 * @author system
 */
public interface SalYtOrderMapper extends BaseMapper<SalYtOrder> {

    /**
     * 查询订单主表列表
     * @return 订单主表集合
     */
    List<SalYtOrderVo> selectSalYtOrderList(SalYtOrderQueryParams params);

    SalYtOrderVo detail(Long orderId);

    /**
     * 查询客户近一年订单总金额
     * @param customerId 客户ID
     * @return 订单总金额
     */
    BigDecimal getYearOrderAmountByCustomerId(Long customerId);

    /**
     * 查询客户最近下单时间
     * @param customerId 客户ID
     * @return 最近下单时间
     */
    LocalDateTime getLastOrderTimeByCustomerId(Long customerId);

    /**
     * 查询客户在指定日期范围内的月度消费统计
     * @param customerId 客户ID
     * @param startTime 起始时间
     * @param endTime 结束时间
     * @return 月度消费统计列表
     */
    List<Map<String, Object>> getMonthlyConsumptionByCustomerId(@Param("customerId") Long customerId,
                                                               @Param("startTime") LocalDateTime startTime,
                                                               @Param("endTime") LocalDateTime endTime);

    /**
     * 查询客户在指定日期范围内的产品分类消费占比
     * @param customerId 客户ID
     * @param startTime 起始时间
     * @param endTime 结束时间
     * @return 产品分类消费占比列表
     */
    List<Map<String, Object>> getConsumptionRatioByCustomerId(@Param("customerId") Long customerId,
                                                             @Param("startTime") LocalDateTime startTime,
                                                             @Param("endTime") LocalDateTime endTime);


    /**
     * 根据平台类型查询订单平台单号信息
     * @return
     */
    List<SalYtOrder> getOrderNoByThirdPlatform(@Param("platform") String platform);

    /**
     * 根据业务员ID、跟进人和数据所属日期查询销售业绩订单（按 submit_order_time 归属）
     * @return 订单列表
     */
    List<SalYtOrder> selectBySalesIdAndDataBelongTime(@Param("saleEmployeeId") Long saleEmployeeId,@Param("followEmployeeId") Long followEmployeeId,@Param("dataBelongTime") LocalDateTime dataBelongTime,@Param("dataBelongTimeEnd") LocalDateTime dataBelongTimeEnd);

    /**
     * 根据订单ID查询订单子表
     * @param id
     * @return
     */
    List<SalYtOrderSub> selectSalYtOrderSubByOrderId(Long id);

    /**
     * 查找满足条件的完成业绩订单（三个完成时间都存在时，取最晚时间归属）
     * @param dataBelongTime 开始时间
     * @param dataBelongTimeEnd 结束时间
     * @return 订单列表
     */
    List<SalYtOrder> selectBySalesIdAndDataBelongTime2(@Param("saleEmployeeId") Long saleEmployeeId,@Param("followEmployeeId") Long followEmployeeId,@Param("dataBelongTime") LocalDateTime dataBelongTime,@Param("dataBelongTimeEnd") LocalDateTime dataBelongTimeEnd);

    /**
     * 根据时间段和用户ID查询订单
     * @param yearTime
     * @param yesterdayTime
     * @return
     */
    List<SalYtOrder> selectOrderListByTimeAndUserId(@Param("saleEmployeeId") Long saleEmployeeId,@Param("followEmployeeId") Long followEmployeeId, @Param("yearTime") LocalDateTime yearTime, @Param("yesterdayTime") LocalDateTime yesterdayTime);

    /**
     * 根据可见业务员范围和时间段查询订单
     */
    List<SalYtOrder> selectOrderListByTimeAndUserIds(@Param("userIds") List<Long> userIds,
                                                     @Param("yearTime") LocalDateTime yearTime,
                                                     @Param("yesterdayTime") LocalDateTime yesterdayTime);

    /**
     * 根据（订单完成时间和回款时间最晚的时间）和用户ID查询订单
     * @param yearTime
     * @param yesterdayTime
     * @return
     */
    List<SalYtOrder> selectOrderListByFinishTimeAndUserId(@Param("saleEmployeeId") Long saleEmployeeId,@Param("followEmployeeId") Long followEmployeeId, @Param("yearTime") LocalDateTime yearTime, @Param("yesterdayTime") LocalDateTime yesterdayTime);

    /**
     * 根据可见业务员范围和完成时间查询订单
     */
    List<SalYtOrder> selectOrderListByFinishTimeAndUserIds(@Param("userIds") List<Long> userIds,
                                                           @Param("yearTime") LocalDateTime yearTime,
                                                           @Param("yesterdayTime") LocalDateTime yesterdayTime);

    /**
     * 根据客户ID和提交时间段查询订单
     * @param customerId
     * @param yesterdayTime
     * @param yesterdayTime1  yesterdayTime减去一年
     * @return
     */
    List<SalYtOrder> selectOrderListByCustomerIdAndTime(@Param("customerId") Long customerId,@Param("yesterdayTime") LocalDateTime yesterdayTime,@Param("yesterdayTime1") LocalDateTime yesterdayTime1);

    SalYtOrder selectByOrderItemId(Long orderItemId);

    /**
     * 根据业务员ID和时间范围查询订单（包含金额汇总）
     * @param saleEmployeeId 业务员ID
     * @param followEmployeeId 跟进人ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 订单列表
     */
    List<SalYtOrder> selectBySalesIdAndTimeRange(@Param("saleEmployeeId") Long saleEmployeeId,
                                                  @Param("followEmployeeId") Long followEmployeeId,
                                                  @Param("startTime") LocalDateTime startTime,
                                                  @Param("endTime") LocalDateTime endTime);

    /**
     * 根据业务员ID和时间范围查询订单，状态（进行中的订单）
     * @param salesmanId
     * @param startDate
     * @param nowDate
     * @return
     */
    List<SalYtOrder> selectOrderByStatus1AndTime(@Param("salesmanId") Long salesmanId,@Param("followEmployeeId") Long followEmployeeId,@Param("startDate") LocalDate startDate,@Param("nowDate") LocalDate nowDate);

    /**
     * 根据可见业务员范围和时间范围查询进行中的订单
     */
    List<SalYtOrder> selectOrderByStatus1AndUserIdsAndTime(@Param("userIds") List<Long> userIds,
                                                           @Param("startDate") LocalDate startDate,
                                                           @Param("nowDate") LocalDate nowDate);

    /**
     * 根据业务员ID和时间范围查询订单，状态（已完成的订单）
     * @param salesmanId
     * @param salesmanId
     * @param startDate
     * @param nowDate
     * @return
     */
    List<SalYtOrder> selectOrderByStatus2AndTime(@Param("salesmanId") Long salesmanId,@Param("followEmployeeId") Long followEmployeeId,@Param("startDate") LocalDate startDate,@Param("nowDate") LocalDate nowDate);

    /**
     * 根据可见业务员范围和时间范围查询已完成的订单
     */
    List<SalYtOrder> selectOrderByStatus2AndUserIdsAndTime(@Param("userIds") List<Long> userIds,
                                                           @Param("startDate") LocalDate startDate,
                                                           @Param("nowDate") LocalDate nowDate);

    /**
     * 根据业务员ID和时间范围查询订单列表
     * @param salesmanId
     * @param salesmanId
     * @param startDate
     * @param nowDate
     * @return
     */
    List<SalYtOrder> selectOrderListBySalesIdAndTimeRange(@Param("salesmanId") Long salesmanId,@Param("followEmployeeId") Long followEmployeeId,@Param("startDate") LocalDate startDate,@Param("nowDate") LocalDate nowDate);

    /**
     * 根据可见业务员范围和时间范围查询订单列表
     */
    List<SalYtOrder> selectOrderListBySalesIdsAndTimeRange(@Param("userIds") List<Long> userIds,
                                                           @Param("startDate") LocalDate startDate,
                                                           @Param("nowDate") LocalDate nowDate);

    /**
     * 根据客户ID查询订单列表
     * @param customerId
     * @return
     */
    List<SalYtOrder> selectOrderListByCustomerId(Long customerId);

    /**
     * 根据订单子表ID查询订单
     * @param orderSubId
     * @return
     */
    SalYtOrder selectSalYtOrderBySubOrderId(Long orderSubId);

    /**
     * 根据发货单ID查询订单
     * @param deliveryId
     * @return
     */
    List<SalYtOrder> selectByDeliveryId(Long deliveryId);
}