package com.qmy.project.core.auth.service.strategy;

import com.qmy.project.common.constants.AuthErrorCodeConstants;
import com.qmy.project.common.exception.ServiceExceptionUtil;
import com.qmy.project.common.enums.ScanLoginTypeEnum;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 09:49
 */
@Component
public class ScanLoginStrategyContext {

    private final Map<ScanLoginTypeEnum, ScanLoginStrategy> strategyMap = new EnumMap<>(ScanLoginTypeEnum.class);

    public ScanLoginStrategyContext(List<ScanLoginStrategy> strategies) {
        strategies.forEach(item -> strategyMap.put(item.getType(), item));
    }

    public ScanLoginStrategy getStrategy(ScanLoginTypeEnum loginType) {
        ScanLoginStrategy strategy = strategyMap.get(loginType);
        if (strategy == null) {
            throw ServiceExceptionUtil.exception(AuthErrorCodeConstants.UNSUPPORTED_SCAN_LOGIN_TYPE);
        }
        return strategy;
    }
}
