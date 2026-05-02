/*
 * @author java_deng
 * @date 2025/11/21 14:12
 * @description
 */
package com.qiaomoyun.eunm.yt;

import lombok.Getter;

@Getter
public enum OrderStatusEnum {
    WaitAudit(1,"待审核"),
    AuditRejected(2,"审核拒绝"),
    Saved(3,"暂存"),
    AuditApproved(4,"审核通过"),
    Passed(5,"已生成申购单");


    private final Integer key;
    private final String value;

    OrderStatusEnum(Integer key, String value) {
        this.value = value;
        this.key = key;
    }
}
