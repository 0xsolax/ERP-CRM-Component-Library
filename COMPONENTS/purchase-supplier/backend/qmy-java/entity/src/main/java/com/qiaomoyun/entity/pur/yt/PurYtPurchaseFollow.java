package com.qiaomoyun.entity.pur.yt;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.qiaomoyun.entity.BaseEntity;
import lombok.Data;
import java.util.Date;

/**
 * 采购跟进实体类
 * 对应表: pur_yt_purchase_follow
 */
@Data
@TableName("pur_yt_purchase_follow")
public class PurYtPurchaseFollow extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 采购订单ID
     */
    private Long purchaseId;

    /**
     * 主题
     */
    private String theme;

    /**
     * 联系人
     */
    private String contactPerson;

    /**
     * 行动描述
     */
    private String description;

    /**
     * 下次回访日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date nextVisitDate;

    @TableField(exist = false)
    private String createUserName;
}