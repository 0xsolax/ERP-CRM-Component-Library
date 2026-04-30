package com.qmy.zhongsheng.core.material.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.qmy.zhongsheng.core.material.dao.MaterialCategoryDAO;
import com.qmy.zhongsheng.core.material.manager.MaterialCategoryManager;
import com.qmy.zhongsheng.core.material.model.entity.MaterialCategoryDO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author AI Coding
 */
@Component
@RequiredArgsConstructor
public class MaterialCategoryManagerImpl implements MaterialCategoryManager {

    private final MaterialCategoryDAO materialCategoryDAO;

    @Override
    public Long saveOrUpdate(MaterialCategoryDO row) {
        if (row.getId() == null) {
            materialCategoryDAO.insert(row);
            return row.getId();
        }
        materialCategoryDAO.updateById(row);
        return row.getId();
    }

    @Override
    public MaterialCategoryDO getById(Long id) {
        return materialCategoryDAO.selectById(id);
    }

    @Override
    public List<MaterialCategoryDO> listAll() {
        return materialCategoryDAO.selectList(Wrappers.<MaterialCategoryDO>lambdaQuery()
                .eq(MaterialCategoryDO::getIsDeleted, 0)
                .orderByAsc(MaterialCategoryDO::getSortNum)
                .orderByAsc(MaterialCategoryDO::getId));
    }

    @Override
    public List<MaterialCategoryDO> listByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return List.of();
        }
        return materialCategoryDAO.selectBatchIds(ids);
    }

    @Override
    public List<MaterialCategoryDO> list(LambdaQueryWrapper<MaterialCategoryDO> wrapper) {
        return materialCategoryDAO.selectList(wrapper);
    }

    @Override
    public List<MaterialCategoryDO> listByLikeName(String likeName) {
        return materialCategoryDAO.selectList(Wrappers.<MaterialCategoryDO>lambdaQuery()
                .eq(MaterialCategoryDO::getIsDeleted, 0)
                .like(likeName != null && !likeName.isEmpty(), MaterialCategoryDO::getName, likeName)
                .orderByAsc(MaterialCategoryDO::getSortNum)
                .orderByAsc(MaterialCategoryDO::getId));
    }

    @Override
    public Boolean delete(Long id) {
        LambdaUpdateWrapper<MaterialCategoryDO> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(MaterialCategoryDO::getId, id);
        wrapper.set(MaterialCategoryDO::getIsDeleted, 1);
        wrapper.set(MaterialCategoryDO::getDeletedTime, LocalDateTime.now());
        materialCategoryDAO.update(wrapper);
        return Boolean.TRUE;
    }

    @Override
    public int countAll() {
        return Math.toIntExact(materialCategoryDAO.selectCount(
                Wrappers.<MaterialCategoryDO>lambdaQuery()
                        .eq(MaterialCategoryDO::getIsDeleted, 0)));
    }

    @Override
    public void shiftSortNum(int fromSortNum, int toSortNum, int delta) {
        if (fromSortNum > toSortNum || delta == 0) {
            return;
        }
        materialCategoryDAO.update(Wrappers.<MaterialCategoryDO>lambdaUpdate()
                .eq(MaterialCategoryDO::getIsDeleted, 0)
                .ge(MaterialCategoryDO::getSortNum, fromSortNum)
                .le(MaterialCategoryDO::getSortNum, toSortNum)
                .setSql("sort_num = sort_num + (" + delta + ")"));
    }

    @Override
    public MaterialCategoryDO getByName(String name) {
        return materialCategoryDAO.selectOne(Wrappers.<MaterialCategoryDO>lambdaQuery()
                .eq(MaterialCategoryDO::getIsDeleted, 0)
                .eq(MaterialCategoryDO::getName, name)
                .last("LIMIT 1"));
    }
}