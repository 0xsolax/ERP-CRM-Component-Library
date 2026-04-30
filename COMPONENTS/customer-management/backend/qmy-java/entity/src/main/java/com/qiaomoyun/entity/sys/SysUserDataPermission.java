/*
 * @author java_deng
 * @date 2025/10/27 22:45
 * @description
 */
package com.qiaomoyun.entity.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qiaomoyun.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("sys_user_data_permission")
public class SysUserDataPermission extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Long userId;
    private String permissions;
    @Schema($schema = "是否按组织架构，1全部数据，2按组织架构")
    private Integer isOrganizeData;
}
