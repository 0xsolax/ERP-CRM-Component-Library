package com.qmy.project.core.user.manager.impl;

import com.qmy.project.common.utils.BeanUtils;
import com.qmy.project.core.user.manager.SysUserProfileManager;
import com.qmy.project.core.user.manager.UserManager;
import com.qmy.project.core.user.model.entity.UserDO;
import com.qmy.project.core.user.model.vo.SysUserInfoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 16:12
 */
@Component
@RequiredArgsConstructor
public class SysUserProfileManagerImpl implements SysUserProfileManager {

    private final UserManager userManager;

    @Override
    public SysUserInfoVO getCurrentUserInfo(Long userId) {
        UserDO userDO = userManager.getById(userId);
        SysUserInfoVO userInfoVO = BeanUtils.toBean(userDO, SysUserInfoVO.class);
        userInfoVO.setUserId(userDO.getId());
        userInfoVO.setAvatarUrl(userManager.resolveAvatarUrl(userDO));
        return userInfoVO;
    }
}
