package com.qiaomoyun.eunm.sed;

import lombok.Getter;

@Getter
public enum OrderGjpSyncStatusEnum {
    UNSYNCHRONIZED("未同步", 1),
    SYNCHRONIZATION_FAILED("同步失败", 2),
    SYNCHRONIZATION_SUCCESS("同步成功", 3),
    ;

    private final String code;
    private final Integer info;

    OrderGjpSyncStatusEnum(String code, Integer info) {
        this.code = code;
        this.info = info;
    }
}