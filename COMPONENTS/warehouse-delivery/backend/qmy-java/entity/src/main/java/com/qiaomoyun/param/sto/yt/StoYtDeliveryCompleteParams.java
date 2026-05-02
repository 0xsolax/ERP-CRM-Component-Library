/*
 * @author java_deng
 * @date 2026/3/8 15:47
 * @description
 */
package com.qiaomoyun.param.sto.yt;

import lombok.Data;

import java.util.List;

@Data
public class StoYtDeliveryCompleteParams {
    private Long deliveryId;
    /** 前端传入的需要通知的订单号列表（状态列为红色的订单） */
    private List<String> orderCodes;
}
