/*
 * @author java_deng
 * @date 2024/11/21 16:30
 * @description 库存管理类
 */
package com.qiaomoyun.manager.sto.yt;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.qiaomoyun.Exception.BizException;
import com.qiaomoyun.Exception.ExceptionCodeEnum;
import com.qiaomoyun.entity.pro.yt.ProYtProduct;
import com.qiaomoyun.entity.pro.yt.ProYtProductFile;
import com.qiaomoyun.entity.pro.yt.ProYtProductSpecification;
import com.qiaomoyun.entity.pro.yt.ProYtProductSpecificationItem;
import com.qiaomoyun.entity.pur.yt.PurYtStoreWarning;
import com.qiaomoyun.entity.sal.yt.SalYtCustomer;
import com.qiaomoyun.entity.sal.yt.SalYtCustomerStore;
import com.qiaomoyun.entity.sal.yt.SalYtOrder;
import com.qiaomoyun.entity.sto.yt.*;
import com.qiaomoyun.eunm.sys.DictionaryConfigEnum;
import com.qiaomoyun.eunm.yt.OrderSubItemStatusEnum;
import com.qiaomoyun.eunm.yt.ProductFilesTypeEnum;
import com.qiaomoyun.eunm.yt.StoreEnterOutTypeEnum;
import com.qiaomoyun.event.yt.StoreChangeEvent;
import com.qiaomoyun.info.PageResultInfo;
import com.qiaomoyun.manager.pro.yt.ProYtProductManager;
import com.qiaomoyun.manager.sal.yt.SalYtOrderSubItemOperationManager;
import com.qiaomoyun.mapper.pro.yt.ProYtProductFileMapper;
import com.qiaomoyun.mapper.pro.yt.ProYtProductMapper;
import com.qiaomoyun.mapper.pro.yt.ProYtProductSpecificationItemMapper;
import com.qiaomoyun.mapper.pro.yt.ProYtProductSpecificationMapper;
import com.qiaomoyun.mapper.pur.yt.PurYtStoreWarningMapper;
import com.qiaomoyun.mapper.sal.yt.SalYtCustomerMapper;
import com.qiaomoyun.mapper.sal.yt.SalYtCustomerStoreMapper;
import com.qiaomoyun.mapper.sal.yt.SalYtOrderMapper;
import com.qiaomoyun.mapper.sto.yt.*;
import com.qiaomoyun.mapper.sys.SysDictionaryMapper;
import com.qiaomoyun.entity.sys.SysDictionary;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.qiaomoyun.entity.sal.yt.SalYtOrderSubItem;
import com.qiaomoyun.mapper.sal.yt.SalYtOrderSubItemMapper;
import com.qiaomoyun.param.sto.yt.StoYtStoreRecordQueryParams;
import com.qiaomoyun.param.sto.yt.StoYtStoreOrderAddParams;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 库存管理类
 */
@Service
public class StoYtStoreManager {

    @Autowired
    private SalYtOrderSubItemMapper salYtOrderSubItemMapper;
    @Autowired
    private StoYtStoreMapper stoYtStoreMapper;
    @Autowired
    private ProYtProductMapper proYtProductMapper;
    @Autowired
    private ProYtProductFileMapper proYtProductFileMapper;
    @Autowired
    private ProYtProductSpecificationItemMapper proYtProductSpecificationItemMapper;
    @Autowired
    private ProYtProductSpecificationMapper proYtProductSpecificationMapper;
    @Autowired
    private StoYtStoreRecordMapper stoYtStoreRecordMapper;
    @Autowired
    private SalYtCustomerStoreMapper salYtCustomerStoreMapper;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private SysDictionaryMapper sysDictionaryMapper;
    @Autowired
    private ProYtProductManager proYtProductManager;
    @Autowired
    private StoYtDeliveryItemMapper stoYtDeliveryItemMapper;
    @Autowired
    private PurYtStoreWarningMapper purYtStoreWarningMapper;
    @Autowired
    private SalYtCustomerMapper salYtCustomerMapper;
    @Autowired
    private SalYtOrderMapper salYtOrderMapper;
    @Autowired
    private SalYtOrderSubItemOperationManager salYtOrderSubItemOperationManager;
    @Autowired
    private StoYtDeliveryMapper stoYtDeliveryMapper;
    @Autowired
    private StoYtStoreOrderMapper stoYtStoreOrderMapper;

