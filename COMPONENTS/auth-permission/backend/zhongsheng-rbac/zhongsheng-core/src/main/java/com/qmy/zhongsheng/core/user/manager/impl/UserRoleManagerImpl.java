package com.qmy.zhongsheng.core.user.manager.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.qmy.zhongsheng.core.user.dao.UserRoleDAO;
import com.qmy.zhongsheng.core.user.manager.UserRoleManager;
import com.qmy.zhongsheng.core.user.model.entity.UserRoleDO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author AI Coding
 */
@Component
@RequiredArgsConstructor
public class UserRoleManagerImpl implements UserRoleManager {

    private final UserRoleDAO userRoleDAO;

    @Override
    public List<UserRoleDO> listByUserId(Long userId) {
        if (userId == null) {
            return List.of();
        }
        return userRoleDAO.selectList(Wrappers.<UserRoleDO>lambdaQuery()
                .eq(UserRoleDO::getUserId, userId)
                .eq(UserRoleDO::getIsDeleted, 0)
                .orderByAsc(UserRoleDO::getId));
    }
}
