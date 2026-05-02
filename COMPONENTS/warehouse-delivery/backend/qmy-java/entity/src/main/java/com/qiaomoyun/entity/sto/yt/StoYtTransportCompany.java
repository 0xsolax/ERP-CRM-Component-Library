package com.qiaomoyun.entity.sto.yt;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qiaomoyun.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 物流公司实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sto_yt_transport_company")
public class StoYtTransportCompany extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 物流公司编号
     */
    private String code;

    /**
     * 物流公司名称
     */
    private String name;

    /**
     * 物流公司类型
     */
    private String type;

    /**
     * 物流公司地址
     */
    private String address;

    /**
     * 是否提供上门服务
     */
    private Integer isHomeService;
}
