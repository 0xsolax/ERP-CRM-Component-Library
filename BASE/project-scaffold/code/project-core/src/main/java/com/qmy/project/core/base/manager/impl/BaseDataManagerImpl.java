package com.qmy.project.core.base.manager.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.qmy.project.core.base.dao.BaseDataDAO;
import com.qmy.project.core.base.manager.BaseDataManager;
import com.qmy.project.core.base.model.entity.BaseDataDO;
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
public class BaseDataManagerImpl implements BaseDataManager {

    private final BaseDataDAO baseDataDAO;

    @Override
    public Long saveOrUpdate(BaseDataDO row) {
        if (row.getId() == null) {
            baseDataDAO.insert(row);
            return row.getId();
        }
        // 没有 @TableLogic 注解，直接 updateById 即可更新 isDeleted 字段
        baseDataDAO.updateById(row);
        return row.getId();
    }

    @Override
    public BaseDataDO getById(Long id) {
        return baseDataDAO.selectById(id);
    }

    @Override
    public List<BaseDataDO> listByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return List.of();
        }
        return baseDataDAO.selectBatchIds(ids);
    }

    @Override
    public List<BaseDataDO> listByNodeIds(List<Long> nodeIds) {
        return baseDataDAO.selectList(Wrappers.<BaseDataDO>lambdaQuery()
                .in(!CollectionUtils.isEmpty(nodeIds), BaseDataDO::getNodeId, nodeIds).eq(BaseDataDO::getIsDeleted, 0)
                .orderByAsc(BaseDataDO::getNodeId)
                .orderByAsc(BaseDataDO::getId));
    }

    @Override
    public List<BaseDataDO> listByNodeId(Long nodeId) {
        return baseDataDAO.selectList(Wrappers.<BaseDataDO>lambdaQuery().eq(BaseDataDO::getNodeId, nodeId).eq(BaseDataDO::getIsDeleted, 0).orderByAsc(BaseDataDO::getId));
    }

    @Override
    public BaseDataDO getByNodeIdAndValue1(Long nodeId, String value1) {
        return baseDataDAO.selectOne(Wrappers.<BaseDataDO>lambdaQuery().eq(BaseDataDO::getNodeId, nodeId).eq(BaseDataDO::getValue1, value1).eq(BaseDataDO::getIsDeleted, 0).last("LIMIT 1"));
    }

    @Override
    public Boolean deleted(Long id) {
        return baseDataDAO.update(Wrappers.<BaseDataDO>lambdaUpdate().eq(BaseDataDO::getId, id).set(BaseDataDO::getIsDeleted, 1).set(BaseDataDO::getDeletedTime, LocalDateTime.now())) > 0;
    }
}