    public void reduceOccupyStoreStockToEnableStock(Long specificationId, Integer number) {
        StoYtStore store = stoYtStoreMapper.selectBySpecificationId(specificationId);
        if(store == null) {
            throw new BizException(ExceptionCodeEnum.Not_Exists);
        }
        Integer occupyStore = store.getOccupyStore();
        if(occupyStore<number){
            throw new BizException(ExceptionCodeEnum.Stock_Lack_Error);
        }
        Integer enableStore = store.getEnableStore();
        occupyStore=occupyStore-number;
        store.setOccupyStore(occupyStore);
        enableStore=enableStore+number;
        store.setEnableStore(enableStore);
        stoYtStoreMapper.updateById(store);
    }

    public void reduceOccupyTransitStockToEnableTransitStock(Long specificationId, Integer number) {
        StoYtStore store = stoYtStoreMapper.selectBySpecificationId(specificationId);
        if(store == null) {
            throw new BizException(ExceptionCodeEnum.Not_Exists);
        }
        Integer occupyTransit = store.getOccupyTransit();
        if(occupyTransit < number){
            throw new BizException(ExceptionCodeEnum.Stock_Lack_Error);
        }
        Integer enableTransit = store.getEnableTransit();
        store.setOccupyTransit(occupyTransit - number);
        store.setEnableTransit(enableTransit + number);
        stoYtStoreMapper.updateById(store);
    }

    @Transactional
    public void reduceTransitStockToStoreStock(Long specificationId, Integer enableTransitChange,Integer occupyTransitChange,Integer realTransitChange) {
        StoYtStore stoYtStore = stoYtStoreMapper.selectBySpecificationId(specificationId);

        Integer occupyTransit = stoYtStore.getOccupyTransit();
        Integer enableTransit = stoYtStore.getEnableTransit();
        Integer realTransit = stoYtStore.getRealTransit();
        if(occupyTransit<occupyTransitChange ||  enableTransit<enableTransitChange || realTransit<realTransitChange){
            throw new BizException(ExceptionCodeEnum.Stock_Lack_Error);
        }
        stoYtStore.setOccupyStore(stoYtStore.getOccupyStore()+occupyTransitChange);
        stoYtStore.setEnableStore(stoYtStore.getEnableStore()+enableTransitChange);
        stoYtStore.setRealStore(stoYtStore.getRealStore()+realTransitChange);
        stoYtStore.setOccupyTransit(stoYtStore.getOccupyTransit()-occupyTransitChange);
        stoYtStore.setRealTransit(stoYtStore.getRealTransit()-realTransitChange);
        stoYtStore.setEnableTransit(stoYtStore.getEnableTransit()-enableTransitChange);
        stoYtStoreMapper.updateById(stoYtStore);
    }
    /**
     * 根据规格ID查询库存信息
     * 逻辑：如果查询为空，则库存字段都赋值为0
     * @param specificationId 规格ID
     * @return 库存信息对象
     */
    public StoYtStore selectStockBySpecificationId(Long specificationId) {
        // 查询库存信息
        StoYtStore store = stoYtStoreMapper.selectBySpecificationId(specificationId);

        // 如果查询为空，则创建一个新对象并将所有库存字段赋值为0
        if (store == null) {
            store=getNewStore(specificationId);
        }

        return store;
    }

    @Transactional
    public void addOccupyStoreStock(Long  specificationId, Integer occupyStock) {
        StoYtStore store = stoYtStoreMapper.selectBySpecificationId(specificationId);
        if(store == null) {
            throw new BizException(ExceptionCodeEnum.Not_Exists);
        }
        Integer enableStore = store.getEnableStore();
        if(enableStore<occupyStock){
            throw new BizException(ExceptionCodeEnum.Stock_Lack_Error);
        }
        Integer occupyStore = store.getOccupyStore();
        enableStore=enableStore-occupyStock;
        store.setEnableStore(enableStore);
        occupyStore=occupyStore+occupyStock;
        store.setOccupyStore(occupyStore);
        stoYtStoreMapper.updateById(store);
    }

    @Transactional
    public void addOccupyTransitStock(Long productSpecificationId, Integer occupyTransit) {
        StoYtStore store = stoYtStoreMapper.selectBySpecificationId(productSpecificationId);
        if(store == null) {
            throw new BizException(ExceptionCodeEnum.Not_Exists);
        }
        Integer enableTransitStore = store.getEnableTransit();
        Integer occupyTransitStore = store.getOccupyTransit();
        enableTransitStore=enableTransitStore-occupyTransit;
        store.setEnableTransit(enableTransitStore);
        occupyTransitStore=occupyTransitStore+occupyTransit;
        store.setOccupyTransit(occupyTransitStore);
        stoYtStoreMapper.updateById(store);
    }

