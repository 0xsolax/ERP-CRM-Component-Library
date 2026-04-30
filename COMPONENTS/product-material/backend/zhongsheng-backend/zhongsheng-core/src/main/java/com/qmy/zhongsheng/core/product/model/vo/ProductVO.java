package com.qmy.zhongsheng.core.product.model.vo;

import com.qmy.zhongsheng.core.file.model.vo.FileVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 产品 VO。
 *
 * @author 单漪甜
 */
@Data
@Schema(description = "产品 VO")
public class ProductVO {

    @Schema(description = "主键 ID")
    private Long id;

    @Schema(description = "产品编号")
    private String productCode;

    @Schema(description = "产品类型列表")
    private List<ProductTypeVO> productTypes;

    @Schema(description = "毛重 (g)")
    private BigDecimal grossWeight;

    @Schema(description = "净重 (g)")
    private BigDecimal netWeight;

    @Schema(description = "损耗/杂费")
    private BigDecimal lossFee;

    @Schema(description = "货品描述（中文）")
    private String descriptionZh;

    @Schema(description = "货品描述（英文）")
    private String descriptionEn;

    @Schema(description = "体积 (m³)")
    private BigDecimal volume;

    @Schema(description = "小柜 (20GP) 装箱预估")
    private Integer smallCabinet;

    @Schema(description = "高柜 (40HC) 装箱预估")
    private Integer largeCabinet;

    @Schema(description = "总成本")
    private BigDecimal totalCost;

    @Schema(description = "售价")
    private BigDecimal sellingPrice;

    @Schema(description = "箱规（取产品包材中尺寸最大一条的尺寸，如 10*40、10*40*1）")
    private String boxSpec;

    @Schema(description = "装箱数（与箱规对应的产品包材行的装箱数）")
    private Integer boxCount;

    @Schema(description = "产品图片列表")
    private List<FileVO> images;
}
