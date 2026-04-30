package com.qmy.project.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 09:49
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "QMY Project API",
                version = "1.0.0",
                description = "QMY 团队 project 脚手架接口文档",
                contact = @Contact(name = "qmy"),
                license = @License(name = "All Rights Reserved")
        )
)
public class OpenApiConfiguration {

}
