package com.qmy.zhongsheng.core.document.model.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qmy.zhongsheng.infrastructure.model.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 公共单据动作日志 DO。
 *
 * @author AI Coding
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("document_action_log")
public class DocumentActionLogDO extends BaseDO {

    @TableField("document_type")
    private String documentType;

    @TableField("document_id")
    private Long documentId;

    @TableField(value = "base_code", updateStrategy = FieldStrategy.ALWAYS)
    private String baseCode;

    @TableField(value = "serial_code", updateStrategy = FieldStrategy.ALWAYS)
    private String serialCode;

    @TableField("action_type")
    private String actionType;

    @TableField(value = "before_status", updateStrategy = FieldStrategy.ALWAYS)
    private String beforeStatus;

    @TableField(value = "after_status", updateStrategy = FieldStrategy.ALWAYS)
    private String afterStatus;

    @TableField(value = "before_lock_state", updateStrategy = FieldStrategy.ALWAYS)
    private String beforeLockState;

    @TableField(value = "after_lock_state", updateStrategy = FieldStrategy.ALWAYS)
    private String afterLockState;

    @TableField("operator_id")
    private Long operatorId;

    @TableField("operator_name")
    private String operatorName;

    @TableField(value = "action_reason", updateStrategy = FieldStrategy.ALWAYS)
    private String actionReason;

    @TableField(value = "diff_summary", updateStrategy = FieldStrategy.ALWAYS)
    private String diffSummary;

    @TableField(value = "diff_detail", updateStrategy = FieldStrategy.ALWAYS)
    private String diffDetail;
}
