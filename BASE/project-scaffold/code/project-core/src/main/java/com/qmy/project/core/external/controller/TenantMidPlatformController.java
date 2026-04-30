package com.qmy.project.core.external.controller;

import com.qmy.project.api.dto.midplatform.TenantMidPlatformSyncRequest;
import com.qmy.project.api.reponse.ResultInfo;
import com.qmy.project.core.external.service.TenantMidPlatformSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 中台修改租户信息的 HTTP 入口
 * 中台在自有工程中定义 Feign/HTTP 客户端，指向本服务
 *
 * @author AI Coding
 */
@RestController
@Validated
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/external/tenant")
@Tag(name = "中台开放接口", description = "供中台系统操作租户数据")
public class TenantMidPlatformController {

    private final TenantMidPlatformSyncService tenantMidPlatformSyncService;

    @PostMapping("/sync")
    @Operation(summary = "同步租户配置与文件", description = "配置项为 null 时不更新；文件列表为 null 时不调整该分类；文件以 url 匹配，is_deleted=1 表示删除")
    public ResultInfo<Boolean> sync(@RequestBody @Valid TenantMidPlatformSyncRequest request) {
        log.info("同步租户配置与文件：{}", request);
        tenantMidPlatformSyncService.syncFromMidPlatformRequest(request);
        return ResultInfo.success(Boolean.TRUE);
    }
}
