package com.qmy.zhongsheng.common.error;

/**
 * 产品（product）相关错误码。
 *
 * @author 单漪甜
 */
public final class ProductErrorCodeConstants {

    public static final ErrorCode PRODUCT_NOT_FOUND = new ErrorCode(40480, "产品不存在");

    public static final ErrorCode PRODUCT_FABRIC_PRINTING_SIZE_NOT_EQUAL = new ErrorCode(40481, "产品面料选择列表与印刷方式列表长度不一致");
    private ProductErrorCodeConstants() {
    }
}
