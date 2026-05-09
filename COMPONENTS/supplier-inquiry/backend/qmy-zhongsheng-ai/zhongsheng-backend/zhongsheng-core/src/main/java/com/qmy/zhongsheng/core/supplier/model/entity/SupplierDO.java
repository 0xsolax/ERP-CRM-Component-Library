package com.qmy.zhongsheng.core.supplier.model.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qmy.zhongsheng.infrastructure.model.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 供应商主档表 {@code supplier}。
 *
 * @author AI Coding
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("supplier")
public class SupplierDO extends BaseDO {

    @TableField("code")
    private String code;

    @TableField("name")
    private String name;

    @TableField(value = "contact", updateStrategy = FieldStrategy.ALWAYS)
    private String contact;

    @TableField(value = "phone", updateStrategy = FieldStrategy.ALWAYS)
    private String phone;

    @TableField(value = "email", updateStrategy = FieldStrategy.ALWAYS)
    private String email;

    @TableField(value = "address", updateStrategy = FieldStrategy.ALWAYS)
    private String address;

    @TableField("status")
    private Integer status;

    @TableField(value = "remark", updateStrategy = FieldStrategy.ALWAYS)
    private String remark;
}
