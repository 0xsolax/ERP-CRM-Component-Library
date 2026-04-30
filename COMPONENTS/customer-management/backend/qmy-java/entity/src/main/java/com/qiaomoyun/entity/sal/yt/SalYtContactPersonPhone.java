package com.qiaomoyun.entity.sal.yt;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qiaomoyun.entity.BaseEntity;

import lombok.Data;
import java.util.Date;

/**
 * 联系人电话实体类
 * 对应表 sal_yt_contact_person_phone
 */
@Data
@TableName("sal_yt_contact_person_phone")
public class SalYtContactPersonPhone extends BaseEntity{
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 联系人ID
     */
    private Long contactId;
    
    /**
     * 区号
     */
    private String areaCode;
    
    /**
     * 联系方式
     */
    private String phone;
}