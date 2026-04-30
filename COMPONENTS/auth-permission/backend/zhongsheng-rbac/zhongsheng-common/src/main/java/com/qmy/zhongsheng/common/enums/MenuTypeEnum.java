package com.qmy.zhongsheng.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 菜单类型枚举：与库表 {@code system_menu.type} 一致。
 * <p>
 * 约定：{@code -1} 未知，{@code 1} 目录，{@code 2} 菜单，{@code 3} 按钮。
 *
 * @author 单漪甜
 */
@Getter
@RequiredArgsConstructor
public enum MenuTypeEnum {

    /**
     * 未知（如接口权限自动落库的占位行，待归类）。
     */
    UNKNOWN(-1),

    /**
     * 目录。
     */
    DIRECTORY(1),

    /**
     * 菜单。
     */
    MENU(2),

    /**
     * 按钮。
     */
    BUTTON(3);

    private final Integer code;

    /**
     * 按库表/接口中的 {@code type} 整数值解析，未知值时返回 {@code null}。
     *
     * @param code 库表或请求中的类型值
     * @return 对应枚举或 null
     */
    public static MenuTypeEnum fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (MenuTypeEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }
}