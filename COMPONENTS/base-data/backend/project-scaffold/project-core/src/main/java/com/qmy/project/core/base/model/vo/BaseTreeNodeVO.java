package com.qmy.project.core.base.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础树节点展示对象。
 *
 * @author AI Coding
 */
@Data
@Schema(description = "基础树节点")
public class BaseTreeNodeVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "业务类型：FIELD_MGMT 字段管理；CATEGORY 分类", example = "FIELD_MGMT")
    private String bizType;

    @Schema(description = "父节点 id，根为 0")
    private Long parentId;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "层级")
    private Integer level;

    @Schema(description = "排序号")
    private Integer sortNum;

    @Schema(description = "是否允许绑定基础数据：0 不允许，1 允许")
    private Integer dataBindFlag;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
