package com.qiaomoyun.param.sto.yt;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 物流公司修改参数类
 */
@Data
public class StoYtTransportCompanyUpdateParams {

    /**
     * 物流公司ID
     */
    @NotNull(message = "物流公司ID不能为空")
    private Long id;

    /**
     * 物流公司编号
     */
    private String code;

    /**
     * 物流公司名称
     */
    @NotBlank(message = "物流公司名称不能为空")
    private String name;

    /**
     * 物流公司类型
     */
    private String type;

    /**
     * 物流公司地址
     */
    private String address;

    /**
     * 是否提供上门服务
     */
    private Integer isHomeService;
}
