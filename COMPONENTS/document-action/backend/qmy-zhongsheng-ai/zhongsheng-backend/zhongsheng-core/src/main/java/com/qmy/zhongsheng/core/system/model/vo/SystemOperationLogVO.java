package com.qmy.zhongsheng.core.system.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统操作日志 VO。
 *
 * @author AI Coding
 */
@Data
@Schema(description = "系统操作日志 VO")
public class SystemOperationLogVO {

    private String id;

    private String moduleType;

    private String moduleLabel;

    private String documentType;

    private String documentTypeLabel;

    private String documentId;

    private String documentCode;

    private String actionType;

    private String actionLabel;

    private String beforeStatus;

    private String afterStatus;

    private String beforeLockState;

    private String afterLockState;

    private String operatorId;

    private String operatorName;

    private String actionReason;

    private String diffSummary;

    private String diffDetail;

    private LocalDateTime createTime;
}
