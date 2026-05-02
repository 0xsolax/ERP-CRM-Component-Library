/*
 * @author java_deng
 * @date 2026/1/12 11:02
 * @description
 */
package com.qiaomoyun.vo.sal.yt;

import com.qiaomoyun.entity.sal.yt.SalYtOrderSub;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SalYtOrderSubVo extends SalYtOrderSub {
    @Schema(description = "收入总计")
    private BigDecimal totalItemPrice;
    @Schema(description = "运费金额")
    private BigDecimal totalShippingPrice;
    @Schema(description = "产品金额")
    private BigDecimal totalOrderPrice;
    @Schema(description = "支出总计")
    private BigDecimal totalItemCost;
    @Schema(description = "运费金额（实际支出）")
    private BigDecimal totalShippingCost;
    @Schema(description = "产品金额（实际支出）")
    private BigDecimal totalOrderCost;
    @Schema(description = "利润")
    private BigDecimal profit;
    @Schema(description = "利润率")
    private BigDecimal profitRate;

    private Long followEmployeeId;
    private String followEmployeeName;
    private Integer followRatio;
    private BigDecimal followingPrice;
    private Long saleEmployeeId;
    private String saleEmployeeName;
    private Integer saleRatio;
    private BigDecimal salePrice;
    private String customerAddress;

    private String customerName;
}
