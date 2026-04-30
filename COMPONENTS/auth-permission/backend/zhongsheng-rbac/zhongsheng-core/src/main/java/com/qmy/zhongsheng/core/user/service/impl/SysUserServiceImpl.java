package com.qmy.zhongsheng.core.user.service.impl;

import com.qmy.zhongsheng.common.constants.TenantConfigCodeConstants;
import com.qmy.zhongsheng.common.context.LoginUserInfoContext;
import com.qmy.zhongsheng.common.login.LoginUserInfo;
import com.qmy.zhongsheng.common.utils.ValidityUtils;
import com.qmy.zhongsheng.core.tenant.manager.TenantConfigManager;
import com.qmy.zhongsheng.core.tenant.model.entity.TenantConfigDO;
import com.qmy.zhongsheng.core.user.manager.RoleManager;
import com.qmy.zhongsheng.core.user.manager.UserManager;
import com.qmy.zhongsheng.core.user.manager.UserRoleManager;
import com.qmy.zhongsheng.core.user.model.entity.RoleDO;
import com.qmy.zhongsheng.core.user.model.entity.UserDO;
import com.qmy.zhongsheng.core.user.model.entity.UserRoleDO;
import com.qmy.zhongsheng.core.user.model.vo.SysUserInfoVO;
import com.qmy.zhongsheng.core.user.model.vo.SysUserPermissionVO;
import com.qmy.zhongsheng.core.user.model.vo.SysUserVO;
import com.qmy.zhongsheng.core.user.service.SysUserService;
import com.qmy.zhongsheng.core.user.service.UserPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author AI Coding
 * @description SysUserServiceImpl
 * @date 2026/03/20 16:12
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {

    private final UserManager userManager;

    private final TenantConfigManager tenantConfigManager;

    private final UserRoleManager userRoleManager;

    private final RoleManager roleManager;

    private final UserPermissionService userPermissionService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public SysUserInfoVO getCurrentUserInfo() {
        LoginUserInfo loginUserInfo = LoginUserInfoContext.requireLoginUserInfo();
        UserDO userDO = userManager.getById(loginUserInfo.getUserId());
        Integer tenantId = resolveTenantId();

        List<UserRoleDO> userRoles = userRoleManager.listByUserId(userDO.getId());
        Set<Long> roleIdList = userRoles.stream().map(UserRoleDO::getRoleId).filter(Objects::nonNull).collect(Collectors.toSet());
        List<RoleDO> roleRows = roleManager.listByIds(roleIdList);
        String roleNamesJoined = joinRoleNames(roleIdList, roleRows);

        SysUserInfoVO vo = new SysUserInfoVO();
        vo.setPermission(buildPermissionVO(userDO));
        vo.setUser(buildUserVO(userDO, loginUserInfo, tenantId, roleIdList, roleNamesJoined));
        return vo;
    }

    /**
     * 从租户配置解析 {@code tenantId}，解析失败为 {@code null}。
     */
    private Integer resolveTenantId() {
        List<TenantConfigDO> tenantConfigs = tenantConfigManager.listByCodes(List.of(TenantConfigCodeConstants.TENANT_ID));
        Map<String, String> configMap = tenantConfigs.stream()
                .filter(r -> r.getConfigCode() != null && r.getConfigValue() != null)
                .collect(Collectors.toMap(TenantConfigDO::getConfigCode, TenantConfigDO::getConfigValue, (a, b) -> b));
        String tenantIdStr = configMap.get(TenantConfigCodeConstants.TENANT_ID);
        if (tenantIdStr == null) {
            return null;
        }
        try {
            return Integer.parseInt(tenantIdStr);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String joinRoleNames(Set<Long> roleIdList, List<RoleDO> roleRows) {
        if (roleIdList.isEmpty()) {
            return null;
        }
        Map<Long, RoleDO> roleById = roleRows.stream()
                .collect(Collectors.toMap(RoleDO::getId, r -> r, (a, b) -> a));
        String joined = roleIdList.stream()
                .map(roleById::get)
                .filter(Objects::nonNull)
                .map(RoleDO::getName)
                .collect(Collectors.joining(","));
        return ValidityUtils.isBlank(joined) ? null : joined;
    }

    /**
     * 超管为通配；否则从启用角色关联的菜单中汇总权限标识（去重、排序）。
     */
    private SysUserPermissionVO buildPermissionVO(UserDO userDO) {
        SysUserPermissionVO permissionVO = new SysUserPermissionVO();
        List<String> perms = new ArrayList<>(userPermissionService.loadPermissionCodes(userDO));
        Collections.sort(perms);
        permissionVO.setCurPermissions(perms);
        return permissionVO;
    }

    private SysUserVO buildUserVO(UserDO userDO, LoginUserInfo loginUserInfo, Integer tenantId,
                                  Set<Long> roleIdList, String roleNamesJoined) {
        SysUserVO userVO = new SysUserVO();
        userVO.setUserId(String.valueOf(userDO.getId()));
        userVO.setToken(loginUserInfo.getToken());
        userVO.setUserName(userDO.getUserName());
        userVO.setNickName(userDO.getNickName());
        userVO.setTenantId(tenantId);
        userVO.setStatus(userDO.getStatus() != null ? String.valueOf(userDO.getStatus()) : null);
        userVO.setCode(null);
        userVO.setRoleIds(new ArrayList<>(roleIdList));
        userVO.setCreateTime(userDO.getCreateTime() != null ? userDO.getCreateTime().format(DATE_FORMATTER) : null);
        userVO.setRoleNames(roleNamesJoined);
        userVO.setFeiShuUserId(null);
        userVO.setDingTalkUserId(null);
        userVO.setDepartmentNames(null);
        userVO.setSysRoleList(Collections.emptyList());
        return userVO;
    }
}
