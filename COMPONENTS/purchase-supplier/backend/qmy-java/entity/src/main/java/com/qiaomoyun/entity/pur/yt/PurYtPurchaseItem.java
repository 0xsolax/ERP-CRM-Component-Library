package com.qiaomoyun.entity.pur.yt;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qiaomoyun.entity.BaseEntity;
import com.qiaomoyun.entity.pro.yt.ProYtProductFile;
import com.qiaomoyun.entity.pro.yt.ProYtProductSpecificationItem;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * 采购订单子表实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pur_yt_purchase_item")
public class PurYtPurchaseItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 采购订单ID
     */
    @TableField("purchase_id")
    private Long purchaseId;

    /**
     * 申请采购ID
     */
    @TableField("apply_purchase_id")
    private Long applyPurchaseId;

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
     * 订单子ID
     */
    @TableField("order_sub_id")
    private Long orderSubId;
    private Long orderSubItemId;

    /**
     * 供应商单价
     */
    @TableField("supplier_price")
    private BigDecimal supplierPrice;

    /**
     * 采购规格备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 定制化属性id
     */
    @TableField("category_specification_item_id")
    private Long categorySpecificationItemId;

    /**
     * 定制化属性item
     */
    @TableField("category_specification_item_name")
    private String categorySpecificationItemName;

    /**
     * 采购数量
     */
    @TableField("number")
    private Integer number;
    private Integer enterNumber;

    /**
     * 客户id
     */
    @TableField("customer_id")
    private Long customerId;

    /**
     * 客户名称
     */
    @TableField("customer_name")
    private String customerName;

    /**
     * 销售员工ID
     */
    @TableField("sales_employee_id")
    private Long salesEmployeeId;

    /**
     * 状态
     */
    @TableField("status")
    private String status;

    private String orderRemark;

    private Long confirmItemId;
    private Boolean isNotice;

    /**
     * 产品图片列表
     */
    @TableField(exist = false)
    private List<ProYtProductFile> productImageList;

    /**
     * 规格图片列表
     */
    @TableField(exist = false)
    private List<ProYtProductFile> specificationImageList;

    @TableField(exist = false)
    private List<PurYtPurchaseItem> confirmItemList; //半成品确认的规格

    @TableField(exist = false)
    private Integer deliveryNumber;

    /**
     * 规格项列表
     */
    @TableField(exist = false)
    private List<ProYtProductSpecificationItem> specificationItemList; //规格项列表
    @TableField(exist = false)
    private String productCode;

    @TableField(exist = false)
    private BigDecimal productTotalCost;//产品总成本
    @TableField(exist = false)
    private Integer productTotalNumber;//产品总数量
    @TableField(exist = false)
    private BigDecimal specificationTotalCost;//规格总成本
    @TableField(exist = false)
    private Integer specificationTotalNumber;//规格总数量
    @TableField(exist = false)
    private String orderSubCode;
    @TableField(exist = false)
    private String salesEmployeeName;
    @TableField(exist = false)
    private Integer confirmStatus;
    @TableField(exist = false)
    private Boolean isCustomerStore;

    //采购单编辑参数
    @TableField(exist = false)
    private Integer applyNumber;
    @TableField(exist = false)
    private String supplierSpecification;
    @TableField(exist = false)
    private String supplierSpecificationCode;
    @TableField(exist = false)
    private String minNumber;
    @TableField(exist = false)
    private String orderCode;
    @TableField(exist = false)
    private String orderNote;
    @TableField(exist = false)
    private String handProductLevel;
}