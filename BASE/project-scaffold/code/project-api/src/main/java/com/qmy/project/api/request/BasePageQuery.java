package com.qmy.project.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 分页查询基类。
 *
 * @author AI Coding
 */
@Data
public class BasePageQuery {

    private static final Integer MAX_PAGE_SIZE = 1000;
    private static final Integer DEFAULT_PAGE_SIZE = 10;
    private static final Integer DEFAULT_PAGE_NUM = 1;

    @Schema(description = "页码，默认：1", defaultValue = "1")
    private Integer pageNum = DEFAULT_PAGE_NUM;

    @Schema(description = "每页条数", defaultValue = "20")
    private Integer pageSize = DEFAULT_PAGE_SIZE;

    /**
     * 安全获取 pageSize：
     * - 若 isGetAll=true，返回 -1（表示不分页）
     * - 否则使用 pageSize，但限制在 [1, MAX_PAGE_SIZE]
     */
    public Integer getPageSize() {
        if (pageSize == null || pageSize <= 0) {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.min(pageSize, MAX_PAGE_SIZE);
    }

    /**
     * 安全获取 pageNum：
     * - 若 isGetAll=true，返回 -1（或任意无效值，因为不分页）
     * - 否则确保 >=1
     */
    public Integer getPageNum() {
        if (pageNum == null || pageNum <= 0) {
            return DEFAULT_PAGE_NUM;
        }
        return pageNum;
    }
}