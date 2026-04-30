package com.qmy.zhongsheng.common.error;

/**
 * @author AI Coding
 * @description OssErrorCodeConstants
 * @date 2026/03/20 09:49
 */
public final class OssErrorCodeConstants {

    public static final ErrorCode OSS_STS_CONFIG_MISSING = new ErrorCode(1002001, "OSS STS配置不完整");
    public static final ErrorCode OSS_STS_TOKEN_ERROR = new ErrorCode(1002002, "获取OSS STS失败");

    private OssErrorCodeConstants() {
    }
}
