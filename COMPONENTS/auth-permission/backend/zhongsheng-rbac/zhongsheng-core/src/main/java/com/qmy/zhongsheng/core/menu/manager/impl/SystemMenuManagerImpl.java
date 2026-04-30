package com.qmy.zhongsheng.core.menu.manager.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.qmy.zhongsheng.api.dto.menu.SystemMenuListQueryDTO;
import com.qmy.zhongsheng.common.utils.ValidityUtils;
import com.qmy.zhongsheng.core.menu.dao.SystemMenuDAO;
import com.qmy.zhongsheng.core.menu.manager.SystemMenuManager;
import com.qmy.zhongsheng.core.menu.model.entity.SystemMenuDO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 系统菜单 Manager 实现。
 *
 * @author 单漪甜
 */
@Component
@RequiredArgsConstructor
public class SystemMenuManagerImpl implements SystemMenuManager {

    private final SystemMenuDAO systemMenuDAO;

    @Override
    public Long saveOrUpdate(SystemMenuDO row) {
        if (row.getId() == null) {
            systemMenuDAO.insert(row);
            return row.getId();
        }
        systemMenuDAO.updateById(row);
        return row.getId();
    }

    @Override
    public SystemMenuDO getById(Long id) {
        return systemMenuDAO.selectById(id);
    }

    @Override
    public SystemMenuDO getByPermission(String permission, Long excludeId) {
        return systemMenuDAO.selectOne(Wrappers.<SystemMenuDO>lambdaQuery()
                .eq(SystemMenuDO::getPermission, permission)
                .eq(SystemMenuDO::getIsDeleted, 0)
                .ne(excludeId != null, SystemMenuDO::getId, excludeId)
                .last("LIMIT 1"));
    }

    @Override
    public List<SystemMenuDO> list(SystemMenuListQueryDTO query) {
        return systemMenuDAO.selectList(Wrappers.<SystemMenuDO>lambdaQuery()
                .like(ValidityUtils.isNotBlank(query.getName()), SystemMenuDO::getName, query.getName())
                .like(ValidityUtils.isNotBlank(query.getPermission()), SystemMenuDO::getPermission, query.getPermission())
                .eq(query.getType() != null, SystemMenuDO::getType, query.getType())
                .eq(query.getParentId() != null, SystemMenuDO::getParentId, query.getParentId())
                .eq(query.getStatus() != null, SystemMenuDO::getStatus, query.getStatus())
                .eq(query.getVisible() != null, SystemMenuDO::getVisible, query.getVisible())
                .eq(SystemMenuDO::getIsDeleted, 0)
                .orderByAsc(SystemMenuDO::getSort)
                .orderByAsc(SystemMenuDO::getId));
    }

    @Override
    public List<SystemMenuDO> listByIds(List<Long> ids) {
        if (ids == null || ValidityUtils.isEmpty(ids)) {
            return List.of();
        }
        List<Long> distinct = ids.stream().filter(Objects::nonNull).distinct().toList();
        if (ValidityUtils.isEmpty(distinct)) {
            return List.of();
        }
        return systemMenuDAO.selectList(Wrappers.<SystemMenuDO>lambdaQuery()
                .in(SystemMenuDO::getId, distinct)
                .eq(SystemMenuDO::getIsDeleted, 0));
    }

    @Override
    public boolean existsByParentId(Long parentId) {
        return systemMenuDAO.selectCount(Wrappers.<SystemMenuDO>lambdaQuery()
                .eq(SystemMenuDO::getParentId, parentId)
                .eq(SystemMenuDO::getIsDeleted, 0)) > 0;
    }

    @Override
    public Boolean delete(Long id) {
        return systemMenuDAO.update(Wrappers.<SystemMenuDO>lambdaUpdate()
                .eq(SystemMenuDO::getId, id)
                .set(SystemMenuDO::getIsDeleted, 1)
                .set(SystemMenuDO::getDeletedTime, LocalDateTime.now())) > 0;
    }
}