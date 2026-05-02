/*
 * @author java_deng
 * @date 2025/12/9 10:25
 * @description
 */
package com.qiaomoyun.listener.yt;

import com.qiaomoyun.entity.pur.yt.PurYtPurchase;
import com.qiaomoyun.entity.sal.yt.SalYtCustomerStore;
import com.qiaomoyun.entity.sal.yt.SalYtOrder;
import com.qiaomoyun.entity.sal.yt.SalYtOrderSub;
import com.qiaomoyun.entity.sto.yt.StoYtStore;
import com.qiaomoyun.entity.sto.yt.StoYtStoreRecord;
import com.qiaomoyun.eunm.yt.StoreEnterOutTypeEnum;
import com.qiaomoyun.event.yt.StoreChangeEvent;
import com.qiaomoyun.manager.sto.yt.StoYtStoreManager;
import com.qiaomoyun.mapper.pur.yt.PurYtPurchaseMapper;
import com.qiaomoyun.mapper.sal.yt.SalYtCustomerStoreMapper;
import com.qiaomoyun.mapper.sal.yt.SalYtOrderMapper;
import com.qiaomoyun.mapper.sal.yt.SalYtOrderSubMapper;
import com.qiaomoyun.mapper.sto.yt.StoYtStoreMapper;
import com.qiaomoyun.mapper.sto.yt.StoYtStoreRecordMapper;
import com.qiaomoyun.util.EntityCodeGenerateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StoreEventListener {
    @Autowired
    private StoYtStoreRecordMapper stoYtStoreRecordMapper;
    @Autowired
    private StoYtStoreMapper stoYtStoreMapper;
    @Autowired
    private SalYtCustomerStoreMapper salYtCustomerStoreMapper;
    @Autowired
    private StoYtStoreManager stoYtStoreManager;
    @Autowired
    private SalYtOrderMapper salYtOrderMapper;
    @Autowired
    private PurYtPurchaseMapper purYtPurchaseMapper;
    @Autowired
    private SalYtOrderSubMapper salYtOrderSubMapper;

    //创建订单
//    @Async("StoreRecordExecutor") //不在事务内导致拿到的库存数据有误
    @Transactional
    @EventListener(StoreChangeEvent.class)
    public void saveRecordByCreateOrder(StoreChangeEvent storeChangeEvent) {
        Integer changeType = storeChangeEvent.getChangeType();
        if(changeType.equals(StoreEnterOutTypeEnum.createOrder.getKey())){
            Long customerStoreId = storeChangeEvent.getCustomerStoreId();
            Integer occupyStoreChange = storeChangeEvent.getOccupyStoreChange();
            Integer occupyTransitChange = storeChangeEvent.getOccupyTransitChange();
            Long specificationId = storeChangeEvent.getSpecificationId();
            Long orderSubId = storeChangeEvent.getOrderSubId();
            StoYtStoreRecord storeRecord = createStoreRecord(specificationId);
            storeRecord.setType(changeType);
            storeRecord.setOrderSubId(orderSubId);
            storeRecord.setCode(EntityCodeGenerateUtil.generateUniqueId("C"));
            if(customerStoreId==null){
                //公共仓创建订单
                storeRecord.setOccupyStoreInit(storeRecord.getOccupyStoreInit()-occupyStoreChange);
                storeRecord.setOccupyStoreChange(occupyStoreChange);
                storeRecord.setOccupyTransitInit(storeRecord.getOccupyTransitInit()-occupyTransitChange);
                storeRecord.setOccupyTransitChange(occupyTransitChange);
                storeRecord.setEnableStoreInit(storeRecord.getEnableStoreInit()+occupyStoreChange);
                storeRecord.setEnableStoreChange(-occupyStoreChange);
                storeRecord.setEnableTransitInit(storeRecord.getEnableTransitInit()+occupyTransitChange);
                storeRecord.setEnableTransitChange(-occupyTransitChange);

            }else if(customerStoreId!=null){
                storeRecord= createCustomerRecord(customerStoreId);
                storeRecord.setType(changeType);
                storeRecord.setOrderSubId(orderSubId);
                storeRecord.setCode(EntityCodeGenerateUtil.generateUniqueId("C"));
                SalYtCustomerStore salYtCustomerStore = salYtCustomerStoreMapper.selectById(customerStoreId);
                //独立仓创建订单
                storeRecord.setRealStoreInit(salYtCustomerStore.getStoreNumber()+occupyStoreChange);
                storeRecord.setRealStoreChange(-occupyStoreChange);
                storeRecord.setCustomerId(salYtCustomerStore.getCustomerId());
            }
            stoYtStoreRecordMapper.insert(storeRecord);
        }
    }

    //创建采购单
    @Async("StoreRecordExecutor")
    @EventListener(StoreChangeEvent.class)
    public void saveRecordByCreatePurchase(StoreChangeEvent storeChangeEvent) {
        Integer changeType = storeChangeEvent.getChangeType();
        if(changeType.equals(StoreEnterOutTypeEnum.createPurchaseOrder.getKey())){
            Integer realTransitChange = storeChangeEvent.getRealTransitChange();
            Integer occupyTransitChange = storeChangeEvent.getOccupyTransitChange();
            Long specificationId = storeChangeEvent.getSpecificationId();
            Long orderSubId = storeChangeEvent.getOrderSubId();
            StoYtStoreRecord storeRecord = createStoreRecord(specificationId,storeChangeEvent.getStoYtStore());

            storeRecord.setType(changeType);
            storeRecord.setOrderSubId(orderSubId);
            storeRecord.setPurchaseId(storeChangeEvent.getPurchaseId());
            storeRecord.setCode(EntityCodeGenerateUtil.generateUniqueId("C"));
            storeRecord.setRealTransitInit(storeRecord.getRealTransitInit()-realTransitChange);
            storeRecord.setRealTransitChange(realTransitChange);
            storeRecord.setOccupyTransitInit(storeRecord.getOccupyTransitInit()-occupyTransitChange);
            storeRecord.setOccupyTransitChange(occupyTransitChange);
            storeRecord.setEnableTransitInit(storeRecord.getEnableTransitInit()-(realTransitChange-occupyTransitChange));
            storeRecord.setEnableTransitChange(realTransitChange-occupyTransitChange);
            stoYtStoreRecordMapper.insert(storeRecord);
        }
    }

    //创建采购单
    @Async("StoreRecordExecutor")
    @EventListener(StoreChangeEvent.class)
    public void saveRecordByCreateCustomerPurchase(StoreChangeEvent storeChangeEvent) {
        Integer changeType = storeChangeEvent.getChangeType();
        SalYtCustomerStore customerStore = storeChangeEvent.getCustomerStore();
        if(changeType.equals(StoreEnterOutTypeEnum.createPurchaseOrder.getKey()) && customerStore!=null){
            Integer realTransitChange = storeChangeEvent.getRealTransitChange();
            Long specificationId = storeChangeEvent.getSpecificationId();
            StoYtStoreRecord storeRecord = createCustomerRecord(customerStore.getId());
            storeRecord.setType(changeType);
            storeRecord.setPurchaseId(storeChangeEvent.getPurchaseId());
            storeRecord.setCode(EntityCodeGenerateUtil.generateUniqueId("C"));
//            storeRecord.setRealTransitInit(storeRecord.getRealTransitInit()-realTransitChange);
            storeRecord.setRealTransitChange(realTransitChange);
//            storeRecord.setEnableTransitInit(storeRecord.getEnableTransitInit()-realTransitChange);
            storeRecord.setEnableTransitChange(realTransitChange);
            stoYtStoreRecordMapper.insert(storeRecord);
        }
    }

    //独立仓转入转出
    @Transactional
    @EventListener(StoreChangeEvent.class)
    public void saveRecordByCustomerStore(StoreChangeEvent storeChangeEvent) {
        Integer changeType = storeChangeEvent.getChangeType();
        if(!changeType.equals(StoreEnterOutTypeEnum.customerSimpleOutStore.getKey())&&!changeType.equals(StoreEnterOutTypeEnum.customerSimpleEnterStore.getKey())){
            return;
        }
        Integer realStoreChange = storeChangeEvent.getRealStoreChange();
        Long customerId = storeChangeEvent.getCustomerId();
        Long specificationId = storeChangeEvent.getSpecificationId();
        SalYtCustomerStore salYtCustomerStore = salYtCustomerStoreMapper.selectByCustomerIdAndSpecificationId(customerId, specificationId);
        StoYtStoreRecord storeRecord = new  StoYtStoreRecord();
        storeRecord.setSpecificationId(specificationId);
        storeRecord.setType(changeType);
        storeRecord.setCustomerId(customerId);
        storeRecord.setCode(EntityCodeGenerateUtil.generateUniqueId("C"));
        storeRecord.setCustomerStoreId(salYtCustomerStore.getId());
        storeRecord.setRemark(storeChangeEvent.getRemark());
        if(changeType.equals(StoreEnterOutTypeEnum.customerSimpleOutStore.getKey())){
            storeRecord.setRealStoreInit(salYtCustomerStore.getStoreNumber()+realStoreChange);
            storeRecord.setRealStoreChange(-realStoreChange);
            storeRecord.setRealTransitInit(salYtCustomerStore.getTransitNumber());
            storeRecord.setRealTransitChange(0);
        }else {
            storeRecord.setRealStoreInit(salYtCustomerStore.getStoreNumber()-realStoreChange);
            storeRecord.setRealStoreChange(realStoreChange);
            storeRecord.setRealTransitInit(salYtCustomerStore.getTransitNumber());
            storeRecord.setRealTransitChange(0);
        }
        stoYtStoreRecordMapper.insert(storeRecord);
    }

    //采购单入库
    @Transactional
    @EventListener(StoreChangeEvent.class)
    public void saveRecordByPurchaseEnterStore(StoreChangeEvent storeChangeEvent) {
        Integer changeType = storeChangeEvent.getChangeType();
        if(!changeType.equals(StoreEnterOutTypeEnum.purchaseEnterStore.getKey())){
            return;
        }

        Long customerId = storeChangeEvent.getCustomerId();
        Long specificationId = storeChangeEvent.getSpecificationId();
        SalYtCustomerStore salYtCustomerStore = salYtCustomerStoreMapper.selectByCustomerIdAndSpecificationId(customerId, specificationId);
        StoYtStoreRecord storeRecord = new  StoYtStoreRecord();
        storeRecord.setSpecificationId(specificationId);
        storeRecord.setType(changeType);
        storeRecord.setCustomerId(customerId);
        storeRecord.setCode(EntityCodeGenerateUtil.generateUniqueId("C"));
        storeRecord.setPurchaseId(storeChangeEvent.getPurchaseId());
        if(salYtCustomerStore!=null){
            //独立仓采购入库
            String orderCode = storeChangeEvent.getOrderCode();
            Integer realStoreChange = storeChangeEvent.getRealStoreChange();
            storeRecord.setRealStoreInit(salYtCustomerStore.getStoreNumber()-realStoreChange);
            storeRecord.setRealStoreChange(realStoreChange);
            storeRecord.setRealTransitInit(salYtCustomerStore.getTransitNumber()+realStoreChange);
            storeRecord.setRealTransitChange(-realStoreChange);
            storeRecord.setCustomerStoreId(salYtCustomerStore.getId());
            storeRecord.setCustomerId(salYtCustomerStore.getCustomerId());
            storeRecord.setProductId(salYtCustomerStore.getProductId());
            storeRecord.setOrderCode(orderCode);
            storeRecord.setSpecificationId(salYtCustomerStore.getSpecificationId());
            stoYtStoreRecordMapper.insert(storeRecord);
        }else {
            //公共仓采购入库
            StoYtStoreRecord changeStoreRecord = storeChangeEvent.getStoYtStoreRecord();
            specificationId=changeStoreRecord.getSpecificationId();
            StoYtStore stoYtStore = stoYtStoreMapper.selectBySpecificationId(specificationId);
            String orderSubCode = changeStoreRecord.getOrderSubCode();
            String orderCode = changeStoreRecord.getOrderCode();
            storeRecord.setOrderSubCode(orderSubCode);
            storeRecord.setOrderCode(orderCode != null && !orderCode.isEmpty() ? orderCode : orderSubCode);
            storeRecord.setCustomerId(changeStoreRecord.getCustomerId());
            storeRecord.setStoreId(stoYtStore.getId());
            storeRecord.setSpecificationId(specificationId);

            // 设置在途库存变动（取反表示减少）
            storeRecord.setEnableTransitChange(-changeStoreRecord.getEnableTransitChange());
            storeRecord.setEnableTransitInit(stoYtStore.getEnableTransit()+changeStoreRecord.getEnableTransitChange());
            storeRecord.setOccupyTransitChange(-changeStoreRecord.getOccupyTransitChange());
            storeRecord.setOccupyTransitInit(stoYtStore.getOccupyTransit()+changeStoreRecord.getOccupyTransitChange());
            storeRecord.setRealTransitChange(-changeStoreRecord.getRealTransitChange());
            storeRecord.setRealTransitInit(stoYtStore.getRealTransit()+changeStoreRecord.getRealTransitChange());

            // 设置实际库存变动（在途转实际）
            storeRecord.setRealStoreChange(changeStoreRecord.getRealTransitChange());
            storeRecord.setRealStoreInit(stoYtStore.getRealStore()-changeStoreRecord.getRealTransitChange());

            // 设置占用库存变动（占用在途转占用）
            storeRecord.setOccupyStoreChange(changeStoreRecord.getOccupyTransitChange());
            storeRecord.setOccupyStoreInit(stoYtStore.getOccupyStore()-changeStoreRecord.getOccupyTransitChange());

            // 设置可用库存变动（可用在途转可用）
            storeRecord.setEnableStoreChange(changeStoreRecord.getEnableTransitChange());
            storeRecord.setEnableStoreInit(stoYtStore.getEnableStore()-changeStoreRecord.getEnableTransitChange());

            stoYtStoreRecordMapper.insert(storeRecord);
        }
    }

    //单独入库
    @Async("StoreRecordExecutor")
    @EventListener(StoreChangeEvent.class)
    public void saveRecordBySimpleEnterStore(StoreChangeEvent storeChangeEvent) {
        Integer changeType = storeChangeEvent.getChangeType();
        if(changeType.equals(StoreEnterOutTypeEnum.simpleEnterStore.getKey())){
            Integer realStoreChange = storeChangeEvent.getRealStoreChange();
            Long specificationId = storeChangeEvent.getSpecificationId();
            Long orderSubId = storeChangeEvent.getOrderSubId();
            StoYtStoreRecord storeRecord = createStoreRecord(specificationId,storeChangeEvent.getStoYtStore());
            storeRecord.setType(changeType);
            storeRecord.setOrderSubId(orderSubId);
            storeRecord.setCode(EntityCodeGenerateUtil.generateUniqueId("C"));
            storeRecord.setRealStoreInit(storeRecord.getRealStoreInit()-realStoreChange);
            storeRecord.setRealStoreChange(realStoreChange);
            storeRecord.setEnableStoreInit(storeRecord.getEnableStoreInit()-realStoreChange);
            storeRecord.setEnableStoreChange(realStoreChange);
            storeRecord.setRemark(storeChangeEvent.getRemark());
            stoYtStoreRecordMapper.insert(storeRecord);
        }
    }

    //单独出库
    @Async("StoreRecordExecutor")
    @EventListener(StoreChangeEvent.class)
    public void saveRecordBySimpleOutStore(StoreChangeEvent storeChangeEvent) {
        Integer changeType = storeChangeEvent.getChangeType();
        if(changeType.equals(StoreEnterOutTypeEnum.simpleOutStore.getKey())){
            Integer realStoreChange = storeChangeEvent.getRealStoreChange();
            Long specificationId = storeChangeEvent.getSpecificationId();
            Long orderSubId = storeChangeEvent.getOrderSubId();
            StoYtStoreRecord storeRecord = createStoreRecord(specificationId,storeChangeEvent.getStoYtStore());
            storeRecord.setType(changeType);
            storeRecord.setOrderSubId(orderSubId);
            storeRecord.setCode(EntityCodeGenerateUtil.generateUniqueId("C"));
            storeRecord.setRealStoreInit(storeRecord.getRealStoreInit()+realStoreChange);
            storeRecord.setRealStoreChange(-realStoreChange);
            storeRecord.setEnableStoreInit(storeRecord.getEnableStoreInit()+realStoreChange);
            storeRecord.setEnableStoreChange(-realStoreChange);
            storeRecord.setRemark(storeChangeEvent.getRemark());
            stoYtStoreRecordMapper.insert(storeRecord);
        }
    }

    //确认半成品
    @Transactional
    @EventListener(StoreChangeEvent.class)
    public void saveRecordByInCompleteConfirm(StoreChangeEvent storeChangeEvent) {
        Integer changeType = storeChangeEvent.getChangeType();
        if(changeType.equals(StoreEnterOutTypeEnum.inCompleteConfirm.getKey())){
            Integer incompletedNumber = storeChangeEvent.getInCompleteNumber();
            Long specificationId = storeChangeEvent.getSpecificationId();
            Long orderId = storeChangeEvent.getOrderId();
            Long purchaseId = storeChangeEvent.getPurchaseId();
            StoYtStoreRecord storeRecord = createStoreRecord(specificationId);
            storeRecord.setType(changeType);
            storeRecord.setOrderId(orderId);
            SalYtOrder salYtOrder = salYtOrderMapper.selectById(orderId);
            storeRecord.setOrderCode(salYtOrder.getCode());
            storeRecord.setPurchaseId(purchaseId);
            if(purchaseId!=null){
                PurYtPurchase purYtPurchase = purYtPurchaseMapper.selectById(purchaseId);
                storeRecord.setPurchaseCode(purYtPurchase.getCode());
            }
            storeRecord.setCode(EntityCodeGenerateUtil.generateUniqueId("C"));
            storeRecord.setRealTransitInit(storeRecord.getRealTransitInit()-incompletedNumber);
            storeRecord.setRealTransitChange(incompletedNumber);
            storeRecord.setOccupyTransitInit(storeRecord.getOccupyTransitInit()-incompletedNumber);
            storeRecord.setOccupyTransitChange(incompletedNumber);
            stoYtStoreRecordMapper.insert(storeRecord);
        }
    }

    //发货
    @Transactional
    @EventListener(StoreChangeEvent.class)
    public void saveRecordByDelivery(StoreChangeEvent storeChangeEvent) {
        Integer changeType = storeChangeEvent.getChangeType();
        SalYtCustomerStore customerStore = storeChangeEvent.getCustomerStore();
        if(changeType.equals(StoreEnterOutTypeEnum.delivery.getKey())){
            Integer deliveryNumber = storeChangeEvent.getDeliveryNumber();
            Long specificationId = storeChangeEvent.getSpecificationId();
            Long orderId = storeChangeEvent.getOrderId();
            if(customerStore==null){
                StoYtStoreRecord storeRecord = createStoreRecord(specificationId);
                storeRecord.setType(changeType);
                storeRecord.setOrderId(orderId);
                SalYtOrder salYtOrder = salYtOrderMapper.selectById(orderId);
                storeRecord.setOrderCode(salYtOrder.getCode());
                storeRecord.setCode(EntityCodeGenerateUtil.generateUniqueId("C"));
                storeRecord.setRealStoreInit(storeRecord.getRealStoreInit()+deliveryNumber);
                storeRecord.setRealStoreChange(-deliveryNumber);
                storeRecord.setOccupyStoreInit(storeRecord.getOccupyStoreInit()+deliveryNumber);
                storeRecord.setOccupyStoreChange(-deliveryNumber);
                stoYtStoreRecordMapper.insert(storeRecord);
            }else {
                StoYtStoreRecord storeRecord = createCustomerRecord(customerStore.getId());
                storeRecord.setType(changeType);
                storeRecord.setOrderId(orderId);
                storeRecord.setCustomerStoreId(customerStore.getId());
                SalYtOrder salYtOrder = salYtOrderMapper.selectById(orderId);
                storeRecord.setOrderCode(salYtOrder.getCode());
                storeRecord.setCode(EntityCodeGenerateUtil.generateUniqueId("C"));
                stoYtStoreRecordMapper.insert(storeRecord);
            }

        }
    }

    //关闭订单释放占用
    @Transactional
    @EventListener(StoreChangeEvent.class)
    public void saveRecordByCloseOrderRelease(StoreChangeEvent storeChangeEvent) {
        Integer changeType = storeChangeEvent.getChangeType();
        if (!changeType.equals(StoreEnterOutTypeEnum.closeOrderRelease.getKey())) {
            return;
        }

        Integer occupyStoreChange = storeChangeEvent.getOccupyStoreChange() != null ? storeChangeEvent.getOccupyStoreChange() : 0;
        Integer occupyTransitChange = storeChangeEvent.getOccupyTransitChange() != null ? storeChangeEvent.getOccupyTransitChange() : 0;
        if (occupyStoreChange <= 0 && occupyTransitChange <= 0) {
            return;
        }

        Long specificationId = storeChangeEvent.getSpecificationId();
        StoYtStoreRecord storeRecord = createStoreRecord(specificationId);
        storeRecord.setType(changeType);
        storeRecord.setOrderId(storeChangeEvent.getOrderId());
        storeRecord.setOrderSubId(storeChangeEvent.getOrderSubId());
        storeRecord.setCode(EntityCodeGenerateUtil.generateUniqueId("C"));
        storeRecord.setRemark(storeChangeEvent.getRemark());

        if (storeChangeEvent.getOrderId() != null) {
            SalYtOrder salYtOrder = salYtOrderMapper.selectById(storeChangeEvent.getOrderId());
            if (salYtOrder != null) {
                storeRecord.setOrderCode(salYtOrder.getCode());
            }
        }
        if (storeChangeEvent.getOrderSubId() != null) {
            SalYtOrderSub salYtOrderSub = salYtOrderSubMapper.selectById(storeChangeEvent.getOrderSubId());
            if (salYtOrderSub != null) {
                storeRecord.setOrderSubCode(salYtOrderSub.getSubCode());
            }
        }

        if (occupyStoreChange > 0) {
            storeRecord.setOccupyStoreInit(storeRecord.getOccupyStoreInit() + occupyStoreChange);
            storeRecord.setOccupyStoreChange(-occupyStoreChange);
            storeRecord.setEnableStoreInit(storeRecord.getEnableStoreInit() - occupyStoreChange);
            storeRecord.setEnableStoreChange(occupyStoreChange);
        }
        if (occupyTransitChange > 0) {
            storeRecord.setOccupyTransitInit(storeRecord.getOccupyTransitInit() + occupyTransitChange);
            storeRecord.setOccupyTransitChange(-occupyTransitChange);
            storeRecord.setEnableTransitInit(storeRecord.getEnableTransitInit() - occupyTransitChange);
            storeRecord.setEnableTransitChange(occupyTransitChange);
        }

        stoYtStoreRecordMapper.insert(storeRecord);
    }


    @Transactional
    public StoYtStoreRecord createStoreRecord(Long specificationId){
        StoYtStoreRecord stoYtStoreRecord = new StoYtStoreRecord();
        //这里使用规格id查询当前库存作为出入库记录的初期库存，可能会引发多线程问题，如果出现初期库存和实际修改前库存不一致的问题，可考虑修改此处为初期库存从事件发布者处传参过来
        //为什么不直接避免问题，1是参数太多了不好传，2是考虑到如果是库存不够在时间发布者处扣减库存时就已经做了判断，这里只是展示作用，所以不会引发较严重的事故，3是考虑到toB项目高并发情况较少
        StoYtStore stoYtStore = stoYtStoreManager.selectOrCreateStockBySpecificationId(specificationId);
        stoYtStoreRecord.setStoreId(stoYtStore.getId());
        stoYtStoreRecord.setSpecificationId(specificationId);
        stoYtStoreRecord.setRealStoreInit(stoYtStore.getRealStore());
        stoYtStoreRecord.setRealStoreChange(0);
        stoYtStoreRecord.setEnableStoreInit(stoYtStore.getEnableStore());
        stoYtStoreRecord.setEnableStoreChange(0);
        stoYtStoreRecord.setOccupyStoreInit(stoYtStore.getOccupyStore());
        stoYtStoreRecord.setOccupyTransitChange(0);
        stoYtStoreRecord.setRealTransitInit(stoYtStore.getRealTransit());
        stoYtStoreRecord.setRealTransitChange(0);
        stoYtStoreRecord.setEnableTransitInit(stoYtStore.getEnableTransit());
        stoYtStoreRecord.setEnableTransitChange(0);
        stoYtStoreRecord.setOccupyTransitInit(stoYtStore.getOccupyTransit());
        stoYtStoreRecord.setOccupyTransitChange(0);
        return stoYtStoreRecord;
    }

    private StoYtStoreRecord createCustomerRecord(Long customerStoreId){
        SalYtCustomerStore salYtCustomerStore = salYtCustomerStoreMapper.selectById(customerStoreId);
        StoYtStoreRecord stoYtStoreRecord = new StoYtStoreRecord();
        stoYtStoreRecord.setCustomerStoreId(salYtCustomerStore.getId());
        stoYtStoreRecord.setSpecificationId(salYtCustomerStore.getSpecificationId());
        stoYtStoreRecord.setRealStoreInit(salYtCustomerStore.getStoreNumber());
        stoYtStoreRecord.setRealStoreChange(0);
        stoYtStoreRecord.setEnableStoreInit(salYtCustomerStore.getStoreNumber());
        stoYtStoreRecord.setEnableStoreChange(0);
        stoYtStoreRecord.setOccupyStoreInit(0);
        stoYtStoreRecord.setOccupyTransitChange(0);
        stoYtStoreRecord.setRealTransitInit(salYtCustomerStore.getTransitNumber());
        stoYtStoreRecord.setRealTransitChange(0);
        stoYtStoreRecord.setEnableTransitInit(salYtCustomerStore.getTransitNumber());
        stoYtStoreRecord.setEnableTransitChange(0);
        stoYtStoreRecord.setOccupyTransitInit(0);
        stoYtStoreRecord.setOccupyTransitChange(0);
        return stoYtStoreRecord;
    }

    private StoYtStoreRecord createStoreRecord(Long specificationId,StoYtStore stoYtStore){
        StoYtStoreRecord stoYtStoreRecord = new StoYtStoreRecord();
        stoYtStoreRecord.setStoreId(stoYtStore.getId());
        stoYtStoreRecord.setSpecificationId(specificationId);
        if(specificationId==null){
            stoYtStoreRecord.setSpecificationId(stoYtStore.getSpecificationId());
        }
        stoYtStoreRecord.setRealStoreInit(stoYtStore.getRealStore());
        stoYtStoreRecord.setRealStoreChange(0);
        stoYtStoreRecord.setEnableStoreInit(stoYtStore.getEnableStore());
        stoYtStoreRecord.setEnableStoreChange(0);
        stoYtStoreRecord.setOccupyStoreInit(stoYtStore.getOccupyStore());
        stoYtStoreRecord.setOccupyTransitChange(0);
        stoYtStoreRecord.setRealTransitInit(stoYtStore.getRealTransit());
        stoYtStoreRecord.setRealTransitChange(0);
        stoYtStoreRecord.setEnableTransitInit(stoYtStore.getEnableTransit());
        stoYtStoreRecord.setEnableTransitChange(0);
        stoYtStoreRecord.setOccupyTransitInit(stoYtStore.getOccupyTransit());
        stoYtStoreRecord.setOccupyTransitChange(0);
        return stoYtStoreRecord;
    }
}
