package com.qiaomoyun.eunm.yt;

import lombok.Getter;

@Getter
public enum DeliveryOrderStatusEnum {
    waitPackage(0,"待打包"),
    waitDelivery(1,"待发货"),
    delivered(2,"已发货"),;

    private final Integer key;
    private final String value;
    DeliveryOrderStatusEnum(Integer key, String value) {
        this.key = key;
        this.value = value;
    }
}
