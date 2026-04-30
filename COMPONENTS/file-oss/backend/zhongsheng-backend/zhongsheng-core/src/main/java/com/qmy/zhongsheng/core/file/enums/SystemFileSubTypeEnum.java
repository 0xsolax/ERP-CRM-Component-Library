package com.qmy.zhongsheng.core.file.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * @author AI Coding
 * @description 系统文件次类型（全量集中定义）。每个常量通过 {@link #mainType} 关联所属 {@link SystemFileMainTypeEnum}。
 * @date 2026/03/20 09:49
 */
@Getter
@AllArgsConstructor
public enum SystemFileSubTypeEnum {

    // —— 主类型 TENANT ——
    BACKGROUND("BACKGROUND", SystemFileMainTypeEnum.TENANT),
    LOGIN_LOGO("LOGIN_LOGO", SystemFileMainTypeEnum.TENANT),
    MENU_COLLAPSED_LOGO("MENU_COLLAPSED_LOGO", SystemFileMainTypeEnum.TENANT),
    MENU_EXPANDED_LOGO("MENU_EXPANDED_LOGO", SystemFileMainTypeEnum.TENANT),

    // —— 主类型 USER ——
    /** 第三方 OAuth 头像，{@code system_file.url} 存外网可访问地址 */
    USER_AVATAR("USER_AVATAR", SystemFileMainTypeEnum.USER),

    // —— 主类型 MATERIAL ——
    /** 材料图片，{@code master_id} 关联 material.id */
    MATERIAL_IMAGE("MATERIAL_IMAGE", SystemFileMainTypeEnum.MATERIAL),

    /** 伞架图片，{@code master_id} 关联 umbrella_frame.id */
    UMBRELLA_FRAME_IMAGE("UMBRELLA_FRAME_IMAGE", SystemFileMainTypeEnum.MATERIAL),

    // —— 主类型 PRODUCT ——
    /** 产品图片，{@code master_id} 关联 product.id */
    PRODUCT_IMAGE("PRODUCT_IMAGE", SystemFileMainTypeEnum.PRODUCT);

    private final String code;
    private final SystemFileMainTypeEnum mainType;

    /**
     * 指定主类型下的全部次类型（顺序稳定，可用于分组初始化）。
     */
    public static List<SystemFileSubTypeEnum> subTypesOf(SystemFileMainTypeEnum mainType) {
        return Arrays.stream(values())
                .filter(e -> e.mainType == mainType)
                .toList();
    }

    /**
     * 在已知主类型时解析次类型（推荐：库表按 main_type + sub_type 存储时用此解析）。
     */
    public static SystemFileSubTypeEnum fromCodeUnderMain(String code, SystemFileMainTypeEnum mainType) {
        if (code == null || code.isBlank() || mainType == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(e -> e.mainType == mainType && e.code.equals(code))
                .findFirst()
                .orElse(null);
    }

    /**
     * 仅按 code 解析；要求全局 code 不重复。若未来不同主类型下出现相同 code，请改用 {@link #fromCodeUnderMain}。
     */
    public static SystemFileSubTypeEnum fromCode(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }
        return Arrays.stream(values())
                .filter(e -> e.code.equals(code))
                .findFirst()
                .orElse(null);
    }
}
