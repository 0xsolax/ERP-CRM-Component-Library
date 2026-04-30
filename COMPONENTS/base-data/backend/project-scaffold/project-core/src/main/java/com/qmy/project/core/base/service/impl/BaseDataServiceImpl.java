package com.qmy.project.core.base.service.impl;

import com.qmy.project.api.dto.base.BaseDataListQueryDTO;
import com.qmy.project.api.dto.base.BaseDataSaveDTO;
import com.qmy.project.common.enums.BaseTreeBizTypeEnum;
import com.qmy.project.common.error.BaseDataErrorCodeConstants;
import com.qmy.project.common.exception.ServiceExceptionUtil;
import com.qmy.project.common.utils.BeanUtils;
import com.qmy.project.core.base.manager.BaseDataManager;
import com.qmy.project.core.base.manager.BaseTreeNodeManager;
import com.qmy.project.core.base.model.entity.BaseDataDO;
import com.qmy.project.core.base.model.entity.BaseTreeNodeDO;
import com.qmy.project.core.base.model.vo.BaseDataVO;
import com.qmy.project.core.base.model.vo.BaseTreeNodeVO;
import com.qmy.project.core.base.service.BaseDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author AI Coding
 */
@Service
@RequiredArgsConstructor
public class BaseDataServiceImpl implements BaseDataService {
    private final BaseDataManager baseDataManager;
    private final BaseTreeNodeManager baseTreeNodeManager;

    @Override
    @Transactional
    public Long saveOrUpdate(BaseDataSaveDTO dto) {
        BaseDataDO existing = null;
        if (dto.getId() == null) {
            validateNodeIdRequired(dto.getNodeId());
            ensureTreeNodeAllowsDataBind(dto.getNodeId());
        } else {
            existing = baseDataManager.getById(dto.getId());
            if (dto.getNodeId() != null) {
                ensureTreeNodeAllowsDataBind(dto.getNodeId());
            }
        }
        BaseDataDO row = BeanUtils.toBean(dto, BaseDataDO.class);
        if (dto.getId() != null && dto.getNodeId() == null && existing != null) {
            row.setNodeId(existing.getNodeId());
        }
        return baseDataManager.saveOrUpdate(row);
    }

    @Override
    public List<BaseDataVO> list(BaseDataListQueryDTO query) {
        List<Long> nodeIds = query == null ? null : query.getNodeIds();
        List<BaseDataDO> rows = baseDataManager.listByNodeIds(nodeIds);
        List<Long> idList = rows.stream()
                .map(BaseDataDO::getNodeId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        List<BaseTreeNodeDO> nodes = baseTreeNodeManager.listByIds(idList);
        Map<Long, BaseTreeNodeDO> nodeMap = nodes.stream()
                .collect(Collectors.toMap(BaseTreeNodeDO::getId, n -> n, (a, b) -> a));
        return rows.stream()
                .map(row -> toVo(row, nodeMap.get(row.getNodeId())))
                .toList();
    }

    @Override
    public List<BaseDataVO> listByNodeKey(String nodeKey) {
        if (!StringUtils.hasText(nodeKey)) {
            return List.of();
        }
        BaseTreeNodeDO nodeDO = baseTreeNodeManager.getByNodeKey(nodeKey);
        if (nodeDO == null) {
            return List.of();
        }
        List<BaseDataDO> baseDataDOList = baseDataManager.listByNodeId(nodeDO.getId());
        List<BaseDataVO> resultVOList = BeanUtils.toBean(baseDataDOList, BaseDataVO.class);
        for (BaseDataVO resultVO : resultVOList) {
            resultVO.setNodeName(nodeDO.getName());
            resultVO.setNodeId(nodeDO.getId());
            resultVO.setNodeName(nodeDO.getName());
            resultVO.setNodeKey(nodeDO.getNodeKey());
            resultVO.setBizType(nodeDO.getBizType());
        }
        return resultVOList;
    }

    @Override
    public List<BaseTreeNodeVO> listTreeNodesByBizType(String bizType) {
        List<BaseTreeNodeDO> rows = baseTreeNodeManager.listByBizType(bizType);
        return BeanUtils.toBean(rows, BaseTreeNodeVO.class);
    }

    private static BaseDataVO toVo(BaseDataDO row, BaseTreeNodeDO node) {
        BaseDataVO vo = BeanUtils.toBean(row, BaseDataVO.class);
        if (node != null) {
            vo.setBizType(node.getBizType());
            vo.setNodeName(node.getName());
            vo.setNodeKey(node.getNodeKey());
        }
        return vo;
    }

    private static void validateNodeIdRequired(Long nodeId) {
        if (nodeId == null) {
            throw ServiceExceptionUtil.exception(BaseDataErrorCodeConstants.BASE_DATA_TREE_NODE_INVALID);
        }
    }

    private void ensureTreeNodeAllowsDataBind(Long nodeId) {
        BaseTreeNodeDO node = baseTreeNodeManager.getById(nodeId);
        if (node == null) {
            throw ServiceExceptionUtil.exception(BaseDataErrorCodeConstants.BASE_DATA_TREE_NODE_INVALID);
        }
        if (!Integer.valueOf(1).equals(node.getDataBindFlag())) {
            throw ServiceExceptionUtil.exception(BaseDataErrorCodeConstants.BASE_DATA_TREE_NODE_DATA_BIND_NOT_ALLOWED);
        }
        BaseTreeBizTypeEnum bizType = BaseTreeBizTypeEnum.fromValue(node.getBizType());
        if (bizType != null && bizType.isLeafOnlyDataBind() && baseTreeNodeManager.existsChildByParentId(nodeId)) {
            throw ServiceExceptionUtil.exception(BaseDataErrorCodeConstants.BASE_DATA_TREE_NODE_DATA_BIND_NOT_ALLOWED);
        }
    }

    @Override
    public Boolean delete(Long id) {
        return baseDataManager.deleted(id);
    }
}
