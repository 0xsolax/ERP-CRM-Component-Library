package com.qiaomoyun.entity.sto.yt;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qiaomoyun.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 入库单操作表
 * </p>
 *
 * @author 系统
 * @since 2023-01-01
 */
@TableName("sto_yt_store_order_operation")
@Data
public class StoYtStoreOrderOperation extends BaseEntity{
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Integer type;
    private Long storeOrderId;
    private Integer number;

    /**
     * 自动分配的订单号以及数量
     */
    @TableField(typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private List<StoYtStoreOrderOperationDetail> operationDetail;

    @TableField(exist = false)
    private String createUserName;

}