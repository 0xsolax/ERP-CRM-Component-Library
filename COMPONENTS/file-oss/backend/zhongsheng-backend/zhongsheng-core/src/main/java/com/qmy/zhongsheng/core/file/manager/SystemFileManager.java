package com.qmy.zhongsheng.core.file.manager;

import com.qmy.zhongsheng.core.file.enums.SystemFileMainTypeEnum;
import com.qmy.zhongsheng.core.file.enums.SystemFileSubTypeEnum;
import com.qmy.zhongsheng.core.file.model.entity.SystemFileDO;

import java.util.List;

/**
 * @author AI Coding
 * @description SystemFileManager
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

    /**
     * 按主类型 + 次类型 + masterId 查询文件列表。
     */
    List<SystemFileDO> listByMainSubAndMasterId(SystemFileMainTypeEnum mainType, SystemFileSubTypeEnum subType, Long masterId);

    /**
     * 按主类型 + 次类型 + masterId 批量查询文件（单次 SQL）。
     */
    List<SystemFileDO> listByMainSubAndMasterIds(SystemFileMainTypeEnum mainType, SystemFileSubTypeEnum subType, List<Long> masterIds);

    /**
     * 将指定主类型、次类型、主从 ID 下未逻辑删除的文件统一标记删除（{@code is_deleted=1} 并写入 {@code deleted_time}）。
     *
     * @param mainType 文件主类型
     * @param subType  文件次类型
     * @param masterId 主从 ID（如租户 ID）
     */
    void deleteByMainSubAndMasterId(SystemFileMainTypeEnum mainType, SystemFileSubTypeEnum subType, Long masterId);

    /**
     * 保存文件存储记录（无主从类型分类）。
     * 将 URL 拆分为 endpoint 和 key 存储，解决 OSS 地址变更问题。
     *
     * @param url      完整 URL
     * @param fileName 文件名称
     * @param type     文件类型（MIME类型）
     * @param size     文件大小（字节）
     * @return 保存后的主键 ID
     */
    Long save(String url, String fileName, String type, Long size);
}
