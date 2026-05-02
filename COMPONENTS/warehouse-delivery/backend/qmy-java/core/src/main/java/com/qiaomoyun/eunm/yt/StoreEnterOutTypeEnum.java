package com.qiaomoyun.eunm.yt;

import lombok.Getter;

@Getter
public enum StoreEnterOutTypeEnum {
    createOrder(1,"创建订单"),
    createPurchaseOrder(2,"创建采购单"),
    purchaseEnterStore(3,"采购单入库"),
    simpleEnterStore(4,"独立入库"),
    customerSimpleEnterStore(5,"客户独立仓独立入库"),
    simpleOutStore(6,"独立出库"),
    customerSimpleOutStore(7,"客户独立仓独立出库"),
    delivery(8,"发货"),
    orderOccupy(9,"订单占用"),
    returnOrder(10,"退货"),
    returnPurchase(11,"采购单退货"),
    inCompleteConfirm(12,"半成品确认"),
    closeOrderRelease(13,"关闭订单释放占用");

    private final Integer key;
    private final String desc;
    StoreEnterOutTypeEnum(Integer key, String desc) {
        this.key = key;
        this.desc = desc;
    }
}
