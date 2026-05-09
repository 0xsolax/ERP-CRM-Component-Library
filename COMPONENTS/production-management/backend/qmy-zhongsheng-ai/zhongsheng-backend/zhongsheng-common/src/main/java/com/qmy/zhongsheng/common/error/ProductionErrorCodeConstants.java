package com.qmy.zhongsheng.common.error;

/**
 * 生产履约相关错误码。
 *
 * @author AI Coding
 */
public final class ProductionErrorCodeConstants {

    public static final ErrorCode PRODUCTION_GROUP_NOT_FOUND = new ErrorCode(40700, "生产组不存在或已停用");

    public static final ErrorCode PRODUCTION_GROUP_NAME_REQUIRED = new ErrorCode(40701, "生产组名称不能为空");

    public static final ErrorCode PRODUCTION_GROUP_CODE_DUPLICATE = new ErrorCode(40702, "生产组编码已存在");

    public static final ErrorCode PRODUCTION_ORDER_NOT_FOUND = new ErrorCode(40710, "生产总单不存在或无权访问");

    public static final ErrorCode PRODUCTION_ORDER_REQUIRED = new ErrorCode(40711, "请选择生产总单");

    public static final ErrorCode PRODUCTION_ORDER_ONLY_CONFIRMED = new ErrorCode(40712, "仅已确认订单可创建生产总单");

    public static final ErrorCode PRODUCTION_ORDER_CODE_DUPLICATE = new ErrorCode(40713, "生产总单号已存在");

    public static final ErrorCode PRODUCTION_ORDER_PRODUCT_REQUIRED = new ErrorCode(40714, "请至少添加一个生产产品");

    public static final ErrorCode PRODUCTION_ORDER_SOURCE_READONLY = new ErrorCode(40715, "来源订单生产总单不可通过手工新增入口修改");

    public static final ErrorCode PRODUCTION_PROGRESS_NOT_FOUND = new ErrorCode(40720, "生产产品行进度不存在");

    public static final ErrorCode PRODUCTION_BATCH_QTY_INVALID = new ErrorCode(40721, "本次安排数量必须大于 0 且不能超过可安排数量");

    public static final ErrorCode PRODUCTION_DELIVERY_QTY_INVALID = new ErrorCode(40722, "本次交货数量必须大于 0 且不能超过剩余待交数量");

    public static final ErrorCode PRODUCTION_BATCH_REQUIRED = new ErrorCode(40723, "请至少提交一条生产安排");

    private ProductionErrorCodeConstants() {
    }
}
