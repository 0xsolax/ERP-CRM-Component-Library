package com.qiaomoyun.entity.sal.sed;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qiaomoyun.entity.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * 报价单历史表(谁干了哪件事)实体类
 */
@Data
@TableName("sal_sed_quotation_history")
public class SalSedQuotationHistory extends BaseEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 报价单id
     */
    private Long quotationId;

    /**
     * 操作内容
     */
    private String context;

}
