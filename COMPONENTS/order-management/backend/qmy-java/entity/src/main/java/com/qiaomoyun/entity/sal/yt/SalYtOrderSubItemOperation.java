package com.qiaomoyun.entity.sal.yt;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qiaomoyun.entity.BaseEntity;

import lombok.Data;

import java.util.Date;

/**
 * 亚拓订单子订单商品项操作记录表(SalYtOrderSubItemOperation)表实体类
 *
 * @author makejava
 * @since 2024-01-01 10:00:00
 */
@Data
@TableName("sal_yt_order_sub_item_operation")
public class SalYtOrderSubItemOperation extends BaseEntity{

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer type;
    private Long orderSubItemId;
    private Long purchaseItemId;

    /**
     * 操作数量
     */
    private Integer operationCount;
    private String operationCode;

    /**
     * 占用库存
     */
    private Integer occupyStore;

    /**
     * 占用在途
     */
    private Integer occupyTransit;

    /**
     * 申请采购数量
     */
    private Integer applyPurchaseCount;

    /**
     * 操作订单编号
     */
    private String operationOrderCode;

    /**
     * 包裹编号
     */
    private String packageCode;

    @TableField(exist = false)
    private String createUserName;

}