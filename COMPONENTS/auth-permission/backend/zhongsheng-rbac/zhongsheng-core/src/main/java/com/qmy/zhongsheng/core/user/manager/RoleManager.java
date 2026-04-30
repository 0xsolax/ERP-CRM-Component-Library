package com.qmy.zhongsheng.core.user.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.api.dto.role.RoleListQueryDTO;
import com.qmy.zhongsheng.core.user.model.condition.RoleQueryCondition;
import com.qmy.zhongsheng.core.user.model.entity.RoleDO;

import java.util.Collection;
import java.util.List;

/**
 * 系统角色查询与持久化。
 *
 * @author AI Coding
 */
public interface RoleManager {

    /**
     * 分页查询未逻辑删除的角色，支持按名称模糊筛选。
     *
     * @param condition 分页参数与名称
     * @return 分页数据
     */
    Page<RoleDO> page(RoleQueryCondition condition);

    /**
     * 按主键 id 列表查询未逻辑删除的角色。
     *
     * @param ids 主键 id 集合，为 null 或空时返回空列表
     * @return 角色列表
     */
    List<RoleDO> listByIds(Collection<Long> ids);
}
