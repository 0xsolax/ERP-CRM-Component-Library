package com.qmy.project.config;

import com.qmy.project.core.auth.model.AuthSettings;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 将 {@code auth.*} 配置绑定为可注入的 {@link AuthSettings}（定义在 core.auth.model，绑定在 application）。
 */
@Configuration
public class AuthSettingsConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "auth")
    public AuthSettings authSettings() {
        return new AuthSettings();
    }
}
