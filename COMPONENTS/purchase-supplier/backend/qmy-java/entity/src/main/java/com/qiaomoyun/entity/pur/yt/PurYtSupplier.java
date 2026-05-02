/*
 * @author java_deng
 * @date 2025/11/6 15:27
 * @description
 */
package com.qiaomoyun.entity.pur.yt;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.qiaomoyun.entity.BaseEntity;
import com.qiaomoyun.entity.pro.yt.ProYtProductLabel;
import com.qiaomoyun.entity.sal.yt.SalYtContactPerson;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PurYtSupplier extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String code;
    private String name;
    private String shortName;
    private String address;
    private String remark;

    @TableField(exist = false)
    private List<ProYtProductLabel> labelList;
    @TableField(exist = false)
    private List<PurYtSupplierFollow> followList;
    @TableField(exist = false)
    private List<SalYtContactPerson> contactPersonList;
    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date recentPurchaseTime; // 最近采购时间
    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date recentFollowTime; // 最近跟进时间
}