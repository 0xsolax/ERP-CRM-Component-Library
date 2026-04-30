package com.qmy.zhongsheng.core.process.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.core.process.model.entity.ProcessDO;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 工序持久化与查询。
 *
 * @author AI Coding
 */
public interface ProcessManager {

    /**
     * 单条保存或更新：{@code id} 为 {@code null} 时插入，否则按主键更新。
     *
     * @param row 待持久化的实体
     * @return 记录主键 id
     */
    Long saveOrUpdate(ProcessDO row);

    /**
     * 按 id 查询。
     *
     * @param id 主键 id
     * @return 匹配的记录
     */
    ProcessDO getById(Long id);

    /**
     * 检查工序名称是否已存在（排除指定 id）。
     *
     * @param name 工序名称
     * @param excludeId 排除的 id（更新时排除自身）
     * @return 是否存在
     */
    boolean existsByName(String name, Long excludeId);

    /**
     * 分页查询：支持名称模糊匹配。
     *
     * @param name 名称模糊匹配条件，为 null 时不加该条件
     * @param pageNum 当前页
     * @param pageSize 每页条数
     * @return 分页结果
     */
    Page<ProcessDO> page(String name, Integer pageNum, Integer pageSize);

    /**
     * 查询所有
     *
     * @return 工序列表
     */
    List<ProcessDO> list();

    Boolean delete(Long id);
}