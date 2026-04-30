package com.qmy.zhongsheng.core.material.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.api.dto.material.PackagingListQueryDTO;
import com.qmy.zhongsheng.api.dto.material.PackagingSaveDTO;
import com.qmy.zhongsheng.common.enums.BaseTreeNodeSeedEnum;
import com.qmy.zhongsheng.common.error.PackagingErrorCodeConstants;
import com.qmy.zhongsheng.common.exception.ServiceExceptionUtil;
import com.qmy.zhongsheng.common.utils.BeanUtils;
import com.qmy.zhongsheng.core.base.manager.BaseDataManager;
import com.qmy.zhongsheng.core.base.manager.BaseTreeNodeManager;
import com.qmy.zhongsheng.core.base.model.entity.BaseDataDO;
import com.qmy.zhongsheng.core.material.manager.PackagingManager;
import com.qmy.zhongsheng.core.material.model.entity.PackagingDO;
import com.qmy.zhongsheng.core.material.model.vo.PackagingVO;
import com.qmy.zhongsheng.core.material.service.PackagingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
public class PackagingServiceImpl implements PackagingService {

    private final PackagingManager packagingManager;
    private final BaseDataManager baseDataManager;
    private final BaseTreeNodeManager baseTreeNodeManager;

    @Override
    public Long saveOrUpdate(PackagingSaveDTO dto) {
        // 校验必填字段
        if (dto.getTypeId() == null) {
            throw ServiceExceptionUtil.exception(PackagingErrorCodeConstants.PACKAGING_TYPE_ID_REQUIRED);
        }
        if (!StringUtils.hasText(dto.getName())) {
            throw ServiceExceptionUtil.exception(PackagingErrorCodeConstants.PACKAGING_NAME_REQUIRED);
        }
        if (dto.getPrice() == null) {
            throw ServiceExceptionUtil.exception(PackagingErrorCodeConstants.PACKAGING_PRICE_REQUIRED);
        }

        // 校验 baseDataId 是否存在
        BaseDataDO typeData = baseDataManager.getById(dto.getTypeId());
        if (typeData == null) {
            throw ServiceExceptionUtil.exception(PackagingErrorCodeConstants.PACKAGING_TYPE_DATA_NOT_FOUND);
        }

        // 更新时检查是否存在
        if (dto.getId() != null) {
            PackagingDO existing = packagingManager.getById(dto.getId());
            if (existing == null) {
                throw ServiceExceptionUtil.exception(PackagingErrorCodeConstants.PACKAGING_NOT_FOUND);
            }
        } else {
            // 新增时，将包材类型名称赋值到 DTO
            dto.setTypeName(typeData.getValue1());
        }

        // 重复性校验：同一类型下名称不能重复（排除自身）
        validatePackagingDuplicate(dto.getTypeId(), dto.getName(), dto.getId());

        PackagingDO row = BeanUtils.toBean(dto, PackagingDO.class);
        return packagingManager.saveOrUpdate(row);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Long> saveOrUpdateDefaultPaperBox(List<PackagingSaveDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取默认纸盒类型的 ID：node_id 为 PACKAGING_TYPE 且 value2 为 1 的数据
        Long packagingTypeNodeId = baseTreeNodeManager.getByNodeKey(BaseTreeNodeSeedEnum.PACKAGING_TYPE.getNodeKey()).getId();
        List<BaseDataDO> packagingTypeList = baseDataManager.listByNodeId(packagingTypeNodeId);

        BaseDataDO defaultPaperBoxType = packagingTypeList.stream()
                .filter(data -> "1".equals(data.getValue2()))
                .findFirst()
                .orElseThrow(() -> ServiceExceptionUtil.exception(PackagingErrorCodeConstants.PACKAGING_TYPE_DATA_NOT_FOUND));

        Long defaultTypeId = defaultPaperBoxType.getId();
        String defaultTypeName = defaultPaperBoxType.getValue1();

        // 用于收集保存成功的记录 ID
        List<Long> savedIds = new ArrayList<>();

        // 批量保存或更新
        for (PackagingSaveDTO dto : dtos) {
            // 强制设置 typeId 为默认纸盒类型
            dto.setTypeId(defaultTypeId);

            // 校验必填字段
            if (!StringUtils.hasText(dto.getName())) {
                throw ServiceExceptionUtil.exception(PackagingErrorCodeConstants.PACKAGING_NAME_REQUIRED);
            }
            if (dto.getPrice() == null) {
                throw ServiceExceptionUtil.exception(PackagingErrorCodeConstants.PACKAGING_PRICE_REQUIRED);
            }

            // 更新时检查是否存在
            if (dto.getId() != null) {
                PackagingDO existing = packagingManager.getById(dto.getId());
                if (existing == null) {
                    throw ServiceExceptionUtil.exception(PackagingErrorCodeConstants.PACKAGING_NOT_FOUND);
                }
            } else {
                // 新增时，将包材类型名称赋值到 DTO
                dto.setTypeName(defaultTypeName);
            }

            // 重复性校验：同一类型下名称不能重复（排除自身）
            validatePackagingDuplicate(defaultTypeId, dto.getName(), dto.getId());

            PackagingDO row = BeanUtils.toBean(dto, PackagingDO.class);
            Long savedId = packagingManager.saveOrUpdate(row);
            savedIds.add(savedId);
        }
        return savedIds;
    }

    @Override
    public Page<PackagingVO> page(PackagingListQueryDTO query) {
        Long typeId = query != null ? query.getTypeId() : null;
        String likeSize = query != null ? query.getLikeSize() : null;
        String keyword = query != null ? query.getKeywords() : null;
        String defaultTypeFlag = query != null ? query.getDefaultTypeFlag() : null;
        Integer pageNum = query != null ? query.getPageNum() : null;
        Integer pageSize = query != null ? query.getPageSize() : null;

        Page<PackagingDO> doPage = packagingManager.page(typeId, likeSize, keyword, defaultTypeFlag, pageNum, pageSize);

        Page<PackagingVO> voPage = new Page<>(doPage.getCurrent(), doPage.getSize(), doPage.getTotal());
        List<PackagingVO> voList = BeanUtils.toBean(doPage.getRecords(), PackagingVO.class);

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public Boolean delete(Long id) {
        return packagingManager.deleted(id);
    }

    /**
     * 校验包材名称是否重复
     *
     * @param typeId 类型 ID
     * @param name 包材名称
     * @param excludeId 排除的 id（更新时使用，避免与自己比较）
     */
    private void validatePackagingDuplicate(Long typeId, String name, Long excludeId) {
        PackagingDO duplicate = packagingManager.getByTypeIdAndName(typeId, name);
        if (duplicate != null && !duplicate.getId().equals(excludeId)) {
            throw ServiceExceptionUtil.exception(PackagingErrorCodeConstants.PACKAGING_NAME_DUPLICATE);
        }
    }

    /**
     * 填充 baseData 名称
     */
    private void fillBaseDataNames(List<PackagingVO> voList) {
        // 收集所有需要查询的 baseDataId
        Set<Long> baseDataIds = new HashSet<>();
        for (PackagingVO vo : voList) {
            if (vo.getTypeId() != null) baseDataIds.add(vo.getTypeId());
        }

        if (baseDataIds.isEmpty()) {
            return;
        }

        // 批量查询 baseData
        List<BaseDataDO> baseDataList = baseDataManager.listByIds(new ArrayList<>(baseDataIds));
        Map<Long, BaseDataDO> baseDataMap = baseDataList.stream()
                .collect(Collectors.toMap(BaseDataDO::getId, Function.identity(), (a, b) -> a));

        // 填充名称
        for (PackagingVO vo : voList) {
            if (vo.getTypeId() != null) {
                BaseDataDO data = baseDataMap.get(vo.getTypeId());
                vo.setTypeName(data != null ? data.getValue1() : null);
            }
        }
    }
}