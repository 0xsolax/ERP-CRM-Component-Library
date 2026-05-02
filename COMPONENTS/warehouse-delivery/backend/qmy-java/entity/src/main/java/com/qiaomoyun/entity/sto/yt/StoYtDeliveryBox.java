package com.qiaomoyun.entity.sto.yt;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qiaomoyun.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 发货箱实体类
 */
@Data
@TableName("sto_yt_delivery_box")
public class StoYtDeliveryBox extends BaseEntity {

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

    private String boxCode;

    /**
     * 箱子ID
     */
    @TableField("box_id")
    private Long boxId;

    /**
     * 箱子重量
     */
    @TableField("box_weight")
    private java.math.BigDecimal boxWeight;

    /**
     * 箱子尺寸
     */
    @TableField("box_size")
    private String boxSize;

    @TableField(exist = false)
    List<StoYtDeliveryBoxItem> boxItemList;

    @TableField(exist = false)
    private StoYtBox box;
}
