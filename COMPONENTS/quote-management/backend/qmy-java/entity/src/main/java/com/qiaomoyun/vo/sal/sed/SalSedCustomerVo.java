package com.qiaomoyun.vo.sal.sed;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 客户信息
 */
@Data
public class SalSedCustomerVo {
    @Schema(description = "客户id")
    private Long id;

    @Schema(description = "客户名称")
    private String name;
}
