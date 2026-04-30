package com.qmy.zhongsheng.core.user.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author 单漪甜
 * @description 用户信息返回包装对象
 * @date 2026/04/08 10:00
 */
@Data
@Schema(description = "用户信息返回包装对象")
public class SysUserInfoVO {

    @Schema(description = "权限信息")
    private SysUserPermissionVO permission;

    @Schema(description = "用户信息")
    private SysUserVO user;

}