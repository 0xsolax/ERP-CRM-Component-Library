package com.qmy.project.core.base.manager.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.qmy.project.core.base.dao.BaseTreeNodeDAO;
import com.qmy.project.core.base.manager.BaseTreeNodeManager;
import com.qmy.project.core.base.model.entity.BaseTreeNodeDO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author AI Coding
 */
@Component
@RequiredArgsConstructor
public class BaseTreeNodeManagerImpl implements BaseTreeNodeManager {

    private final BaseTreeNodeDAO baseTreeNodeDAO;

    @Override
    public BaseTreeNodeDO getById(Long id) {
        return baseTreeNodeDAO.selectById(id);
    }

    @Override
    public List<BaseTreeNodeDO> listByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return List.of();
        }
        return baseTreeNodeDAO.selectList(Wrappers.<BaseTreeNodeDO>lambdaQuery()
                .in(BaseTreeNodeDO::getId, ids)
                .eq(BaseTreeNodeDO::getIsDeleted, 0)
                .orderByAsc(BaseTreeNodeDO::getBizType)
                .orderByAsc(BaseTreeNodeDO::getParentId)
                .orderByAsc(BaseTreeNodeDO::getSortNum)
                .orderByAsc(BaseTreeNodeDO::getId));
    }

    @Override
    public List<BaseTreeNodeDO> listAll() {
        return baseTreeNodeDAO.selectList(Wrappers.<BaseTreeNodeDO>lambdaQuery()
                .orderByAsc(BaseTreeNodeDO::getBizType)
                .orderByAsc(BaseTreeNodeDO::getParentId)
                .orderByAsc(BaseTreeNodeDO::getSortNum)
                .orderByAsc(BaseTreeNodeDO::getId));
    }

    @Override
    public boolean existsChildByParentId(Long parentId) {
        if (parentId == null) {
            return false;
        }
        return baseTreeNodeDAO.selectCount(Wrappers.<BaseTreeNodeDO>lambdaQuery()
                .eq(BaseTreeNodeDO::getParentId, parentId)
                .last("LIMIT 1")) > 0;
    }

    @Override
    public Map<String, Long> getNodeIdMapBySeedKeys(List<String> seedKeys) {
        if (CollectionUtils.isEmpty(seedKeys)) {
            return Map.of();
        }

        // 通过 nodeKey 批量查询节点
        List<BaseTreeNodeDO> nodes = baseTreeNodeDAO.selectList(Wrappers.<BaseTreeNodeDO>lambdaQuery()
                .in(BaseTreeNodeDO::getNodeKey, seedKeys));

        // 构建 nodeKey -> nodeId 映射
        Map<String, Long> result = new HashMap<>();
        for (BaseTreeNodeDO node : nodes) {
            if (node.getNodeKey() != null) {
                result.put(node.getNodeKey(), node.getId());
            }
        }

        return result;
    }

    @Override
    public List<BaseTreeNodeDO> listByBizType(String bizType) {
        if (bizType == null || bizType.isBlank()) {
            return listAll();
        }
        return baseTreeNodeDAO.selectList(Wrappers.<BaseTreeNodeDO>lambdaQuery()
                .eq(BaseTreeNodeDO::getBizType, bizType.trim())
                .orderByAsc(BaseTreeNodeDO::getParentId)
                .orderByAsc(BaseTreeNodeDO::getSortNum)
                .orderByAsc(BaseTreeNodeDO::getId));
    }

    @Override
    public BaseTreeNodeDO getByNodeKey(String nodeKey) {
        return baseTreeNodeDAO.selectOne(Wrappers.<BaseTreeNodeDO>lambdaQuery()
                .eq(BaseTreeNodeDO::getNodeKey, nodeKey));
    }
}
