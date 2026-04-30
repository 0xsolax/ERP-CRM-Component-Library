package com.qmy.project.core.auth.service.strategy;

import com.qmy.project.core.user.model.vo.ThirdPartyUserIdentityVO;
import com.qmy.project.common.enums.ScanLoginTypeEnum;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 09:49
 */
public interface ScanLoginStrategy {

    /**
     * 获取当前策略类型
     *
     * @return ScanLoginTypeEnum
     */
    ScanLoginTypeEnum getType();

    /**
     * 三方登录认证
     * @param code 认证码
     * @param referer 浏览器 {@code Referer}，用于解析 OAuth {@code redirect_uri}（去掉 query/fragment 后的页面地址）
     * @return 认证信息
     */
    ThirdPartyUserIdentityVO authenticate(String code, String referer);
}
