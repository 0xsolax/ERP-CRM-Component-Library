package com.qmy.zhongsheng.core.material.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.core.material.model.entity.UmbrellaFrameDO;

import java.util.List;

/**
 * 伞架持久化与查询。
 *
 * @author AI Coding
 */
public interface UmbrellaFrameManager {

    /**
     * 单条保存或更新：{@code id} 为 {@code null} 时插入，否则按主键更新。
     *
     * @param row 待持化的实体
     * @return 记录主键 id
     */
    Long saveOrUpdate(UmbrellaFrameDO row);

    /**
     * 按 id 查询。
     *
     * @param id 主键 id
     * @return 匹配的记录
     */
    UmbrellaFrameDO getById(Long id);

    /**
     * 分页查询：支持功能、类型、尺寸、材料筛选。
     *
     * @param functionId 功能ID，为 null 时不加该条件
     * @param typeId 类型ID，为 null 时不加该条件
     * @param lengthId 伞架长度ID，为 null 时不加该条件
     * @param diameterId 中棒直径ID，为 null 时不加该条件
     * @param ribCountId 伞骨数量ID，为 null 时不加该条件
     * @param materialId 材料ID，为 null 时不加该条件
     * @param keywords 关键词，对6个名称快照字段模糊匹配
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    Page<UmbrellaFrameDO> page(Long functionId, Long typeId, Long lengthId, Long diameterId, Long ribCountId, Long materialId, String keywords, Integer pageNum, Integer pageSize);

    /**
     * 删除伞架（逻辑删除）。
     *
     * @param id 伞架 ID
     * @return 是否删除成功
     */
    Boolean deleted(Long id);

    /**
     * 判断是否存在引用了指定基础数据 ID 的伞架记录（功能）。
     *
     * @param functionId 基础数据 ID
     * @return 是否存在引用记录
     */
    boolean existsByFunctionId(Long functionId);

    /**
     * 判断是否存在引用了指定基础数据 ID 的伞架记录（类型）。
     *
     * @param typeId 基础数据 ID
     * @return 是否存在引用记录
     */
    boolean existsByTypeId(Long typeId);

    /**
     * 判断是否存在引用了指定基础数据 ID 的伞架记录（伞架长度）。
     *
     * @param lengthId 基础数据 ID
     * @return 是否存在引用记录
     */
    boolean existsByLengthId(Long lengthId);

    /**
     * 判断是否存在引用了指定基础数据 ID 的伞架记录（中棒直径）。
     *
     * @param diameterId 基础数据 ID
     * @return 是否存在引用记录
     */
    boolean existsByDiameterId(Long diameterId);

    /**
     * 判断是否存在引用了指定基础数据 ID 的伞架记录（伞骨数量）。
     *
     * @param ribCountId 基础数据 ID
     * @return 是否存在引用记录
     */
    boolean existsByRibCountId(Long ribCountId);

    /**
     * 判断是否存在引用了指定基础数据 ID 的伞架记录（材料）。
     *
     * @param materialId 基础数据 ID
     * @return 是否存在引用记录
     */
    boolean existsByMaterialId(Long materialId);

    /**
     * 查询伞架列表（用于下拉框选择）；各筛选条件为 {@code null} 时不生效。
     *
     * @param functionId 功能ID，为 null 时不加该条件
     * @param typeId 类型ID，为 null 时不加该条件
     * @param lengthId 伞架长度ID，为 null 时不加该条件
     * @param diameterId 中棒直径ID，为 null 时不加该条件
     * @param ribCountId 伞骨数量ID，为 null 时不加该条件
     * @param materialId 材料ID，为 null 时不加该条件
     * @return 伞架列表
     */
    List<UmbrellaFrameDO> listByCondition(Long functionId, Long typeId, Long lengthId, Long diameterId, Long ribCountId, Long materialId);

    /**
     * 按维度组合查询伞架（用于重复性校验）
     *
     * @param functionId 功能 ID
     * @param typeId 类型 ID
     * @param lengthId 伞架长度 ID
     * @param diameterId 中棒直径 ID
     * @param ribCountId 伞骨数量 ID
     * @param materialId 材料 ID
     * @return 匹配的记录
     */
    UmbrellaFrameDO getByDimensionCombination(Long functionId, Long typeId, Long lengthId, Long diameterId, Long ribCountId, Long materialId);
}