/*
 * @author java_deng
 * @date 2026/1/2 14:03
 * @description
 */
package com.qiaomoyun.vo.pur.yt;

import com.qiaomoyun.entity.pur.yt.PurYtPurchaseItem;
import lombok.Data;

import java.util.List;

@Data
public class PurYtPurchaseExport {
    private Integer index;
    private String productCode;
    private String imageUrl;
    private List<PurYtPurchaseItem> purYtPurchaseItems;
}
