package com.qmy.project.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 基础树节点业务类型：与库表 {@code base_tree_node.biz_type} 一致，存大写英文，区分字段管理与分类等场景。
 *
 * @author AI Coding
 */
@Getter
@RequiredArgsConstructor
public enum BaseTreeBizTypeEnum {

    /**
     * 字段管理（页面约定、启动种子维护）。
     * {@link #leafOnlyDataBind} 为 true：仅允许叶子节点的 {@code data_bind_flag=1}，非叶子必须为 0。
     */
    EXAMPLE_MGMT("EXAMPLE_MGMT", true);

    /**
     * 落库值，与枚举常量语义一致的大写英文。
     */
    private final String value;

    /**
     * 是否限制「仅叶子节点可绑定基础数据」：为 true 时，仅叶子节点允许 {@code data_bind_flag=1}。
     */
    private final boolean leafOnlyDataBind;

    /**
     * 按库表/接口中的 {@code biz_type} 字符串解析，未知或空串时返回 {@code null}。
     *
     * @param value 库表或请求中的类型值
     * @return 对应枚举或 null
     */
    public static BaseTreeBizTypeEnum fromValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String v = value.trim();
        for (BaseTreeBizTypeEnum e : values()) {
            if (e.value.equals(v)) {
                return e;
            }
        }
        return null;
    }
}
