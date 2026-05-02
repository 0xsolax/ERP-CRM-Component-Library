/*
 * @author java_deng
 * @date 2025/12/18 11:05
 * @description
 */
package com.qiaomoyun.vo.sto.yt;

import com.qiaomoyun.entity.sto.yt.StoYtDelivery;
import com.qiaomoyun.entity.sto.yt.StoYtDeliveryItem;
import com.qiaomoyun.entity.sto.yt.StoYtTransportCompany;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class StoYtDeliveryVo extends StoYtDelivery {
    List<StoYtDeliveryItem> itemList;
    private Boolean isChina;
    private String consignee;
    private String phone;
    private String orderSubCode;
    private String orderCode;
    private String salesEmployeeName;
    private String orderSubShippingMethod;
    private String orderShippingMethod;
    private String orderSubRemark;
    private String orderRemark;
    private Boolean orderProductComplete; // 订单产品状态
    private Boolean orderPackageComplete; // 订单打包状态

    private StoYtTransportCompany stoYtTransportCompany;


    //发货单付款列表
    private List<Map<String, Object>> orderSubAmountList;
    private List<Map<String, Object>> orderAmountList;
    private java.math.BigDecimal receiveAmount;
    private String transportCompanyName;

    @Schema(description = "回款币种")
    private Integer deliveryReceiveCurrency;
}
