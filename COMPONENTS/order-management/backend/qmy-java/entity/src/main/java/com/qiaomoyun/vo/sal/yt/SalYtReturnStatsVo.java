package com.qiaomoyun.vo.sal.yt;

import com.qiaomoyun.entity.pro.yt.ProYtProductFile;
import com.qiaomoyun.entity.pro.yt.ProYtProductSpecificationItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import com.qiaomoyun.entity.sal.yt.SalYtReturnOrder;

/**
 * 退货统计信息VO
 */
@Data
public class SalYtReturnStatsVo {

    @Schema(description = "子订单id")
    private Long orderSubId;

//    @Schema(description = "产品Id")
//    private Long productId;

//    @Schema(description = "规格id")
//    private Long specificationId;

    @Schema(description = "定制化属性id")
    private Long labelId;

    @Schema(description = "订单规格备注")
    private String remark;

    @Schema(description = "销售单价")
    private BigDecimal price;

    @Schema(description = "供应商价格")
    private BigDecimal supplierPrice;
    /**
     * 规格ID
     */
    private Long specificationId;

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 产品编号
     */
    private String productCode;

    /**
     * 初始数量（最早退货记录中的before_return_number）
     */
    private Integer initialNumber;

    /**
     * 总退货数量
     */
    private Integer totalReturnNumber;

    /**
     * 剩余数量（初始数量 - 总退货数量）
     */
    private Integer remainingNumber;

    /**
     * 退货记录列表
     */
    private List<SalYtReturnOrder> returnOrderList;
    private List<ProYtProductFile> imageList;
    private List<ProYtProductSpecificationItem> itemList;
}