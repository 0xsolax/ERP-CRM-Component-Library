package com.qmy.zhongsheng.core.process.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 工序视图对象。
 *
 * @author AI Coding
 */
@Data
@Schema(description = "工序视图对象")
public class ProcessVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "工序名称")
    private String name;
}