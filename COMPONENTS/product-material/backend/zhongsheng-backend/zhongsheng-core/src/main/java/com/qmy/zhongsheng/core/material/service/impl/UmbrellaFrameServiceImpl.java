package com.qmy.zhongsheng.core.material.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.api.dto.material.UmbrellaFrameListQueryDTO;
import com.qmy.zhongsheng.api.dto.material.UmbrellaFrameMaterialSaveDTO;
import com.qmy.zhongsheng.api.dto.material.UmbrellaFrameSaveDTO;
import com.qmy.zhongsheng.api.dto.material.UmbrellaFrameSelectQueryDTO;
import com.qmy.zhongsheng.common.error.BaseDataErrorCodeConstants;
import com.qmy.zhongsheng.common.error.MaterialErrorCodeConstants;
import com.qmy.zhongsheng.common.error.UmbrellaFrameErrorCodeConstants;
import com.qmy.zhongsheng.common.exception.ServiceExceptionUtil;
import com.qmy.zhongsheng.common.utils.BeanUtils;
import com.qmy.zhongsheng.common.utils.ValidityUtils;
import com.qmy.zhongsheng.core.base.manager.BaseDataManager;
import com.qmy.zhongsheng.core.base.model.entity.BaseDataDO;
import com.qmy.zhongsheng.core.file.enums.SystemFileMainTypeEnum;
import com.qmy.zhongsheng.core.file.enums.SystemFileSubTypeEnum;
import com.qmy.zhongsheng.core.file.manager.SystemFileManager;
import com.qmy.zhongsheng.core.file.model.entity.SystemFileDO;
import com.qmy.zhongsheng.core.file.model.vo.FileVO;
import com.qmy.zhongsheng.core.file.service.SystemFileService;
import com.qmy.zhongsheng.core.material.manager.MaterialCategoryManager;
import com.qmy.zhongsheng.core.material.manager.MaterialManager;
import com.qmy.zhongsheng.core.material.manager.UmbrellaFrameManager;
import com.qmy.zhongsheng.core.material.manager.UmbrellaFrameMaterialManager;
import com.qmy.zhongsheng.core.material.model.entity.MaterialCategoryDO;
import com.qmy.zhongsheng.core.material.model.entity.MaterialDO;
import com.qmy.zhongsheng.core.material.model.entity.UmbrellaFrameDO;
import com.qmy.zhongsheng.core.material.model.entity.UmbrellaFrameMaterialDO;
import com.qmy.zhongsheng.core.material.model.vo.UmbrellaFrameDetailVO;
import com.qmy.zhongsheng.core.material.model.vo.UmbrellaFrameMaterialVO;
import com.qmy.zhongsheng.core.material.model.vo.UmbrellaFrameVO;
import com.qmy.zhongsheng.core.material.service.UmbrellaFrameService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * @author AI Coding
 */
@Service
@RequiredArgsConstructor
public class UmbrellaFrameServiceImpl implements UmbrellaFrameService {

    private final UmbrellaFrameManager umbrellaFrameManager;
    private final UmbrellaFrameMaterialManager umbrellaFrameMaterialManager;
    private final BaseDataManager baseDataManager;
    private final MaterialManager materialManager;
    private final MaterialCategoryManager materialCategoryManager;
    private final SystemFileManager systemFileManager;
    private final SystemFileService systemFileService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveOrUpdate(UmbrellaFrameSaveDTO dto) {
        // 更新时检查是否存在
        if (dto.getId() != null) {
            UmbrellaFrameDO existing = umbrellaFrameManager.getById(dto.getId());
            if (existing == null) {
                throw ServiceExceptionUtil.exception(UmbrellaFrameErrorCodeConstants.UMBRELLA_FRAME_NOT_FOUND);
            }
        }

        // 重复性校验：检查维度组合是否已存在（排除自身）
        validateUmbrellaFrameDuplicate(dto.getFunctionId(), dto.getTypeId(), dto.getLengthId(),
                dto.getDiameterId(), dto.getRibCountId(), dto.getMaterialId(), dto.getId());

        // 保存伞架
        UmbrellaFrameDO row = BeanUtils.toBean(dto, UmbrellaFrameDO.class);
        fillBaseDataNameSnapshot(row);
        Long umbrellaFrameId = umbrellaFrameManager.saveOrUpdate(row);

        // 保存图片
        if (ValidityUtils.isNotEmpty(dto.getImages())) {
            systemFileService.saveFiles(umbrellaFrameId, SystemFileMainTypeEnum.MATERIAL, SystemFileSubTypeEnum.UMBRELLA_FRAME_IMAGE, dto.getImages());
        }

        // 保存伞架材料绑定
        if (ValidityUtils.isNotEmpty(dto.getMaterials())) {
            saveUmbrellaFrameMaterials(umbrellaFrameId, dto.getMaterials());
        }

        return umbrellaFrameId;
    }

