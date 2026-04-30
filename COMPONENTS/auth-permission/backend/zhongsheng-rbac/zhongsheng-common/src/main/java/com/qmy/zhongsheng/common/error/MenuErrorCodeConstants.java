package com.qmy.zhongsheng.common.error;

/**
 * 系统菜单相关错误码。
 *
 * @author 单漪甜
 */
public final class MenuErrorCodeConstants {

    public static final ErrorCode MENU_NOT_FOUND = new ErrorCode(40440, "菜单不存在");

    public static final ErrorCode MENU_PERMISSION_DUPLICATE = new ErrorCode(40041, "权限标识已存在");

    public static final ErrorCode MENU_PARENT_NOT_FOUND = new ErrorCode(40042, "父级菜单不存在");

    public static final ErrorCode MENU_TYPE_INVALID = new ErrorCode(40043, "菜单类型无效");

    public static final ErrorCode MENU_HAS_CHILDREN = new ErrorCode(40044, "存在子菜单，无法删除");
}