package com.qmy.zhongsheng.core.document.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.api.dto.document.DocumentActionLogQueryDTO;
import com.qmy.zhongsheng.api.dto.document.DocumentOwnerAssignDTO;
import com.qmy.zhongsheng.api.dto.document.DocumentUnlockActionDTO;
import com.qmy.zhongsheng.api.reponse.PageResponse;
import com.qmy.zhongsheng.api.reponse.ResultInfo;
import com.qmy.zhongsheng.core.document.model.vo.DocumentActionLogVO;
import com.qmy.zhongsheng.core.document.model.vo.DocumentOwnerAssignResultVO;
import com.qmy.zhongsheng.core.document.model.vo.DocumentUnlockResultVO;
import com.qmy.zhongsheng.core.document.service.DocumentActionService;
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

/**
 * 公共单据状态、动作日志与锁定接口。
 *
 * @author AI Coding
 */
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/document")
@Tag(name = "公共单据动作", description = "报价、订单等业务单据的动作日志、解锁与负责人改派")
public class DocumentActionController {

    private final DocumentActionService documentActionService;

    @PostMapping("/actionLog/page")
    @PreAuthorize("@ss.hasPermission(@ss.perm('DOCUMENT_ACTION_LOG_LIST'))")
    @Operation(summary = "分页查询动作日志")
    public ResultInfo<PageResponse<DocumentActionLogVO>> actionLogPage(@Valid @RequestBody DocumentActionLogQueryDTO query) {
        Page<DocumentActionLogVO> page = documentActionService.pageLogs(query);
        return ResultInfo.success(PageResponse.of(page.getCurrent(), page.getSize(), page.getTotal(), page.getRecords()));
    }

    @PostMapping("/unlock/request")
    @PreAuthorize("@ss.hasPermission(@ss.perm('DOCUMENT_UNLOCK_REQUEST'))")
    @Operation(summary = "申请解锁")
    public ResultInfo<DocumentUnlockResultVO> requestUnlock(@Valid @RequestBody DocumentUnlockActionDTO dto) {
        return ResultInfo.success(documentActionService.requestUnlock(dto));
    }

    @PostMapping("/unlock/warning")
    @PreAuthorize("@ss.hasPermission(@ss.perm('DOCUMENT_UNLOCK_APPROVE'))")
    @Operation(summary = "解锁单据")
    public ResultInfo<DocumentUnlockResultVO> warningUnlock(@Valid @RequestBody DocumentUnlockActionDTO dto) {
        return ResultInfo.success(documentActionService.warningUnlock(dto));
    }

    @PostMapping("/unlock/approve")
    @PreAuthorize("@ss.hasPermission(@ss.perm('DOCUMENT_UNLOCK_APPROVE'))")
    @Operation(summary = "审批同意解锁")
    public ResultInfo<DocumentUnlockResultVO> approveUnlock(@Valid @RequestBody DocumentUnlockActionDTO dto) {
        return ResultInfo.success(documentActionService.approveUnlock(dto));
    }

    @PostMapping("/unlock/reject")
    @PreAuthorize("@ss.hasPermission(@ss.perm('DOCUMENT_UNLOCK_APPROVE'))")
    @Operation(summary = "审批拒绝解锁")
    public ResultInfo<DocumentUnlockResultVO> rejectUnlock(@Valid @RequestBody DocumentUnlockActionDTO dto) {
        return ResultInfo.success(documentActionService.rejectUnlock(dto));
    }

    @PostMapping("/reconfirm")
    @PreAuthorize("@ss.hasPermission(@ss.perm('DOCUMENT_UNLOCK_REQUEST'))")
    @Operation(summary = "重新确认并锁定单据")
    public ResultInfo<DocumentUnlockResultVO> reconfirm(@Valid @RequestBody DocumentUnlockActionDTO dto) {
        return ResultInfo.success(documentActionService.reconfirm(dto));
    }

    @PostMapping("/owner/assign")
    @PreAuthorize("@ss.hasPermission(@ss.perm('DOCUMENT_REASSIGN_OWNER'))")
    @Operation(summary = "管理员改派负责人")
    public ResultInfo<DocumentOwnerAssignResultVO> assignOwner(@Valid @RequestBody DocumentOwnerAssignDTO dto) {
        return ResultInfo.success(documentActionService.assignOwner(dto));
    }
}
