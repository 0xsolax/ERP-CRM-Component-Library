package com.qmy.zhongsheng.core.menu.manager;

import com.qmy.zhongsheng.api.dto.menu.SystemMenuListQueryDTO;
import com.qmy.zhongsheng.core.menu.model.entity.SystemMenuDO;

import java.util.List;

/**
 * 系统菜单持久化与查询。
 *
 * @author 单漪甜
 */
public interface SystemMenuManager {

    /**
     * 单条保存或更新：{@code id} 为 {@code null} 时插入（插入后回填 id），否则按主键更新。
     *
     * @param row 待持久化的实体
     * @return 记录主键 id
     */
    Long saveOrUpdate(SystemMenuDO row);

    /**
     * 按 id 查询。
     *
     * @param id 主键 id
     * @return 匹配的记录，不存在时返回 null
     */
    SystemMenuDO getById(Long id);

    /**
     * 按权限标识查询（用于唯一性校验）。
     *
     * @param permission 权限标识
     * @param excludeId  排除的 id（更新时使用，避免与自己比较）
     * @return 匹配的记录，不存在时返回 null
     */
    SystemMenuDO getByPermission(String permission, Long excludeId);

    /**
     * 按条件查询列表。
     *
     * @param query 查询条件
     * @return 菜单列表
     */
    List<SystemMenuDO> list(SystemMenuListQueryDTO query);

    /**
     * 按主键 id 列表查询未逻辑删除的菜单。
     *
     * @param ids 主键 id 列表，为 null 或空时返回空列表
     * @return 菜单列表
     */
    List<SystemMenuDO> listByIds(List<Long> ids);

    /**
     * 判断是否存在子菜单。
     *
     * @param parentId 父级菜单ID
     * @return 是否存在子菜单
     */
    boolean existsByParentId(Long parentId);

    /**
     * 逻辑删除。
     *
     * @param id 主键 id
     * @return 删除结果
     */
    Boolean delete(Long id);
}