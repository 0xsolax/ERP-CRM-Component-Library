/*
 * @author java_deng
 * @date 2025/12/1 13:09
 * @description 库存预警管理类
 */
package com.qiaomoyun.manager.pur.yt;

import com.github.pagehelper.PageHelper;
import com.qiaomoyun.Exception.BizException;
import com.qiaomoyun.Exception.ExceptionCodeEnum;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.time.LocalDateTime;

import com.qiaomoyun.entity.pro.yt.ProYtProductSpecification;
import com.qiaomoyun.entity.pur.yt.PurYtApplyPurchase;
import com.qiaomoyun.entity.pur.yt.PurYtStoreWarning;
import com.qiaomoyun.entity.pro.yt.ProYtProductFile;
import com.qiaomoyun.entity.pro.yt.ProYtProductSpecificationItem;
import com.qiaomoyun.entity.pro.yt.ProYtProductSpecificationSupplier;
import com.qiaomoyun.entity.sal.yt.SalYtCustomer;
import com.qiaomoyun.entity.sal.yt.SalYtCustomerStore;
import com.qiaomoyun.entity.sto.yt.StoYtStore;
import com.qiaomoyun.entity.sys.SysDictionary;
import com.qiaomoyun.eunm.sys.DictionaryConfigEnum;
import com.qiaomoyun.eunm.yt.ProductFilesTypeEnum;
import com.qiaomoyun.info.PageResultInfo;
import com.qiaomoyun.manager.sto.yt.StoYtStoreManager;
import com.qiaomoyun.mapper.pro.yt.ProYtProductFileMapper;
import com.qiaomoyun.mapper.pro.yt.ProYtProductSpecificationItemMapper;
import com.qiaomoyun.mapper.pro.yt.ProYtProductSpecificationMapper;
import com.qiaomoyun.mapper.pro.yt.ProYtProductSpecificationSupplierMapper;
import com.qiaomoyun.mapper.pur.yt.PurYtApplyPurchaseMapper;
import com.qiaomoyun.mapper.pur.yt.PurYtStoreWarningMapper;
import com.qiaomoyun.mapper.sal.yt.SalYtCustomerMapper;
import com.qiaomoyun.mapper.sal.yt.SalYtCustomerStoreMapper;
import com.qiaomoyun.mapper.sto.yt.StoYtStoreMapper;
import com.qiaomoyun.mapper.sys.SysDictionaryMapper;
import com.qiaomoyun.param.pur.yt.PurYtStoreWarningQueryParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PurYtStoreWarningManager {

    @Autowired
    private PurYtStoreWarningMapper purYtStoreWarningMapper;
    @Autowired
    private ProYtProductSpecificationItemMapper productSpecificationItemMapper;
    @Autowired
    private ProYtProductFileMapper productFileMapper;
    @Autowired
    private SalYtCustomerStoreMapper customerStoreMapper;
    @Autowired
    private StoYtStoreMapper storeMapper;

    @Autowired
    private PurYtApplyPurchaseManager purYtApplyPurchaseManager;
    @Autowired
    private ProYtProductSpecificationSupplierMapper proYtProductSpecificationSupplierMapper;
    @Autowired
    private SalYtCustomerMapper salYtCustomerMapper;
    @Autowired
    private PurYtApplyPurchaseMapper purYtApplyPurchaseMapper;
    @Autowired
    private SalYtCustomerStoreMapper salYtCustomerStoreMapper;
    @Autowired
    private StoYtStoreMapper stoYtStoreMapper;
    @Autowired
    private SysDictionaryMapper sysDictionaryMapper;
    @Autowired
    private StoYtStoreManager stoYtStoreManager;
    @Autowired
    private ProYtProductSpecificationMapper proYtProductSpecificationMapper;

    /**
     * 获取库存预警列表
     * @param params 查询参数
     * @return 分页结果
     */
    public Object list(PurYtStoreWarningQueryParams params) {
        PageHelper.startPage(params.getPageNum(), params.getPageSize());
        List<PurYtStoreWarning> purYtStoreWarning = purYtStoreWarningMapper.list(params);
        // 处理每个库存预警项
        purYtStoreWarning.forEach(item -> {
            Long specificationId = item.getSpecificationId();
            //处理规格项和图片
            //填充规格图片
            List<ProYtProductFile> proYtProductFiles = productFileMapper.selectByMasterIdAndType(specificationId, ProductFilesTypeEnum.specification.getKey());
            item.setImageList(proYtProductFiles);

            //填充规格项
            List<ProYtProductSpecificationItem> specificationItems = productSpecificationItemMapper.selectByProductSpecificationId(specificationId);
            item.setItemList(specificationItems);

            // 判断customerId是否有值
            Long customerId = item.getCustomerId();
            if (customerId != null) {
                // customerId有值则查询客户独立仓库存数据并填充
                SalYtCustomerStore customerStore = customerStoreMapper.selectByCustomerIdAndSpecificationId(customerId, item.getSpecificationId());
                if (customerStore != null) {
                    item.setRealStore(customerStore.getStoreNumber());
                    item.setRealTransit(customerStore.getTransitNumber());
                }
            } else {
                // customerId为空则查询仓库数据并填充
                StoYtStore store = storeMapper.selectBySpecificationId(item.getSpecificationId());
                if (store != null) {
                    item.setEnableStore(store.getEnableStore());
                    item.setOccupyStore(store.getOccupyStore());
                    item.setEnableTransit(store.getEnableTransit());
                    item.setOccupyTransit(store.getOccupyTransit());
                    item.setRealStore(store.getRealStore());
                    item.setRealTransit(store.getRealTransit());
                }
            }
        });

        return new PageResultInfo<>(purYtStoreWarning);
    }

    public Object applyDetail(PurYtStoreWarningQueryParams params) {
        List<Long> warningIdList = params.getWarningIdList();
        if (warningIdList == null || warningIdList.isEmpty()) {
            throw new BizException(ExceptionCodeEnum.Param_Exception);
        }

        // 根据id列表查询预警数据
        List<PurYtStoreWarning> purYtStoreWarnings = purYtStoreWarningMapper.selectByIds(warningIdList);

        // 查询出来的数据填充图片和规格项
        purYtStoreWarnings.forEach(item -> {
            Long specificationId = item.getSpecificationId();
            //填充规格图片
            List<ProYtProductFile> proYtProductFiles = productFileMapper.selectByMasterIdAndType(specificationId, ProductFilesTypeEnum.specification.getKey());
            item.setImageList(proYtProductFiles);

            //填充规格项
            List<ProYtProductSpecificationItem> specificationItems = productSpecificationItemMapper.selectByProductSpecificationId(specificationId);
            item.setItemList(specificationItems);

            List<ProYtProductSpecificationSupplier> specificationSupplier = proYtProductSpecificationSupplierMapper.selectBySpecificationId(specificationId);
            item.setSupplierList(specificationSupplier);
        });

        return purYtStoreWarnings;
    }

    /**
     * 提交申购
     * @param storeWarningList 库存预警数据列表
     * @return 操作结果
     */
    /**
     * 删除前一天的库存预警数据
     */
    public void deleteYesterdayWarningData() {
        purYtStoreWarningMapper.deleteYesterdayData();
    }

    /**
     * 处理独立仓预警
     */
    public void processCustomerStoreWarning() {
        // 查询所有客户独立仓数据（未删除的数据）
        QueryWrapper<SalYtCustomerStore> customerStoreWrapper = new QueryWrapper<>();
        customerStoreWrapper.eq("is_deleted", 0);
        List<SalYtCustomerStore> customerStoreList = salYtCustomerStoreMapper.selectList(customerStoreWrapper);

        for (SalYtCustomerStore store : customerStoreList) {
            // 判断库存是否低于预警值
            Integer storeNumber = store.getStoreNumber()+store.getTransitNumber();
            SalYtCustomer customer = salYtCustomerMapper.selectById(store.getCustomerId());
            Integer warningNumber = customer.getStoreWarningNumber();
            if(store.getWarningNumber()!=null){
                warningNumber=store.getWarningNumber();
            }
            if (warningNumber!=null && storeNumber <= warningNumber) {
                if(warningNumber==null){
                    warningNumber=customer.getStoreWarningNumber();
                }
                // 创建库存预警记录
                PurYtStoreWarning warning = new PurYtStoreWarning();
                warning.setCustomerId(store.getCustomerId());
                warning.setStoreName(customer.getName());
                warning.setProductId(store.getProductId());
                warning.setSpecificationId(store.getSpecificationId());
                warning.setWarningTime(LocalDateTime.now());
                warning.setWarningReason("实际库存+实际在途小于等于"+warningNumber);
                warning.setIsApplyPurchase(false);
                purYtStoreWarningMapper.insert(warning);
            }
        }
    }

    /**
     * 处理公共仓预警
     */
    @Transactional
    public void processPublicStoreWarning() {
        // 查询所有未删除的产品规格
        QueryWrapper<ProYtProductSpecification> specificationWrapper = new QueryWrapper<>();
        specificationWrapper.eq("is_deleted", 0);
        List<ProYtProductSpecification> specifications = proYtProductSpecificationMapper.selectList(specificationWrapper);

        List<SysDictionary> warningNumberConfig = sysDictionaryMapper.selectByCode(DictionaryConfigEnum.storeWarningStore.getKey());
        Integer warningNumber= Integer.parseInt(warningNumberConfig.get(0).getValue());

        for (ProYtProductSpecification specification : specifications) {
            // 检查并创建库存记录
            StoYtStore store = stoYtStoreManager.selectOrCreateStockBySpecificationId(specification.getId());

            if(store.getWarningNumber()!=null){
                warningNumber=store.getWarningNumber();
            }
            Integer storeNumber=store.getEnableStore()+store.getEnableTransit();
            // 判断库存是否低于预警值
            if (storeNumber <= warningNumber) {
                // 创建库存预警记录
                PurYtStoreWarning warning = new PurYtStoreWarning();
                warning.setStoreName("公共仓");
                warning.setProductId(store.getProductId());
                warning.setSpecificationId(store.getSpecificationId());
                warning.setWarningTime(LocalDateTime.now());
                warning.setWarningReason("可用库存+可用在途小于等于"+warningNumber);
                warning.setIsApplyPurchase(false);

                purYtStoreWarningMapper.insert(warning);
            }
        }
    }

    /**
     * 提交申购
     * @param storeWarningList 库存预警数据列表
     * @return 操作结果
     */
    public String submitApplyPurchase(List<PurYtStoreWarning> storeWarningList) {
        // 参数校验
        if (storeWarningList == null || storeWarningList.isEmpty()) {
            throw new BizException(ExceptionCodeEnum.Param_Exception);
        }

        // 根据库存预警数据创建申购单
        for(PurYtStoreWarning item : storeWarningList) {
            // 校验必填字段
            if (item.getProductId() == null) {
                throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(),"产品ID不能为空");
            }
            if (item.getSpecificationId() == null) {
                throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(),"规格ID不能为空");
            }
            if (item.getSupplierId() == null) {
                throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(),"供应商ID不能为空");
            }
            if (item.getApplyPurchaseNumber() == null || item.getApplyPurchaseNumber() <= 0) {
                throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(),"申购数量不能为空且必须大于0");
            }

            PurYtApplyPurchase applyPurchase = new PurYtApplyPurchase();
            applyPurchase.setProductId(item.getProductId());
            applyPurchase.setProductSpecificationId(item.getSpecificationId());
            applyPurchase.setSupplierId(item.getSupplierId());
            // 设置申购数量
            applyPurchase.setNumber(item.getApplyPurchaseNumber());

            Long customerId = item.getCustomerId();
            if(customerId != null) {
                SalYtCustomer customer = salYtCustomerMapper.selectById(customerId);
                applyPurchase.setCustomerId(item.getCustomerId());
                applyPurchase.setSalesEmployeeId(customer.getBelongEmployeeId());
            }
            purYtApplyPurchaseMapper.insert(applyPurchase);

            // 更新库存预警数据的isApplyPurchase状态为true
            item.setIsApplyPurchase(true);
            purYtStoreWarningMapper.updateById(item);
        }

        return "申购提交成功";
    }

    public void deleteAll() {
        purYtStoreWarningMapper.deleteAllWarningData();
    }
}