package com.qmy.zhongsheng.core.material.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.api.dto.material.FabricListQueryDTO;
import com.qmy.zhongsheng.api.dto.material.FabricSaveDTO;
import com.qmy.zhongsheng.api.dto.material.FabricSelectQueryDTO;
import com.qmy.zhongsheng.common.error.FabricErrorCodeConstants;
import com.qmy.zhongsheng.common.exception.ServiceExceptionUtil;
import com.qmy.zhongsheng.common.utils.BeanUtils;
import com.qmy.zhongsheng.core.base.manager.BaseDataManager;
import com.qmy.zhongsheng.core.base.model.entity.BaseDataDO;
import com.qmy.zhongsheng.core.material.manager.FabricManager;
import com.qmy.zhongsheng.core.material.model.entity.FabricDO;
import com.qmy.zhongsheng.core.material.model.vo.FabricVO;
import com.qmy.zhongsheng.core.material.service.FabricService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author AI Coding
 */
@Service
@RequiredArgsConstructor
public class FabricServiceImpl implements FabricService {

    private final FabricManager fabricManager;
    private final BaseDataManager baseDataManager;

    @Override
    public Long saveOrUpdate(FabricSaveDTO dto) {
        // 校验单位只能是米或码
        if (!"米".equals(dto.getUnit()) && !"码".equals(dto.getUnit())) {
            throw ServiceExceptionUtil.exception(FabricErrorCodeConstants.FABRIC_UNIT_INVALID);
        }
        BaseDataDO typeData = baseDataManager.getById(dto.getTypeId());
        BaseDataDO modelData = baseDataManager.getById(dto.getModelId());
        BaseDataDO widthData = baseDataManager.getById(dto.getWidthId());
        // 更新时检查是否存在
        if (dto.getId() != null) {
            FabricDO existing = fabricManager.getById(dto.getId());
            if (existing == null) {
                throw ServiceExceptionUtil.exception(FabricErrorCodeConstants.FABRIC_NOT_FOUND);
            }
        }
        // 重复性校验：检查维度组合是否已存在（排除自身）
        validateFabricDuplicate(dto.getTypeId(), dto.getModelId(), dto.getWidthId(), dto.getId());
        FabricDO row = BeanUtils.toBean(dto, FabricDO.class);
        row.setModelName(modelData.getValue1());
        row.setTypeName(typeData.getValue1());
        row.setWidthName(widthData.getValue1());
        return fabricManager.saveOrUpdate(row);
    }

    /**
     * 校验面料维度组合是否重复
     *
     * @param typeId 种类 ID
     * @param modelId 型号 ID
     * @param widthId 门幅 ID
     * @param excludeId 排除的 id（更新时使用，避免与自己比较）
     */
    private void validateFabricDuplicate(Long typeId, Long modelId, Long widthId, Long excludeId) {
        FabricDO duplicate = fabricManager.getByDimensionCombination(typeId, modelId, widthId);
        if (duplicate != null && !duplicate.getId().equals(excludeId)) {
            throw ServiceExceptionUtil.exception(FabricErrorCodeConstants.FABRIC_DUPLICATE);
        }
    }

    @Override
    public Boolean delete(Long id) {
        return fabricManager.deleted(id);
    }

    @Override
    public FabricVO getDetail(Long id) {
        FabricDO row = fabricManager.getById(id);
        return BeanUtils.toBean(row, FabricVO.class);
    }

    @Override
    public Page<FabricVO> page(FabricListQueryDTO query) {
        Page<FabricDO> doPage = fabricManager.page(query.getTypeId(), query.getModelId(), null, query.getKeywords(), query.getPageNum(), query.getPageSize());
        Page<FabricVO> voPage = new Page<>(doPage.getCurrent(), doPage.getSize(), doPage.getTotal());
        List<FabricVO> voList = BeanUtils.toBean(doPage.getRecords(), FabricVO.class);
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public List<FabricVO> listForSelect(FabricSelectQueryDTO query) {
        Long typeId = query != null ? query.getTypeId() : null;
        Long modelId = query != null ? query.getModelId() : null;
        List<FabricDO> rows = fabricManager.list(typeId, modelId);
        return BeanUtils.toBean(rows, FabricVO.class);
    }
}