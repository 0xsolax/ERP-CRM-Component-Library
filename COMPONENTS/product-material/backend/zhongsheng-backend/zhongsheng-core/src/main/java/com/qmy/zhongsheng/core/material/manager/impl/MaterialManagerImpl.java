package com.qmy.zhongsheng.core.material.manager.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.core.material.dao.MaterialDAO;
import com.qmy.zhongsheng.core.material.manager.MaterialManager;
import com.qmy.zhongsheng.core.material.model.entity.MaterialDO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.qmy.zhongsheng.common.utils.ValidityUtils.isNotBlank;

/**
 * @author AI Coding
 */
@Component
@RequiredArgsConstructor
public class MaterialManagerImpl implements MaterialManager {

    private final MaterialDAO materialDAO;

    @Override
    public Long saveOrUpdate(MaterialDO row) {
        if (row.getId() == null) {
            materialDAO.insert(row);
            return row.getId();
        }
        materialDAO.updateById(row);
        return row.getId();
    }

    @Override
    public MaterialDO getById(Long id) {
        return materialDAO.selectById(id);
    }

    @Override
    public Page<MaterialDO> page(Long categoryId, String likeName, String likeSize, Integer pageNum, Integer pageSize) {
        return materialDAO.selectPage(
                new Page<>(pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 10),
                Wrappers.<MaterialDO>lambdaQuery()
                        .eq(categoryId != null, MaterialDO::getCategoryId, categoryId)
                        .eq(MaterialDO::getIsDeleted, 0).
                        like(isNotBlank(likeName), MaterialDO::getName, likeName).
                        like(isNotBlank(likeSize), MaterialDO::getSize, likeSize)
                        .orderByDesc(MaterialDO::getId));
    }

    @Override
    public List<MaterialDO> listByCategoryIds(List<Long> categoryIds) {
        if (CollectionUtils.isEmpty(categoryIds)) {
            return List.of();
        }
        return materialDAO.selectList(Wrappers.<MaterialDO>lambdaQuery()
                .in(MaterialDO::getCategoryId, categoryIds)
                .eq(MaterialDO::getIsDeleted, 0)
                .orderByDesc(MaterialDO::getId));
    }

    @Override
    public List<MaterialDO> listByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return List.of();
        }
        return materialDAO.selectBatchIds(ids);
    }

    @Override
    public List<MaterialDO> listByCategoryId(Long id) {
        return materialDAO.selectList(Wrappers.<MaterialDO>lambdaQuery()
                .eq(MaterialDO::getCategoryId, id)
                .eq(MaterialDO::getIsDeleted, 0)
                .orderByDesc(MaterialDO::getId));
    }

    @Override
    public Boolean deleted(Long id) {
        return materialDAO.update(Wrappers.<MaterialDO>lambdaUpdate()
                .eq(MaterialDO::getId, id)
                .set(MaterialDO::getIsDeleted, 1)
                .set(MaterialDO::getDeletedTime, LocalDateTime.now())) > 0;
    }

    @Override
    public MaterialDO getByCategoryIdAndName(Long categoryId, String name) {
        return materialDAO.selectOne(Wrappers.<MaterialDO>lambdaQuery()
                .eq(MaterialDO::getIsDeleted, 0)
                .eq(MaterialDO::getCategoryId, categoryId)
                .eq(MaterialDO::getName, name)
                .last("LIMIT 1"));
    }
}