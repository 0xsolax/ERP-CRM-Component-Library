package com.qmy.project.core.auth.manager;

import com.qmy.project.core.user.model.entity.UserDO;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 09:49
 */
public interface UserLoginLogManager {

    void recordSuccess(UserDO userDO, String loginType, String platform, String tokenId);

    void recordFailure(String loginAccount, String loginType, String platform, String message);
}
