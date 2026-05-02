/*
 * @author java_deng
 * @date 2024/12/15 10:00
 * @description 箱规管理实体类
 */
package com.qiaomoyun.entity.sto.yt;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qiaomoyun.entity.BaseEntity;

import jakarta.persistence.Table;
import lombok.Data;

/**
 * 箱规管理实体类
 */
@Data
@TableName("sto_yt_box")
public class StoYtBox extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String code;
    private Integer length;
    private Integer width;
    private Integer height;
    private Integer weight;
}
