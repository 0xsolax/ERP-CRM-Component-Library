package com.qmy.project.core.file.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author AI Coding
 * @description 文件主类型。具体次类型见 {@link SystemFileSubTypeEnum}，每个次类型通过其 {@code mainType} 字段关联到本枚举。
 * @date 2026/03/20 09:49
 */
@Getter
@AllArgsConstructor
public enum SystemFileMainTypeEnum {

    /** 当前独立部署实例的租户级资源文件 */
    TENANT("TENANT"),

    /** 用户维度资源（如第三方头像元数据） */
    USER("USER");

    private final String code;
}
