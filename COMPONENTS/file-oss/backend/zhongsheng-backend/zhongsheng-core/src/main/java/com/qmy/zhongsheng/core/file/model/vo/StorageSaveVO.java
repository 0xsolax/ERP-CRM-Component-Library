package com.qmy.zhongsheng.core.file.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author shanyitian
 * @description 文件存储保存返回 VO
 * @date 2026/4/8
 */
@Data
public class StorageSaveVO {

    @Schema(description = "文件主键 ID")
    private Long id;

    @Schema(description = "文件访问 URL（endpoint + key 拼接）")
    private String url;
}