package com.qmy.project.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author shanyitian
 * @description id 请求模型
 * @date 2026/1/13 15:00
 */
@Schema(description = "id 请求模型")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IdRequestParam implements Serializable {

    @Schema(description = "id")
    @NotNull(message = "id 为空")
    private Long id;
}