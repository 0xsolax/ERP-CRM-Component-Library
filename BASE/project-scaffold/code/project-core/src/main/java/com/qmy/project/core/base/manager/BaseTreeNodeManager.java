package com.qmy.project.core.base.manager;

import com.qmy.project.core.base.model.entity.BaseTreeNodeDO;

import java.util.List;
import java.util.Map;

/**
 * 基础树节点查询与存在性校验。
 *
 * @author AI Coding
 */
public interface BaseTreeNodeManager {

    /**
     * 按 id 查询。
     *
     * @param id 主键
     * @return 实体或 null
     */
    BaseTreeNodeDO getById(Long id);

    /**
     * 按 id 集合查询。
     *
     * @param ids 主键集合
     * @return 列表（顺序：业务类型、父 id、排序、id）
     */
    List<BaseTreeNodeDO> listByIds(List<Long> ids);

    /**
     * 查询全部节点（配置列表、下拉等）。
     *
     * @return 全部未删除节点
     */
    List<BaseTreeNodeDO> listAll();

    /**
     * 是否存在以指定 id 为父节点的子节点（用于判断是否为叶子节点）。
     *
     * @param parentId 父节点主键
     * @return 存在未删除子节点时为 true
     */
    boolean existsChildByParentId(Long parentId);

    /**
     * 通过 seedKey 批量查询节点ID映射。
     *
     * @param seedKeys 种子唯一标识列表
     * @return seedKey -> nodeId 映射
     */
    Map<String, Long> getNodeIdMapBySeedKeys(List<String> seedKeys);

    /**
     * 按业务类型查询节点列表。
     *
     * @param bizType 业务类型值（对应 BaseTreeBizTypeEnum.value）
     * @return 该业务类型下的全部节点
     */
    List<BaseTreeNodeDO> listByBizType(String bizType);

    /**
     * 按节点 key 查询节点。
     *
     * @param nodeKey 节点 key
     * @return 节点
     */
    BaseTreeNodeDO getByNodeKey(String nodeKey);
}
