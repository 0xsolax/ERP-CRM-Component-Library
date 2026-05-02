package com.qiaomoyun.entity.sal.yt;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qiaomoyun.entity.BaseEntity;
import com.qiaomoyun.entity.pro.yt.ProYtProductFile;
import com.qiaomoyun.entity.pro.yt.ProYtProductSpecificationItem;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 退货订单实体类
 * 对应表：sal_yt_return_order
 */
@Data
@TableName("sal_yt_return_order")
public class SalYtReturnOrder extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 类型：1订单 2采购单
     */
    private Integer type;
    private Long orderSubItemId;
    private Long purchaseItemId;
    private Integer beforeReturnNumber;
    private Integer returnNumber;
    private String reason;

    @TableField(exist = false)
    private Long specificationId;
    @TableField(exist = false)
    private Long productId;
    @TableField(exist = false)
    private String productCode;
    @TableField(exist = false)
    private String returnUserName;
    @TableField(exist = false)
    private String specificationName;

    @TableField(exist = false)
    private List<ProYtProductFile> imageList;
    @TableField(exist = false)
    private List<ProYtProductSpecificationItem> itemList;

    //采购单详情退货信息字段
    @TableField(exist = false)
    private Integer purchaseInitNumber;
    @TableField(exist = false)
    private Integer purchaseTotalReturnNumber;
    @TableField(exist = false)
    private Integer purchaseCurrentNumber;

    //订单详情退货信息字段
    @TableField(exist = false)
    private Integer orderInitNumber;
    @TableField(exist = false)
    private Integer orderTotalReturnNumber;
    @TableField(exist = false)
    private Integer orderCurrentNumber;
    @TableField(exist = false)
    private Long orderSubId;
    @TableField(exist = false)
    private String orderSubCode;

}