package com.qiaomoyun.entity.pur.yt;

import com.baomidou.mybatisplus.annotation.*;
import com.qiaomoyun.entity.BaseEntity;

import com.qiaomoyun.entity.pro.yt.ProYtProductFile;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 供应商跟进实体类
 * 对应表名：pur_yt_supplier_follow
 */
@Data
@TableName("pur_yt_supplier_follow")
public class PurYtSupplierFollow extends BaseEntity{

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 供应商ID
     */
    private Long supplierId;

    /**
     * 主题
     */
    private String theme;

    /**
     * 联系人
     */
    private String contactPerson;

    /**
     * 行动描述
     */
    private String description;

    /**
     * 下次回访日期
     */
    private LocalDateTime nextVisitDate;
    @TableField(exist = false)
    private List<ProYtProductFile> fileList;


}
