/*
 * @author java_deng
 * @date 2025/11/27 14:33
 * @description
 */
package com.qiaomoyun.manager.sal.yt;

import com.qiaomoyun.entity.pro.yt.ProYtProductSpecificationItem;
import com.qiaomoyun.entity.pur.yt.PurYtPurchaseItem;
import com.qiaomoyun.entity.sal.yt.SalYtOrderSubItem;
import com.qiaomoyun.entity.sal.yt.SalYtOrderSubItemOperation;
import com.qiaomoyun.entity.sto.yt.StoYtDelivery;
import com.qiaomoyun.entity.sto.yt.StoYtDeliveryItem;
import com.qiaomoyun.entity.sto.yt.StoYtStoreOrder;
import com.qiaomoyun.entity.sto.yt.StoYtStoreOrderOperation;
import com.qiaomoyun.eunm.yt.ItemOperationTypeEnum;
import com.qiaomoyun.manager.pro.yt.ProYtProductManager;
import com.qiaomoyun.mapper.pur.yt.PurYtPurchaseItemMapper;
import com.qiaomoyun.mapper.sal.yt.SalYtOrderSubItemMapper;
import com.qiaomoyun.mapper.sal.yt.SalYtOrderSubItemOperationMapper;
import com.qiaomoyun.mapper.sto.yt.StoYtStoreOrderMapper;
import com.qiaomoyun.mapper.sto.yt.StoYtStoreOrderOperationMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

@Service
public class SalYtOrderSubItemOperationManager {
    @Resource
    private SalYtOrderSubItemOperationMapper salYtOrderSubItemOperationMapper;
    @Resource
    private SalYtOrderSubItemMapper  salYtOrderSubItemMapper;
    @Resource
    private PurYtPurchaseItemMapper purYtPurchaseItemMapper;
    @Resource
    private StoYtStoreOrderMapper stoYtStoreOrderMapper;
    @Resource
    private StoYtStoreOrderOperationMapper stoYtStoreOrderOperationMapper;
    @Autowired
    private ProYtProductManager proYtProductManager;

    //订单申购
    @Transactional
    public void placeOrderByItemIds(HashSet<Long> itemIdSet) {
        itemIdSet.forEach(itemId->{
            SalYtOrderSubItem salYtOrderSubItem = salYtOrderSubItemMapper.selectById(itemId);
            Integer occupyStoreNumber = salYtOrderSubItem.getOccupyStoreNumber();
            Integer occupyTransitNumber = salYtOrderSubItem.getOccupyTransitNumber();
            Integer number = salYtOrderSubItem.getNumber();
            Integer applyPurchaseNumber = salYtOrderSubItem.getApplyPurchaseNumber();

            Integer type= ItemOperationTypeEnum.PlaceOrder.getKey();

            SalYtOrderSubItemOperation operation = new SalYtOrderSubItemOperation();
            operation.setOperationCount(number);
            operation.setOrderSubItemId(itemId);
            operation.setOccupyTransit(occupyTransitNumber);
            operation.setApplyPurchaseCount(applyPurchaseNumber);
            operation.setType(type);
            operation.setOccupyStore(occupyStoreNumber);
            salYtOrderSubItemOperationMapper.insert(operation);
        });
    }

    public List<SalYtOrderSubItemOperation> operationList(Long subItemId){
        return salYtOrderSubItemOperationMapper.selectByOrderSubItemId(subItemId);
    }

