package com.qmy.project.core.file.manager;

import com.qmy.project.core.file.enums.SystemFileMainTypeEnum;
import com.qmy.project.core.file.enums.SystemFileSubTypeEnum;
import com.qmy.project.core.file.model.entity.SystemFileDO;

import java.util.List;
import java.util.Set;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/26
 */
public interface SystemFileManager {

    /**
     * 按主键查询文件；不存在或已逻辑删除则返回 {@code null}。
     */
    SystemFileDO getById(Long id);

    /**
     * 维护用户第三方头像对应的 {@code system_file} 行：{@code url} 为外网地址；无 url 时返回已有 {@code existingFileId}。
     */
    Long upsertUserAvatarFile(Long existingFileId, String avatarUrl);

    /**
     * 按主类型 + 次类型查询文件列表。
     */
    List<SystemFileDO> listByMainAndSub(SystemFileMainTypeEnum mainType, SystemFileSubTypeEnum subType);

    /**
     * 按主类型一次性查询多个次类型（单次 SQL），调用方自行分组。
     */
    List<SystemFileDO> listByMainAndSubTypes(SystemFileMainTypeEnum mainType, List<SystemFileSubTypeEnum> subTypes);

    /**
     * 保存或更新文件
     * @param mainType 文件主类型
     * @param subType 文件次类型
     * @param items 文件列表，url 不能为空
     */
    void saveOrUpdate(SystemFileMainTypeEnum mainType, SystemFileSubTypeEnum subType, List<SystemFileDO> items);
}
