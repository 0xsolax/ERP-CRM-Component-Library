package com.qiaomoyun.vo.sal.yt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 订单详情里面的物流导出的产品的规格信息
 */
@Data
public class SalYtOrderExportDeliverySpecsVo {
    @Schema(description = "规格图片地址")
    private String specsImg;

    @Schema(description = "规格ID")
    private Long specsId;

    @Schema(description = "规格名称")
    private String specsName;

    @Schema(description = "本次发货数量")
    private Integer specsNum;
}
