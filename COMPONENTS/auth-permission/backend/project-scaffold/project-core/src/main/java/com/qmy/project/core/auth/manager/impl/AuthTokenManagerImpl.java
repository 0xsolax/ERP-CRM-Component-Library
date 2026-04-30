package com.qmy.project.core.auth.manager.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.qmy.project.common.utils.BeanUtils;
import com.qmy.project.core.auth.dao.AuthTokenDAO;
import com.qmy.project.core.auth.manager.AuthTokenStateManager;
import com.qmy.project.core.auth.model.AuthTokenSession;
import com.qmy.project.core.auth.model.entity.AuthTokenDO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author AI Coding
 * @description {@link AuthTokenStateManager} 的 MySQL 实现（表 {@code auth_token}）。
 * @date 2026/03/31 09:49
 */
@Component
@RequiredArgsConstructor
public class AuthTokenManagerImpl implements AuthTokenStateManager {

    private final AuthTokenDAO authTokenDAO;

    @Override
    public void save(AuthTokenSession session) {
        authTokenDAO.insert(BeanUtils.toBean(session, AuthTokenDO.class));
    }

    @Override
    public AuthTokenSession get(String tokenId) {
        AuthTokenDO tokenDO = authTokenDAO.selectOne(Wrappers.<AuthTokenDO>lambdaQuery()
                .eq(AuthTokenDO::getTokenId, tokenId)
                .last("limit 1"));
        if (tokenDO == null) {
            return null;
        }
        if (tokenDO.getExpireTime() != null && tokenDO.getExpireTime().isBefore(LocalDateTime.now())) {
            authTokenDAO.update(null, Wrappers.<AuthTokenDO>lambdaUpdate()
                    .eq(AuthTokenDO::getId, tokenDO.getId())
                    .set(AuthTokenDO::getStatus, AuthTokenSession.STATUS_EXPIRED));
            return null;
        }
        if (!AuthTokenSession.STATUS_ACTIVE.equals(tokenDO.getStatus())) {
            return null;
        }
        return BeanUtils.toBean(tokenDO, AuthTokenSession.class);
    }

    @Override
    public void revoke(String tokenId) {
        authTokenDAO.update(null, Wrappers.<AuthTokenDO>lambdaUpdate()
                .eq(AuthTokenDO::getTokenId, tokenId)
                .set(AuthTokenDO::getStatus, AuthTokenSession.STATUS_REVOKED)
                .set(AuthTokenDO::getLastVerifyTime, LocalDateTime.now()));
    }
}
