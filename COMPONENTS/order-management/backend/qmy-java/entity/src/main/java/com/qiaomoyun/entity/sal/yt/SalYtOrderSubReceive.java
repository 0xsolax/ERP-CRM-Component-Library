/*
 * @author java_deng
 * @date 2026/01/09 16:30
 * @description 一唐-子订单回款实体类
 */
package com.qiaomoyun.entity.sal.yt;

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
 * 一唐-子订单回款实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sal_yt_order_sub_receive")
public class SalYtOrderSubReceive extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 子订单ID
     */
    @TableField("order_sub_id")
    private Long orderSubId;

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

    /**
     * 主订单id
     */
    @TableField("order_id")
    private Long orderId;

    /**
     * 第三方回款时间
     */
    @TableField("third_receive_time")
    private LocalDateTime thirdReceiveTime;

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
