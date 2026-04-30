package com.qmy.zhongsheng.core.material.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.core.material.model.entity.FabricDO;

import java.util.List;

/**
 * 面料持久化与查询。
 *
 * @author AI Coding
 */
public interface FabricManager {

    /**
     * 单条保存或更新：{@code id} 为 {@code null} 时插入，否则按主键更新。
     *
     * @param row 待持久化的实体
     * @return 记录主键 id
     */
    Long saveOrUpdate(FabricDO row);

    /**
     * 按 id 查询。
     *
     * @param id 主键 id
     * @return 匹配的记录
     */
    FabricDO getById(Long id);

    /**
     * 分页查询：支持种类、型号、门幅筛选。
     *
     * @param typeId 种类ID（baseDataId），为 null 时不加该条件
     * @param modelId 型号ID（baseDataId），为 null 时不加该条件
     * @param widthId 门幅ID（baseDataId），为 null 时不加该条件
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    Page<FabricDO> page(Long typeId, Long modelId, Long widthId, String keywords, Integer pageNum, Integer pageSize);

    /**
     * 列表查询（不分页）：按种类、型号筛选，用于下拉等场景；参数为 {@code null} 时不加对应条件。
     *
     * @param typeId 种类ID（baseDataId）
     * @param modelId 型号ID（baseDataId）
     * @return 未删除的面料列表，按 id 降序
     */
    List<FabricDO> list(Long typeId, Long modelId);

    /**
     * 删除
     *
     * @param id 主键 id
     * @return 删除结果
     */
    Boolean deleted(Long id);

    /**
     * 按维度组合查询面料（用于重复性校验）
     *
     * @param typeId 种类 ID
     * @param modelId 型号 ID
     * @param widthId 门幅 ID
     * @return 匹配的记录
     */
    FabricDO getByDimensionCombination(Long typeId, Long modelId, Long widthId);
}