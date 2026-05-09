package com.qmy.zhongsheng.core.document.model.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qmy.zhongsheng.infrastructure.model.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 公共单据解锁申请 DO。
 *
 * @author AI Coding
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("document_unlock_request")
public class DocumentUnlockRequestDO extends BaseDO {

    @TableField("document_type")
    private String documentType;

    @TableField("document_id")
    private Long documentId;

    @TableField(value = "base_code", updateStrategy = FieldStrategy.ALWAYS)
    private String baseCode;

    @TableField(value = "serial_code", updateStrategy = FieldStrategy.ALWAYS)
    private String serialCode;

    @TableField("request_type")
    private String requestType;

    @TableField("requester_id")
    private Long requesterId;

    @TableField("requester_name")
    private String requesterName;

    @TableField(value = "approver_role_key", updateStrategy = FieldStrategy.ALWAYS)
    private String approverRoleKey;

    @TableField(value = "approver_id", updateStrategy = FieldStrategy.ALWAYS)
    private Long approverId;

    @TableField(value = "approver_name", updateStrategy = FieldStrategy.ALWAYS)
    private String approverName;

    @TableField("request_reason")
    private String requestReason;

    @TableField("request_status")
    private String requestStatus;

    @TableField(value = "decision_remark", updateStrategy = FieldStrategy.ALWAYS)
    private String decisionRemark;

    @TableField(value = "processed_at", updateStrategy = FieldStrategy.ALWAYS)
    private LocalDateTime processedAt;
}
