package com.qmy.zhongsheng.common.constants;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 接口级权限标识（与 {@code system_menu.permission} 对齐；超管拥有 {@code *} 时放行全部）。
 * <p>
 * 本类为权限串<strong>唯一维护处</strong>。Controller 配合 Bean {@code ss} 写
 * {@code @PreAuthorize("@ss.hasPermission(@ss.perm('MATERIAL_PAGE'))")}，括号内字符串须与本类某
 * {@code public static final String} 字段名一致；{@code SpElPermissionService} 启动时反射加载，无需再抄一遍。
 *
 * @author AI Coding
 */
@SuppressWarnings("unused")
public final class ApiPermissionConstants {

    private ApiPermissionConstants() {
    }

    /**
     * 扫描本类所有 {@code public static final String} 常量：字段名 → 权限串（与 {@code system_menu.permission} 一致）。
     * <p>
     * 供 {@code SpElPermissionService}、启动时菜单占位同步等复用，避免多处反射逻辑分叉。
     *
     * @return 不可变映射，迭代顺序与字段声明顺序一致
     */
    public static Map<String, String> scanAllEntries() {
        Map<String, String> map = new LinkedHashMap<>();
        for (Field field : ApiPermissionConstants.class.getDeclaredFields()) {
            int mod = field.getModifiers();
            if (Modifier.isStatic(mod) && Modifier.isPublic(mod) && field.getType() == String.class) {
                try {
                    String value = (String) field.get(null);
                    if (value != null && !value.isBlank()) {
                        map.put(field.getName(), value);
                    }
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Cannot read ApiPermissionConstants." + field.getName(), e);
                }
            }
        }
        return Collections.unmodifiableMap(map);
    }

    /**
     * 角色管理
     */
    public static final String ROLE_PAGE = "system:role:page";

    /**
     * 菜单管理
     */
    public static final String MENU_SAVE_OR_UPDATE = "system:menu:save";
    public static final String MENU_LIST = "system:menu:list";
    public static final String MENU_DELETE = "system:menu:remove";

    /**
     * 产品
     */
    public static final String PRODUCT_SAVE_OR_UPDATE = "product:product:save";
    public static final String PRODUCT_PAGE = "product:product:page";
    public static final String PRODUCT_DETAIL = "product:product:detail";
    public static final String PRODUCT_DELETE = "product:product:remove";

    /**
     * 基础数据
     */
    public static final String BASE_DATA_SAVE_OR_UPDATE = "base:data:save";
    public static final String BASE_DATA_DELETE = "base:data:remove";
    public static final String BASE_DATA_LIST = "base:data:list";
    public static final String BASE_DATA_LIST_BY_NODE_KEY = "base:data:query";
    public static final String BASE_DATA_TREE_NODE_LIST = "base:data:tree";

    /**
     * 材料
     */
    public static final String MATERIAL_CATEGORY_SAVE = "material:category:save";
    public static final String MATERIAL_CATEGORY_LIST = "material:category:list";
    public static final String MATERIAL_CATEGORY_DELETE = "material:category:remove";
    public static final String MATERIAL_SAVE_OR_UPDATE = "material:material:save";
    public static final String MATERIAL_PAGE = "material:material:page";
    public static final String MATERIAL_DELETE = "material:material:remove";
    public static final String MATERIAL_LIST_BY_CATEGORY = "material:material:list";

    /**
     * 面料
     */
    public static final String FABRIC_SAVE_OR_UPDATE = "material:fabric:save";
    public static final String FABRIC_PAGE = "material:fabric:page";
    public static final String FABRIC_LIST = "material:fabric:list";
    public static final String FABRIC_DETAIL = "material:fabric:detail";
    public static final String FABRIC_DELETE = "material:fabric:remove";

    /**
     * 伞架
     */
    public static final String UMBRELLA_FRAME_SAVE_OR_UPDATE = "material:umbrella:save";
    public static final String UMBRELLA_FRAME_PAGE = "material:umbrella:page";
    public static final String UMBRELLA_FRAME_DETAIL = "material:umbrella:detail";
    public static final String UMBRELLA_FRAME_DELETE = "material:umbrella:remove";
    public static final String UMBRELLA_FRAME_LIST = "material:umbrella:list";

    /**
     * 包材
     */
    public static final String PACKAGING_SAVE_OR_UPDATE = "material:packaging:save";
    public static final String PACKAGING_SAVE_DEFAULT = "material:packaging:saveDefault";
    public static final String PACKAGING_PAGE = "material:packaging:page";
    public static final String PACKAGING_DELETE = "material:packaging:remove";

    /**
     * 工艺
     */
    public static final String PROCESS_SAVE_OR_UPDATE = "process:process:save";
    public static final String PROCESS_PAGE = "process:process:page";
    public static final String PROCESS_LIST = "process:process:list";
    public static final String PROCESS_DELETE = "process:process:remove";
}
