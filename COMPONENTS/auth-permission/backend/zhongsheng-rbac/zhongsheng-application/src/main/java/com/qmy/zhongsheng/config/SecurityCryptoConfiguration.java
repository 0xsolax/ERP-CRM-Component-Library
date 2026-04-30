package com.qmy.zhongsheng.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 与 Spring Security Crypto 相关的启动期 Bean（原在 core 的 AuthConfiguration）。
 */
@Configuration
public class SecurityCryptoConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
