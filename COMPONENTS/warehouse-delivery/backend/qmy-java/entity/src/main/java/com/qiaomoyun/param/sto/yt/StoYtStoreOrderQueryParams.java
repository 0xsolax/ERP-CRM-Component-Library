/*
 * @author java_deng
 * @date 2025/12/5 15:30
 * @description 出入库单查询参数
 */
package com.qiaomoyun.param.sto.yt;

import com.qiaomoyun.param.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 出入库单查询参数
 */
@Data
public class StoYtStoreOrderQueryParams extends BasePageQuery {
    @Schema(description = "订单单号")
    private String orderCode;
    private String purchaseCode;
    private String supplierName;

    private Long storeOrderId;

//    @Schema(description = "出入库单号")
//    private String orderCode;

    @Schema(description = "出入库类型")
    private String orderType;

    @Schema(description = "商品规格ID")
    private Long specificationId;

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "出入库时间开始")
    private String startTime;

    @Schema(description = "出入库时间结束")
    private String endTime;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "产品编码")
    private String productCode;

    @Schema(description = "规格名称")
    private String specificationName;
    @Schema(description = "入库单id列表")
    private List<Long> storeOrderIdList;
}
