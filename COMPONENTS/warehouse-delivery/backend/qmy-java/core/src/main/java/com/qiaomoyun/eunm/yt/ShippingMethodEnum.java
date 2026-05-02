/*
 * @author java_deng
 * @date 2025/12/12 14:53
 * @description
 */
package com.qiaomoyun.eunm.yt;

import lombok.Getter;

@Getter
public enum ShippingMethodEnum {
    entireOrder("0","整单齐发"),
    entireProduct("1","单款齐发"),
    entireSpecification("2","单规格齐发"),
    hasStock("3","有货就发");
    private final String key;
    private final String value;
    ShippingMethodEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static String getValueByKey(String shippingMethod) {
        for (ShippingMethodEnum shippingMethodEnum : ShippingMethodEnum.values()) {
            if (shippingMethodEnum.getKey().equals(shippingMethod)) {
                return shippingMethodEnum.value;
            }
        }
        return null;
    }
}
