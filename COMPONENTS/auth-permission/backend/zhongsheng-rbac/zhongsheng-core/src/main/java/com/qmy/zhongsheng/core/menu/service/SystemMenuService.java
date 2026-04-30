package com.qmy.zhongsheng.core.menu.service;

import com.qmy.zhongsheng.api.dto.menu.SystemMenuListQueryDTO;
import com.qmy.zhongsheng.api.dto.menu.SystemMenuSaveDTO;
import com.qmy.zhongsheng.core.menu.model.vo.SystemMenuVO;

import java.util.List;

/**
 * 系统菜单服务。
 *
 * @author 单漪甜
 */
public interface SystemMenuService {

    /**
     * 单条保存或更新：无 id 为新增，有 id 为按非 null 字段更新。
     *
     * @param dto 保存请求体
     * @return 记录主键 id
     */
    Long saveOrUpdate(SystemMenuSaveDTO dto);

    /**
     * 查询菜单列表。
     *
     * @param query 查询条件，可空表示不加过滤
     * @return 菜单列表
     */
    List<SystemMenuVO> list(SystemMenuListQueryDTO query);

    /**
     * 删除菜单。
     *
     * @param id 记录主键 id
     * @return 删除结果
     */
    Boolean delete(Long id);
}