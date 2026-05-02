package com.qiaomoyun.entity.sal.sed;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qiaomoyun.entity.BaseEntity;
import lombok.Data;

/**
 * 订单操作记录
 */
@Data
@TableName("sal_sed_order_operate_record")
public class SalSedOrderOperateRecord extends BaseEntity {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 订单id
     */
    private Long orderId;
    /**
     * 操作内容
     */
    private String operateContent;
}

