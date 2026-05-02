/*
 * @author java_deng
 * @date 2025/11/24 19:55
 * @description
 */
package com.qiaomoyun.manager.pur.yt;

import com.github.pagehelper.PageHelper;
import com.qiaomoyun.Exception.BizException;
import com.qiaomoyun.Exception.ExceptionCodeEnum;
import com.qiaomoyun.entity.pro.yt.*;
import com.qiaomoyun.entity.pur.yt.PurYtApplyPurchase;
import com.qiaomoyun.entity.pur.yt.PurYtPurchase;
import com.qiaomoyun.entity.pur.yt.PurYtPurchaseItem;
import com.qiaomoyun.entity.sal.yt.*;
import com.qiaomoyun.eunm.yt.OrderSubItemStatusEnum;
import com.qiaomoyun.eunm.yt.PurchaseStatusEnum;
import com.qiaomoyun.entity.sto.yt.StoYtStore;
import com.qiaomoyun.eunm.yt.OrderStatusEnum;
import com.qiaomoyun.eunm.yt.StoreEnterOutTypeEnum;
import com.qiaomoyun.event.yt.DeliveryEvent;
import com.qiaomoyun.event.yt.StoreChangeEvent;
import com.qiaomoyun.info.PageResultInfo;
import com.qiaomoyun.manager.pro.yt.ProYtProductLabelManager;
import com.qiaomoyun.manager.pro.yt.ProYtProductManager;
import com.qiaomoyun.manager.sal.yt.SalYtCustomerStoreManager;
import com.qiaomoyun.manager.sal.yt.SalYtOrderSubItemOperationManager;
import com.qiaomoyun.manager.sto.yt.StoYtStoreManager;
import com.qiaomoyun.mapper.pro.yt.ProYtCategorySpecificationItemMapper;
import com.qiaomoyun.mapper.pro.yt.ProYtProductLabelMapper;
import com.qiaomoyun.mapper.pro.yt.ProYtProductSpecificationSupplierMapper;
import com.qiaomoyun.mapper.pur.yt.PurYtApplyPurchaseMapper;
import com.qiaomoyun.mapper.pur.yt.PurYtPurchaseMapper;
import com.qiaomoyun.mapper.pur.yt.PurYtPurchaseItemMapper;
import com.qiaomoyun.mapper.sal.yt.SalYtCustomerMapper;
import com.qiaomoyun.mapper.sal.yt.SalYtCustomerStoreMapper;
import com.qiaomoyun.mapper.sal.yt.SalYtOrderMapper;
import com.qiaomoyun.mapper.sal.yt.SalYtOrderSubItemMapper;
import com.qiaomoyun.mapper.sto.yt.StoYtDeliveryMapper;
import com.qiaomoyun.param.sal.yt.PurYtApplyPurchaseQueryParams;
import com.qiaomoyun.param.sto.yt.StoYtDeliveryQueryParams;
import com.qiaomoyun.util.EntityCodeGenerateUtil;
import com.qiaomoyun.vo.pur.yt.PurYtApplyPurchaseListVo;
import com.qiaomoyun.vo.sto.yt.StoYtDeliveryVo;
import com.qiaomoyun.entity.sto.yt.StoYtDelivery;
import com.qiaomoyun.eunm.yt.DeliveryOrderStatusEnum;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class PurYtApplyPurchaseManager {
    @Resource
    private PurYtApplyPurchaseMapper purYtApplyPurchaseMapper;
    @Autowired
    private SalYtCustomerStoreMapper salYtCustomerStoreMapper;
    @Autowired
    private SalYtCustomerStoreManager salYtCustomerStoreManager;
    @Autowired
    private SalYtOrderSubItemMapper salYtOrderSubItemMapper;
    @Autowired
    private StoYtStoreManager stoYtStoreManager;
    @Autowired
    private SalYtOrderMapper salYtOrderMapper;
    @Autowired
    private SalYtOrderSubItemOperationManager salYtOrderSubItemOperationManager;
    @Autowired
    private ProYtProductSpecificationSupplierMapper proYtProductSpecificationSupplierMapper;
    @Autowired
    private PurYtPurchaseMapper purYtPurchaseMapper;
    @Autowired
    private SalYtCustomerMapper salYtCustomerMapper;
    @Autowired
    private ProYtCategorySpecificationItemMapper proYtCategorySpecificationItemMapper;
    @Autowired
    private ProYtProductManager proYtProductManager;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private ProYtProductLabelMapper proYtProductLabelMapper;
    @Autowired
    private StoYtDeliveryMapper stoYtDeliveryMapper;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;


    @Transactional
    public void saveOrUpdate(List<PurYtApplyPurchase> paramsList) {
        List<PurYtApplyPurchase> resultList=new ArrayList<>();
        // 遍历paramsList
        // 校验categoryLabelId字段是否为空，不为空则具有定制化属性，说明需要直接全量生成申购单
        // 如果不具有定制化属性，则判断是否有规格id
        // 如果没有规格id,说明是半成品，直接全量生成申购单
        // 如果有规格id,则根据规格id和客户id到sal_yt_customer_store中查询是否是独立仓
        // 如果是，则查询独立仓数量store_number
        // 如果独立仓数量-订单数量大于零，就直接扣除独立仓库存，并设置已入库数量，
        // 如果独立仓数量-订单数量小于零，则扣除独立仓库存到负数，设置已入库数量为扣减前的独立仓数量
        // 如果独立仓数量-订单数量小于零，则扣除独立仓库存到负数，设置已入库数量为扣减前的独立仓数量
        // 如果不是该客户独立仓产品，则根据规格id查询出库存表中的可用在途是否大于占用在途，库存表中的可用库存是否大于占用库存
        // 如果两者有一样不大于则抛出异常，
        // 未抛出异常则减少可用库存，增加占用库存，
        // 然后赋值该子订单item的占用库存和已入库库存，
        // 在途库存也是同理，但是赋值的是该子订单item的占用在途，并且不增加已入库数量,等采购该规格的采购单入库再增加入库数量
        // 然后保存申购单

        //订单id汇总
        HashSet<Long> orderIdSet=new HashSet<>();

        HashSet<Long> itemIdSet=new HashSet<>();

        List<SalYtOrderSubItem> subItemList=new ArrayList<>();
        //遍历paramsList
        for(PurYtApplyPurchase purYtApplyPurchase:paramsList){
            Long orderId = purYtApplyPurchase.getOrderId();
            if(!orderIdSet.contains(orderId)){
                SalYtOrder salYtOrder = salYtOrderMapper.selectById(orderId);
                if(salYtOrder.getStatus().equals(OrderStatusEnum.Passed.getKey())){
                    throw new BizException("该订单已经申购过！");
                }
            }
            orderIdSet.add(orderId);
            itemIdSet.add(purYtApplyPurchase.getOrderSubItemId());
            Integer orderNumber = purYtApplyPurchase.getOrderNumber();
            Long categoryLabelId = purYtApplyPurchase.getCategoryLabelId();
            Integer number = purYtApplyPurchase.getNumber();
            //校验categoryLabelId字段是否为空，不为空则具有定制化属性，说明需要直接全量生成申购单
            Long orderSubItemId = purYtApplyPurchase.getOrderSubItemId();
            SalYtOrderSubItem salYtOrderSubItem = salYtOrderSubItemMapper.selectById(orderSubItemId);
            salYtOrderSubItem.setIsApply(true);
            subItemList.add(salYtOrderSubItem);
            if(categoryLabelId!=null){
                purYtApplyPurchase.setNumber(orderNumber);
                purYtApplyPurchase.setOrderSubItemId(salYtOrderSubItem.getId());
                purYtApplyPurchase.setOrderRemark(salYtOrderSubItem.getRemark());
                resultList.add(purYtApplyPurchase);

                salYtOrderSubItem.setOccupyStoreNumber(0);
                salYtOrderSubItem.setEnterNumber(0);
                salYtOrderSubItem.setOccupyTransitNumber(0);
                salYtOrderSubItem.setOccupyTransitEnterNumber(0);
                salYtOrderSubItem.setDeliveryNumber(0);
                salYtOrderSubItem.setApplyPurchaseNumber(orderNumber);
                salYtOrderSubItemMapper.updateById(salYtOrderSubItem);
            }else {
                //如果不具有定制化属性，则判断是否有规格id
                Long productSpecificationId = purYtApplyPurchase.getProductSpecificationId();
                //如果没有规格id,说明是半成品，直接全量生成申购单
                if(productSpecificationId==null){
                    purYtApplyPurchase.setNumber(orderNumber);
                    purYtApplyPurchase.setOrderSubItemId(salYtOrderSubItem.getId());
                    purYtApplyPurchase.setOrderRemark(salYtOrderSubItem.getRemark());
                    resultList.add(purYtApplyPurchase);

                    salYtOrderSubItem.setOccupyStoreNumber(0);
                    salYtOrderSubItem.setEnterNumber(0);
                    salYtOrderSubItem.setOccupyTransitNumber(0);
                    salYtOrderSubItem.setOccupyTransitEnterNumber(0);
                    salYtOrderSubItem.setDeliveryNumber(0);
                    salYtOrderSubItem.setApplyPurchaseNumber(orderNumber);
                    salYtOrderSubItemMapper.updateById(salYtOrderSubItem);
                }else {
                    //如果有规格id,则根据规格id和客户id到sal_yt_customer_store中查询是否是独立仓
                    Long customerId = purYtApplyPurchase.getCustomerId();
                    if(customerId==null){
                        throw new BizException(ExceptionCodeEnum.Not_Exists.getCode(),"客户id不能为空");
                    }
                    SalYtCustomerStore salYtCustomerStore = salYtCustomerStoreMapper.selectByCustomerIdAndSpecificationId(customerId, productSpecificationId);
                    //如果是，则查询独立仓数量store_number
                    if(salYtCustomerStore!=null){
                        Integer storeNumber = salYtCustomerStore.getStoreNumber();
                        //如果独立仓数量-订单数量大于零，就直接扣除独立仓库存，并设置已入库数量，
                        //如果独立仓数量-订单数量小于零，则扣除独立仓库存到负数，设置已入库数量为扣减前的独立仓数量
                        salYtOrderSubItem.setIsApply(false);
                        salYtOrderSubItem.setCustomerStoreId(salYtCustomerStore.getId());
                        if(storeNumber - orderNumber>=0){
                            //扣除独立仓库存
                            Long id = salYtCustomerStore.getId();
                            salYtCustomerStoreManager.reduceStock(id,orderNumber);
                            //设置已入库数量

                            salYtOrderSubItem.setOccupyStoreNumber(orderNumber);
                            salYtOrderSubItem.setEnterNumber(orderNumber);
                            salYtOrderSubItem.setStatus(OrderSubItemStatusEnum.WaitPackage.getKey());
                            salYtOrderSubItem.setOccupyTransitNumber(0);
                            salYtOrderSubItem.setOccupyTransitEnterNumber(0);
                            salYtOrderSubItem.setDeliveryNumber(0);
                            salYtOrderSubItem.setApplyPurchaseNumber(0);
                            salYtOrderSubItemMapper.updateById(salYtOrderSubItem);
                        }else {
                            //如果独立仓数量-订单数量小于零，则扣除独立仓库存到负数，设置已入库数量为扣减前的独立仓数量
                            //扣除独立仓库存
                            Long id = salYtCustomerStore.getId();
                            salYtCustomerStoreManager.reduceStock(id,orderNumber);
                            //设置占据库存数量是下单数量
                            salYtOrderSubItem.setOccupyStoreNumber(orderNumber);
                            salYtOrderSubItem.setStatus(OrderSubItemStatusEnum.WaitEnterStore.getKey());
                            if(storeNumber<0){
                                //如果此时独立仓库存已经变成了空，那么设置这条订单item入库数量是0
                                storeNumber=0;
                            }
                            salYtOrderSubItem.setEnterNumber(storeNumber);
                            salYtOrderSubItem.setOccupyTransitNumber(0);
                            salYtOrderSubItem.setOccupyTransitEnterNumber(0);
                            salYtOrderSubItem.setDeliveryNumber(0);
                            salYtOrderSubItem.setApplyPurchaseNumber(0);
                            salYtOrderSubItemMapper.updateById(salYtOrderSubItem);
                        }
                    }else {
                        //如果不是该客户独立仓产品，则根据规格id查询出库存表中的可用在途是否大于占用在途，库存表中的可用库存是否大于占用库存
                        Integer occupyStore = purYtApplyPurchase.getOccupyStore();
                        Integer occupyTransit = purYtApplyPurchase.getOccupyTransit();
                        if(occupyTransit==null || occupyStore==null){
                            throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(),"请填写占用在途或占用库存");
                        }
                        // 如果两者有一样不大于则抛出异常，未抛出异常则减少可用库存，增加占用库存，然后赋值该子订单item的占用库存和已入库库存，在途库存也是同理，但是赋值的是该子订单item的占用在途，并且不增加已入库数量
                        //如果两者有一样不大于则抛出异常
                        if(occupyTransit>0 || occupyStore>0){
                            StoYtStore stoYtStore = stoYtStoreManager.selectStockBySpecificationId(productSpecificationId);
                            if(occupyTransit>stoYtStore.getEnableTransit() || occupyStore>stoYtStore.getEnableStore()){
                                throw new BizException(ExceptionCodeEnum.Stock_Lack_Error);
                            }

                            //减少可用库存，增加占用库存
                            stoYtStoreManager.addOccupyStoreStock(productSpecificationId,occupyStore);
                            //减少在途可用库存，增加占用在途
                            stoYtStoreManager.addOccupyTransitStock(productSpecificationId,occupyTransit);
                            if(occupyStore+occupyTransit==salYtOrderSubItem.getNumber()){
                                salYtOrderSubItem.setStatus(OrderSubItemStatusEnum.WaitEnterStore.getKey());
                            }
                            if(occupyStore.equals(salYtOrderSubItem.getNumber())){
                                salYtOrderSubItem.setStatus(OrderSubItemStatusEnum.WaitPackage.getKey());
                            }
                        }

                        //增加子订单item的占用库存数和已入库库存
                        salYtOrderSubItem.setOccupyStoreNumber(occupyStore);
                        salYtOrderSubItem.setEnterNumber(occupyStore);
                        salYtOrderSubItem.setOccupyTransitNumber(occupyTransit);
                        salYtOrderSubItem.setOccupyTransitEnterNumber(0);
                        salYtOrderSubItem.setDeliveryNumber(0);
                        salYtOrderSubItem.setApplyPurchaseNumber(number);
                        salYtOrderSubItemMapper.updateById(salYtOrderSubItem);
//                        if(occupyStore.equals(salYtOrderSubItem.getNumber())){
//                            //占用库存已达到数量，判断是否生成发货单
//                            eventPublisher.publishEvent(new DeliveryEvent(this, salYtOrderSubItem.getId()));
//                        }
                        if(number!=null && number>0){
                            purYtApplyPurchase.setNumber(number);
                            purYtApplyPurchase.setOrderSubItemId(salYtOrderSubItem.getId());
                            purYtApplyPurchase.setOrderRemark(salYtOrderSubItem.getRemark());
                            resultList.add(purYtApplyPurchase);
                        }

                    }
                }
            }
        }
        // 保存申购单
        resultList.forEach(item->{
            purYtApplyPurchaseMapper.insert(item);
        });

        // 修改子订单状态为已申购
        orderIdSet.forEach(item->{
            SalYtOrder salYtOrder = new SalYtOrder();
            salYtOrder.setId(item);
            salYtOrder.setStatus(OrderStatusEnum.Passed.getKey());
            //设置订单提交时间
            salYtOrder.setSubmitOrderTime(LocalDateTime.now());
            salYtOrderMapper.updateById(salYtOrder);
            //将子订单item状态改为待采购  OrderSubItemStatusEnum.WaitPurchase.getKey()
            //根据订单id查询出所有子订单item
            List<SalYtOrderSubItem> subItemList1 =salYtOrderSubItemMapper.selectSalYtOrderSubItemListByOrderId(item);
            //将状态改为待采购
            subItemList1.forEach(item1->{
                if(OrderSubItemStatusEnum.Draft.getKey().equals(item1.getStatus())){
                    item1.setStatus(OrderSubItemStatusEnum.WaitPurchase.getKey());
                    salYtOrderSubItemMapper.updateById(item1);
                }
            });
        });

        //添加产品记录
        salYtOrderSubItemOperationManager.placeOrderByItemIds(itemIdSet);

        //发布出入库记录事件
        for (SalYtOrderSubItem item:subItemList){
            if(item.getOccupyTransitNumber()==null || item.getOccupyStoreNumber()==null){
                continue;
            }
            if(item.getOccupyTransitNumber().equals(0) && item.getOccupyStoreNumber().equals(0)){
                continue;
            }
            if(!item.getIsApply()){
                //独立仓创建订单申购生成出入库记录
                eventPublisher.publishEvent(new StoreChangeEvent(this, StoreEnterOutTypeEnum.createOrder.getKey(), item.getOccupyStoreNumber(),item.getOccupyTransitNumber(), item.getSpecificationId(), item.getOrderSubId(),item.getCustomerStoreId()));
                continue;
            }
            //发布出入库记录事件
            eventPublisher.publishEvent(new StoreChangeEvent(this, StoreEnterOutTypeEnum.createOrder.getKey(), item.getOccupyStoreNumber(),item.getOccupyTransitNumber(), item.getSpecificationId(), item.getOrderSubId()));
        }

        // 为每个订单生成发货单
        for (Long orderId : orderIdSet) {
            applicationEventPublisher.publishEvent(new DeliveryEvent(this, orderId));
        }

        return;
    }

    public Object list(PurYtApplyPurchaseQueryParams params) {
        PageHelper.startPage(params.getPageNum(),params.getPageSize());
        //已根据supplier_id分组查询供应商信息
        List<PurYtApplyPurchaseListVo> list = purYtApplyPurchaseMapper.list(params);

        list.forEach(item->{
            //根据supplierId查询这个供应商下的采购单项
            Long supplierId = item.getSupplierId();
            params.setSupplierId(supplierId);
            params.setIsInboundDelivery(item.getIsInboundDelivery());
            List<PurYtApplyPurchase> applyPurchaseList = purYtApplyPurchaseMapper.selectBySupplierIdAndIsInboundDelivery(params);

            BigDecimal totalAmount=BigDecimal.ZERO;
            for(PurYtApplyPurchase applyPurchase:applyPurchaseList){
                //存入父订单的订单编号
                String orderCode=purYtApplyPurchaseMapper.getOrderCodeByOrderSubId(applyPurchase.getOrderSubId());
                applyPurchase.setOrderCode(orderCode);
                //存入父订单的订单备注
                String orderNote=purYtApplyPurchaseMapper.getOrderNoteByOrderSubId(applyPurchase.getOrderSubId());
                applyPurchase.setOrderNote(orderNote);
                //设置定制化属性
                if(applyPurchase.getCategoryLabelId()!=null){
                    ProYtProductLabel proYtProductLabel = proYtProductLabelMapper.selectById(applyPurchase.getCategoryLabelId());
                    applyPurchase.setCategoryLabelName(proYtProductLabel.getValue());
                }
                //计算采购总额
                Long productSpecificationId = applyPurchase.getProductSpecificationId();
                if(productSpecificationId==null||productSpecificationId==0){
                    continue;
                }
                ProYtProductSpecificationSupplier specificationSupplier = proYtProductSpecificationSupplierMapper.selectBySpecificationIdAndSupplier(productSpecificationId, supplierId);
                if(specificationSupplier!=null){
                    Integer number = applyPurchase.getNumber();
                    BigDecimal supplierPrice = specificationSupplier.getSupplierPrice();
                    totalAmount=totalAmount.add(supplierPrice.multiply(BigDecimal.valueOf(number)));
                    item.setPurchaseTotalAmount(totalAmount);
                    //设置供应商价格
                    applyPurchase.setSupplierPrice(supplierPrice);
                }
                //规格项
                List<ProYtProductSpecificationItem> itemsListBySpecification = proYtProductManager.getItemsListBySpecification(productSpecificationId);
                applyPurchase.setItemList(itemsListBySpecification);
                //规格图片
                List<ProYtProductFile> fileListBySpecification = proYtProductManager.getFileListBySpecification(productSpecificationId);
                applyPurchase.setImageList(fileListBySpecification);
            }
            item.setApplyPurchaseList(applyPurchaseList);
        });
        return new PageResultInfo<>(list);
    }

    public void replaceSupplier(PurYtApplyPurchaseQueryParams paramsList) {
        List<Long> applyPurchaseIdList = paramsList.getApplyPurchaseIdList();
        Long supplierId = paramsList.getSupplierId();
        applyPurchaseIdList.forEach(applyPurchaseId->{
            PurYtApplyPurchase old = purYtApplyPurchaseMapper.selectById(applyPurchaseId);
            Long productSpecificationId = old.getProductSpecificationId();
            ProYtProductSpecificationSupplier specificationSupplier = proYtProductSpecificationSupplierMapper.selectBySpecificationIdAndSupplier(productSpecificationId, supplierId);
            if(specificationSupplier==null){
                throw new BizException(ExceptionCodeEnum.Not_Exists.getCode(),"该供应商没有该规格");
            }
            PurYtApplyPurchase purYtApplyPurchase = new PurYtApplyPurchase();
            purYtApplyPurchase.setSupplierId(supplierId);
            purYtApplyPurchase.setId(applyPurchaseId);
            purYtApplyPurchaseMapper.updateById(purYtApplyPurchase);
        });
    }

    /**
     * 根据规格ID列表获取可更换的供应商
     * @param specificationIds 规格ID列表
     * @return 可更换的供应商列表
     */
    public List<ProYtProductSpecificationSupplier> listReplaceableSuppliers(List<Long> specificationIds) {
        List<ProYtProductSpecificationSupplier> supplierList = proYtProductSpecificationSupplierMapper.listReplaceableSuppliers(specificationIds);
        return supplierList;
    }

    @Autowired
    private PurYtPurchaseItemMapper purYtPurchaseItemMapper;

    @Transactional
    public void addPurchase(PurYtApplyPurchaseQueryParams paramsList) {
        Long supplierId = paramsList.getSupplierId();
        Long purchaseId = paramsList.getPurchaseId();
        Boolean isInboundDelivery = paramsList.getIsInboundDelivery();
        List<Long> applyPurchaseIdList = paramsList.getApplyPurchaseIdList();

        PurYtPurchase purYtPurchase = purYtPurchaseMapper.selectById(purchaseId);
        if(purYtPurchase==null){
            throw new BizException(ExceptionCodeEnum.Not_Exists);
        }
        if(!isInboundDelivery.equals(purYtPurchase.getIsInboundDelivery())|| !supplierId.equals(purYtPurchase.getSupplierId())){
            throw new BizException(ExceptionCodeEnum.Not_Exists.getCode(),"追加的采购单和该申购单不是同一个供应商和发货方式");
        }

        // 根据applyPurchaseIdList查询出申购单
        List<PurYtApplyPurchase> applyPurchaseList = purYtApplyPurchaseMapper.selectBatchIds(applyPurchaseIdList);

        if (applyPurchaseList == null || applyPurchaseList.isEmpty()) {
            throw new BizException(ExceptionCodeEnum.Not_Exists);
        }

        // 为每个申购单创建采购单item
        List<PurYtPurchaseItem> purchaseItemList = new ArrayList<>();
        for (PurYtApplyPurchase applyPurchase : applyPurchaseList) {
            PurYtPurchaseItem purchaseItem = new PurYtPurchaseItem();

            // 填充字段
            purchaseItem.setPurchaseId(purchaseId);
            purchaseItem.setApplyPurchaseId(applyPurchase.getId());
            purchaseItem.setProductId(applyPurchase.getProductId());
            Long productSpecificationId = applyPurchase.getProductSpecificationId();
            if(productSpecificationId!=null){
                purchaseItem.setSpecificationId(productSpecificationId);
                ProYtProductSpecificationSupplier specificationSupplier = proYtProductSpecificationSupplierMapper.selectBySpecificationIdAndSupplier(productSpecificationId, supplierId);
                purchaseItem.setSupplierPrice(specificationSupplier.getSupplierPrice()); // 默认值，可能需要从其他地方获取
            }
            else {
                //todo 无需判断是否有已确认的成品要加入采购单，因为追加的只能是暂存的采购单，暂存的采购单在生成的时候自然会判断是否有半成品
//                Long orderSubItemId = applyPurchase.getOrderSubItemId();
//                List<SalYtOrderSubItem> salYtOrderSubItems = salYtOrderSubItemMapper.selectByConfirmItemId(orderSubItemId);
//                if(salYtOrderSubItems!=null){
//                    salYtOrderSubItems.forEach(salYtOrderSubItem -> {
//                        purYtPurchaseManager.confirmProduct(salYtOrderSubItem,purchaseItem);
//                    });
//                }
            }
            purchaseItem.setNumber(applyPurchase.getNumber());
            purchaseItem.setEnterNumber(0);
            purchaseItem.setStatus(PurchaseStatusEnum.temporary.getKey()); // 设置为暂存状态
            purchaseItem.setSalesEmployeeId(applyPurchase.getSalesEmployeeId());
            purchaseItem.setOrderSubId(applyPurchase.getOrderSubId());
            purchaseItem.setOrderSubItemId(applyPurchase.getOrderSubItemId());
            //客户姓名
            Long customerId = applyPurchase.getCustomerId();
            if(customerId!=null){
                SalYtCustomer customer = salYtCustomerMapper.selectById(customerId);
                purchaseItem.setCustomerId(customerId);
                purchaseItem.setCustomerName(customer.getName());
            }

            //定制属性
            Long categoryLabelId = applyPurchase.getCategoryLabelId();
            if(categoryLabelId!=null){
               // ProYtCategorySpecificationItem proYtCategorySpecificationItem = proYtCategorySpecificationItemMapper.selectById(categoryLabelId);
                //
                ProYtProductLabel proYtProductLabel = proYtProductLabelMapper.selectById(categoryLabelId);
                purchaseItem.setCategorySpecificationItemId(categoryLabelId);
                //purchaseItem.setCategorySpecificationItemName(proYtCategorySpecificationItem.getValue());
                purchaseItem.setCategorySpecificationItemName(proYtProductLabel.getValue());
            }
            purchaseItemList.add(purchaseItem);

            applyPurchase.setIsDeleted(1);
            purYtApplyPurchaseMapper.updateById(applyPurchase);
        }

        // 批量插入采购单item
        if (!purchaseItemList.isEmpty()) {
            purYtPurchaseItemMapper.saveBatch(purchaseItemList);
        }
    }

    public Object saveDetail(PurYtApplyPurchaseQueryParams paramsList) {
        List<Long> applyPurchaseIdList = paramsList.getApplyPurchaseIdList();
        // 使用自定义SQL查询，获取productCode,客户名称，供应商规格、供应商单价、供应商起订量
        List<PurYtApplyPurchase> applyPurchaseList = purYtApplyPurchaseMapper.selectByIdsWithDetails(applyPurchaseIdList);
        applyPurchaseList.forEach(item->{
            Long productSpecificationId = item.getProductSpecificationId();
            if(productSpecificationId!=null){
                List<ProYtProductSpecificationItem> items = proYtProductManager.getItemsListBySpecification(productSpecificationId);
                List<ProYtProductFile> fileList = proYtProductManager.getFileListBySpecification(productSpecificationId);
                item.setItemList(items);
                item.setImageList(fileList);
            }
            //填充订单备注
            String orderNote = purYtApplyPurchaseMapper.getOrderNoteByOrderSubId(item.getOrderSubId());
            item.setOrderNote(orderNote);
            //计算自动产品层级
            Long specId = item.getProductSpecificationId();
            if(specId != null){
                item.setHandProductLevel(proYtProductManager.getAutoProductLevel(specId));
            }
        });
        return applyPurchaseList;
    }

    /**
     * 退回申购记录（逻辑删除，仅允许无订单绑定的申购记录）
     */
    public void withdraw(Long id) {
        PurYtApplyPurchase applyPurchase = purYtApplyPurchaseMapper.selectById(id);
        if (applyPurchase == null) {
            throw new BizException(ExceptionCodeEnum.Not_Exists);
        }
        if (applyPurchase.getOrderSubId() != null) {
            throw new BizException(512, "该申购记录关联了订单，不允许退回");
        }
        purYtApplyPurchaseMapper.deleteById(id);
    }
}
