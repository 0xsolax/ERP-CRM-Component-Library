package com.qmy.project.core.tenant.controller;

import com.qmy.project.api.reponse.ResultInfo;
import com.qmy.project.core.tenant.model.vo.TenantInfoVO;
import com.qmy.project.core.tenant.service.TenantConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 17:03
 */
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/qiaoMoYun/tenant")
@Tag(name = "租户信息管理", description = "管理租户信息")
public class TenantController {

    private final TenantConfigService tenantConfigService;

    /**
     * 根据访问域名校验并返回当前实例的租户展示信息与租户级文件列表。
     */
    @GetMapping("/getTenantId")
    @Operation(summary = "获取租户信息", description = "域名须与库中 tenant.domain-name 配置一致")
    public ResultInfo<TenantInfoVO> getTenantId(
            @RequestParam("domainName")
            @NotBlank(message = "域名不能为空")
            @Parameter(name = "domainName", description = "当前访问域名，与 tenant_config 中 tenant.domain-name 一致", required = true,
                    example = "example.com")
            String domainName) {
        return ResultInfo.success(tenantConfigService.getByDomainName(domainName));
    }
}
