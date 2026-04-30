/*
 * @author java_deng
 * @date 2024/11/20 17:10
 * @description 联系人社交账号实体类
 */
package com.qiaomoyun.entity.sal.yt;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qiaomoyun.entity.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 联系人社交账号表实体类
 */
@Data
@TableName("sal_yt_contact_person_social")
public class SalYtContactPersonSocial extends BaseEntity {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 联系人ID
     */
    @NotNull(message = "联系人id不能为空")
    private Long contactId;

    /**
     * 社交平台
     */
    @NotBlank(message = "请选择社交平台")
    private String socialPlatform;

    /**
     * 社交账号值
     */
    @NotBlank(message = "请输入社交账号")
    private String value;
}