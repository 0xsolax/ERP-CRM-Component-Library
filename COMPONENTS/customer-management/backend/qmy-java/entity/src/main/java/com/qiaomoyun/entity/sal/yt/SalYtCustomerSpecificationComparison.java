/*
 * @author java_deng
 * @date 2025/11/20 10:00
 * @description 客户规格映射实体类
 */
package com.qiaomoyun.entity.sal.yt;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qiaomoyun.entity.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * 客户规格映射表
 */
@Data
@TableName("sal_yt_customer_specification_comparison")
public class SalYtCustomerSpecificationComparison extends BaseEntity implements Serializable {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long customerId;
    private String specification;
    private String customerSpecification;
    private String itemNumber;
    private Long specificationId;
    private Long productId;
}