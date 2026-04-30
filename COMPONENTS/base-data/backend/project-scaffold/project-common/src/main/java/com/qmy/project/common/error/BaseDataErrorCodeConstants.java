package com.qmy.project.common.error;

/**
 * 通用基础数据（base_data）相关错误码。
 *
 * @author AI Coding
 */
public final class BaseDataErrorCodeConstants {

    public static final ErrorCode BASE_DATA_NOT_FOUND = new ErrorCode(40430, "基础数据不存在");

    public static final ErrorCode BASE_DATA_TREE_NODE_INVALID = new ErrorCode(40032, "基础树节点无效或不存在");

    public static final ErrorCode BASE_DATA_TREE_NODE_DATA_BIND_NOT_ALLOWED = new ErrorCode(40033, "该树节点不允许绑定基础数据");

    private BaseDataErrorCodeConstants() {
    }
}
