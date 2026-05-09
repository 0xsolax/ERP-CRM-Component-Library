package com.qmy.zhongsheng.api.dto.production;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 生产组分页查询 DTO。
 *
 * @author AI Coding
 */
@Data
@Schema(description = "生产组分页查询 DTO")
public class ProductionGroupListQueryDTO {

    private Integer pageNum = 1;

    private Integer pageSize = 20;

    private String keyword;

    private Integer status;
}
