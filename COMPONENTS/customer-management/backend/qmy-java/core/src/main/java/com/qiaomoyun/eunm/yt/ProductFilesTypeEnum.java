package com.qiaomoyun.eunm.yt;

import lombok.Getter;

@Getter
public enum ProductFilesTypeEnum {
    gallery("图库图片","1"),
    product("产品图片","2"),
    specification("产品规格图片","3"),
    combination("组合图片","4"),
    combinationSpecification("组合规格图片","5"),
    contactPersonProFile("客户联系人名片/头像","6"),
    CustomerFollowFile("客户跟进附件","7"),
    SupplierFollowFile("供应商跟进附件","8"),
    PurchasePaymentFile("财务付款附件","9"),
    orderReceiveFile("订单收款附件","10"),
    deliveryReceiveFile("发货回款附件","11")
    ;


    private final String name;
    private final String key;

    ProductFilesTypeEnum(String name, String key) {
        this.name = name;
        this.key = key;
    }
}
