/*
 * @author java_deng
 * @date 2024/11/21 16:45
 * @description 客户独立仓Manager类
 */
package com.qiaomoyun.manager.sal.yt;

import com.github.pagehelper.PageHelper;
import com.qiaomoyun.Exception.BizException;
import com.qiaomoyun.Exception.ExceptionCodeEnum;
import com.qiaomoyun.entity.pro.yt.ProYtProductSpecification;
import com.qiaomoyun.entity.pro.yt.ProYtProductSpecificationItem;
import com.qiaomoyun.entity.sal.yt.SalYtCustomer;
import com.qiaomoyun.entity.sal.yt.SalYtCustomerStore;
import com.qiaomoyun.entity.sto.yt.StoYtStoreRecord;
import com.qiaomoyun.manager.pro.yt.ProYtProductManager;
import com.qiaomoyun.mapper.pro.yt.ProYtProductSpecificationMapper;
import com.qiaomoyun.mapper.sal.yt.SalYtCustomerMapper;
import com.qiaomoyun.mapper.sal.yt.SalYtCustomerStoreMapper;
import com.qiaomoyun.mapper.sto.yt.StoYtStoreRecordMapper;
import com.qiaomoyun.param.sal.yt.SalYtCustomerQueryParams;
import com.qiaomoyun.param.sto.yt.StoYtStoreRecordQueryParams;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 客户独立仓Manager类
 */
@Component
public class SalYtCustomerStoreManager {
    
    @Autowired
    private SalYtCustomerStoreMapper salYtCustomerStoreMapper;
    
    @Autowired
    private ProYtProductSpecificationMapper productSpecificationMapper;
    @Autowired
    private ProYtProductManager proYtProductManager;
    @Autowired
    private SalYtCustomerMapper salYtCustomerMapper;
    @Autowired
    private StoYtStoreRecordMapper stoYtStoreRecordMapper;


    /**
     * 查询独立仓产品列表
     */
    public List<Map<String, Object>> productList(SalYtCustomerQueryParams params) {
        PageHelper.startPage(params.getPageNum(), params.getPageSize());
        return salYtCustomerStoreMapper.selectProductList(params);
    }
    
    /**
     * 检查产品是否开启独立仓
     */
    public boolean checkStoreExists(Long customerId, Long productId) {
        Integer count = salYtCustomerStoreMapper.selectStoreExists(customerId, productId);
        return count != null && count > 0;
    }
    
    /**
     * 保存或更新独立仓信息
     */
    public void saveOrUpdate(SalYtCustomerStore store) {
        if (store.getId() == null) {
            salYtCustomerStoreMapper.insert(store);
        } else {
            salYtCustomerStoreMapper.updateById(store);
        }
    }
    
    /**
     * 根据ID查询独立仓信息
     */
    public SalYtCustomerStore getById(Long id) {
        return salYtCustomerStoreMapper.selectById(id);
    }
    
    /**
     * 根据产品ID查询独立仓信息
     */
    public SalYtCustomerStore getByProductId(Long productId) {
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("product_id", productId);
        queryMap.put("is_deleted", 0);
        return salYtCustomerStoreMapper.selectByMap(queryMap).stream().findFirst().orElse(null);
    }
    
    /**
     * 查询产品规格列表
     */
    public Object specificationList(SalYtCustomerQueryParams params) {
        Long customerId = params.getCustomerId();
        Long productId = params.getProductId();
        ProYtProductSpecification proYtProductSpecification = new ProYtProductSpecification();
        proYtProductSpecification.setName(params.getSpecificationName());
        // 先根据productId查询出产品的所有规格
        List<ProYtProductSpecification> specificationList = proYtProductManager.selectSpecificationByProductId(productId, proYtProductSpecification);
        
        // 构建返回结果
        for (ProYtProductSpecification spec : specificationList) {
            spec.setCustomerId(customerId);
            SalYtCustomerStore store = salYtCustomerStoreMapper.selectBySpecificationAndCustomer(spec);
            if (store != null) {
                spec.setStoreStatus(store.getStatus());
                spec.setWarningNumber(store.getWarningNumber());
                spec.setStoreNumber(store.getStoreNumber());
                spec.setTransitNumber(store.getTransitNumber());
                spec.setCustomerStoreId(store.getId());
            } else {
                spec.setStoreStatus("0");
            }
        }
        
        return specificationList;
    }

    public void reduceStock(Long id, Integer storeNumber) {
        SalYtCustomerStore customerStore = salYtCustomerStoreMapper.selectById(id);
        customerStore.setStoreNumber(customerStore.getStoreNumber() - storeNumber);
        salYtCustomerStoreMapper.updateById(customerStore);
    }
    
