package com.qmy.zhongsheng.core.user.manager.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.qmy.zhongsheng.common.error.AuthErrorCodeConstants;
import com.qmy.zhongsheng.common.exception.ServiceExceptionUtil;
import com.qmy.zhongsheng.common.enums.UserGenderEnum;
import com.qmy.zhongsheng.common.enums.StatusEnum;
import com.qmy.zhongsheng.core.user.model.vo.ThirdPartyUserIdentityVO;
import com.qmy.zhongsheng.core.file.manager.SystemFileManager;
import com.qmy.zhongsheng.core.file.model.entity.SystemFileDO;
import com.qmy.zhongsheng.core.user.dao.UserDAO;
import com.qmy.zhongsheng.core.user.manager.UserManager;
import com.qmy.zhongsheng.core.user.model.entity.UserDO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @author AI Coding
 * @description UserManagerImpl
 * @date 2026/03/20 09:49
 */
@Component
@RequiredArgsConstructor
public class UserManagerImpl implements UserManager {

    private final UserDAO userDAO;

    private final PasswordEncoder passwordEncoder;

    private final SystemFileManager systemFileManager;

    @Override
    public UserDO authenticateByUserName(String userName, String rawPassword) {
        UserDO userDO = userDAO.selectOne(Wrappers.<UserDO>lambdaQuery()
                .and(wrapper -> wrapper.eq(UserDO::getUserName, userName)
                        .or().eq(UserDO::getEmail, userName)
                        .or().eq(UserDO::getMobile, userName))
                .last("limit 1"));
        if (userDO == null || userDO.getPasswordHash() == null || userDO.getPasswordHash().isBlank()
                || !passwordEncoder.matches(rawPassword, userDO.getPasswordHash())) {
            throw ServiceExceptionUtil.exception(AuthErrorCodeConstants.ACCOUNT_OR_PASSWORD_INVALID);
        }
        validateLoginable(userDO);
        return userDO;
    }

    @Override
    public UserDO getById(Long userId) {
        UserDO userDO = userDAO.selectById(userId);
        if (userDO == null) {
            throw ServiceExceptionUtil.exception(AuthErrorCodeConstants.USER_NOT_FOUND);
        }
        return userDO;
    }

    @Override
    public void validateLoginable(UserDO userDO) {
        if (userDO == null) {
            throw ServiceExceptionUtil.exception(AuthErrorCodeConstants.USER_NOT_FOUND);
        }
        if (StatusEnum.DISABLED.getCode().equals(userDO.getStatus())) {
            throw ServiceExceptionUtil.exception(AuthErrorCodeConstants.ACCOUNT_DISABLED);
        }
    }

    @Override
    public UserDO save(UserDO userDO) {
        if (userDO.getStatus() == null) {
            userDO.setStatus(StatusEnum.NORMAL.getCode());
        }
        if (userDO.getGender() == null) {
            userDO.setGender(UserGenderEnum.UNKNOWN.getCode());
        }
        userDAO.insert(userDO);
        return userDO;
    }

    @Override
    public void syncProfile(UserDO userDO, ThirdPartyUserIdentityVO identity) {
        Long newAvatarFileId = systemFileManager.upsertUserAvatarFile(userDO.getAvatarFileId(), identity.getAvatarUrl());
        boolean persistAvatar = newAvatarFileId != null
                && (userDO.getAvatarFileId() == null || !userDO.getAvatarFileId().equals(newAvatarFileId));
        userDAO.update(null, Wrappers.<UserDO>lambdaUpdate()
                .eq(UserDO::getId, userDO.getId())
                .set(identity.getNickName() != null && !identity.getNickName().isBlank(), UserDO::getNickName, identity.getNickName())
                .set(identity.getEmail() != null && !identity.getEmail().isBlank(), UserDO::getEmail, identity.getEmail())
                .set(identity.getMobile() != null && !identity.getMobile().isBlank(), UserDO::getMobile, identity.getMobile())
                .set(persistAvatar, UserDO::getAvatarFileId, newAvatarFileId));
        if (persistAvatar) {
            userDO.setAvatarFileId(newAvatarFileId);
        }
    }

    @Override
    public String resolveAvatarUrl(UserDO userDO) {
        if (userDO == null || userDO.getAvatarFileId() == null) {
            return null;
        }
        SystemFileDO file = systemFileManager.getById(userDO.getAvatarFileId());
        return file != null ? file.getUrl() : null;
    }

    @Override
    public void assignExclusiveSuperAdmin(Long userId) {
        userDAO.update(null, Wrappers.<UserDO>lambdaUpdate().set(UserDO::getAdminFlag, false));
        userDAO.update(null, Wrappers.<UserDO>lambdaUpdate()
                .eq(UserDO::getId, userId)
                .set(UserDO::getAdminFlag, true));
    }
}
