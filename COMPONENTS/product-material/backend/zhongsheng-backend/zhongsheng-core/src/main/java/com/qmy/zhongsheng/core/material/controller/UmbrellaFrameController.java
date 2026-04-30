package com.qmy.zhongsheng.core.material.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.api.dto.material.UmbrellaFrameListQueryDTO;
import com.qmy.zhongsheng.api.dto.material.UmbrellaFrameSaveDTO;
import com.qmy.zhongsheng.api.dto.material.UmbrellaFrameSelectQueryDTO;
import com.qmy.zhongsheng.api.reponse.PageResponse;
import com.qmy.zhongsheng.api.reponse.ResultInfo;
import com.qmy.zhongsheng.api.request.IdRequestParam;
import com.qmy.zhongsheng.core.material.model.vo.UmbrellaFrameDetailVO;
import com.qmy.zhongsheng.core.material.model.vo.UmbrellaFrameVO;
import com.qmy.zhongsheng.core.material.service.UmbrellaFrameService;
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
 * 伞架管理接口。
 *
 * @author AI Coding
 */
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/umbrellaFrame")
@Tag(name = "伞架管理", description = "维护 umbrella_frame 伞架数据")
public class UmbrellaFrameController {

    private final UmbrellaFrameService umbrellaFrameService;

    /**
     * 保存或更新伞架。
     *
     * @param dto 请求体无 id 为新增；有 id 为更新；isDeleted=1 时软删除
     * @return 统一响应，data 为记录主键 id
     */
    @PostMapping("/saveOrUpdate")
    @PreAuthorize("@ss.hasPermission(@ss.perm('UMBRELLA_FRAME_SAVE_OR_UPDATE'))")
    @Operation(summary = "保存或更新伞架", description = "无 id 为新增；有 id 为更新；isDeleted=1 时执行软删除")
    public ResultInfo<Long> saveOrUpdate(@Valid @RequestBody UmbrellaFrameSaveDTO dto) {
        return ResultInfo.success(umbrellaFrameService.saveOrUpdate(dto));
    }

    /**
     * 分页查询伞架列表。
     *
     * @param query 查询条件，支持功能、类型、尺寸、材料筛选
     * @return 统一响应，data 为分页结果
     */
    @PostMapping("/page")
    @PreAuthorize("@ss.hasPermission(@ss.perm('UMBRELLA_FRAME_PAGE'))")
    @Operation(summary = "分页查询伞架列表", description = "支持功能、类型、尺寸、材料筛选")
    public ResultInfo<PageResponse<UmbrellaFrameDetailVO>> page(@RequestBody UmbrellaFrameListQueryDTO query) {
        Page<UmbrellaFrameDetailVO> page = umbrellaFrameService.page(query);
        return ResultInfo.success(PageResponse.of(page.getCurrent(), page.getSize(), page.getTotal(), page.getRecords()));
    }

    /**
     * 查询伞架详情。
     *
     * @param idRequestParam 伞架 ID 请求参数
     * @return 统一响应，data 为伞架详情（包含绑定的材料列表）
     */
    @PostMapping("/detail")
    @PreAuthorize("@ss.hasPermission(@ss.perm('UMBRELLA_FRAME_DETAIL'))")
    @Operation(summary = "查询伞架详情", description = "查询伞架详细信息及绑定的材料")
    public ResultInfo<UmbrellaFrameDetailVO> detail(@RequestBody @Valid IdRequestParam idRequestParam) {
        return ResultInfo.success(umbrellaFrameService.getDetail(idRequestParam.getId()));
    }

    /**
     * 删除伞架。
     */
    @PostMapping("/delete")
    @PreAuthorize("@ss.hasPermission(@ss.perm('UMBRELLA_FRAME_DELETE'))")
    @Operation(summary = "删除伞架", description = "逻辑删除")
    public ResultInfo<Boolean> delete(@RequestBody IdRequestParam idRequestParam) {
        return ResultInfo.success(umbrellaFrameService.delete(idRequestParam.getId()));
    }

    /**
     * 查询伞架列表（用于下拉框选择）；请求体可选，传入功能/类型/长度/直径/伞骨数量/材料等 baseDataId 时按条件筛选。
     *
     * @param query 筛选条件，可为空；未传或字段为 null 表示不按该维度过滤
     * @return 统一响应，data 为伞架列表（仅包含 id 和名称等基本信息）
     */
    @PostMapping("/list")
    @PreAuthorize("@ss.hasPermission(@ss.perm('UMBRELLA_FRAME_LIST'))")
    @Operation(summary = "查询伞架列表", description = "用于产品添加时下拉框选择；可按功能、类型、尺寸、材料等 baseDataId 筛选")
    public ResultInfo<List<UmbrellaFrameVO>> listForSelect(@RequestBody(required = false) UmbrellaFrameSelectQueryDTO query) {
        return ResultInfo.success(umbrellaFrameService.listForSelect(query));
    }
}