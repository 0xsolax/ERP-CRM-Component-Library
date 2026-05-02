package com.qiaomoyun.param.sal.sed;

import com.qiaomoyun.vo.pro.sed.ProSedFileVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 采购成本确认、提交审核、总裁微信审核操作
 */

@Data
public class SalSedQuotationOperateParams {

    @Schema(description = "报价单id")
    private Long id;

    @Schema(description = "业务员id")
    private Long salesmanId;

    @Schema(description = "采购成本")
    private BigDecimal procurementCost;

    @Schema(description = "包材信息")
    private List<SalSedQuotationPackingParams> packingInfo;

    @Schema(description = "零件信息")
    private List<SalSedQuotationPartParams> partInfo;

    @Schema(description = "配件信息")
    private List<SalSedQuotationFittingParams> fittingInfo;

    @Schema(description = "总裁微信审核凭证图片列表")
    private List<ProSedFileVO> imageList;
}
