package com.qmy.zhongsheng.core.document.model.condition;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 公共单据动作日志查询条件。
 *
 * @author AI Coding
 */
@Data
public class DocumentActionLogQueryCondition {

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    private String documentType;

    private Long documentId;

    private List<String> documentTypes;

    private Long operatorId;

    private String operatorKeyword;

    private String actionType;

    private String keyword;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
