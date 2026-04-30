package com.qmy.project.core.file.manager.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.qmy.project.common.utils.BeanUtils;
import com.qmy.project.common.utils.ValidityUtils;
import com.qmy.project.core.file.dao.SystemFileDAO;
import com.qmy.project.core.file.enums.SystemFileMainTypeEnum;
import com.qmy.project.core.file.enums.SystemFileSubTypeEnum;
import com.qmy.project.core.file.manager.SystemFileManager;
import com.qmy.project.core.file.model.entity.SystemFileDO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.qmy.project.common.utils.ValidityUtils.isNotBlank;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/26
 */
@Component
@RequiredArgsConstructor
public class SystemFileManagerImpl implements SystemFileManager {

    private final SystemFileDAO systemFileDAO;

    @Override
    public SystemFileDO getById(Long id) {
        if (id == null) {
            return null;
        }
        SystemFileDO row = systemFileDAO.selectById(id);
        if (row == null || (row.getIsDeleted() != null && row.getIsDeleted() != 0)) {
            return null;
        }
        return row;
    }

    @Override
    public Long upsertUserAvatarFile(Long existingFileId, String avatarUrl) {
        if (ValidityUtils.isBlank(avatarUrl)) {
            return existingFileId;
        }
        String url = avatarUrl.trim();
        if (existingFileId != null) {
            SystemFileDO row = systemFileDAO.selectById(existingFileId);
            if (row != null && (row.getIsDeleted() == null || row.getIsDeleted() == 0)) {
                if (url.equals(row.getUrl())) {
                    return existingFileId;
                }
                row.setUrl(url);
                row.setName(fileNameHintFromUrl(url));
                systemFileDAO.updateById(row);
                return existingFileId;
            }
        }
        SystemFileDO insert = new SystemFileDO();
        insert.setMainType(SystemFileMainTypeEnum.USER.getCode());
        insert.setSubType(SystemFileSubTypeEnum.USER_AVATAR.getCode());
        insert.setUrl(url);
        insert.setName(fileNameHintFromUrl(url));
        systemFileDAO.insert(insert);
        return insert.getId();
    }

    private static String fileNameHintFromUrl(String url) {
        int slash = url.lastIndexOf('/');
        if (slash >= 0 && slash < url.length() - 1) {
            return url.substring(slash + 1);
        }
        return "avatar";
    }

    @Override
    public List<SystemFileDO> listByMainAndSub(SystemFileMainTypeEnum mainType, SystemFileSubTypeEnum subType) {
        if (subType.getMainType() != mainType) {
            return List.of();
        }
        return systemFileDAO.selectList(Wrappers.<SystemFileDO>lambdaQuery()
                .eq(SystemFileDO::getMainType, mainType.getCode())
                .eq(SystemFileDO::getSubType, subType.getCode())
                .orderByAsc(SystemFileDO::getId));
    }

    @Override
    public List<SystemFileDO> listByMainAndSubTypes(SystemFileMainTypeEnum mainType, List<SystemFileSubTypeEnum> subTypes) {
        if (CollectionUtils.isEmpty(subTypes)) {
            return List.of();
        }
        List<String> subCodes = subTypes.stream()
                .filter(s -> s.getMainType() == mainType)
                .map(SystemFileSubTypeEnum::getCode)
                .toList();
        if (subCodes.isEmpty()) {
            return List.of();
        }
        return systemFileDAO.selectList(Wrappers.<SystemFileDO>lambdaQuery()
                .eq(SystemFileDO::getMainType, mainType.getCode())
                .in(SystemFileDO::getSubType, subCodes)
                .orderByAsc(SystemFileDO::getId));
    }

    @Override
    public void saveOrUpdate(SystemFileMainTypeEnum mainType, SystemFileSubTypeEnum subType, List<SystemFileDO> items) {
        if (subType.getMainType() != mainType || items == null || items.isEmpty()) {
            return;
        }
        List<SystemFileDO> rows = items.stream().filter(item -> item != null && isNotBlank(item.getUrl())).toList();
        if (rows.isEmpty()) {
            return;
        }
        String mainCode = mainType.getCode();
        String subCode = subType.getCode();
        Set<String> urls = rows.stream().map(SystemFileDO::getUrl).collect(Collectors.toSet());
        List<SystemFileDO> existingRows = systemFileDAO.selectList(Wrappers.<SystemFileDO>lambdaQuery().eq(SystemFileDO::getMainType, mainCode).eq(SystemFileDO::getSubType, subCode).in(SystemFileDO::getUrl, urls));
        Map<String, SystemFileDO> byUrl = existingRows.stream().collect(Collectors.toMap(SystemFileDO::getUrl, r -> r));
        for (SystemFileDO row : rows) {
            String url = row.getUrl();
            SystemFileDO existing = byUrl.get(url);
            if (existing != null) {
                SystemFileDO updateRow = BeanUtils.toBean(row, SystemFileDO.class);
                updateRow.setId(existing.getId());
                systemFileDAO.updateById(updateRow);
            } else {
                SystemFileDO insertRow = BeanUtils.toBean(row, SystemFileDO.class);
                systemFileDAO.insert(insertRow);
            }
        }
    }

}
