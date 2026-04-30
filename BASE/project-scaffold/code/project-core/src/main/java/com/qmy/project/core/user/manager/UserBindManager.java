package com.qmy.project.core.user.manager;

import com.qmy.project.core.user.model.vo.ThirdPartyUserIdentityVO;
import com.qmy.project.core.user.model.entity.UserBindDO;
import com.qmy.project.core.user.model.entity.UserDO;

import java.util.List;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 09:49
 */
public interface UserBindManager {

    /**
     * 根据第三方身份解析已绑定用户；若无绑定则报错，不自动创建用户。
     */
    UserDO getBoundUserByIdentity(ThirdPartyUserIdentityVO identity);

    /**
     * 该用户下全部绑定，按 {@code update_time}、{@code id} 降序。
     */
    List<UserBindDO> listByUserId(Long userId);

    /**
     * 已绑定的 {@code platform} 列表（去重保序）。
     */
    List<String> listBoundPlatforms(Long userId);

    /**
     * 按平台与第三方用户 ID（{@code third_user_id}）查询绑定，最多一条。
     */
    UserBindDO findByPlatformAndThirdUserId(String platform, String thirdUserId);

    /**
     * 新增第三方绑定；是否允许同一用户多条由库表唯一键决定，冲突时抛业务异常。
     */
    Long save(UserBindDO bind);
}