    public List<SalYtOrderSubItemOperation> operationListWithPurchase(Long subItemId) {
        return operationList(subItemId);
//        List<PurYtPurchaseItem> purchaseItemList = purYtPurchaseItemMapper.selectListByOrderSubItemId(subItemId);
//        if (purchaseItemList == null || purchaseItemList.isEmpty()) {
//            return operationList(subItemId);
//        }
//        SalYtOrderSubItem orderSubItem = salYtOrderSubItemMapper.selectById(subItemId);
//        List<SalYtOrderSubItemOperation> operationList = new ArrayList<>();
//        for (PurYtPurchaseItem purchaseItem : purchaseItemList) {
//            operationList.addAll(
//                    salYtOrderSubItemOperationMapper.selectByPurchaseItemIdOrOrderSubItemId(purchaseItem.getId(), subItemId)
//            );
//        }

//        int remainNeedEnterNumber = 0;
//        if (orderSubItem != null) {
//            int totalNumber = orderSubItem.getNumber() == null ? 0 : orderSubItem.getNumber();
//            int occupyStoreNumber = orderSubItem.getOccupyStoreNumber() == null ? 0 : orderSubItem.getOccupyStoreNumber();
//            int deliveryNumber = orderSubItem.getDeliveryNumber() == null ? 0 : orderSubItem.getDeliveryNumber();
//            int endReturnNumber = orderSubItem.getEndReturnNumber() == null ? 0 : orderSubItem.getEndReturnNumber();
//            remainNeedEnterNumber = Math.max(totalNumber - occupyStoreNumber - deliveryNumber - endReturnNumber, 0);
//        }
//
//        boolean hasEnterStoreOperation = operationList.stream()
//                .anyMatch(item -> item != null && ItemOperationTypeEnum.EnterStore.getKey().equals(item.getType()));
//        if (!hasEnterStoreOperation && remainNeedEnterNumber > 0) {
//            for (PurYtPurchaseItem purchaseItem : purchaseItemList) {
//                StoYtStoreOrder storeOrder = stoYtStoreOrderMapper.selectByPurchaseItemId(purchaseItem.getId());
//                if (storeOrder == null) {
//                    continue;
//                }
//                List<StoYtStoreOrderOperation> storeOrderOperations =
//                        stoYtStoreOrderOperationMapper.selectByStoreOrderId(storeOrder.getId());
//                if (storeOrderOperations != null && !storeOrderOperations.isEmpty()) {
//                    for (StoYtStoreOrderOperation storeOrderOperation : storeOrderOperations) {
//                        if (storeOrderOperation == null || storeOrderOperation.getNumber() == null || storeOrderOperation.getNumber() <= 0) {
//                            continue;
//                        }
//                        SalYtOrderSubItemOperation enterOperation = new SalYtOrderSubItemOperation();
//                        enterOperation.setType(ItemOperationTypeEnum.EnterStore.getKey());
//                        enterOperation.setOrderSubItemId(subItemId);
//                        enterOperation.setPurchaseItemId(purchaseItem.getId());
//                        enterOperation.setOperationCount(storeOrderOperation.getNumber());
//                        enterOperation.setCreateTime(storeOrderOperation.getCreateTime());
//                        enterOperation.setCreateUserName(storeOrderOperation.getCreateUserName());
//                        operationList.add(enterOperation);
//                    }
//                } else if (storeOrder.getEnterNumber() != null && storeOrder.getEnterNumber() > 0) {
//                    SalYtOrderSubItemOperation enterOperation = new SalYtOrderSubItemOperation();
//                    enterOperation.setType(ItemOperationTypeEnum.EnterStore.getKey());
//                    enterOperation.setOrderSubItemId(subItemId);
//                    enterOperation.setPurchaseItemId(purchaseItem.getId());
//                    enterOperation.setOperationCount(storeOrder.getEnterNumber());
//                    enterOperation.setCreateTime(storeOrder.getUpdateTime());
//                    operationList.add(enterOperation);
//                }
//            }
//        }

//        operationList.sort(
//                Comparator.comparing(SalYtOrderSubItemOperation::getCreateTime, Comparator.nullsLast(Comparator.naturalOrder())).reversed()
//        );
//        return operationList;
    }

    //退货
    public void returnOperation(Integer operationNumber,Long itemId) {
        SalYtOrderSubItemOperation operation = new SalYtOrderSubItemOperation();
        operation.setOperationCount(operationNumber);
        operation.setType(ItemOperationTypeEnum.Return.getKey());
        operation.setOrderSubItemId(itemId);
        salYtOrderSubItemOperationMapper.insert(operation);
    }

    //采购单退货
    public void purchaseReturnOperation(Integer operationNumber,Long itemId) {
        SalYtOrderSubItemOperation operation = new SalYtOrderSubItemOperation();
        operation.setOperationCount(operationNumber);
        operation.setType(ItemOperationTypeEnum.PURCHASE_RETURN.getKey());
        operation.setPurchaseItemId(itemId);
        salYtOrderSubItemOperationMapper.insert(operation);
    }

    //关闭订单
    public void closeOrderOperation(Integer operationNumber, BigDecimal operationAmount, Long itemId) {
        SalYtOrderSubItemOperation operation = new SalYtOrderSubItemOperation();
        operation.setOperationCount(operationNumber);
        operation.setType(ItemOperationTypeEnum.CLOSE_ORDER.getKey());
        operation.setOrderSubItemId(itemId);
        operation.setOperationCode(operationAmount == null ? "0" : operationAmount.stripTrailingZeros().toPlainString());
        salYtOrderSubItemOperationMapper.insert(operation);
    }

