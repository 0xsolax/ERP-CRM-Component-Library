package com.qmy.zhongsheng.core.product.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 产品工价 VO。
 *
 * @author 单漪甜
 */
@Data
@Schema(description = "产品工价 VO")
public class ProductProcessPriceVO {

    @Schema(description = "主键 ID")
    private Long id;

    @Schema(description = "工序 ID")
    private Long processId;

    @Schema(description = "工序名称")
    private String name;

    @Schema(description = "工序金额")
    private BigDecimal price;

}
