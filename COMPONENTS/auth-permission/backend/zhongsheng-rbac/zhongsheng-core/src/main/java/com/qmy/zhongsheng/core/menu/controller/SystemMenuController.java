package com.qmy.zhongsheng.core.menu.controller;

import com.qmy.zhongsheng.api.dto.menu.SystemMenuListQueryDTO;
import com.qmy.zhongsheng.api.dto.menu.SystemMenuSaveDTO;
import com.qmy.zhongsheng.api.reponse.ResultInfo;
import com.qmy.zhongsheng.api.request.IdRequestParam;
import com.qmy.zhongsheng.core.menu.model.vo.SystemMenuVO;
import com.qmy.zhongsheng.core.menu.service.SystemMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 系统菜单管理接口。
 *
 * @author 单漪甜
 */
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/menu")
@Tag(name = "菜单管理", description = "维护 system_menu 系统菜单数据")
public class SystemMenuController {

    private final SystemMenuService systemMenuService;

    /**
     * 保存或更新菜单。
     *
     * @param dto 请求体无 id 为新增；有 id 为更新
     * @return 统一响应，data 为记录主键 id
     */
    @PostMapping("/saveOrUpdate")
    @PreAuthorize("@ss.hasPermission(@ss.perm('MENU_SAVE_OR_UPDATE'))")
    @Operation(summary = "保存或更新菜单", description = "无 id 为新增；有 id 为更新")
    public ResultInfo<Long> saveOrUpdate(@Valid @RequestBody SystemMenuSaveDTO dto) {
        return ResultInfo.success(systemMenuService.saveOrUpdate(dto));
    }

    /**
     * 查询菜单列表。
     *
     * @param query 查询条件，支持多维度筛选
     * @return 统一响应，data 为菜单列表（扁平结构）
     */
    @PostMapping("/list")
    @PreAuthorize("@ss.hasPermission(@ss.perm('MENU_LIST'))")
    @Operation(summary = "查询菜单列表", description = "返回扁平结构菜单列表，前端自行构建树形")
    public ResultInfo<List<SystemMenuVO>> list(@RequestBody(required = false) SystemMenuListQueryDTO query) {
        return ResultInfo.success(systemMenuService.list(query));
    }

    /**
     * 删除菜单。
     *
     * @param idRequestParam 菜单 ID 请求参数
     * @return 统一响应，data 为删除结果
     */
    @PostMapping("/delete")
    @PreAuthorize("@ss.hasPermission(@ss.perm('MENU_DELETE'))")
    @Operation(summary = "删除菜单", description = "逻辑删除，存在子菜单时不允许删除")
    public ResultInfo<Boolean> delete(@RequestBody @Valid IdRequestParam idRequestParam) {
        return ResultInfo.success(systemMenuService.delete(idRequestParam.getId()));
    }
}