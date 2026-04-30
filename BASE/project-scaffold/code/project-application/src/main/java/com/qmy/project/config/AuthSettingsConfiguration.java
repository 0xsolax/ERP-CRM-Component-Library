package com.qmy.project.config;

import com.qmy.project.core.auth.model.AuthSettings;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

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

    @Bean
    public SmartInitializingSingleton authSettingsValidator(AuthSettings authSettings) {
        return () -> {
            if (!StringUtils.hasText(authSettings.getJwt().getSecret())) {
                throw new IllegalStateException("auth.jwt.secret must be configured by environment or secret manager");
            }
        };
    }
}
