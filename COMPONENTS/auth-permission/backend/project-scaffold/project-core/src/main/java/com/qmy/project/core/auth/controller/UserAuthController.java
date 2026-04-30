package com.qmy.project.core.auth.controller;

import com.qmy.project.api.dto.PasswordLoginDTO;
import com.qmy.project.api.dto.ScanLoginDTO;
import com.qmy.project.api.reponse.ResultInfo;
import com.qmy.project.core.auth.model.vo.UserLoginVO;
import com.qmy.project.core.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 09:49
 */
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/sysUser")
@Tag(name = "账号认证管理")
public class UserAuthController {

    private final AuthService authService;

    @PostMapping("/loginByPassword")
    @Operation(summary = "账号密码登录")
    public ResultInfo<UserLoginVO> loginByPassword(@Valid @RequestBody PasswordLoginDTO loginDTO) {
        return ResultInfo.success(authService.loginByPassword(loginDTO));
    }

    @PostMapping("/loginByScan")
    @Operation(summary = "通过第三方扫码登录（飞书/钉钉需平台配置，企业微信待接入）")
    public ResultInfo<UserLoginVO> loginByScan(@Valid @RequestBody ScanLoginDTO param, HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        return ResultInfo.success(authService.loginByScan(param, referer));
    }

    @PostMapping("/logout")
    @Operation(summary = "退出登录")
    public ResultInfo<Void> logout() {
        authService.logout();
        return ResultInfo.success(null);
    }
}
