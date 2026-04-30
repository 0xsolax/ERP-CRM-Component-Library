package com.qmy.project.core.base.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qmy.project.common.enums.BaseTreeNodeSeedEnum;
import com.qmy.project.infrastructure.model.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 基础树节点表 {@code base_tree_node}（字段管理、分类等共用一棵树，{@code biz_type} 区分）。
 *
 * @author AI Coding
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("base_tree_node")
public class BaseTreeNodeDO extends BaseDO {

    /**
     * 业务类型，与 {@link com.qmy.project.common.enums.BaseTreeBizTypeEnum#getValue()} 一致的大写英文。
     */
    @TableField("biz_type")
    private String bizType;

    /**
     * 父节点 id，根节点为 0。
     */
    @TableField("parent_id")
    private Long parentId;

    @TableField("name")
    private String name;

    @TableField("level")
    private Integer level;

    @TableField("sort_num")
    private Integer sortNum;

    /**
     * 节点唯一标识（对应 {@link BaseTreeNodeSeedEnum#getNodeKey()}）。
     * 用于前后端交互时标识节点，例如：FABRIC_TYPE 等。
     */
    @TableField("node_key")
    private String nodeKey;

    /**
     * 是否允许绑定 {@code base_data}：0 不允许，1 允许。
     * 当业务类型 {@link com.qmy.project.common.enums.BaseTreeBizTypeEnum#isLeafOnlyDataBind()} 为 true 时，仅叶子节点可为 1。
     */
    @TableField("data_bind_flag")
    private Integer dataBindFlag;
}
