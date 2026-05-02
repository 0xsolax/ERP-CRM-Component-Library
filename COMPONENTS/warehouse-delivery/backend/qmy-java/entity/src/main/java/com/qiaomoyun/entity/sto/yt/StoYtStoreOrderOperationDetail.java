/*
 * @author java_deng
 * @date 2025/12/9 16:26
 * @description
 */
package com.qiaomoyun.entity.sto.yt;

import lombok.Data;

@Data
public class StoYtStoreOrderOperationDetail {
    private Long orderId;
    private String orderCode;
    private Integer number;
}