    private StoYtStore getNewStore(Long specificationId) {
        StoYtStore store = new StoYtStore();
        store.setSpecificationId(specificationId);
        store.setRealStore(0);
        store.setEnableStore(0);
        store.setOccupyStore(0);
        store.setRealTransit(0);
        store.setEnableTransit(0);
        store.setOccupyTransit(0);
        return store;
    }

    /**
     * 根据规格ID查询库存信息（返回实体类格式）
     * 逻辑：如果查询为空，则库存字段都赋值为0
     * @param specificationId 规格ID
     * @return 库存信息实体类
     */
    public StoYtStore getStockBySpecificationId(Long specificationId) {
        // 查询库存信息
        StoYtStore store = stoYtStoreMapper.selectBySpecificationId(specificationId);

        // 如果查询为空，则创建一个新对象并将所有库存字段赋值为0
        if (store == null) {
            store = new StoYtStore();
            store.setSpecificationId(specificationId);
            store.setRealStore(0);
            store.setEnableStore(0);
            store.setOccupyStore(0);
            store.setRealTransit(0);
            store.setEnableTransit(0);
            store.setOccupyTransit(0);
        } else {
            // 确保所有库存字段都有值，防止null值
            if (store.getRealStore() == null) store.setRealStore(0);
            if (store.getEnableStore() == null) store.setEnableStore(0);
            if (store.getOccupyStore() == null) store.setOccupyStore(0);
            if (store.getRealTransit() == null) store.setRealTransit(0);
            if (store.getEnableTransit() == null) store.setEnableTransit(0);
            if (store.getOccupyTransit() == null) store.setOccupyTransit(0);
        }

        return store;
    }

    /**
     * 保存库存信息
     * @param store 库存信息
     */
    public void save(StoYtStore store) {
        if (store.getId() == null) {
            // 设置默认值
            if (store.getRealStore() == null) store.setRealStore(0);
            if (store.getEnableStore() == null) store.setEnableStore(0);
            if (store.getOccupyStore() == null) store.setOccupyStore(0);
            if (store.getRealTransit() == null) store.setRealTransit(0);
            if (store.getEnableTransit() == null) store.setEnableTransit(0);
            if (store.getOccupyTransit() == null) store.setOccupyTransit(0);

            stoYtStoreMapper.insert(store);
        } else {
            stoYtStoreMapper.updateById(store);
        }
    }

    public StoYtStore selectOrCreateStockBySpecificationId(Long specificationId) {
        StoYtStore stoYtStore = stoYtStoreMapper.selectBySpecificationId(specificationId);
        if(stoYtStore == null){
            stoYtStore = new StoYtStore();
            ProYtProductSpecification proYtProductSpecification = proYtProductSpecificationMapper.selectById(specificationId);
            stoYtStore.setSpecificationId(specificationId);
            stoYtStore.setProductId(proYtProductSpecification.getProductId());
            stoYtStore.setLocationId(proYtProductSpecification.getLocationId());
            save(stoYtStore);
        }
        return  stoYtStore;
    }

    /**
     * 根据产品ID查询库存信息列表
     * @param productId 产品ID
     * @return 库存信息列表
     */
    public Object getStockByProductId(Long productId) {
        List<StoYtStore> result=new ArrayList<>();
        ProYtProduct proYtProduct = proYtProductMapper.selectById(productId);
        List<ProYtProductSpecification> proYtProductSpecifications = proYtProductSpecificationMapper.selectByProductId(productId,null);
        List<StoYtStore> stoYtStores = stoYtStoreMapper.selectByProductId(productId);
        Map<Long, StoYtStore> specificationStoreMap = stoYtStores.stream().collect(Collectors.toMap(StoYtStore::getSpecificationId, s -> s));

        for(ProYtProductSpecification specification : proYtProductSpecifications){
            StoYtStore stoYtStore = specificationStoreMap.get(specification.getId());
            if(stoYtStore==null){
                stoYtStore=getStockBySpecificationId(specification.getId());
            }
            result.add(stoYtStore);
            stoYtStore.setSpecificationId(specification.getId());
            stoYtStore.setDescription(specification.getDescription());
            Long specificationId = stoYtStore.getSpecificationId();
            //填充规格图片
            List<ProYtProductFile> proYtProductFiles = proYtProductFileMapper.selectByMasterIdAndType(specificationId, ProductFilesTypeEnum.specification.getKey());
            stoYtStore.setImageList(proYtProductFiles);

            //填充规格项
            List<ProYtProductSpecificationItem> specificationItems = proYtProductSpecificationItemMapper.selectByProductSpecificationId(specificationId);
            stoYtStore.setItemList(specificationItems);

//            ProYtProductSpecification proYtProductSpecification = proYtProductSpecificationMapper.selectLocationBySpecificationId(specificationId);
            stoYtStore.setLocationId(specification.getLocationId());
            stoYtStore.setLocationName(specification.getLocationName());
        }
        HashMap<String,Object> map = new HashMap<>();
        map.put("stockList",result);
        map.put("productId",productId);
        map.put("code",proYtProduct.getCode());
        map.put("remark",proYtProduct.getRemark());
        return map;
    }

