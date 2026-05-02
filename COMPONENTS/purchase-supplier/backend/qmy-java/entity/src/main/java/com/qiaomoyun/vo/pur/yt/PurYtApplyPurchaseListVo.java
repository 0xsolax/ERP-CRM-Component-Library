/*
 * @author java_deng
 * @date 2025/12/1 16:08
 * @description
 */
package com.qiaomoyun.vo.pur.yt;

import com.qiaomoyun.entity.pur.yt.PurYtApplyPurchase;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PurYtApplyPurchaseListVo {
    private Long supplierId;
    private String supplierName;
    private Integer purchaseTotalCount;
    private BigDecimal purchaseTotalAmount;
    private Boolean isInboundDelivery;
    private List<PurYtApplyPurchase> applyPurchaseList;
}
