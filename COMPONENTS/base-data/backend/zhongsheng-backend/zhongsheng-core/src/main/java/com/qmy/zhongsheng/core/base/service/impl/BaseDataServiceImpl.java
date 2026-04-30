package com.qmy.zhongsheng.core.base.service.impl;

import com.qmy.zhongsheng.api.dto.base.BaseDataListQueryDTO;
import com.qmy.zhongsheng.api.dto.base.BaseDataSaveDTO;
import com.qmy.zhongsheng.common.enums.BaseTreeBizTypeEnum;
import com.qmy.zhongsheng.common.enums.BaseTreeNodeSeedEnum;
import com.qmy.zhongsheng.common.error.BaseDataErrorCodeConstants;
import com.qmy.zhongsheng.common.exception.ServiceExceptionUtil;
import com.qmy.zhongsheng.common.utils.BeanUtils;
import com.qmy.zhongsheng.core.base.manager.BaseDataManager;
import com.qmy.zhongsheng.core.base.manager.BaseTreeNodeManager;
import com.qmy.zhongsheng.core.base.model.entity.BaseDataDO;
import com.qmy.zhongsheng.core.base.model.entity.BaseTreeNodeDO;
import com.qmy.zhongsheng.core.base.model.vo.BaseDataVO;
import com.qmy.zhongsheng.core.base.model.vo.BaseTreeNodeVO;
import com.qmy.zhongsheng.core.base.service.BaseDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.qmy.zhongsheng.common.constants.BaseDataConstants.DEFAULT_FLAG;
import static com.qmy.zhongsheng.common.utils.ValidityUtils.isNull;

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
            validBaseDataDO(existing);
            if (dto.getNodeId() != null) {
                ensureTreeNodeAllowsDataBind(dto.getNodeId());
            }
        }
        // 重复性校验：面料用量校验 value1+value2，其他校验 value1
        validateDuplicate(dto.getNodeId() != null ? dto.getNodeId() : (existing != null ? existing.getNodeId() : null),
                dto, dto.getId());

        BaseDataDO row = BeanUtils.toBean(dto, BaseDataDO.class);
        if (dto.getId() != null && dto.getNodeId() == null && existing != null) {
            row.setNodeId(existing.getNodeId());
        }
        return baseDataManager.saveOrUpdate(row);
    }

    /**
     * 重复性校验：面料用量校验 value1+value2，其他校验 value1
     *
     * @param nodeId 树节点 id
     * @param dto 保存数据
     * @param excludeId 排除的 id（更新时使用，避免与自己比较）
     */
    private void validateDuplicate(Long nodeId, BaseDataSaveDTO dto, Long excludeId) {
        if (nodeId == null) {
            return;
        }
        BaseTreeNodeDO node = baseTreeNodeManager.getById(nodeId);
        if (node == null) {
            return;
        }
        // 面料用量节点：校验 value1 + value2 组合
        if (BaseTreeNodeSeedEnum.FIELD_MGMT_FABRIC_USAGE.getNodeKey().equals(node.getNodeKey())) {
            validateFabricUsageDuplicate(nodeId, dto.getValue1(), dto.getValue2(), excludeId);
        } else {
            // 其他节点：校验 value1
            validateValue1Duplicate(nodeId, dto.getValue1(), excludeId);
        }
    }

    /**
     * 校验面料用量是否重复（value1 + value2 组合）
     *
     * @param nodeId 树节点 id
     * @param value1 待校验的 value1 值
     * @param value2 待校验的 value2 值
     * @param excludeId 排除的 id（更新时使用，避免与自己比较）
     */
    private void validateFabricUsageDuplicate(Long nodeId, String value1, String value2, Long excludeId) {
        if (nodeId == null || value1 == null || value2 == null) {
            return;
        }
        BaseDataDO duplicate = baseDataManager.getByNodeIdAndValue1AndValue2(nodeId, value1, value2);
        if (duplicate != null && !duplicate.getId().equals(excludeId)) {
            throw ServiceExceptionUtil.exception(BaseDataErrorCodeConstants.BASE_DATA_VALUE1_DUPLICATE);
        }
    }

    /**
     * 校验同一 nodeId 下 value1 是否重复
     *
     * @param nodeId 树节点 id
     * @param value1 待校验的 value1 值
     * @param excludeId 排除的 id（更新时使用，避免与自己比较）
     */
    private void validateValue1Duplicate(Long nodeId, String value1, Long excludeId) {
        if (nodeId == null || value1 == null) {
            return;
        }
        BaseDataDO duplicate = baseDataManager.getByNodeIdAndValue1(nodeId, value1);
        if (duplicate != null && !duplicate.getId().equals(excludeId)) {
            throw ServiceExceptionUtil.exception(BaseDataErrorCodeConstants.BASE_DATA_VALUE1_DUPLICATE);
        }
    }

    private void validBaseDataDO(BaseDataDO baseDataDO) {
        // 印刷方式/包材类型默认节点，不允许被修改
        if (isNull(baseDataDO)) {
            throw ServiceExceptionUtil.exception(BaseDataErrorCodeConstants.BASE_DATA_NOT_FOUND);
        }
        BaseTreeNodeDO nodeDO = baseTreeNodeManager.getById(baseDataDO.getNodeId());
        if (isNull(nodeDO)) {
            throw ServiceExceptionUtil.exception(BaseDataErrorCodeConstants.BASE_DATA_TREE_NODE_INVALID);
        }
        if (nodeDO.getNodeKey().equals(BaseTreeNodeSeedEnum.FIELD_MGMT_PRINTING_METHOD.getNodeKey())
                || nodeDO.getNodeKey().equals(BaseTreeNodeSeedEnum.PACKAGING_TYPE.getNodeKey())) {
            if (DEFAULT_FLAG.equals(baseDataDO.getValue2())) {
                throw ServiceExceptionUtil.exception(BaseDataErrorCodeConstants.BASE_DATA_TREE_NODE_DATA_NOT_UPDATE);
            }
        }
    }

    @Override
    public List<BaseDataVO> list(BaseDataListQueryDTO query) {
        List<Long> nodeIds = query.getNodeIds();
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
        BaseTreeNodeDO nodeDO = baseTreeNodeManager.getByNodeKey(nodeKey);
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
    public List<BaseTreeNodeVO> listNodeByBizTypeOrNodeKey(String bizType, String nodeKey) {
        List<BaseTreeNodeDO> rows = baseTreeNodeManager.listByBizTypeOrNodeKey(bizType, nodeKey);
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
