package com.qmy.project.core.auth.model;

import lombok.Data;

/**
 * 认证相关运行时配置（由启动模块通过 {@code @Bean @ConfigurationProperties} 绑定，无 Spring 注解）。
 */
@Data
public class AuthSettings {

    private Jwt jwt = new Jwt();

    private Scan scan = new Scan();

    @Data
    public static class Jwt {

        private String issuer = "project-scaffold";

        private String secret;

        private Long accessTokenExpireSeconds = 7200L;

        private String headerName = "qiaomoyun-token";
    }

    @Data
    public static class Scan {

        private Provider feishu = new Provider();

        private Provider dingtalk = new Provider();

        private Provider wecom = new Provider();
    }

    @Data
    public static class Provider {

        private String appId;

        private String appSecret;

        /**
         * 与飞书开放平台「重定向 URL」一致；当请求未携带可用的 {@code Origin} 或无法拼出 {@code /login} 回调时回退使用。
         */
        private String redirectUri;

        private String corpId;

        private String agentId;
    }
}
