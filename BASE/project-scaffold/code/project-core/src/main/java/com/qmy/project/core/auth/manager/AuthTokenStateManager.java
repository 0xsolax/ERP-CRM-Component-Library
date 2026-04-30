package com.qmy.project.core.auth.manager;

import com.qmy.project.core.auth.model.AuthTokenSession;

/**
 * Token 会话持久化（当前实现为 MySQL {@code auth_token} 表，见 {@link com.qmy.project.core.auth.manager.impl.AuthTokenManagerImpl}）。
 */
public interface AuthTokenStateManager {

    void save(AuthTokenSession session);

    AuthTokenSession get(String tokenId);

    void revoke(String tokenId);
}
