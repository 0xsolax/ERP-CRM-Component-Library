package com.qiaomoyun.mapper.sal.yt;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiaomoyun.entity.sal.yt.SalYtOrderSub;
import com.qiaomoyun.entity.sal.yt.SalYtOrderSubItem;
import com.qiaomoyun.param.fin.yt.FinYtProfitQueryParams;
import com.qiaomoyun.vo.fin.yt.CustomerProductProfitVo;
import com.qiaomoyun.vo.fin.yt.FinYtCustomerProfitVo;
import com.qiaomoyun.vo.fin.yt.FinYtOrderProfitProductVo;
import com.qiaomoyun.vo.fin.yt.FinYtProfitOrderProfitListVo;
import com.qiaomoyun.vo.sal.yt.SalYtOrderSubVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单子表Mapper接口
 * @author system
 */
public interface SalYtOrderSubMapper extends BaseMapper<SalYtOrderSub> {

    /**
     * 查询订单子表列表
     * @param salYtOrderSub 订单子表
     * @return 订单子表集合
     */
    List<SalYtOrderSub> selectSalYtOrderSubList(SalYtOrderSub salYtOrderSub);

    /**
     * 根据订单ID查询订单子表
     * @param orderId 订单ID
     * @return 订单子表集合
     */
    List<SalYtOrderSub> selectSalYtOrderSubByOrderId(Long orderId);

    /**
     * 根据订单ID删除订单子表
     * @param orderId 订单ID
     * @return 结果
     */
    int deleteSalYtOrderSubByOrderId(Long orderId);

    /**
     * 获取子订单下商品项的最小状态
     *
     * @param orderSubId 子订单ID
     * @return 最小状态
     */
    String selectMinStatusByOrderSubId(Long orderSubId);

    /**
     * 计算子订单总金额（包含运费）
     *
     * @param orderSubId 子订单ID
     * @return 子订单总金额
     */
    BigDecimal calculateOrderSubAmount(Long orderSubId);

    List<FinYtProfitOrderProfitListVo> selectOrderProfitList(FinYtProfitQueryParams params);

    /**
     * 计算子订单回款运费
     *
     * @param orderSubId 子订单ID
     * @return 回款运费
     */
    BigDecimal calculateReceiveShipping(Long orderSubId);

    /**
     * 计算子订单付款运费
     *
     * @param orderSubId 子订单ID
     * @return 付款运费
     */
    BigDecimal calculatePaymentShipping(Long orderSubId);

    /**
     * 根据子订单ID查询订单利润详情
     * @param params 查询参数
     * @return 订单利润详情
     */
    FinYtProfitOrderProfitListVo selectOrderProfitDetail(FinYtProfitQueryParams params);

    /**
     * 查询订单利润产品列表
     * @param params 查询参数
     * @return 产品利润列表
     */
    List<FinYtOrderProfitProductVo> selectOrderProfitProduct(FinYtProfitQueryParams params);

    List<FinYtCustomerProfitVo>  customerProfitList(FinYtProfitQueryParams request);

    FinYtCustomerProfitVo customerProfitDetail(FinYtProfitQueryParams request);

    List<CustomerProductProfitVo> customerProductProfit(FinYtProfitQueryParams request);

    BigDecimal customerProductTotalProfit(FinYtProfitQueryParams request);

    String selectConfirmedItemMinStatusByOrderSubId(Long subId);




    /**
     * 根据订单ID查询订单子表
     * @param orderId
     * @return
     */
    List<SalYtOrderSubItem> selectOrderSubByOrderId(Long orderId);

    /**
     * 查询父订单下其他子订单是否都已经发货完成（排除当前orderSubId）
     * @param orderId
     * @return
     */
    List<SalYtOrderSubItem> selectNotDeliveredOrderSubByOrderId(@Param("orderId") Long orderId,@Param("orderSubId") Long orderSubId);

    /**
     * 根据订单ID查询订单子表（）
     * @param
     * @return
     */
    List<SalYtOrderSub> selectByOrderIdExcludeOrderSubId(@Param("orderId") Long orderId, @Param("orderSubId") Long orderSubId);
}