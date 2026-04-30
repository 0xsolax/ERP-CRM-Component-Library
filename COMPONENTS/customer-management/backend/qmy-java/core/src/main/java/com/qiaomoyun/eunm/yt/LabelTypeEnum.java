package com.qiaomoyun.eunm.yt;

import lombok.Getter;

@Getter
public enum LabelTypeEnum {
    CustomAttributes("定制属性","1"),
    ProductLabel("产品标签","2"),
    CombinationLabel("产品组合","3"),
    customerLabel("客户标签","4"),
    supplierLabel("供应商标签","5");


    private final String name;
    private final String key;

    LabelTypeEnum(String name, String key) {
        this.name = name;
        this.key = key;
    }
}
