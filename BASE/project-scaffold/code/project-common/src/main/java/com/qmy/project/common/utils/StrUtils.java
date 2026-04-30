package com.qmy.project.common.utils;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;

/**
 * @author shanyitian
 * @description 字符串工具类
 * @date 2026/4/8
 */
public final class StrUtils {

    private StrUtils() {
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
     * @param key 存储路径部分
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
    @Getter
    public static final class UrlParts {

        private final String endpoint;

        private final String key;

        public UrlParts(String endpoint, String key) {
            this.endpoint = endpoint;
            this.key = key;
        }

    }
}