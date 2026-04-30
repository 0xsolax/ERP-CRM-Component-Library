package com.qmy.zhongsheng.api.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * @author shanyitian
 * @description 文件存储 DTO
 * @date 2026/4/8 17:07
 */
@Data
@Schema(description = "文件存储 DTO")
public class StorageDTO implements Serializable {

    @Schema(description = "文件名称")
    @NotBlank(message = "文件名称不能为空")
    private String name;

    @Schema(description = "文件访问URL")
    @NotBlank(message = "文件URL不能为空")
    private String url;

    @Schema(description = "文件类型（MIME类型）")
    private String type;

    @Schema(description = "文件大小（字节）")
    private Long size;

}
