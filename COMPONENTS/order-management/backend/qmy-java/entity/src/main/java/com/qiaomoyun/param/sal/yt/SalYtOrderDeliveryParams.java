/*
 * @author AI Assistant
 * @date 2024/02/29
 * @description 确认发货参数类
 */
package com.qiaomoyun.param.sal.yt;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;


/**
 * 确认发货参数类
 */
@Data
public class SalYtOrderDeliveryParams {

    /**
     * 订单子项ID
     */
    @NotNull(message = "订单子项ID不能为空")
    private Long itemId;

    /**
     * 发货数量
     */
    @NotNull(message = "发货数量不能为空")
    private Integer deliveryNumber;

    //订单物流信息查询
    private Long orderSubId;
    private Long orderId;
    private String deliveryCode;
    private LocalDateTime deliveryTime;
    private String packageCode;
    private String productCode;
}
