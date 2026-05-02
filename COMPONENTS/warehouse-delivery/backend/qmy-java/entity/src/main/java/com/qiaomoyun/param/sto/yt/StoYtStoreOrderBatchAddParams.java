package com.qiaomoyun.param.sto.yt;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 批量新增出入库单参数类
 */
@Data
public class StoYtStoreOrderBatchAddParams {

    /**
     * 出入库单列表
     */
    @NotEmpty(message = "出入库单列表不能为空")
    @Valid
    private List<StoYtStoreOrderAddParams> orders;
}
