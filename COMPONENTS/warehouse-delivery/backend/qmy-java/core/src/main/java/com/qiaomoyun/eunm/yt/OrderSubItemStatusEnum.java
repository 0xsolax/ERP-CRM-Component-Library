package com.qiaomoyun.eunm.yt;

import lombok.Getter;

@Getter
public enum OrderSubItemStatusEnum {
    Draft("-1","暂存"),
    WaitPurchase("0","待采购"),
    WaitEnterStore("1","待入库"),
    WaitPackage("2","待打包"),
    WaitDelivery("3","待发货"),
    Delivered("4","已发货"),
    Completed("5","已完成"),
    WaitConfirm("6","待确认"),
    Closed("7","已关闭");


    private final String key;
    private final String value;

    OrderSubItemStatusEnum(String key, String value) {
        this.value = value;
        this.key = key;
    }
}
