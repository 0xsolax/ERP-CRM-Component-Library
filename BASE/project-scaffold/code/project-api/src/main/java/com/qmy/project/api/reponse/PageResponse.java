package com.qmy.project.api.reponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 09:49
 */
@Data
@Schema(description = "分页返回")
public class PageResponse<T> implements Serializable {

    @Schema(description = "当前页")
    private Long current;

    @Schema(description = "每页条数")
    private Long size;

    @Schema(description = "总量", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long total;

    @Schema(description = "数据列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<T> list;

    public PageResponse() {
    }

    public PageResponse(Long current, Long size, List<T> list, Long total) {
        this.current = current;
        this.size = size;
        this.list = list;
        this.total = total;
    }

    public PageResponse(List<T> list, Long total) {
        this.list = list;
        this.total = total;
    }

    public PageResponse(Long total) {
        this.list = new ArrayList<>();
        this.total = total;
    }

    public static <T> PageResponse<T> empty() {
        return new PageResponse<>(0L);
    }

    public static <T> PageResponse<T> empty(Long total) {
        return new PageResponse<>(total);
    }

    public static <T> PageResponse<T> of(Long current, Long size, Long total, List<T> list) {
        return new PageResponse<>(current, size, list, total);
    }
}
