package com.qmy.project.core.file.service.impl;

import com.qmy.project.api.dto.file.SystemFileDTO;
import com.qmy.project.common.utils.BeanUtils;
import com.qmy.project.common.utils.ValidityUtils;
import com.qmy.project.core.file.enums.SystemFileMainTypeEnum;
import com.qmy.project.core.file.enums.SystemFileSubTypeEnum;
import com.qmy.project.core.file.manager.SystemFileManager;
import com.qmy.project.core.file.model.entity.SystemFileDO;
import com.qmy.project.core.file.model.vo.FileVO;
import com.qmy.project.core.file.service.SystemFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/26
 */
@Service
@RequiredArgsConstructor
public class SystemFileServiceImpl implements SystemFileService {

    private final SystemFileManager systemFileManager;

    @Override
    public Map<SystemFileSubTypeEnum, List<FileVO>> listFilesGroupedByMainType(SystemFileMainTypeEnum mainType) {
        List<SystemFileSubTypeEnum> subs = SystemFileSubTypeEnum.subTypesOf(mainType);
        Map<SystemFileSubTypeEnum, List<FileVO>> result = new LinkedHashMap<>();
        for (SystemFileSubTypeEnum sub : subs) {
            result.put(sub, new ArrayList<>());
        }
        List<SystemFileDO> rows = systemFileManager.listByMainAndSubTypes(mainType, subs);
        Map<String, List<SystemFileDO>> bySubCode = rows.stream()
                .collect(Collectors.groupingBy(SystemFileDO::getSubType));
        for (Map.Entry<String, List<SystemFileDO>> e : bySubCode.entrySet()) {
            SystemFileSubTypeEnum subEnum = SystemFileSubTypeEnum.fromCodeUnderMain(e.getKey(), mainType);
            if (subEnum == null) {
                continue;
            }
            result.put(subEnum, e.getValue().stream().map(this::toFileVo).toList());
        }
        return result;
    }

    @Override
    public void saveFiles(Long masterId, SystemFileMainTypeEnum mainType, SystemFileSubTypeEnum subType, List<SystemFileDTO> files) {
        if (ValidityUtils.isEmpty(files)) {
            return;
        }
        List<SystemFileDO> fileDOList = new ArrayList<>();
        for (SystemFileDTO file : files) {
            if (file != null && file.getUrl() != null) {
                SystemFileDO fileDO = BeanUtils.toBean(file, SystemFileDO.class);
                fileDO.setId(file.getStorageId());
                fileDO.setFileKey(file.getFileKey());
                fileDO.setEndpoint(file.getEndpoint());
                fileDO.setMainType(mainType.getCode());
                fileDO.setSubType(subType.getCode());
                fileDO.setMasterId(masterId);
                fileDOList.add(fileDO);
            }
        }

        if (!fileDOList.isEmpty()) {
            systemFileManager.saveOrUpdate(mainType, subType, fileDOList);
        }
    }

    private FileVO toFileVo(SystemFileDO fileDO) {
        return BeanUtils.toBean(fileDO, FileVO.class);
    }
}
