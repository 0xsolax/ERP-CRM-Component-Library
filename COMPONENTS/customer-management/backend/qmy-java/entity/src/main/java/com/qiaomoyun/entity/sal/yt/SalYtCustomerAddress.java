/*
 * @author java_deng
 * @date 2024/11/20 16:35
 * @description 客户地址实体类
 */
package com.qiaomoyun.entity.sal.yt;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qiaomoyun.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 客户地址表实体类
 */
@Data
@TableName("sal_yt_customer_address")
public class SalYtCustomerAddress extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    @NotNull(message = "客户id不能为空")
    private Long customerId;
    @NotBlank(message = "请输入收货人")
    private String consignee;
    @NotBlank(message = "请输入手机号")
    private String phone;
    @NotBlank(message = "请输入国家地区")
    private String countryRegion;
    @NotNull(message = "请输入国家地区")
    private Long countryRegionId;
    private Long regionId;
//    @NotBlank(message = "请选择省份")
    private String province;
//    @NotBlank(message = "请选择城市")
    private String city;
//    @NotBlank(message = "请选择区县")
    private String county;
    @NotBlank(message = "请输入详细地址")
    private String detail;

    @TableField(exist = false)
    private Long provinceId;
    @TableField(exist = false)
    private Long cityId;
    @TableField(exist = false)
    private Long countyId;

    //客户信息
    @Schema(description = "客户订单默认备注")
    @TableField(exist = false)
    private String orderDefaultRemark;
}