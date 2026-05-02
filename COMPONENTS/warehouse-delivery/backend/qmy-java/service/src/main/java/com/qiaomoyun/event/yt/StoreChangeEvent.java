/*
 * @author java_deng
 * @date 2025/12/7 13:56
 * @description
 */
package com.qiaomoyun.event.yt;

import com.qiaomoyun.entity.sal.yt.SalYtCustomerStore;
import com.qiaomoyun.entity.sto.yt.StoYtStore;
import com.qiaomoyun.entity.sto.yt.StoYtStoreRecord;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class StoreChangeEvent extends ApplicationEvent {
    private final Integer changeType;
    private Integer realStoreInit;
    private Integer realStoreChange;
    private Integer enableStoreInit;
    private Integer enableStoreChange;
    private Integer occupyStoreInit;
    private Integer occupyStoreChange;
    private Integer realTransitInit;
    private Integer realTransitChange;
    private Integer enableTransitInit;
    private Integer enableTransitChange;
    private Integer occupyTransitInit;
    private Integer occupyTransitChange;
    private Long specificationId;
    private Long orderSubId;
    private Long purchaseId;
    private Long customerId;
    private Long orderId;
    private String orderCode;
    private Long customerStoreId;
    private StoYtStore stoYtStore;
    private String remark;

    //采购单常规品入库
    private StoYtStoreRecord stoYtStoreRecord;

    //半成品确认数量
    private Integer inCompleteNumber;

    //发货数量
    private Integer deliveryNumber;

    //独立仓
    private SalYtCustomerStore customerStore;

    //创建订单
    public StoreChangeEvent(Object source,Integer changeType,Integer occupyStoreChange,Integer occupyTransitChange,Long specificationId,Long orderSubId) {
        super(source);
        this.changeType = changeType;
        this.occupyStoreChange = occupyStoreChange;
        this.occupyTransitChange = occupyTransitChange;
        this.specificationId = specificationId;
        this.orderSubId = orderSubId;
    }

    //创建独立仓订单
    public StoreChangeEvent(Object source,Integer changeType,Integer occupyStoreChange,Integer occupyTransitChange,Long specificationId,Long orderSubId,Long customerStoreId) {
        super(source);
        this.changeType = changeType;
        this.occupyStoreChange = occupyStoreChange;
        this.occupyTransitChange = occupyTransitChange;
        this.specificationId = specificationId;
        this.orderSubId = orderSubId;
        this.customerStoreId=customerStoreId;
    }

    //创建采购单
    public StoreChangeEvent(Object source, Long purchaseId, Integer changeType, Integer realTransitChange, Integer occupyTransitChange, Long specificationId, Long orderSubId, StoYtStore stoYtStore) {
        super(source);
        this.changeType = changeType;
        this.realTransitChange = realTransitChange;
        this.occupyTransitChange = occupyTransitChange;
        this.specificationId = specificationId;
        this.orderSubId = orderSubId;
        this.purchaseId=purchaseId;
        this.stoYtStore=stoYtStore;
    }

    //创建独立仓采购单
    public StoreChangeEvent(Object source, Long purchaseId, Integer changeType, Integer realTransitChange, Long specificationId, SalYtCustomerStore salYtCustomerStore) {
        super(source);
        this.specificationId=specificationId;
        this.changeType = changeType;
        this.realTransitChange = realTransitChange;
        this.purchaseId=purchaseId;
        this.customerStore=salYtCustomerStore;
    }

    //独立仓转入转出
    public StoreChangeEvent(Object source,Integer changeType,Integer realStoreChange,Long specificationId,Long customerId,String remark) {
        super(source);
        this.changeType = changeType;
        this.realStoreChange = realStoreChange;
        this.specificationId = specificationId;
        this.customerId=customerId;
        this.remark=remark;
    }

    //采购单独立仓入库
    public StoreChangeEvent(Object source,Integer changeType,Integer realStoreChange,Long specificationId,Long customerId,String orderCode,Long purchaseId) {
        super(source);
        this.changeType = changeType;
        this.realStoreChange = realStoreChange;
        this.specificationId = specificationId;
        this.customerId=customerId;
        this.orderCode=orderCode;
        this.purchaseId=purchaseId;
    }

    //采购单公共仓入库
    public StoreChangeEvent(Object source, Integer changeType, StoYtStoreRecord stoYtStoreRecord, Long purchaseId) {
        super(source);
        this.changeType = changeType;
        this.stoYtStoreRecord = stoYtStoreRecord;
        this.purchaseId=purchaseId;
    }

    //单独入库或单独出库
    public StoreChangeEvent(Object source, Integer changeType, Integer realStoreChange,String remark,StoYtStore stoYtStore) {
        super(source);
        this.changeType = changeType;
        this.realStoreChange = realStoreChange;
        this.remark = remark;
        this.stoYtStore=stoYtStore;
    }

    //半成品确认
    public StoreChangeEvent(Object source, Integer changeType, Integer inCompleteNumber,Long orderId,Long purchaseId,Long specificationId) {
        super(source);
        this.specificationId=specificationId;
        this.changeType = changeType;
        this.inCompleteNumber = inCompleteNumber;
        this.orderId = orderId;
        this.purchaseId = purchaseId;
    }

    //发货
    public StoreChangeEvent(Object source, Integer changeType,Integer deliveryNumber,Long orderId,Long specificationId) {
        super(source);
        this.specificationId=specificationId;
        this.changeType = changeType;
        this.orderId = orderId;
        this.deliveryNumber=deliveryNumber;
    }

    //独立仓发货
    public StoreChangeEvent(Object source, Integer changeType,Integer deliveryNumber,Long orderId,Long specificationId,SalYtCustomerStore customerStore) {
        super(source);
        this.specificationId=specificationId;
        this.changeType = changeType;
        this.orderId = orderId;
        this.deliveryNumber=deliveryNumber;
        this.customerStore=customerStore;
    }

    //关闭订单释放占用
    public StoreChangeEvent(Object source, Integer changeType, Integer occupyStoreChange, Integer occupyTransitChange,
                            Long specificationId, Long orderId, Long orderSubId, String remark) {
        super(source);
        this.changeType = changeType;
        this.occupyStoreChange = occupyStoreChange;
        this.occupyTransitChange = occupyTransitChange;
        this.specificationId = specificationId;
        this.orderId = orderId;
        this.orderSubId = orderSubId;
        this.remark = remark;
    }
}
