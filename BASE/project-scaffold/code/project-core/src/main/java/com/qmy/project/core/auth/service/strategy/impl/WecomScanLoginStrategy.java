package com.qmy.project.core.auth.service.strategy.impl;

import com.qmy.project.core.user.model.vo.ThirdPartyUserIdentityVO;
import com.qmy.project.common.enums.ScanLoginTypeEnum;
import com.qmy.project.common.constants.AuthErrorCodeConstants;
import com.qmy.project.common.exception.ServiceExceptionUtil;
import com.qmy.project.core.auth.service.strategy.ScanLoginStrategy;
import org.springframework.stereotype.Component;

/**
 * 企业微信扫码：待接入开放平台换票与用户身份，当前未实现。
 */
@Component
public class WecomScanLoginStrategy implements ScanLoginStrategy {

    @Override
    public ScanLoginTypeEnum getType() {
        return ScanLoginTypeEnum.WECOM;
    }

    @Override
    public ThirdPartyUserIdentityVO authenticate(String code, String referer) {
        throw ServiceExceptionUtil.exception(AuthErrorCodeConstants.SCAN_LOGIN_NOT_READY);
    }
}
