package com.qiaomoyun.entity.sto.yt;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qiaomoyun.entity.BaseEntity;
import com.qiaomoyun.entity.pro.yt.ProYtProductFile;
import com.qiaomoyun.entity.pro.yt.ProYtProductSpecificationItem;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 发货箱物品实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sto_yt_delivery_box_item")
public class StoYtDeliveryBoxItem extends BaseEntity {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 发货箱ID
     */
    @TableField("delivery_box_id")
    private Long deliveryBoxId;

    /**
     * 规格ID
     */
    @TableField("specification_id")
    private Long specificationId;

    /**
     * 库位ID
     */
    @TableField("location_id")
    private Long locationId;

    /**
     * 打包数量
     */
    @TableField("number")
    private Integer number;

    private Long categorySpecificationItemId;
    private String categorySpecificationItemName;
    private Long orderItemId;

    /**
     * 产品ID
     */
    @TableField(exist = false)
    private Long productId;

    /**
     * 产品编码
     */
    @TableField(exist = false)
    private String productCode;

    /**
     * 库位名称
     */
    @TableField(exist = false)
    private String locationName;

    /**
     * 订单ID
     */
    @TableField(exist = false)
    private Long orderId;

    /**
     * 订单子ID
     */
    @TableField(exist = false)
    private Long orderSubId;

    /**
     * 订单子项ID
     */
    @TableField(exist = false)
    private Long orderSubItemId;

    /**
     * 图片列表
     */
    @TableField(exist = false)
    private List<ProYtProductFile> imageList;

    /**
     * 规格详情列表
     */
    @TableField(exist = false)
    private List<ProYtProductSpecificationItem> itemList;

    /**
     * 所属订单号（查询时关联 sal_yt_order.code 返回，多订单时逗号分隔）
     */
    @TableField(exist = false)
    private String orderNo;
}
