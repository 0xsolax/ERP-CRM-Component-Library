package com.qmy.zhongsheng.core.material.manager.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.core.material.dao.FabricDAO;
import com.qmy.zhongsheng.core.material.manager.FabricManager;
import com.qmy.zhongsheng.core.material.model.entity.FabricDO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static com.qmy.zhongsheng.common.utils.ValidityUtils.isNotBlank;
import static com.qmy.zhongsheng.common.utils.ValidityUtils.nonNull;

/**
 * @author AI Coding
 */
@Component
@RequiredArgsConstructor
public class FabricManagerImpl implements FabricManager {

    private final FabricDAO fabricDAO;

    @Override
    public Long saveOrUpdate(FabricDO row) {
        if (row.getId() == null) {
            fabricDAO.insert(row);
            return row.getId();
        }
        fabricDAO.updateById(row);
        return row.getId();
    }

    @Override
    public FabricDO getById(Long id) {
        return fabricDAO.selectById(id);
    }

    @Override
    public Page<FabricDO> page(Long typeId, Long modelId, Long widthId, String keywords, Integer pageNum, Integer pageSize) {
        return fabricDAO.selectPage(
                new Page<>(pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 10),
                Wrappers.<FabricDO>lambdaQuery()
                        .eq(nonNull(typeId), FabricDO::getTypeId, typeId)
                        .eq(nonNull(modelId), FabricDO::getModelId, modelId)
                        .eq(nonNull(widthId), FabricDO::getWidthId, widthId)
                        .eq(FabricDO::getIsDeleted, 0)
                        .and(isNotBlank(keywords), wrapper -> wrapper
                                .like(FabricDO::getTypeName, keywords)
                                .or()
                                .like(FabricDO::getModelName, keywords)
                                .or()
                                .like(FabricDO::getWidthName, keywords))
                        .orderByDesc(FabricDO::getId));
    }

    @Override
    public List<FabricDO> list(Long typeId, Long modelId) {
        return fabricDAO.selectList(
                Wrappers.<FabricDO>lambdaQuery()
                        .eq(nonNull(typeId), FabricDO::getTypeId, typeId)
                        .eq(nonNull(modelId), FabricDO::getModelId, modelId)
                        .eq(FabricDO::getIsDeleted, 0)
                        .orderByDesc(FabricDO::getId));
    }

    @Override
    public Boolean deleted(Long id) {
        return fabricDAO.update(Wrappers.<FabricDO>lambdaUpdate()
                .eq(FabricDO::getId, id)
                .set(FabricDO::getIsDeleted, 1)
                .set(FabricDO::getDeletedTime, LocalDateTime.now())) > 0;
    }

    @Override
    public FabricDO getByDimensionCombination(Long typeId, Long modelId, Long widthId) {
        return fabricDAO.selectOne(Wrappers.<FabricDO>lambdaQuery()
                .eq(FabricDO::getIsDeleted, 0)
                .eq(FabricDO::getTypeId, typeId)
                .eq(FabricDO::getModelId, modelId)
                .eq(FabricDO::getWidthId, widthId)
                .last("LIMIT 1"));
    }
}