package com.qmy.zhongsheng.common.error;

/**
 * 面料（fabric）相关错误码。
 *
 * @author AI Coding
 */
public final class FabricErrorCodeConstants {

    public static final ErrorCode FABRIC_NOT_FOUND = new ErrorCode(40450, "面料不存在");

    public static final ErrorCode FABRIC_TYPE_ID_REQUIRED = new ErrorCode(40051, "面料种类ID不能为空");

    public static final ErrorCode FABRIC_MODEL_ID_REQUIRED = new ErrorCode(40052, "面料型号ID不能为空");

    public static final ErrorCode FABRIC_WIDTH_ID_REQUIRED = new ErrorCode(40053, "面料门幅ID不能为空");

    public static final ErrorCode FABRIC_TYPE_DATA_NOT_FOUND = new ErrorCode(40057, "面料种类基础数据不存在");

    public static final ErrorCode FABRIC_MODEL_DATA_NOT_FOUND = new ErrorCode(40058, "面料型号基础数据不存在");

    public static final ErrorCode FABRIC_WIDTH_DATA_NOT_FOUND = new ErrorCode(40059, "面料门幅基础数据不存在");

    public static final ErrorCode FABRIC_PRICE_REQUIRED = new ErrorCode(40054, "面料单价不能为空");

    public static final ErrorCode FABRIC_UNIT_REQUIRED = new ErrorCode(40055, "面料单位不能为空");

    public static final ErrorCode FABRIC_UNIT_INVALID = new ErrorCode(40056, "面料单位只能是米或码");

    public static final ErrorCode FABRIC_DUPLICATE = new ErrorCode(40060, "面料数据已存在");
}