package com.qmy.zhongsheng.core.material.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qmy.zhongsheng.core.material.model.entity.MaterialCategoryDO;

import java.util.List;

/**
 * @author AI Coding
 */
public interface MaterialCategoryManager {

    Long saveOrUpdate(MaterialCategoryDO row);

    MaterialCategoryDO getById(Long id);

    List<MaterialCategoryDO> listAll();

    List<MaterialCategoryDO> listByIds(List<Long> ids);

    List<MaterialCategoryDO> list(LambdaQueryWrapper<MaterialCategoryDO> wrapper);

    List<MaterialCategoryDO> listByLikeName(String likeName);

    Boolean delete(Long id);

    /**
     * 统计未删除的分类总数。
     *
     * @return 分类数量
     */
    int countAll();

    /**
     * 批量调整排序号：将 sortNum 在 [fromSortNum, toSortNum] 范围内的记录的排序号加上 delta。
     *
     * @param fromSortNum 起始排序号（含）
     * @param toSortNum   结束排序号（含）
     * @param delta       偏移量，正数上移、负数下移
     */
    void shiftSortNum(int fromSortNum, int toSortNum, int delta);

    /**
     * 按名称查询材料分类（用于重复性校验）
     *
     * @param name 分类名称
     * @return 匹配的记录
     */
    MaterialCategoryDO getByName(String name);
}