package com.qmy.zhongsheng.core.material.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.api.dto.material.PackagingListQueryDTO;
import com.qmy.zhongsheng.api.dto.material.PackagingSaveDTO;
import com.qmy.zhongsheng.api.reponse.PageResponse;
import com.qmy.zhongsheng.api.reponse.ResultInfo;
import com.qmy.zhongsheng.api.request.IdRequestParam;
import com.qmy.zhongsheng.core.material.model.vo.PackagingVO;
import com.qmy.zhongsheng.core.material.service.PackagingService;
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
 * 包材管理接口。
 *
 * @author AI Coding
 */
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/packaging")
@Tag(name = "包材管理", description = "维护 packaging 包材数据")
public class PackagingController {

    private final PackagingService packagingService;

    /**
     * 保存或更新包材。
     *
     * @param dto 请求体无 id 为新增；有 id 为更新；isDeleted=1 时软删除
     * @return 统一响应，data 为记录主键 id
     */
    @PostMapping("/saveOrUpdate")
    @PreAuthorize("@ss.hasPermission(@ss.perm('PACKAGING_SAVE_OR_UPDATE'))")
    @Operation(summary = "保存或更新包材", description = "无 id 为新增；有 id 为更新；isDeleted=1 时执行软删除")
    public ResultInfo<Long> saveOrUpdate(@Valid @RequestBody PackagingSaveDTO dto) {
        return ResultInfo.success(packagingService.saveOrUpdate(dto));
    }

    /**
     * 批量保存或更新默认纸箱包材
     */
    @PostMapping("/saveOrUpdateDefaultPaperBox")
    @PreAuthorize("@ss.hasPermission(@ss.perm('PACKAGING_SAVE_DEFAULT'))")
    @Operation(summary = "批量保存或更新默认纸箱包材", description = "无 id 为新增；有 id 为更新；isDeleted=1 时执行软删除")
    public ResultInfo<List<Long>> saveOrUpdateDefaultPaperBox(@Valid @RequestBody List<PackagingSaveDTO> dto) {
        return ResultInfo.success(packagingService.saveOrUpdateDefaultPaperBox(dto));
    }

    /**
     * 分页查询包材列表。
     *
     * @param query 查询条件，支持类型、尺寸搜索
     * @return 统一响应，data 为分页结果
     */
    @PostMapping("/page")
    @PreAuthorize("@ss.hasPermission(@ss.perm('PACKAGING_PAGE'))")
    @Operation(summary = "分页查询包材列表", description = "支持包材类型、尺寸搜索")
    public ResultInfo<PageResponse<PackagingVO>> page(@RequestBody PackagingListQueryDTO query) {
        Page<PackagingVO> page = packagingService.page(query);
        return ResultInfo.success(PageResponse.of(page.getCurrent(), page.getSize(), page.getTotal(), page.getRecords()));
    }

    /**
     * 包材删除
     */
    @PostMapping("/delete")
    @PreAuthorize("@ss.hasPermission(@ss.perm('PACKAGING_DELETE'))")
    @Operation(summary = "包材-删除", description = "逻辑删除")
    public ResultInfo<Boolean> delete(@RequestBody IdRequestParam idRequestParam){
        return ResultInfo.success(packagingService.delete(idRequestParam.getId()));
    }


}