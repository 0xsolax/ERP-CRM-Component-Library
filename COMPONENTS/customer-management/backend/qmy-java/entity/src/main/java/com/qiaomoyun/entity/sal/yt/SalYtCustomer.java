/*
 * @author java_deng
 * @date 2024/11/20 16:30
 * @description 客户实体类
 */
package com.qiaomoyun.entity.sal.yt;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qiaomoyun.entity.BaseEntity;
import com.qiaomoyun.entity.pro.yt.ProYtProductLabel;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 客户表实体类
 */
@Data
@TableName("sal_yt_customer")
public class SalYtCustomer extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String code;
    private String name;
    private Long belongEmployeeId;
    private Long followEmployeeId;
    private String type;
    @TableField(exist = false)
    private String typeValue;
    private String handLevel;
    private String countryRegion;
    private String companyWebsite;
    private String companyAddress;
    private String companyName;
    private String customerSource;
    @TableField(exist = false)
    private String customerSourceValue;
    private String remark;
    /**
     * 客户订单默认备注
     */
    private String orderDefaultRemark;

    /**
     * 管家婆客户编码
     */
    private String guanjiapoCode;

    /**
     * 管家婆客户名称
     */
    private String guanjiapoName;

    @TableField(exist = false)
    private String autoCustomerLevel;

    //独立仓属性
    private Integer storeStatus;
    private LocalDateTime storeOperationTime;
    private Long storeOperationUser;
    private Integer storeWarningNumber;

    //无限制售卖客户
    private Boolean isVip;
    @TableField(exist = false)
    private String storeOperationUserName; //驳回账号姓名
    @TableField(exist = false)
    private String belongEmployeeName;
    @TableField(exist = false)
    private String followEmployeeName;
    @TableField(exist = false)
    private String countryRegionName;
    @TableField(exist = false)
    private LocalDateTime followTime;

    @TableField(exist = false)
    private LocalDateTime lastOrderTime;
    @TableField(exist = false)
    private BigDecimal yearOrderAmount;

    @TableField(exist = false)
    private List<ProYtProductLabel> labelList;
}
