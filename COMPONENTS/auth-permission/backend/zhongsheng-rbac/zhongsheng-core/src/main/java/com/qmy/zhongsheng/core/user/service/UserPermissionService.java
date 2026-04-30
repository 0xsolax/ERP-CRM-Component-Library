package com.qmy.zhongsheng.core.user.service;

import com.qmy.zhongsheng.core.user.model.entity.UserDO;

import java.util.Set;

/**
 * 按用户汇总接口权限标识（与前端路由、{@code system_menu.permission} 一致）。
 *
 * @author AI Coding
 */
public interface UserPermissionService {

    /**
     * 根据用户主键加载权限码集合；超管返回仅含 {@code *} 的集合。
     *
     * @param userId 用户 id
     * @return 权限标识，非 null（无权限时为空集）
     */
    Set<String> loadPermissionCodes(Long userId);

    /**
     * 根据已加载用户加载权限码，避免重复查用户表。
     *
     * @param userDO 用户 DO
     * @return 权限标识，非 null
     */
    Set<String> loadPermissionCodes(UserDO userDO);
}
