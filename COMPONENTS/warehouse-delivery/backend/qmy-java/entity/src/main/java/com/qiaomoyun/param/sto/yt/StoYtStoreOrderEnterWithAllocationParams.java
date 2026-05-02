package com.qiaomoyun.param.sto.yt;

import lombok.Data;
import java.util.List;

/**
 * 按订单维度入库参数
 */
@Data
public class StoYtStoreOrderEnterWithAllocationParams {
    /** 入库单ID列表 */
    private List<Long> storeOrderIdList;
    /** 按订单分配的入库列表 */
    private List<OrderEnterItem> orderEnterList;
    /** 额外入库数量（纯可用库存，不绑定订单） */
    private Integer extraEnterNumber;
}
