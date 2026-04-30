package com.qmy.zhongsheng.common.error;

/**
 * 包材（packaging）相关错误码。
 *
 * @author AI Coding
 */
public final class PackagingErrorCodeConstants {

    public static final ErrorCode PACKAGING_NOT_FOUND = new ErrorCode(40460, "包材不存在");

    public static final ErrorCode PACKAGING_TYPE_ID_REQUIRED = new ErrorCode(40061, "包材类型ID不能为空");

    public static final ErrorCode PACKAGING_TYPE_DATA_NOT_FOUND = new ErrorCode(40064, "包材类型基础数据不存在");

    public static final ErrorCode PACKAGING_NAME_REQUIRED = new ErrorCode(40062, "包材名称不能为空");

    public static final ErrorCode PACKAGING_PRICE_REQUIRED = new ErrorCode(40063, "包材单价不能为空");

    public static final ErrorCode PACKAGING_NAME_DUPLICATE = new ErrorCode(40065, "包材名称已存在");

    private PackagingErrorCodeConstants() {
    }
}