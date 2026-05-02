/*
 * @author java_deng
 * @date 2025/12/9 10:23
 * @description
 */
package com.qiaomoyun.event.yt;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PurchaseEvent extends ApplicationEvent{
    private Long purchaseId;
    private  Long purchaseItemId;
    private  Integer number;
    private Integer type; // 1 采购单提交，2 采购单确认

    public PurchaseEvent(Object source, Long purchaseId) {
        super(source);
        this.purchaseId = purchaseId;
    }

    public PurchaseEvent(Object source, Long purchaseItemId, Integer number) {
        super(source);
        this.purchaseItemId = purchaseItemId;
        this.number = number;
    }
}
