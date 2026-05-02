package com.qiaomoyun.eunm.sed;

import lombok.Getter;

@Getter
public enum QuotationStatusEnum {
    STORAGE("0","暂存"),
    CALCULATING("1","计算成本中"),
    CALCULATED("2","计算完成"),
    AUDIT_PASSED("4","审核通过"),
    AUDIT_FINANCE_PASSED("5","总裁未审核，财务未审核"),
    PRESIDENT_PASSED_FINANCE_PENDING("6","总裁审核通过，财务未审核"),
    FINANCE_PASSED_PRESIDENT_PENDING("7","总裁未审核，财务审核通过"),
    AUDIT_REJECTED("-1","审核驳回"),
    PRESIDENT_WX_PASSED("8","总裁微信审核通过，财务未审核"),
    ;

    private final String code;
    private final String info;

    QuotationStatusEnum(String code, String info) {
        this.code=code;
        this.info=info;
    }


}
