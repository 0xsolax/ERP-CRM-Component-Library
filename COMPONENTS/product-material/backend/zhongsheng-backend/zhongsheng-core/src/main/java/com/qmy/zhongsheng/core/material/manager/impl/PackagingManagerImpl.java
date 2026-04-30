package com.qmy.zhongsheng.core.material.manager.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.core.material.dao.PackagingDAO;
import com.qmy.zhongsheng.core.material.manager.PackagingManager;
import com.qmy.zhongsheng.core.material.model.entity.PackagingDO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * @author AI Coding
 */
@Component
@RequiredArgsConstructor
public class PackagingManagerImpl implements PackagingManager {

    private final PackagingDAO packagingDAO;

    @Override
    public Long saveOrUpdate(PackagingDO row) {
        if (row.getId() == null) {
            packagingDAO.insert(row);
            return row.getId();
        }
        packagingDAO.updateById(row);
        return row.getId();
    }

    @Override
    public PackagingDO getById(Long id) {
        return packagingDAO.selectById(id);
    }

    @Override
    public Page<PackagingDO> page(Long typeId, String likeSize, String keyword, String defaultTypeFlag, Integer pageNum, Integer pageSize) {
        return packagingDAO.selectPage(
                new Page<>(pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 10),
                Wrappers.<PackagingDO>lambdaQuery()
                        .eq(typeId != null, PackagingDO::getTypeId, typeId)
                        .like(StringUtils.hasText(likeSize), PackagingDO::getSize, likeSize)
                        .and(StringUtils.hasText(keyword), wrapper -> wrapper
                                .like(PackagingDO::getTypeName, keyword)
                                .or()
                                .like(PackagingDO::getName, keyword)
                                .or()
                                .like(PackagingDO::getSize, keyword)
                                .or()
                                .apply("CAST(price AS CHAR) LIKE CONCAT('%', {0}, '%')", keyword))
                        .and(StringUtils.hasText(defaultTypeFlag), wrapper -> wrapper
                                .apply("type_id IN (SELECT id FROM base_data WHERE value2 = {0})", defaultTypeFlag))
                        .eq(PackagingDO::getIsDeleted, 0)
                        .orderByDesc(PackagingDO::getId));
    }

    @Override
    public Boolean deleted(Long id) {
        return packagingDAO.update(Wrappers.<PackagingDO>lambdaUpdate()
                .eq(PackagingDO::getId, id)
                .set(PackagingDO::getIsDeleted, 1)
                .set(PackagingDO::getDeletedTime, LocalDateTime.now())) > 0;
    }

    @Override
    public PackagingDO getByTypeIdAndName(Long typeId, String name) {
        return packagingDAO.selectOne(Wrappers.<PackagingDO>lambdaQuery()
                .eq(PackagingDO::getIsDeleted, 0)
                .eq(PackagingDO::getTypeId, typeId)
                .eq(PackagingDO::getName, name)
                .last("LIMIT 1"));
    }
}