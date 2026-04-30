package com.qmy.zhongsheng.core.file.controller;

import com.qmy.zhongsheng.api.reponse.ResultInfo;
import com.qmy.zhongsheng.core.file.model.vo.OssStsTokenVO;
import com.qmy.zhongsheng.core.file.service.OssService;
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
 * @description OssController
 * @date 2026/03/20 09:49
 */
@RestController
@RequestMapping("/oss")
@Validated
@RequiredArgsConstructor
@Tag(name = "文件管理", description = "文件存储/ OSS 相关功能")
public class OssController {

    private final OssService ossService;

    @GetMapping("/getOssToken")
    @PreAuthorize("@ss.hasPermission('file:oss:token')")
    @Operation(summary = "获取OSS STS临时凭证")
    public ResultInfo<OssStsTokenVO> getOssToken() {
        return ResultInfo.success(ossService.getOssToken());
    }
}
