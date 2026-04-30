package com.qmy.zhongsheng.core.user.manager;

import com.qmy.zhongsheng.core.user.model.entity.RoleMenuDO;

import java.util.Collection;
import java.util.List;

/**
 * 角色与菜单关联。
 *
 * @author AI Coding
 */
public interface RoleMenuManager {

    /**
     * 查询多个角色下未逻辑删除的菜单关联，用于汇总权限码。
     *
     * @param roleIds 角色 id 集合，为 null 或空时返回空列表
     * @return 关联行列表
     */
    List<RoleMenuDO> listByRoleIds(Collection<Long> roleIds);
}
