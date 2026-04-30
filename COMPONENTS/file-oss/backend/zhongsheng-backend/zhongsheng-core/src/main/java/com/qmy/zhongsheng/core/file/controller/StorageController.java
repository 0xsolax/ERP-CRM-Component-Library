package com.qmy.zhongsheng.core.file.controller;

import com.qmy.zhongsheng.api.dto.file.StorageDTO;
import com.qmy.zhongsheng.api.reponse.ResultInfo;
import com.qmy.zhongsheng.core.file.manager.SystemFileManager;
import com.qmy.zhongsheng.core.file.model.vo.StorageSaveVO;
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
 * @author shanyitian
 * @description 存储管理
 * @date 2026/4/8 16:28
 */
@RestController
@RequestMapping("/storage")
@Validated
@RequiredArgsConstructor
@Tag(name = "文件管理", description = "文件存储/ OSS 相关功能")
public class StorageController {

    private final SystemFileManager systemFileManager;

    /**
     * 保存文件存储记录。
     * 将 URL 拆分为 endpoint 和 key 存储，解决 OSS 地址变更问题。
     *
     * @param storageDTO 文件存储 DTO
     * @return 包含 id 和 url 的结果
     */
    @PostMapping("/saveSysStorage")
    @Operation(summary = "保存文件", description = "返回文件表 id 和 url，可用于进一步文件的保存")
    public ResultInfo<StorageSaveVO> saveSysStorage(@RequestBody @Validated StorageDTO storageDTO) {
        Long id = systemFileManager.save(
                storageDTO.getUrl(),
                storageDTO.getName(),
                storageDTO.getType(),
                storageDTO.getSize()
        );
        StorageSaveVO vo = new StorageSaveVO();
        vo.setId(id);
        vo.setUrl(storageDTO.getUrl());
        return ResultInfo.success(vo);
    }
}