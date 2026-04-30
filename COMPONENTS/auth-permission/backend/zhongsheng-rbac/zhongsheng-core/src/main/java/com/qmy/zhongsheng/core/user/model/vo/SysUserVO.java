package com.qmy.zhongsheng.core.user.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author 单漪甜
 * @description 用户信息详情
 * @date 2026/04/08 10:00
 */
@Data
@Schema(description = "用户信息详情")
public class SysUserVO {

    @Schema(description = "用户 ID")
    private String userId;

    @Schema(description = "令牌")
    private String token;

    @Schema(description = "登录用户名")
    private String userName;

    @Schema(description = "昵称")
    private String nickName;

    @Schema(description = "租户 ID")
    private Integer tenantId;

    @Schema(description = "账号状态")
    private String status;

    @Schema(description = "用户编码")
    private String code;

    @Schema(description = "角色 ID 列表")
    private List<Long> roleIds;

    @Schema(description = "创建时间")
    private String createTime;

    @Schema(description = "角色名称列表")
    private String roleNames;

    @Schema(description = "飞书用户 ID")
    private String feiShuUserId;

    @Schema(description = "钉钉用户 ID")
    private String dingTalkUserId;

    @Schema(description = "部门名称列表")
    private String departmentNames;

    @Schema(description = "系统角色列表")
    private List<Object> sysRoleList;

}