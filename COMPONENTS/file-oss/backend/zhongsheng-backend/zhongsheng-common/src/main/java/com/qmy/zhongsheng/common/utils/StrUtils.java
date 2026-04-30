package com.qmy.zhongsheng.common.utils;

import cn.hutool.core.util.StrUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static com.qmy.zhongsheng.common.utils.ValidityUtils.isBlank;

/**
 * @author shanyitian
 * @description 字符串工具类
 * @date 2026/4/8
 */
public final class StrUtils {

    private StrUtils() {
    }

    /**
     * 比较两条包材「尺寸」字符串的大小（如 {@code 10*40}、{@code 10*40*1}，支持 {@code ×}/{@code x}/{@code X} 分隔）。
     * <p>先按各维乘积（体积代理）比，再按最大单边，再按原始字符串字典序。</p>
     *
     * @param sizeA 尺寸 a
     * @param sizeB 尺寸 b
     * @return 大于 0 表示 a 更大；小于 0 表示 b 更大；0 表示相当
     */
    public static int compareByPackagingSize(String sizeA, String sizeB) {
        List<BigDecimal> da = parsePackagingDimensions(sizeA);
        List<BigDecimal> db = parsePackagingDimensions(sizeB);
        BigDecimal va = packagingVolumeProduct(da);
        BigDecimal vb = packagingVolumeProduct(db);
        int c = va.compareTo(vb);
        if (c != 0) {
            return c;
        }
        BigDecimal ma = packagingMaxDimension(da);
        BigDecimal mb = packagingMaxDimension(db);
        c = ma.compareTo(mb);
        if (c != 0) {
            return c;
        }
        if (isBlank(sizeA) && isBlank(sizeB)) {
            return 0;
        }
        if (isBlank(sizeA)) {
            return -1;
        }
        if (isBlank(sizeB)) {
            return 1;
        }
        return Objects.requireNonNull(sizeA).compareTo(Objects.requireNonNull(sizeB));
    }

    private static List<BigDecimal> parsePackagingDimensions(String size) {
        if (isBlank(size)) {
            return List.of();
        }
        String normalized = size.trim()
                .replace('×', '*')
                .replace('＊', '*');
        normalized = normalized.replace('x', '*').replace('X', '*');
        String[] parts = normalized.split("\\*");
        List<BigDecimal> out = new ArrayList<>();
        for (String part : parts) {
            String t = part.trim();
            if (isBlank(t)) {
                continue;
            }
            try {
                out.add(new BigDecimal(t));
            } catch (NumberFormatException ignored) {
                // 跳过无法解析的段
            }
        }
        return out;
    }

    private static BigDecimal packagingVolumeProduct(List<BigDecimal> dims) {
        if (dims == null || dims.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal v = BigDecimal.ONE;
        for (BigDecimal d : dims) {
            v = v.multiply(d);
        }
        return v;
    }

    private static BigDecimal packagingMaxDimension(List<BigDecimal> dims) {
        if (dims == null || dims.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return Collections.max(dims, Comparator.naturalOrder());
    }

    /**
     * 从 URL 中解析获取 endpoint（域名部分）。
     * 例如：https://qmcloud-oss-test.oss-cn-hangzhou.aliyuncs.com/uploads/2026/04/file.jpg
     * 返回：https://qmcloud-oss-test.oss-cn-hangzhou.aliyuncs.com
     *
     * @param url 完整 URL
     * @return endpoint，URL 为空时返回空字符串
     */
    public static String parseEndpoint(String url) {
        UrlParts parts = parseUrl(url);
        return parts.endpoint;
    }

    /**
     * 从 URL 中解析获取 key（存储路径部分）。
     * 例如：https://qmcloud-oss-test.oss-cn-hangzhou.aliyuncs.com/uploads/2026/04/file.jpg
     * 返回：uploads/2026/04/file.jpg
     *
     * @param url 完整 URL
     * @return key，URL 为空时返回空字符串
     */
    public static String parseKey(String url) {
        UrlParts parts = parseUrl(url);
        return parts.key;
    }

    /**
     * 解析 URL，提取 endpoint 和 key。
     *
     * @param url 完整 URL
     * @return 解析结果，包含 endpoint 和 key
     */
    public static UrlParts parseUrl(String url) {
        if (StrUtil.isBlank(url)) {
            return new UrlParts("", "");
        }
        url = url.trim();
        int protocolEnd = url.indexOf("://");
        if (protocolEnd < 0) {
            int firstSlash = url.indexOf('/');
            if (firstSlash < 0) {
                return new UrlParts(url, "");
            }
            return new UrlParts(url.substring(0, firstSlash), url.substring(firstSlash + 1));
        }
        int domainStart = protocolEnd + 3;
        int pathStart = url.indexOf('/', domainStart);
        if (pathStart < 0) {
            return new UrlParts(url, "");
        }
        String endpoint = url.substring(0, pathStart);
        String key = url.substring(pathStart + 1);
        return new UrlParts(endpoint, key);
    }

    /**
     * 拼接 endpoint + key 为完整 URL。
     *
     * @param endpoint 域名部分
     * @param key      存储路径部分
     * @return 完整 URL
     */
    public static String buildUrl(String endpoint, String key) {
        if (StrUtil.isBlank(endpoint)) {
            return key;
        }
        if (StrUtil.isBlank(key)) {
            return endpoint;
        }
        return endpoint + "/" + key;
    }

    /**
     * URL 解析结果
     */
    public static final class UrlParts {
        private final String endpoint;
        private final String key;

        public UrlParts(String endpoint, String key) {
            this.endpoint = endpoint;
            this.key = key;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public String getKey() {
            return key;
        }
    }
}