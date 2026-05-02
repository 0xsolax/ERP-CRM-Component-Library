package com.qiaomoyun.entity.sto.yt;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qiaomoyun.entity.BaseEntity;
import com.qiaomoyun.entity.pro.yt.ProYtProductFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 一唐-发货回款实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sto_yt_delivery_receive")
public class StoYtDeliveryReceive extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 发货单ID
     */
    @TableField("delivery_id")
    private Long deliveryId;

    /**
     * 回款金额
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 币种
     */
    @TableField("currency")
    private Integer currency;

    @TableField(exist = false)
    List<ProYtProductFile> fileList;

    @TableField(exist = false)
    private Boolean isCompletedReceive;

    @TableField(exist = false)
    private String createUserName;

    @TableField(exist = false)
    @Schema(description = "回款完成时间")
    private LocalDateTime receiveFinishTime;

}
