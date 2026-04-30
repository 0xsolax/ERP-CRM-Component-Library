package com.qmy.zhongsheng.core.user.manager.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.qmy.zhongsheng.common.utils.ValidityUtils;
import com.qmy.zhongsheng.core.user.dao.RoleMenuDAO;
import com.qmy.zhongsheng.core.user.manager.RoleMenuManager;
import com.qmy.zhongsheng.core.user.model.entity.RoleMenuDO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * @author AI Coding
 */
@Component
@RequiredArgsConstructor
public class RoleMenuManagerImpl implements RoleMenuManager {

    private final RoleMenuDAO roleMenuDAO;

    @Override
    public List<RoleMenuDO> listByRoleIds(Collection<Long> roleIds) {
        if (roleIds == null || ValidityUtils.isEmpty(roleIds)) {
            return List.of();
        }
        return roleMenuDAO.selectList(Wrappers.<RoleMenuDO>lambdaQuery()
                .in(RoleMenuDO::getRoleId, roleIds)
                .eq(RoleMenuDO::getIsDeleted, 0)
                .orderByAsc(RoleMenuDO::getId));
    }
}
