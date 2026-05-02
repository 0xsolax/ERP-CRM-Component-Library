package com.qiaomoyun.vo.sal.yt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SalYtOrderClosePreviewItemVo {

    /** 订单子项ID */
    @Schema(description = "订单子项ID")
    private Long orderSubItemId;

    /** 产品ID */
    @Schema(description = "产品ID")
    private Long productId;

    /** 产品规格ID */
    @Schema(description = "产品规格ID")
    private Long specificationId;

    /** 产品编码 */
    @Schema(description = "产品编码")
    private String productCode;

    /** 规格名称 */
    @Schema(description = "规格名称")
    private String specName;

    /** 规格图片 */
    @Schema(description = "规格图片")
    private String specImage;

    /** 单价 */
    @Schema(description = "单价")
    private BigDecimal price;

    /** 下单数量 */
    @Schema(description = "下单数量")
    private Integer totalNumber;

    /** 待采购数量 */
    @Schema(description = "待采购数量")
    private Integer pendingPurchaseQty;

    /** 待入库数量 */
    @Schema(description = "待入库数量")
    private Integer waitEnterQty;

    /** 待打包数量 */
    @Schema(description = "待打包数量")
    private Integer waitPackageQty;

    /** 待发货数量 */
    @Schema(description = "待发货数量")
    private Integer waitDeliveryQty;

    /** 已发货数量 */
    @Schema(description = "已发货数量")
    private Integer deliveredQty;

    /** 可关闭数量 */
    @Schema(description = "可关闭数量")
    private Integer closableQty;

    /** 是否独立仓商品 */
    @Schema(description = "是否独立仓商品")
    private Boolean customerStore;
}
