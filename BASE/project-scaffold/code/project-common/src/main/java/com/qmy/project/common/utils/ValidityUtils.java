package com.qmy.project.common.utils;

import cn.hutool.core.util.ObjectUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author shanyitian
 * @description 基础通用工具方法，主要涵盖常用的空判断、字符串、集合、布尔判断等。
 * @date 2026/1/9 17:12
 */
public final class ValidityUtils {

    private ValidityUtils() {
    }

    @SuppressWarnings("rawtypes")
    public static final List EMPTY_LIST = Collections.EMPTY_LIST;

    public static boolean equals(Object obj1, Object obj2) {
        return Objects.equals(obj1, obj2);
    }

    public static boolean contains(Collection<?> coll, Object target) {
        return coll != null && target != null && coll.contains(target);
    }

    /**
     * 判空（null、空串、全空白、"null"）
     */
    public static boolean isBlank(String str) {
        return StringUtils.isBlank(str) || "null".equalsIgnoreCase(str);
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * 等价 hasText：非 null 且包含非空白字符
     */
    public static boolean hasText(String str) {
        return StringUtils.isNotBlank(str);
    }

    public static boolean isNull(Object obj) {
        return ObjectUtil.isNull(obj);
    }

    public static boolean nonNull(Object obj) {
        return ObjectUtil.isNotNull(obj);
    }

    public static boolean isEmpty(Collection<?> coll) {
        return coll == null || CollectionUtils.isEmpty(coll);
    }

    public static boolean isNotEmpty(Collection<?> coll) {
        return !isEmpty(coll);
    }

    public static boolean isTrue(Boolean flag) {
        return nonNull(flag) && flag;
    }

    public static boolean isFalse(Boolean flag) {
        return nonNull(flag) && !flag;
    }
}
