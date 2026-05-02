/*
 * @author java_deng
 * @date 2025/12/9 17:30
 * @description
 */
package com.qiaomoyun.listener.yt;


import com.qiaomoyun.entity.sal.yt.SalYtCustomerStore;
import com.qiaomoyun.entity.sal.yt.SalYtOrder;
import com.qiaomoyun.entity.sal.yt.SalYtOrderSub;
import com.qiaomoyun.entity.sal.yt.SalYtOrderSubItem;
import com.qiaomoyun.entity.sto.yt.StoYtDeliveryItem;
import com.qiaomoyun.entity.sto.yt.StoYtStore;
import com.qiaomoyun.eunm.yt.OrderSubItemStatusEnum;

import java.time.LocalDateTime;
import com.qiaomoyun.event.yt.DeliveryEvent;
import com.qiaomoyun.mapper.sal.yt.SalYtCustomerStoreMapper;
import com.qiaomoyun.mapper.sal.yt.SalYtOrderMapper;
import com.qiaomoyun.mapper.sal.yt.SalYtOrderSubItemMapper;
import com.qiaomoyun.mapper.sal.yt.SalYtOrderSubMapper;
import com.qiaomoyun.mapper.sto.yt.StoYtDeliveryItemMapper;
import com.qiaomoyun.mapper.sto.yt.StoYtDeliveryMapper;
import com.qiaomoyun.mapper.sto.yt.StoYtStoreMapper;
import com.qiaomoyun.param.sto.yt.StoYtDeliveryQueryParams;
import com.qiaomoyun.eunm.yt.DeliveryOrderStatusEnum;
import com.qiaomoyun.entity.sto.yt.StoYtDelivery;
import com.qiaomoyun.util.EntityCodeGenerateUtil;
import com.qiaomoyun.vo.sto.yt.StoYtDeliveryVo;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class DeliveryEventListener {

    private final SalYtOrderSubItemMapper salYtOrderSubItemMapper;
    private final SalYtOrderSubMapper salYtOrderSubMapper;
    private final SalYtOrderMapper salYtOrderMapper;
    private final StoYtDeliveryMapper stoYtDeliveryMapper;
    private final SalYtCustomerStoreMapper salYtCustomerStoreMapper;
    private final StoYtStoreMapper stoYtStoreMapper;
    private final StoYtDeliveryItemMapper stoYtDeliveryItemMapper;

    public DeliveryEventListener(SalYtOrderSubItemMapper salYtOrderSubItemMapper, SalYtOrderSubMapper salYtOrderSubMapper, SalYtOrderMapper salYtOrderMapper, StoYtDeliveryMapper stoYtDeliveryMapper, SalYtCustomerStoreMapper salYtCustomerStoreMapper, StoYtStoreMapper stoYtStoreMapper, StoYtDeliveryItemMapper stoYtDeliveryItemMapper) {
        this.salYtOrderSubItemMapper = salYtOrderSubItemMapper;
        this.salYtOrderSubMapper = salYtOrderSubMapper;
        this.salYtOrderMapper = salYtOrderMapper;
        this.stoYtDeliveryMapper = stoYtDeliveryMapper;
        this.salYtCustomerStoreMapper = salYtCustomerStoreMapper;
        this.stoYtStoreMapper = stoYtStoreMapper;
        this.stoYtDeliveryItemMapper = stoYtDeliveryItemMapper;
    }

    @EventListener(DeliveryEvent.class)
    @Transactional
    public void deliveryEvent(DeliveryEvent deliveryEvent){
        // 获取订单ID
        Long orderId = deliveryEvent.getOrderId();
        if (orderId == null) {
            return;
        }

        // 查询订单信息
        SalYtOrder salYtOrder = salYtOrderMapper.selectById(orderId);
        if (salYtOrder == null) {
            return;
        }

        // 供应商发货的订单不生成发货单
        if (salYtOrder.getIsInboundDelivery() != null && !salYtOrder.getIsInboundDelivery()) {
            return;
        }

        // 查询所有子订单
        List<SalYtOrderSub> orderSubList = salYtOrderSubMapper.selectSalYtOrderSubByOrderId(orderId);
        if (orderSubList == null || orderSubList.isEmpty()) {
            return;
        }

        // 遍历子订单
        for (SalYtOrderSub salYtOrderSub : orderSubList) {
            Long orderSubId = salYtOrderSub.getId();

            // 如果是半成品单判断子订单是否已经都确认完毕
            if (salYtOrderSub.getOrderType().equals("1") && !validOrderSubCompleted(salYtOrderSub)) {
                break;
            }

            // 查询子订单的所有item
            List<SalYtOrderSubItem> subItemList = salYtOrderSubItemMapper.selectSalYtOrderSubItemByOrderSubId(orderSubId);
            if (subItemList == null || subItemList.isEmpty()) {
                continue;
            }

            for (SalYtOrderSubItem item : subItemList) {
                int totalNumber = item.getNumber() != null ? item.getNumber() : 0;
                int endReturnNumber = item.getEndReturnNumber() != null ? item.getEndReturnNumber() : 0;
                int deliveryNumber = item.getDeliveryNumber() != null ? item.getDeliveryNumber() : 0;
                int effectiveTotalNumber = Math.max(totalNumber - endReturnNumber, 0);
                int waitDeliveryNumber = getWaitDeliveryNumber(item.getId());
                int pendingDeliveryNumber = Math.max(effectiveTotalNumber - deliveryNumber - waitDeliveryNumber, 0);
                if (OrderSubItemStatusEnum.Closed.getKey().equals(item.getStatus())) {
                    pendingDeliveryNumber = 0;
                }

                // 直接生成或同步待打包发货单，不再校验发货形式
                StoYtDelivery delivery = getDelivery(orderId);
                StoYtDeliveryItem queryItem = new StoYtDeliveryItem();
                queryItem.setDeliveryId(delivery.getId());
                queryItem.setOrderSubItemId(item.getId());
                List<StoYtDeliveryItem> existingItems = stoYtDeliveryItemMapper.list(queryItem);
                StoYtDeliveryItem existingItem = (existingItems == null || existingItems.isEmpty()) ? null : existingItems.get(0);

                if (pendingDeliveryNumber <= 0) {
                    if (existingItem != null) {
                        existingItem.setIsDeleted(1);
                        stoYtDeliveryItemMapper.updateById(existingItem);
                    }
                    continue;
                }

                StoYtDeliveryItem stoYtDeliveryItem = existingItem != null ? existingItem : new StoYtDeliveryItem();
                stoYtDeliveryItem.setDeliveryId(delivery.getId());
                stoYtDeliveryItem.setOrderSubId(item.getOrderSubId());
                stoYtDeliveryItem.setOrderSubItemId(item.getId());
                stoYtDeliveryItem.setProductId(item.getProductId());
                stoYtDeliveryItem.setSpecificationId(item.getSpecificationId());
                Long customerId = salYtOrder.getCustomerId();
                SalYtCustomerStore salYtCustomerStore = salYtCustomerStoreMapper.selectByCustomerIdAndSpecificationId(customerId, item.getSpecificationId());
                if (salYtCustomerStore != null) {
                    stoYtDeliveryItem.setLocationId(salYtCustomerStore.getLocationId());
                } else {
                    StoYtStore stoYtStore = stoYtStoreMapper.selectBySpecificationId(item.getSpecificationId());
                    if (stoYtStore != null) {
                        stoYtDeliveryItem.setLocationId(stoYtStore.getLocationId());
                    }
                }
                stoYtDeliveryItem.setNumber(pendingDeliveryNumber);
                stoYtDeliveryItem.setShippedNumber(0);
                stoYtDeliveryItem.setCategorySpecificationItemId(item.getLabelId());
                stoYtDeliveryItem.setCategorySpecificationItemName(item.getLabelName());
                if (existingItem == null) {
                    stoYtDeliveryItemMapper.insert(stoYtDeliveryItem);
                } else {
                    stoYtDeliveryItemMapper.updateById(stoYtDeliveryItem);
                }
            }
        }
    }

    private Boolean validOrderSubCompleted(SalYtOrderSub salYtOrderSub) {
        Boolean result = true;
        Long id = salYtOrderSub.getId();
        List<SalYtOrderSubItem> salYtOrderSubItems = salYtOrderSubItemMapper.selectSalYtOrderSubItemByOrderSubId(id);
        for(SalYtOrderSubItem item : salYtOrderSubItems){
            Long specificationId = item.getSpecificationId();
            if(specificationId==null){
                // 说明是半成品需要判断是否确认
                Long itemId = item.getId();
                // 遍历查找confirmItemId等于当前itemId的item
                int totalConfirmNumber = 0;
                for(SalYtOrderSubItem subItem : salYtOrderSubItems){
                    if(itemId.equals(subItem.getConfirmItemId())){
                        totalConfirmNumber += subItem.getNumber();
                    }
                }
                // 判断确认数量总和是否和当前item的number相等
                if(item.getNumber()>totalConfirmNumber){
                    result = false;
                    break;
                }
            }
        }

        return result;
    }

    private int getWaitDeliveryNumber(Long orderSubItemId) {
        if (orderSubItemId == null) {
            return 0;
        }
        List<Map<String, Object>> numberList = stoYtDeliveryItemMapper.sumWaitDeliveryNumberByOrderSubItemIds(
            Collections.singletonList(orderSubItemId)
        );
        if (numberList == null || numberList.isEmpty()) {
            return 0;
        }
        Object totalNumber = numberList.get(0).get("totalNumber");
        if (totalNumber == null) {
            return 0;
        }
        return Integer.parseInt(totalNumber.toString());
    }

    @Transactional
    public StoYtDelivery getDelivery(Long orderId){
        SalYtOrder salYtOrder = salYtOrderMapper.selectById(orderId);
        Long customerId = salYtOrder.getCustomerId();
        // 查找当前客户下待发货状态的发货单
        StoYtDeliveryQueryParams queryParams = new StoYtDeliveryQueryParams();
        queryParams.setCustomerId(customerId);
        queryParams.setStatus(DeliveryOrderStatusEnum.waitPackage.getKey().toString());
        queryParams.setAddressId(salYtOrder.getCustomerAddressId());
        List<StoYtDeliveryVo> deliveryList = stoYtDeliveryMapper.list(queryParams);
        StoYtDelivery delivery=null;
        if(deliveryList==null||deliveryList.isEmpty()){
            delivery = new StoYtDelivery();
            delivery.setCode(EntityCodeGenerateUtil.generateUniqueId("F"));
            delivery.setCustomerId(customerId);
            delivery.setAddressId(salYtOrder.getCustomerAddressId());
            delivery.setStatus(DeliveryOrderStatusEnum.waitPackage.getKey().toString());
            delivery.setAddress(salYtOrder.getCustomerAddress());
            delivery.setConsignee(salYtOrder.getReceiver());
            delivery.setPhone(salYtOrder.getReceiverPhone());
            stoYtDeliveryMapper.insert(delivery);
        }else {
            delivery=deliveryList.get(0);
            delivery.setUpdateTime(LocalDateTime.now());
            stoYtDeliveryMapper.updateById(delivery);
        }
        return delivery;
    }

}
