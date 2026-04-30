package com.qmy.zhongsheng.core.user.service;

import com.qmy.zhongsheng.core.user.model.vo.SysUserInfoVO;

/**
 * @author AI Coding
 * @description SysUserService
 * @date 2026/03/20 16:12
 */
public interface SysUserService {

    /**
     * 获取当前登录用户资料：租户、用户基本信息、角色及菜单权限码。
     *
     * @return 用户信息（含 {@code permission.curPermissions}）
     */
    SysUserInfoVO getCurrentUserInfo();
}
