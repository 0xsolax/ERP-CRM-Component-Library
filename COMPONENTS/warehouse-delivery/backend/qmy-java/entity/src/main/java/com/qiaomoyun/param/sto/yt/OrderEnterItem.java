package com.qiaomoyun.param.sto.yt;

import lombok.Data;

/**
 * 按订单分配的入库项
 */
@Data
public class OrderEnterItem {
    /** 订单子项ID（sal_yt_order_sub_item.id） */
    private Long orderSubItemId;
    /** 入库数量 */
    private Integer enterNumber;
    /**
     * 是否为在途占用路径。
     * true：强制走 enterTransitOccupy 路径（更新 occupyTransitEnterNumber）；
     * false（默认）：走普通 enter 路径。
     */
    private Boolean isTransit;
}
