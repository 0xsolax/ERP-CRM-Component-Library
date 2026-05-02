/*
 * @author java_deng
 * @date 2025/12/9 10:27
 * @description
 */
package com.qiaomoyun.listener.yt;

import com.qiaomoyun.entity.pur.yt.PurYtApplyPurchase;
import com.qiaomoyun.entity.pur.yt.PurYtPurchase;
import com.qiaomoyun.entity.pur.yt.PurYtPurchaseItem;
import com.qiaomoyun.entity.sal.yt.SalYtCustomerStore;
import com.qiaomoyun.entity.sal.yt.SalYtOrder;
import com.qiaomoyun.entity.sal.yt.SalYtOrderSub;
import com.qiaomoyun.entity.sal.yt.SalYtOrderSubItem;
import com.qiaomoyun.entity.sto.yt.StoYtStore;
import com.qiaomoyun.entity.sto.yt.StoYtStoreOrder;
import com.qiaomoyun.eunm.yt.OrderSubItemStatusEnum;
import com.qiaomoyun.eunm.yt.StoreEnterOutTypeEnum;
import com.qiaomoyun.event.yt.PurchaseEvent;
import com.qiaomoyun.event.yt.StoreChangeEvent;
import com.qiaomoyun.manager.sal.yt.SalYtOrderSubItemOperationManager;
import com.qiaomoyun.manager.sto.yt.StoYtStoreManager;
import com.qiaomoyun.mapper.pur.yt.PurYtApplyPurchaseMapper;
import com.qiaomoyun.mapper.pur.yt.PurYtPurchaseItemMapper;
import com.qiaomoyun.mapper.pur.yt.PurYtPurchaseMapper;
import com.qiaomoyun.mapper.sal.yt.SalYtCustomerStoreMapper;
import com.qiaomoyun.mapper.sal.yt.SalYtOrderMapper;
import com.qiaomoyun.mapper.sal.yt.SalYtOrderSubItemMapper;
import com.qiaomoyun.mapper.sal.yt.SalYtOrderSubMapper;
import com.qiaomoyun.mapper.sto.yt.StoYtStoreMapper;
import com.qiaomoyun.mapper.sto.yt.StoYtStoreOrderMapper;
import com.qiaomoyun.util.EntityCodeGenerateUtil;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PurchaseEventListener {
    private final PurYtPurchaseItemMapper purYtPurchaseItemMapper;
    private final SalYtOrderSubItemMapper salYtOrderSubItemMapper;
    private final SalYtOrderSubItemOperationManager salYtOrderSubItemOperationManager;
    private final PurYtApplyPurchaseMapper purYtApplyPurchaseMapper;
    private final PurYtPurchaseMapper purYtPurchaseMapper;
    private final StoYtStoreMapper stoYtStoreMapper;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final SalYtCustomerStoreMapper salYtCustomerStoreMapper;
    private final StoYtStoreOrderMapper stoYtStoreOrderMapper;
    private final StoYtStoreManager stoYtStoreManager;
    private final SalYtOrderSubMapper salYtOrderSubMapper;
    private final SalYtOrderMapper salYtOrderMapper;

    public PurchaseEventListener(PurYtPurchaseItemMapper purYtPurchaseItemMapper, SalYtOrderSubItemMapper salYtOrderSubItemMapper, SalYtOrderSubItemOperationManager salYtOrderSubItemOperationManager, PurYtApplyPurchaseMapper purYtApplyPurchaseMapper, PurYtPurchaseMapper purYtPurchaseMapper, StoYtStoreMapper stoYtStoreMapper, ApplicationEventPublisher applicationEventPublisher, SalYtCustomerStoreMapper salYtCustomerStoreMapper, StoYtStoreOrderMapper stoYtStoreOrderMapper, StoYtStoreManager stoYtStoreManager, SalYtOrderSubMapper salYtOrderSubMapper, SalYtOrderMapper salYtOrderMapper) {
        this.purYtPurchaseItemMapper = purYtPurchaseItemMapper;
        this.salYtOrderSubItemMapper = salYtOrderSubItemMapper;
        this.salYtOrderSubItemOperationManager = salYtOrderSubItemOperationManager;
        this.purYtApplyPurchaseMapper = purYtApplyPurchaseMapper;
        this.purYtPurchaseMapper = purYtPurchaseMapper;
        this.stoYtStoreMapper = stoYtStoreMapper;
        this.applicationEventPublisher = applicationEventPublisher;
        this.salYtCustomerStoreMapper = salYtCustomerStoreMapper;
        this.stoYtStoreOrderMapper = stoYtStoreOrderMapper;
        this.stoYtStoreManager = stoYtStoreManager;
        this.salYtOrderSubMapper = salYtOrderSubMapper;
        this.salYtOrderMapper = salYtOrderMapper;
    }

    //采购单提交
    @EventListener(PurchaseEvent.class)
    @Transactional
    public void purchaseSubmit(PurchaseEvent purchaseEvent){
        if(purchaseEvent.getPurchaseId()==null){
            //说明不是采购单提交，直接跳过
            return;
        }
        Long purchaseId = purchaseEvent.getPurchaseId();
        PurYtPurchase purYtPurchase = purYtPurchaseMapper.selectById(purchaseId);
        Boolean isInboundDelivery = purYtPurchase.getIsInboundDelivery();
        List<PurYtPurchaseItem> itemList=purYtPurchaseItemMapper.selectByPurchaseId(purchaseId);
        //修改子订单item的状态
        for (PurYtPurchaseItem item : itemList) {
            Long orderSubItemId = item.getOrderSubItemId();
            if(orderSubItemId!=null){
                SalYtOrderSubItem salYtOrderSubItem= new SalYtOrderSubItem();
                salYtOrderSubItem.setId(orderSubItemId);
                //添加子订单item的操作记录
                salYtOrderSubItemOperationManager.purchaseOperation(item.getNumber(),orderSubItemId,purYtPurchase.getCode());
                //判断是否是入库发货
                if(isInboundDelivery){
                    salYtOrderSubItem.setStatus(OrderSubItemStatusEnum.WaitEnterStore.getKey());
                    salYtOrderSubItemMapper.updateById(salYtOrderSubItem);
                }else {
                    salYtOrderSubItem.setStatus(OrderSubItemStatusEnum.WaitDelivery.getKey());
                    salYtOrderSubItemMapper.updateById(salYtOrderSubItem);
                    continue;
                }
            }else {
                salYtOrderSubItemOperationManager.purchaseOperation(item.getNumber(),item.getId(),purYtPurchase.getCode());
            }

            //添加在途库存
            Long specificationId = item.getSpecificationId();
            if(specificationId != null){
//               PurYtApplyPurchase applyPurchase= purYtApplyPurchaseMapper.selectById(item.getApplyPurchaseId());
//                Integer applyPurchaseNumber = applyPurchase.getNumber();
                Integer purchaseNumber = item.getNumber();
                //采购单创建时增加在途库存，如果没有库存数据则增加库存数据
                StoYtStore stoYtStore = stoYtStoreManager.selectOrCreateStockBySpecificationId(specificationId);
                stoYtStore.setRealTransit(stoYtStore.getRealTransit()+purchaseNumber);
                Long locationId = stoYtStore.getLocationId();
                Long customerId = item.getCustomerId();
                Boolean isCustomerStore=Boolean.FALSE;
                if(customerId!=null){
                    SalYtCustomerStore salYtCustomerStore = salYtCustomerStoreMapper.selectByCustomerIdAndSpecificationId(customerId, item.getSpecificationId());
                    if(salYtCustomerStore!=null){
                        //说明是独立仓的采购
                        isCustomerStore=Boolean.TRUE;
                        if(salYtCustomerStore.getLocationId()!=null){
                            locationId = salYtCustomerStore.getLocationId();
                        }
                        salYtCustomerStore.setTransitNumber(salYtCustomerStore.getTransitNumber()+purchaseNumber);
                        salYtCustomerStoreMapper.updateById(salYtCustomerStore);
                        //添加出入库记录
                        applicationEventPublisher.publishEvent(new StoreChangeEvent(this, purchaseId,StoreEnterOutTypeEnum.createPurchaseOrder.getKey(), purchaseNumber, item.getSpecificationId(),salYtCustomerStore));
                    }
                }
                if(!isCustomerStore){
                    //不是独立仓的采购
                    //生成出入库记录要用的增加的占用在途
                    Integer occupyTransitChange=0;
                    if(orderSubItemId!=null){
                        SalYtOrderSubItem orderSubItem = salYtOrderSubItemMapper.selectById(orderSubItemId);
                        Integer occupyStoreNumber = orderSubItem.getOccupyStoreNumber() == null ? 0 : orderSubItem.getOccupyStoreNumber();
                        Integer occupyTransitNumber = orderSubItem.getOccupyTransitNumber() == null ? 0 : orderSubItem.getOccupyTransitNumber();
                        //被占用数量=下单数量-占用库存-占用在途
                        Integer orderSubItemOccupyNumber = orderSubItem.getNumber() - occupyStoreNumber - occupyTransitNumber;
                        if (orderSubItemOccupyNumber < 0) {
                            orderSubItemOccupyNumber = 0;
                        }
                        stoYtStore.setOccupyTransit(stoYtStore.getOccupyTransit()+orderSubItemOccupyNumber);
                        stoYtStore.setEnableTransit(stoYtStore.getEnableTransit()+purchaseNumber-orderSubItemOccupyNumber);
                        occupyTransitChange=orderSubItemOccupyNumber;
                        //这里并不需要更新到订单子项，因为订单子项的占用在途说的是占用的已经生成的采购单中的数量，
                        //生成采购单后该订单的总占用在途数量就是整个订单数量-入库数量，其他的数量都是在占用在途
//                        if (occupyTransitChange > 0) {
//                            orderSubItem.setOccupyTransitNumber(occupyTransitNumber + occupyTransitChange);
//                            salYtOrderSubItemMapper.updateById(orderSubItem);
//                        }
                    }else {
                        stoYtStore.setEnableTransit(stoYtStore.getEnableTransit()+purchaseNumber);
                        occupyTransitChange=0;
                    }
                    stoYtStoreMapper.updateById(stoYtStore);
                    //添加出入库记录
                    applicationEventPublisher.publishEvent(new StoreChangeEvent(this, purchaseId,StoreEnterOutTypeEnum.createPurchaseOrder.getKey(), purchaseNumber,occupyTransitChange, item.getSpecificationId(), item.getOrderSubId(),stoYtStore));
                }
                //增加入库单
                StoYtStoreOrder stoYtStoreOrder = new StoYtStoreOrder();
                stoYtStoreOrder.setCode(EntityCodeGenerateUtil.generateUniqueId("C"));
                stoYtStoreOrder.setType(StoreEnterOutTypeEnum.purchaseEnterStore.getKey().toString());
                stoYtStoreOrder.setPurchaseId(purchaseId);
                stoYtStoreOrder.setProductId(item.getProductId());
                stoYtStoreOrder.setSpecificationId(item.getSpecificationId());
                stoYtStoreOrder.setSupplierId(purYtPurchase.getSupplierId());
                stoYtStoreOrder.setPurchaseItemId(item.getId());
                stoYtStoreOrder.setLocationId(locationId);
                stoYtStoreOrder.setCustomerId(customerId);
                stoYtStoreOrder.setTotalNumber(item.getNumber());
                stoYtStoreOrder.setEnterNumber(0);
                stoYtStoreOrderMapper.insert(stoYtStoreOrder);
            }
        }

    }


    //采购单规格确认，添加入库单
    @EventListener(PurchaseEvent.class)
    @Transactional
    public void purchaseConfirm(PurchaseEvent purchaseEvent){
        if(purchaseEvent.getPurchaseItemId()==null){
            //说明不是采购单确认，直接跳过
            return;
        }
        Long purchaseItemId = purchaseEvent.getPurchaseItemId();
        Integer number = purchaseEvent.getNumber();

        PurYtPurchaseItem purYtPurchaseItem = purYtPurchaseItemMapper.selectById(purchaseItemId);
        Long purchaseId = purYtPurchaseItem.getPurchaseId();
        Long orderSubItemId = purYtPurchaseItem.getOrderSubItemId();
        SalYtOrderSubItem salYtOrderSubItem = salYtOrderSubItemMapper.selectById(orderSubItemId);
        Long orderSubId = salYtOrderSubItem.getOrderSubId();
        SalYtOrderSub salYtOrderSub = salYtOrderSubMapper.selectById(orderSubId);
        SalYtOrder salYtOrder = salYtOrderMapper.selectById(salYtOrderSub.getOrderId());
        PurYtPurchase purYtPurchase = purYtPurchaseMapper.selectById(purchaseId);

        Boolean isInboundDelivery = salYtOrder.getIsInboundDelivery();
        if(!isInboundDelivery){
            return;
        }
        //确认是否有入库单，没有则新增一个入库单，如果有则往入库单增加数量（有没有入库单取决于这个半成品确认是否有未通知过的采购单item）
        StoYtStoreOrder stoYtStoreOrder = stoYtStoreOrderMapper.selectByPurchaseItemId(purchaseItemId);
        if(stoYtStoreOrder!=null){
            stoYtStoreOrder.setTotalNumber(stoYtStoreOrder.getTotalNumber()+number);
            stoYtStoreOrderMapper.updateById(stoYtStoreOrder);
        }else {
            StoYtStore stoYtStore = stoYtStoreManager.selectOrCreateStockBySpecificationId(purYtPurchaseItem.getSpecificationId());
            //增加入库单
            stoYtStoreOrder = new StoYtStoreOrder();
            stoYtStoreOrder.setCode(EntityCodeGenerateUtil.generateUniqueId("C"));
            stoYtStoreOrder.setType(StoreEnterOutTypeEnum.purchaseEnterStore.getKey().toString());
            stoYtStoreOrder.setPurchaseId(purYtPurchaseItem.getPurchaseId());
            stoYtStoreOrder.setProductId(purYtPurchaseItem.getProductId());
            stoYtStoreOrder.setSpecificationId(purYtPurchaseItem.getSpecificationId());
            stoYtStoreOrder.setSupplierId(purYtPurchase.getSupplierId());
            stoYtStoreOrder.setPurchaseItemId(purchaseItemId);
            Long customerId = purYtPurchaseItem.getCustomerId();
            Long locationId = stoYtStore.getLocationId();
            stoYtStoreOrder.setLocationId(locationId);
            if(customerId!=null){
                stoYtStoreOrder.setCustomerId(customerId);
                SalYtCustomerStore salYtCustomerStore = salYtCustomerStoreMapper.selectByCustomerIdAndSpecificationId(customerId, purYtPurchaseItem.getSpecificationId());
                if(salYtCustomerStore!=null&& salYtCustomerStore.getLocationId()!=null){
                    stoYtStoreOrder.setLocationId(salYtCustomerStore.getLocationId());
                }
            }
            stoYtStoreOrder.setTotalNumber(number);
            stoYtStoreOrder.setEnterNumber(0);
            stoYtStoreOrderMapper.insert(stoYtStoreOrder);
        }
    }
}
