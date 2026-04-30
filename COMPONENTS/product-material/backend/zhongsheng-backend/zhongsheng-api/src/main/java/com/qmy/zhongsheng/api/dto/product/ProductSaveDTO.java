package com.qmy.zhongsheng.api.dto.product;

import com.qmy.zhongsheng.api.dto.file.SystemFileDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 产品保存请求 DTO。
 *
 * @author 单漪甜
 */
@Data
@Schema(description = "产品保存请求 DTO")
public class ProductSaveDTO {

    @Schema(description = "主键 ID；为空表示新增", hidden = true)
    private Long id;

    @Schema(description = "产品编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "产品编号不能为空")
    private String productCode;

    @Schema(description = "产品类型 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "产品类型不能为空")
    private List<Long> productTypeIdList;

    @Schema(description = "毛重 (g)")
    @Digits(integer = 10, fraction = 2, message = "毛重格式无效")
    private BigDecimal grossWeight;

    @Schema(description = "净重 (g)")
    @Digits(integer = 10, fraction = 2, message = "净重格式无效")
    private BigDecimal netWeight;

    @Schema(description = "损耗/杂费")
    @Digits(integer = 20, fraction = 2, message = "损耗/杂费格式无效")
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
    @Digits(integer = 20, fraction = 2, message = "总成本格式无效")
    private BigDecimal totalCost;

    @Schema(description = "售价", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "售价不能为空")
    @Digits(integer = 20, fraction = 2, message = "售价格式无效")
    private BigDecimal sellingPrice;

    @Schema(description = "伞架信息（单条）")
    private ProductUmbrellaFrameSaveDTO umbrellaFrame;

    @Schema(description = "其他材料列表")
    private List<ProductMaterialSaveDTO> materials;

    @Schema(description = "面料列表")
    private List<ProductFabricSaveDTO> fabrics;

    @Schema(description = "印刷列表")
    private List<ProductPrintingSaveDTO> printingList;

    @Schema(description = "包材列表")
    private List<ProductPackagingSaveDTO> packagingList;

    @Schema(description = "工价列表")
    private List<ProductProcessPriceSaveDTO> processPriceList;

    @Schema(description = "产品图片列表（写入 system_file，主类型 PRODUCT / 次类型 PRODUCT_IMAGE）")
    private List<SystemFileDTO> images;
}
