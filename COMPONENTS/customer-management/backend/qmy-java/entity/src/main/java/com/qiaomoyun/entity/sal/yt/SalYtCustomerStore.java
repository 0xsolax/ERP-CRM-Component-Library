/*
 * @author java_deng
 * @date 2024/11/21 16:30
 * @description 客户独立仓实体类
 */
package com.qiaomoyun.entity.sal.yt;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qiaomoyun.entity.BaseEntity;
import com.qiaomoyun.entity.pro.yt.ProYtCategorySpecificationItem;
import com.qiaomoyun.entity.pro.yt.ProYtProductFile;
import com.qiaomoyun.entity.pro.yt.ProYtProductSpecificationItem;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 客户独立仓实体类
 */
@Data
@TableName("sal_yt_customer_store")
public class SalYtCustomerStore extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long customerId;
    private Long specificationId; //产品规格id
    private Long productId; //产品id
    private Integer storeNumber; //实际库存
    private Integer transitNumber; //在途库存
    private Long locationId; //库位id
    private String status;
    private Integer warningNumber; //预警规则

    @TableField(exist = false)
    private String locationName;
    @TableField(exist = false)
    private List<ProYtProductSpecificationItem> itemList;
    @TableField(exist = false)
    private String productCode;
    @TableField(exist = false)
    private List<ProYtProductFile> imageList;
}