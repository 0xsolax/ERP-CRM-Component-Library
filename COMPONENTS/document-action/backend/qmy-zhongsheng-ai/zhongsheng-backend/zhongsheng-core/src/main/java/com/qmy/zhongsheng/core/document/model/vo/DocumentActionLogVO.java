package com.qmy.zhongsheng.core.document.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公共单据动作日志 VO。
 *
 * @author AI Coding
 */
@Data
@Schema(description = "公共单据动作日志 VO")
public class DocumentActionLogVO {

    private Long id;

    private String documentType;

    private Long documentId;

    private String baseCode;

    private String serialCode;

    private String actionType;

    private String beforeStatus;

    private String afterStatus;

    private String beforeLockState;

    private String afterLockState;

    private Long operatorId;

    private String operatorName;

    private String actionReason;

    private String diffSummary;

    private String diffDetail;

    private LocalDateTime createTime;
}
