/*
 * @author java_deng
 * @date 2025/12/13 15:23
 * @description 采购订单子项通知参数
 */
package com.qiaomoyun.param.pur.yt;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 采购订单子项通知参数
 */
@Data
public class PurYtPurchaseItemNotifyParams {

    /**
     * 采购订单子项ID
     */
    @NotNull(message = "采购订单子项ID不能为空")
    private Long purchaseItemId;

    /**
     * 供应商单价
     */
    @NotNull(message = "供应商单价不能为空")
    private BigDecimal supplierPrice;
}