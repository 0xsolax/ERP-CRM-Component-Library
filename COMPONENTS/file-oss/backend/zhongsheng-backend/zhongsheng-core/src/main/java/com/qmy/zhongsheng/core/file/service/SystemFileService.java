package com.qmy.zhongsheng.core.file.service;

import com.qmy.zhongsheng.api.dto.file.SystemFileDTO;
import com.qmy.zhongsheng.core.file.enums.SystemFileMainTypeEnum;
import com.qmy.zhongsheng.core.file.enums.SystemFileSubTypeEnum;
import com.qmy.zhongsheng.core.file.model.vo.FileVO;

import java.util.List;
import java.util.Map;

/**
 * @author AI Coding
 * @description SystemFileService
 * @date 2026/03/26
 */
public interface SystemFileService {


    /**
     * 按主类型将文件按次类型分组；仅包含该主类型下已定义的次类型，无数据则为空列表。
     * @param mainType 主类型
     * @return 按次类型分组的文件
     */
    Map<SystemFileSubTypeEnum, List<FileVO>> listFilesGroupedByMainType(SystemFileMainTypeEnum mainType);

    /**
     * 批量保存或更新文件列表
     * <p>
     * 将 {@link SystemFileDTO} 列表转换为 {@code system_file} 记录，统一设置主类型、次类型与关联主键。
     * 过滤掉 {@code null} 或 {@code url} 为空的项。
     * {@code storageId} 会映射为文件记录主键 {@code id}，用于更新已存在的记录。
     *
     * @param masterId  主对象 ID（如材料 ID、伞架 ID）
     * @param mainType  文件主类型
     * @param subType   文件次类型
     * @param files     文件 DTO 列表
     */
    void saveFiles(Long masterId, SystemFileMainTypeEnum mainType, SystemFileSubTypeEnum subType, List<SystemFileDTO> files);

}
