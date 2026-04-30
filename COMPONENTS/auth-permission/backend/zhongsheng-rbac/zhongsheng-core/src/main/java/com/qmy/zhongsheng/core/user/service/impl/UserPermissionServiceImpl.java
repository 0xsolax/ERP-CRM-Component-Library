package com.qmy.zhongsheng.core.user.service.impl;

import com.qmy.zhongsheng.common.utils.ValidityUtils;
import com.qmy.zhongsheng.core.menu.manager.SystemMenuManager;
import com.qmy.zhongsheng.core.menu.model.entity.SystemMenuDO;
import com.qmy.zhongsheng.core.user.manager.RoleManager;
import com.qmy.zhongsheng.core.user.manager.RoleMenuManager;
import com.qmy.zhongsheng.core.user.manager.UserManager;
import com.qmy.zhongsheng.core.user.manager.UserRoleManager;
import com.qmy.zhongsheng.core.user.model.entity.RoleDO;
import com.qmy.zhongsheng.core.user.model.entity.RoleMenuDO;
import com.qmy.zhongsheng.core.user.model.entity.UserDO;
import com.qmy.zhongsheng.core.user.model.entity.UserRoleDO;
import com.qmy.zhongsheng.core.user.service.UserPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 从用户-角色-菜单汇总权限标识，与 {@link com.qmy.zhongsheng.core.user.service.impl.SysUserServiceImpl} 中 curPermissions 语义一致。
 *
 * @author AI Coding
 */
@Service
@RequiredArgsConstructor
public class UserPermissionServiceImpl implements UserPermissionService {

    private static final String ALL_PERMISSION = "*";

    private final UserManager userManager;

    private final UserRoleManager userRoleManager;

    private final RoleManager roleManager;

    private final RoleMenuManager roleMenuManager;

    private final SystemMenuManager systemMenuManager;

    @Override
    public Set<String> loadPermissionCodes(Long userId) {
        if (userId == null) {
            return Collections.emptySet();
        }
        UserDO userDO = userManager.getById(userId);
        if (userDO == null) {
            return Collections.emptySet();
        }
        return loadPermissionCodes(userDO);
    }

    @Override
    public Set<String> loadPermissionCodes(UserDO userDO) {
        if (Boolean.TRUE.equals(userDO.getAdminFlag())) {
            return Set.of(ALL_PERMISSION);
        }
        List<UserRoleDO> userRoles = userRoleManager.listByUserId(userDO.getId());
        List<Long> roleIdList = userRoles.stream()
                .map(UserRoleDO::getRoleId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (roleIdList.isEmpty()) {
            return Collections.emptySet();
        }
        List<RoleDO> roleRows = roleManager.listByIds(roleIdList);
        List<Long> enabledRoleIds = roleRows.stream()
                .filter(r -> r.getEnabled() != null && r.getEnabled() == 1)
                .map(RoleDO::getId)
                .toList();
        if (enabledRoleIds.isEmpty()) {
            return Collections.emptySet();
        }
        List<RoleMenuDO> roleMenus = roleMenuManager.listByRoleIds(enabledRoleIds);
        List<Long> menuIds = roleMenus.stream()
                .map(RoleMenuDO::getMenuId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (menuIds.isEmpty()) {
            return Collections.emptySet();
        }
        List<SystemMenuDO> menus = systemMenuManager.listByIds(menuIds);
        return menus.stream()
                .filter(m -> m.getStatus() != null && m.getStatus() == 0)
                .map(SystemMenuDO::getPermission)
                .filter(ValidityUtils::isNotBlank)
                .collect(Collectors.toCollection(HashSet::new));
    }
}
