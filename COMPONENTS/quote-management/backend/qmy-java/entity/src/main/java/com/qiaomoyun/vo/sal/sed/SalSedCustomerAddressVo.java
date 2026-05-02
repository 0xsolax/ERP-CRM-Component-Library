package com.qiaomoyun.vo.sal.sed;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

/**
 * 客户收货地址信息
 */
@Data
public class SalSedCustomerAddressVo {
    @Schema(description = "id")
    private Long id;

    @Schema(description = "客户id")
    private Long customerId;

    @Schema(description = "收货人")
    private String consignee;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "国家地区")
    private String countryRegion;

    @Schema(description = "国家地区id")
    private Long countryRegionId;

    @Schema(description = "区县id")
    private Long regionId;

    @Schema(description = "省份")
    private String province;

    @Schema(description = "城市")
    private String city;

    @Schema(description = "区县")
    private String county;

    @Schema(description = "详细地址")
    private String detail;

//    @Schema(description = "省份id")
//    private Long provinceId;
//
//    @Schema(description = "城市id")
//    private Long cityId;
//
//    @Schema(description = "区县id")
//    private Long countyId;
}
