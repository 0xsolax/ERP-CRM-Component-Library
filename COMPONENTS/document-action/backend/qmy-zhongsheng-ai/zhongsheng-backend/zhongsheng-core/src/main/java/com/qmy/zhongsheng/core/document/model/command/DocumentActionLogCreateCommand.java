package com.qmy.zhongsheng.core.document.model.command;

import lombok.Builder;
import lombok.Data;

/**
 * 公共单据动作日志写入命令。
 *
 * @author AI Coding
 */
@Builder
@Data
public class DocumentActionLogCreateCommand {

    private String documentType;

    private Long documentId;

    private String baseCode;

    private String serialCode;

    private String actionType;

    private String beforeStatus;

    private String afterStatus;

    private String beforeLockState;

    private String afterLockState;

    private String actionReason;

    private String diffSummary;

    private String diffDetail;
}
