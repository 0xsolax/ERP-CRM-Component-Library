/*
 * @author java_deng
 * @date 2025/11/3 14:41
 * @description
 */
package com.qiaomoyun.entity.pro.yt;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qiaomoyun.entity.BaseEntity;
import lombok.Data;

@TableName("pro_yt_product_label")
@Data
public class ProYtProductLabel extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Long masterId;
    private String value;
    private String type;

    @TableField(exist = false)
    private String oldValue;
}
