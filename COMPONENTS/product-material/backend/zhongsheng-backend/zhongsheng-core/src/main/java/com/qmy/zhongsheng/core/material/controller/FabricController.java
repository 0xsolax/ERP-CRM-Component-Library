package com.qmy.zhongsheng.core.material.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.api.dto.material.FabricListQueryDTO;
import com.qmy.zhongsheng.api.dto.material.FabricSaveDTO;
import com.qmy.zhongsheng.api.dto.material.FabricSelectQueryDTO;
import com.qmy.zhongsheng.api.reponse.PageResponse;
import com.qmy.zhongsheng.api.reponse.ResultInfo;
import com.qmy.zhongsheng.api.request.IdRequestParam;
import com.qmy.zhongsheng.core.material.model.vo.FabricVO;
import com.qmy.zhongsheng.core.material.service.FabricService;
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
 * 面料管理接口。
 *
 * @author AI Coding
 */
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/fabric")
@Tag(name = "面料管理", description = "维护 fabric 面料数据")
public class FabricController {

    private final FabricService fabricService;

    /**
     * 保存或更新面料。
     *
     * @param dto 请求体无 id 为新增；有 id 为更新；isDeleted=1 时软删除
     * @return 统一响应，data 为记录主键 id
     */
    @PostMapping("/saveOrUpdate")
    @PreAuthorize("@ss.hasPermission(@ss.perm('FABRIC_SAVE_OR_UPDATE'))")
    @Operation(summary = "保存或更新面料", description = "无 id 为新增；有 id 为更新；isDeleted=1 时执行软删除")
    public ResultInfo<Long> saveOrUpdate(@Valid @RequestBody FabricSaveDTO dto) {
        return ResultInfo.success(fabricService.saveOrUpdate(dto));
    }

    /**
     * 分页查询面料列表。
     *
     * @param query 查询条件，支持种类、型号模糊搜索
     * @return 统一响应，data 为分页结果
     */
    @PostMapping("/page")
    @PreAuthorize("@ss.hasPermission(@ss.perm('FABRIC_PAGE'))")
    @Operation(summary = "分页查询面料列表", description = "支持面料种类、型号模糊搜索")
    public ResultInfo<PageResponse<FabricVO>> page(@RequestBody FabricListQueryDTO query) {
        Page<FabricVO> page = fabricService.page(query);
        return ResultInfo.success(PageResponse.of(page.getCurrent(), page.getSize(), page.getTotal(), page.getRecords()));
    }

    /**
     * 查询面料列表（不分页），用于产品保存时下拉选择；可按种类、型号 baseDataId 筛选。
     *
     * @param query 筛选条件，可为空；未传或字段为 null 表示不按该维度过滤
     * @return 统一响应，data 为 {@link FabricVO} 列表
     */
    @PostMapping("/list")
    @PreAuthorize("@ss.hasPermission(@ss.perm('FABRIC_LIST'))")
    @Operation(summary = "查询面料列表（下拉）", description = "不分页；可按种类、型号 baseDataId 筛选，用于产品保存时选择面料")
    public ResultInfo<List<FabricVO>> listForSelect(@RequestBody(required = false) FabricSelectQueryDTO query) {
        return ResultInfo.success(fabricService.listForSelect(query));
    }

    @PostMapping("/deteil")
    @PreAuthorize("@ss.hasPermission(@ss.perm('FABRIC_DETAIL'))")
    @Operation(summary = "查询面料详情", description = "根据 id 查询")
    public ResultInfo<FabricVO> getDetail(@RequestBody IdRequestParam param) {
        return ResultInfo.success(fabricService.getDetail(param.getId()));
    }


    /**
     * 删除面料
     *
     * @param param 请求体
     * @return 统一响应，data 为 true/false
     */
    @PostMapping("/delete")
    @PreAuthorize("@ss.hasPermission(@ss.perm('FABRIC_DELETE'))")
    @Operation(summary = "删除面料", description = "根据面料 id 删除")
    public ResultInfo<Boolean> delete(@RequestBody IdRequestParam param) {
        return ResultInfo.success(fabricService.delete(param.getId()));
    }
}