    /**
     * 校验伞架维度组合是否重复
     *
     * @param functionId 功能 ID
     * @param typeId 类型 ID
     * @param lengthId 长度 ID
     * @param diameterId 直径 ID
     * @param ribCountId 伞骨数量 ID
     * @param materialId 材料 ID
     * @param excludeId 排除的 id（更新时使用，避免与自己比较）
     */
    private void validateUmbrellaFrameDuplicate(Long functionId, Long typeId, Long lengthId,
                                                 Long diameterId, Long ribCountId, Long materialId, Long excludeId) {
        UmbrellaFrameDO duplicate = umbrellaFrameManager.getByDimensionCombination(
                functionId, typeId, lengthId, diameterId, ribCountId, materialId);
        if (duplicate != null && !duplicate.getId().equals(excludeId)) {
            throw ServiceExceptionUtil.exception(UmbrellaFrameErrorCodeConstants.UMBRELLA_FRAME_DUPLICATE);
        }
    }

    @Override
    public Page<UmbrellaFrameDetailVO> page(UmbrellaFrameListQueryDTO query) {
        Long functionId = query.getFunctionId();
        Long typeId = query.getTypeId();
        Long lengthId = query.getLengthId();
        Long diameterId = query.getDiameterId();
        Long ribCountId = query.getRibCountId();
        Long materialId = query.getMaterialId();
        String keywords = query.getKeywords();
        Integer pageNum = query.getPageNum();
        Integer pageSize = query.getPageSize();

        Page<UmbrellaFrameDO> doPage = umbrellaFrameManager.page(functionId, typeId, lengthId, diameterId, ribCountId, materialId, keywords, pageNum, pageSize);

        Page<UmbrellaFrameDetailVO> voPage = new Page<>(doPage.getCurrent(), doPage.getSize(), doPage.getTotal());
        List<UmbrellaFrameDetailVO> voList = BeanUtils.toBean(doPage.getRecords(), UmbrellaFrameDetailVO.class);

        // 填充图片和材料
        if (!voList.isEmpty()) {
            fillImages(voList);
            fillMaterials(voList, UmbrellaFrameDetailVO::getId, UmbrellaFrameDetailVO::setMaterials);
        }

        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 保存伞架材料绑定
     */
    private void saveUmbrellaFrameMaterials(Long umbrellaFrameId, List<UmbrellaFrameMaterialSaveDTO> materials) {
        for (UmbrellaFrameMaterialSaveDTO materialDTO : materials) {
            if (materialDTO != null) {
                // 校验必填字段
                if (materialDTO.getMaterialId() == null) {
                    throw ServiceExceptionUtil.exception(MaterialErrorCodeConstants.MATERIAL_NOT_FOUND);
                }
                if (materialDTO.getQuantity() == null) {
                    throw new IllegalArgumentException("数量不能为空");
                }

                // 校验材料是否存在
                MaterialDO material = materialManager.getById(materialDTO.getMaterialId());
                if (material == null) {
                    throw ServiceExceptionUtil.exception(MaterialErrorCodeConstants.MATERIAL_NOT_FOUND);
                }

                // 更新时检查是否存在
                if (materialDTO.getId() != null) {
                    UmbrellaFrameMaterialDO existing = umbrellaFrameMaterialManager.getById(materialDTO.getId());
                    if (existing == null) {
                        throw new IllegalArgumentException("伞架材料绑定不存在");
                    }
                }

                // 设置伞架 ID
                UmbrellaFrameMaterialDO row = BeanUtils.toBean(materialDTO, UmbrellaFrameMaterialDO.class);
                row.setUmbrellaFrameId(umbrellaFrameId);
                if (material.getCategoryId() == null) {
                    throw new IllegalArgumentException("材料类型不能为空");
                }
                row.setMaterialCategoryId(material.getCategoryId());
                row.setMaterialName(material.getName());
                MaterialCategoryDO materialCategory = materialCategoryManager.getById(material.getCategoryId());
                row.setMaterialCategoryName(materialCategory != null ? materialCategory.getName() : null);
                row.setSize(material.getSize());
                row.setPrice(material.getPrice());
                umbrellaFrameMaterialManager.saveOrUpdate(row);
            }
        }
    }

    /**
     * 填充图片（用于 UmbrellaFrameDetailVO）
     */
    private void fillImages(List<UmbrellaFrameDetailVO> voList) {
        List<Long> ids = voList.stream().map(UmbrellaFrameDetailVO::getId).toList();

        if (ids.isEmpty()) {
            return;
        }

        List<SystemFileDO> fileList = systemFileManager.listByMainSubAndMasterIds(SystemFileMainTypeEnum.MATERIAL, SystemFileSubTypeEnum.UMBRELLA_FRAME_IMAGE, ids);

        Map<Long, List<FileVO>> imageMap = BeanUtils.groupToMap(fileList, SystemFileDO::getMasterId).entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> BeanUtils.toBean(e.getValue(), FileVO.class)));

