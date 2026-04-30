package com.qmy.zhongsheng.core.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.api.dto.role.RoleListQueryDTO;
import com.qmy.zhongsheng.api.reponse.PageResponse;
import com.qmy.zhongsheng.api.reponse.ResultInfo;
import com.qmy.zhongsheng.core.user.model.vo.RolePageVO;
import com.qmy.zhongsheng.core.user.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 角色管理接口。
 *
 * @author AI Coding
 */
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/role")
@Tag(name = "角色管理", description = "系统角色与权限")
public class RoleController {

    private final RoleService roleService;

    /**
     * 分页查询角色列表（含权限标识）。
     *
     * @param query 分页与筛选条件
     * @return 统一响应，data 为分页结果
     */
    @PostMapping("/page")
    @PreAuthorize("@ss.hasPermission(@ss.perm('ROLE_PAGE'))")
    @Operation(summary = "分页查询角色列表", description = "返回 id、name、desc、enabled、permissions（菜单 permission 集合）")
    public ResultInfo<PageResponse<RolePageVO>> page(@RequestBody RoleListQueryDTO query) {
        Page<RolePageVO> page = roleService.page(query);
        return ResultInfo.success(PageResponse.of(
                page.getCurrent(), page.getSize(), page.getTotal(), page.getRecords()));
    }
}
