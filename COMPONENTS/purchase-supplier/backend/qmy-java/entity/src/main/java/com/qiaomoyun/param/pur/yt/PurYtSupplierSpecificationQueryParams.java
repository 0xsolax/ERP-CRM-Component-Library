/*
 * @author java_deng
 * @date 2026/04/07
 * @description 供应商规格查询参数
 */
package com.qiaomoyun.param.pur.yt;

import com.qiaomoyun.param.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PurYtSupplierSpecificationQueryParams extends BasePageQuery {

    @Schema(description = "供应商ID", required = true)
    private Long supplierId;

    @Schema(description = "产品编码")
    private String productCode;

    @Schema(description = "规格名称")
    private String specificationName;

    @Schema(description = "供应商规格")
    private String supplierSpecification;

    @Schema(description = "供应商编号")
    private String supplierSpecificationCode;

}
