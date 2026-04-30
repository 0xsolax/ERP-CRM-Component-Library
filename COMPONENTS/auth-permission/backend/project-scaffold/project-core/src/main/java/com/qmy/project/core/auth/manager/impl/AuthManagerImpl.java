package com.qmy.project.core.auth.manager.impl;

import com.qmy.project.core.auth.model.AuthTokenSession;
import com.qmy.project.core.auth.model.JwtTokenClaims;
import com.qmy.project.common.enums.LoginTypeEnum;
import com.qmy.project.common.constants.AuthErrorCodeConstants;
import com.qmy.project.common.exception.ServiceExceptionUtil;
import com.qmy.project.common.login.LoginUserInfo;
import com.qmy.project.common.utils.BeanUtils;
import com.qmy.project.core.auth.manager.AuthManager;
import com.qmy.project.core.auth.manager.AuthTokenStateManager;
import com.qmy.project.core.auth.manager.UserLoginLogManager;
import com.qmy.project.core.auth.model.vo.UserLoginVO;
import com.qmy.project.core.auth.support.JwtTokenService;
import com.qmy.project.core.auth.support.RequestClientInfoSupport;
import com.qmy.project.core.user.manager.UserManager;
import com.qmy.project.core.user.model.entity.UserDO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 09:49
 */
@Component
@RequiredArgsConstructor
public class AuthManagerImpl implements AuthManager {

    private final JwtTokenService jwtTokenService;

    private final AuthTokenStateManager authTokenStateManager;

    private final UserManager userManager;

    private final UserLoginLogManager userLoginLogManager;

    @Override
    public UserLoginVO createLoginResult(UserDO userDO, LoginTypeEnum loginType, String platform) {
        String tokenId = UUID.randomUUID().toString().replace("-", "");
        String accessToken = jwtTokenService.createAccessToken(userDO.getId(), tokenId, loginType.getCode(), platform);
        JwtTokenClaims tokenClaims = jwtTokenService.parseAndValidate(accessToken);

        RequestClientInfoSupport.ClientInfo clientInfo = RequestClientInfoSupport.current();
        AuthTokenSession tokenSession = new AuthTokenSession();
        tokenSession.setTokenId(tokenId);
        tokenSession.setUserId(userDO.getId());
        tokenSession.setToken(accessToken);
        tokenSession.setLoginType(loginType.getCode());
        tokenSession.setPlatform(platform);
        tokenSession.setStatus(AuthTokenSession.STATUS_ACTIVE);
        tokenSession.setClientIp(clientInfo.getClientIp());
        tokenSession.setUserAgent(clientInfo.getUserAgent());
        tokenSession.setLastVerifyTime(LocalDateTime.now());
        tokenSession.setExpireTime(LocalDateTime.ofInstant(Instant.ofEpochSecond(tokenClaims.getExpiresAt()), ZoneId.systemDefault()));
        authTokenStateManager.save(tokenSession);

        userLoginLogManager.recordSuccess(userDO, loginType.getCode(), platform, tokenId);

        UserLoginVO loginVO = new UserLoginVO();
        loginVO.setUserId(userDO.getId());
        loginVO.setUsername(userDO.getUserName());
        loginVO.setNickName(userDO.getNickName());
        loginVO.setToken(accessToken);
        return loginVO;
    }

    @Override
    public LoginUserInfo resolveLoginUser(String accessToken) {
        JwtTokenClaims claims = jwtTokenService.parseAndValidate(accessToken);
        AuthTokenSession tokenSession = authTokenStateManager.get(claims.getTokenId());
        if (tokenSession == null) {
            throw ServiceExceptionUtil.exception(AuthErrorCodeConstants.TOKEN_INVALID);
        }
        if (!tokenSession.getUserId().equals(claims.getUserId()) || !accessToken.equals(tokenSession.getToken())) {
            throw ServiceExceptionUtil.exception(AuthErrorCodeConstants.TOKEN_INVALID);
        }

        UserDO userDO = userManager.getById(claims.getUserId());
        userManager.validateLoginable(userDO);

        LoginUserInfo loginUserInfo = BeanUtils.toBean(userDO, LoginUserInfo.class);
        loginUserInfo.setUserId(userDO.getId());
        loginUserInfo.setAvatarUrl(userManager.resolveAvatarUrl(userDO));
        loginUserInfo.setTokenId(claims.getTokenId());
        loginUserInfo.setLoginType(claims.getLoginType());
        loginUserInfo.setPlatform(claims.getPlatform());
        return loginUserInfo;
    }

    @Override
    public void logout(String tokenId) {
        authTokenStateManager.revoke(tokenId);
    }
}
