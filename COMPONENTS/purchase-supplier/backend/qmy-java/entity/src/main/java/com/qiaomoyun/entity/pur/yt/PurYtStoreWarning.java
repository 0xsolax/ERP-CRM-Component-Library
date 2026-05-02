/*
 * @author java_deng
 * @date 2025/11/15 15:27
 * @description 库存预警实体类
 */
package com.qiaomoyun.entity.pur.yt;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qiaomoyun.entity.BaseEntity;
import com.qiaomoyun.entity.pro.yt.ProYtProductFile;
import com.qiaomoyun.entity.pro.yt.ProYtProductSpecificationItem;
import com.qiaomoyun.entity.pro.yt.ProYtProductSpecificationSupplier;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("pur_yt_store_warning")
public class PurYtStoreWarning extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long customerId;
    private Long productId;
    private Long specificationId;
    private LocalDateTime warningTime;
    private String warningReason;
    private String storeName;
    private Boolean isApplyPurchase;

    @TableField(exist = false)
    private String productCode;
    @TableField(exist = false)
    private Integer realStore; //实际库存
    @TableField(exist = false)
    private Integer enableStore; //可用库存
    @TableField(exist = false)
    private Integer occupyStore; //占用库存
    @TableField(exist = false)
    private Integer realTransit; //实际在途
    @TableField(exist = false)
    private Integer enableTransit; //可用在途
    @TableField(exist = false)
    private Integer occupyTransit; //占用在途

    @TableField(exist = false)
    private List<ProYtProductSpecificationItem> itemList;
    @TableField(exist = false)
    private List<ProYtProductFile> imageList;
    @TableField(exist = false)
    private List<ProYtProductSpecificationSupplier> supplierList;

    //提交申购参数
    @TableField(exist = false)
    private Long supplierId;
    @TableField(exist = false)
    private Integer applyPurchaseNumber;
}