    /**
     * 根据查询参数获取库存历史流向记录
     * @param params 查询参数
     * @return 分页后的库存历史记录列表
     */
    public Object getStoreHistory(StoYtStoreRecordQueryParams params) {
        Long specificationId = params.getSpecificationId();
        StoYtStore stoYtStore = stoYtStoreMapper.selectBySpecificationId(specificationId);
        List<StoYtStoreRecord> list=null;
        if(stoYtStore!=null){
            params.setStoreId(stoYtStore.getId());
            PageHelper.startPage(params.getPageNum(),params.getPageSize());
            list = stoYtStoreRecordMapper.listByPage(params);
        }else {
            list=new ArrayList<>();
        }

        return new PageResultInfo<>(list);
    }

    /**
     * 获取特定类型的出入库记录（独立仓、单独出入库、采购单入库）
     * @param params 查询参数
     * @return 分页后的出入库记录列表
     */
    public Object getStoreEnterOutRecords(StoYtStoreRecordQueryParams params) {
        // 设置需要查询的类型列表
        List<Integer> typeList = Arrays.asList(
            StoreEnterOutTypeEnum.customerSimpleEnterStore.getKey(),
            StoreEnterOutTypeEnum.customerSimpleOutStore.getKey(),
            StoreEnterOutTypeEnum.simpleEnterStore.getKey(),
            StoreEnterOutTypeEnum.simpleOutStore.getKey(),
            StoreEnterOutTypeEnum.purchaseEnterStore.getKey()
        );
        params.setTypeList(typeList);

        // 如果指定了规格ID，则设置仓库ID
        if (params.getSpecificationId() != null) {
            StoYtStore stoYtStore = stoYtStoreMapper.selectBySpecificationId(params.getSpecificationId());
            if (stoYtStore != null) {
                params.setStoreId(stoYtStore.getId());
            }
        }

        PageHelper.startPage(params.getPageNum(), params.getPageSize());
        List<StoYtStoreRecord> stoYtStoreRecords = stoYtStoreRecordMapper.listByPage(params);
        for(StoYtStoreRecord stoYtStoreRecord:stoYtStoreRecords){
            List<ProYtProductFile> fileListBySpecification = proYtProductManager.getFileListBySpecification(stoYtStoreRecord.getSpecificationId());
            stoYtStoreRecord.setImageList(fileListBySpecification);
            List<ProYtProductSpecificationItem> itemsListBySpecification = proYtProductManager.getItemsListBySpecification(stoYtStoreRecord.getSpecificationId());
            stoYtStoreRecord.setItemList(itemsListBySpecification);
            //操作数量
            Integer realStoreChange = stoYtStoreRecord.getRealStoreChange();
            if(realStoreChange!=null&&realStoreChange!=0){
                stoYtStoreRecord.setOperationNumber(realStoreChange);
            }else {
                stoYtStoreRecord.setOperationNumber(stoYtStoreRecord.getRealTransitChange());
            }
            //库位
            Long storeId = stoYtStoreRecord.getStoreId();
            if(storeId!=null){
                ProYtProductSpecification proYtProductSpecification = proYtProductSpecificationMapper.selectLocationBySpecificationId(stoYtStoreRecord.getSpecificationId());
                if(proYtProductSpecification!=null){
                    stoYtStoreRecord.setLocationName(proYtProductSpecification.getLocationName());
                }

            }
            if(stoYtStoreRecord.getCustomerStoreId()!=null){
                //客户独立仓产品库位
                SalYtCustomerStore salYtCustomerStore = salYtCustomerStoreMapper.selectLocationById(stoYtStoreRecord.getCustomerStoreId());
                if(salYtCustomerStore!=null){
                    stoYtStoreRecord.setLocationName(salYtCustomerStore.getLocationName());
                }
            }
        }
        return new PageResultInfo<>(stoYtStoreRecords);
    }

