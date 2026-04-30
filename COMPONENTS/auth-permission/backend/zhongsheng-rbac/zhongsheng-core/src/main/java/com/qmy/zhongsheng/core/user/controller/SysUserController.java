package com.qmy.zhongsheng.core.user.controller;

import com.qmy.zhongsheng.api.reponse.ResultInfo;
import com.qmy.zhongsheng.core.user.model.vo.SysUserInfoVO;
import com.qmy.zhongsheng.core.user.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author AI Coding
 * @description SysUserController
 * @date 2026/03/20 16:12
 */
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/sysUser")
@Tag(name = "用户管理")
public class SysUserController {

    private final SysUserService sysUserService;

    @GetMapping("/info")
    @Operation(summary = "获取当前登录用户信息")
    public ResultInfo<SysUserInfoVO> info() {
        return ResultInfo.success(sysUserService.getCurrentUserInfo());
    }
}
