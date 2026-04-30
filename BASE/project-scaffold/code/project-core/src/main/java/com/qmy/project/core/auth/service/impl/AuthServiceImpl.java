package com.qmy.project.core.auth.service.impl;

import com.qmy.project.common.exception.ServiceException;
import com.qmy.project.common.login.LoginUserInfo;
import com.qmy.project.common.context.LoginUserInfoContext;
import com.qmy.project.common.enums.LoginTypeEnum;
import com.qmy.project.common.enums.ScanLoginTypeEnum;
import com.qmy.project.core.auth.manager.AuthManager;
import com.qmy.project.core.auth.manager.UserLoginLogManager;
import com.qmy.project.core.user.model.vo.ThirdPartyUserIdentityVO;
import com.qmy.project.api.dto.PasswordLoginDTO;
import com.qmy.project.api.dto.ScanLoginDTO;
import com.qmy.project.core.auth.model.vo.UserLoginVO;
import com.qmy.project.core.auth.service.strategy.ScanLoginStrategy;
import com.qmy.project.core.auth.service.strategy.ScanLoginStrategyContext;
import com.qmy.project.core.auth.service.AuthService;
import com.qmy.project.core.user.manager.UserBindManager;
import com.qmy.project.core.user.manager.UserManager;
import com.qmy.project.core.user.model.entity.UserDO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 09:49
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserManager userManager;

    private final UserBindManager userBindManager;

    private final UserLoginLogManager userLoginLogManager;

    private final AuthManager authManager;

    private final ScanLoginStrategyContext scanLoginStrategyContext;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserLoginVO loginByPassword(PasswordLoginDTO loginDTO) {
        try {
            UserDO userDO = userManager.authenticateByUserName(loginDTO.getUserName(), loginDTO.getPassword());
            return authManager.createLoginResult(userDO, LoginTypeEnum.PASSWORD, null);
        } catch (ServiceException exception) {
            userLoginLogManager.recordFailure(loginDTO.getUserName(), LoginTypeEnum.PASSWORD.getCode(), null, exception.getMessage());
            throw exception;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserLoginVO loginByScan(ScanLoginDTO param, String referer) {
        ScanLoginTypeEnum loginType = null;
        try {
            loginType = ScanLoginTypeEnum.getByCode(param.getType());
            ScanLoginStrategy strategy = scanLoginStrategyContext.getStrategy(loginType);
            ThirdPartyUserIdentityVO identity = strategy.authenticate(param.getCode(), referer);
            UserDO userDO = userBindManager.getBoundUserByIdentity(identity);
            userManager.validateLoginable(userDO);
            return authManager.createLoginResult(userDO, loginType.getLoginType(), loginType.getCode());
        } catch (ServiceException exception) {
            userLoginLogManager.recordFailure(param.getType(), loginType == null ? null : loginType.getLoginType().getCode(), param.getType(), exception.getMessage());
            throw exception;
        }
    }

    @Override
    public void logout() {
        LoginUserInfo loginUserInfo = LoginUserInfoContext.requireLoginUserInfo();
        if ("SUPER_TOKEN".equals(loginUserInfo.getTokenId())) {
            return;
        }
        authManager.logout(loginUserInfo.getTokenId());
    }
}
