/*
 * @author java_deng
 * @date 2025/12/12 10:26
 * @description
 */
package com.qiaomoyun.event.yt;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class DeliveryEvent extends ApplicationEvent {
    private final Long orderSubItemId;
    private final Long orderId;

    public DeliveryEvent(Object source, Long orderSubItemId, Long orderId) {
        super(source);
        this.orderSubItemId = orderSubItemId;
        this.orderId = orderId;
    }

    public DeliveryEvent(Object source, Long orderId) {
        super(source);
        this.orderSubItemId = null;
        this.orderId = orderId;
    }
}