    @Transactional
    public void enter(StoYtStoreRecord stoYtStoreRecord,StoYtStoreOrder stoYtStoreOrder,StoYtStoreOrderAddParams params) {
        Long specificationId = stoYtStoreOrder.getSpecificationId();
        Long customerId = stoYtStoreOrder.getCustomerId();
        SalYtCustomerStore salYtCustomerStore = salYtCustomerStoreMapper.selectByCustomerIdAndSpecificationId(customerId, specificationId);

        if(salYtCustomerStore != null && stoYtStoreRecord==null){
            //未关联采购单、而且是独立仓，说明是独立仓转入或独立仓转出，或者独立仓采购单转入，独立仓采购单入库在订单入库中也将stoYtStoreRecord赋值为null了
            String type = stoYtStoreOrder.getType();
            if(type.equals(StoreEnterOutTypeEnum.customerSimpleEnterStore.getKey().toString())){
                //独立仓转入
                Integer initStoreNumber=salYtCustomerStore.getStoreNumber();
                salYtCustomerStore.setStoreNumber(salYtCustomerStore.getStoreNumber()+params.getEnterNumber());
                salYtCustomerStoreMapper.updateById(salYtCustomerStore);
                if(initStoreNumber<0){
                    //如果入库前独立仓库存为负，则说明有独立仓客户订单需要入库
                    Integer enterNumber = params.getEnterNumber();
                    //查找该客户的订单下子订单的子订单item等于该规格的，并且入库数量小于订单数量的
                    Map<String, Object> queryParams = new HashMap<>();
                    queryParams.put("customerId", customerId);
                    queryParams.put("specificationId", specificationId);
                    List<SalYtOrderSubItem> orderSubItems = salYtOrderSubItemMapper.selectByCustomerAndSpecificationAndEnterNumberLessThanNumber(queryParams);
                    //遍历这些item，分配入库数量，直到遍历结束或者enterNumber分配完
                    for (SalYtOrderSubItem item : orderSubItems) {
                        if (enterNumber <= 0) {
                            break;
                        }
                        //计算该订单子项还需要入库的数量
                        Integer needEnterNumber = item.getNumber() - item.getEnterNumber();
                        //如果需要入库的数量大于可用的入库数量，则只分配可用的入库数量
                        if (needEnterNumber > enterNumber) {
                            needEnterNumber = enterNumber;
                        }
                        //更新订单子项的入库数量
                        item.setEnterNumber(item.getEnterNumber() + needEnterNumber);
                        salYtOrderSubItemMapper.updateById(item);
                        //判断是否要生成发货单
//                        applicationEventPublisher.publishEvent(new DeliveryEvent(this,item.getId()));
                        //更新剩余的入库数量
                        enterNumber -= needEnterNumber;
                    }
                }
                //独立仓转入出入库记录
                applicationEventPublisher.publishEvent(new StoreChangeEvent(
                        this,Integer.parseInt(type),params.getEnterNumber(),specificationId,customerId,stoYtStoreOrder.getRemark())
                );
            }else if(type.equals(StoreEnterOutTypeEnum.customerSimpleOutStore.getKey().toString())){
                //独立仓转出
                salYtCustomerStore.setStoreNumber(salYtCustomerStore.getStoreNumber()-params.getEnterNumber());
                salYtCustomerStoreMapper.updateById(salYtCustomerStore);

                //独立仓转出出入库记录
                applicationEventPublisher.publishEvent(new StoreChangeEvent(
                        this,Integer.parseInt(type),params.getEnterNumber(),specificationId,customerId,stoYtStoreOrder.getRemark())
                );
            }else if(type.equals(StoreEnterOutTypeEnum.purchaseEnterStore.getKey().toString())){
                Integer customerStoreNumber = salYtCustomerStore.getStoreNumber();
                StringBuffer orderCode=new StringBuffer();
                //采购单独立仓入库
                salYtCustomerStore.setStoreNumber(salYtCustomerStore.getStoreNumber()+params.getEnterNumber());
                salYtCustomerStore.setTransitNumber(salYtCustomerStore.getTransitNumber()-params.getEnterNumber());
                salYtCustomerStoreMapper.updateById(salYtCustomerStore);
                if(customerStoreNumber<0){
                    //如果入库前独立仓库存为负，则说明有独立仓客户订单需要入库
                    Integer enterNumber = params.getEnterNumber();
                    //查找该客户的订单下子订单的子订单item等于该规格的，并且入库数量小于订单数量的
                    Map<String, Object> queryParams = new HashMap<>();
                    queryParams.put("customerId", customerId);
                    queryParams.put("specificationId", specificationId);
                    List<SalYtOrderSubItem> orderSubItems = salYtOrderSubItemMapper.selectByCustomerAndSpecificationAndEnterNumberLessThanNumber(queryParams);
                    //遍历这些item，分配入库数量，直到遍历结束或者enterNumber分配完
                    for (SalYtOrderSubItem item : orderSubItems) {
                        if (enterNumber <= 0) {
                            break;
                        }
                        //计算该订单子项还需要入库的数量
                        Integer needEnterNumber = item.getNumber() - item.getEnterNumber();
                        //如果需要入库的数量大于可用的入库数量，则只分配可用的入库数量
                        if (needEnterNumber > enterNumber) {
                            needEnterNumber = enterNumber;
                        }
                        //更新订单子项的入库数量
                        item.setEnterNumber(item.getEnterNumber() + needEnterNumber);
                        salYtOrderSubItemMapper.updateById(item);
                        //判断是否要生成发货单
//                        applicationEventPublisher.publishEvent(new DeliveryEvent(this,item.getId()));
                        //更新剩余的入库数量
                        enterNumber -= needEnterNumber;
                        SalYtOrder salYtOrder = salYtOrderMapper.selectByOrderItemId(item.getId());
                        orderCode.append(salYtOrder.getCode()).append(",");
                        salYtOrderSubItemOperationManager.enterStoreOperation(needEnterNumber, item.getId());

                    }
                }
                //生成独立仓采购的出入库记录
                if(orderCode.length()>1){
                    orderCode.deleteCharAt(orderCode.length()-1);
                }
                applicationEventPublisher.publishEvent(new StoreChangeEvent(
                        this,Integer.parseInt(type),params.getEnterNumber(),specificationId,customerId,orderCode.toString(),stoYtStoreOrder.getPurchaseId())
                );
            }
        }else if(salYtCustomerStore == null && stoYtStoreRecord!=null){
            String type = stoYtStoreOrder.getType();
            if(type.equals(StoreEnterOutTypeEnum.simpleEnterStore.getKey().toString())){
                //独立入库
                //修改库存
                simpleEnterStore(stoYtStoreOrder);
            }
            else if(type.equals(StoreEnterOutTypeEnum.simpleOutStore.getKey().toString())){
                //独立出库
                //修改库存
                simpleOutStore(stoYtStoreOrder);
            }else if(type.equals(StoreEnterOutTypeEnum.purchaseEnterStore.getKey().toString())){
                //公共仓采购单采购入库
                //采购入库的订单数量分配已经在订单入库时分配
                Integer realTransitChange = stoYtStoreRecord.getRealTransitChange();
                Integer occupyTransitChange = stoYtStoreRecord.getOccupyTransitChange();
                Integer enableTransitChange = stoYtStoreRecord.getEnableTransitChange();
                stoYtStoreRecord.setSpecificationId(specificationId);
                stoYtStoreRecord.setCustomerId(customerId);
                reduceTransitStockToStoreStock(specificationId,enableTransitChange,occupyTransitChange,realTransitChange);
                applicationEventPublisher.publishEvent(new StoreChangeEvent(
                        this,StoreEnterOutTypeEnum.purchaseEnterStore.getKey(),stoYtStoreRecord,stoYtStoreOrder.getPurchaseId())
                );
            }

        }


    }