        voList.forEach(vo -> vo.setImages(imageMap.getOrDefault(vo.getId(), new ArrayList<>())));
    }

    /**
     * 通用填充材料方法
     */
    private <T> void fillMaterials(List<T> voList, java.util.function.Function<T, Long> idGetter, java.util.function.BiConsumer<T, List<UmbrellaFrameMaterialVO>> materialsSetter) {
        List<Long> ids = voList.stream().map(idGetter).toList();

        if (ids.isEmpty()) {
            return;
        }

        List<UmbrellaFrameMaterialDO> materialDOList = umbrellaFrameMaterialManager.listByUmbrellaFrameIds(ids);

        Map<Long, List<UmbrellaFrameMaterialVO>> materialMap = BeanUtils.groupToMap(materialDOList, UmbrellaFrameMaterialDO::getUmbrellaFrameId).entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> BeanUtils.toBean(e.getValue(), UmbrellaFrameMaterialVO.class)));

        voList.forEach(vo -> materialsSetter.accept(vo, materialMap.getOrDefault(idGetter.apply(vo), new ArrayList<>())));
    }

    @Override
    public Boolean delete(Long id) {
        return umbrellaFrameManager.deleted(id);
    }

    @Override
    public List<UmbrellaFrameMaterialVO> listMaterials(Long umbrellaFrameId) {
        List<UmbrellaFrameMaterialDO> materialDOList = umbrellaFrameMaterialManager.listByUmbrellaFrameId(umbrellaFrameId);
        return BeanUtils.toBean(materialDOList, UmbrellaFrameMaterialVO.class);
    }

    @Override
    public UmbrellaFrameDetailVO getDetail(Long umbrellaFrameId) {
        // 查询伞架基本信息
        UmbrellaFrameDO umbrellaFrame = umbrellaFrameManager.getById(umbrellaFrameId);
        if (umbrellaFrame == null) {
            throw ServiceExceptionUtil.exception(UmbrellaFrameErrorCodeConstants.UMBRELLA_FRAME_NOT_FOUND);
        }

        // 转换为 VO
        UmbrellaFrameDetailVO detailVO = BeanUtils.toBean(umbrellaFrame, UmbrellaFrameDetailVO.class);

        // 填充图片和材料
        List<UmbrellaFrameDetailVO> detailVOList = List.of(detailVO);
        fillImages(detailVOList);
        fillMaterials(detailVOList, UmbrellaFrameDetailVO::getId, UmbrellaFrameDetailVO::setMaterials);

        return detailVO;
    }

    private void fillBaseDataNameSnapshot(UmbrellaFrameDO row) {
        Set<Long> baseDataIds = new HashSet<>(List.of(
                row.getFunctionId(),
                row.getTypeId(),
                row.getLengthId(),
                row.getDiameterId(),
                row.getRibCountId(),
                row.getMaterialId()
        ));

        List<BaseDataDO> baseDataList = baseDataManager.listByIds(new ArrayList<>(baseDataIds));
        Map<Long, BaseDataDO> baseDataMap = baseDataList.stream()
                .collect(Collectors.toMap(BaseDataDO::getId, Function.identity(), (a, b) -> a));
        if (baseDataMap.size() != baseDataIds.size()) {
            throw ServiceExceptionUtil.exception(BaseDataErrorCodeConstants.BASE_DATA_NOT_FOUND);
        }

        row.setFunctionName(baseDataMap.get(row.getFunctionId()).getValue1());
        row.setTypeName(baseDataMap.get(row.getTypeId()).getValue1());
        row.setLengthName(baseDataMap.get(row.getLengthId()).getValue1());
        row.setDiameterName(baseDataMap.get(row.getDiameterId()).getValue1());
        row.setRibCountName(baseDataMap.get(row.getRibCountId()).getValue1());
        row.setMaterialName(baseDataMap.get(row.getMaterialId()).getValue1());
    }

    @Override
    public List<UmbrellaFrameVO> listForSelect(UmbrellaFrameSelectQueryDTO query) {
        Long functionId = query != null ? query.getFunctionId() : null;
        Long typeId = query != null ? query.getTypeId() : null;
        Long lengthId = query != null ? query.getLengthId() : null;
        Long diameterId = query != null ? query.getDiameterId() : null;
        Long ribCountId = query != null ? query.getRibCountId() : null;
        Long materialId = query != null ? query.getMaterialId() : null;
        List<UmbrellaFrameDO> doList = umbrellaFrameManager.listByCondition(functionId, typeId, lengthId, diameterId, ribCountId, materialId);
        List<UmbrellaFrameVO> voList = BeanUtils.toBean(doList, UmbrellaFrameVO.class);

        // 填充绑定的材料
        if (!voList.isEmpty()) {
            fillMaterials(voList, UmbrellaFrameVO::getId, UmbrellaFrameVO::setMaterials);
        }

        return voList;
    }
}