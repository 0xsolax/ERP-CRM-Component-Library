package com.qmy.zhongsheng.core.user.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.api.dto.role.RoleListQueryDTO;
import com.qmy.zhongsheng.common.utils.ValidityUtils;
import com.qmy.zhongsheng.core.menu.manager.SystemMenuManager;
import com.qmy.zhongsheng.core.menu.model.entity.SystemMenuDO;
import com.qmy.zhongsheng.core.user.manager.RoleManager;
import com.qmy.zhongsheng.core.user.manager.RoleMenuManager;
import com.qmy.zhongsheng.core.user.model.condition.RoleQueryCondition;
import com.qmy.zhongsheng.core.user.model.entity.RoleDO;
import com.qmy.zhongsheng.core.user.model.entity.RoleMenuDO;
import com.qmy.zhongsheng.core.user.model.vo.RolePageVO;
import com.qmy.zhongsheng.core.user.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 角色查询实现。
 *
 * @author AI Coding
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleManager roleManager;

    private final RoleMenuManager roleMenuManager;

    private final SystemMenuManager systemMenuManager;

    @Override
    public Page<RolePageVO> page(RoleListQueryDTO query) {
        RoleQueryCondition condition = new RoleQueryCondition();
        condition.setLikeName(query.getLikeName());
        Page<RoleDO> doPage = roleManager.page(condition);
        List<RoleDO> rows = doPage.getRecords();
        if (rows.isEmpty()) {
            Page<RolePageVO> empty = new Page<>(doPage.getCurrent(), doPage.getSize(), doPage.getTotal());
            empty.setRecords(List.of());
            return empty;
        }
        List<Long> roleIds = rows.stream().map(RoleDO::getId).toList();
        List<RoleMenuDO> roleMenus = roleMenuManager.listByRoleIds(roleIds);
        Map<Long, List<Long>> roleIdToMenuIds = roleMenus.stream()
                .filter(rm -> rm.getRoleId() != null && rm.getMenuId() != null)
                .collect(Collectors.groupingBy(
                        RoleMenuDO::getRoleId,
                        Collectors.mapping(RoleMenuDO::getMenuId, Collectors.toList())));
        List<Long> allMenuIds = roleMenus.stream()
                .map(RoleMenuDO::getMenuId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        Map<Long, String> menuIdToPermission = allMenuIds.isEmpty()
                ? Map.of()
                : systemMenuManager.listByIds(allMenuIds).stream()
                .filter(m -> m.getStatus() != null && m.getStatus() == 0)
                .collect(Collectors.toMap(SystemMenuDO::getId, SystemMenuDO::getPermission, (a, b) -> a));

        List<RolePageVO> records = new ArrayList<>(rows.size());
        for (RoleDO r : rows) {
            RolePageVO vo = new RolePageVO();
            vo.setId(r.getId());
            vo.setName(r.getName());
            vo.setDesc(r.getDesc());
            vo.setEnabled(r.getEnabled());
            List<String> perms = roleIdToMenuIds.getOrDefault(r.getId(), List.of()).stream()
                    .map(menuIdToPermission::get)
                    .filter(ValidityUtils::isNotBlank)
                    .distinct()
                    .sorted(Comparator.naturalOrder())
                    .toList();
            vo.setPermissions(perms);
            records.add(vo);
        }
        Page<RolePageVO> out = new Page<>(doPage.getCurrent(), doPage.getSize(), doPage.getTotal());
        out.setRecords(records);
        return out;
    }
}
