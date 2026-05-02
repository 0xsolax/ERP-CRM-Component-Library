/*
 * @author java_deng
 * @date 2024/11/21 16:30
 * @description 库存实体类
 */
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

import java.time.LocalDateTime;
import java.util.List;

/**
 * 库存实体类
 */
@Data
@TableName("sto_yt_store")
public class StoYtStore extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long productId;
    private Long specificationId; //产品规格id
    private Integer realStore; //实际库存
    private Integer enableStore; //可用库存
    private Integer occupyStore; //占用库存
    private Integer realTransit; //实际在途
    private Integer enableTransit; //可用在途
    private Integer occupyTransit; //占用在途
    private Integer warningNumber; //预警规则

    @TableField(exist = false)
    private List<ProYtProductFile> imageList;
    @TableField(exist = false)
    private List<ProYtProductSpecificationItem> itemList;
    @TableField(exist = false)
    private Long locationId;
    @TableField(exist = false)
    private String locationName;
    @TableField(exist = false)
    private String description;
}