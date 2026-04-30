package com.qmy.zhongsheng.core.user.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author 单漪甜
 * @description 用户权限信息
 * @date 2026/04/08 10:00
 */
@Data
@Schema(description = "用户权限信息")
public class SysUserPermissionVO {

    @Schema(description = "当前权限列表")
    private List<String> curPermissions;

}