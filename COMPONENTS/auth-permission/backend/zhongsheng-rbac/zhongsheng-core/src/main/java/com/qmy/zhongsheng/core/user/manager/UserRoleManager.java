package com.qmy.zhongsheng.core.user.manager;

import com.qmy.zhongsheng.core.user.model.entity.UserRoleDO;

import java.util.List;

/**
 * 用户与角色关联。
 *
 * @author AI Coding
 */
public interface UserRoleManager {

    /**
     * 查询某用户下未逻辑删除的关联行。
     *
     * @param userId 用户 id
     * @return 关联列表，无则空列表
     */
    List<UserRoleDO> listByUserId(Long userId);
}
