package com.qmy.zhongsheng.common.error;

/**
 * 公共单据动作相关错误码。
 *
 * @author AI Coding
 */
public final class DocumentErrorCodeConstants {

    public static final ErrorCode DOCUMENT_TYPE_UNSUPPORTED = new ErrorCode(40090, "暂不支持的单据类型");

    public static final ErrorCode DOCUMENT_NOT_FOUND = new ErrorCode(40490, "单据不存在或无权访问");

    public static final ErrorCode DOCUMENT_UNLOCK_REASON_REQUIRED = new ErrorCode(40091, "请填写解锁原因");

    public static final ErrorCode DOCUMENT_NOT_LOCKED = new ErrorCode(40092, "当前单据未锁定，无需解锁");

    public static final ErrorCode DOCUMENT_UNLOCK_ALREADY_PENDING = new ErrorCode(40093, "当前单据已有待审批解锁申请");

    public static final ErrorCode DOCUMENT_UNLOCK_REQUEST_NOT_FOUND = new ErrorCode(40491, "未找到待审批解锁申请");

    public static final ErrorCode DOCUMENT_RECONFIRM_NOT_REQUIRED = new ErrorCode(40094, "当前单据无需重新确认");

    public static final ErrorCode DOCUMENT_OWNER_REQUIRED = new ErrorCode(40095, "请指定新的负责人");

    public static final ErrorCode DOCUMENT_OWNER_NOT_FOUND = new ErrorCode(40492, "指定负责人不存在");

    private DocumentErrorCodeConstants() {
    }
}
