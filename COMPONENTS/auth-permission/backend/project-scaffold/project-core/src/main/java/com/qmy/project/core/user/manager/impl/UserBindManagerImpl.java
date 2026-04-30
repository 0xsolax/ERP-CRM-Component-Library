package com.qmy.project.core.user.manager.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.qmy.project.common.exception.ServiceExceptionUtil;
import com.qmy.project.common.utils.ValidityUtils;
import com.qmy.project.common.constants.AuthErrorCodeConstants;
import com.qmy.project.core.user.dao.UserBindDAO;
import com.qmy.project.core.user.manager.UserBindManager;
import com.qmy.project.core.user.manager.UserManager;
import com.qmy.project.core.user.model.entity.UserBindDO;
import com.qmy.project.core.user.model.entity.UserDO;
import com.qmy.project.core.user.model.vo.ThirdPartyUserIdentityVO;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 登录始终以 {@link UserDO} 为会话主体；{@code user_bind} 仅解析第三方身份到 {@code user_id}。
 * 同一用户允许几条绑定由数据库唯一键约束，应用层不做「每用户一条」预校验。
 *
 * @author AI Coding
 * @description
 * @date 2026/03/20 09:49
 */
@Component
@RequiredArgsConstructor
public class UserBindManagerImpl implements UserBindManager {

    private final UserBindDAO userBindDAO;

    private final UserManager userManager;

    @Override
    public List<UserBindDO> listByUserId(Long userId) {
        if (userId == null) {
            return List.of();
        }
        return userBindDAO.selectList(Wrappers.<UserBindDO>lambdaQuery()
                .eq(UserBindDO::getUserId, userId)
                .orderByDesc(UserBindDO::getUpdateTime)
                .orderByDesc(UserBindDO::getId));
    }

    @Override
    public Long save(UserBindDO bind) {
        try {
            userBindDAO.insert(bind);
            return bind.getId();
        } catch (Exception e) {
            if (isUniqueConstraintViolation(e)) {
                throw ServiceExceptionUtil.exception(AuthErrorCodeConstants.USER_BIND_UNIQUE_VIOLATION);
            }
            if (e instanceof RuntimeException re) {
                throw re;
            }
            throw new IllegalStateException(e);
        }
    }

    private static boolean isUniqueConstraintViolation(Throwable e) {
        for (Throwable t = e; t != null; t = t.getCause()) {
            if (t instanceof DuplicateKeyException || t instanceof SQLIntegrityConstraintViolationException) {
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDO getBoundUserByIdentity(ThirdPartyUserIdentityVO identity) {
        UserBindDO bindDO = findByIdentity(identity);
        if (bindDO == null) {
            throw ServiceExceptionUtil.exception(AuthErrorCodeConstants.ACCOUNT_NOT_EXISTS);
        }
        touchBinding(bindDO, identity);
        UserDO userDO = userManager.getById(bindDO.getUserId());
        userManager.syncProfile(userDO, identity);
        return userManager.getById(bindDO.getUserId());
    }

    @Override
    public List<String> listBoundPlatforms(Long userId) {
        Set<String> platforms = new LinkedHashSet<>();
        for (UserBindDO row : listByUserId(userId)) {
            if (row.getPlatform() != null && !row.getPlatform().isBlank()) {
                platforms.add(row.getPlatform());
            }
        }
        return new ArrayList<>(platforms);
    }

    @Override
    public UserBindDO findByPlatformAndThirdUserId(String platform, String thirdUserId) {
        if (platform == null || platform.isBlank() || thirdUserId == null || thirdUserId.isBlank()) {
            return null;
        }
        return userBindDAO.selectOne(Wrappers.<UserBindDO>lambdaQuery()
                .eq(UserBindDO::getPlatform, platform)
                .eq(UserBindDO::getThirdUserId, thirdUserId)
                .last("limit 1"));
    }

    private UserBindDO findByIdentity(ThirdPartyUserIdentityVO identity) {
        if (identity.getUnionId() != null && !identity.getUnionId().isBlank()) {
            UserBindDO bindDO = userBindDAO.selectOne(Wrappers.<UserBindDO>lambdaQuery()
                    .eq(UserBindDO::getPlatform, identity.getPlatform())
                    .eq(UserBindDO::getThirdUserId, identity.getThirdUserId())
                    .last("limit 1"));
            if (bindDO != null) {
                return bindDO;
            }
        }
        if (identity.getOpenId() != null && !identity.getOpenId().isBlank()) {
            UserBindDO bindDO = userBindDAO.selectOne(Wrappers.<UserBindDO>lambdaQuery()
                    .eq(UserBindDO::getPlatform, identity.getPlatform())
                    .eq(UserBindDO::getOpenId, identity.getOpenId())
                    .last("limit 1"));
            if (bindDO != null) {
                return bindDO;
            }
        }
        if (identity.getThirdUserId() != null && !identity.getThirdUserId().isBlank()) {
            return findByPlatformAndThirdUserId(identity.getPlatform(), identity.getThirdUserId());
        }
        return null;
    }

    private void touchBinding(UserBindDO bindDO, ThirdPartyUserIdentityVO identity) {
        boolean fillUnionId = ValidityUtils.isNotBlank(identity.getUnionId())
                && (bindDO.getUnionId() == null || bindDO.getUnionId().isBlank());
        boolean fillOpenId = ValidityUtils.isNotBlank(identity.getOpenId())
                && (bindDO.getOpenId() == null || bindDO.getOpenId().isBlank());
        userBindDAO.update(null, Wrappers.<UserBindDO>lambdaUpdate()
                .eq(UserBindDO::getId, bindDO.getId())
                .set(UserBindDO::getLastAuthTime, LocalDateTime.now())
                .set(identity.getNickName() != null && !identity.getNickName().isBlank(), UserBindDO::getThirdNickname, identity.getNickName())
                .set(fillUnionId, UserBindDO::getUnionId, identity.getUnionId())
                .set(fillOpenId, UserBindDO::getOpenId, identity.getOpenId()));
        if (fillUnionId) {
            bindDO.setUnionId(identity.getUnionId());
        }
        if (fillOpenId) {
            bindDO.setOpenId(identity.getOpenId());
        }
    }
}
