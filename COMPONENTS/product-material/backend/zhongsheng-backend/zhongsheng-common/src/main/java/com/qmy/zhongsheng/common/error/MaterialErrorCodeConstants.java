package com.qmy.zhongsheng.common.error;

/**
 * 材料（material）相关错误码。
 *
 * @author AI Coding
 */
public final class MaterialErrorCodeConstants {

    public static final ErrorCode MATERIAL_NOT_FOUND = new ErrorCode(40480, "材料不存在");

    public static final ErrorCode MATERIAL_CATEGORY_ID_REQUIRED = new ErrorCode(40081, "材料分类ID不能为空");

    public static final ErrorCode MATERIAL_NAME_REQUIRED = new ErrorCode(40082, "材料名称不能为空");

    public static final ErrorCode MATERIAL_CATEGORY_NOT_FOUND = new ErrorCode(40083, "材料分类不存在");

    public static final ErrorCode MATERIAL_CATEGORY_SORT_NUM_OUT_OF_RANGE = new ErrorCode(40086, "材料分类排序号超出范围，范围是为【1,%d】");

    public static final ErrorCode MATERIAL_CATEGORY_NAME_DUPLICATE = new ErrorCode(40087, "材料分类名称已存在");

    public static final ErrorCode MATERIAL_NAME_DUPLICATE = new ErrorCode(40088, "材料名称已存在");
}