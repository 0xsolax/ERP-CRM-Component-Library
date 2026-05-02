package com.qiaomoyun.eunm.sed;

import lombok.Getter;

/**
 * 报价单会签操作类型（接口入参）。
 */
@Getter
public enum QuotationJointAuditActionEnum {
    FINANCE_PASS("FINANCE_PASS", "财务审核通过"),
    PRESIDENT_PASS("PRESIDENT_PASS", "总裁审核通过"),
    ;

    private final String code;
    private final String desc;

    QuotationJointAuditActionEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static QuotationJointAuditActionEnum fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (QuotationJointAuditActionEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }
}
