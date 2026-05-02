package com.qiaomoyun.param.sto.yt;

import com.qiaomoyun.param.BasePageQuery;
import lombok.Data;

import java.util.List;

/**
 * 库存历史流向记录查询参数类
 */
@Data
public class StoYtStoreRecordQueryParams extends BasePageQuery {

    /**
     * 规格ID
     */
    private Long specificationId;

    /**
     * 子订单号
     */
    private Long orderSubId;

    /**
     * 采购单号
     */
    private Long purchaseId;

    /**
     * 业务类型
     */
    private Integer type;

    private Long storeId;
    private Long customerStoreId;
    private String customerName;
    private List<Integer> typeList;

    private String createUserName;
    private String productCode;
    private String specificationName;
    private String orderSubCode;
    private String orderCode;
    private String purchaseCode;
    private List<Long> storeOrderIdList;
    //定制化属性id
    private Long labelId;
}