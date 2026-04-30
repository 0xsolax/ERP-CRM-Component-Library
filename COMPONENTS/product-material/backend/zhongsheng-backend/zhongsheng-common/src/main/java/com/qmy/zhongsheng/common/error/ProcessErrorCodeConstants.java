package com.qmy.zhongsheng.common.error;

/**
 * 工序（process）相关错误码。
 *
 * @author AI Coding
 */
public final class ProcessErrorCodeConstants {

    public static final ErrorCode PROCESS_NOT_FOUND = new ErrorCode(40440, "工序不存在");

    public static final ErrorCode PROCESS_NAME_REQUIRED = new ErrorCode(40042, "工序名称不能为空");

    public static final ErrorCode PROCESS_NAME_DUPLICATE = new ErrorCode(40043, "工序名称已存在");

    private ProcessErrorCodeConstants() {
    }
}