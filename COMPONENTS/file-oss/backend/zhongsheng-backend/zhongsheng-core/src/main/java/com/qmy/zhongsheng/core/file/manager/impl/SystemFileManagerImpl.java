package com.qmy.zhongsheng.core.file.manager.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.qmy.zhongsheng.common.utils.BeanUtils;
import com.qmy.zhongsheng.common.utils.StrUtils;
import com.qmy.zhongsheng.common.utils.ValidityUtils;
import com.qmy.zhongsheng.core.file.dao.SystemFileDAO;
import com.qmy.zhongsheng.core.file.enums.SystemFileMainTypeEnum;
import com.qmy.zhongsheng.core.file.enums.SystemFileSubTypeEnum;
import com.qmy.zhongsheng.core.file.manager.SystemFileManager;
import com.qmy.zhongsheng.core.file.model.entity.SystemFileDO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.qmy.zhongsheng.common.utils.ValidityUtils.isNotBlank;
import static com.qmy.zhongsheng.common.utils.ValidityUtils.nonNull;

/**
 * @author AI Coding
 * @description SystemFileManagerImpl
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
        return systemFileDAO.selectById(id);
    }

    @Override
    public Long upsertUserAvatarFile(Long existingFileId, String avatarUrl) {
        if (ValidityUtils.isBlank(avatarUrl)) {
            return existingFileId;
        }
        String url = avatarUrl.trim();
        if (existingFileId != null) {
            SystemFileDO row = systemFileDAO.selectById(existingFileId);
            if (row != null) {
                if (url.equals(row.getUrl())) {
                    return existingFileId;
                }
                row.setName(fileNameHintFromUrl(url));
                systemFileDAO.updateById(row);
                return existingFileId;
            }
        }
        SystemFileDO insert = new SystemFileDO();
        insert.setMainType(SystemFileMainTypeEnum.USER.getCode());
        insert.setSubType(SystemFileSubTypeEnum.USER_AVATAR.getCode());
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
                .eq(SystemFileDO::getIsDeleted, 0)
                .in(SystemFileDO::getSubType, subCodes)
                .orderByDesc(SystemFileDO::getId));
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
        List<SystemFileDO> existingRows = systemFileDAO.selectList(Wrappers.<SystemFileDO>lambdaQuery().in(SystemFileDO::getUrl, urls).eq(SystemFileDO::getIsDeleted, 0));
        Map<String, SystemFileDO> byUrl = existingRows.stream().collect(Collectors.toMap(SystemFileDO::getUrl, r -> r));
        for (SystemFileDO row : rows) {
            String url = row.getUrl();
            SystemFileDO existing = byUrl.get(url);
            Long existingId = nonNull(existing) ? existing.getId() : row.getId();
            if (existingId != null) {
                SystemFileDO updateRow = BeanUtils.toBean(row, SystemFileDO.class);
                updateRow.setId(existingId);
                updateRow.setMainType(mainCode);
                updateRow.setSubType(subCode);
                systemFileDAO.updateById(updateRow);
            } else {
                SystemFileDO insertRow = BeanUtils.toBean(row, SystemFileDO.class);
                insertRow.setMainType(mainCode);
                insertRow.setSubType(subCode);
                systemFileDAO.insert(insertRow);
            }
        }
    }

    @Override
    public List<SystemFileDO> listByMainSubAndMasterId(SystemFileMainTypeEnum mainType, SystemFileSubTypeEnum subType, Long masterId) {
        if (masterId == null || subType.getMainType() != mainType) {
            return List.of();
        }
        return systemFileDAO.selectList(Wrappers.<SystemFileDO>lambdaQuery()
                .eq(SystemFileDO::getMainType, mainType.getCode())
                .eq(SystemFileDO::getSubType, subType.getCode())
                .eq(SystemFileDO::getMasterId, masterId)
                .orderByAsc(SystemFileDO::getId));
    }

    @Override
    public List<SystemFileDO> listByMainSubAndMasterIds(SystemFileMainTypeEnum mainType, SystemFileSubTypeEnum subType, List<Long> masterIds) {
        if (CollectionUtils.isEmpty(masterIds) || subType.getMainType() != mainType) {
            return List.of();
        }
        return systemFileDAO.selectList(Wrappers.<SystemFileDO>lambdaQuery()
                .eq(SystemFileDO::getMainType, mainType.getCode())
                .eq(SystemFileDO::getSubType, subType.getCode())
                .in(SystemFileDO::getMasterId, masterIds)
                .eq(SystemFileDO::getIsDeleted, 0)
                .orderByAsc(SystemFileDO::getId));
    }

    @Override
    public void deleteByMainSubAndMasterId(SystemFileMainTypeEnum mainType, SystemFileSubTypeEnum subType, Long masterId) {
        if (masterId == null || subType.getMainType() != mainType) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        systemFileDAO.update(null, Wrappers.<SystemFileDO>lambdaUpdate()
                .eq(SystemFileDO::getMainType, mainType.getCode())
                .eq(SystemFileDO::getSubType, subType.getCode())
                .eq(SystemFileDO::getMasterId, masterId)
                .eq(SystemFileDO::getIsDeleted, 0)
                .set(SystemFileDO::getIsDeleted, 1)
                .set(SystemFileDO::getDeletedTime, now));
    }

    @Override
    public Long save(String url, String fileName, String type, Long size) {
        SystemFileDO sysStorage = new SystemFileDO();
        // 从 URL 中提取 endpoint 和 key
        sysStorage.setEndpoint(StrUtils.parseEndpoint(url));
        sysStorage.setUrl(url);
        String key = StrUtils.parseKey(url);
        sysStorage.setFileKey(StrUtils.parseKey(url));
        sysStorage.setName(nonNull(fileName) ? fileName : key.trim());
        sysStorage.setType(type);
        sysStorage.setSize(size);
        systemFileDAO.insert(sysStorage);
        return sysStorage.getId();
    }
}
