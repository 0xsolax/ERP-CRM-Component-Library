package com.qmy.zhongsheng.core.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.api.dto.system.SystemOperationLogQueryDTO;
import com.qmy.zhongsheng.api.reponse.PageResponse;
import com.qmy.zhongsheng.api.reponse.ResultInfo;
import com.qmy.zhongsheng.core.system.model.vo.SystemOperationLogVO;
import com.qmy.zhongsheng.core.system.service.SystemOperationLogService;
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
 * 系统操作日志接口。
 *
 * @author AI Coding
 */
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/system/operationLog")
@Tag(name = "系统操作日志", description = "管理员查询全局业务操作审计记录")
public class SystemOperationLogController {

    private final SystemOperationLogService systemOperationLogService;

    @PostMapping("/page")
    @PreAuthorize("@ss.hasPermission(@ss.perm('SYSTEM_OPERATION_LOG_PAGE'))")
    @Operation(summary = "分页查询系统操作日志")
    public ResultInfo<PageResponse<SystemOperationLogVO>> page(@RequestBody(required = false) SystemOperationLogQueryDTO query) {
        Page<SystemOperationLogVO> page = systemOperationLogService.page(query);
        return ResultInfo.success(PageResponse.of(page.getCurrent(), page.getSize(), page.getTotal(), page.getRecords()));
    }
}
