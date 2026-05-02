/*
 * @author java_deng
 * @date 2025/12/21 23:10
 * @description
 */
package com.qiaomoyun.eunm.sed;

import lombok.Getter;

@Getter
public enum FileTypeEnum {
    product("产品图片",1),
    sku("Sku图片",2),
    fitting("配件图片",3),
    quotationPackingFile("报价单包材附件",4),
    color("颜色图片",5),
    orderFile("订单附件",6),
    part("零件图片",7),
    partSpecification("零件规格图片",8),
    fittingSpecification("配件规格图片",7),
    presidentWxAuditImage("总裁微信审核凭证图片",9),
    ;
    private final String name;
    private final Integer key;

    FileTypeEnum(String name, Integer key) {
        this.name = name;
        this.key = key;
    }
}
