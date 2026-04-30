package com.qmy.project.core.auth.manager;

import com.qmy.project.common.login.LoginUserInfo;
import com.qmy.project.common.enums.LoginTypeEnum;
import com.qmy.project.core.auth.model.vo.UserLoginVO;
import com.qmy.project.core.user.model.entity.UserDO;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 09:49
 */
public interface AuthManager {

    UserLoginVO createLoginResult(UserDO userDO, LoginTypeEnum loginType, String platform);

    LoginUserInfo resolveLoginUser(String accessToken);

    void logout(String tokenId);
}
