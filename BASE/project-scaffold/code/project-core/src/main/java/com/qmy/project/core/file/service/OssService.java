package com.qmy.project.core.file.service;

import com.qmy.project.core.file.model.vo.OssStsTokenVO;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 09:49
 */
public interface OssService {

    /**
     * 获取 OSS STS 临时访问凭证。
     *
     * @return OSS STS 临时凭证
     */
    OssStsTokenVO getOssToken();
}
