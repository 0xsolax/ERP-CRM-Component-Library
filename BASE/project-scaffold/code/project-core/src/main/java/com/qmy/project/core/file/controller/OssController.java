package com.qmy.project.core.file.controller;

import com.qmy.project.api.reponse.ResultInfo;
import com.qmy.project.core.file.model.vo.OssStsTokenVO;
import com.qmy.project.core.file.service.OssService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 09:49
 */
@RestController
@RequestMapping("/oss")
@Validated
@RequiredArgsConstructor
@Tag(name = "OSS 管理", description = "阿里云 OSS 相关功能")
public class OssController {

    private final OssService ossService;

    @GetMapping("/getOssToken")
    @Operation(summary = "获取OSS STS临时凭证")
    public ResultInfo<OssStsTokenVO> getOssToken() {
        return ResultInfo.success(ossService.getOssToken());
    }
}
