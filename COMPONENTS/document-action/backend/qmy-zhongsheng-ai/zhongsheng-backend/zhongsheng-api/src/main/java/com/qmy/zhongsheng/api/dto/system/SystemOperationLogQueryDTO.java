package com.qmy.zhongsheng.api.dto.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.qmy.zhongsheng.api.request.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 系统操作日志分页查询 DTO。
 *
 * @author AI Coding
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "系统操作日志分页查询 DTO")
public class SystemOperationLogQueryDTO extends BasePageQuery {

    @Schema(description = "业务模块：sales/purchase/production")
    private String moduleType;

    @Schema(description = "单据类型：quote/order/purchase/production")
    private String documentType;

    @Schema(description = "动作类型")
    private String actionType;

    @Schema(description = "操作人 ID")
    private Long operatorId;

    @Schema(description = "操作人名称关键字")
    private String operatorKeyword;

    @Schema(description = "单据编号、原因或摘要关键字")
    private String keyword;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "操作开始时间")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "操作结束时间")
    private LocalDateTime endTime;
}
