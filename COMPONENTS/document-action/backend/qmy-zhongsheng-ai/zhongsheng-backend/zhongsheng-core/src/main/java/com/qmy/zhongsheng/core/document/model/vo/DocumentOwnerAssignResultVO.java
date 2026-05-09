package com.qmy.zhongsheng.core.document.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 公共单据负责人改派结果 VO。
 *
 * @author AI Coding
 */
@Data
@Schema(description = "公共单据负责人改派结果 VO")
public class DocumentOwnerAssignResultVO {

    private String documentType;

    private Long documentId;

    private Long ownerId;

    private String ownerName;

    private List<String> syncedDocuments = new ArrayList<>();
}
