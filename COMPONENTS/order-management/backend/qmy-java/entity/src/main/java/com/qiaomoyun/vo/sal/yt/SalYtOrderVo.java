/*
 * @author java_deng
 * @date 2025/11/21 15:20
 * @description
 */
package com.qiaomoyun.vo.sal.yt;

import com.qiaomoyun.entity.sal.yt.SalYtOrder;
import com.qiaomoyun.entity.sal.yt.SalYtOrderSub;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class SalYtOrderVo extends SalYtOrder {
    private BigDecimal amount;
    private Integer subOrderCount;
    private String customerName;
    private String customerCode;
    private String followEmployeeName;
    private String salesEmployeeName;


    @Schema(description = "订单状态")
    private String orderStatus;

    @Schema(description = "订单总金额")
    private BigDecimal orderAmount;

    @Schema(description = "订单总预计成本")
    private BigDecimal orderCostAmount;

    @Schema(description = "订单总预计毛利")
    private BigDecimal orderProfitAmount;

    @Schema(description = "判断是否有半成品订单类型，0=没有，1=有")
    private String hasHalfProductOrder;

    @Schema(description = "成品tab数量")
    private Map<String,Integer> statusCountMap;


    // 子订单列表
    private List<SalYtOrderSub> subOrderList;


    //子订单详情参数
    private SalYtOrderSub subOrder;
}
