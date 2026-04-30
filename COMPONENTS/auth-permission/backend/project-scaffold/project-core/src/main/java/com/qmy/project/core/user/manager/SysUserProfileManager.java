package com.qmy.project.core.user.manager;

import com.qmy.project.core.user.model.vo.SysUserInfoVO;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 16:12
 */
public interface SysUserProfileManager {

    SysUserInfoVO getCurrentUserInfo(Long userId);
}
