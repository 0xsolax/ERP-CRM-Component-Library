package com.qiaomoyun.entity.sto.yt;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.qiaomoyun.entity.BaseEntity;
import com.qiaomoyun.entity.pro.yt.ProYtProductFile;
import com.qiaomoyun.entity.pro.yt.ProYtProductSpecificationItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * 发货单条目实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sto_yt_delivery_item")
public class StoYtDeliveryItem extends BaseEntity {

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
     * 订单子ID
     */
    @TableField("order_sub_id")
    private Long orderSubId;

    /**
     * 订单子项ID
     */
    @TableField("order_sub_item_id")
    private Long orderSubItemId;

    /**
     * 产品ID
     */
    @TableField("product_id")
    private Long productId;

    /**
     * 规格ID
     */
    @TableField("specification_id")
    private Long specificationId;

    /**
     * 库位id
     */
    @TableField("location_id")
    private Long locationId;

    /**
     * 总数量
     */
    @TableField("number")
    private Integer number;

    /**
     * 已发货数量
     */
    @TableField("shipped_number")
    private Integer shippedNumber;

    /**
     * 定制属性id
     */
    @TableField("category_specification_item_id")
    private Long categorySpecificationItemId;

    /**
     * 定制属性值
     */
    @TableField("category_specification_item_name")
    private String categorySpecificationItemName;


    //列表返回参数
    @TableField(exist = false)
    private Long orderId;
    @TableField(exist = false)
    private String orderSubCode;
    @TableField(exist = false)
    private String orderCode;
    @TableField(exist = false)
    private Integer shippingMethod;
    @TableField(exist = false)
    private String orderRemark;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(exist = false)
    private Date orderDeliveryTime;
    @TableField(exist = false)
    private String saleEmployeeName;


    //详情返回参数
    @TableField(exist = false)
    private String productCode;
    @TableField(exist = false)
    private String locationName;
    @TableField(exist = false)
    private List<ProYtProductFile> imageList;
    @TableField(exist = false)
    private List<ProYtProductSpecificationItem> itemList;
    @TableField(exist = false)
    private String specificationDesc;
    @TableField(exist = false)
    private Boolean isCustomerStore;

    @TableField(exist = false)
    private Boolean productComplete;

    @TableField(exist = false)
    private Boolean packageComplete;

    @TableField(exist = false)
    private Integer packageNumber;
    @TableField(exist = false)
    private Integer enterNumber;

    @Schema(description = "收件人")
    @TableField(exist = false)
    private String receiver;
    @Schema(description = "收件人电话")
    @TableField(exist = false)
    private String receiverPhone;
}
