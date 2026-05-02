package com.qiaomoyun.vo.sal.yt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 订单详情里面的导出物流产品信息
 */
@Data
public class SalYtOrderExportDeliveryProductInfoVo {

    @Schema(description = "发货箱id")
    private Long deliveryBoxId;

    @Schema(description = "箱号")
    private String boxCode;

    @Schema(description = "尺寸")
    private String boxSize;

    @Schema(description = "重量")
    private String boxWeight;

    @Schema(description = "产品id")
    private Long productId;

    @Schema(description = "产品ID/编号")
    private String productCode;

    @Schema(description = "本次发货总数")
    private Integer totalNum;

    @Schema(description = "规格图片地址")
    private String specsImg;

    @Schema(description = "规格ID")
    private Long specsId;

    @Schema(description = "规格名称")
    private String specsName;

    @Schema(description = "规格本次发货数量")
    private Integer specsNum;

    @Schema(description = "规格信息")
    private List<SalYtOrderExportDeliverySpecsVo> specsInfos;
}
