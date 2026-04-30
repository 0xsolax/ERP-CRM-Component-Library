package com.qmy.zhongsheng.core.material.service.impl;

import com.qmy.zhongsheng.api.dto.material.MaterialCategoryListQueryDTO;
import com.qmy.zhongsheng.api.dto.material.MaterialCategorySaveDTO;
import com.qmy.zhongsheng.common.error.MaterialErrorCodeConstants;
import com.qmy.zhongsheng.common.exception.ServiceExceptionUtil;
import com.qmy.zhongsheng.common.utils.BeanUtils;
import com.qmy.zhongsheng.core.material.manager.MaterialCategoryManager;
import com.qmy.zhongsheng.core.material.manager.MaterialManager;
import com.qmy.zhongsheng.core.material.model.entity.MaterialCategoryDO;
import com.qmy.zhongsheng.core.material.model.entity.MaterialDO;
import com.qmy.zhongsheng.core.material.model.vo.MaterialCategoryVO;
import com.qmy.zhongsheng.core.material.service.MaterialCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author AI Coding
 */
@Service
@RequiredArgsConstructor
public class MaterialCategoryServiceImpl implements MaterialCategoryService {

    private final MaterialCategoryManager materialCategoryManager;
    private final MaterialManager materialManager;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveOrUpdate(MaterialCategorySaveDTO dto) {
        int totalCount = materialCategoryManager.countAll();
        boolean isInsert = dto.getId() == null;
        int maxSortNum = isInsert ? totalCount + 1 : totalCount;

        if (dto.getSortNum() < 1 || dto.getSortNum() > maxSortNum) {
            throw ServiceExceptionUtil.exception(
                    MaterialErrorCodeConstants.MATERIAL_CATEGORY_SORT_NUM_OUT_OF_RANGE.getCode(),
                    String.format(MaterialErrorCodeConstants.MATERIAL_CATEGORY_SORT_NUM_OUT_OF_RANGE.getMessage(), maxSortNum));
        }

        // 重复性校验：检查名称是否已存在（排除自身）
        validateMaterialCategoryNameDuplicate(dto.getName(), dto.getId());

        int newSortNum = dto.getSortNum();

        if (isInsert) {
            materialCategoryManager.shiftSortNum(newSortNum, totalCount, 1);
        } else {
            MaterialCategoryDO existing = materialCategoryManager.getById(dto.getId());
            if (existing == null) {
                throw ServiceExceptionUtil.exception(MaterialErrorCodeConstants.MATERIAL_CATEGORY_NOT_FOUND);
            }
            int oldSortNum = existing.getSortNum();
            if (newSortNum < oldSortNum) {
                materialCategoryManager.shiftSortNum(newSortNum, oldSortNum - 1, 1);
            } else if (newSortNum > oldSortNum) {
                materialCategoryManager.shiftSortNum(oldSortNum + 1, newSortNum, -1);
            }
        }

        MaterialCategoryDO row = BeanUtils.toBean(dto, MaterialCategoryDO.class);
        return materialCategoryManager.saveOrUpdate(row);
    }

    /**
     * 校验材料分类名称是否重复
     *
     * @param name 分类名称
     * @param excludeId 排除的 id（更新时使用，避免与自己比较）
     */
    private void validateMaterialCategoryNameDuplicate(String name, Long excludeId) {
        MaterialCategoryDO duplicate = materialCategoryManager.getByName(name);
        if (duplicate != null && !duplicate.getId().equals(excludeId)) {
            throw ServiceExceptionUtil.exception(MaterialErrorCodeConstants.MATERIAL_CATEGORY_NAME_DUPLICATE);
        }
    }

    @Override
    public List<MaterialCategoryVO> listByLikeName(MaterialCategoryListQueryDTO query) {
        List<MaterialCategoryDO> categoryList = materialCategoryManager.listByLikeName(query.getLikeName());
        return BeanUtils.toBean(categoryList, MaterialCategoryVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(Long id) {
        MaterialCategoryDO category = materialCategoryManager.getById(id);
        if (category == null) {
            throw ServiceExceptionUtil.exception(MaterialErrorCodeConstants.MATERIAL_CATEGORY_NOT_FOUND);
        }
        List<MaterialDO> materialList = materialManager.listByCategoryId(id);
        int materialCount = materialList.size();
        if (materialCount > 0) {
            throw ServiceExceptionUtil.exception(500,
                    String.format("分类「%s」下还有 %d 条材料，请先删除或移出材料后再删除分类", category.getName(), materialCount));
        }

        int deletedSortNum = category.getSortNum();
        int totalCount = materialCategoryManager.countAll();
        materialCategoryManager.delete(id);
        materialCategoryManager.shiftSortNum(deletedSortNum + 1, totalCount, -1);
        return Boolean.TRUE;
    }
}