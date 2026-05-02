/*
 * @author java_deng
 * @date 2025/11/28 11:19
 * @description
 */
package com.qiaomoyun.vo.sal.yt;

import lombok.Data;

import java.util.List;

@Data
public class SalYtOrderExportVo {
    private String code;//货号或产品code
    private String imageUrl;
    private Integer totalQuantity;
    private List<SalYtOrderExportItemVo> itemList;
}
