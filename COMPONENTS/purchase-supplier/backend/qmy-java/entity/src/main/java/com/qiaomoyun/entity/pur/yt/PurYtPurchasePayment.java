/*
 * @author java_deng
 * @date 2026/01/09 15:30
 * @description 一唐-采购单付款实体类
 */
package com.qiaomoyun.entity.pur.yt;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qiaomoyun.entity.BaseEntity;
import com.qiaomoyun.entity.pro.yt.ProYtProductFile;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * 一唐-采购单付款实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pur_yt_purchase_payment")
public class PurYtPurchasePayment extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 采购单ID
     */
    @TableField("purchase_id")
    private Long purchaseId;

    /**
     * 付款金额
     */
    @TableField("amount")
    private BigDecimal amount;
    private Integer currency;

    @TableField(exist = false)
    private String createUserName;

    @TableField(exist = false)
    List<ProYtProductFile> fileList;
}
