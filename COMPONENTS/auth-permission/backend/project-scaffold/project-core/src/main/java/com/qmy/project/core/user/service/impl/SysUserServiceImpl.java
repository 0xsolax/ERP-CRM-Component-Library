package com.qmy.project.core.user.service.impl;

import com.qmy.project.common.login.LoginUserInfo;
import com.qmy.project.common.context.LoginUserInfoContext;
import com.qmy.project.core.user.manager.SysUserProfileManager;
import com.qmy.project.core.user.model.vo.SysUserInfoVO;
import com.qmy.project.core.user.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 16:12
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {

    private final SysUserProfileManager sysUserProfileManager;

    @Override
    public SysUserInfoVO getCurrentUserInfo() {
        LoginUserInfo loginUserInfo = LoginUserInfoContext.requireLoginUserInfo();
        return sysUserProfileManager.getCurrentUserInfo(loginUserInfo.getUserId());
    }
}
