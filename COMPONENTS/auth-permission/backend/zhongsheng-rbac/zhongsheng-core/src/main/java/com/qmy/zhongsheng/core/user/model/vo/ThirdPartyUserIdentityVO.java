package com.qmy.zhongsheng.core.user.model.vo;

import lombok.Data;

/**
 * @author AI Coding
 * @description ThirdPartyUserIdentityVO
 * @date 2026/03/20 09:49
 */
@Data
public class ThirdPartyUserIdentityVO {

    private String platform;

    private String unionId;

    private String openId;

    private String thirdUserId;

    private String nickName;

    private String avatarUrl;

    private String email;

    private String mobile;
}
