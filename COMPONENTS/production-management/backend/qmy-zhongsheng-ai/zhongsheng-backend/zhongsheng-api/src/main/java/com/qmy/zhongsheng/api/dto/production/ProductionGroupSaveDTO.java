package com.qmy.zhongsheng.api.dto.production;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 生产组保存 DTO。
 *
 * @author AI Coding
 */
@Data
@Schema(description = "生产组保存 DTO")
public class ProductionGroupSaveDTO {

    private Long id;

    private String code;

    @NotBlank(message = "生产组名称不能为空")
    private String name;

    private Integer status = 1;

    private String remark;
}
