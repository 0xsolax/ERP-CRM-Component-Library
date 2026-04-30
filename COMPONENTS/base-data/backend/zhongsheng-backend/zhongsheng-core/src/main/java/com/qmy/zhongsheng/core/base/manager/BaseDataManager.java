package com.qmy.zhongsheng.core.base.manager;

import com.qmy.zhongsheng.core.base.model.entity.BaseDataDO;

import java.util.List;

/**
 * 基础数据持久化与查询。
 *
 * @author AI Coding
 */
public interface BaseDataManager {

    /**
     * 单条保存或更新：{@code id} 为 {@code null} 时插入（插入后回填 id），否则按主键更新。
     *
     * @param row 待持久化的实体
     * @return 记录主键 id
     */
    Long saveOrUpdate(BaseDataDO row);

    /**
     * 按 id 查询
     *
     * @param id 主键 id
     * @return 匹配的记录
     */
    BaseDataDO getById(Long id);

    /**
     * 按 id 列表批量查询
     *
     * @param ids 主键 id 列表
     * @return 数据行列表
     */
    List<BaseDataDO> listByIds(List<Long> ids);

    /**
     * 按基础树节点 id 集合过滤；{@code nodeIds} 为 null 或空时不加该条件。
     *
     * @param nodeIds 树节点主键集合
     * @return 数据行列表
     */
    List<BaseDataDO> listByNodeIds(List<Long> nodeIds);

    /**
     * 按基础树节点列表
     * @param nodeId  树节点主键
     * @return 数据行列表
     */
    List<BaseDataDO> listByNodeId(Long nodeId);

    /**
     * 按树节点 id 和 value1 查询（用于重复性校验）
     *
     * @param nodeId 树节点主键
     * @param value1 值 1
     * @return 匹配的数据行
     */
    BaseDataDO getByNodeIdAndValue1(Long nodeId, String value1);

    /**
     * 按树节点 id、value1 和 value2 查询（用于面料用量重复性校验）
     *
     * @param nodeId 树节点主键
     * @param value1 值 1
     * @param value2 值 2
     * @return 匹配的数据行
     */
    BaseDataDO getByNodeIdAndValue1AndValue2(Long nodeId, String value1, String value2);

    /**
     * 删除
     * @param id 主键 id
     * @return 删除结果
     */
    Boolean deleted(Long id);
}
