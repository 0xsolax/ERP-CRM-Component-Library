package com.qmy.zhongsheng.core.material.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.api.dto.material.MaterialListQueryDTO;
import com.qmy.zhongsheng.api.dto.material.MaterialSaveDTO;
import com.qmy.zhongsheng.api.request.IdRequestParam;
import com.qmy.zhongsheng.common.error.MaterialErrorCodeConstants;
import com.qmy.zhongsheng.common.exception.ServiceExceptionUtil;
import com.qmy.zhongsheng.common.utils.BeanUtils;
import com.qmy.zhongsheng.common.utils.ValidityUtils;
import com.qmy.zhongsheng.core.file.enums.SystemFileMainTypeEnum;
import com.qmy.zhongsheng.core.file.enums.SystemFileSubTypeEnum;
import com.qmy.zhongsheng.core.file.manager.SystemFileManager;
import com.qmy.zhongsheng.core.file.model.entity.SystemFileDO;
import com.qmy.zhongsheng.core.file.model.vo.FileVO;
import com.qmy.zhongsheng.core.file.service.SystemFileService;
import com.qmy.zhongsheng.core.material.manager.MaterialCategoryManager;
import com.qmy.zhongsheng.core.material.manager.MaterialManager;
import com.qmy.zhongsheng.core.material.model.entity.MaterialCategoryDO;
import com.qmy.zhongsheng.core.material.model.entity.MaterialDO;
import com.qmy.zhongsheng.core.material.model.vo.MaterialSimpleVO;
import com.qmy.zhongsheng.core.material.model.vo.MaterialVO;
import com.qmy.zhongsheng.core.material.service.MaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
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
public class MaterialServiceImpl implements MaterialService {

    private final MaterialManager materialManager;
    private final MaterialCategoryManager materialCategoryManager;
    private final SystemFileManager systemFileManager;
    private final SystemFileService systemFileService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveOrUpdate(MaterialSaveDTO dto) {
        // 校验必填字段
        if (dto.getCategoryId() == null) {
            throw ServiceExceptionUtil.exception(MaterialErrorCodeConstants.MATERIAL_CATEGORY_ID_REQUIRED);
        }
        if (!ValidityUtils.hasText(dto.getName())) {
            throw ServiceExceptionUtil.exception(MaterialErrorCodeConstants.MATERIAL_NAME_REQUIRED);
        }

        // 校验分类是否存在
        MaterialCategoryDO category = materialCategoryManager.getById(dto.getCategoryId());
        if (category == null) {
            throw ServiceExceptionUtil.exception(MaterialErrorCodeConstants.MATERIAL_CATEGORY_NOT_FOUND);
        }

        // 更新时检查是否存在
        if (dto.getId() != null) {
            MaterialDO existing = materialManager.getById(dto.getId());
            if (existing == null) {
                throw ServiceExceptionUtil.exception(MaterialErrorCodeConstants.MATERIAL_NOT_FOUND);
            }
        }

        // 重复性校验：同一分类下名称不能重复（排除自身）
        validateMaterialDuplicate(dto.getCategoryId(), dto.getName(), dto.getId());

        // 保存材料
        MaterialDO row = BeanUtils.toBean(dto, MaterialDO.class);
        Long materialId = materialManager.saveOrUpdate(row);

        // 保存图片
        if (ValidityUtils.isNotEmpty(dto.getImages())) {
            systemFileService.saveFiles(materialId, SystemFileMainTypeEnum.MATERIAL, SystemFileSubTypeEnum.MATERIAL_IMAGE, dto.getImages());
        }

        return materialId;
    }

    /**
     * 校验材料名称是否重复
     *
     * @param categoryId 分类 ID
     * @param name 材料名称
     * @param excludeId 排除的 id（更新时使用，避免与自己比较）
     */
    private void validateMaterialDuplicate(Long categoryId, String name, Long excludeId) {
        MaterialDO duplicate = materialManager.getByCategoryIdAndName(categoryId, name);
        if (duplicate != null && !duplicate.getId().equals(excludeId)) {
            throw ServiceExceptionUtil.exception(MaterialErrorCodeConstants.MATERIAL_NAME_DUPLICATE);
        }
    }

    @Override
    public Page<MaterialVO> page(MaterialListQueryDTO query) {
        Page<MaterialDO> doPage = materialManager.page(query.getCategoryId(), query.getLikeName(), query.getLikeSize(), query.getPageNum(), query.getPageSize());
        Page<MaterialVO> voPage = new Page<>(doPage.getCurrent(), doPage.getSize(), doPage.getTotal());
        List<MaterialVO> voList = BeanUtils.toBean(doPage.getRecords(), MaterialVO.class);

        // 填充分类名称和图片
        if (!voList.isEmpty()) {
            fillCategoryNamesAndImages(voList);
        }

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public List<MaterialSimpleVO> listByCategoryId(IdRequestParam idRequestParam) {
        List<MaterialDO> materialDOList = materialManager.listByCategoryId(idRequestParam.getId());
        return BeanUtils.toBean(materialDOList, MaterialSimpleVO.class);
    }

    /**
     * 填充分类名称和图片
     */
    private void fillCategoryNamesAndImages(List<MaterialVO> voList) {
        // 收集所有需要查询的 categoryId 和 materialId
        Set<Long> categoryIds = new HashSet<>();
        List<Long> materialIds = new ArrayList<>();
        for (MaterialVO vo : voList) {
            if (vo.getCategoryId() != null) {
                categoryIds.add(vo.getCategoryId());
            }
            if (vo.getId() != null) {
                materialIds.add(vo.getId());
            }
        }

        // 批量查询分类
        if (!categoryIds.isEmpty()) {
            List<MaterialCategoryDO> categoryList = materialCategoryManager.listByIds(new ArrayList<>(categoryIds));
            Map<Long, MaterialCategoryDO> categoryMap = categoryList.stream()
                    .collect(Collectors.toMap(MaterialCategoryDO::getId, Function.identity(), (a, b) -> a));

            // 填充分类名称
            for (MaterialVO vo : voList) {
                if (vo.getCategoryId() != null) {
                    MaterialCategoryDO category = categoryMap.get(vo.getCategoryId());
                    vo.setCategoryName(category != null ? category.getName() : null);
                }
            }
        }

        // 批量查询图片
        if (!materialIds.isEmpty()) {
            List<SystemFileDO> fileList = systemFileManager.listByMainSubAndMasterIds(
                    SystemFileMainTypeEnum.MATERIAL,
                    SystemFileSubTypeEnum.MATERIAL_IMAGE,
                    materialIds);

            // 按 masterId 分组
            Map<Long, List<FileVO>> imageMap = new HashMap<>();
            for (SystemFileDO file : fileList) {
                Long masterId = file.getMasterId();
                if (masterId != null) {
                    imageMap.computeIfAbsent(masterId, k -> new ArrayList<>())
                            .add(BeanUtils.toBean(file, FileVO.class));
                }
            }

            // 填充图片
            for (MaterialVO vo : voList) {
                if (vo.getId() != null) {
                    vo.setImages(imageMap.getOrDefault(vo.getId(), new ArrayList<>()));
                }
            }
        }
    }

    @Override
    public Boolean delete(Long id) {
        return materialManager.deleted(id);
    }
}