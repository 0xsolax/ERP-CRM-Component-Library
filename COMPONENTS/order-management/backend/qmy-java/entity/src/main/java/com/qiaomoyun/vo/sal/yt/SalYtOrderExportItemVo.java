/*
 * @author java_deng
 * @date 2025/11/28 11:22
 * @description
 */
package com.qiaomoyun.vo.sal.yt;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SalYtOrderExportItemVo {
    private List<String> specificationNames;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal totalPrice;
}
