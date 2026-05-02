/*
 * @author java_deng
 * @date 2025/12/1 16:05
 * @description
 */
package com.qiaomoyun.param.sal.yt;

import com.qiaomoyun.param.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class PurYtApplyPurchaseQueryParams extends BasePageQuery {
    private String orderSubCode;
    @Schema(description = "父订单编号")
    private String orderCode;
    private String productCode;
    private String specificationName;
    private String supplierName;
    private String customerName;
    private Long salesEmployeeId;

    @NotNull(message = "请选择申购单")
    private List<Long> applyPurchaseIdList;
    private Long purchaseId;
    private Boolean isInboundDelivery;
    private Long supplierId;
}
