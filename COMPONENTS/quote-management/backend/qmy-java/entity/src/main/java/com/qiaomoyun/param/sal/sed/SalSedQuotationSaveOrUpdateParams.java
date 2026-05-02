package com.qiaomoyun.param.sal.sed;

import com.qiaomoyun.vo.pro.sed.ProSedFileVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


import java.math.BigDecimal;
import java.util.List;

/**
 * 新增报价单、编辑修改和再次创建报价单参数类
 */
@Data
public class SalSedQuotationSaveOrUpdateParams {

    @Schema(description = "0=暂存，1=计算成本中，2=计算完成，5=总裁未审核，财务未审核，6=总裁审核通过，财务未审核，7=总裁未审核，财务审核通过，8=总裁微信审核通过，4=审核通过，-1=审核驳回")
    @NotNull(message = "状态不能为空")
    private String status;

    @Schema(description = "操作内容")
    @NotNull(message = "操作内容不能为空")
    private String operation;

    @Schema(description = "报价单id,修改才传")
    private Long id;

    @Schema(description = "报价单编号")
    private String quotationCode;

    @Schema(description = "客户id")
    @NotNull(message = "客户id不能为空")
    private Long customerId;

    @Schema(description = "收货地址id")
    @NotNull(message = "收货地址id不能为空")
    private Long receiveAddressId;

    @Schema(description = "特殊要求")
    private String specialRequirements;

    @Schema(description = "业务员id")
    @NotNull(message = "业务员id不能为空")
    private Long salesmanId;

//    @Schema(description = "物流备注")
//    private String logisticsRemark;

    @Schema(description = "优惠金额")
    private BigDecimal discountAmount;

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

    @Schema(description = "SKU信息")
    private List<SalSedQuotationSkuParams> skuList;

}
