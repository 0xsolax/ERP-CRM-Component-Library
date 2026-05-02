/*
 * @author java_deng
 * @date 2025/12/2 15:23
 * @description
 */
package com.qiaomoyun.param.pur.yt;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.qiaomoyun.entity.pur.yt.PurYtPurchase;
import com.qiaomoyun.entity.pur.yt.PurYtPurchaseItem;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PurYtPurchaseUpdateParams extends PurYtPurchase {
    @NotBlank(message = "状态不能为空")
    private String status;

    @NotNull(message = "supplierId不能为空")
    private Long supplierId;

    @NotBlank(message = "供应商姓名不能为空")
    private String supplierName;

    @NotNull(message = "1688单号不能为空")
    private String orderPlatformCode;

    @NotNull(message = "运费不能为空")
    private BigDecimal shippingCost;

    @DecimalMin(value = "0.00", inclusive = true, message = "折扣不能小于0")
    private BigDecimal discountAmount;

    @NotNull(message = "交货时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deliveryTime;

    @NotBlank(message = "付款方式不能为空")
    private String payMethod;

    private String payWay;

    @NotNull(message = "是否入库发货不能为空")
    private Boolean isInboundDelivery;

    private List<PurYtPurchaseItem> itemList;
}
