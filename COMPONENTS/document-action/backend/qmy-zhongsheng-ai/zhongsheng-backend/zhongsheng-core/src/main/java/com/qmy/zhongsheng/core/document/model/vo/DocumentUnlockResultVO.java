package com.qmy.zhongsheng.core.document.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 公共单据解锁动作结果 VO。
 *
 * @author AI Coding
 */
@Data
@Schema(description = "公共单据解锁动作结果 VO")
public class DocumentUnlockResultVO {

    private String documentType;

    private Long documentId;

    private String status;

    private String lockState;

    private Boolean needsReconfirm;

    private Long unlockRequestId;

    private String requestStatus;
}
