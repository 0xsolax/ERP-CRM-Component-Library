package com.qiaomoyun.vo.sal.sed;

import com.qiaomoyun.vo.pro.sed.ProSedFileVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 报价单详情
 */
@Data
public class SalSedQuotationDetailVo {

    @Schema(description = "报价单id")
    private Long id;

    @Schema(description = "客户id")
    private Long customerId;

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "收货地址id")
    private Long receiveAddressId;

    @Schema(description = "收货地址")
    private String receiveAddress;

    @Schema(description = "特殊要求")
    private String specialRequirements;

    @Schema(description = "业务员id")
    private Long salesmanId;

    @Schema(description = "业务员名称")
    private String salesmanName;

    @Schema(description = "采购成本")
    private BigDecimal procurementCost;

    @Schema(description = "采购成本状态 0=待确认，1=已确认")
    private String procurementCostState;

    @Schema(description = "运输体积")
    private BigDecimal volume;

    @Schema(description = "物流成本")
    private BigDecimal logisticsCost;

    @Schema(description = "物流成本状态 0=待确认，1=已确认")
    private String logisticsCostState;

    @Schema(description = "物流备注")
    private String logisticsRemark;

    @Schema(description = "优惠金额")
    private BigDecimal discountAmount;

    @Schema(description = "毛利率")
    private BigDecimal grossProfitMargin;

    @Schema(description = "物流占订单比例")
    private BigDecimal logisticsProportion;

    @Schema(description = "订单金额")
    private BigDecimal orderAmount;

    @Schema(description = "订单状态")
    private String status;

    @Schema(description = "状态展示文案（与 status 对应）")
    private String statusLabel;

    @Schema(description = "当前用户是否可点击「财务审核通过」")
    private Boolean canFinanceApprove;

    @Schema(description = "当前用户是否可点击「总裁审核通过」")
    private Boolean canPresidentApprove;

    @Schema(description = "当前用户是否可点击「总裁微信审核通过」")
    private Boolean canPresidentWxApprove;

    @Schema(description = "总裁微信审核凭证图片列表")
    private List<ProSedFileVO> presidentWxAuditImageList;

    @Schema(description = "驳回原因")
    private String rejectReason;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "是否含税")
    private String tax;

    @Schema(description = "装运港")
    private String fob;

    @Schema(description = "指定地点")
    private String exw;

    @Schema(description = "汇率")
    private BigDecimal exchangeRate;

    @Schema(description = "转订单状态：0=未转换，1=已转换")
    private String shiftStatus;

    //SKU 信息
    @Schema(description = "报价单中的SKU信息")
    private List<SalSedQuotationSkuVo> skuList;


    //历史记录
    @Schema(description = "历史记录")
    private List<SalSedQuotationHistoryVo> historyList;

}
