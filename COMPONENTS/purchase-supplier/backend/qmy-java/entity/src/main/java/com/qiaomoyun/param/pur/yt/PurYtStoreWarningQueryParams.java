/*
 * @author java_deng
 * @date 2025/12/1 13:09
 * @description 库存预警查询参数
 */
package com.qiaomoyun.param.pur.yt;

import com.qiaomoyun.param.BasePageQuery;
import lombok.Data;

import java.util.List;

@Data
public class PurYtStoreWarningQueryParams extends BasePageQuery {
    private Long customerId;      // 客户ID
    private Long productId;       // 产品ID
    private Long specificationId; // 规格ID
    private String storeName;
    private String productCode;

    List<Long> warningIdList;
}