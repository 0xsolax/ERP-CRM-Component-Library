package com.qmy.zhongsheng.core.product.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 产品详情 VO。
 *
 * @author 单漪甜
 */
@Data
@Schema(description = "产品详情 VO")
public class ProductDetailVO {

    @Schema(description = "产品基本信息")
    private ProductVO product;

    @Schema(description = "伞架信息")
    private ProductUmbrellaFrameVO umbrellaFrame;

    @Schema(description = "其他材料列表")
    private List<ProductMaterialVO> materials;

    @Schema(description = "面料列表")
    private List<ProductFabricVO> fabrics;

    @Schema(description = "包材列表")
    private List<ProductPackagingVO> packagingList;

    @Schema(description = "印刷列表")
    private List<ProductPrintingVO> printingList;

    @Schema(description = "工价列表")
    private List<ProductProcessPriceVO> processPriceList;
}
