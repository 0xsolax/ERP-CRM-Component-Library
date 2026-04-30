package com.qmy.zhongsheng.core.user.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.common.utils.ValidityUtils;
import com.qmy.zhongsheng.core.user.dao.RoleDAO;
import com.qmy.zhongsheng.core.user.manager.RoleManager;
import com.qmy.zhongsheng.core.user.model.condition.RoleQueryCondition;
import com.qmy.zhongsheng.core.user.model.entity.RoleDO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

import static com.qmy.zhongsheng.common.utils.ValidityUtils.isNotBlank;

/**
 * @author AI Coding
 */
@Component
@RequiredArgsConstructor
public class RoleManagerImpl implements RoleManager {

    private final RoleDAO roleDAO;

    @Override
    public Page<RoleDO> page(RoleQueryCondition condition) {
        Page<RoleDO> page = new Page<>(condition.getPageNum(), condition.getPageSize());
        LambdaQueryWrapper<RoleDO> w = Wrappers.<RoleDO>lambdaQuery();
        w.eq(RoleDO::getIsDeleted, 0);
        if (isNotBlank(condition.getLikeName())) {
            w.like(RoleDO::getName, condition.getLikeName().trim());
        }
        w.orderByAsc(RoleDO::getId);
        return roleDAO.selectPage(page, w);
    }

    @Override
    public List<RoleDO> listByIds(Collection<Long> ids) {
        if (ids == null || ValidityUtils.isEmpty(ids)) {
            return List.of();
        }
        return roleDAO.selectList(Wrappers.<RoleDO>lambdaQuery()
                .in(RoleDO::getId, ids)
                .eq(RoleDO::getIsDeleted, 0));
    }
}
