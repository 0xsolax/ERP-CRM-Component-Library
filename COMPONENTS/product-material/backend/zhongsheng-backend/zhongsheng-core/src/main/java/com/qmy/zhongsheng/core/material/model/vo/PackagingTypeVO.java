package com.qmy.zhongsheng.core.material.model.vo;

/**
 * @author Gh
 * @data 2026/4/8 16:28
 * @description 包材类型
 */

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 包材类型视图对象。
 *
 */
@Data
@Schema(description = "包材类型视图对象")
public class PackagingTypeVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "包材类型名称")
    private String typeName;

    @Schema(description = "是否为默认包装")
    private String defaultTypeFlag;

}
