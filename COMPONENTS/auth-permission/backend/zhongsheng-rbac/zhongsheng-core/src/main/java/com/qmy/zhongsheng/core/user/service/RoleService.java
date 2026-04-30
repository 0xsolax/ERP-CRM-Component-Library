package com.qmy.zhongsheng.core.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.api.dto.role.RoleListQueryDTO;
import com.qmy.zhongsheng.core.user.model.vo.RolePageVO;

/**
 * 角色查询。
 *
 * @author AI Coding
 */
public interface RoleService {

    /**
     * 分页查询角色，并填充各角色已绑定的权限标识（菜单 permission）。
     *
     * @param query 分页与名称筛选
     * @return 分页数据
     */
    Page<RolePageVO> page(RoleListQueryDTO query);
}
