package com.qiaomoyun.eunm.yt;

import lombok.Data;
import lombok.Getter;

@Getter
public enum ReceiveStatusEnum {
    WaitReceive(0,"未付款"),
    ReceivePart(1,"部分付款"),
    Completed(2,"已完成"),
    ;


    private final Integer key;
    private final String value;

    ReceiveStatusEnum(Integer key, String value) {
        this.value = value;
        this.key = key;
    }
}
