package com.qiaomoyun.entity.sal.yt;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.qiaomoyun.entity.BaseEntity;
import com.qiaomoyun.entity.pro.yt.ProYtProductFile;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 客户跟进记录表
 */
@Data
@TableName("sal_yt_customer_follow")
public class SalYtCustomerFollow extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 客户ID
     */
    private Long customerId;

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date nextVisitDate;

    @TableField(exist = false)
    private List<ProYtProductFile> fileList;
}