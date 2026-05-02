/*
 * @author java_deng
 * @date 2025/12/5 15:22
 * @description
 */
package com.qiaomoyun.manager.sto.yt;

import cn.hutool.core.lang.hash.Hash;
import com.github.pagehelper.PageHelper;
import com.qiaomoyun.Exception.BizException;
import com.qiaomoyun.Exception.ExceptionCodeEnum;
import com.qiaomoyun.entity.pro.yt.ProYtProductFile;
import com.qiaomoyun.entity.pro.yt.ProYtProductSpecificationItem;
import com.qiaomoyun.entity.sal.yt.SalYtCustomerStore;
import com.qiaomoyun.entity.sto.yt.*;
import com.qiaomoyun.eunm.yt.StoreEnterOutTypeEnum;
import com.qiaomoyun.info.PageResultInfo;
import com.qiaomoyun.manager.pro.yt.ProYtProductManager;
import com.qiaomoyun.mapper.pur.yt.PurYtPurchaseItemMapper;
import com.qiaomoyun.mapper.pur.yt.PurYtPurchaseMapper;
import com.qiaomoyun.mapper.sal.yt.SalYtCustomerStoreMapper;
import com.qiaomoyun.mapper.sal.yt.SalYtOrderSubItemMapper;
import com.qiaomoyun.mapper.sto.yt.StoYtStoreMapper;
import com.qiaomoyun.mapper.sto.yt.StoYtStoreOrderMapper;
import com.qiaomoyun.mapper.sto.yt.StoYtStoreOrderOperationMapper;
import com.qiaomoyun.param.sto.yt.StoYtStoreOrderAddParams;
import com.qiaomoyun.param.sto.yt.StoYtStoreOrderQueryParams;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qiaomoyun.util.EntityCodeGenerateUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class StoYtStoreOrderManager {


    private final StoYtStoreOrderMapper stoYtStoreOrderMapper;
    private final SalYtCustomerStoreMapper salYtCustomerStoreMapper;
    private final StoYtStoreMapper stoYtStoreMapper;
    private final StoYtStoreOrderOperationMapper stoYtStoreOrderOperationMapper;
    private final PurYtPurchaseItemMapper purYtPurchaseItemMapper;
    private final PurYtPurchaseMapper purYtPurchaseMapper;
    private final SalYtOrderSubItemMapper salYtOrderSubItemMapper;
    private final StoYtStoreManager stoYtStoreManager;
    private final ProYtProductManager proYtProductManager;

    public StoYtStoreOrderManager(StoYtStoreOrderMapper stoYtStoreOrderMapper, SalYtCustomerStoreMapper salYtCustomerStoreMapper, StoYtStoreMapper stoYtStoreMapper, StoYtStoreOrderOperationMapper stoYtStoreOrderOperationMapper, PurYtPurchaseItemMapper purYtPurchaseItemMapper, PurYtPurchaseMapper purYtPurchaseMapper, SalYtOrderSubItemMapper salYtOrderSubItemMapper, StoYtStoreManager stoYtStoreManager, ProYtProductManager proYtProductManager) {
        this.stoYtStoreOrderMapper = stoYtStoreOrderMapper;
        this.salYtCustomerStoreMapper = salYtCustomerStoreMapper;
        this.stoYtStoreMapper = stoYtStoreMapper;
        this.stoYtStoreOrderOperationMapper = stoYtStoreOrderOperationMapper;
        this.purYtPurchaseItemMapper = purYtPurchaseItemMapper;
        this.purYtPurchaseMapper = purYtPurchaseMapper;
        this.salYtOrderSubItemMapper = salYtOrderSubItemMapper;
        this.stoYtStoreManager = stoYtStoreManager;
        this.proYtProductManager = proYtProductManager;
    }

    /**
     * 批量新增出入库单
     */
    public void addStoreOrder(List<StoYtStoreOrderAddParams> orderList) {
        // 遍历校验每个出入库单
        for (StoYtStoreOrderAddParams orderParams : orderList) {
            Long specificationId = orderParams.getSpecificationId();
            Integer totalNumber = orderParams.getTotalNumber();
            Long customerId = orderParams.getCustomerId();

            // 获取可用库存
            Integer enableStore;
            if (customerId != null && customerId > 0) {
                // 校验独立仓的可用库存
                enableStore = getEnableStoreCustomerStockBySpecificationId(customerId,specificationId);
            } else {
                // 校验普通可用库存
                enableStore = getEnableStockBySpecificationId(specificationId);
            }

            // 校验总数是否大于可用库存
            if (totalNumber > enableStore) {
                throw new BizException(ExceptionCodeEnum.Stock_Lack_Error.getCode(),
                        String.format("规格ID: %s 的可用库存不足，可用库存: %d，申请数量: %d",
                                specificationId, enableStore, totalNumber));
            }
        }

        // 所有校验通过，批量保存出入库单
        List<StoYtStoreOrder> storeOrders = new java.util.ArrayList<>();
        for (StoYtStoreOrderAddParams orderParams : orderList) {
            StoYtStoreOrder storeOrder = new StoYtStoreOrder();
            storeOrder.setCode(EntityCodeGenerateUtil.generateUniqueId("C"));
            BeanUtils.copyProperties(orderParams, storeOrder);
            storeOrders.add(storeOrder);
        }
        // 批量保存出入库单
        for (StoYtStoreOrder storeOrder : storeOrders) {
            stoYtStoreOrderMapper.insert(storeOrder);
        }
    }

    private Integer getEnableStockBySpecificationId(Long specificationId) {
        StoYtStore stoYtStore = stoYtStoreMapper.selectBySpecificationId(specificationId);
        return stoYtStore.getEnableStore() == null ? 0 : stoYtStore.getEnableStore();
    }

    private Integer getEnableStoreCustomerStockBySpecificationId(Long customerId, Long specificationId) {
        SalYtCustomerStore salYtCustomerStore = salYtCustomerStoreMapper.selectByCustomerIdAndSpecificationId(customerId, specificationId);
        return salYtCustomerStore.getStoreNumber() == null ? 0 : salYtCustomerStore.getStoreNumber();
    }

    /**
     * 分页查询出入库单列表
     * @param params 查询参数
     * @return 分页结果
     */
    public Page<StoYtStoreOrder> getStoreOrderPage(StoYtStoreOrderQueryParams params) {
        Page<StoYtStoreOrder> page = new Page<>(params.getPageNum(), params.getPageSize());
        QueryWrapper<StoYtStoreOrder> queryWrapper = new QueryWrapper<>();

        // 构建查询条件
        if (params.getOrderCode() != null) {
            queryWrapper.like("order_code", params.getOrderCode());
        }
        if (params.getOrderType() != null) {
            queryWrapper.eq("order_type", params.getOrderType());
        }
        if (params.getSpecificationId() != null) {
            queryWrapper.eq("specification_id", params.getSpecificationId());
        }
        if (params.getCustomerId() != null) {
            queryWrapper.eq("customer_id", params.getCustomerId());
        }
        if (params.getStartTime() != null) {
            queryWrapper.ge("create_time", params.getStartTime());
        }
        if (params.getEndTime() != null) {
            queryWrapper.le("create_time", params.getEndTime());
        }
        if (params.getStatus() != null) {
            queryWrapper.eq("status", params.getStatus());
        }

        // 默认按创建时间倒序
        queryWrapper.orderByDesc("create_time");

        return stoYtStoreOrderMapper.selectPage(page, queryWrapper);
    }

    /**
     * 查询所有出入库单列表
     * @param params 查询参数
     * @return 出入库单列表
     */
    public Object getStoreOrderList(StoYtStoreOrderQueryParams params) {
        PageHelper.startPage(params.getPageNum(), params.getPageSize());
        List<StoYtStoreOrder> list = stoYtStoreOrderMapper.selectStoreOrderList(params);
        for(StoYtStoreOrder storeOrder : list){
            Long specificationId = storeOrder.getSpecificationId();
            List<ProYtProductSpecificationItem> itemsListBySpecification = proYtProductManager.getItemsListBySpecification(specificationId);
            List<ProYtProductFile> fileListBySpecification = proYtProductManager.getFileListBySpecification(specificationId);
            storeOrder.setItemList(itemsListBySpecification);
            storeOrder.setImageList(fileListBySpecification);

            //设置入库单下的订单
            List<String> orderCodeList=new ArrayList<>();
            List<HashMap<String,Object>> orderInfoList=new ArrayList<>();
            List<Long> storeOrderIdList = storeOrder.getStoreOrderIdList();
            if(storeOrderIdList != null && !storeOrderIdList.isEmpty()){
                for(Long storeOrderId : storeOrderIdList){
                    HashMap<String,Object> orderInfo= stoYtStoreOrderMapper.selectOrderItemInfo(storeOrderId);
                    if(orderInfo!=null){
                        //这是因为半成品确认可能会出现多个相同规格和订单的入库单多个入库单已经合在一起了，他们对应的订单也要在这里合在一起
                        if(!orderCodeList.contains(orderInfo.get("orderCode").toString())){
                            orderInfoList.add(orderInfo);
                            orderCodeList.add(orderInfo.get("orderCode").toString());
                        }else {
                            for(HashMap<String,Object> existOrderInfo : orderInfoList){
                                if(orderInfo.get("orderCode").toString().equals(existOrderInfo.get("orderCode").toString())){
                                    int existTotalNumber = existOrderInfo.get("totalNumber") == null ? 0 : Integer.parseInt(existOrderInfo.get("totalNumber").toString());
                                    int existEnterNumber = existOrderInfo.get("enter_number") == null ? 0 : Integer.parseInt(existOrderInfo.get("enter_number").toString());
                                    int currentTotalNumber = orderInfo.get("totalNumber") == null ? 0 : Integer.parseInt(orderInfo.get("totalNumber").toString());
                                    int currentEnterNumber = orderInfo.get("enter_number") == null ? 0 : Integer.parseInt(orderInfo.get("enter_number").toString());
                                    existOrderInfo.put("totalNumber", existTotalNumber + currentTotalNumber);
                                    existOrderInfo.put("enter_number", existEnterNumber + currentEnterNumber);
                                    break;
                                }
                            }
                        }
                    }

                }
            }
            storeOrder.setOrderInfoList(orderInfoList);
        }
        return new PageResultInfo<>(list);
    }

    /**
     * 根据ID查询出入库单详情
     * @param id 出入库单ID
     * @return 出入库单详情
     */
    public StoYtStoreOrder getStoreOrderById(Long id) {
        return stoYtStoreOrderMapper.selectById(id);
    }

    /**
     * 查询入库单进度列表
     * @param params 查询参数
     * @return 入库单进度列表
     */
    public List<StoYtStoreOrderOperation> getStoreOrderProgressList(StoYtStoreOrderQueryParams params) {
        return stoYtStoreOrderOperationMapper.selectStoreOrderProgressList(params);
    }

    @Transactional
    public StoYtStoreOrder enter(StoYtStoreOrderAddParams params, StoYtStoreRecord stoYtStoreRecord) {
        StoYtStoreOrder stoYtStoreOrder = stoYtStoreOrderMapper.selectById(params.getId());
        if(stoYtStoreOrder.getEnterNumber()+params.getEnterNumber()>stoYtStoreOrder.getTotalNumber()){
            throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(),"入库数量大于待入库数量");
        }
        //修改入库单入库数量
        stoYtStoreOrder.setEnterNumber(stoYtStoreOrder.getEnterNumber()+params.getEnterNumber());
        stoYtStoreOrderMapper.updateById(stoYtStoreOrder);

        //生成入库单操作记录
        StoYtStoreOrderOperation stoYtStoreOrderOperation = new StoYtStoreOrderOperation();
        stoYtStoreOrderOperation.setStoreOrderId(stoYtStoreOrder.getId());
        stoYtStoreOrderOperation.setType(1);
        stoYtStoreOrderOperation.setNumber(params.getEnterNumber());
        List<StoYtStoreOrderOperationDetail> details = new ArrayList<>();
        if(stoYtStoreRecord!=null){
            List<HashMap<Object, Object>> allocationOrderSubCodeList = stoYtStoreRecord.getAllocationOrderSubCodeList();
            if(allocationOrderSubCodeList!=null){
                for(HashMap<Object, Object> map : allocationOrderSubCodeList){
                    StoYtStoreOrderOperationDetail stoYtStoreOrderOperationDetail = new StoYtStoreOrderOperationDetail();
                    stoYtStoreOrderOperationDetail.setOrderId(stoYtStoreOrder.getId());
                    stoYtStoreOrderOperationDetail.setOrderCode(map.get("orderCode").toString());
                    stoYtStoreOrderOperationDetail.setNumber(Integer.parseInt(map.get("number").toString()));
                    details.add(stoYtStoreOrderOperationDetail);
                }
            }
        }

        stoYtStoreOrderOperation.setOperationDetail(details);
        stoYtStoreOrderOperationMapper.insert(stoYtStoreOrderOperation);
        return stoYtStoreOrder;
    }

    @Transactional
    public void addStore(List<StoYtStoreOrderAddParams> params) {
        for (StoYtStoreOrderAddParams stoYtStoreOrderAddParams : params) {
            StoYtStoreOrder stoYtStoreOrder = new StoYtStoreOrder();
            BeanUtils.copyProperties(stoYtStoreOrderAddParams, stoYtStoreOrder);

            //为了兼容stoYtStoreManager.enter()方法中根据stoYtStoreRecord来判断是独立仓还是公共仓
            StoYtStoreRecord stoYtStoreRecord = null;
            if(stoYtStoreOrder.getType().equals(StoreEnterOutTypeEnum.simpleEnterStore.getKey().toString())||stoYtStoreOrder.getType().equals(StoreEnterOutTypeEnum.simpleOutStore.getKey().toString())){
                stoYtStoreRecord=new StoYtStoreRecord();
            }else if(stoYtStoreOrder.getType().equals(StoreEnterOutTypeEnum.customerSimpleEnterStore.getKey().toString()) || stoYtStoreOrder.getType().equals(StoreEnterOutTypeEnum.customerSimpleOutStore.getKey().toString()) ){
                stoYtStoreRecord = null;
            }else {
                throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(),"无效的出入库方式");
            }
            stoYtStoreManager.enter(stoYtStoreRecord,stoYtStoreOrder,stoYtStoreOrderAddParams);

        }
    }
}
