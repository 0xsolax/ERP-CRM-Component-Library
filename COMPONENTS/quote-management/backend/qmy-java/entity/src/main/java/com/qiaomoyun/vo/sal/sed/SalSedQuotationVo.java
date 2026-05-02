package com.qiaomoyun.vo.sal.sed;

import com.qiaomoyun.entity.pro.sed.ProSedFile;
import com.qiaomoyun.entity.sal.sed.SalSedQuotation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class SalSedQuotationVo extends SalSedQuotation {

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "业务员名称")
    private String salesmanName;

    @Schema(description = "采购成本明细")
    private Map<String, List<SalSedQuotationProcurementVo>> procurementMap;

    @Schema(description = "包材附件信息集合")
    private List<ProSedFile> attachmentsLists;

    @Schema(description = "总计金额")
    private BigDecimal totalPrice;



    @Schema(description = "订单金额")
    private BigDecimal orderAmount;

    @Schema(description = "状态展示文案（与 status 对应）")
    private String statusLabel;

    @Schema(description = "当前用户是否可点击「财务审核通过」（角色+状态）")
    private Boolean canFinanceApprove;

    @Schema(description = "当前用户是否可点击「总裁审核通过」（角色+状态）")
    private Boolean canPresidentApprove;

}