    /**
     * 更新独立仓状态
     */
    public void updateStoreStatus(SalYtCustomerStore store) {
        Long customerId = store.getCustomerId();
        Long specificationId = store.getSpecificationId();
        String status = store.getStatus();
        
        // 参数校验
        if (customerId == null || specificationId == null || status == null) {
            throw new IllegalArgumentException("客户ID、规格ID和状态不能为空");
        }
        
        // 状态值校验
        if (!"0".equals(status) && !"1".equals(status)) {
            throw new IllegalArgumentException("状态值只能是0(关闭)或1(开启)");
        }
        
        // 查询现有数据
        SalYtCustomerStore existingStore = salYtCustomerStoreMapper.selectByCustomerIdAndSpecificationId(customerId, specificationId);

        
        if ("1".equals(status)) {
            // 开启独立仓
            if (existingStore != null) {
                // 数据存在，修改状态
                salYtCustomerStoreMapper.updateStatus(existingStore.getId(), status);
            } else {
                // 数据不存在，新增数据
                ProYtProductSpecification proYtProductSpecification = productSpecificationMapper.selectById(specificationId);

                SalYtCustomerStore newStore = new SalYtCustomerStore();
                newStore.setCustomerId(customerId);
                newStore.setSpecificationId(specificationId);
                newStore.setProductId(proYtProductSpecification.getProductId());
                newStore.setStatus("1");
                newStore.setLocationId(store.getLocationId()); // 使用传入的库位ID
                newStore.setStoreNumber(0); // 初始库存为0
                newStore.setTransitNumber(0); // 初始在途库存为0

                salYtCustomerStoreMapper.insert(newStore);
            }
        } else if ("0".equals(status)) {
            // 关闭独立仓，需要检查库存
            if (existingStore != null) {
                // 检查实际库存和在途库存是否都为0
                Integer storeNumber = existingStore.getStoreNumber();
                Integer transitNumber = existingStore.getTransitNumber();
                
                if ((storeNumber != null && storeNumber > 0) || (transitNumber != null && transitNumber > 0)) {
                    throw new BizException("库存不为空，不能关闭独立仓");
                }
                
                // 库存为空，可以关闭
                salYtCustomerStoreMapper.updateStatus(existingStore.getId(), status);
            }
        }
    }

    public void setCustomerStoreWarning(SalYtCustomerStore params) {
        Long customerId = params.getCustomerId();
        Integer warningNumber = params.getWarningNumber();
        if (customerId != null && warningNumber != null) {
            SalYtCustomer customer = new SalYtCustomer();
            customer.setId(customerId);
            customer.setStoreWarningNumber(warningNumber);
            salYtCustomerMapper.updateById(customer);
        }
    }

    public void setProductStoreWarning(SalYtCustomerStore params) {
        Long specificationId = params.getSpecificationId();
        Integer warningNumber = params.getWarningNumber();
        Long customerId = params.getCustomerId();
        if (customerId == null || warningNumber == null || specificationId == null) {
            throw new BizException(ExceptionCodeEnum.Param_Exception);
        }
        SalYtCustomerStore salYtCustomerStore = salYtCustomerStoreMapper.selectByCustomerIdAndSpecificationId(customerId, specificationId);
        if (salYtCustomerStore != null) {
            salYtCustomerStore.setWarningNumber(warningNumber);
            salYtCustomerStoreMapper.updateById(salYtCustomerStore);
        }
    }
    
    /**
     * 获取客户独立仓预警数量
     * @param customerId 客户ID
     * @return 预警产品数量
     */
    public SalYtCustomer getWarningCountByCustomerId(Long customerId) {
        if (customerId == null) {
            throw new IllegalArgumentException("客户ID不能为空");
        }
        return salYtCustomerMapper.selectById(customerId);
    }
    
    /**
     * 获取客户独立仓产品预警数量
     * @param customerId 客户ID
     * @param productId 产品ID
     * @return 预警规格数量
     */
    public Integer getWarningCountByCustomerIdAndProductId(Long customerId, Long productId) {
        if (customerId == null || productId == null) {
            throw new IllegalArgumentException("客户ID和产品ID不能为空");
        }
        return salYtCustomerStoreMapper.selectWarningCountByCustomerIdAndProductId(customerId, productId);
    }

    public SalYtCustomerStore getWarningCountByCustomerIdAndSpecificationId(Long customerId, Long specificationId) {
        return salYtCustomerStoreMapper.selectByCustomerIdAndSpecificationId(customerId, specificationId);

    }

    public Object getRecord(SalYtCustomerQueryParams params) {
        PageHelper.startPage(params.getPageNum(),params.getPageSize());
        StoYtStoreRecordQueryParams stoYtStoreRecordQueryParams = new StoYtStoreRecordQueryParams();
        BeanUtils.copyProperties(params,stoYtStoreRecordQueryParams);
        stoYtStoreRecordQueryParams.setType(params.getRecordType());
        List<StoYtStoreRecord> stoYtStoreRecords = stoYtStoreRecordMapper.listByPage(stoYtStoreRecordQueryParams);
        HashMap<String,Object> map = new HashMap<>();
        Long customerStoreId = params.getCustomerStoreId();
        SalYtCustomerStore salYtCustomerStore = salYtCustomerStoreMapper.selectById(customerStoreId);
        List<ProYtProductSpecificationItem> itemsListBySpecification = proYtProductManager.getItemsListBySpecification(salYtCustomerStore.getSpecificationId());
        // 添加规格图片
        List<com.qiaomoyun.entity.pro.yt.ProYtProductFile> fileListBySpecification = proYtProductManager.getFileListBySpecification(salYtCustomerStore.getSpecificationId());
        salYtCustomerStore.setItemList(itemsListBySpecification);
        salYtCustomerStore.setImageList(fileListBySpecification);
        map.put("customerStore",salYtCustomerStore);
        map.put("recordList",stoYtStoreRecords);
        return map;
    }
}