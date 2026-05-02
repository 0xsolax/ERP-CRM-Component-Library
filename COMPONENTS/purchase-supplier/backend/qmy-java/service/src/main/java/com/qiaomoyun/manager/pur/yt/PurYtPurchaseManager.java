/*
 * @author java_deng
 * @date 2025/12/2 15:51
 * @description 采购订单管理类
 */
package com.qiaomoyun.manager.pur.yt;

import cn.hutool.core.util.ObjectUtil;

import java.io.IOException;
import java.util.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qiaomoyun.Exception.BizException;
import com.qiaomoyun.Exception.ExceptionCodeEnum;
import com.qiaomoyun.entity.pro.yt.ProYtProductSpecificationItem;
import com.qiaomoyun.entity.pro.yt.ProYtProductSpecificationSupplier;
import com.qiaomoyun.entity.pur.yt.PurYtApplyPurchase;
import com.qiaomoyun.entity.pur.yt.PurYtPurchase;
import com.qiaomoyun.entity.pur.yt.PurYtPurchaseItem;
import com.qiaomoyun.entity.pur.yt.PurYtPurchasePayment;
import com.qiaomoyun.entity.sal.yt.*;
import com.qiaomoyun.entity.pur.yt.PurYtPurchaseFollow;
import com.qiaomoyun.entity.sto.yt.StoYtStoreOrder;
import com.qiaomoyun.entity.sto.yt.StoYtStoreOrderOperation;
import com.qiaomoyun.entity.sto.yt.StoYtStoreRecord;
import com.qiaomoyun.event.yt.PurchaseEvent;
import com.qiaomoyun.eunm.yt.ItemOperationTypeEnum;
import com.qiaomoyun.manager.sal.yt.SalYtOrderSubItemOperationManager;
import com.qiaomoyun.mapper.pro.yt.ProYtProductSpecificationSupplierMapper;
import com.qiaomoyun.mapper.sal.yt.*;
import com.qiaomoyun.mapper.pro.yt.ProYtProductFileMapper;
import com.qiaomoyun.mapper.pur.yt.PurYtApplyPurchaseMapper;
import com.qiaomoyun.mapper.sto.yt.StoYtStoreOrderMapper;
import com.qiaomoyun.mapper.sto.yt.StoYtStoreOrderOperationMapper;
import com.qiaomoyun.param.fin.yt.FinYtPaymentQueryParams;
import com.qiaomoyun.param.sal.yt.SalYtReturnOrderQueryParams;
import com.qiaomoyun.param.sto.yt.StoYtStoreOrderAddParams;
import com.qiaomoyun.eunm.yt.ProductFilesTypeEnum;
import com.qiaomoyun.eunm.yt.PurchaseStatusEnum;
import com.qiaomoyun.eunm.yt.ReturnOrderTypeEnum;
import com.qiaomoyun.info.PageResultInfo;
import com.qiaomoyun.entity.pro.yt.ProYtProductFile;
import com.qiaomoyun.manager.pro.yt.ProYtProductManager;
import com.qiaomoyun.mapper.pur.yt.PurYtPurchaseFollowMapper;
import com.qiaomoyun.mapper.pur.yt.PurYtPurchaseItemMapper;
import com.qiaomoyun.mapper.pur.yt.PurYtPurchaseMapper;
import com.qiaomoyun.mapper.pur.yt.PurYtPurchasePaymentMapper;
import com.qiaomoyun.param.pur.yt.PurYtPurchaseProductQueryParams;
import com.qiaomoyun.param.pur.yt.PurYtPurchaseQueryParams;
import com.qiaomoyun.param.pur.yt.PurYtPurchaseUpdateParams;
import com.qiaomoyun.util.EntityCodeGenerateUtil;
import com.qiaomoyun.util.QMYExcelUtil;
import com.qiaomoyun.util.TenantInfoContext;
import com.qiaomoyun.vo.pur.yt.PurYtPurchaseExport;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PurYtPurchaseManager {
    @Autowired
    private PurYtPurchaseMapper purYtPurchaseMapper;
    @Autowired
    private PurYtPurchaseFollowMapper purYtPurchaseFollowMapper;
    @Autowired
    private PurYtPurchaseItemMapper purYtPurchaseItemMapper;
    @Autowired
    private PurYtApplyPurchaseMapper purYtApplyPurchaseMapper;
    @Autowired
    private ProYtProductFileMapper proYtProductFileMapper;
    @Autowired
    private ProYtProductManager proYtProductManager;
    @Autowired
    private SalYtOrderSubItemMapper salYtOrderSubItemMapper;
    @Autowired
    private SalYtCustomerMapper salYtCustomerMapper;
    @Autowired
    private SalYtReturnOrderMapper salYtReturnOrderMapper;
    @Autowired
    private SalYtOrderSubItemOperationMapper salYtOrderSubItemOperationMapper;
    @Autowired
    private SalYtOrderSubItemOperationManager salYtOrderSubItemOperationManager;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private StoYtStoreOrderMapper stoYtStoreOrderMapper;
    @Autowired
    private StoYtStoreOrderOperationMapper stoYtStoreOrderOperationMapper;
    @Autowired
    private SalYtOrderSubMapper salYtOrderSubMapper;
    @Autowired
    private ProYtProductSpecificationSupplierMapper proYtProductSpecificationSupplierMapper;
    @Autowired
    private PurYtPurchasePaymentMapper purYtPurchasePaymentMapper;
    @Autowired
    private SalYtOrderSubReceiveMapper salYtOrderSubReceiveMapper;
    @Autowired
    private SalYtCustomerStoreMapper salYtCustomerStoreMapper;

    /**
     * 新增或编辑采购订单
     */
    @Transactional(rollbackFor = Exception.class)
    public void createOrUpdate(PurYtPurchaseUpdateParams params){
        PurYtPurchase purYtPurchase = new PurYtPurchase();
        BeanUtils.copyProperties(params, purYtPurchase);

        if(purYtPurchase.getId() == null){
            String code= EntityCodeGenerateUtil.generateUniqueId("C");
            purYtPurchase.setCode(code);
            if (PurchaseStatusEnum.Purchase.getKey().equals(purYtPurchase.getStatus())) {
                purYtPurchase.setSubmitPurchaseTime(LocalDateTime.now());
            }
            // 新增采购订单
            purYtPurchaseMapper.insert(purYtPurchase);
        }else {
            PurYtPurchase old = purYtPurchaseMapper.selectById(purYtPurchase.getId());
            String status = old.getStatus();
            if(!PurchaseStatusEnum.temporary.getKey().equals(status)){
                throw new BizException(ExceptionCodeEnum.Status_Error);
            }
            if (PurchaseStatusEnum.Purchase.getKey().equals(purYtPurchase.getStatus())) {
                purYtPurchase.setSubmitPurchaseTime(LocalDateTime.now());
            }
            // 更新采购订单
            purYtPurchaseMapper.updateById(purYtPurchase);

            // 更新采购订单明细（先删除原有的，再插入新的）
            List<PurYtPurchaseItem> existingItems = purYtPurchaseItemMapper.listByPurchaseId(purYtPurchase.getId());
            if(existingItems != null && !existingItems.isEmpty()){
                for(PurYtPurchaseItem item : existingItems){
                    purYtPurchaseItemMapper.deleteById(item.getId());
                    Long applyPurchaseId = item.getApplyPurchaseId();
                    if(applyPurchaseId != null){
                        PurYtApplyPurchase purYtApplyPurchase = new PurYtApplyPurchase();
                        purYtApplyPurchase.setId(applyPurchaseId);
                        purYtApplyPurchase.setIsDeleted(0);
                        purYtApplyPurchaseMapper.updateById(purYtApplyPurchase);
                    }
                }
            }
        }
        // 新增采购订单明细
        if(params.getItemList() != null && !params.getItemList().isEmpty()){
            for(PurYtPurchaseItem item : params.getItemList()){
                if(item.getIsDeleted()!=null &&  item.getIsDeleted()==1){
                    //isDeleted是1说明是前端传来的已经被删除的数据，前面已经做了全量删除所以直接跳过无需关注
                    continue;
                }
                //判断采购数量是否低于订单所需申购数量（比较order_sub_item里面的申购数量）
                if(item.getOrderSubItemId()!=null){
                    //根据子订单iem id查询
                    SalYtOrderSubItem salYtOrderSubItem = salYtOrderSubItemMapper.selectById(item.getOrderSubItemId());
                    if(salYtOrderSubItem.getApplyPurchaseNumber()!=null){
                        //为null代表是独立仓的采购，所以这个字段为null
                        if(salYtOrderSubItem.getApplyPurchaseNumber()>item.getNumber()){
                            //所需申购数量小于采购数量
                            throw new BizException("采购数量不能低于订单所需申购数量");
                        }
                    }
                }
                item.setPurchaseId(purYtPurchase.getId());
                item.setId(null);
                item.setEnterNumber(0);
                item.setStatus(params.getStatus());
                purYtPurchaseItemMapper.insert(item);
                Long applyPurchaseId = item.getApplyPurchaseId();
                if(applyPurchaseId != null){
                    PurYtApplyPurchase oldPurYtApplyPurchase = purYtApplyPurchaseMapper.selectById(applyPurchaseId);
                    if(oldPurYtApplyPurchase==null || oldPurYtApplyPurchase.getIsDeleted()!=0){
                        throw new BizException("这个申购单已经提交过了，请不要重复提交");
                    }
                    PurYtApplyPurchase purYtApplyPurchase = new PurYtApplyPurchase();
                    purYtApplyPurchase.setId(applyPurchaseId);
                    purYtApplyPurchase.setIsDeleted(1);
                    purYtApplyPurchaseMapper.updateById(purYtApplyPurchase);
                }

                //如果specificationId为零，也就是半成品，需要将已经确认的半成品加入
                Long specificationId = item.getSpecificationId();
                if(specificationId==null && applyPurchaseId != null){
                    PurYtApplyPurchase purYtApplyPurchase = purYtApplyPurchaseMapper.selectById(applyPurchaseId);
                    Long orderSubItemId = purYtApplyPurchase.getOrderSubItemId();
                    List<SalYtOrderSubItem> salYtOrderSubItems = salYtOrderSubItemMapper.selectByConfirmItemId(orderSubItemId);
                    salYtOrderSubItems.forEach(salYtOrderSubItem -> {
                        confirmProduct(salYtOrderSubItem,item);
                    });
                }
            }
        }

        String status = params.getStatus();
        if(PurchaseStatusEnum.Purchase.getKey().equals(status)){
            if(params.getItemList() != null && !params.getItemList().isEmpty()) {
                //发布采购提交事件
                eventPublisher.publishEvent(new PurchaseEvent(this,purYtPurchase.getId()));
            }
        }
    }

    /**
     * 通知方法：修改采购订单子项为已通知，并更新供应商单价
     * @param purchaseItemId 采购订单子项ID
     * @param supplierPrice 供应商单价
     */
    @Transactional(rollbackFor = Exception.class)
    public void notifyPurchaseItem(Long purchaseItemId, BigDecimal supplierPrice) {
        // 查询采购订单子项
        PurYtPurchaseItem purchaseItem = purYtPurchaseItemMapper.selectById(purchaseItemId);
        if (purchaseItem == null) {
            throw new BizException(ExceptionCodeEnum.Param_Exception);
        }

        // 修改为已通知，并更新供应商单价
        purchaseItem.setIsNotice(true);
        purchaseItem.setSupplierPrice(supplierPrice);

        // 更新采购订单子项
        purYtPurchaseItemMapper.updateById(purchaseItem);
    }

    public Object getPurchaseTrends(PurYtPurchaseQueryParams params) {
        Long supplierId = params.getSupplierId();
        if (supplierId == null) {
            throw new BizException(ExceptionCodeEnum.Param_Exception);
        }

        // 处理日期参数，默认过去6个月
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        if (params.getStartTime() != null) {
            startTime = params.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } else {
            // 默认开始时间为6个月前
            startTime = LocalDateTime.now().minusMonths(6).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        }
        if (params.getEndTime() != null) {
            endTime = params.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } else {
            // 默认结束时间为当前时间
            endTime = LocalDateTime.now().withDayOfMonth(1).plusMonths(1).minusNanos(1);
        }

        // 生成月份标签列表
        List<String> xAxis = new ArrayList<>();

        LocalDateTime currentMonth = startTime.withDayOfMonth(1);
        while (!currentMonth.isAfter(endTime)) {
            xAxis.add(currentMonth.format(DateTimeFormatter.ofPattern("yyyy-MM")));
            currentMonth = currentMonth.plusMonths(1);
        }

        // 查询实际的月度采购数据
        List<Map<String, Object>> purchaseData = purYtPurchaseMapper.getMonthlyPurchaseBySupplierId(supplierId, startTime, endTime);

        // 将查询结果转换为Map，便于查找
        Map<String, BigDecimal> purchaseMap = new HashMap<>();
        for (Map<String, Object> data : purchaseData) {
            String month = (String) data.get("month");
            BigDecimal amount = (BigDecimal) data.get("amount");
            purchaseMap.put(month, amount);
        }

        // 构建Y轴数据，确保每个月份都有对应的数据点
        List<BigDecimal> series = new ArrayList<>();
        for (String month : xAxis) {
            series.add(purchaseMap.getOrDefault(month, BigDecimal.ZERO));
        }

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("xAxis", xAxis);
        result.put("series", series);

        return result;
    }

    public Object getPurchaseRatio(PurYtPurchaseQueryParams params) {
        Long supplierId = params.getSupplierId();
        if (supplierId == null) {
            throw new BizException(ExceptionCodeEnum.Param_Exception);
        }

        // 处理日期参数
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        if (params.getStartTime() != null) {
            startTime = params.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
        if (params.getEndTime() != null) {
            endTime = params.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        }

        // 查询实际的采购占比数据
        List<Map<String, Object>> purchaseData = purYtPurchaseMapper.getPurchaseRatioBySupplierId(supplierId, startTime, endTime);

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        List<String> xAxis = new ArrayList<>();
        List<BigDecimal> series = new ArrayList<>();

        for (Map<String, Object> data : purchaseData) {
            String categoryName = (String) data.get("categoryName");
            BigDecimal amount = (BigDecimal) data.get("amount");
            xAxis.add(categoryName);
            series.add(amount);
        }

        result.put("xAxis", xAxis);
        result.put("series", series);

        return result;
    }

    @Transactional
    public Long confirmProduct(SalYtOrderSubItem salYtOrderSubItem,PurYtPurchaseItem  item){
        Long applyPurchaseId = item.getApplyPurchaseId();
        Long purchaseId = item.getPurchaseId();
        String status = item.getStatus();
        Long customerId = item.getCustomerId();
        String customerName = item.getCustomerName();
        Long salesEmployeeId = item.getSalesEmployeeId();
        String remark = item.getRemark();
        //判断要确认的这个采购item是否存在，如果存在且未被通知，则增加数量,如果不存在或存在但已被通知，则添加数据，
        Boolean flag=false;
        Long confirmItemId = item.getId();
        Long confirmPurchaseItemId = null;
        List<PurYtPurchaseItem> purYtPurchaseItems = purYtPurchaseItemMapper.selectByConfirmId(confirmItemId);
        for(PurYtPurchaseItem purYtPurchaseItem : purYtPurchaseItems){
            if(purYtPurchaseItem.getSpecificationId().equals(salYtOrderSubItem.getSpecificationId()) && !purYtPurchaseItem.getIsNotice()){
                purYtPurchaseItem.setNumber(purYtPurchaseItem.getNumber()+salYtOrderSubItem.getNumber());
                purYtPurchaseItemMapper.updateById(purYtPurchaseItem);
                confirmPurchaseItemId=purYtPurchaseItem.getId();
                flag=true;
                break;
            }
        }
        if(!flag){
            //flag=false ，不存在，则增加一行采购的那item数据
            PurYtPurchaseItem purYtPurchaseItem = new PurYtPurchaseItem();
            purYtPurchaseItem.setPurchaseId(purchaseId);
            purYtPurchaseItem.setStatus(status);
            purYtPurchaseItem.setApplyPurchaseId(applyPurchaseId);
            purYtPurchaseItem.setProductId(salYtOrderSubItem.getProductId());
            purYtPurchaseItem.setSpecificationId(salYtOrderSubItem.getSpecificationId());
            purYtPurchaseItem.setOrderSubId(salYtOrderSubItem.getOrderSubId());
            //使用该供应商的这个规格成本
            Long specificationId = salYtOrderSubItem.getSpecificationId();
            PurYtPurchase purYtPurchase = purYtPurchaseMapper.selectById(purchaseId);
            ProYtProductSpecificationSupplier specificationSupplier = proYtProductSpecificationSupplierMapper.selectBySpecificationIdAndSupplier(specificationId, purYtPurchase.getSupplierId());
            purYtPurchaseItem.setSupplierPrice(specificationSupplier.getSupplierPrice());
            purYtPurchaseItem.setOrderRemark(salYtOrderSubItem.getRemark());
            purYtPurchaseItem.setCategorySpecificationItemId(salYtOrderSubItem.getLabelId());
            purYtPurchaseItem.setCategorySpecificationItemName(salYtOrderSubItem.getLabelName());
            purYtPurchaseItem.setNumber(salYtOrderSubItem.getNumber());
            purYtPurchaseItem.setEnterNumber(0);
            purYtPurchaseItem.setCustomerId(customerId);
            purYtPurchaseItem.setCustomerName(customerName);
            purYtPurchaseItem.setSalesEmployeeId(salesEmployeeId);
            purYtPurchaseItem.setConfirmItemId(item.getId());
            purYtPurchaseItem.setIsNotice(false);
            purYtPurchaseItem.setOrderSubItemId(salYtOrderSubItem.getId());
            purYtPurchaseItem.setRemark(remark);
            purYtPurchaseItemMapper.insert(purYtPurchaseItem);
            confirmPurchaseItemId=purYtPurchaseItem.getId();
        }
        return confirmPurchaseItemId;

    }

    /**
     * 获取采购订单详情
     */
    public Map<String, Object> detail(Long id){
        // 获取采购订单主信息
        PurYtPurchase purchase = purYtPurchaseMapper.selectById(id);
        if(purchase == null){
            return null;
        }

        // 获取采购订单明细
        List<PurYtPurchaseItem> items = purYtPurchaseItemMapper.listByPurchaseId(id);

        for(PurYtPurchaseItem item : items){
            Long specificationId = item.getSpecificationId();
            if(specificationId!=null){
                //规格item
                List<ProYtProductSpecificationItem> itemsListBySpecification = proYtProductManager.getItemsListBySpecification(specificationId);
                item.setSpecificationItemList(itemsListBySpecification);
                //规格图片
                List<ProYtProductFile> fileListBySpecification = proYtProductManager.getFileListBySpecification(specificationId);
                item.setSpecificationImageList(fileListBySpecification);
                //供应商规格
                ProYtProductSpecificationSupplier specificationSupplier = proYtProductSpecificationSupplierMapper.selectBySpecificationIdAndSupplier(specificationId, purchase.getSupplierId());
                if(specificationSupplier!=null){
                    item.setSupplierSpecification(specificationSupplier.getSupplierSpecification());
                    item.setSupplierSpecificationCode(specificationSupplier.getSupplierSpecificationCode());
                    item.setMinNumber(specificationSupplier.getMinNumber());
                }
                Boolean isCustomerStore=false;
                Long customerId = item.getCustomerId();
                if(customerId!=null){
                    SalYtCustomerStore salYtCustomerStore = salYtCustomerStoreMapper.selectByCustomerIdAndSpecificationId(customerId, specificationId);
                    if(salYtCustomerStore!=null){
                        isCustomerStore=true;
                    }
                }
                item.setIsCustomerStore(isCustomerStore);
                //计算自动产品层级
                item.setHandProductLevel(proYtProductManager.getAutoProductLevel(specificationId));
            }
        }
        HashMap<String, Integer> statusNumberMap = new HashMap<>();
        // 将不同状态的item计算数量并以状态作为key存入statusNumberMap
        for (PurYtPurchaseItem item : items) {
            if(item.getSpecificationId()!=null){
                String status = item.getStatus();
                statusNumberMap.put(status, statusNumberMap.getOrDefault(status, 0) + 1);
            }
        }
        //获取半成品的状态map
        HashMap<Object, Object> inCompletedStatusNumberMap = new HashMap<>();
        // 获取半成品状态map
        List<Map<String, Long>> semiFinishedStatusMapList = purYtPurchaseItemMapper.getSemiFinishedProductStatusMapByPurchaseId(id);
        // 如果有结果，将其放入返回的map中
        if (semiFinishedStatusMapList != null) {
            semiFinishedStatusMapList.forEach(semiFinishedStatus -> {
               inCompletedStatusNumberMap.put(semiFinishedStatus.get("status"),semiFinishedStatus.get("count"));
            });
        }
        //采购总价
        BigDecimal totalAmount = calculateTotalAmountByPurchaseId(purchase.getId());
        if(totalAmount != null){
            BigDecimal amount = totalAmount.subtract(purchase.getDiscountAmount()).add(purchase.getShippingCost());
            purchase.setTotalAmount(amount);
        }
        // 使用SQL获取最小状态值
        String minStatus = purYtPurchaseItemMapper.getMinStatusByPurchaseId(purchase.getId());
        if(minStatus != null){
            purchase.setStatus(minStatus);
        }

        //
        Boolean isApplyPurchase=false;

        for(PurYtPurchaseItem purchaseItem : items){
            Long applyPurchaseId = purchaseItem.getApplyPurchaseId();
            if(applyPurchaseId!=null){
                isApplyPurchase=true;
                break;
            }
        }
        // 组装返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("purchase", purchase);
        result.put("items", items);
        result.put("statusNumber", statusNumberMap);
        result.put("isApplyPurchase", isApplyPurchase);
        result.put("inCompletedStatusNumber", inCompletedStatusNumberMap);
        return result;
    }

    private BigDecimal calculateTotalAmountByPurchaseId(Long purchaseId) {
        List<PurYtPurchaseItem> purYtPurchaseItems = purYtPurchaseItemMapper.selectByPurchaseId(purchaseId);
        BigDecimal totalAmount = BigDecimal.ZERO;

        for(PurYtPurchaseItem purYtPurchaseItem : purYtPurchaseItems){
            BigDecimal supplierPrice = purYtPurchaseItem.getSupplierPrice();
            Integer number = purYtPurchaseItem.getNumber();
            Long specificationId = purYtPurchaseItem.getSpecificationId();
            Long itemId = purYtPurchaseItem.getId();

            // 确保价格和数量不为空
            if (supplierPrice == null) {
                supplierPrice = BigDecimal.ZERO;
            }
            if (number == null) {
                number = 0;
            }



            BigDecimal itemAmount;
            if (specificationId == null) {
                // 计算已确认数量：所有confirmId等于当前item id的采购项目的数量之和
                Integer confirmedNumber = 0;
                if (itemId != null) {
                    List<PurYtPurchaseItem> confirmItems = purYtPurchaseItemMapper.selectByConfirmId(itemId);
                    for (PurYtPurchaseItem confirmItem : confirmItems) {
                        Integer confirmItemNumber = confirmItem.getNumber();
                        if (confirmItemNumber != null) {
                            confirmedNumber += confirmItemNumber;
                        }
                    }
                }
                // 规格为空，是半成品，计算数量为采购数量减去已确认数量
                int calculateNumber = number - confirmedNumber;
                // 确保计算数量不为负数
                calculateNumber = Math.max(calculateNumber, 0);
                itemAmount = supplierPrice.multiply(new BigDecimal(calculateNumber));
            } else {
                // 规格不为空，是成品，直接计算
                itemAmount = supplierPrice.multiply(new BigDecimal(number));
            }

            // 累加到总金额
            totalAmount = totalAmount.add(itemAmount);
        }

        return totalAmount;
    }

    /**
     * 查询采购订单列表
     */
    public Object list(PurYtPurchaseQueryParams params){
        // 分页查询
        PageHelper.startPage(params.getPageNum(), params.getPageSize());
        List<PurYtPurchase> list = purYtPurchaseMapper.list(params);
        for(PurYtPurchase purYtPurchase : list){
            // 使用SQL计算采购总价
                BigDecimal totalAmount = calculateTotalAmountByPurchaseId(purYtPurchase.getId());
                if(totalAmount != null){
                    BigDecimal amount = totalAmount.subtract(purYtPurchase.getDiscountAmount()).add(purYtPurchase.getShippingCost());
                    purYtPurchase.setTotalAmount(amount);
                }

                // 使用SQL获取最小状态值
                String minStatus = purYtPurchaseItemMapper.getMinStatusByPurchaseId(purYtPurchase.getId());
                if(minStatus != null){
                    purYtPurchase.setStatus(minStatus);
                }

                //计算待入库数量
            Integer waitEnter = purYtPurchaseItemMapper.calculateWaitEnterByPurchaseId(purYtPurchase.getId());
            if(waitEnter != null){
                purYtPurchase.setWaitEnterNumber(waitEnter);
            }
        }
        return new PageResultInfo<>(list);
    }

    public Object listTemporary(PurYtPurchaseQueryParams params) {
        return purYtPurchaseMapper.list(params);
    }

    /**
     * 根据采购单ID、订单子ID、规格名称、产品code查询采购单产品列表
     * @param params 查询参数
     * @return 采购单产品列表
     */
    public List<PurYtPurchaseItem> listProductsByParams(PurYtPurchaseProductQueryParams params) {
        if (params == null) {
            return new ArrayList<>();
        }
        PurYtPurchase purchase = purYtPurchaseMapper.selectById(params.getPurchaseId());
        List<PurYtPurchaseItem> purYtPurchaseItems = purYtPurchaseItemMapper.completedListByPurchaseAndProductParams(params);

        // 获取所有的产品ID和规格ID
        List<Long> productIds = new ArrayList<>();
        List<Long> specificationIds = new ArrayList<>();

        // 用于存储产品总成本和总数量
        Map<Long, BigDecimal> productCostMap = new HashMap<>();
        Map<Long, Integer> productNumberMap = new HashMap<>();

        // 用于存储规格总成本和总数量
        Map<Long, BigDecimal> specificationCostMap = new HashMap<>();
        Map<Long, Integer> specificationNumberMap = new HashMap<>();

        for (PurYtPurchaseItem item : purYtPurchaseItems) {
            if (item.getProductId() != null) {
                productIds.add(item.getProductId());
                // 计算产品总成本和产品总数量
                Long productId = item.getProductId();
                BigDecimal cost = item.getSupplierPrice().multiply(new BigDecimal(item.getNumber()));
                productCostMap.put(productId, productCostMap.getOrDefault(productId, BigDecimal.ZERO).add(cost));
                productNumberMap.put(productId, productNumberMap.getOrDefault(productId, 0) + item.getNumber());
            }
            if (item.getSpecificationId() != null) {
                specificationIds.add(item.getSpecificationId());
                // 计算规格总成本和规格总数量
                Long specificationId = item.getSpecificationId();
                BigDecimal cost = item.getSupplierPrice().multiply(new BigDecimal(item.getNumber()));
                specificationCostMap.put(specificationId, specificationCostMap.getOrDefault(specificationId, BigDecimal.ZERO).add(cost));
                specificationNumberMap.put(specificationId, specificationNumberMap.getOrDefault(specificationId, 0) + item.getNumber());

                Boolean isCustomerStore=false;
                Long customerId = item.getCustomerId();
                if(customerId!=null){
                    SalYtCustomerStore salYtCustomerStore = salYtCustomerStoreMapper.selectByCustomerIdAndSpecificationId(customerId, specificationId);
                    if(salYtCustomerStore!=null){
                        isCustomerStore=true;
                    }
                }
                item.setIsCustomerStore(isCustomerStore);
            }

        }

        // 根据产品ID获取产品图片
        Map<Long, List<ProYtProductFile>> productImageMap = new HashMap<>();
        for (Long productId : productIds) {
            if (!productImageMap.containsKey(productId)) {
                List<ProYtProductFile> productImages = proYtProductFileMapper.selectByMasterIdAndType(productId, ProductFilesTypeEnum.product.getKey());
                productImageMap.put(productId, productImages);
            }
        }

        // 根据规格ID获取规格图片
        Map<Long, List<ProYtProductFile>> specificationImageMap = new HashMap<>();
        for (Long specificationId : specificationIds) {
            if (!specificationImageMap.containsKey(specificationId)) {
                List<ProYtProductFile> specificationImages = proYtProductManager.getFileListBySpecification(specificationId);
                specificationImageMap.put(specificationId, specificationImages);
            }
        }

        // 根据规格ID获取规格item
        Map<Long, List<ProYtProductSpecificationItem>> specificationItemMap = new HashMap<>();
        for (Long specificationId : specificationIds) {
            if (!specificationItemMap.containsKey(specificationId)) {
                List<ProYtProductSpecificationItem> itemList = proYtProductManager.getItemsListBySpecification(specificationId);
                specificationItemMap.put(specificationId, itemList);
            }
        }

        // 将图片和规格项分配到对应的采购订单子项中
        for (PurYtPurchaseItem item : purYtPurchaseItems) {
            if (item.getProductId() != null) {
                item.setProductImageList(productImageMap.getOrDefault(item.getProductId(), new ArrayList<>()));
                // 设置产品成本和总数量
                item.setProductTotalCost(productCostMap.get(item.getProductId()));
                item.setProductTotalNumber(productNumberMap.get(item.getProductId()));
            }
            if (item.getSpecificationId() != null) {
                item.setSpecificationImageList(specificationImageMap.getOrDefault(item.getSpecificationId(), new ArrayList<>()));
                item.setSpecificationItemList(specificationItemMap.getOrDefault(item.getSpecificationId(), new ArrayList<>()));
                if (purchase != null) {
                    ProYtProductSpecificationSupplier specificationSupplier =
                            proYtProductSpecificationSupplierMapper.selectBySpecificationIdAndSupplier(item.getSpecificationId(), purchase.getSupplierId());
                    if (specificationSupplier != null) {
                        item.setSupplierSpecificationCode(specificationSupplier.getSupplierSpecificationCode());
                    }
                }
                // 设置规格成本和总数量
                item.setSpecificationTotalCost(specificationCostMap.get(item.getSpecificationId()));
                item.setSpecificationTotalNumber(specificationNumberMap.get(item.getSpecificationId()));
            }

        }

        return purYtPurchaseItems;
    }

    /**
     * 根据采购单ID、订单子ID、产品code查询采购单半成品列表（没有规格的产品）
     * @param params 查询参数
     * @return 采购单半成品列表
     */
    public List<PurYtPurchaseItem> listSemiFinishedProductsByParams(PurYtPurchaseProductQueryParams params) {
        if (params == null) {
            return new ArrayList<>();
        }
        if(params.getPurchaseId()==null){
            throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(),"采购单id不能为空");
        }
        PurYtPurchase purchase = purYtPurchaseMapper.selectById(params.getPurchaseId());
        // 查询没有规格的半成品列表
        List<PurYtPurchaseItem> purYtPurchaseItems = purYtPurchaseItemMapper.listSemiFinishedProductsByParams(params);

        // 获取所有的产品ID
        List<Long> productIds = new ArrayList<>();
        for (PurYtPurchaseItem item : purYtPurchaseItems) {
            if (item.getProductId() != null) {
                productIds.add(item.getProductId());
            }
        }

        // 根据产品ID获取产品图片
        Map<Long, List<ProYtProductFile>> productImageMap = new HashMap<>();
        for (Long productId : productIds) {
            if (!productImageMap.containsKey(productId)) {
                List<ProYtProductFile> productImages = proYtProductFileMapper.selectByMasterIdAndType(productId, ProductFilesTypeEnum.product.getKey());
                productImageMap.put(productId, productImages);
            }
        }

        // 将产品图片分配到对应的采购订单子项中（不需要规格图片和规格项）
        for (PurYtPurchaseItem item : purYtPurchaseItems) {
            if (item.getProductId() != null) {
                item.setProductImageList(productImageMap.getOrDefault(item.getProductId(), new ArrayList<>()));
            }
        }

        // 提取所有确认子项的规格ID
        List<Long> confirmSpecificationIds = new ArrayList<>();
        for (PurYtPurchaseItem item : purYtPurchaseItems) {
                Long confirmId = item.getId();
                List<PurYtPurchaseItem> confirmItemList = purYtPurchaseItemMapper.selectByConfirmId(confirmId);
                item.setConfirmItemList(confirmItemList);
                if (confirmItemList != null && !confirmItemList.isEmpty()) {
                    item.setConfirmItemList(confirmItemList);
                    for (PurYtPurchaseItem confirmItem : confirmItemList) {
                        if (confirmItem.getSpecificationId() != null) {
                            confirmSpecificationIds.add(confirmItem.getSpecificationId());
                        }
                    }
                }
        }

        // 获取确认子项的规格图片
        Map<Long, List<ProYtProductFile>> confirmSpecificationImageMap = new HashMap<>();
        for (Long specificationId : confirmSpecificationIds) {
            if (!confirmSpecificationImageMap.containsKey(specificationId)) {
                List<ProYtProductFile> specificationImages = proYtProductManager.getFileListBySpecification(specificationId);
                confirmSpecificationImageMap.put(specificationId, specificationImages);
            }
        }

        // 获取确认子项的规格项
        Map<Long, List<ProYtProductSpecificationItem>> confirmSpecificationItemMap = new HashMap<>();
        for (Long specificationId : confirmSpecificationIds) {
            if (!confirmSpecificationItemMap.containsKey(specificationId)) {
                List<ProYtProductSpecificationItem> itemList = proYtProductManager.getItemsListBySpecification(specificationId);
                confirmSpecificationItemMap.put(specificationId, itemList);
            }
        }

        // 为确认子项填充规格图片和规格项
        for (PurYtPurchaseItem item : purYtPurchaseItems) {
            List<PurYtPurchaseItem> confirmItemList = item.getConfirmItemList();
            if (confirmItemList != null) {
                for (PurYtPurchaseItem confirmItem : confirmItemList) {
                    if (confirmItem.getSpecificationId() != null) {
                        confirmItem.setSpecificationImageList(confirmSpecificationImageMap.getOrDefault(confirmItem.getSpecificationId(), new ArrayList<>()));
                        confirmItem.setSpecificationItemList(confirmSpecificationItemMap.getOrDefault(confirmItem.getSpecificationId(), new ArrayList<>()));
                        if (purchase != null) {
                            ProYtProductSpecificationSupplier specificationSupplier =
                                    proYtProductSpecificationSupplierMapper.selectBySpecificationIdAndSupplier(
                                            confirmItem.getSpecificationId(), purchase.getSupplierId());
                            if (specificationSupplier != null) {
                                confirmItem.setSupplierSpecificationCode(specificationSupplier.getSupplierSpecificationCode());
                            }
                        }
                    }
                }
            }
        }

        return purYtPurchaseItems;
    }

    /**
     * 采购单退货处理
     * 如果是半成品需要判断退货数量是否大于了待确认数量
     * 如果是成品需要确认退货数量是否大于了待入库数量
     */
    @Transactional(rollbackFor = Exception.class)
    public void returnPurchase(SalYtReturnOrder params) {
        // 参数校验
        if (params == null || params.getPurchaseItemId() == null || params.getReturnNumber() == null) {
            throw new BizException(ExceptionCodeEnum.Param_Exception);
        }

        // 查询采购订单子项
        PurYtPurchaseItem purchaseItem = purYtPurchaseItemMapper.selectById(params.getPurchaseItemId());
        if (purchaseItem == null) {
            throw new BizException(ExceptionCodeEnum.Not_Exists);
        }
        //添加退货记录
        params.setType(ReturnOrderTypeEnum.purchaseOrder.getKey());
        params.setBeforeReturnNumber(purchaseItem.getNumber());
        salYtReturnOrderMapper.insert(params);

        // 检查是否为半成品（没有规格ID）
        if (purchaseItem.getSpecificationId() == null) {
            // 半成品退货逻辑
            handleSemiFinishedProductReturn(purchaseItem, params);
        } else {
            // 成品退货逻辑
            handleFinishedProductReturn(purchaseItem, params);
        }

        //添加操作记录
        salYtOrderSubItemOperationManager.purchaseReturnOperation(params.getReturnNumber(),purchaseItem.getId());
    }

    /**
     * 处理半成品退货
     */
    private void handleSemiFinishedProductReturn(PurYtPurchaseItem purchaseItem, SalYtReturnOrder params) {
        // 获取确认子项列表
        List<PurYtPurchaseItem> confirmItemList = purYtPurchaseItemMapper.selectByConfirmId(purchaseItem.getId());


        // 计算待确认数量：总采购数量 - 已确认数量
        int totalConfirmNumber = 0;
        if (confirmItemList == null || confirmItemList.isEmpty()) {
            for (PurYtPurchaseItem confirmItem : confirmItemList) {
                if (confirmItem.getNumber() != null) {
                    totalConfirmNumber += confirmItem.getNumber();
                }
            }
        }

        int toBeConfirmedNumber = purchaseItem.getNumber() - totalConfirmNumber;

        // 判断退货数量是否大于已确认数量
        if (params.getReturnNumber() > toBeConfirmedNumber) {
            throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(), "退货数量不能大于已确认数量");
        }

        //减少总采购数量
        purchaseItem.setNumber(purchaseItem.getNumber()-params.getReturnNumber());
        purYtPurchaseItemMapper.updateById(purchaseItem);

    }

    /**
     * 处理成品退货
     */
    private void handleFinishedProductReturn(PurYtPurchaseItem purchaseItem, SalYtReturnOrder params) {

        // 计算待入库数量：采购数量 - 已入库数量
        int toBeInboundNumber = purchaseItem.getNumber() - purchaseItem.getEnterNumber();

        // 判断退货数量是否大于待入库数量
        if (params.getReturnNumber() > toBeInboundNumber) {
            throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(), "退货数量不能大于待入库数量");
        }

        //减少采购数量
        purchaseItem.setNumber(purchaseItem.getNumber()-params.getReturnNumber());
        purYtPurchaseItemMapper.updateById(purchaseItem);

        //减少入库单待入库数量
        StoYtStoreOrder stoYtStoreOrder= stoYtStoreOrderMapper.selectByPurchaseItemId(purchaseItem.getId());
        if(stoYtStoreOrder!=null){
            stoYtStoreOrder.setTotalNumber(stoYtStoreOrder.getTotalNumber()-params.getReturnNumber());
            stoYtStoreOrderMapper.updateById(stoYtStoreOrder);
        }
    }

    /**
     * 查询采购单退货记录
     */
    public List<SalYtReturnOrder> listReturnRecords(PurYtPurchaseQueryParams params) {
        Long purchaseId = params.getPurchaseId();
        String productCode = params.getProductCode();
        String specificationName = params.getSpecificationName();
        // 参数校验
        if (purchaseId == null) {
            throw new BizException(ExceptionCodeEnum.Param_Exception);
        }

        // 构建采购子项查询参数
        PurYtPurchaseProductQueryParams queryParams = new PurYtPurchaseProductQueryParams();
        queryParams.setPurchaseId(purchaseId);
        queryParams.setProductCode(productCode);
        queryParams.setSpecificationName(specificationName);

        // 查询采购子项
        List<PurYtPurchaseItem> purchaseItemList = purYtPurchaseItemMapper.listByPurchaseAndProductParams(queryParams);

        if (purchaseItemList == null || purchaseItemList.isEmpty()) {
            return new ArrayList<>();
        }

        List<SalYtReturnOrder> result = new ArrayList<>();
        purchaseItemList.forEach(purchaseItem -> {
            Long purchaseItemId = purchaseItem.getId();
            Long orderSubItemId = purchaseItem.getOrderSubItemId();

            // 获取采购单退货信息（类型2）
            List<SalYtReturnOrder> purchaseReturnOrders = salYtReturnOrderMapper.selectByTypeAndPurchaseItemId(ReturnOrderTypeEnum.purchaseOrder.getKey(), purchaseItemId);

            // 获取订单退货信息（类型1）
            List<SalYtReturnOrder> orderReturnOrders = salYtReturnOrderMapper.selectByTypeAndOrderSubId(ReturnOrderTypeEnum.order.getKey(), orderSubItemId);

            if(ObjectUtil.isNotEmpty(purchaseReturnOrders) || ObjectUtil.isNotEmpty(orderReturnOrders)){
                SalYtReturnOrder salYtReturnOrder = new SalYtReturnOrder();
                salYtReturnOrder.setOrderSubItemId(orderSubItemId);
                salYtReturnOrder.setPurchaseItemId(purchaseItemId);
                salYtReturnOrder.setProductCode(purchaseItem.getProductCode());
                salYtReturnOrder.setProductId(purchaseItem.getProductId());
                salYtReturnOrder.setSpecificationId(purchaseItem.getSpecificationId());
                salYtReturnOrder.setOrderSubId(purchaseItem.getOrderSubId());
                if(ObjectUtil.isNotEmpty(purchaseItem.getOrderSubId())){
                    SalYtOrderSub salYtOrderSub = salYtOrderSubMapper.selectById(purchaseItem.getOrderSubId());
                    salYtReturnOrder.setOrderSubCode(salYtOrderSub.getSubCode());
                }

                // 处理采购单退货信息
                if (purchaseReturnOrders != null && !purchaseReturnOrders.isEmpty()) {
                    // 获取采购退货初始数量（最早的退货数据的before_return_number）
                    SalYtReturnOrder firstPurchaseReturn = purchaseReturnOrders.stream()
                            .min((o1, o2) -> o1.getCreateTime().compareTo(o2.getCreateTime()))
                            .orElse(null);
                    Integer initialPurchaseReturnNumber = firstPurchaseReturn != null ? firstPurchaseReturn.getBeforeReturnNumber() : 0;

                    // 统计采购总退货数量
                    Integer totalPurchaseReturnNumber = purchaseReturnOrders.stream()
                            .mapToInt(SalYtReturnOrder::getReturnNumber)
                            .sum();

                    // 计算采购当前数量
                    Integer purchaseCurrentNumber = initialPurchaseReturnNumber - totalPurchaseReturnNumber;

                    salYtReturnOrder.setPurchaseInitNumber(initialPurchaseReturnNumber);
                    salYtReturnOrder.setPurchaseTotalReturnNumber(totalPurchaseReturnNumber);
                    salYtReturnOrder.setPurchaseCurrentNumber(purchaseCurrentNumber);


                }

                // 处理订单退货信息
                if (orderReturnOrders != null && !orderReturnOrders.isEmpty()) {
                    // 获取订单退货初始数量（最早的退货数据的before_return_number）
                    SalYtReturnOrder firstOrderReturn = orderReturnOrders.stream()
                            .min((o1, o2) -> o1.getCreateTime().compareTo(o2.getCreateTime()))
                            .orElse(null);
                    Integer initialOrderReturnNumber = firstOrderReturn != null ? firstOrderReturn.getBeforeReturnNumber() : 0;

                    // 统计订单总退货数量
                    Integer totalOrderReturnNumber = orderReturnOrders.stream()
                            .mapToInt(SalYtReturnOrder::getReturnNumber)
                            .sum();

                    // 计算订单当前数量
                    Integer orderCurrentNumber = initialOrderReturnNumber - totalOrderReturnNumber;

                    // 将统计信息设置到每条订单退货记录中
                    salYtReturnOrder.setOrderInitNumber(initialOrderReturnNumber);
                    salYtReturnOrder.setOrderTotalReturnNumber(totalOrderReturnNumber);
                    salYtReturnOrder.setOrderCurrentNumber(orderCurrentNumber);
                }
                result.add(salYtReturnOrder);
            }
        });

        // 遍历result,如果salYtReturnOrder规格id不为空则根据规格id获取规格项、规格图片
        // 收集所有非空的specificationId
        List<Long> specificationIds = result.stream()
                .map(SalYtReturnOrder::getSpecificationId)
                .filter(ObjectUtil::isNotNull)
                .distinct()
                .collect(Collectors.toList());

        if (!specificationIds.isEmpty()) {
            // 获取规格图片
            Map<Long, List<ProYtProductFile>> specificationImageMap = new HashMap<>();
            for (Long specificationId : specificationIds) {
                List<ProYtProductFile> specificationImages = proYtProductManager.getFileListBySpecification(specificationId);
                specificationImageMap.put(specificationId, specificationImages);
            }

            // 获取规格项
            Map<Long, List<ProYtProductSpecificationItem>> specificationItemMap = new HashMap<>();
            for (Long specificationId : specificationIds) {
                List<ProYtProductSpecificationItem> itemList = proYtProductManager.getItemsListBySpecification(specificationId);
                specificationItemMap.put(specificationId, itemList);
            }

            // 为退货记录填充规格图片和规格项
            for (SalYtReturnOrder returnOrder : result) {
                Long specificationId = returnOrder.getSpecificationId();
                if (specificationId != null) {
                    returnOrder.setImageList(specificationImageMap.getOrDefault(specificationId, new ArrayList<>()));
                    returnOrder.setItemList(specificationItemMap.getOrDefault(specificationId, new ArrayList<>()));
                }
            }
        }
        return result;
    }

    /**
     * 获取采购单退货统计信息
     * @param params 查询参数，包含采购单ID、规格名称、产品code、开始时间、结束时间等
     * @return 退货统计信息列表
     */
    public List<SalYtReturnOrder> getReturnStats(PurYtPurchaseQueryParams params) {
        Long purchaseId = params.getPurchaseId();
        // 参数校验
        if (purchaseId == null) {
            throw new BizException(ExceptionCodeEnum.Param_Exception);
        }

        // 创建退货订单查询参数
        SalYtReturnOrderQueryParams returnOrderParams = new SalYtReturnOrderQueryParams();
        returnOrderParams.setPurchaseId(purchaseId);
        returnOrderParams.setProductCode(params.getProductCode());
        returnOrderParams.setSpecificationName(params.getSpecificationName());

        // 将Date类型转换为LocalDateTime类型
        if (params.getStartTime() != null) {
            returnOrderParams.setStartTime(params.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        }
        if (params.getEndTime() != null) {
            returnOrderParams.setEndTime(params.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        }

        // 查询退货信息
        List<SalYtReturnOrder> result = salYtReturnOrderMapper.getReturnByPurchaseId(returnOrderParams);


        // 为每个统计结果添加规格图片和规格项
        if (result != null && !result.isEmpty()) {
            // 收集所有规格ID
            List<Long> specificationIds = result.stream()
                    .map(SalYtReturnOrder::getSpecificationId)
                    .filter(ObjectUtil::isNotNull)
                    .distinct()
                    .collect(Collectors.toList());

            if (!specificationIds.isEmpty()) {
                // 获取规格图片
                Map<Long, List<ProYtProductFile>> specificationImageMap = new HashMap<>();
                for (Long specificationId : specificationIds) {
                    List<ProYtProductFile> specificationImages = proYtProductManager.getFileListBySpecification(specificationId);
                    specificationImageMap.put(specificationId, specificationImages);
                }

                // 获取规格项
                Map<Long, List<ProYtProductSpecificationItem>> specificationItemMap = new HashMap<>();
                for (Long specificationId : specificationIds) {
                    List<ProYtProductSpecificationItem> itemList = proYtProductManager.getItemsListBySpecification(specificationId);
                    specificationItemMap.put(specificationId, itemList);
                }

                // 为统计结果填充规格图片和规格项
                for (SalYtReturnOrder statsVo : result) {
                    Long specificationId = statsVo.getSpecificationId();
                    if (specificationId != null) {
                        statsVo.setImageList(specificationImageMap.getOrDefault(specificationId, new ArrayList<>()));
                        statsVo.setItemList(specificationItemMap.getOrDefault(specificationId, new ArrayList<>()));
                    }
                }
            }
        }

        return result;
    }

    /**
     * 查询采购单跟进记录
     * @param params 查询参数
     * @return 采购单跟进记录列表
     */
    public List<PurYtPurchaseFollow> getPurchaseFollowList(PurYtPurchaseQueryParams params) {
        Long purchaseId = params.getPurchaseId();
        // 参数校验
        if (purchaseId == null) {
            throw new BizException(ExceptionCodeEnum.Param_Exception);
        }

        // 查询跟进记录
        return purYtPurchaseFollowMapper.selectByPurchaseId(params);
    }

    /**
     * 跟进采购单
     * @param follow 跟进记录信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void followPurchase(PurYtPurchaseFollow follow) {
        Long purchaseId = follow.getPurchaseId();
        // 参数校验
        if (purchaseId == null || follow.getTheme() == null) {
            throw new BizException(ExceptionCodeEnum.Param_Exception);
        }

        // 保存跟进记录
        if (follow.getId() == null) {
            purYtPurchaseFollowMapper.insert(follow);
        } else {
            purYtPurchaseFollowMapper.updateById(follow);
        }
    }

    @Transactional
    public Long enter(StoYtStoreOrderAddParams params, StoYtStoreRecord stoYtStoreRecord) {
        StoYtStoreOrder stoYtStoreOrder = stoYtStoreOrderMapper.selectById(params.getId());
        //查找关联的采购单是否需要入库，并且判断状态是否需要改变
        Long purchaseItemId = stoYtStoreOrder.getPurchaseItemId();
        if(purchaseItemId!=null){
            PurYtPurchaseItem purYtPurchaseItem = purYtPurchaseItemMapper.selectById(purchaseItemId);
            Integer enterNumber=purYtPurchaseItem.getEnterNumber()+ params.getEnterNumber();
            purYtPurchaseItem.setEnterNumber(enterNumber);
            if(enterNumber>purYtPurchaseItem.getNumber()){
                throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(),"入库数量大于该采购单的采购数量");
            }else if(enterNumber.equals(purYtPurchaseItem.getNumber())){
                purYtPurchaseItem.setStatus(PurchaseStatusEnum.EnterStore.getKey());
            }
            purYtPurchaseItemMapper.updateById(purYtPurchaseItem);
            List<PurYtPurchaseItem> purYtPurchaseItems = purYtPurchaseItemMapper.selectCompletedByPurchaseIdAndStatus(purYtPurchaseItem.getPurchaseId(), PurchaseStatusEnum.Purchase.getKey());
            if(purYtPurchaseItems==null || purYtPurchaseItems.isEmpty()){
                PurYtPurchase purYtPurchase = new PurYtPurchase();
                purYtPurchase.setId(purYtPurchaseItem.getPurchaseId());
                purYtPurchase.setStatus(PurchaseStatusEnum.EnterStore.getKey());
                purYtPurchase.setCompletedTime(LocalDateTime.now());
                purYtPurchaseMapper.updateById(purYtPurchase);
            }
            if(stoYtStoreRecord!=null){
                Boolean isPurchaseCreateOperation = stoYtStoreRecord.getIsPurchaseCreateOperation();
                //订单没有生成入库记录，说明采购单就要生成入库记录
                if(isPurchaseCreateOperation!=null && isPurchaseCreateOperation){
                    salYtOrderSubItemOperationManager.enterStorePurchaseOperation(params.getEnterNumber(), purchaseItemId);
                }
            }
        }
        return purchaseItemId;
    }

    public Object returnDetail(PurYtPurchaseQueryParams params) {
        Long itemId = params.getItemId();
        if(itemId==null){
            throw new  BizException(ExceptionCodeEnum.Param_Exception);
        }

        // 1. 查询采购单item信息
        PurYtPurchaseItem purchaseItem = purYtPurchaseItemMapper.selectById(itemId);
        if(purchaseItem == null) {
            throw new BizException(ExceptionCodeEnum.Param_Exception);
        }

        // 2. 查询子订单item信息
        SalYtOrderSubItem orderSubItem = null;
        Long orderSubItemId = purchaseItem.getOrderSubItemId();
        if(orderSubItemId != null) {
            orderSubItem = salYtOrderSubItemMapper.selectById(orderSubItemId);
        }

        // 3. 查询采购单和子订单的退货信息
        List<SalYtReturnOrder> returnList = salYtReturnOrderMapper.selectByPurchaseItemIdOrOrderSubItemId(itemId, orderSubItemId);

        // 4. 组装返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("purchaseItem", purchaseItem);
        result.put("orderSubItem", orderSubItem);
        result.put("returnList", returnList);

        return result;
    }

    public Object itemOperation(Long itemId) {
        // 1. 查询采购单item信息，获取对应的orderSubItemId
        PurYtPurchaseItem purchaseItem = purYtPurchaseItemMapper.selectById(itemId);
        if (purchaseItem == null) {
            throw new BizException(ExceptionCodeEnum.Param_Exception);
        }

        List<SalYtOrderSubItemOperation> operationList =
                new ArrayList<>(salYtOrderSubItemOperationMapper.selectByPurchaseItemIdOrOrderSubItemId(itemId, purchaseItem.getOrderSubItemId()));

        boolean hasEnterStoreOperation = operationList.stream()
                .anyMatch(item -> item != null && ItemOperationTypeEnum.EnterStore.getKey().equals(item.getType()));
        if (!hasEnterStoreOperation) {
            StoYtStoreOrder storeOrder = stoYtStoreOrderMapper.selectByPurchaseItemId(itemId);
            if (storeOrder != null) {
                List<StoYtStoreOrderOperation> storeOrderOperationList =
                        stoYtStoreOrderOperationMapper.selectByStoreOrderId(storeOrder.getId());
                if (storeOrderOperationList != null && !storeOrderOperationList.isEmpty()) {
                    for (StoYtStoreOrderOperation storeOrderOperation : storeOrderOperationList) {
                        if (storeOrderOperation == null || storeOrderOperation.getNumber() == null || storeOrderOperation.getNumber() <= 0) {
                            continue;
                        }
                        SalYtOrderSubItemOperation enterOperation = new SalYtOrderSubItemOperation();
                        enterOperation.setType(ItemOperationTypeEnum.EnterStore.getKey());
                        enterOperation.setPurchaseItemId(itemId);
                        enterOperation.setOrderSubItemId(purchaseItem.getOrderSubItemId());
                        enterOperation.setOperationCount(storeOrderOperation.getNumber());
                        enterOperation.setCreateTime(storeOrderOperation.getCreateTime());
                        enterOperation.setCreateUserName(storeOrderOperation.getCreateUserName());
                        operationList.add(enterOperation);
                    }
                } else if (storeOrder.getEnterNumber() != null && storeOrder.getEnterNumber() > 0) {
                    SalYtOrderSubItemOperation enterOperation = new SalYtOrderSubItemOperation();
                    enterOperation.setType(ItemOperationTypeEnum.EnterStore.getKey());
                    enterOperation.setPurchaseItemId(itemId);
                    enterOperation.setOrderSubItemId(purchaseItem.getOrderSubItemId());
                    enterOperation.setOperationCount(storeOrder.getEnterNumber());
                    enterOperation.setCreateTime(storeOrder.getUpdateTime());
                    operationList.add(enterOperation);
                }
            }
        }

        operationList.sort(
                Comparator.comparing(SalYtOrderSubItemOperation::getCreateTime, Comparator.nullsLast(Comparator.naturalOrder())).reversed()
        );
        return operationList;
    }

    /**
     * 删除暂存的采购单（逻辑删除），同时恢复关联的申购记录
     */
    @Transactional
    public void deletePurchase(Long purchaseId) {
        PurYtPurchase purchase = purYtPurchaseMapper.selectById(purchaseId);
        if (purchase == null) {
            throw new BizException(ExceptionCodeEnum.Not_Exists);
        }
        // 恢复关联的申购记录，并删除采购单明细
        List<PurYtPurchaseItem> items = purYtPurchaseItemMapper.listByPurchaseId(purchaseId);
        if (items != null) {
            for (PurYtPurchaseItem item : items) {
                purYtPurchaseItemMapper.deleteById(item.getId());
                Long applyPurchaseId = item.getApplyPurchaseId();
                if (applyPurchaseId != null) {
                    PurYtApplyPurchase purYtApplyPurchase = new PurYtApplyPurchase();
                    purYtApplyPurchase.setId(applyPurchaseId);
                    purYtApplyPurchase.setIsDeleted(0);
                    purYtApplyPurchaseMapper.updateById(purYtApplyPurchase);
                }
            }
        }
        // 逻辑删除采购单主表
        purYtPurchaseMapper.deleteById(purchaseId);
    }

    public void export(HttpServletResponse response, Long purchaseId) throws IOException {
//        PurYtPurchase purYtPurchase = purYtPurchaseMapper.selectById(purchaseId);
//        ServletOutputStream outputStream = null;
//        String fileName="采购单导出_" + System.currentTimeMillis() + ".xlsx";
//        String code = TenantInfoContext.getCurrentTenant().getCode();
//        String templatePath = "excel-template/" + code + "-purchaseExport.xlsx";
//        ClassPathResource resource = new ClassPathResource(templatePath);
//        Workbook workbook = new XSSFWorkbook(resource.getInputStream());
//        // 遍历所有工作表
//        org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);
//        // 遍历所有行
//        for (org.apache.poi.ss.usermodel.Row row : sheet) {
//            if (row != null) {
//                // 遍历所有单元格
//                for (org.apache.poi.ss.usermodel.Cell cell : row) {
//                    if (cell != null && cell.getCellType() == org.apache.poi.ss.usermodel.CellType.STRING) {
//                        QMYExcelUtil.replaceCellValue("{remark}", purYtPurchase.getRemark(), cell);
//                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//                        QMYExcelUtil.replaceCellValue("{deliveryTime}", purYtPurchase.getDeliveryTime().format(formatter), cell);
//                    }
//                }
//            }
//        }
//
//        HashMap<Long, PurYtPurchaseExport> exportMap=new HashMap<>();
//        List<PurYtPurchaseItem> purYtPurchaseItems = purYtPurchaseItemMapper.selectByPurchaseId(purchaseId);
//        Integer index=1;
//        for(PurYtPurchaseItem purYtPurchaseItem : purYtPurchaseItems){
//            Long productId = purYtPurchaseItem.getProductId();
//            PurYtPurchaseExport purYtPurchaseExport = exportMap.get(productId);
//            if(purYtPurchaseExport == null){
//                purYtPurchaseExport = new PurYtPurchaseExport();
//                purYtPurchaseExport.setIndex(index);
//                purYtPurchaseExport.setProductCode(purYtPurchaseItem.getProductCode());
//                List<ProYtProductFile> proYtProductFiles = proYtProductFileMapper.selectByMasterIdAndType(productId, ProductFilesTypeEnum.product.getKey());
//                purYtPurchaseExport.setImageUrl(proYtProductFiles.get(0).getUrl());
//                List<PurYtPurchaseItem> itemList=new ArrayList<>();
//                //处理半成品
//                checkIncomplete(purYtPurchaseItem);
//                if(purYtPurchaseItem.getNumber()==0){
//                    continue;
//                }
//                itemList.add(purYtPurchaseItem);
//                purYtPurchaseExport.setPurYtPurchaseItems(itemList);
//                exportMap.put(productId, purYtPurchaseExport);
//                index++;
//            }else {
//                List<PurYtPurchaseItem> itemList = purYtPurchaseExport.getPurYtPurchaseItems();
//                checkIncomplete(purYtPurchaseItem);
//                if(purYtPurchaseItem.getNumber()==0){
//                    continue;
//                }
//                itemList.add(purYtPurchaseItem);
//            }
//        }
//
//        //填充表格数据
//        Integer rowIndex=3;
//        for (Map.Entry<Long, PurYtPurchaseExport> entry : exportMap.entrySet()){
//
//            PurYtPurchaseExport value = entry.getValue();
//            Row row = sheet.createRow(rowIndex);
//            row.setHeight((short) 1000);
//            CellStyle cellStyle = workbook.createCellStyle();
//            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//            cellStyle.setAlignment(HorizontalAlignment.CENTER);
//
//            //序号
//            Cell cell0 = row.createCell(0);
//            cell0.setCellValue(value.getIndex());
//            cell0.setCellStyle(cellStyle);
//
//            //产品编号
//            Cell cell1 = row.createCell(1);
//            cell1.setCellValue(value.getProductCode());
//            cell1.setCellStyle(cellStyle);
//
//
//
//            //规格信息
//            List<PurYtPurchaseItem> itemList = value.getPurYtPurchaseItems();
//            Integer startIndex=rowIndex;
//            for (PurYtPurchaseItem item : itemList) {
//                Row speRow=null;
//                if(startIndex.equals(rowIndex)){
//                    speRow=row;
//                }else {
//                    speRow=sheet.createRow(startIndex);
//                    speRow.setHeight((short) 1000);
//                }
//                Long specificationId = item.getSpecificationId();
//                String specificationName;
//                if(specificationId==null){
//                    specificationName="半成品";
//                }else {
//                    List<ProYtProductSpecificationItem> itemsListBySpecification = proYtProductManager.getItemsListBySpecification(specificationId);
//                    List<String> list = itemsListBySpecification.stream().map(ProYtProductSpecificationItem::getCategorySpecificationItemValue).toList();
//                    specificationName=String.join(",", list);
//                }
//
//                //设置规格名称
//                Cell cell3 = speRow.createCell(3);
//                cell3.setCellValue(specificationName);
//                cell3.setCellStyle(cellStyle);
//
//                //设置数量
//                Cell cell4 = speRow.createCell(4);
//                cell4.setCellValue(item.getNumber());
//                cell4.setCellStyle(cellStyle);
//
//                //设置单价
//                Cell cell5 = speRow.createCell(5);
//                cell5.setCellValue(item.getSupplierPrice().toString());
//                cell5.setCellStyle(cellStyle);
//
//                //设置总价
//                Cell cell6 = speRow.createCell(6);
//                cell6.setCellValue(item.getSupplierPrice().multiply(new BigDecimal(item.getNumber())).toString());
//                cell6.setCellStyle(cellStyle);
//
//                //设置定制化属性
//                Cell cell7 = speRow.createCell(7);
//                cell7.setCellValue(item.getCategorySpecificationItemName());
//                cell7.setCellStyle(cellStyle);
//
//                //设置采购规格
//                Cell cell8 = speRow.createCell(8);
//                cell8.setCellValue(item.getRemark());
//                cell8.setCellStyle(cellStyle);
//
//                //设置供应商信息
//                Long supplierId = purYtPurchase.getSupplierId();
//                ProYtProductSpecificationSupplier specificationSupplier = proYtProductSpecificationSupplierMapper.selectBySpecificationIdAndSupplier(specificationId, supplierId);
//                if(specificationSupplier!=null){
//                    Cell cell9 = speRow.createCell(9);
//                    cell9.setCellValue(specificationSupplier.getSupplierSpecificationCode()+"/"+specificationSupplier.getSupplierSpecification());
//                    cell9.setCellStyle(cellStyle);
//                }
//
//                startIndex++;
//            }
//
//            //合并行
//            if(!startIndex.equals(rowIndex+1)){
//                CellRangeAddress mergedRegion = new CellRangeAddress(rowIndex, startIndex-1, 0, 0);
//                sheet.addMergedRegion(mergedRegion);
//                CellRangeAddress mergedRegion1 = new CellRangeAddress(rowIndex, startIndex-1, 1, 1);
//                sheet.addMergedRegion(mergedRegion1);
//                CellRangeAddress mergedRegion2 = new CellRangeAddress(rowIndex, startIndex-1, 2, 2);
//                sheet.addMergedRegion(mergedRegion2);
//            }
//            //图片
//            String imageUrl = value.getImageUrl();
//            Picture picture=null;
//            if (imageUrl != null && !imageUrl.isEmpty()) {
//                Drawing<?> drawing = sheet.createDrawingPatriarch();
//                // 使用insertImageToMulCell方法，支持跨多行的图片插入
//                // 参数：workbook, sheet, drawing, imgUrl, startRow, endRow, col, horizontalOffset, verticalOffset, imageWidthRatio
//                picture=QMYExcelUtil.insertImageToMulCell(workbook, sheet, drawing, imageUrl, rowIndex, startIndex-1, 2, 0.4, 0.2, 1.6);
//            }
//
//            rowIndex=startIndex;
//        }
//
//        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//        response.setHeader("Content-Disposition", "attachment; filename=" + java.net.URLEncoder.encode(fileName, "UTF-8"));
//
//        // 获取输出流
//        outputStream = response.getOutputStream();
//
//        // 输出到响应流
//        workbook.write(outputStream);
//        outputStream.flush();

        PurYtPurchase purYtPurchase = purYtPurchaseMapper.selectById(purchaseId);
        ServletOutputStream outputStream = null;
        String fileName = purYtPurchase.getSupplierName() + "_" + purYtPurchase.getCode() + ".xlsx";
        String code = TenantInfoContext.getCurrentTenant().getCode();
        String templatePath = "excel-template/" + code + "-purchaseExport.xlsx";
        ClassPathResource resource = new ClassPathResource(templatePath);
        Workbook workbook = new XSSFWorkbook(resource.getInputStream());
        // 遍历所有工作表
        org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);
        // 设置图片列（第2列）列宽为10个字符宽度
        sheet.setColumnWidth(2, 10 * 256);
        // 遍历所有行
        for (org.apache.poi.ss.usermodel.Row row : sheet) {
            if (row != null) {
                // 遍历所有单元格
                for (org.apache.poi.ss.usermodel.Cell cell : row) {
                    if (cell != null && cell.getCellType() == org.apache.poi.ss.usermodel.CellType.STRING) {
                        QMYExcelUtil.replaceCellValue("{remark}", purYtPurchase.getRemark(), cell);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        QMYExcelUtil.replaceCellValue("{deliveryTime}", purYtPurchase.getDeliveryTime().format(formatter), cell);
                    }
                }
            }
        }

        HashMap<Long, PurYtPurchaseExport> exportMap=new HashMap<>();
        List<PurYtPurchaseItem> purYtPurchaseItems = purYtPurchaseItemMapper.selectByPurchaseId(purchaseId);
        Integer index=1;
        for(PurYtPurchaseItem purYtPurchaseItem : purYtPurchaseItems){
            Long productId = purYtPurchaseItem.getProductId();
            PurYtPurchaseExport purYtPurchaseExport = exportMap.get(productId);
            if(purYtPurchaseExport == null){
                purYtPurchaseExport = new PurYtPurchaseExport();
                purYtPurchaseExport.setIndex(index);
                purYtPurchaseExport.setProductCode(purYtPurchaseItem.getProductCode());
                List<ProYtProductFile> proYtProductFiles = proYtProductFileMapper.selectByMasterIdAndType(productId, ProductFilesTypeEnum.product.getKey());
                purYtPurchaseExport.setImageUrl(proYtProductFiles.get(0).getUrl());
                List<PurYtPurchaseItem> itemList=new ArrayList<>();
                //处理半成品
                checkIncomplete(purYtPurchaseItem);
                if(purYtPurchaseItem.getNumber()==0){
                    continue;
                }
                itemList.add(purYtPurchaseItem);
                purYtPurchaseExport.setPurYtPurchaseItems(itemList);
                exportMap.put(productId, purYtPurchaseExport);
                index++;
            }else {
                List<PurYtPurchaseItem> itemList = purYtPurchaseExport.getPurYtPurchaseItems();
                checkIncomplete(purYtPurchaseItem);
                if(purYtPurchaseItem.getNumber()==0){
                    continue;
                }
                itemList.add(purYtPurchaseItem);
            }
        }

        //填充表格数据
        Integer rowIndex=3;
        int seqNo = 1;
        int totalQuantity = 0;
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (Map.Entry<Long, PurYtPurchaseExport> entry : exportMap.entrySet()){

            PurYtPurchaseExport value = entry.getValue();

            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyle.setAlignment(HorizontalAlignment.CENTER);

            //规格信息
            List<PurYtPurchaseItem> itemList = value.getPurYtPurchaseItems();
            for (PurYtPurchaseItem item : itemList) {
                Row speRow = sheet.createRow(rowIndex);
                speRow.setHeight((short) 1000);

                //序号（每行递增）
                Cell cell0 = speRow.createCell(0);
                cell0.setCellValue(seqNo++);
                cell0.setCellStyle(cellStyle);

                Long specificationId = item.getSpecificationId();
                String specificationName;
                if(specificationId==null){
                    specificationName="半成品";
                }else {
                    List<ProYtProductSpecificationItem> itemsListBySpecification = proYtProductManager.getItemsListBySpecification(specificationId);
                    List<String> list = itemsListBySpecification.stream().map(ProYtProductSpecificationItem::getCategorySpecificationItemValue).toList();
                    specificationName=String.join(",", list);
                }

                //设置供应商信息
                Long supplierId = purYtPurchase.getSupplierId();
                ProYtProductSpecificationSupplier specificationSupplier = proYtProductSpecificationSupplierMapper.selectBySpecificationIdAndSupplier(specificationId, supplierId);
                if(specificationSupplier!=null){
                    //第1列：供应商规格代码
                    Cell cell1 = speRow.createCell(1);
                    cell1.setCellValue(specificationSupplier.getSupplierSpecificationCode());
                    cell1.setCellStyle(cellStyle);

                    //第3列：供应商规格名称
                    Cell cell3 = speRow.createCell(3);
                    cell3.setCellValue(specificationSupplier.getSupplierSpecification());
                    cell3.setCellStyle(cellStyle);
                }

                //设置数量
                int number = item.getNumber();
                Cell cell4 = speRow.createCell(4);
                cell4.setCellValue(number);
                cell4.setCellStyle(cellStyle);
                totalQuantity += number;

                //设置单价
                Cell cell5 = speRow.createCell(5);
                cell5.setCellValue(item.getSupplierPrice().toString());
                cell5.setCellStyle(cellStyle);

                //设置总价
                BigDecimal rowTotal = item.getSupplierPrice().multiply(new BigDecimal(number));
                Cell cell6 = speRow.createCell(6);
                cell6.setCellValue(rowTotal.toString());
                cell6.setCellStyle(cellStyle);
                totalPrice = totalPrice.add(rowTotal);

                //设置定制化属性
                Cell cell7 = speRow.createCell(7);
                cell7.setCellValue(item.getCategorySpecificationItemName());
                cell7.setCellStyle(cellStyle);

                //第8列：采购规格
                Cell cell8 = speRow.createCell(8);
                cell8.setCellValue(item.getRemark());
                cell8.setCellStyle(cellStyle);

                //第9列：规格名称 + "/" + 产品编号
                Cell cell9 = speRow.createCell(9);
                cell9.setCellValue(value.getProductCode() + "/" + specificationName);
                cell9.setCellStyle(cellStyle);

                //第2列：规格图片（每行独立）
                if(specificationId != null){
                    List<ProYtProductFile> specFiles = proYtProductManager.getFileListBySpecification(specificationId);
                    if(specFiles != null && !specFiles.isEmpty()){
                        String specImageUrl = specFiles.get(0).getUrl();
                        if(specImageUrl != null && !specImageUrl.isEmpty()){
                            Drawing<?> drawing = sheet.createDrawingPatriarch();
                            Picture pic = QMYExcelUtil.insertImageToMulCell(workbook, sheet, drawing, specImageUrl, rowIndex, rowIndex, 2, 0.4, 0.2, 1.6);
                            if(pic != null){
                                pic.getClientAnchor().setAnchorType(ClientAnchor.AnchorType.DONT_MOVE_AND_RESIZE);
                            }
                        }
                    }
                }

                rowIndex++;
            }
        }

        //总计行
        CellStyle totalCellStyle = workbook.createCellStyle();
        totalCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        totalCellStyle.setAlignment(HorizontalAlignment.CENTER);
        Font boldFont = workbook.createFont();
        boldFont.setBold(true);
        totalCellStyle.setFont(boldFont);

        Row totalRow = sheet.createRow(rowIndex);
        Cell totalLabelCell = totalRow.createCell(0);
        totalLabelCell.setCellValue("总计");
        totalLabelCell.setCellStyle(totalCellStyle);

        Cell totalQuantityCell = totalRow.createCell(4);
        totalQuantityCell.setCellValue(totalQuantity);
        totalQuantityCell.setCellStyle(totalCellStyle);

        Cell totalPriceCell = totalRow.createCell(6);
        totalPriceCell.setCellValue(totalPrice.toString());
        totalPriceCell.setCellStyle(totalCellStyle);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + java.net.URLEncoder.encode(fileName, "UTF-8"));

        // 获取输出流
        outputStream = response.getOutputStream();

        // 输出到响应流
        workbook.write(outputStream);
        outputStream.flush();
    }

    private void checkIncomplete(PurYtPurchaseItem purYtPurchaseItem) {
        Long specificationId = purYtPurchaseItem.getSpecificationId();
        if(specificationId != null) {
            //不是半成品，不用处理，直接返回
            return;
        }
        //是半成品，需要处理是否已经全部确认
        Long id = purYtPurchaseItem.getId();
        List<PurYtPurchaseItem> purYtPurchaseItems = purYtPurchaseItemMapper.selectByConfirmId(id);
        if(purYtPurchaseItems==null){
            //未确认过，直接返回
            return;
        }
        Integer sumConfirm = purYtPurchaseItems.stream().mapToInt(PurYtPurchaseItem::getNumber).sum();
        Integer number = purYtPurchaseItem.getNumber();
        if(sumConfirm<number){
            purYtPurchaseItem.setNumber(number-sumConfirm);
        }else {
            purYtPurchaseItem.setNumber(0);
        }
    }

    public Object purchasePaymentList(FinYtPaymentQueryParams params) {
        PageHelper.startPage(params.getPageNum(), params.getPageSize());

        // 将财务付款查询参数转换为采购订单查询参数
        PurYtPurchaseQueryParams purchaseParams = new PurYtPurchaseQueryParams();
        purchaseParams.setCode(params.getPurchaseCode()); // 采购单号
        purchaseParams.setSupplierName(params.getSupplierName()); // 供应商名称
        purchaseParams.setOrderSubCode(params.getOrderSubCode()); // 子订单号
        purchaseParams.setDeliveryEndTime(params.getDeliveryEndTime());
        purchaseParams.setDeliveryStartTime(params.getDeliveryStartTime());
        //设置不出现暂存的采购单
        purchaseParams.setMinStatus("0");
        // 查询采购订单列表
        List<PurYtPurchase> purchaseList = purYtPurchaseMapper.list(purchaseParams);

        // 计算每个采购订单的已付款金额
        for (PurYtPurchase purchase : purchaseList) {
            //采购总价
            BigDecimal totalAmount = purYtPurchaseItemMapper.calculateTotalAmountByPurchaseId(purchase.getId());
            if(totalAmount != null){
                BigDecimal amount = totalAmount.subtract(purchase.getDiscountAmount()).add(purchase.getShippingCost());
                purchase.setTotalAmount(amount);
            }
            // 已付款金额
            BigDecimal totalPayment = purYtPurchasePaymentMapper.getTotalPaymentByPurchaseId(purchase.getId());
            purchase.setTotalPaymentAmount(totalPayment);
        }

        return new PageResultInfo<>(purchaseList);
    }

    /**
     * 采购单确认付款
     * @param params 采购单确认付款请求参数
     */
    public void confirmPurchasePayment(PurYtPurchasePayment params) {
        // 保存付款记录
        purYtPurchasePaymentMapper.insert(params);
    }

    public List<PurYtPurchasePayment> purchasePaymentDetail(FinYtPaymentQueryParams params) {
        List<PurYtPurchasePayment> list = purYtPurchasePaymentMapper.list(params);
        for(PurYtPurchasePayment payment : list){
            Long id = payment.getId();
            List<ProYtProductFile> proYtProductFiles = proYtProductFileMapper.selectByMasterIdAndType(id, ProductFilesTypeEnum.PurchasePaymentFile.getKey());
            payment.setFileList(proYtProductFiles);
        }
        return list;
    }
}
