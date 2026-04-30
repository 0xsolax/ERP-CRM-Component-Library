package com.qmy.project.core.user.manager;

import com.qmy.project.core.user.model.vo.ThirdPartyUserIdentityVO;
import com.qmy.project.core.user.model.entity.UserDO;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 09:49
 */
public interface UserManager {

    UserDO authenticateByUserName(String userName, String rawPassword);

    UserDO getById(Long userId);

    void validateLoginable(UserDO userDO);

    /**
     * 持久化新用户；{@code status}、{@code gender} 未设置时按普通第三方用户默认填充。
     */
    UserDO save(UserDO userDO);

    void syncProfile(UserDO userDO, ThirdPartyUserIdentityVO identity);

    /**
     * 根据 {@code user.avatar_file_id} 解析展示用头像 URL；无文件或文件不存在则 {@code null}。
     */
    String resolveAvatarUrl(UserDO userDO);

    /**
     * 将指定用户设为唯一超级管理员：其余用户 {@code admin_flag} 置为 false，该用户为 true。
     */
    void assignExclusiveSuperAdmin(Long userId);
}
