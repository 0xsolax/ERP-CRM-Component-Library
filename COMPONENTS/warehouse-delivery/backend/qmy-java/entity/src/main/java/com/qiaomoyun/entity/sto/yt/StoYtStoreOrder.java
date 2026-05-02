package com.qiaomoyun.entity.sto.yt;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qiaomoyun.entity.BaseEntity;
import com.qiaomoyun.entity.pro.yt.ProYtProductFile;
import com.qiaomoyun.entity.pro.yt.ProYtProductLabel;
import com.qiaomoyun.entity.pro.yt.ProYtProductSpecificationItem;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 出入库单实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sto_yt_store_order")
public class StoYtStoreOrder extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单编号
     */
    private String code;

    /**
     * 订单类型
     */
    private String type;

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 规格ID
     */
    private Long specificationId;

    /**
     * 供应商ID
     */
    private Long supplierId;

    /**
     * 采购单ID
     */
    private Long purchaseId;

    /**
     * 采购单明细ID
     */
    private Long purchaseItemId;

    /**
     * 位置ID
     */
    private Long locationId;

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 总数
     */
    private Integer totalNumber;

    /**
     * 入库数
     */
    private Integer enterNumber;

    /**
     * 备注
     */
    private String remark;

    @TableField(exist = false)
    private String purchaseCode;
    @TableField(exist = false)
    private String supplierName;
    @TableField(exist = false)
    private String productCode;
    @TableField(exist = false)
    private List<Long> storeOrderIdList;
    @TableField(exist = false)
    private List<HashMap<String,Object>> orderInfoList;
    @TableField(exist = false)
    private List<ProYtProductFile> imageList;
    @TableField(exist = false)
    private List<ProYtProductSpecificationItem> itemList;
    @TableField(exist = false)
    private String purchaseRemark;
    @TableField(exist = false)
    private String purchaseItemRemark;
    @TableField(exist = false)
    private String locationName;

    @TableField(exist = false)
    private Long labelId;
    @TableField(exist = false)
    private String labelName;
    @TableField(exist = false)
    private String specificationDesc;

}