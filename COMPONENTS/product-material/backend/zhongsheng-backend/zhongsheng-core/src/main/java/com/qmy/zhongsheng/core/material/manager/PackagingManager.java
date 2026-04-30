package com.qmy.zhongsheng.core.material.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.core.material.model.entity.PackagingDO;

/**
 * 包材持久化与查询。
 *
 * @author AI Coding
 */
public interface PackagingManager {

    /**
     * 单条保存或更新：{@code id} 为 {@code null} 时插入，否则按主键更新。
     *
     * @param row 待持久化的实体
     * @return 记录主键 id
     */
    Long saveOrUpdate(PackagingDO row);

    /**
     * 按 id 查询。
     *
     * @param id 主键 id
     * @return 匹配的记录
     */
    PackagingDO getById(Long id);

    /**
     * 分页查询：支持类型、尺寸匹配。
     *
     * @param typeId 类型ID（baseDataId），为 null 时不加该条件
     * @param size 尺寸匹配条件，为 null 时不加该条件
     * @param keyword 关键词，支持包材类型名称、包材名称、尺寸、单价模糊搜索，为 null 时不加该条件
     * @param defaultTypeFlag 是否为默认类型包材，为 null 或空时不加该条件
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    Page<PackagingDO> page(Long typeId, String size, String keyword, String defaultTypeFlag, Integer pageNum, Integer pageSize);

    /**
     * 删除：按 id 删除。
     *
     * @param id 主键 id
     * @return 是否成功
     */
    Boolean deleted(Long id);

    /**
     * 按类型 ID 和名称查询包材（用于重复性校验）
     *
     * @param typeId 类型 ID
     * @param name 包材名称
     * @return 匹配的记录
     */
    PackagingDO getByTypeIdAndName(Long typeId, String name);
}