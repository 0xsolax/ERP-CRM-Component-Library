package com.qiaomoyun.vo.sal.sed;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 报价单历史记录
 */
@Data
public class SalSedQuotationHistoryVo {
    @Schema(description = "操作时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime operateTime;

    @Schema(description = "操作人")
    private String operatePerson;

    @Schema(description = "操作内容")
    private String operation;
}
