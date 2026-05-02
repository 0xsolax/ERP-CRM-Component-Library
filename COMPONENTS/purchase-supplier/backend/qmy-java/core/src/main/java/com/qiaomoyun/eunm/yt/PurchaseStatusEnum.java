package com.qiaomoyun.eunm.yt;

import lombok.Getter;

/**
 * 采购单状态枚举
 */
@Getter
public enum PurchaseStatusEnum {
    temporary("暂存","0"),
    Purchase("采购中","1"),
    EnterStore("已入库","2"),
    Delivery("已发货","3");


    private final String name;
    private final String key;

    PurchaseStatusEnum(String name, String key) {
        this.name = name;
        this.key = key;
    }
}
