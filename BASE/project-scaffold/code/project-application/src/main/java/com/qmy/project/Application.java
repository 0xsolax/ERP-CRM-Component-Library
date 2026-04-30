package com.qmy.project;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 09:49
 */
@SpringBootApplication(scanBasePackages = "com.qmy.project")
@MapperScan(basePackages = "com.qmy.project.core", annotationClass = Mapper.class)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
