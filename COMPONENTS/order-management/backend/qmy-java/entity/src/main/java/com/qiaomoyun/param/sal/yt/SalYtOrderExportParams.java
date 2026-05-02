/*
 * @author java_deng
 * @date 2025/11/27 16:04
 * @description
 */
package com.qiaomoyun.param.sal.yt;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SalYtOrderExportParams {
   // @NotNull(message = "子订单id不能为空")
    private Long orderSubId;

    @NotNull(message = "父订单id不能为空")
    private Long orderId;
    private Integer type;
    private Boolean isEnglish;
    private Boolean isCustomerItemNumber;
    private Boolean isShowSpecification;
    private Boolean isShowMade;
    private Integer abandonNumber;
}
