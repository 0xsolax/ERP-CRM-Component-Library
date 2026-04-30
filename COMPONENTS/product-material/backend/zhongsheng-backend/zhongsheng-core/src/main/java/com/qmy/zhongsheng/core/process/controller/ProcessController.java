package com.qmy.zhongsheng.core.process.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.api.dto.process.ProcessListQueryDTO;
import com.qmy.zhongsheng.api.dto.process.ProcessSaveDTO;
import com.qmy.zhongsheng.api.reponse.PageResponse;
import com.qmy.zhongsheng.api.reponse.ResultInfo;
import com.qmy.zhongsheng.api.request.IdRequestParam;
import com.qmy.zhongsheng.core.process.model.vo.ProcessVO;
import com.qmy.zhongsheng.core.process.service.ProcessService;
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
 * 工序管理接口。
 *
 * @author AI Coding
 */
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/process")
@Tag(name = "工价管理", description = "维护 process 工序数据")
public class ProcessController {

    private final ProcessService processService;

    /**
     * 保存或更新工序。
     *
     * @param dto 请求体无 id 为新增；有 id 为更新；isDeleted=1 时软删除
     * @return 统一响应，data 为记录主键 id
     */
    @PostMapping("/saveOrUpdate")
    @PreAuthorize("@ss.hasPermission(@ss.perm('PROCESS_SAVE_OR_UPDATE'))")
    @Operation(summary = "保存或更新工序", description = "无 id 为新增；有 id 为更新；isDeleted=1 时执行软删除")
    public ResultInfo<Long> saveOrUpdate(@Valid @RequestBody ProcessSaveDTO dto) {
        return ResultInfo.success(processService.saveOrUpdate(dto));
    }

    /**
     * 分页查询工序列表。
     *
     * @param query 查询条件，支持 name 模糊搜索
     * @return 统一响应，data 为分页结果
     */
    @PostMapping("/page")
    @PreAuthorize("@ss.hasPermission(@ss.perm('PROCESS_PAGE'))")
    @Operation(summary = "分页查询工序列表", description = "支持工序名称模糊搜索")
    public ResultInfo<PageResponse<ProcessVO>> page(@RequestBody ProcessListQueryDTO query) {
        Page<ProcessVO> page = processService.page(query);
        return ResultInfo.success(PageResponse.of(page.getCurrent(), page.getSize(), page.getTotal(), page.getRecords()));
    }

    /**
     * 分页查询工序列表。
     *
     * @return 统一响应，data 为分页结果
     */
    @PostMapping("/list")
    @PreAuthorize("@ss.hasPermission(@ss.perm('PROCESS_LIST'))")
    @Operation(summary = "查询工序列表", description = "查询工序列表")
    public ResultInfo<List<ProcessVO>> list() {
        return ResultInfo.success(processService.list());
    }

    @PostMapping("/delete")
    @PreAuthorize("@ss.hasPermission(@ss.perm('PROCESS_DELETE'))")
    @Operation(summary = "删除工序", description = "删除工序")
    public ResultInfo<Boolean> delete(@RequestBody IdRequestParam id) {
        return ResultInfo.success(processService.delete(id));
    }
}