package com.qiaomoyun.eunm.yt;

import lombok.Getter;

@Getter
public enum ReturnOrderTypeEnum {
    order("订单",1),
    purchaseOrder("采购单",2);


    private final String name;
    private final Integer key;

    ReturnOrderTypeEnum(String name, Integer key) {
        this.name = name;
        this.key = key;
    }
}
