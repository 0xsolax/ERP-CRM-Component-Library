/*
 * @author java_deng
 * @date 2024/11/20 16:40
 * @description 客户联系人实体类
 */
package com.qiaomoyun.entity.sal.yt;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qiaomoyun.entity.BaseEntity;
import com.qiaomoyun.entity.pro.yt.ProYtProductFile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import lombok.Data;

import java.util.List;

/**
 * 客户联系人表实体类
 */
@Data
@TableName("sal_yt_contact_person")
public class SalYtContactPerson extends BaseEntity {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 客户ID
     */
    private Long customerId;
    
    /**
     * 联系人名
     */
    @NotBlank(message = "请输入联系人姓名")
    private String name;
    
    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String email;
    
    /**
     * 职位
     */
    private String position;
    
    /**
     * 生日
     */
    private String birthday;
    
    /**
     * 性别
     */
    private String gender;
    
    /**
     * 备注
     */
    private String remark;

    private Long supplierId;

    @TableField(exist = false)
    private List<ProYtProductFile> fileList;

    @TableField(exist = false)
    private List<SalYtContactPersonSocial> socialList;

    @TableField(exist = false)
    private List<SalYtContactPersonPhone> phoneList;
}