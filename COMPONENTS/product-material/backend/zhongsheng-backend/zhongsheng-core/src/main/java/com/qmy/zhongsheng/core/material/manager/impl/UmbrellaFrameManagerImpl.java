package com.qmy.zhongsheng.core.material.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.common.utils.ValidityUtils;
import com.qmy.zhongsheng.core.material.dao.UmbrellaFrameDAO;
import com.qmy.zhongsheng.core.material.manager.UmbrellaFrameManager;
import com.qmy.zhongsheng.core.material.model.entity.UmbrellaFrameDO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static com.qmy.zhongsheng.common.utils.ValidityUtils.nonNull;

/**
 * @author AI Coding
 */
@Component
@RequiredArgsConstructor
public class UmbrellaFrameManagerImpl implements UmbrellaFrameManager {

    private final UmbrellaFrameDAO umbrellaFrameDAO;

    @Override
    public Long saveOrUpdate(UmbrellaFrameDO row) {
        if (row.getId() == null) {
            umbrellaFrameDAO.insert(row);
            return row.getId();
        }
        umbrellaFrameDAO.updateById(row);
        return row.getId();
    }

    @Override
    public UmbrellaFrameDO getById(Long id) {
        return umbrellaFrameDAO.selectById(id);
    }

    @Override
    public Page<UmbrellaFrameDO> page(Long functionId, Long typeId, Long lengthId, Long diameterId, Long ribCountId, Long materialId, String keywords, Integer pageNum, Integer pageSize) {
        return umbrellaFrameDAO.selectPage(
                new Page<>(pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 10),
                buildListQueryWrapper(functionId, typeId, lengthId, diameterId, ribCountId, materialId, keywords));
    }

    @Override
    public Boolean deleted(Long id) {
        return umbrellaFrameDAO.update(Wrappers.<UmbrellaFrameDO>lambdaUpdate()
                .eq(UmbrellaFrameDO::getId, id)
                .set(UmbrellaFrameDO::getIsDeleted, 1)
                .set(UmbrellaFrameDO::getDeletedTime, LocalDateTime.now())) > 0;
    }

    @Override
    public boolean existsByFunctionId(Long functionId) {
        if (functionId == null) {
            return false;
        }
        return umbrellaFrameDAO.selectCount(
                Wrappers.<UmbrellaFrameDO>lambdaQuery()
                        .eq(UmbrellaFrameDO::getIsDeleted, 0)
                        .eq(UmbrellaFrameDO::getFunctionId, functionId)
        ) > 0;
    }

    @Override
    public boolean existsByTypeId(Long typeId) {
        if (typeId == null) {
            return false;
        }
        return umbrellaFrameDAO.selectCount(
                Wrappers.<UmbrellaFrameDO>lambdaQuery()
                        .eq(UmbrellaFrameDO::getIsDeleted, 0)
                        .eq(UmbrellaFrameDO::getTypeId, typeId)
        ) > 0;
    }

    @Override
    public boolean existsByLengthId(Long lengthId) {
        if (lengthId == null) {
            return false;
        }
        return umbrellaFrameDAO.selectCount(
                Wrappers.<UmbrellaFrameDO>lambdaQuery()
                        .eq(UmbrellaFrameDO::getIsDeleted, 0)
                        .eq(UmbrellaFrameDO::getLengthId, lengthId)
        ) > 0;
    }

    @Override
    public boolean existsByDiameterId(Long diameterId) {
        if (diameterId == null) {
            return false;
        }
        return umbrellaFrameDAO.selectCount(
                Wrappers.<UmbrellaFrameDO>lambdaQuery()
                        .eq(UmbrellaFrameDO::getIsDeleted, 0)
                        .eq(UmbrellaFrameDO::getDiameterId, diameterId)
        ) > 0;
    }

    @Override
    public boolean existsByRibCountId(Long ribCountId) {
        if (ribCountId == null) {
            return false;
        }
        return umbrellaFrameDAO.selectCount(
                Wrappers.<UmbrellaFrameDO>lambdaQuery()
                        .eq(UmbrellaFrameDO::getIsDeleted, 0)
                        .eq(UmbrellaFrameDO::getRibCountId, ribCountId)
        ) > 0;
    }

    @Override
    public boolean existsByMaterialId(Long materialId) {
        if (materialId == null) {
            return false;
        }
        return umbrellaFrameDAO.selectCount(
                Wrappers.<UmbrellaFrameDO>lambdaQuery()
                        .eq(UmbrellaFrameDO::getIsDeleted, 0)
                        .eq(UmbrellaFrameDO::getMaterialId, materialId)
        ) > 0;
    }

    @Override
    public List<UmbrellaFrameDO> listByCondition(Long functionId, Long typeId, Long lengthId, Long diameterId, Long ribCountId, Long materialId) {
        return umbrellaFrameDAO.selectList(buildListQueryWrapper(functionId, typeId, lengthId, diameterId, ribCountId, materialId, null));
    }

    @Override
    public UmbrellaFrameDO getByDimensionCombination(Long functionId, Long typeId, Long lengthId, Long diameterId, Long ribCountId, Long materialId) {
        return umbrellaFrameDAO.selectOne(Wrappers.<UmbrellaFrameDO>lambdaQuery()
                .eq(UmbrellaFrameDO::getIsDeleted, 0)
                .eq(UmbrellaFrameDO::getFunctionId, functionId)
                .eq(UmbrellaFrameDO::getTypeId, typeId)
                .eq(UmbrellaFrameDO::getLengthId, lengthId)
                .eq(UmbrellaFrameDO::getDiameterId, diameterId)
                .eq(UmbrellaFrameDO::getRibCountId, ribCountId)
                .eq(UmbrellaFrameDO::getMaterialId, materialId)
                .last("LIMIT 1"));
    }

    private LambdaQueryWrapper<UmbrellaFrameDO> buildListQueryWrapper(Long functionId, Long typeId, Long lengthId, Long diameterId, Long ribCountId, Long materialId, String keywords) {
        return Wrappers.<UmbrellaFrameDO>lambdaQuery()
                .eq(UmbrellaFrameDO::getIsDeleted, 0)
                .eq(nonNull(functionId), UmbrellaFrameDO::getFunctionId, functionId)
                .eq(nonNull(typeId), UmbrellaFrameDO::getTypeId, typeId)
                .eq(nonNull(lengthId), UmbrellaFrameDO::getLengthId, lengthId)
                .eq(nonNull(diameterId), UmbrellaFrameDO::getDiameterId, diameterId)
                .eq(nonNull(ribCountId), UmbrellaFrameDO::getRibCountId, ribCountId)
                .eq(nonNull(materialId), UmbrellaFrameDO::getMaterialId, materialId)
                .and(ValidityUtils.isNotBlank(keywords), wrapper -> wrapper
                        .like(UmbrellaFrameDO::getFunctionName, keywords)
                        .or()
                        .like(UmbrellaFrameDO::getTypeName, keywords)
                        .or()
                        .like(UmbrellaFrameDO::getLengthName, keywords)
                        .or()
                        .like(UmbrellaFrameDO::getDiameterName, keywords)
                        .or()
                        .like(UmbrellaFrameDO::getRibCountName, keywords)
                        .or()
                        .like(UmbrellaFrameDO::getMaterialName, keywords)
                        .or().like(UmbrellaFrameDO::getSpecificAttribute, keywords))
                .orderByDesc(UmbrellaFrameDO::getId);
    }
}