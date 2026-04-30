package com.qmy.project.api.dto.file;

import com.qmy.project.common.utils.StrUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 与 {@code system_file} 业务列对齐的通用文件传输对象；{@code main_type}/{@code sub_type} 由服务端按接口语义设置，不在请求体中传递。
 *
 * @author AI Coding
 */
@Data
@Schema(description = "系统文件项（与库表业务字段对应）")
public class SystemFileDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "文件存储主键")
    private Long storageId;

    @Schema(description = "文件名")
    private String name;

    @Schema(description = "访问 URL")
    @NotBlank(message = "访问 URL 不能为空")
    private String url;

    @Schema(description = "文件大小")
    private Long size;

    @Schema(description = "文件类型")
    private String type;

    @Schema(description = "逻辑删除：1 表示删除；0 或 null 表示新增/更新")
    private Integer isDeleted;

    public String getEndpoint() {
        return StrUtils.parseEndpoint(this.url);
    }

    public String getFileKey() {
        return StrUtils.parseKey(this.url);
    }
}
