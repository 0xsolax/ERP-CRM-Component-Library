package com.qmy.project.core.file.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author shanyitian
 * @description 文件返回模型（与 system_file 表 name/url/size/type 列一致）
 * @date 2026/3/26 16:51
 */
@Data
public class FileVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "图片/文件地址")
    private String url;

    @Schema(description = "文件大小")
    private Long size;

    @Schema(description = "文件类型")
    private String type;

    @Schema(description = "文件名称")
    private String name;

    @Schema(description = "主类型编码，对应 SystemFileMainTypeEnum")
    private String mainType;

    @Schema(description = "次类型编码，对应 SystemFileSubTypeEnum#code（需与 mainType 组合解析）")
    private String subType;
}