    @Transactional
    public void simpleOutStore(StoYtStoreOrder stoYtStoreOrder) {
        Long specificationId = stoYtStoreOrder.getSpecificationId();
        if(specificationId==null){
            throw new BizException(ExceptionCodeEnum.Param_Exception);
        }
        StoYtStore stoYtStore = selectOrCreateStockBySpecificationId(specificationId);
        if(stoYtStore.getEnableStore()<stoYtStoreOrder.getEnterNumber()){
            throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(),"出库数量大于了当前可用数量");
        }
        stoYtStore.setEnableStore(stoYtStore.getEnableStore()-stoYtStoreOrder.getEnterNumber());
        stoYtStore.setRealStore(stoYtStore.getRealStore()-stoYtStoreOrder.getEnterNumber());
        stoYtStoreMapper.updateById(stoYtStore);

        //生成出入库记录
        applicationEventPublisher.publishEvent(new StoreChangeEvent(this,Integer.parseInt(stoYtStoreOrder.getType()),stoYtStoreOrder.getEnterNumber(),stoYtStoreOrder.getRemark(),stoYtStore));
    }

    private void simpleEnterStore(StoYtStoreOrder stoYtStoreOrder) {
        Long specificationId = stoYtStoreOrder.getSpecificationId();
        if(specificationId==null){
            throw new BizException(ExceptionCodeEnum.Param_Exception);
        }
        StoYtStore stoYtStore = selectOrCreateStockBySpecificationId(specificationId);
        stoYtStore.setEnableStore(stoYtStore.getEnableStore()+stoYtStoreOrder.getEnterNumber());
        stoYtStore.setRealStore(stoYtStore.getRealStore()+stoYtStoreOrder.getEnterNumber());
        stoYtStoreMapper.updateById(stoYtStore);

        //生成出入库记录
        applicationEventPublisher.publishEvent(new StoreChangeEvent(this,Integer.parseInt(stoYtStoreOrder.getType()),stoYtStoreOrder.getEnterNumber(),stoYtStoreOrder.getRemark(),stoYtStore));
    }

    /**
     * 查询库存占用详情
     * @param params 查询参数，包含specificationId
     * @return 订单子项列表
     */
    public Object storeOccupyDetail(StoYtStoreRecordQueryParams params) {
        if (params.getSpecificationId() == null) {
            throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(), "规格ID不能为空");
        }
        List<SalYtOrderSubItem> salYtOrderSubItems = salYtOrderSubItemMapper.selectStoreOccupyDetail(params);
        return salYtOrderSubItems;
    }

    /**
     * 查询在途占用详情
     * @param params 查询参数，包含specificationId
     * @return 订单子项列表
     */
    public Object transitOccupyDetail(StoYtStoreRecordQueryParams params) {
        if (params.getSpecificationId() == null) {
            throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(), "规格ID不能为空");
        }
        List<Long> storeOrderIdList = params.getStoreOrderIdList();
        Long specificationId = params.getSpecificationId();
        if(storeOrderIdList!=null && !storeOrderIdList.isEmpty()){
            for (Long id:storeOrderIdList){
                StoYtStoreOrder stoYtStoreOrder = stoYtStoreOrderMapper.selectById(id);
                Long customerId = stoYtStoreOrder.getCustomerId();
                if(customerId!=null){
                    SalYtCustomerStore salYtCustomerStore = salYtCustomerStoreMapper.selectByCustomerIdAndSpecificationId(customerId, specificationId);
                    if(salYtCustomerStore!=null && salYtCustomerStore.getStatus().equals("1")){
                        return null;
                    }else {
                        //不是独立仓直接退出循环，因为出库单列表已经将是独立仓和不是独立仓区分了
                        break;
                    }
                }
            }
        }

        //占用在途数量=订单数量-订单已入库数量
        List<SalYtOrderSubItem> salYtOrderSubItems = salYtOrderSubItemMapper.selectTransitOccupyDetail(params);

        for(SalYtOrderSubItem item:salYtOrderSubItems){
            //设置占用在途待入库数量给入库时用
            int min = Math.min(item.getOccupyTransitNumber() - item.getOccupyTransitEnterNumber(), item.getNumber() - item.getEnterNumber());
            item.setOccupyTransitWaitEnterNumber(min);
            if(!OrderSubItemStatusEnum.WaitPurchase.getKey().equals(item.getStatus())){
                item.setOccupyTransitNumber(item.getTotalOccupyTransitNumber());
            }
        }

        salYtOrderSubItems = salYtOrderSubItems.stream().filter(item -> item.getOccupyTransitNumber() > 0).collect(Collectors.toList());
        return salYtOrderSubItems;
    }

    /**
     * 获取库存预警规则
     * @return 预警规则值
     */
    public String getStoreWarningRule() {
        List<SysDictionary> dictionaries = sysDictionaryMapper.selectByCode("storeWarningStore");
        if (dictionaries != null && !dictionaries.isEmpty()) {
            for (SysDictionary dict : dictionaries) {
                if ("storeWarningStore".equals(dict.getKey())) {
                    return dict.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 设置库存预警规则
     * @param value 预警规则值
     */
    public void setStoreWarningRule(String value) {
        List<SysDictionary> dictionaries = sysDictionaryMapper.selectByCode("storeWarningStore");
        if (dictionaries != null && !dictionaries.isEmpty()) {
            for (SysDictionary dict : dictionaries) {
                if ("storeWarningStore".equals(dict.getKey())) {
                    dict.setValue(value);
                    sysDictionaryMapper.updateById(dict);
                    return;
                }
            }
        }
        // 如果不存在则创建
        SysDictionary newDict = new SysDictionary();
        newDict.setCode("storeWarningStore");
        newDict.setKey("storeWarningStore");
        newDict.setValue(value);
        newDict.setDescription("库存预警数量");
        sysDictionaryMapper.insert(newDict);
    }

    /**
     * 获取规格预警规则
     * @param storeId 库存ID
     * @return 规格预警数量
     */
    public Integer getSpecificationWarningRule(Long storeId) {
        StoYtStore store = stoYtStoreMapper.selectById(storeId);
        if (store != null) {
            return store.getWarningNumber();
        }
        return null;
    }

    /**
     * 设置规格预警规则
     * @param storeId 库存ID
     * @param warningNumber 预警数量
     */
    public void setSpecificationWarningRule(Long storeId, Integer warningNumber) {
        StoYtStore store = stoYtStoreMapper.selectById(storeId);
        if (store == null) {
            throw new BizException(ExceptionCodeEnum.Not_Exists);
        }
        store.setWarningNumber(warningNumber);
        stoYtStoreMapper.updateById(store);
    }


    @Transactional
    public StoYtStore confirmInCompleted(Long specificationId,Integer confirmNumber,Long orderId,Long purchaseId) {
        StoYtStore stoYtStore = selectOrCreateStockBySpecificationId(specificationId);
        stoYtStore.setRealTransit(stoYtStore.getRealTransit()+confirmNumber);
        stoYtStore.setOccupyTransit(stoYtStore.getOccupyTransit()+confirmNumber);
        stoYtStoreMapper.updateById(stoYtStore);
        applicationEventPublisher.publishEvent(new StoreChangeEvent(this, StoreEnterOutTypeEnum.inCompleteConfirm.getKey(),confirmNumber,orderId,purchaseId,specificationId));
        return stoYtStore;
    }

    @Transactional
    public void delivery(StoYtDelivery params) {
        Long id = params.getId();
        List<StoYtDeliveryItem> stoYtDeliveryItemList = stoYtDeliveryItemMapper.listByDeliveryId(id);
        StoYtDelivery delivery = stoYtDeliveryMapper.selectById(id);
        Long customerId = delivery.getCustomerId();
        for(StoYtDeliveryItem stoYtDeliveryItem : stoYtDeliveryItemList){
            Long orderSubItemId = stoYtDeliveryItem.getOrderSubItemId();
            SalYtOrder salYtOrder=salYtOrderSubItemMapper.selectOrderById(orderSubItemId);
            Long specificationId = stoYtDeliveryItem.getSpecificationId();
            SalYtCustomerStore salYtCustomerStore = salYtCustomerStoreMapper.selectByCustomerIdAndSpecificationId(customerId, specificationId);
            if(salYtCustomerStore == null){
                StoYtStore stoYtStore = selectOrCreateStockBySpecificationId(specificationId);
                stoYtStore.setOccupyStore(stoYtStore.getOccupyStore()-stoYtDeliveryItem.getNumber());
                stoYtStore.setRealStore(stoYtStore.getRealStore()-stoYtDeliveryItem.getNumber());
                stoYtStoreMapper.updateById(stoYtStore);
                //添加出入库记录
                applicationEventPublisher.publishEvent(new StoreChangeEvent(this, StoreEnterOutTypeEnum.delivery.getKey(),stoYtDeliveryItem.getNumber(),salYtOrder.getId(),specificationId));
            }else {
                applicationEventPublisher.publishEvent(new StoreChangeEvent(this, StoreEnterOutTypeEnum.delivery.getKey(),stoYtDeliveryItem.getNumber(),salYtOrder.getId(),specificationId,salYtCustomerStore));
            }
        }

    }
}