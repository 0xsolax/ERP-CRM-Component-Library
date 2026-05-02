/*
 * @author java_deng
 * @date 2025/11/6 16:15
 * @description
 */
package com.qiaomoyun.entity.pro.yt;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.qiaomoyun.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProYtProductSpecificationSupplier extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long supplierId;
    private Long specificationId;
    private String supplierSpecification;
    private String supplierSpecificationCode;
    private String minNumber;
    private BigDecimal supplierPrice;

    @TableField(exist = false)
    private String supplierName;
    @TableField(exist = false)
    private String supplierCode;

    //供应商产品信息对照表查询参数
    @TableField(exist = false)
    private String productCode;
    @TableField(exist = false)
    private String specificationName;

    @TableField(exist = false)
    private List<ProYtProductFile> imageList;
    @TableField(exist = false)
    private List<ProYtProductSpecificationItem> itemList;
}
