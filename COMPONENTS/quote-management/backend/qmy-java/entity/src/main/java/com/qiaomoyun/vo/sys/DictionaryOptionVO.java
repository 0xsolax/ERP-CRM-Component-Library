package com.qiaomoyun.vo.sys;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 数据字典下拉选项
 */
@Data
public class DictionaryOptionVO {

    @Schema(description = "显示文本")
    private String label;

    @Schema(description = "值（字典 key）")
    private String value;
}
