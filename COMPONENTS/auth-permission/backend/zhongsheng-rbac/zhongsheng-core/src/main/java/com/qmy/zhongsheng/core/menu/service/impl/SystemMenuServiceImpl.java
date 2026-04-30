package com.qmy.zhongsheng.core.menu.service.impl;

import com.qmy.zhongsheng.api.dto.menu.SystemMenuListQueryDTO;
import com.qmy.zhongsheng.api.dto.menu.SystemMenuSaveDTO;
import com.qmy.zhongsheng.common.enums.MenuTypeEnum;
import com.qmy.zhongsheng.common.error.MenuErrorCodeConstants;
import com.qmy.zhongsheng.common.exception.ServiceExceptionUtil;
import com.qmy.zhongsheng.common.utils.BeanUtils;
import com.qmy.zhongsheng.core.menu.manager.SystemMenuManager;
import com.qmy.zhongsheng.core.menu.model.entity.SystemMenuDO;
import com.qmy.zhongsheng.core.menu.model.vo.SystemMenuVO;
import com.qmy.zhongsheng.core.menu.service.SystemMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 系统菜单服务实现。
 *
 * @author 单漪甜
 */
@Service
@RequiredArgsConstructor
public class SystemMenuServiceImpl implements SystemMenuService {

    private final SystemMenuManager systemMenuManager;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveOrUpdate(SystemMenuSaveDTO dto) {
        // 校验菜单类型
        validateMenuType(dto.getType());

        // 校验父级菜单（非根节点时）
        if (dto.getParentId() != null && dto.getParentId() != 0L) {
            SystemMenuDO parent = systemMenuManager.getById(dto.getParentId());
            if (parent == null || parent.getIsDeleted() == 1) {
                throw ServiceExceptionUtil.exception(MenuErrorCodeConstants.MENU_PARENT_NOT_FOUND);
            }
        }

        // 校验权限标识唯一性
        validatePermissionUnique(dto.getPermission(), dto.getId());

        // 更新时校验菜单是否存在
        if (dto.getId() != null) {
            SystemMenuDO existing = systemMenuManager.getById(dto.getId());
            if (existing == null || existing.getIsDeleted() == 1) {
                throw ServiceExceptionUtil.exception(MenuErrorCodeConstants.MENU_NOT_FOUND);
            }
        }

        SystemMenuDO row = BeanUtils.toBean(dto, SystemMenuDO.class);
        return systemMenuManager.saveOrUpdate(row);
    }

    @Override
    public List<SystemMenuVO> list(SystemMenuListQueryDTO query) {
        if (query == null) {
            query = new SystemMenuListQueryDTO();
        }
        List<SystemMenuDO> rows = systemMenuManager.list(query);
        return BeanUtils.toBean(rows, SystemMenuVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(Long id) {
        // 校验菜单是否存在
        SystemMenuDO existing = systemMenuManager.getById(id);
        if (existing == null || existing.getIsDeleted() == 1) {
            throw ServiceExceptionUtil.exception(MenuErrorCodeConstants.MENU_NOT_FOUND);
        }

        // 校验是否存在子菜单
        if (systemMenuManager.existsByParentId(id)) {
            throw ServiceExceptionUtil.exception(MenuErrorCodeConstants.MENU_HAS_CHILDREN);
        }

        return systemMenuManager.delete(id);
    }

    /**
     * 校验菜单类型是否有效。
     *
     * @param type 菜单类型
     */
    private void validateMenuType(Integer type) {
        if (type == null || MenuTypeEnum.fromCode(type) == null) {
            throw ServiceExceptionUtil.exception(MenuErrorCodeConstants.MENU_TYPE_INVALID);
        }
    }

    /**
     * 校验权限标识唯一性。
     *
     * @param permission 权限标识
     * @param excludeId 排除的 id（更新时使用）
     */
    private void validatePermissionUnique(String permission, Long excludeId) {
        SystemMenuDO duplicate = systemMenuManager.getByPermission(permission, excludeId);
        if (duplicate != null) {
            throw ServiceExceptionUtil.exception(MenuErrorCodeConstants.MENU_PERMISSION_DUPLICATE);
        }
    }
}