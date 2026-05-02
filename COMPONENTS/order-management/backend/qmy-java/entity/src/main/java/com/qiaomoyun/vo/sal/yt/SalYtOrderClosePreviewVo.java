package com.qiaomoyun.vo.sal.yt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SalYtOrderClosePreviewVo {

    /** 是否允许关闭 */
    @Schema(description = "是否允许关闭")
    private Boolean canClose = Boolean.FALSE;

    /** 提示信息 */
    @Schema(description = "提示信息")
    private String message = "";

    /** 待采购数量 */
    @Schema(description = "待采购数量")
    private Integer pendingPurchaseQty = 0;

    /** 待入库数量 */
    @Schema(description = "待入库数量")
    private Integer waitEnterQty = 0;

    /** 待打包数量 */
    @Schema(description = "待打包数量")
    private Integer waitPackageQty = 0;

    /** 待发货数量 */
    @Schema(description = "待发货数量")
    private Integer waitDeliveryQty = 0;

    /** 已发货数量 */
    @Schema(description = "已发货数量")
    private Integer deliveredQty = 0;

    /** 可关闭数量 */
    @Schema(description = "可关闭数量")
    private Integer closableQty = 0;

    /** 是否存在半成品 */
    @Schema(description = "是否存在半成品")
    private Boolean hasIncompleteItem = Boolean.FALSE;

    /** 是否存在独立仓商品 */
    @Schema(description = "是否存在独立仓商品")
    private Boolean hasCustomerStoreItem = Boolean.FALSE;

    /** 关闭预览明细 */
    @Schema(description = "关闭预览明细")
    private List<SalYtOrderClosePreviewItemVo> itemList = new ArrayList<>();
}
