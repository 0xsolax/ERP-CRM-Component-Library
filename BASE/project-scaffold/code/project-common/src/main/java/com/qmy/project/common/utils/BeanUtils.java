package com.qmy.project.common.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 09:49
 */
public final class BeanUtils {

    private BeanUtils() {
    }

    public static <T> T toBean(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        return BeanUtil.toBean(source, targetClass);
    }

    public static <T> T toBean(Object source, Class<T> targetClass, Consumer<T> peek) {
        T target = toBean(source, targetClass);
        if (target != null && peek != null) {
            peek.accept(target);
        }
        return target;
    }

    public static <S, T> List<T> toBean(List<S> source, Class<T> targetType) {
        if (source == null) {
            return null;
        }
        return source.stream().map(item -> toBean(item, targetType)).toList();
    }

    public static <S, T> List<T> toBean(List<S> source, Class<T> targetType, Consumer<T> peek) {
        List<T> list = toBean(source, targetType);
        if (list != null && peek != null) {
            list.forEach(peek);
        }
        return list;
    }

    public static void copyProperties(Object source, Object target) {
        if (source == null || target == null) {
            return;
        }
        BeanUtil.copyProperties(source, target, false);
    }

    public static <T, K> Map<K, List<T>> groupToMap(Collection<T> source, Function<T, K> keyMapper) {
        if (source == null || keyMapper == null) {
            return Collections.emptyMap();
        }
        return source.stream().collect(Collectors.groupingBy(keyMapper));
    }

    public static <T, K, V> Map<K, List<V>> groupToMap(Collection<T> source, Function<T, K> keyMapper,
                                                        Function<T, V> valueMapper) {
        if (source == null || keyMapper == null || valueMapper == null) {
            return Collections.emptyMap();
        }
        return source.stream()
                .collect(Collectors.groupingBy(keyMapper, Collectors.mapping(valueMapper, Collectors.toList())));
    }

    public static <T, K> Map<K, List<T>> listToMap(List<T> source, Function<T, K> keyMapper) {
        return groupToMap(source, keyMapper);
    }

    public static <T, K> Map<K, List<T>> setToMap(Set<T> source, Function<T, K> keyMapper) {
        return groupToMap(source, keyMapper);
    }

    public static <T, K, V> Map<K, V> toMap(Collection<T> source, Function<T, K> keyMapper,
                                            Function<T, V> valueMapper) {
        if (source == null || keyMapper == null || valueMapper == null) {
            return Collections.emptyMap();
        }
        return source.stream()
                .collect(Collectors.toMap(keyMapper, valueMapper, (oldVal, newVal) -> newVal));
    }

    public static <T, K> Map<K, T> toMap(Collection<T> source, Function<T, K> keyMapper) {
        return toMap(source, keyMapper, Function.identity());
    }

    public static <T, R> List<R> toList(Collection<T> source, Function<T, R> mapper) {
        if (source == null || mapper == null) {
            return Collections.emptyList();
        }
        return source.stream().map(mapper).collect(Collectors.toList());
    }

    public static <T, R> Set<R> toSet(Collection<T> source, Function<T, R> mapper) {
        if (source == null || mapper == null) {
            return Collections.emptySet();
        }
        return source.stream().map(mapper).filter(Objects::nonNull).collect(Collectors.toSet());
    }
}
