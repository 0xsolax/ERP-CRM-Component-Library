package com.qmy.zhongsheng.api.dto.production;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 生产总单分页查询 DTO。
 *
 * @author AI Coding
 */
@Data
@Schema(description = "生产总单分页查询 DTO")
public class ProductionOrderListQueryDTO {

    private Integer pageNum = 1;

    private Integer pageSize = 20;

    private String keyword;

    private String status;

    private Long orderId;

    private Long productionGroupId;
}