    //采购
    public void purchaseOperation(Integer operationNumber,Long itemId,String operationOrderCode) {
        SalYtOrderSubItemOperation operation = new SalYtOrderSubItemOperation();
        operation.setOperationCount(operationNumber);
        operation.setOperationOrderCode(operationOrderCode);
        operation.setType(ItemOperationTypeEnum.Purchase.getKey());
        operation.setOrderSubItemId(itemId);
        salYtOrderSubItemOperationMapper.insert(operation);
    }

    //入库
    public void enterStoreOperation(Integer operationNumber,Long orderSubItemId) {
        SalYtOrderSubItemOperation operation = new SalYtOrderSubItemOperation();
        operation.setOperationCount(operationNumber);
        operation.setType(ItemOperationTypeEnum.EnterStore.getKey());
        operation.setOrderSubItemId(orderSubItemId);
        salYtOrderSubItemOperationMapper.insert(operation);
    }

    public void enterStoreOperation(Integer operationNumber,Long orderSubItemId,Long purchaseItemId) {
        SalYtOrderSubItemOperation operation = new SalYtOrderSubItemOperation();
        operation.setOperationCount(operationNumber);
        operation.setType(ItemOperationTypeEnum.EnterStore.getKey());
        operation.setOrderSubItemId(orderSubItemId);
        //-1表示不是强绑定的采购单入库的，而是其他采购单分配入库的
        operation.setPurchaseItemId(purchaseItemId);
        salYtOrderSubItemOperationMapper.insert(operation);
    }

    //未关联订单的采购单入库
    public void enterStorePurchaseOperation(Integer operationNumber,Long purchaseItemId) {
        SalYtOrderSubItemOperation operation = new SalYtOrderSubItemOperation();
        operation.setOperationCount(operationNumber);
        operation.setType(ItemOperationTypeEnum.EnterStore.getKey());
        operation.setPurchaseItemId(purchaseItemId);
        salYtOrderSubItemOperationMapper.insert(operation);
    }


    //发货
    public void delivery(StoYtDeliveryItem deliveryItem, StoYtDelivery delivery) {
        delivery(deliveryItem, delivery, null);
    }

    //发货（带订单号）
    public void delivery(StoYtDeliveryItem deliveryItem, StoYtDelivery delivery, String orderCode) {
        Long orderSubItemId = deliveryItem.getOrderSubItemId();
        Integer number = deliveryItem.getNumber();
        SalYtOrderSubItemOperation salYtOrderSubItemOperation = new SalYtOrderSubItemOperation();
        salYtOrderSubItemOperation.setType(ItemOperationTypeEnum.Delivery.getKey());
        salYtOrderSubItemOperation.setOrderSubItemId(orderSubItemId);
        salYtOrderSubItemOperation.setOperationCount(number);
        salYtOrderSubItemOperation.setOperationOrderCode(delivery.getCode());
        salYtOrderSubItemOperation.setOperationCode(orderCode);
        salYtOrderSubItemOperationMapper.insert(salYtOrderSubItemOperation);
    }

    //订单确认发货
    public void confirmDelivery(Long orderSubItemId,Integer deliveryNumber) {
        SalYtOrderSubItemOperation salYtOrderSubItemOperation = new SalYtOrderSubItemOperation();
        salYtOrderSubItemOperation.setType(ItemOperationTypeEnum.Delivery.getKey());
        salYtOrderSubItemOperation.setOrderSubItemId(orderSubItemId);
        salYtOrderSubItemOperation.setOperationCount(deliveryNumber);
//        salYtOrderSubItemOperation.setOperationOrderCode(delivery.getCode());
        salYtOrderSubItemOperationMapper.insert(salYtOrderSubItemOperation);
    }

    //确认半成品
    public void confirm(SalYtOrderSubItem confirmItem) {
        SalYtOrderSubItemOperation salYtOrderSubItemOperation = new SalYtOrderSubItemOperation();
        salYtOrderSubItemOperation.setType(ItemOperationTypeEnum.CONFIRM_SPECIFICATION.getKey());
        salYtOrderSubItemOperation.setOperationCount(confirmItem.getNumber());
        salYtOrderSubItemOperation.setOrderSubItemId(confirmItem.getId());
        List<ProYtProductSpecificationItem> itemsListBySpecification = proYtProductManager.getItemsListBySpecification(confirmItem.getSpecificationId());
        if(itemsListBySpecification!=null && !itemsListBySpecification.isEmpty()){
            StringBuilder name= new StringBuilder();
            for(ProYtProductSpecificationItem item:itemsListBySpecification){
                name.append(item.getCategorySpecificationItemValue()).append(" ");
            }
            salYtOrderSubItemOperation.setOperationCode(name.toString());
        }
        salYtOrderSubItemOperationMapper.insert(salYtOrderSubItemOperation);
    }
}
