package com.qmy.project.core.auth.manager.impl;

import com.qmy.project.common.context.LoginUserInfoContext;
import com.qmy.project.core.auth.dao.UserLoginLogDAO;
import com.qmy.project.core.auth.manager.UserLoginLogManager;
import com.qmy.project.core.auth.model.entity.UserLoginLogDO;
import com.qmy.project.core.user.model.entity.UserDO;
import com.qmy.project.core.auth.support.RequestClientInfoSupport;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 09:49
 */
@Component
@RequiredArgsConstructor
public class UserLoginLogManagerImpl implements UserLoginLogManager {

    private final UserLoginLogDAO userLoginLogDAO;

    @Override
    public void recordSuccess(UserDO userDO, String loginType, String platform, String tokenId) {
        RequestClientInfoSupport.ClientInfo clientInfo = RequestClientInfoSupport.current();
        UserLoginLogDO loginLogDO = new UserLoginLogDO();
        loginLogDO.setUserId(userDO.getId());
        loginLogDO.setLoginAccount(userDO.getUserName());
        loginLogDO.setLoginType(loginType);
        loginLogDO.setPlatform(platform);
        loginLogDO.setLoginStatus(0);
        loginLogDO.setTokenId(tokenId);
        loginLogDO.setClientIp(clientInfo.getClientIp());
        loginLogDO.setUserAgent(clientInfo.getUserAgent());
        loginLogDO.setTraceId(MDC.get("traceId"));
        loginLogDO.setMessage("登录成功");
        loginLogDO.setLoginTime(LocalDateTime.now());
        long auditUid = userDO.getId() != null ? userDO.getId() : LoginUserInfoContext.currentUserIdOrDefault(-1L);
        loginLogDO.setCreateUser(auditUid);
        loginLogDO.setUpdateUser(auditUid);
        userLoginLogDAO.insert(loginLogDO);
    }

    @Override
    public void recordFailure(String loginAccount, String loginType, String platform, String message) {
        RequestClientInfoSupport.ClientInfo clientInfo = RequestClientInfoSupport.current();
        UserLoginLogDO loginLogDO = new UserLoginLogDO();
        loginLogDO.setLoginAccount(loginAccount);
        loginLogDO.setLoginType(loginType);
        loginLogDO.setPlatform(platform);
        loginLogDO.setLoginStatus(1);
        loginLogDO.setClientIp(clientInfo.getClientIp());
        loginLogDO.setUserAgent(clientInfo.getUserAgent());
        loginLogDO.setTraceId(MDC.get("traceId"));
        loginLogDO.setMessage(message);
        loginLogDO.setLoginTime(LocalDateTime.now());
        long auditUid = LoginUserInfoContext.currentUserIdOrDefault(-1L);
        loginLogDO.setCreateUser(auditUid);
        loginLogDO.setUpdateUser(auditUid);
        userLoginLogDAO.insert(loginLogDO);
    }
}
