package com.qiaomoyun.eunm.sed;

import lombok.Getter;

@Getter
public enum QuotationOperationEnum {

     pass_audit("1","通过审核"),
    commit_audit("2","提交审核"),
    confirm_logistics("3","确认运输成本"),
    confirm_purchase("4","确认采购成本"),
    modify_quotation("5","修改报价单，重新进入成本核算环节"),
    confirm_quotation("6","确定提交，进入成本核算环节"),
    modify_quotation_save("7","修改报价单，暂存报价单"),
    reject_audit("8","审核驳回"),
    finance_pass_audit("9","财务审核通过"),
    president_pass_audit("10","总裁审核通过"),
    president_wx_pass_audit("11","总裁微信审核通过");
         ;


    private final String code;
    private final String info;

    QuotationOperationEnum(String code, String info) {
        this.code=code;
        this.info=info;
    }

    // 核心方法：根据编码code获取对应的枚举描述
    public static String getInfoByCode(String code) {
        // 遍历所有枚举值
        for (QuotationOperationEnum enumObj : QuotationOperationEnum.values()) {
            // 匹配编码
            if (enumObj.getCode().equals(code)) {
                return enumObj.getInfo();
            }
        }
        // 无匹配时返回默认值（可根据业务调整，比如返回空/抛异常）
        return "";
    }
}
