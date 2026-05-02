package com.qiaomoyun.manager.sal.yt;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.qiaomoyun.Exception.BizException;
import com.qiaomoyun.Exception.ExceptionCodeEnum;
import com.qiaomoyun.entity.pro.yt.*;
import com.qiaomoyun.entity.pur.yt.PurYtPurchase;
import com.qiaomoyun.entity.pur.yt.PurYtPurchaseItem;
import com.qiaomoyun.entity.pur.yt.PurYtSupplier;
import com.qiaomoyun.entity.sal.sed.SalSedOrder;
import com.qiaomoyun.entity.sal.sed.SalSedQuotation;
import com.qiaomoyun.entity.sal.yt.*;
import com.qiaomoyun.entity.sto.yt.*;
import com.qiaomoyun.entity.sys.SysDictionary;
import com.qiaomoyun.entity.sys.SysStorage;
import com.qiaomoyun.entity.sys.SysUser;
import com.qiaomoyun.eunm.sys.DictionaryConfigEnum;
import com.qiaomoyun.eunm.sys.TenantConfigEnum;
import com.qiaomoyun.eunm.yt.*;
import com.qiaomoyun.event.yt.DeliveryEvent;
import com.qiaomoyun.event.yt.PurchaseEvent;
import com.qiaomoyun.event.yt.StoreChangeEvent;
import com.qiaomoyun.manager.pro.yt.ProYtProductManager;
import com.qiaomoyun.manager.pur.yt.PurYtPurchaseManager;
import com.qiaomoyun.manager.sto.yt.StoYtDeliveryManager;
import com.qiaomoyun.manager.sto.yt.StoYtStoreManager;
import com.qiaomoyun.manager.sto.yt.StoYtStoreOrderManager;
import com.qiaomoyun.manager.sto.yt.StoYtStoreRecordManager;
import com.qiaomoyun.manager.sys.SysDictionaryManager;
import com.qiaomoyun.mapper.pur.yt.PurYtSupplierMapper;
import com.qiaomoyun.mapper.sal.yt.*;
import com.qiaomoyun.mapper.pro.yt.*;
import com.qiaomoyun.mapper.pur.yt.PurYtApplyPurchaseMapper;
import com.qiaomoyun.mapper.pur.yt.PurYtPurchaseItemMapper;
import com.qiaomoyun.mapper.pur.yt.PurYtPurchaseMapper;
import com.qiaomoyun.mapper.sto.yt.*;
import com.qiaomoyun.mapper.sys.SysDictionaryMapper;
import com.qiaomoyun.mapper.sys.SysStorageMapper;
import com.qiaomoyun.mapper.sys.SysTenantConfigMapper;
import com.qiaomoyun.mapper.sys.SysUserMapper;
import com.qiaomoyun.param.fin.yt.FinYtPaymentUpdateParams;
import com.qiaomoyun.param.fin.yt.FinYtProfitQueryParams;
import com.qiaomoyun.param.fin.yt.FinYtReceiveQueryParams;
import com.qiaomoyun.param.sal.yt.*;
import com.qiaomoyun.param.sto.yt.StoYtStoreOrderAddParams;
import com.qiaomoyun.util.*;
import com.qiaomoyun.vo.fin.yt.CustomerProductProfitVo;
import com.qiaomoyun.vo.fin.yt.FinYtCustomerProfitVo;
import com.qiaomoyun.vo.fin.yt.FinYtOrderProfitProductVo;
import com.qiaomoyun.vo.fin.yt.FinYtProfitOrderProfitListVo;
import com.qiaomoyun.vo.sal.yt.*;
import com.qiaomoyun.info.PageResultInfo;
import com.qiaomoyun.manager.feishu.FeiShuManager;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.qiaomoyun.entity.sal.yt.SalYtOrder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 订单管理类
 */
@Component
public class SalYtOrderManager {

    @Autowired
    private SalYtOrderMapper salYtOrderMapper;

    @Autowired
    private SalYtOrderSubMapper salYtOrderSubMapper;

    @Autowired
    private SalYtOrderSubItemMapper salYtOrderSubItemMapper;
    @Autowired
    private SalYtCustomerAddressMapper salYtCustomerAddressMapper;
    @Autowired
    private FeiShuManager feiShuManager;
    @Autowired
    private SalYtCustomerStoreMapper salYtCustomerStoreMapper;
    @Autowired
    private ProYtProductFileMapper proYtProductFileMapper;
    @Autowired
    private ProYtProductSpecificationItemMapper proYtProductSpecificationItemMapper;
    @Autowired
    private PurYtPurchaseManager purYtPurchaseManager;
    @Autowired
    private SalYtCustomerMapper salYtCustomerMapper;
    @Autowired
    private StoYtStoreManager stoYtStoreManager;
    @Autowired
    private SalYtOrderSubItemOperationManager salYtOrderSubItemOperationManager;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SalYtCustomerSpecificationComparisonMapper salYtCustomerSpecificationComparisonMapper;
    @Autowired
    private ProYtProductMapper proYtProductMapper;
    @Autowired
    private ProYtProductSpecificationMapper proYtProductSpecificationMapper;
    @Autowired
    private ProYtProductSpecificationSupplierMapper proYtProductSpecificationSupplierMapper;
    @Autowired
    private SalYtReturnOrderMapper salYtReturnOrderMapper;
    @Autowired
    private PurYtApplyPurchaseMapper purYtApplyPurchaseMapper;
    @Autowired
    private PurYtPurchaseItemMapper purYtPurchaseItemMapper;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private StoYtDeliveryItemMapper stoYtDeliveryItemMapper;
    @Autowired
    private StoYtDeliveryMapper stoYtDeliveryMapper;
    @Autowired
    private StoYtDeliveryBoxMapper stoYtDeliveryBoxMapper;
    @Autowired
    private PurYtSupplierMapper purYtSupplierMapper;
    @Autowired
    private StoYtDeliveryBoxItemMapper stoYtDeliveryBoxItemMapper;
    @Autowired
    private SysDictionaryManager sysDictionaryManager;
    @Autowired
    private StoYtBoxMapper stoYtBoxMapper;
    @Autowired
    private ProYtProductManager proYtProductManager;
    @Autowired
    private SalYtOrderSubReceiveMapper salYtOrderSubReceiveMapper;
    @Autowired
    private SysDictionaryMapper sysDictionaryMapper;
    @Autowired
    private PurYtPurchaseMapper purYtPurchaseMapper;

    @Autowired
    private StoYtStoreOrderMapper stoYtStoreOrderMapper;

    @Autowired
    private StoYtStoreOrderOperationMapper stoYtStoreOrderOperationMapper;

    @Autowired
    private SysTenantConfigMapper sysTenantConfigMapper;
    @Autowired
    private SalYtOrderSubItemConfirmMapper salYtOrderSubItemConfirmMapper;

    @Autowired
    private SysStorageMapper sysStorageMapper;

    @Autowired
    private StoYtDeliveryManager stoYtDeliveryManager;

    @Autowired
    private SalYtDailyStatisRecordMapper salYtDailyStatisRecordMapper;

    @Autowired
    private StoYtTransportCompanyMapper stoYtTransportCompanyMapper;

    /**
     * 新增或编辑订单
     *
     * @return 操作结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Long saveOrUpdateOrder(SalYtOrderUpdateParams params) {
        boolean result = false;
        Long orderId = params.getId();

        SalYtOrder salYtOrder = new SalYtOrder();
        BeanUtils.copyProperties(params, salYtOrder);

        //处理客户地址
        Long customerAddressId = salYtOrder.getCustomerAddressId();
        SalYtCustomerAddress salYtCustomerAddress = salYtCustomerAddressMapper.selectById(customerAddressId);
        StringBuffer address = new StringBuffer();
        String province = salYtCustomerAddress.getProvince().equals("0")?"":salYtCustomerAddress.getProvince();
        String city=salYtCustomerAddress.getCity().equals("0")?"":salYtCustomerAddress.getCity();
        String county=salYtCustomerAddress.getCounty().equals("0")?"":salYtCustomerAddress.getCounty();
        address.append(salYtCustomerAddress.getCountryRegion()).append(" ")
                .append(province).append(city).append(county)
                .append(salYtCustomerAddress.getDetail());

        salYtOrder.setReceiver(salYtCustomerAddress.getConsignee());
        salYtOrder.setReceiverPhone(salYtCustomerAddress.getPhone());
        salYtOrder.setCustomerAddress(address.toString());
        //设置汇率
        //查询转换率
        List<SysDictionary> sysDictionaries = sysDictionaryMapper.selectByCode("exchangeRate");
        //美元转人民币汇率
        BigDecimal exchangeRate = new BigDecimal(sysDictionaries.get(0).getValue());
        salYtOrder.setExchangeRate(exchangeRate);
        //订单币种
        String currency = params.getCurrency();
        if (orderId == null) {
            // 新增订单
            String code = EntityCodeGenerateUtil.generateUniqueId("D");
            salYtOrder.setCode(code);
            //客户运费回款状态
              //判断是否已收运费
            if(params.getIsCollectedShippingCost().equals(1)) {
                //收费了就已完成
                salYtOrder.setShippingReceiveStatus(ReceiveStatusEnum.Completed.getKey());
                //不插入时间是因为订单还没有发货完毕
                //salYtOrder.setShippingReceiveFinishTime(LocalDateTime.now());
            }else {
                //未收费就未完成
                salYtOrder.setShippingReceiveStatus(ReceiveStatusEnum.WaitReceive.getKey());
            }
            result = salYtOrderMapper.insert(salYtOrder) > 0;
            orderId = salYtOrder.getId();
        } else {
            // 编辑订单
            //订单不是暂存以下的状态不允许编辑
            SalYtOrder old = salYtOrderMapper.selectById(orderId);
            if (old.getStatus() > OrderStatusEnum.Saved.getKey()) {
                throw new BizException(ExceptionCodeEnum.Order_Status_Error.getCode(), ExceptionCodeEnum.Order_Status_Error.getValue());
            }
            result = salYtOrderMapper.updateById(salYtOrder) > 0;
        }

        List<SalYtOrderSub> orderSubList = params.getOrderSubList();

        // 保存子订单数据
        if (result && orderSubList != null && !orderSubList.isEmpty()) {
            for (SalYtOrderSub orderSub : orderSubList) {
                orderSub.setOrderId(orderId);
                orderSub.setSubCode(EntityCodeGenerateUtil.generateUniqueId("D"));
                if (orderSub.getId() == null) {
                    salYtOrderSubMapper.insert(orderSub);
                } else {
                    salYtOrderSubMapper.updateById(orderSub);
                }
                // 保存子订单的商品项
                List<SalYtOrderSubItem> itemList = orderSub.getItemList();
                if (itemList != null && !itemList.isEmpty()) {
                    for (SalYtOrderSubItem item : itemList) {
                        item.setOrderSubId(orderSub.getId());
                        //item.setStatus(OrderSubItemStatusEnum.WaitPurchase.getKey());
                        //暂存的订单应该显示未暂存
                        item.setStatus(OrderSubItemStatusEnum.Draft.getKey());
                        Long specificationId = item.getSpecificationId();
                        if (specificationId == null) {
                            //半成品供应商id和单价使用产品默认供应商和单价
                            Long productId = item.getProductId();
                            ProYtProduct proYtProduct = proYtProductMapper.selectById(productId);
                            item.setSupplierId(proYtProduct.getDefaultSupplierId());
                        }
                        //添加本位币
                          if(currency.equals(CurrencyEnum.dollar.getValue())){
                              //当订单是美元时，本位币为换算后的人民币金额
                              item.setBasePrice(item.getPrice().multiply(exchangeRate));
                          }else{
                              item.setBasePrice(item.getPrice());
                          }
                        if (item.getId() == null) {
                            salYtOrderSubItemMapper.insert(item);
                        } else {
                            salYtOrderSubItemMapper.updateById(item);
                        }
                    }
                }
            }
        }

        return orderId;
    }

    /**
     * 查询订单详情（包含子订单和按productId分组的商品项）
     *
     * @param id 订单ID
     * @return SalYtOrderVo 订单详情对象
     */
    public SalYtOrderVo getOrderDetailWithProducts(Long id) {
        // 查询订单主表
        SalYtOrder order = salYtOrderMapper.selectById(id);
        if (order == null || order.getIsDeleted() == 1) {
            return null;
        }

        // 创建订单Vo对象并复制基本信息
        SalYtOrderVo orderVo = new SalYtOrderVo();
        BeanUtils.copyProperties(order, orderVo);

        List<SalYtOrderSub> subList = salYtOrderSubMapper.selectSalYtOrderSubByOrderId(orderVo.getId());

        // 构建子订单列表
        for (SalYtOrderSub subOrder : subList) {
            // 查询子订单的商品项
            List<SalYtOrderSubItem> itemList = salYtOrderSubItemMapper.selectSalYtOrderSubItemByOrderSubId(subOrder.getId());


            for (SalYtOrderSubItem item : itemList) {
                // 判断是否是半成品或独立仓产品
                item.setIsIncompleteProduct(false);
                item.setIsCustomerStore(false);
                Long specificationId = item.getSpecificationId();
                if (specificationId == null) {
                    item.setIsIncompleteProduct(true);
                    List<ProYtProductFile> proYtProductFiles = proYtProductFileMapper.selectByMasterIdAndType(item.getProductId(), ProductFilesTypeEnum.product.getKey());
                    item.setImageList(proYtProductFiles);
                } else {
                    Long customerId = order.getCustomerId();
                    SalYtCustomerStore salYtCustomerStore = salYtCustomerStoreMapper.selectByCustomerIdAndSpecificationId(customerId, specificationId);
                    if (salYtCustomerStore != null) {
                        item.setIsCustomerStore(true);
                    }
                    //填充供应商最小起订量
                    ProYtProductSpecificationSupplier specificationSupplier = proYtProductSpecificationSupplierMapper.selectBySpecificationIdAndSupplier(specificationId, item.getSupplierId());
                    item.setMinNumber(specificationSupplier.getMinNumber());

                    //填充规格图片
                    List<ProYtProductFile> proYtProductFiles = proYtProductFileMapper.selectByMasterIdAndType(specificationId, ProductFilesTypeEnum.specification.getKey());
                    item.setImageList(proYtProductFiles);

                    //填充规格项
                    List<ProYtProductSpecificationItem> specificationItems = proYtProductSpecificationItemMapper.selectByProductSpecificationId(specificationId);
                    item.setItemList(specificationItems);

                    //计算可用库存、可用在途
                    StoYtStore stoYtStore = stoYtStoreManager.selectStockBySpecificationId(specificationId);
                    item.setEnabledStore(stoYtStore.getEnableStore());
                    item.setEnabledTransit(stoYtStore.getEnableTransit());
                }

            }

            subOrder.setItemList(itemList);
        }

        orderVo.setSubOrderList(subList);
        return orderVo;
    }


    /**
     * 查询订单列表
     *
     * @param params 查询参数
     * @return 分页订单列表
     */
    public Object selectOrderPage(SalYtOrderQueryParams params) {
        if (params.needPaging()) {
            PageHelper.startPage(params.getPageNum(), params.getPageSize());
        }
        List<SalYtOrderVo> list = salYtOrderMapper.selectSalYtOrderList(params);
        fillOrderStatusForPage(list);

        return new PageResultInfo<>(list);
    }

    /**
     * 批量补充分页订单的展示状态，避免按订单逐条查询子订单和子项。
     */
    private void fillOrderStatusForPage(List<SalYtOrderVo> orderList) {
        if (orderList == null || orderList.isEmpty()) {
            return;
        }

        List<Long> orderIds = orderList.stream()
                .map(SalYtOrderVo::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (orderIds.isEmpty()) {
            return;
        }

        LambdaQueryWrapper<SalYtOrderSub> subQueryWrapper = Wrappers.lambdaQuery();
        subQueryWrapper.in(SalYtOrderSub::getOrderId, orderIds)
                .eq(SalYtOrderSub::getIsDeleted, 0);
        List<SalYtOrderSub> subOrderList = salYtOrderSubMapper.selectList(subQueryWrapper);

        Map<Long, List<SalYtOrderSub>> subOrdersByOrderId = subOrderList.stream()
                .collect(Collectors.groupingBy(SalYtOrderSub::getOrderId));
        Map<Long, Long> subOrderToOrderIdMap = subOrderList.stream()
                .collect(Collectors.toMap(SalYtOrderSub::getId, SalYtOrderSub::getOrderId));

        Map<Long, List<SalYtOrderSubItem>> itemListBySubOrderId = new HashMap<>();
        Map<Long, String> orderMinStatusMap = new HashMap<>();

        if (!subOrderList.isEmpty()) {
            List<Long> subOrderIds = subOrderList.stream()
                    .map(SalYtOrderSub::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            List<SalYtOrderSubItem> allItemList = salYtOrderSubItemMapper.selectSalYtOrderSubItemByOrderSubIds(subOrderIds);

            itemListBySubOrderId = allItemList.stream()
                    .collect(Collectors.groupingBy(SalYtOrderSubItem::getOrderSubId));

            for (SalYtOrderSubItem item : allItemList) {
                Long orderId = subOrderToOrderIdMap.get(item.getOrderSubId());
                if (orderId == null || item.getStatus() == null) {
                    continue;
                }
                orderMinStatusMap.merge(orderId, item.getStatus(), this::getLowerStatus);
            }
        }

        for (SalYtOrderVo order : orderList) {
            // 给关闭订单总金额减去关闭金额和额外关闭金额
            BigDecimal amount = order.getAmount() == null ? BigDecimal.ZERO : order.getAmount();
            if (order.getEndAmount() == null) {
                order.setEndAmount(BigDecimal.ZERO);
            }
            if (order.getEndOtherAmount() == null) {
                order.setEndOtherAmount(BigDecimal.ZERO);
            }
            if (order.getDiscountAmount() == null) {
                order.setDiscountAmount(BigDecimal.ZERO);
            }
            order.setAmount(amount.subtract(order.getEndAmount()).subtract(order.getEndOtherAmount()).subtract(order.getDiscountAmount()));

            String minStatus = calculatePageOrderStatus(
                    subOrdersByOrderId.getOrDefault(order.getId(), Collections.emptyList()),
                    itemListBySubOrderId,
                    orderMinStatusMap.get(order.getId())
            );
            if (minStatus != null) {
                order.setOrderStatus(minStatus);
            }
        }
    }

    /**
     * 复用原列表页状态判断规则，在内存中计算当前订单的展示状态。
     */
    private String calculatePageOrderStatus(
            List<SalYtOrderSub> subOrderList,
            Map<Long, List<SalYtOrderSubItem>> itemListBySubOrderId,
            String initMinStatus
    ) {
        String minStatus = initMinStatus;
        for (SalYtOrderSub subOrder : subOrderList) {
            List<SalYtOrderSubItem> itemList = itemListBySubOrderId.getOrDefault(subOrder.getId(), Collections.emptyList());
            if ("1".equals(subOrder.getOrderType()) && minStatus != null) {
                boolean isAllConfirm = true;
                // 半成品单在已采购后，需要判断未绑定规格项是否已全部确认
                for (SalYtOrderSubItem item : itemList) {
                    if (item.getSpecificationId() == null && !validItemConfirm(item, itemList)) {
                        isAllConfirm = false;
                        break;
                    }
                }
                if (isAllConfirm) {
                    minStatus = getMinStatus(itemList.stream()
                            .filter(item -> item.getSpecificationId() != null && item.getStatus() != null)
                            .map(SalYtOrderSubItem::getStatus)
                            .collect(Collectors.toList()));
                } else if (Integer.parseInt(minStatus) > Integer.parseInt(OrderSubItemStatusEnum.WaitPurchase.getKey())) {
                    minStatus = OrderSubItemStatusEnum.WaitConfirm.getKey();
                }
            }
        }
        return minStatus;
    }

    private String getLowerStatus(String currentStatus, String nextStatus) {
        if (currentStatus == null) {
            return nextStatus;
        }
        if (nextStatus == null) {
            return currentStatus;
        }
        return Integer.parseInt(currentStatus) <= Integer.parseInt(nextStatus) ? currentStatus : nextStatus;
    }

    /**
     * 查询订单详情
     *
     * @param id 订单ID
     * @return 订单信息（包含子表）
     */
    public Map<String, Object> selectOrderDetail(Long id) {
        Map<String, Object> result = new HashMap<>();

        // 查询订单主表
        SalYtOrder order = salYtOrderMapper.selectById(id);
        if (order == null || order.getIsDeleted() == 1) {
            return null;
        }

        result.put("order", order);

        // 查询订单子表
        QueryWrapper<SalYtOrderSub> subWrapper = new QueryWrapper<>();
        subWrapper.eq("order_id", id);
        List<SalYtOrderSub> subList = salYtOrderSubMapper.selectList(subWrapper);

        // 为每个子订单计算总金额和最小状态
        for (SalYtOrderSub subOrder : subList) {
            // 使用SQL查询获取子订单商品总金额
            BigDecimal productTotal = salYtOrderSubMapper.calculateOrderSubAmount(subOrder.getId());
            if (productTotal == null) {
                productTotal = BigDecimal.ZERO;
            }

            // 添加运费（如果有）
            BigDecimal totalAmount = productTotal;
            if (subOrder.getIsCollectedShippingCost() == 1 && subOrder.getShippingCost() != null) {
                totalAmount = totalAmount.add(subOrder.getShippingCost());
            }

            // 设置子订单总金额
            subOrder.setAmount(totalAmount);

            Long subOrderId = subOrder.getId();
            // 获取子订单item数据
            QueryWrapper<SalYtOrderSubItem> itemWrapper = new QueryWrapper<>();
            itemWrapper.eq("order_sub_id", subOrderId);
            itemWrapper.eq("is_deleted", 0);
            List<SalYtOrderSubItem> itemList = salYtOrderSubItemMapper.selectList(itemWrapper);
            // 使用SQL查询获取子订单商品项的最小状态
            String minStatus = salYtOrderSubMapper.selectMinStatusByOrderSubId(subOrderId);
            if (subOrder.getOrderType().equals("1") && minStatus != null) {
                Boolean isAllConfirm = true;
                //半成品单，并且已经采购，则要确认产品是否已经全部确认
                for (SalYtOrderSubItem item : itemList) {
                    if (item.getSpecificationId() == null) {
                        if (!validItemConfirm(item, itemList)) {
                            isAllConfirm = false;
                            break;
                        }
                    }
                }
                //如果全部确认
                if(isAllConfirm){
                    //只算有规格的最小状态
                    minStatus = salYtOrderSubMapper.selectConfirmedItemMinStatusByOrderSubId(subOrderId);
                }else {
                    //如果还有待确认的
                    Integer status = Integer.parseInt(minStatus);
                    if(status>Integer.parseInt(OrderSubItemStatusEnum.WaitPurchase.getKey())){
                        minStatus = OrderSubItemStatusEnum.WaitConfirm.getKey();
                    }
                }

            }
            if(minStatus!=null){
                subOrder.setSubStatus(minStatus);
            }

//            // 将商品项列表设置到子订单中
//            subOrder.setItemList(itemList);
        }

        result.put("orderSubList", subList);

        return result;
    }


    public SalYtOrderVo getSubDetail(Long subId) {
        // 获取子订单主表数据
        SalYtOrderSub orderSub = salYtOrderSubMapper.selectById(subId);
        if (orderSub == null) {
            throw new BizException(ExceptionCodeEnum.Not_Exists);
        }

        // 获取订单主表数据
        Long orderId = orderSub.getOrderId();
        SalYtOrderVo order = salYtOrderMapper.detail(orderId);
        if (order == null) {
            throw new BizException(ExceptionCodeEnum.Not_Exists);
        }

        // 设置子订单数据到Vo
        order.setSubOrder(orderSub);

        // 获取子订单item数据
        QueryWrapper<SalYtOrderSubItem> itemWrapper = new QueryWrapper<>();
        itemWrapper.eq("order_sub_id", subId);
        itemWrapper.eq("is_deleted", 0);
        List<SalYtOrderSubItem> itemList = salYtOrderSubItemMapper.selectList(itemWrapper);

        // 将item数据设置到子订单中
        orderSub.setItemList(itemList);

        //设置订单总额
        BigDecimal totalAmount = salYtOrderSubMapper.calculateOrderSubAmount(subId);
        if (orderSub.getIsCollectedShippingCost() == 1) {
            // 总金额=商品总额+运费-优惠金额
            totalAmount = totalAmount.add(orderSub.getShippingCost());
        }
        totalAmount = totalAmount.subtract(orderSub.getDiscountAmount());
        orderSub.setAmount(totalAmount);

        // 获取子订单最小状态
        String minStatus = salYtOrderSubMapper.selectMinStatusByOrderSubId(subId);
        if (orderSub.getOrderType().equals("1") && minStatus != null) {
            Boolean isAllConfirm = true;
            //半成品单，并且已经采购，则要确认产品是否已经全部确认
            for (SalYtOrderSubItem item : itemList) {
                if (item.getSpecificationId() == null) {
                    if (!validItemConfirm(item, itemList)) {
                        isAllConfirm = false;
                        break;
                    }
                }
            }
            //如果全部确认
            if(isAllConfirm){
                //只算有规格的最小状态
                minStatus = salYtOrderSubMapper.selectConfirmedItemMinStatusByOrderSubId(subId);
            }else {
                //如果还有待确认的
                Integer status = Integer.parseInt(minStatus);
                if(status>Integer.parseInt(OrderSubItemStatusEnum.WaitPurchase.getKey())){
                    minStatus = OrderSubItemStatusEnum.WaitConfirm.getKey();
                }
            }

        }
        orderSub.setSubStatus(minStatus);

        //设置预计成本价和毛利
        BigDecimal costAmount = BigDecimal.ZERO;
        for (SalYtOrderSubItem item : orderSub.getItemList()) {
            Integer number = item.getNumber();
            BigDecimal supplierPrice = item.getSupplierPrice();
            costAmount = costAmount.add(new BigDecimal(number).multiply(supplierPrice));

            Long specificationId = item.getSpecificationId();
            //填充规格图片
            List<ProYtProductFile> proYtProductFiles = proYtProductFileMapper.selectByMasterIdAndType(specificationId, ProductFilesTypeEnum.specification.getKey());
            item.setImageList(proYtProductFiles);

            //填充规格项
            List<ProYtProductSpecificationItem> specificationItems = proYtProductSpecificationItemMapper.selectByProductSpecificationId(specificationId);
            item.setItemList(specificationItems);

            //填充产品code
            Long productId = item.getProductId();
            ProYtProduct proYtProduct = proYtProductMapper.selectById(productId);
            item.setProductCode(proYtProduct.getCode());
        }

        BigDecimal profitAmount = totalAmount.subtract(costAmount);
        orderSub.setCostAmount(costAmount);
        orderSub.setProfitAmount(profitAmount);

        // 统计item不同状态的数量
        Map<String, Integer> itemStatusCountMap = new HashMap<>();
        for (SalYtOrderSubItem item : itemList) {
            if (item.getSpecificationId() != null) {
                String status = item.getStatus();
                itemStatusCountMap.put(status, itemStatusCountMap.getOrDefault(status, 0) + 1);
            }
        }
        orderSub.setStatusCountMap(itemStatusCountMap);


        //判断是否是美元，是就是汇率转换
        if(order.getCurrency().equals("1")) {
            //查询转换率
            List<SysDictionary> sysDictionaries = sysDictionaryMapper.selectByCode("exchangeRate");
            //美元转人民币汇率
            BigDecimal exchangeRate = new BigDecimal(sysDictionaries.get(0).getValue());

            //毛利
            BigDecimal profitAmount1 = orderSub.getAmount().multiply(exchangeRate).subtract(orderSub.getCostAmount());
            //预计毛利
            orderSub.setProfitAmount(profitAmount1);

        }



        return order;
    }

    private Boolean validItemConfirm(SalYtOrderSubItem item, List<SalYtOrderSubItem> itemList) {
        Long specificationId = item.getSpecificationId();
        if (specificationId == null) {
            //说明是半成品，确认是否已经全部确认
            Integer confirmNumber = 0;
            Long id = item.getId();
            for (SalYtOrderSubItem i : itemList) {
                Long confirmItemId = i.getConfirmItemId();
                if (confirmItemId != null && confirmItemId.equals(id)) {
                    confirmNumber += i.getNumber();
                }
            }
            if (confirmNumber.equals(item.getNumber())) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * 删除订单
     *
     * @param id 订单ID
     * @return 操作结果
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteOrder(Long id) {
        // 逻辑删除订单主表
        SalYtOrder order = new SalYtOrder();
        order.setId(id);
        order.setIsDeleted(1);
        boolean result = salYtOrderMapper.updateById(order) > 0;

        // 逻辑删除子订单
        QueryWrapper<SalYtOrderSub> subWrapper = new QueryWrapper<>();
        subWrapper.eq("order_id", id);
        List<SalYtOrderSub> subList = salYtOrderSubMapper.selectList(subWrapper);

        for (SalYtOrderSub sub : subList) {
            // 逻辑删除子订单的商品项
            QueryWrapper<SalYtOrderSubItem> itemWrapper = new QueryWrapper<>();
            itemWrapper.eq("order_sub_id", sub.getId());
            List<SalYtOrderSubItem> itemList = salYtOrderSubItemMapper.selectList(itemWrapper);
            for (SalYtOrderSubItem item : itemList) {
                item.setIsDeleted(1);
                salYtOrderSubItemMapper.updateById(item);
            }

            // 逻辑删除子订单
            sub.setIsDeleted(1);
            salYtOrderSubMapper.updateById(sub);
        }

        return result;
    }

    /**
     * 订单审核
     *
     * @param params 审核参数
     * @return 操作结果
     */
    public boolean auditOrder(SalYtOrderUpdateParams params) {
        Long orderId = params.getId();
        Integer status = params.getStatus();

        // 查询订单是否存在
        SalYtOrder order = salYtOrderMapper.selectById(orderId);
        if (order == null || order.getIsDeleted() == 1) {
            throw new BizException(ExceptionCodeEnum.Order_Status_Error.getCode(), "订单不存在或已被删除");
        }

        // 校验订单状态，只有待审核状态才能进行审核操作
        if (!Objects.equals(order.getStatus(), OrderStatusEnum.WaitAudit.getKey())) {
            throw new BizException(ExceptionCodeEnum.Order_Status_Error.getCode(), "只有待审核状态的订单才能进行审核操作");
        }

        // 校验status是否为有效的审核状态
        if (!Objects.equals(status, OrderStatusEnum.AuditApproved.getKey()) &&
                !Objects.equals(status, OrderStatusEnum.AuditRejected.getKey())) {
            throw new BizException(ExceptionCodeEnum.Order_Status_Error.getCode(), "无效的审核状态");
        }

        // 如果是审核失败，校验auditOpinion字段
        if (Objects.equals(status, OrderStatusEnum.AuditRejected.getKey())) {
            if (params.getAuditOpinion() == null || params.getAuditOpinion().trim().isEmpty()) {
                throw new BizException(ExceptionCodeEnum.Order_Status_Error.getCode(), "审核失败时，审核意见不能为空");
            }

            //发送飞书消息
            String code = order.getCode();
            String message = "您的订单【" + code + "】被驳回，原因是： " + params.getAuditOpinion();
            ArrayList<Long> userIdList = new ArrayList<>();
            userIdList.add(order.getCreateUser());
            feiShuManager.sengMessageToUser(message, userIdList);
        }

        // 更新订单状态
        SalYtOrder updateOrder = new SalYtOrder();
        updateOrder.setId(orderId);
        updateOrder.setStatus(status);
        updateOrder.setAuditOpinion(params.getAuditOpinion());
        updateOrder.setUpdateTime(LocalDateTime.now());

        return salYtOrderMapper.updateById(updateOrder) > 0;
    }


    private void fillSubOrderAmountAndStatus(SalYtOrderSub subOrder) {
        // 计算子订单总金额和状态
        BigDecimal productTotal = salYtOrderSubMapper.calculateOrderSubAmount(subOrder.getId());
        if (productTotal == null) {
            productTotal = BigDecimal.ZERO;
        }

        BigDecimal totalAmount = productTotal;
        if (subOrder.getIsCollectedShippingCost() == 1 && subOrder.getShippingCost() != null) {
            totalAmount = totalAmount.add(subOrder.getShippingCost());
        }
        subOrder.setAmount(totalAmount);

        String minStatus = salYtOrderSubMapper.selectMinStatusByOrderSubId(subOrder.getId());
        if (minStatus != null) {
            subOrder.setSubStatus(minStatus);
        }
    }

    public Object subDetailList(SalYtOrderSubItem params) {
        SalYtOrderVo result = new SalYtOrderVo();
        List<SalYtOrderSubItem> itemList = salYtOrderSubItemMapper.selectSalYtOrderSubItemList(params);
        List<SalYtOrderSubItem> resultList = new ArrayList<>();
        itemList.forEach(item -> {
            Long specificationId = item.getSpecificationId();
            if (specificationId != null) {
                //填充产品code
                Long productId = item.getProductId();
                ProYtProduct proYtProduct = proYtProductMapper.selectById(productId);
                item.setProductCode(proYtProduct.getCode());
                //填充规格图片
                List<ProYtProductFile> proYtProductFiles = proYtProductFileMapper.selectByMasterIdAndType(specificationId, ProductFilesTypeEnum.specification.getKey());
                item.setImageList(proYtProductFiles);

                //填充规格项
                List<ProYtProductSpecificationItem> specificationItems = proYtProductSpecificationItemMapper.selectByProductSpecificationId(specificationId);
                item.setItemList(specificationItems);
                resultList.add(item);
            }
        });

        SalYtOrderSub orderSub = new SalYtOrderSub();
        // 将item数据设置到子订单中
        orderSub.setItemList(resultList);
        result.setSubOrder(orderSub);

        //根据orderSubId查询订单信息
        SalYtOrderSub orderSub1 = salYtOrderSubMapper.selectById(params.getOrderSubId());
        LambdaQueryWrapper<SalYtOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SalYtOrder::getId, orderSub1.getOrderId());
        SalYtOrder  order = salYtOrderMapper.selectOne(queryWrapper);
        //赋值币种信息
        result.setCurrency(order.getCurrency());
        return result;
    }

    /**
     * 处理订单退货逻辑
     *
     * @param params 退货参数（包含id和number）
     * @return 操作结果
     */
    @Transactional(rollbackFor = Exception.class)
    public void returnOrderItem(SalYtOrderSubItem params) {
        Long id = params.getId();
        Integer returnNumber = params.getNumber();

        // 1. 根据id查询订单子项记录
        SalYtOrderSubItem orderSubItem = salYtOrderSubItemMapper.selectById(id);
        if (orderSubItem == null || orderSubItem.getIsDeleted() == 1) {
            throw new BizException(ExceptionCodeEnum.Not_Exists);
        }

        // 2. 判断退货数量是否大于总数量
        Integer totalNumber = orderSubItem.getNumber();
        if (returnNumber > totalNumber) {
            throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(), "退货数量不能大于总数量");
        }

        //确认是否是半成品单，如果是半成品要确认退货数量不能大于已确认数量，否则确认退货数量不能大于待发货数量
        Long specificationId = orderSubItem.getSpecificationId();

        //不是半成品
        if (specificationId != null) {
            // 3. 计算待发货数量
            Integer deliveryNumber = orderSubItem.getDeliveryNumber() != null ? orderSubItem.getDeliveryNumber() : 0;
            Integer waitDeliveryNumber = totalNumber - deliveryNumber;

            // 4. 判断退货数量是否大于待发货数量
            if (returnNumber > waitDeliveryNumber) {
                throw new BizException(ExceptionCodeEnum.Order_Status_Error.getCode(), "退货数量不能大于待发货数量");
            }

            // 5. 计算待入库数量
            Integer waitStockNumber = totalNumber - deliveryNumber;

            // 6. 如果退货数量大于待入库数量，需要减少被占用库存并增加可用库存
            if (returnNumber > waitStockNumber) {
                if (specificationId != null) {
                    // 减少被占用库存到可用库存
                    stoYtStoreManager.reduceOccupyStoreStockToEnableStock(specificationId, returnNumber - waitStockNumber);
                }
            }

            //判断是否由半成品确认，如果由半成品确认，需要减少半成品的总数量
            Long confirmItemId = orderSubItem.getConfirmItemId();
            if (confirmItemId != null) {
                //由半成品确认，减少半成品的总数量
                SalYtOrderSubItem confirmedOrderSubItem = salYtOrderSubItemMapper.selectById(confirmItemId);
                if (confirmedOrderSubItem != null) {
                    confirmedOrderSubItem.setNumber(confirmedOrderSubItem.getNumber() - returnNumber);
                    salYtOrderSubItemMapper.updateById(confirmedOrderSubItem);
                }
            }
        } else {
            // 是半成品，需要校验退货数量不能大于已确认数量
            List<SalYtOrderSubItem> confirmItems = salYtOrderSubItemMapper.selectByConfirmItemId(id);

            // 计算已确认的总数量
            Integer confirmedTotalNumber = 0;
            if (!confirmItems.isEmpty()) {
                confirmedTotalNumber = confirmItems.stream()
                        .mapToInt(SalYtOrderSubItem::getNumber)
                        .sum();
            }

            // 判断退货数量是否大于已确认数量
            if (returnNumber > orderSubItem.getNumber()-confirmedTotalNumber) {
                throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(),
                        "退货数量不能大于已确认数量（已确认数量：" + confirmedTotalNumber + "）");
            }

        }


        //添加退货记录
        SalYtReturnOrder salYtReturnOrder = new SalYtReturnOrder();
        salYtReturnOrder.setType(1);
        salYtReturnOrder.setOrderSubItemId(orderSubItem.getId());
        salYtReturnOrder.setBeforeReturnNumber(orderSubItem.getNumber());
        salYtReturnOrder.setReturnNumber(returnNumber);
        salYtReturnOrder.setReason(params.getReason());
        salYtReturnOrderMapper.insert(salYtReturnOrder);

        // 7. 减少子订单item的数量
        orderSubItem.setNumber(orderSubItem.getNumber() - returnNumber);

        salYtOrderSubItemMapper.updateById(orderSubItem);

        //添加操作记录
        salYtOrderSubItemOperationManager.returnOperation(returnNumber, orderSubItem.getId());

    }


    public SalYtOrder getOrderBySubId(Long orderSubId) {
        SalYtOrderSub salYtOrderSub = salYtOrderSubMapper.selectById(orderSubId);
        if (salYtOrderSub == null) {
            throw new BizException(ExceptionCodeEnum.Not_Exists);
        }
        Long orderId = salYtOrderSub.getOrderId();
        return salYtOrderMapper.selectById(orderId);
    }

    /**
     * 根据子订单itemId获取退货记录列表
     *
     * @return 退货记录列表
     */
    public List<SalYtReturnOrder> getReturnOrderListByOrderSubId(Long orderSubId) {
        List<SalYtReturnOrder> salYtReturnOrders = salYtReturnOrderMapper.selectByTypeAndOrderSubId(ReturnOrderTypeEnum.order.getKey(), orderSubId);
        salYtReturnOrders.forEach(order -> {
            //填充规格项
            List<ProYtProductSpecificationItem> specificationItems = proYtProductSpecificationItemMapper.selectByProductSpecificationId(order.getSpecificationId());
            order.setItemList(specificationItems);

            //填充规格图片
            List<ProYtProductFile> proYtProductFiles = proYtProductFileMapper.selectByMasterIdAndType(order.getSpecificationId(), ProductFilesTypeEnum.specification.getKey());
            order.setImageList(proYtProductFiles);
        });
        return salYtReturnOrders;
    }

    /**
     * 导出订单数据
     *
     * @param params   导出参数
     * @param response HTTP响应
     * @throws Exception 导出异常
     */
    public void export(SalYtOrderExportParams params, HttpServletResponse response) throws Exception {
        Workbook workbook = null;
        ServletOutputStream outputStream = null;
        String fileName = null;
        try {
            String code = TenantInfoContext.getCurrentTenant().getCode();
            String templatePath = null;
            ClassPathResource resource = null;
            switch (params.getType()) {
                case 1:
                    templatePath = "excel-template/" + code + "-orderExport.xlsx";
                    resource = new ClassPathResource(templatePath);
                    workbook = new XSSFWorkbook(resource.getInputStream());
                    fileName = exportOrder(workbook, params);
                    break;
                case 2:

                    templatePath = "excel-template/" + code + "-comparisonExport.xlsx";
                    resource = new ClassPathResource(templatePath);
                    workbook = new XSSFWorkbook(resource.getInputStream());
                    fileName = exportComparison(workbook, params);
                    break;
                case 3:
                    templatePath = "excel-template/" + code + "-orderLabelExport.xlsx";
                    resource = new ClassPathResource(templatePath);
                    workbook = new XSSFWorkbook(resource.getInputStream());
                    fileName = exportOrderLabel(workbook, params);
                    break;
                default:
                    throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(), "请选择导出类型");
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=" + java.net.URLEncoder.encode(fileName, "UTF-8"));

            // 获取输出流
            outputStream = response.getOutputStream();

            // 输出到响应流
            workbook.write(outputStream);
            outputStream.flush();

        } finally {
            // 7. 关闭资源
            if (outputStream != null) {
                outputStream.close();
            }
            if (workbook != null) {
                workbook.close();
            }
        }
    }

    private String exportOrderLabel(Workbook workbook, SalYtOrderExportParams params) {
        org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);
        CellStyle cellStyle = workbook.createCellStyle();
//改动***********************************
//        Long orderSubId = params.getOrderSubId();
//        List<SalYtOrderSubItem> salYtOrderSubItems = salYtOrderSubItemMapper.selectSalYtOrderSubItemByOrderSubId(orderSubId);
//        Map<Long, String> collect = salYtOrderSubItems.stream().collect(Collectors.toMap(SalYtOrderSubItem::getSpecificationId, SalYtOrderSubItem::getProductCode, (oldValue, newValue) -> oldValue));
//
//        SalYtOrder order = getOrderBySubId(orderSubId);
        //根据父订单id获取下面子订单集合
        List<SalYtOrderSub> salYtOrderSubs = salYtOrderSubMapper.selectSalYtOrderSubByOrderId(params.getOrderId());
        //获取子订单id集合
        List<Long> orderSubIds = salYtOrderSubs.stream().map(SalYtOrderSub::getId).collect(Collectors.toList());
        //根据子订单集合查询子订单下订单项集合
        List<SalYtOrderSubItem> salYtOrderSubItems = salYtOrderSubItemMapper.selectSalYtOrderSubItemByOrderSubIdList(orderSubIds);
        Map<Long, String> collect = salYtOrderSubItems.stream()
                // 过滤1：specificationId非null（避免null Key）
                .filter(item -> item.getSpecificationId() != null)
                // 过滤2：productCode非null（避免null Value触发merge NPE）
                .filter(item -> item.getProductCode() != null)
                // JDK 21的Collectors.toMap完全兼容该写法
                .collect(Collectors.toMap(
                        SalYtOrderSubItem::getSpecificationId,
                        SalYtOrderSubItem::getProductCode,
                        (oldValue, newValue) -> oldValue // 重复Key保留旧值
                ));
        SalYtOrder order = salYtOrderMapper.selectById(params.getOrderId());
//*****************************************
        Long customerId = order.getCustomerId();

        HashMap<Long, Set<Long>> supplierSpecificationMap = new HashMap<>();
        HashMap<Long, Integer> specificationNumberMap = new HashMap<>();

        for (SalYtOrderSubItem salYtOrderSubItem : salYtOrderSubItems) {
            //填充供应商和规格的映射关系
            Long supplierId = salYtOrderSubItem.getSupplierId();
            if (supplierSpecificationMap.containsKey(supplierId)) {
                Set<Long> specificationIdList = supplierSpecificationMap.get(supplierId);
                if (specificationIdList == null) {
                    specificationIdList = new HashSet<>();
                }
                specificationIdList.add(salYtOrderSubItem.getSpecificationId());
            } else {
                Set<Long> specificationIdList = new HashSet<>();
                specificationIdList.add(salYtOrderSubItem.getSpecificationId());
                supplierSpecificationMap.put(supplierId, specificationIdList);
            }
            //填充规格和规格数量的关系
            Long specificationId = salYtOrderSubItem.getSpecificationId();
            if (specificationNumberMap.containsKey(specificationId)) {
                Integer specificationNumber = specificationNumberMap.get(specificationId);
                specificationNumber = specificationNumber + salYtOrderSubItem.getNumber();
                specificationNumberMap.put(specificationId, specificationNumber);
            } else {
                specificationNumberMap.put(specificationId, salYtOrderSubItem.getNumber());
            }
        }

        //填充excel表格字段
        Integer rowIndex = 1;
        for (Map.Entry<Long, Set<Long>> entry : supplierSpecificationMap.entrySet()) {
            //填充供应商名称
            Long supplierId = entry.getKey();
            PurYtSupplier purYtSupplier = purYtSupplierMapper.selectById(supplierId);
            Row supplierNameRow = sheet.createRow(rowIndex);
            Cell cell = supplierNameRow.createCell(0);
            cell.setCellValue("【" + purYtSupplier.getName() + "】");
            rowIndex++;
            //填充标签
            Set<Long> value = entry.getValue();
            for (Long specificationId : value) {
                if(specificationId==null){
                    continue;
                }
                Integer number = specificationNumberMap.get(specificationId);
                //设置标签内容
                String cellValue = "";
                String wrapString = "\n";
                String itemNumber = collect.get(specificationId);
                if (params.getIsCustomerItemNumber() != null && params.getIsCustomerItemNumber()) {
                    SalYtCustomerSpecificationComparison salYtCustomerSpecificationComparison = salYtCustomerSpecificationComparisonMapper.selectBySpecificationIdAndCustomerId(specificationId, customerId);
                    if (salYtCustomerSpecificationComparison != null
                            && salYtCustomerSpecificationComparison.getItemNumber() != null
                            && !salYtCustomerSpecificationComparison.getItemNumber().trim().isEmpty()) {
                        itemNumber = salYtCustomerSpecificationComparison.getItemNumber();
                    }
                }
                //设置标签内容中的货号
                cellValue = cellValue + itemNumber;
                cellValue = cellValue + wrapString;
                //设置规格名称
                if (params.getIsShowSpecification() != null && params.getIsShowSpecification()) {
                    List<String> specificationNameList = new ArrayList<>();
                    List<ProYtProductSpecificationItem> specificationItems = proYtProductSpecificationItemMapper.selectComparisonByProductSpecificationId(specificationId, order.getCustomerId());
                    for (ProYtProductSpecificationItem specificationItem : specificationItems) {
                        String specificationName = specificationItem.getCategorySpecificationItemValue();
                        if (params.getIsEnglish() != null && params.getIsEnglish() && specificationItem.getEngName() != null) {
                            specificationName = specificationItem.getEngName();
                        }
                        if (specificationItem.getCustomerSpecification() != null) {
                            specificationName = specificationItem.getCustomerSpecification();
                        }
                        specificationNameList.add(specificationName);
                    }
                    if (!specificationNameList.isEmpty()) {
                        cellValue = cellValue + (String.join(" ", specificationNameList));
                        cellValue = cellValue + (wrapString);
                    }
                }

                //设置Made
                if (params.getIsShowMade() != null && params.getIsShowMade()) {
                    cellValue = cellValue + ("MADE IN CHINA");
                }

                //设置抛数
                if (params.getAbandonNumber() != null) {
                    number += params.getAbandonNumber();
                }
                for (int i = 0; i < number; i++) {
                    Row row = sheet.createRow(rowIndex);
                    row.setHeightInPoints(70);
                    Cell cell1 = row.createCell(0);
                    cell1.setCellValue(cellValue);
                    cellStyle.setWrapText(true);
                    cellStyle.setVerticalAlignment(VerticalAlignment.TOP);
                    cellStyle.setAlignment(HorizontalAlignment.CENTER);
                    cell1.setCellStyle(cellStyle);
                    rowIndex++;
                }
            }
        }
        return "订单标签导出_" + order.getCode() + ".xlsx";
    }

    private String exportComparison(Workbook workbook, SalYtOrderExportParams params) {
        org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setWrapText(true);

//改动*******************************
        //根据父订单id查询子订单集合
        List<SalYtOrderSub> salYtOrderSubs = salYtOrderSubMapper.selectSalYtOrderSubByOrderId(params.getOrderId());
        //子订单id集合
        List<Long> orderSubIds = salYtOrderSubs.stream().map(SalYtOrderSub::getId).collect(Collectors.toList());
        //根据子订单集合查询子订单下订单项集合
        List<SalYtOrderSubItem> salYtOrderSubItems = salYtOrderSubItemMapper.selectSalYtOrderSubItemByOrderSubIdList(orderSubIds);

        // Long orderSubId = params.getOrderSubId();
        // List<SalYtOrderSubItem> salYtOrderSubItems = salYtOrderSubItemMapper.selectSalYtOrderSubItemByOrderSubId(orderSubId);
        // Map<Long, String> collect = salYtOrderSubItems.stream().collect(Collectors.toMap(SalYtOrderSubItem::getSpecificationId, SalYtOrderSubItem::getProductCode, (oldValue, newValue) -> oldValue));
        Map<Long, String> collect = salYtOrderSubItems.stream()
                // 过滤1：specificationId非null（避免null Key）
                .filter(item -> item.getSpecificationId() != null)
                // 过滤2：productCode非null（避免null Value触发merge NPE）
                .filter(item -> item.getProductCode() != null)
                // JDK 21的Collectors.toMap完全兼容该写法
                .collect(Collectors.toMap(
                        SalYtOrderSubItem::getSpecificationId,
                        SalYtOrderSubItem::getProductCode,
                        (oldValue, newValue) -> oldValue // 重复Key保留旧值
                ));
//*******************************



//改动*******************************
        //SalYtOrder order = getOrderBySubId(orderSubId);
        SalYtOrder order = salYtOrderMapper.selectById(params.getOrderId());
//***********************************
        Long customerId = order.getCustomerId();

        HashMap<Long, Set<Long>> supplierSpecificationMap = new HashMap<>();

        //填充供应商和规格的映射关系
        for (SalYtOrderSubItem salYtOrderSubItem : salYtOrderSubItems) {
            Long supplierId = salYtOrderSubItem.getSupplierId();
            if (supplierSpecificationMap.containsKey(supplierId)) {
                Set<Long> specificationIdList = supplierSpecificationMap.get(supplierId);
                if (specificationIdList == null) {
                    specificationIdList = new HashSet<>();
                }
                specificationIdList.add(salYtOrderSubItem.getSpecificationId());
            } else {
                Set<Long> specificationIdList = new HashSet<>();
                specificationIdList.add(salYtOrderSubItem.getSpecificationId());
                supplierSpecificationMap.put(supplierId, specificationIdList);
            }
        }
        Integer rowIndex = 1;
        for (Map.Entry<Long, Set<Long>> entry : supplierSpecificationMap.entrySet()) {
            Long supplierId = entry.getKey();
            PurYtSupplier purYtSupplier = purYtSupplierMapper.selectById(supplierId);
            Set<Long> value = entry.getValue();
            Row row = sheet.createRow(rowIndex);
            Cell cell = row.createCell(0);
            cell.setCellValue(purYtSupplier.getName());
            cell.setCellStyle(cellStyle);
            Integer startRow = rowIndex;
            for (Long specificationId : value) {
                if (specificationId == null) {
                    //  break;
                    continue;
                }
                ProYtProductSpecificationSupplier specificationSupplier = proYtProductSpecificationSupplierMapper.selectBySpecificationIdAndSupplier(specificationId, supplierId);
                Row speRow = null;
                if (startRow.equals(rowIndex)) {
                    speRow = row;
                    // 设置行高以容纳多行内容
                    speRow.setHeightInPoints(70);
                } else {
                    speRow = sheet.createRow(startRow);
                    // 设置行高以容纳多行内容
                    speRow.setHeightInPoints(70);
                }
                //设置供应商规格编号
                Cell cell1 = speRow.createCell(1);
                cell1.setCellValue(specificationSupplier.getSupplierSpecificationCode() != null ? specificationSupplier.getSupplierSpecificationCode() : "");
                cell1.setCellStyle(cellStyle);
                //设置供应商规格
                Cell cell2 = speRow.createCell(2);
                cell2.setCellValue(specificationSupplier.getSupplierSpecification());
                CellStyle wrapStyle = cell2.getCellStyle();
                wrapStyle.setAlignment(HorizontalAlignment.JUSTIFY);
                wrapStyle.setVerticalAlignment(VerticalAlignment.TOP);
                cell2.setCellStyle(cellStyle);

                String wrapString = "\n";
                //设置货号+规格名称+Made
                String cellValue = "";
                String itemNumber = collect.get(specificationId);
                if (params.getIsCustomerItemNumber() != null && params.getIsCustomerItemNumber()) {
                    SalYtCustomerSpecificationComparison salYtCustomerSpecificationComparison = salYtCustomerSpecificationComparisonMapper.selectBySpecificationIdAndCustomerId(specificationId, customerId);
                    if (salYtCustomerSpecificationComparison != null
                            && salYtCustomerSpecificationComparison.getItemNumber() != null
                            && !salYtCustomerSpecificationComparison.getItemNumber().trim().isEmpty()) {
                        itemNumber = salYtCustomerSpecificationComparison.getItemNumber();
                    }
                }

                //设置货号
                cellValue = cellValue + itemNumber;
                cellValue = cellValue + wrapString;

                //设置规格名称
                if (params.getIsShowSpecification() != null && params.getIsShowSpecification()) {
                    List<String> specificationNameList = new ArrayList<>();
                    List<ProYtProductSpecificationItem> specificationItems = proYtProductSpecificationItemMapper.selectComparisonByProductSpecificationId(specificationId, order.getCustomerId());
                    for (ProYtProductSpecificationItem specificationItem : specificationItems) {
                        String specificationName = specificationItem.getCategorySpecificationItemValue();
                        if (params.getIsEnglish() != null && params.getIsEnglish() && specificationItem.getEngName() != null) {
                            specificationName = specificationItem.getEngName();
                        }
                        if (specificationItem.getCustomerSpecification() != null) {
                            specificationName = specificationItem.getCustomerSpecification();
                        }
                        specificationNameList.add(specificationName);
                    }
                    if (!specificationNameList.isEmpty()) {
                        cellValue = cellValue + (String.join(" ", specificationNameList));
                        cellValue = cellValue + (wrapString);
                    }
                }

                //设置Made
                if (params.getIsShowMade() != null && params.getIsShowMade()) {
                    cellValue = cellValue + ("MADE IN CHINA");
                }
                Cell cell3 = speRow.createCell(3);
                cell3.setCellValue(cellValue.toString());
                startRow++;
            }

            //合并行
//改动***********************
//            if (rowIndex != startRow - 1) {
//                CellRangeAddress mergedRegion = new CellRangeAddress(rowIndex, startRow - 1, 0, 0);
//                sheet.addMergedRegion(mergedRegion);
//            }

            int endRow = startRow - 1; // 提取结束行变量，便于校验
            if (rowIndex != endRow && rowIndex <= endRow && endRow >= 0) {
                // 仅当范围合法时，才创建合并区域
                CellRangeAddress mergedRegion = new CellRangeAddress(rowIndex, endRow, 0, 0);
                sheet.addMergedRegion(mergedRegion);
            }

//************************

            rowIndex = startRow;
        }
        return "规格对照导出_" + order.getCode() + ".xlsx";
    }

    private String exportOrder(Workbook workbook, SalYtOrderExportParams params) throws IOException {
        // 填充Excel数据
        //替换当前登陆人电话，邮箱
        Long userId = LoginUserInfoContext.getUserId();
        SysUser sysUser = sysUserMapper.selectById(userId);
        //改动*******
        //  Long orderSubId = params.getOrderSubId();
        // SalYtOrder order = getOrderBySubId(orderSubId);
        SalYtOrder order = salYtOrderMapper.selectById(params.getOrderId());
        //***********
        Long customerId = order.getCustomerId();
        SalYtCustomer customer = salYtCustomerMapper.selectById(customerId);

        // 遍历所有工作表
        org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);
        // 遍历所有行
        for (org.apache.poi.ss.usermodel.Row row : sheet) {
            if (row != null) {
                // 遍历所有单元格
                for (org.apache.poi.ss.usermodel.Cell cell : row) {
                    if (cell != null && cell.getCellType() == org.apache.poi.ss.usermodel.CellType.STRING) {
                        QMYExcelUtil.replaceCellValue("{tel}", sysUser.getPhonenumber(), cell);
                        QMYExcelUtil.replaceCellValue("{email}", sysUser.getEmail(), cell);
                        QMYExcelUtil.replaceCellValue("{customerName}", customer.getName(), cell);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        QMYExcelUtil.replaceCellValue("{createTime}", order.getCreateTime().format(formatter), cell);
                        QMYExcelUtil.replaceCellValue("{customerAddress}", order.getCustomerAddress(), cell);
                    }
                }
            }
        }

        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        String currencySymbol = getExportCurrencySymbol(order);
        HashMap<String, SalYtOrderExportVo> exportMap = new HashMap<>();
        int rowIndex = 8; // 从指定行开始填充数据

        //改动****************
        //根据父订单id查询子订单集合
        List<SalYtOrderSub> salYtOrderSubs = salYtOrderSubMapper.selectSalYtOrderSubByOrderId(order.getId());
        //获取子订单id集合
        List<Long> orderSubIds = salYtOrderSubs.stream().map(SalYtOrderSub::getId).collect(Collectors.toList());
        //根据子订单集合查询子订单下订单项集合
        List<SalYtOrderSubItem> salYtOrderSubItems = salYtOrderSubItemMapper.selectSalYtOrderSubItemByOrderSubIds(orderSubIds);
        salYtOrderSubItems = filterExportOrderItems(salYtOrderSubItems);
        //  List<SalYtOrderSubItem> salYtOrderSubItems = salYtOrderSubItemMapper.selectSalYtOrderSubItemByOrderSubId(orderSubId);
        //*************************
        for (SalYtOrderSubItem salYtOrderSubItem : salYtOrderSubItems) {
            if (salYtOrderSubItem.getEndReturnNumber() == null) {
                salYtOrderSubItem.setEndReturnNumber(0);
            }
            salYtOrderSubItem.setNumber(salYtOrderSubItem.getNumber()-salYtOrderSubItem.getEndReturnNumber());

            String groupCode = resolveExportModelNo(salYtOrderSubItem, customerId);
            SalYtOrderExportVo item = exportMap.get(groupCode);
            if (item == null) {
                //map中未存在，则新增一个item
                SalYtOrderExportVo salYtOrderExportVo = new SalYtOrderExportVo();
                salYtOrderExportVo.setCode(groupCode);
                // ProYtProductSpecification proYtProductSpecification = proYtProductSpecificationMapper.selectById(specificationId);
                //Long productId = proYtProductSpecification.getProductId();
                Long productId = salYtOrderSubItem.getProductId();
                List<ProYtProductFile> proYtProductFiles = proYtProductFileMapper.selectByMasterIdAndType(productId, ProductFilesTypeEnum.product.getKey());
                //只取了第一张产品图片
                ProYtProductFile productFile = proYtProductFiles.get(0);
                salYtOrderExportVo.setImageUrl(productFile.getUrl());
                salYtOrderExportVo.setTotalQuantity(salYtOrderSubItem.getNumber());
                List<SalYtOrderExportItemVo> itemVoList = new ArrayList<>();
                SalYtOrderExportItemVo exportItemVo = new SalYtOrderExportItemVo();
                //填充规格信息
                getExportItemVo(exportItemVo, salYtOrderSubItem, params, order);
                itemVoList.add(exportItemVo);
                salYtOrderExportVo.setItemList(itemVoList);
                exportMap.put(groupCode, salYtOrderExportVo);
            } else {
                //map中已存在，则只新增规格信息
                List<SalYtOrderExportItemVo> itemList = item.getItemList();
                SalYtOrderExportItemVo exportItemVo = new SalYtOrderExportItemVo();
                if (item.getTotalQuantity() == null) {
                    item.setTotalQuantity(0);
                }
                item.setTotalQuantity(item.getTotalQuantity() + salYtOrderSubItem.getNumber());
                //填充规格信息
                getExportItemVo(exportItemVo, salYtOrderSubItem, params, order);
                itemList.add(exportItemVo);
            }
        }

        BigDecimal totalOrderPrice = BigDecimal.ZERO;
        Integer totalOrderQuantity = 0;
        // 遍历exportMap并将数据写入Excel
        for (Map.Entry<String, SalYtOrderExportVo> entry : exportMap.entrySet()) {
            String groupCode = entry.getKey();
            SalYtOrderExportVo exportVo = entry.getValue();
            List<SalYtOrderExportItemVo> itemList = exportVo.getItemList();
            Row row = sheet.createRow(rowIndex);
            row.setHeight((short) 1000);
            //设置货号或产品编号
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(groupCode);
            cell0.setCellStyle(cellStyle);

            //设置产品规格
            Integer startIndex = rowIndex;
            BigDecimal productTotalPrice = BigDecimal.ZERO;
            for (SalYtOrderExportItemVo item : itemList) {
                Row speRow = null;
                if (startIndex == rowIndex) {
                    speRow = row;
                } else {
                    speRow = sheet.createRow(startIndex);
                    speRow.setHeight((short) 1000);
                }
                List<String> specificationNames = item.getSpecificationNames();
                //设置规格名称
                Cell cell2 = speRow.createCell(2);
                cell2.setCellValue(String.join(",", specificationNames));
                cell2.setCellStyle(cellStyle);
                //设置规格数量
                Cell cell3 = speRow.createCell(3);
                cell3.setCellValue(item.getQuantity());
                cell3.setCellStyle(cellStyle);
                //设置售价
                Cell cell4 = speRow.createCell(4);
                cell4.setCellValue(currencySymbol + item.getPrice().doubleValue());
                cell4.setCellStyle(cellStyle);
                productTotalPrice = productTotalPrice.add(item.getTotalPrice());
                totalOrderPrice = totalOrderPrice.add(item.getTotalPrice());
                startIndex++;
            }
            //设置产品数量
            Cell cell5 = row.createCell(5);
            cell5.setCellValue(exportVo.getTotalQuantity());
            cell5.setCellStyle(cellStyle);
            totalOrderQuantity += exportVo.getTotalQuantity() == null ? 0 : exportVo.getTotalQuantity();
            //设置产品总价
            Cell cell6 = row.createCell(6);
            cell6.setCellValue(currencySymbol + productTotalPrice.doubleValue());
            cell6.setCellStyle(cellStyle);
            //合并行
            if (startIndex != rowIndex+1) {
                CellRangeAddress mergedRegion = new CellRangeAddress(rowIndex, startIndex - 1, 0, 0);
                sheet.addMergedRegion(mergedRegion);
                CellRangeAddress mergedRegion1 = new CellRangeAddress(rowIndex, startIndex - 1, 1, 1);
                sheet.addMergedRegion(mergedRegion1);
                CellRangeAddress mergedRegion2 = new CellRangeAddress(rowIndex, startIndex - 1, 5, 5);
                sheet.addMergedRegion(mergedRegion2);
                CellRangeAddress mergedRegion3 = new CellRangeAddress(rowIndex, startIndex - 1, 6, 6);
                sheet.addMergedRegion(mergedRegion3);
            }
            //设置产品图片
            String imageUrl = exportVo.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Drawing<?> drawing = sheet.createDrawingPatriarch();
                QMYExcelUtil.insertImageToMulCell(workbook, sheet, drawing, imageUrl, rowIndex, startIndex - 1, 1, 0.1, 0.1, 0.8);
            }
            rowIndex = startIndex;
        }

        CellStyle footStyle = workbook.createCellStyle();
        footStyle.setFillForegroundColor(IndexedColors.LAVENDER.getIndex());
        footStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        footStyle.setAlignment(HorizontalAlignment.CENTER);
        footStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        Row row = sheet.createRow(rowIndex);
        for (int i = 0; i <= 7; i++) {
            Cell footerCell = row.createCell(i);
            footerCell.setCellStyle(footStyle);
        }
        Cell cell = row.createCell(0);
        cell.setCellValue("Total:");
        cell.setCellStyle(footStyle);
        Cell totalQtyCell = row.createCell(5);
        totalQtyCell.setCellValue(totalOrderQuantity);
        totalQtyCell.setCellStyle(footStyle);
        Cell cell1 = row.createCell(6);
        cell1.setCellValue(currencySymbol + totalOrderPrice);
        cell1.setCellStyle(footStyle);

        Long orderId = order.getId();
        if(order.getEndOtherAmount() != null && order.getEndOtherAmount().compareTo(BigDecimal.ZERO) > 0){
            Row rowNext = sheet.createRow(rowIndex+1);
            for (int i = 0; i <= 7; i++) {
                Cell footerCell = rowNext.createCell(i);
                footerCell.setCellStyle(footStyle);
            }
            Cell endOtherAmountCell = rowNext.createCell(5);
            endOtherAmountCell.setCellValue(currencySymbol +"-" +order.getEndOtherAmount().toString());
            endOtherAmountCell.setCellStyle(footStyle);
            Cell amountCell = rowNext.createCell(6);
            totalOrderPrice = totalOrderPrice.subtract(order.getEndOtherAmount());
            amountCell.setCellValue(currencySymbol + totalOrderPrice);
            amountCell.setCellStyle(footStyle);
        }


        // 6. 设置响应头，输出Excel文件
        return "订单导出_" + order.getCode() + ".xlsx";
    }

    private String getExportCurrencySymbol(SalYtOrder order) {
        if (order != null && CurrencyEnum.dollar.getValue().equals(order.getCurrency())) {
            return "$";
        }
        return "¥";
    }

    /**
     * 导出时 Model No. 优先取客户货号，其次取订单项产品编号，最后回退到产品主数据编号。
     */
    private String resolveExportModelNo(SalYtOrderSubItem salYtOrderSubItem, Long customerId) {
        if (salYtOrderSubItem.getSpecificationId() != null) {
            SalYtCustomerSpecificationComparison comparison =
                    salYtCustomerSpecificationComparisonMapper.selectBySpecificationIdAndCustomerId(
                            salYtOrderSubItem.getSpecificationId(), customerId);
            if (comparison != null && StringUtils.isNotBlank(comparison.getItemNumber())) {
                return comparison.getItemNumber().trim();
            }
        }

        if (StringUtils.isNotBlank(salYtOrderSubItem.getProductCode())) {
            return salYtOrderSubItem.getProductCode().trim();
        }

        if (salYtOrderSubItem.getProductId() != null) {
            ProYtProduct product = proYtProductMapper.selectById(salYtOrderSubItem.getProductId());
            if (product != null && StringUtils.isNotBlank(product.getCode())) {
                return product.getCode().trim();
            }
        }

        return "";
    }

    /**
     * 订单导出时，半成品原始行需要扣减已确认数量，避免和确认后生成的规格行重复导出。
     */
    private List<SalYtOrderSubItem> filterExportOrderItems(List<SalYtOrderSubItem> orderSubItems) {
        if (orderSubItems == null || orderSubItems.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, Integer> confirmedNumberMap = new HashMap<>();
        for (SalYtOrderSubItem orderSubItem : orderSubItems) {
            if (orderSubItem == null || orderSubItem.getConfirmItemId() == null) {
                continue;
            }
            Integer confirmNumber = orderSubItem.getNumber() == null ? 0 : orderSubItem.getNumber();
            Integer currentConfirmedNumber = confirmedNumberMap.getOrDefault(orderSubItem.getConfirmItemId(), 0);
            confirmedNumberMap.put(orderSubItem.getConfirmItemId(), currentConfirmedNumber + confirmNumber);
        }

        List<SalYtOrderSubItem> exportItems = new ArrayList<>();
        for (SalYtOrderSubItem orderSubItem : orderSubItems) {
            if (orderSubItem == null) {
                continue;
            }
            if (orderSubItem.getSpecificationId() == null) {
                Integer originNumber = orderSubItem.getNumber() == null ? 0 : orderSubItem.getNumber();
                Integer confirmedNumber = confirmedNumberMap.getOrDefault(orderSubItem.getId(), 0);
                int remainNumber = originNumber - confirmedNumber;
                if (remainNumber <= 0) {
                    continue;
                }
                SalYtOrderSubItem exportItem = new SalYtOrderSubItem();
                BeanUtils.copyProperties(orderSubItem, exportItem);
                exportItem.setNumber(remainNumber);
                exportItems.add(exportItem);
                continue;
            }
            exportItems.add(orderSubItem);
        }
        return exportItems;
    }

    private void getExportItemVo(SalYtOrderExportItemVo exportItemVo, SalYtOrderSubItem salYtOrderSubItem, SalYtOrderExportParams params, SalYtOrder order) {
        Long specificationId = salYtOrderSubItem.getSpecificationId();
        exportItemVo.setQuantity(salYtOrderSubItem.getNumber());
        exportItemVo.setPrice(salYtOrderSubItem.getPrice());
        exportItemVo.setTotalPrice(salYtOrderSubItem.getPrice().multiply(BigDecimal.valueOf(salYtOrderSubItem.getNumber())));
        if (specificationId != null) {
            List<ProYtProductSpecificationItem> specificationItems = proYtProductSpecificationItemMapper.selectComparisonByProductSpecificationId(specificationId, order.getCustomerId());
            List<String> specificationNames = new ArrayList<>();
            for (ProYtProductSpecificationItem specificationItem : specificationItems) {
                String specification = specificationItem.getCategorySpecificationItemValue();
                //如果选择了英文规格并且英文规格不为空，则显示英文
                if (params.getIsEnglish() && specificationItem.getEngName() != null) {
                    specification = specificationItem.getEngName();
                }
                //如果有客户规格，无论是否选择，都显示客户规格
                if (specificationItem.getCustomerSpecification() != null) {
                    specification = specificationItem.getCustomerSpecification();
                }
                specificationNames.add(specification);
            }
            exportItemVo.setSpecificationNames(specificationNames);
        } else {
            List<String> specificationNames = new ArrayList<>();
            specificationNames.add("半成品");
            exportItemVo.setSpecificationNames(specificationNames);
        }
    }

    public Object itemOperation(Long itemId) {
        return salYtOrderSubItemOperationManager.operationListWithPurchase(itemId);
    }

    /**
     * 根据查询参数获取退货统计信息
     *
     * @param params 查询参数
     * @return 退货统计信息列表
     */
    public List<SalYtReturnStatsVo> getReturnStats(SalYtReturnOrderQueryParams params) {
        if (params == null) {
            throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(), "查询参数不能为空");
        }
        if (params.getOrderSubId() == null) {
            throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(), "子订单ID不能为空");
        }
        List<SalYtReturnStatsVo> returnStats = salYtReturnOrderMapper.getReturnStats(params);
        returnStats.forEach(salYtReturnStatsVo -> {
            Long specificationId = salYtReturnStatsVo.getSpecificationId();
            if (specificationId != null) {
                //填充规格项
                List<ProYtProductSpecificationItem> specificationItems = proYtProductSpecificationItemMapper.selectByProductSpecificationId(salYtReturnStatsVo.getSpecificationId());
                salYtReturnStatsVo.setItemList(specificationItems);

                //填充规格图片
                List<ProYtProductFile> proYtProductFiles = proYtProductFileMapper.selectByMasterIdAndType(salYtReturnStatsVo.getSpecificationId(), ProductFilesTypeEnum.specification.getKey());
                salYtReturnStatsVo.setImageList(proYtProductFiles);
            } else {
                List<ProYtProductFile> proYtProductFiles = proYtProductFileMapper.selectByMasterIdAndType(salYtReturnStatsVo.getProductId(), ProductFilesTypeEnum.product.getKey());
                salYtReturnStatsVo.setImageList(proYtProductFiles);
            }
        });
        return returnStats;
    }

    /**
     * 根据查询参数获取退货统计信息  2.0
     * @param params
     * @return
     */
    public List<SalYtReturnStatsVo> getReturnStats1(SalYtReturnOrderQueryParams params) {
        if (params == null) {
            throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(), "查询参数不能为空");
        }
        if (params.getOrderId() == null) {
            throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(), "父订单ID不能为空");
        }
        List<SalYtReturnStatsVo> returnStats = salYtReturnOrderMapper.getReturnStats1(params);
        returnStats.forEach(salYtReturnStatsVo -> {
            Long specificationId = salYtReturnStatsVo.getSpecificationId();
            if (specificationId != null) {
                //填充规格项
                List<ProYtProductSpecificationItem> specificationItems = proYtProductSpecificationItemMapper.selectByProductSpecificationId(salYtReturnStatsVo.getSpecificationId());
                salYtReturnStatsVo.setItemList(specificationItems);

                //填充规格图片
                List<ProYtProductFile> proYtProductFiles = proYtProductFileMapper.selectByMasterIdAndType(salYtReturnStatsVo.getSpecificationId(), ProductFilesTypeEnum.specification.getKey());
                salYtReturnStatsVo.setImageList(proYtProductFiles);

            } else {
                List<ProYtProductFile> proYtProductFiles = proYtProductFileMapper.selectByMasterIdAndType(salYtReturnStatsVo.getProductId(), ProductFilesTypeEnum.product.getKey());
                salYtReturnStatsVo.setImageList(proYtProductFiles);
            }
        });

        //合并退货集合
        List<SalYtReturnStatsVo> returnStats1=doMergeReturn(returnStats);

        for (int i = 0; i < returnStats1.size(); i++){
            if(returnStats1.get( i).getReturnOrderList().isEmpty()){
                returnStats1.remove( i);
            }
        }

        return returnStats1;
    }

    private List<SalYtReturnStatsVo> doMergeReturn(List<SalYtReturnStatsVo> returnStats) {
        if (returnStats == null || returnStats.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        java.util.Map<String, SalYtReturnStatsVo> mergedMap = new java.util.LinkedHashMap<>();

        for (SalYtReturnStatsVo vo : returnStats) {
            if (vo == null) {
                continue;
            }

            Long labelId = vo.getLabelId();
            String remark = vo.getRemark();
            java.math.BigDecimal price = vo.getPrice();
            java.math.BigDecimal supplierPrice = vo.getSupplierPrice();
            Long specificationId = vo.getSpecificationId();
            Long productId = vo.getProductId();

            String key =
                    (labelId == null ? "null" : labelId.toString()) + "|" +
                            (remark == null ? "null" : remark) + "|" +
                            (price == null ? "null" : price.toPlainString()) + "|" +
                            (supplierPrice == null ? "null" : supplierPrice.toPlainString()) + "|" +
                            (specificationId == null ? "null" : specificationId.toString()) + "|" +
                            (productId == null ? "null" : productId.toString());

            SalYtReturnStatsVo merged = mergedMap.get(key);
            if (merged == null) {
                // 初始化需要累加的字段，避免后续 NPE
                mergedMap.put(key, vo);
            } else {
                // initialNumber / totalReturnNumber / remainingNumber 相加
                Integer mInitial = merged.getInitialNumber();
                Integer vInitial = vo.getInitialNumber();
                if (mInitial == null) mInitial = 0;
                if (vInitial == null) vInitial = 0;
                merged.setInitialNumber(mInitial + vInitial);

                Integer mTotal = merged.getTotalReturnNumber();
                Integer vTotal = vo.getTotalReturnNumber();
                if (mTotal == null) mTotal = 0;
                if (vTotal == null) vTotal = 0;
                merged.setTotalReturnNumber(mTotal + vTotal);

                Integer mRemain = merged.getRemainingNumber();
                Integer vRemain = vo.getRemainingNumber();
                if (mRemain == null) mRemain = 0;
                if (vRemain == null) vRemain = 0;
                merged.setRemainingNumber(mRemain + vRemain);
            }
        }

        return new java.util.ArrayList<>(mergedMap.values());
    }

    /**
     * 根据查询参数获取退货记录
     *
     * @param params 查询参数
     * @return 退货记录列表
     */
    public List<SalYtReturnOrder> getReturnOrdersByParams(SalYtReturnOrderQueryParams params) {
        if (params == null) {
            throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(), "查询参数不能为空");
        }
//        if (params.getOrderSubId() == null) {
//            throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(), "子订单ID不能为空");
//        }
        List<SalYtReturnOrder> returnOrders = salYtReturnOrderMapper.getReturnOrdersByParams(params);
        if(!returnOrders.isEmpty()) {
            returnOrders.forEach(this::handleReturnOrderItem);
        }
        return returnOrders;
    }

    private void handleReturnOrderItem(SalYtReturnOrder salYtReturnOrder) {
        Long orderSubItemId = salYtReturnOrder.getOrderSubItemId();
        SalYtOrderSubItem salYtOrderSubItem = salYtOrderSubItemMapper.selectById(orderSubItemId);
        //产品code
        Long productId = salYtOrderSubItem.getProductId();
        ProYtProduct proYtProduct = proYtProductMapper.selectById(productId);
        salYtReturnOrder.setProductCode(proYtProduct.getCode());

        //规格图片
        Long specificationId = salYtOrderSubItem.getSpecificationId();
        if (specificationId != null) {
            //填充规格项
            List<ProYtProductSpecificationItem> specificationItems = proYtProductSpecificationItemMapper.selectByProductSpecificationId(salYtReturnOrder.getSpecificationId());
            salYtReturnOrder.setItemList(specificationItems);

            //填充规格图片
            List<ProYtProductFile> proYtProductFiles = proYtProductFileMapper.selectByMasterIdAndType(salYtReturnOrder.getSpecificationId(), ProductFilesTypeEnum.specification.getKey());
            salYtReturnOrder.setImageList(proYtProductFiles);
        } else {
            List<ProYtProductFile> proYtProductFiles = proYtProductFileMapper.selectByMasterIdAndType(productId, ProductFilesTypeEnum.product.getKey());
            salYtReturnOrder.setImageList(proYtProductFiles);
        }
    }

    public List<SalYtOrderSubItem> inCompleteList(SalYtOrderSubItem params) {
        // 校验参数，orderSubId不能为空
        if (params.getOrderSubId() == null) {
            throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(), "子订单ID不能为空");
        }

        List<SalYtOrderSubItem> inCompleteList = salYtOrderSubItemMapper.inCompleteList(params);

        // 对查询结果进行数据填充
        inCompleteList.forEach(item -> {
            Long productId = item.getProductId();
            if (productId != null) {
                // 填充产品信息
                ProYtProduct proYtProduct = proYtProductMapper.selectById(productId);
                if (proYtProduct != null) {
                    item.setProductCode(proYtProduct.getCode());
                }
            }
            //填充产品图片
            List<ProYtProductFile> proYtProductFiles = proYtProductFileMapper.selectByMasterIdAndType(productId, ProductFilesTypeEnum.product.getKey());
            item.setImageList(proYtProductFiles);

            Integer confirmNumber = item.getConfirmNumber();
            if (confirmNumber == null) {
                item.setConfirmNumber(0);
            }
            if (confirmNumber < item.getNumber()) {
                item.setConfirmStatus(0);
            } else {
                item.setConfirmStatus(1);
            }
        });

        return inCompleteList;
    }

    @Transactional(rollbackFor = Exception.class)
    public void confirmInComplete(SalYtConfirmIncompleteParams params) {
        List<Long> itemIdList = params.getItemIdList();
        if (itemIdList == null || itemIdList.isEmpty()) {
            throw new BizException("itemIDList不能为空");
        }

        QueryWrapper<SalYtOrderSubItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", itemIdList);
        queryWrapper.eq("is_deleted", 0);
        List<SalYtOrderSubItem> items = salYtOrderSubItemMapper.selectList(queryWrapper);

        // 查找之前确认过的数量
        queryWrapper = new QueryWrapper<>();
        queryWrapper.in("confirm_item_id", itemIdList);
        queryWrapper.eq("is_deleted", 0);
        List<SalYtOrderSubItem> existingConfirmItems = salYtOrderSubItemMapper.selectList(queryWrapper);

        Integer previousConfirmNumber = 0;
        if (!existingConfirmItems.isEmpty()) {
            previousConfirmNumber = existingConfirmItems.stream()
                    .mapToInt(SalYtOrderSubItem::getNumber)
                    .sum();
        }

        // 计算本次确认的总数量
        Integer currentConfirmTotal = 0;
        if (params.getSpecificationList() != null) {
            currentConfirmTotal = params.getSpecificationList().stream()
                    .mapToInt(p -> p.getNumber() != null ? p.getNumber() : 0)
                    .sum();
        }

        //订单item的总数量
        int sumNumber = items.stream().mapToInt(i -> i.getNumber()).sum();

        // 确认number之和加上之前确认过的数量不能多于订单总数
        Integer totalConfirmNumber = previousConfirmNumber + currentConfirmTotal;
        if (totalConfirmNumber > sumNumber) {
            throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(),
                    "确认数量之和（" + totalConfirmNumber + "）不能大于订单总数（" + sumNumber + "）");
        }

        // 创建确认的item记录
        if (params.getSpecificationList() != null) {
            for (SalYtConfirmIncompleteParams param : params.getSpecificationList()) {
                Integer number = param.getNumber();
                Long specificationId = param.getSpecificationId();

                if (number == null || number <= 0) {
                    throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(), "请输入有效的确认数量");
                }
                if (specificationId == null) {
                    throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(), "请选择规格");
                }
                SalYtOrderSubItem confirmItem = null;
                //先查找该规格是否已经确认过，如果没有确认过则创建新的
                for (SalYtOrderSubItem existingConfirmItem : existingConfirmItems) {
                    if (existingConfirmItem.getSpecificationId().equals(specificationId)) {
                        confirmItem = existingConfirmItem;
                        break;
                    }
                }
                int confirmNumber=number;
                Long orderId=null;
                Long purchaseId=null;
                for(Long id:itemIdList) {
                    SalYtOrderSubItem item = salYtOrderSubItemMapper.selectById(id);
                    Long itemId = item.getId();
                    List<SalYtOrderSubItem> salYtOrderSubItems = salYtOrderSubItemMapper.selectByConfirmItemId(itemId);
                    int sum = salYtOrderSubItems.stream().mapToInt(i -> i.getNumber()).sum();
                    int itemWaitConfirmNumber=item.getNumber()-sum;
                    if(itemWaitConfirmNumber<=0){
                        continue;
                    }
                    int currentConfirmNumber=Math.min(confirmNumber,itemWaitConfirmNumber);
                    if (confirmItem == null) {
                        confirmItem = new SalYtOrderSubItem();
                        BeanUtils.copyProperties(item, confirmItem);
                        confirmItem.setId(null);
                        confirmItem.setSpecificationId(specificationId);
                        confirmItem.setNumber(currentConfirmNumber);
                        confirmItem.setConfirmItemId(id); // 设置关联的半成品ID
                        // 重置占用/入库/发货等数量字段，确认行应从零开始，不继承半成品父行的进度
                        confirmItem.setOccupyStoreNumber(0);
                        confirmItem.setOccupyTransitNumber(0);
                        confirmItem.setOccupyTransitPurchaseItemId(null);
                        confirmItem.setOccupyTransitEnterNumber(0);
                        confirmItem.setEnterNumber(0);
                        confirmItem.setDeliveryNumber(0);
                        confirmItem.setEndReturnNumber(0);
                        confirmItem.setApplyPurchaseNumber(0);
                        //设置供应商单价
                        Long supplierId = item.getSupplierId();
                        if (supplierId == null) {
                            throw new BizException(ExceptionCodeEnum.Not_Exists.getCode(), "这个半成品的默认供应商为空");
                        }
                        ProYtProductSpecificationSupplier specificationSupplier = proYtProductSpecificationSupplierMapper.selectBySpecificationIdAndSupplier(specificationId, supplierId);
                        if(specificationSupplier==null){
                            throw  new BizException(ExceptionCodeEnum.Not_Exists.getCode(),"该供应商没有该规格");
                        }
                        confirmItem.setSupplierPrice(specificationSupplier.getSupplierPrice());
                        salYtOrderSubItemMapper.insert(confirmItem);
                    }else {
                        //已被确认过则添加数量即可
                        confirmItem.setNumber(confirmItem.getNumber() + currentConfirmNumber);
                        salYtOrderSubItemMapper.updateById(confirmItem);
                    }


                    //给确认的成品添加确认半成品操作记录
                    salYtOrderSubItemOperationManager.confirm(confirmItem);
                    //给半成品添加确认记录
                    salYtOrderSubItemOperationManager.confirm(item);

                    //校验是否已采购，如果已采购，则需要添加采购单的已确认产品记录
                    PurYtPurchaseItem purYtPurchaseItem = purYtPurchaseItemMapper.selectByOrderSubItemId(id);

                    if (purYtPurchaseItem != null && PurchaseStatusEnum.Purchase.getKey().equals(purYtPurchaseItem.getStatus())) {
                        purchaseId = purYtPurchaseItem.getPurchaseId();
                        //设置确认数量为本次的确认数量
                        confirmItem.setNumber(number);
                        Long confirmPurchaseItemId = purYtPurchaseManager.confirmProduct(confirmItem, purYtPurchaseItem);
                        //采购单确认之后添加入库单
                        applicationEventPublisher.publishEvent(new PurchaseEvent(this,confirmPurchaseItemId,number));
                        //发送信息给采购单创建人，若这个订单中的半成品已经生成采购单，发送给采购单创建人。若这个订单中的半成品未生成采购单，不发送消息。
                        //得到order_sub_item_id,查询在采购单明细表（pur_yt_purchase_item）中是否存在数据，存在说明订单中的半成品已经生成采购单，反之相反
                        //发送信息给采购单创建人
                        //获取采购单创建人open_id
                        Long createUserId=purYtPurchaseItem.getCreateUser();
                        Integer tenantId = purYtPurchaseItem.getTenantId();
                        //根据用户id获取用户open_id
                        String openId = sysUserMapper.selectById(createUserId).getFeiShuUserId();
                        String appId = sysTenantConfigMapper.getByTenantIdAndConfigName(tenantId, TenantConfigEnum.FeiShuAppId.getKey()).getConfigValue();
                        String appSecret = sysTenantConfigMapper.getByTenantIdAndConfigName(tenantId, TenantConfigEnum.FeiShuAppSecret.getKey()).getConfigValue();

                        //获取产品id,根据orderSubItemId查询sal_yt_order_sub_item表中的productId，再去产品表就能查到产品的code
                        Long productId = salYtOrderSubItemMapper.selectById(id).getProductId();
                        //获取产品code（产品id）
                        String productCode = proYtProductMapper.selectById(productId).getCode();
                        //采购单号
                        String purchaseCode = purYtPurchaseMapper.selectById(purYtPurchaseItem.getPurchaseId()).getCode();


                        String message1 = "采购单号："+purchaseCode+"\n"+"产品规格已确认：";

                        List<SalYtConfirmIncompleteParams> specificationList= params.getSpecificationList();
                        //拼接产品规格信息
                        String message = jointProductSpecification(message1,specificationList,productCode);
                        try {
                            FeiShuUtil.sendTextMessage(appId,appSecret,openId,message);
                        } catch (Exception e) {
                            throw new BizException("发送消息失败：发送对象不属于当前飞书组织");
                        }
                        // FeiShuUtil.sendTextMessage(appId,appSecret,openId,message);
                        //反之不发送
                    }

                    // 发布发货事件，用于生成发货单
                    Long orderSubId = item.getOrderSubId();
                    SalYtOrderSub orderSub = salYtOrderSubMapper.selectById(orderSubId);
                    if (orderSub != null) {
                        orderId = orderSub.getOrderId();
                        applicationEventPublisher.publishEvent(new DeliveryEvent(this, orderId));
                    }
                    confirmNumber=confirmNumber-currentConfirmNumber;
                    if(confirmNumber<=0){
                        break;
                    }
                }
                if(purchaseId!=null){
                    //已经采购则修改库存数据，增加在途，并添加出入库记录(暂未考虑独立仓)
                    stoYtStoreManager.confirmInCompleted(specificationId,number,orderId,purchaseId);
                }

            }
        }

    }

    //拼接产品规格信息
    private String jointProductSpecification(String message,List<SalYtConfirmIncompleteParams> specificationList,String productCode){
        for (SalYtConfirmIncompleteParams specification : specificationList) {
            //查询产品规格名称
            String specificationName = proYtProductSpecificationMapper.selectById(specification.getSpecificationId()).getName();
            message += "\n" +"产品ID："+productCode+"，"+ "产品规格：" + specificationName + "，数量：" + specification.getNumber();
        }
        return message;
    }


    public List<SalYtReturnOrder> getReturnOrdersByOrderSubItem(Long orderSubItemId) {
        return salYtReturnOrderMapper.selectByOrderSubItemId(orderSubItemId);
    }

    /**
     * 确认发货
     *
     * @param params 发货参数列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void confirmDelivery(List<SalYtOrderDeliveryParams> params) {
        if (params == null || params.isEmpty()) {
            throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(), "发货参数不能为空");
        }

        for (SalYtOrderDeliveryParams param : params) {
            // 从参数中获取itemId和发货数量
            Long itemId = param.getItemId();
            Integer deliveryNumber = param.getDeliveryNumber();

            // 验证发货数量
            if (deliveryNumber <= 0) {
                throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(), "发货数量必须大于0");
            }

            // 检查item是否存在
            SalYtOrderSubItem item = salYtOrderSubItemMapper.selectById(itemId);
            if (item == null) {
                throw new BizException(ExceptionCodeEnum.Not_Exists.getCode(), "订单商品项不存在");
            }

            // 检查发货数量是否超过订单数量
            if (deliveryNumber > item.getNumber() - item.getDeliveryNumber()) {
                throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(), "发货数量不能超过待发货数量");
            }

            if (deliveryNumber == (item.getNumber() - item.getDeliveryNumber())) {
                // 更新状态
                item.setStatus(OrderSubItemStatusEnum.Delivered.getKey());
                //更新采购单item的状态
                PurYtPurchaseItem purYtPurchaseItem = purYtPurchaseItemMapper.selectByOrderSubItemId(itemId);
                if(purYtPurchaseItem!=null){
                    purYtPurchaseItem.setStatus(PurchaseStatusEnum.Delivery.getKey());
                    purYtPurchaseItemMapper.updateById(purYtPurchaseItem);
                    //检测采购单是否已经可以完成
                    List<PurYtPurchaseItem> purYtPurchaseItems = purYtPurchaseItemMapper.selectCompletedByPurchaseIdAndStatus(purYtPurchaseItem.getPurchaseId(), PurchaseStatusEnum.Purchase.getKey());
                    if(purYtPurchaseItems==null || purYtPurchaseItems.isEmpty()){
                        PurYtPurchase purYtPurchase = new PurYtPurchase();
                        purYtPurchase.setId(purYtPurchaseItem.getPurchaseId());
                        purYtPurchase.setStatus(PurchaseStatusEnum.Delivery.getKey());
                        purYtPurchase.setCompletedTime(LocalDateTime.now());
                        purYtPurchaseMapper.updateById(purYtPurchase);
                    }
                }else {
                    throw new BizException("该订单还未采购");
                }

            }
            item.setDeliveryNumber(item.getDeliveryNumber() + deliveryNumber);

            salYtOrderSubItemMapper.updateById(item);

            //添加发货操作记录
            salYtOrderSubItemOperationManager.confirmDelivery(item.getId(), deliveryNumber);
        }

    }

    /**
     * 确认完成
     *
     * @param orderId 父订单ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void confirmComplete(Long orderId) {
        //将父订单状态改为已完成，插入完成时间是一样的
        SalYtOrder order = salYtOrderMapper.selectById(orderId);
        order.setOrderFinishTime(LocalDateTime.now());
        salYtOrderMapper.updateById(order);

        //查询出父订单下所有子订单的集合
        List<SalYtOrderSub> orderSubs = salYtOrderSubMapper.selectSalYtOrderSubByOrderId(orderId);
        if (orderId == null) {
            throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(), "父订单ID不能为空");
        }

        // 查询子订单是否存在
//        SalYtOrderSub orderSub = salYtOrderSubMapper.selectById(orderSubId);
//        if (orderSub == null) {
//            throw new BizException(ExceptionCodeEnum.Not_Exists.getCode(), "子订单不存在");
//        }

        //循环子订单
        for(SalYtOrderSub orderSub:orderSubs) {
            // 查询该子订单下的所有商品项
            List<SalYtOrderSubItem> items = salYtOrderSubItemMapper.selectSalYtOrderSubItemByOrderSubId(orderSub.getId());
            if (items == null || items.isEmpty()) {
                //  throw new BizException(ExceptionCodeEnum.Not_Exists.getCode(), "子订单下没有商品项");
                continue;
            }

            // 将所有商品项的状态改为已完成
            for (SalYtOrderSubItem item : items) {
                item.setStatus(OrderSubItemStatusEnum.Completed.getKey());
                salYtOrderSubItemMapper.updateById(item);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void closeOrder(SalYtOrderCloseParams params) {
        if (params == null || params.getOrderId() == null) {
            throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(), "父订单ID不能为空");
        }
//        if (params.getAmount().compareTo(BigDecimal.ZERO) < 0 || params.getOtherAmount().compareTo(BigDecimal.ZERO) < 0) {
//            throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(), "关闭金额不能小于0");
//        }

        SalYtOrder order = salYtOrderMapper.selectById(params.getOrderId());
        if (order == null || Integer.valueOf(1).equals(order.getIsDeleted())) {
            throw new BizException(ExceptionCodeEnum.Not_Exists.getCode(), "订单不存在");
        }

        SalYtOrderClosePreviewVo preview = getCloseOrderPreview(params.getOrderId());
        if (!Boolean.TRUE.equals(preview.getCanClose())) {
            throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(), preview.getMessage());
        }

        List<SalYtOrderSubItem> orderItems = salYtOrderSubItemMapper.selectSalYtOrderSubItemListByOrderId(params.getOrderId());
        if (orderItems == null || orderItems.isEmpty()) {
            throw new BizException(ExceptionCodeEnum.Not_Exists.getCode(), "订单商品项不存在");
        }

        Map<Long, SalYtOrderSubItem> itemMap = orderItems.stream()
                .collect(Collectors.toMap(SalYtOrderSubItem::getId, item -> item, (oldValue, newValue) -> oldValue));
        Map<Long, Integer> refundQtyMap = preview.getItemList().stream()
                .filter(item -> item.getClosableQty() != null && item.getClosableQty() > 0)
                .collect(Collectors.toMap(SalYtOrderClosePreviewItemVo::getOrderSubItemId, SalYtOrderClosePreviewItemVo::getClosableQty));

        order.setEndOtherAmount(params.getOtherAmount());
        BigDecimal closeTotalAmount = preview.getItemList().stream()
                .map(item -> {
                    BigDecimal price = item.getPrice() == null ? BigDecimal.ZERO : item.getPrice();
                    int closableQty = item.getClosableQty() == null ? 0 : item.getClosableQty();
                    return price.multiply(BigDecimal.valueOf(closableQty));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setEndAmount(closeTotalAmount);
        salYtOrderMapper.updateById(order);

        for (SalYtOrderClosePreviewItemVo previewItem : preview.getItemList()) {
            Integer refundQty = refundQtyMap.getOrDefault(previewItem.getOrderSubItemId(), 0);
            if (refundQty <= 0) {
                continue;
            }
            SalYtOrderSubItem orderItem = itemMap.get(previewItem.getOrderSubItemId());
            if (orderItem == null) {
                continue;
            }
            releaseCloseOrderOccupy(order, orderItem);
            cleanupCloseOrderDelivery(orderItem);
            orderItem.setEndReturnNumber(refundQty);
            if ((previewItem.getDeliveredQty() == null ? 0 : previewItem.getDeliveredQty()) > 0) {
                orderItem.setStatus(OrderSubItemStatusEnum.Delivered.getKey());
            } else {
                orderItem.setStatus(OrderSubItemStatusEnum.Closed.getKey());
            }
            orderItem.setOccupyStoreNumber(0);
            orderItem.setOccupyTransitNumber(0);
            orderItem.setOccupyTransitEnterNumber(0);
            orderItem.setOccupyTransitPurchaseItemId(null);
            salYtOrderSubItemMapper.updateById(orderItem);
            BigDecimal closeAmount = (orderItem.getPrice() == null ? BigDecimal.ZERO : orderItem.getPrice())
                    .multiply(BigDecimal.valueOf(refundQty.longValue()));
            salYtOrderSubItemOperationManager.closeOrderOperation(refundQty, closeAmount, orderItem.getId());
        }
    }

    public SalYtOrderClosePreviewVo getCloseOrderPreview(Long orderId) {
        if (orderId == null) {
            throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(), "父订单ID不能为空");
        }

        SalYtOrder order = salYtOrderMapper.selectById(orderId);
        if (order == null || Integer.valueOf(1).equals(order.getIsDeleted())) {
            throw new BizException(ExceptionCodeEnum.Not_Exists.getCode(), "订单不存在");
        }

        SalYtOrderClosePreviewVo preview = new SalYtOrderClosePreviewVo();
        if (!Boolean.TRUE.equals(order.getIsInboundDelivery())) {
            preview.setMessage("仅入库发货订单支持关闭");
            return preview;
        }

        List<SalYtOrderSubItem> allOrderItems = salYtOrderSubItemMapper.selectSalYtOrderSubItemListByOrderId(orderId);
        if (allOrderItems == null || allOrderItems.isEmpty()) {
            preview.setMessage("订单商品项不存在");
            return preview;
        }

        List<SalYtOrderSubItem> completeItems = allOrderItems.stream()
                .filter(item -> item.getSpecificationId() != null)
                .collect(Collectors.toList());
        preview.setHasIncompleteItem(completeItems.size() != allOrderItems.size());
        if (completeItems.isEmpty()) {
            preview.setMessage("订单暂无可关闭的成品项");
            return preview;
        }

        List<Long> orderSubItemIds = completeItems.stream().map(SalYtOrderSubItem::getId).collect(Collectors.toList());
        Map<Long, Integer> applyPurchaseQtyMap = buildNumberMap(purYtApplyPurchaseMapper.sumActiveNumberByOrderSubItemIds(orderSubItemIds));
        Map<Long, Integer> temporaryPurchaseQtyMap = buildNumberMap(purYtPurchaseItemMapper.sumTemporaryNumberByOrderSubItemIds(orderSubItemIds));
        Map<Long, Integer> waitDeliveryQtyMap = buildNumberMap(stoYtDeliveryItemMapper.sumWaitDeliveryNumberByOrderSubItemIds(orderSubItemIds));

        Map<Long, String> productCodeCache = new HashMap<>();
        Map<Long, String> specNameCache = new HashMap<>();
        Map<Long, String> specImageCache = new HashMap<>();

        for (SalYtOrderSubItem item : completeItems) {
            SalYtOrderClosePreviewItemVo itemVo = new SalYtOrderClosePreviewItemVo();
            Long orderSubItemId = item.getId();
            Long specificationId = item.getSpecificationId();
            Long productId = item.getProductId();

            int totalNumber = item.getNumber() == null ? 0 : item.getNumber();
            int enterNumber = item.getEnterNumber() == null ? 0 : item.getEnterNumber();
            int deliveredQty = item.getDeliveryNumber() == null ? 0 : item.getDeliveryNumber();
            int pendingPurchaseQty = applyPurchaseQtyMap.getOrDefault(orderSubItemId, 0)
                    + temporaryPurchaseQtyMap.getOrDefault(orderSubItemId, 0);
            int waitDeliveryQty = waitDeliveryQtyMap.getOrDefault(orderSubItemId, 0);
            int waitEnterQty = Math.max(totalNumber - enterNumber - pendingPurchaseQty, 0);
            int waitPackageQty = Math.max(enterNumber - deliveredQty - waitDeliveryQty, 0);
            int closableQty = waitEnterQty + waitPackageQty;

            boolean customerStore = order.getCustomerId() != null
                    && salYtCustomerStoreMapper.selectByCustomerIdAndSpecificationId(order.getCustomerId(), specificationId) != null;

            itemVo.setOrderSubItemId(orderSubItemId);
            itemVo.setProductId(productId);
            itemVo.setSpecificationId(specificationId);
            itemVo.setProductCode(productCodeCache.computeIfAbsent(productId, id -> {
                ProYtProduct product = proYtProductMapper.selectById(id);
                return product == null ? "" : product.getCode();
            }));
            itemVo.setSpecName(specNameCache.computeIfAbsent(specificationId, id -> {
                List<ProYtProductSpecificationItem> specItems = proYtProductSpecificationItemMapper.selectByProductSpecificationId(id);
                if (specItems == null || specItems.isEmpty()) {
                    return "";
                }
                return specItems.stream()
                        .map(ProYtProductSpecificationItem::getCategorySpecificationItemValue)
                        .filter(Objects::nonNull)
                        .collect(Collectors.joining("-"));
            }));
            itemVo.setSpecImage(specImageCache.computeIfAbsent(specificationId, id -> {
                List<ProYtProductFile> fileList = proYtProductFileMapper.selectByMasterIdAndType(id, ProductFilesTypeEnum.specification.getKey());
                if (fileList == null || fileList.isEmpty()) {
                    return "";
                }
                return fileList.get(0).getUrl();
            }));
            itemVo.setPrice(item.getPrice());
            itemVo.setTotalNumber(totalNumber);
            itemVo.setPendingPurchaseQty(pendingPurchaseQty);
            itemVo.setWaitEnterQty(waitEnterQty);
            itemVo.setWaitPackageQty(waitPackageQty);
            itemVo.setWaitDeliveryQty(waitDeliveryQty);
            itemVo.setDeliveredQty(deliveredQty);
            itemVo.setClosableQty(closableQty);
            itemVo.setCustomerStore(customerStore);
            preview.getItemList().add(itemVo);

            preview.setPendingPurchaseQty(preview.getPendingPurchaseQty() + pendingPurchaseQty);
            preview.setWaitEnterQty(preview.getWaitEnterQty() + waitEnterQty);
            preview.setWaitPackageQty(preview.getWaitPackageQty() + waitPackageQty);
            preview.setWaitDeliveryQty(preview.getWaitDeliveryQty() + waitDeliveryQty);
            preview.setDeliveredQty(preview.getDeliveredQty() + deliveredQty);
            preview.setClosableQty(preview.getClosableQty() + closableQty);
            if (customerStore) {
                preview.setHasCustomerStoreItem(Boolean.TRUE);
            }
        }

        if (Boolean.TRUE.equals(preview.getHasIncompleteItem())) {
            preview.setMessage("订单包含半成品，不支持关闭");
            return preview;
        }
        if (Boolean.TRUE.equals(preview.getHasCustomerStoreItem())) {
            preview.setMessage("订单包含独立仓商品，不支持关闭");
            return preview;
        }
        if (preview.getPendingPurchaseQty() > 0) {
            preview.setMessage("订单仍存在待采购数量，不允许关闭");
            return preview;
        }
        if (preview.getWaitDeliveryQty() > 0) {
            preview.setMessage("订单仍存在待发货数量，请先退回待打包");
            return preview;
        }
        if (preview.getClosableQty() <= 0) {
            preview.setMessage("订单暂无可关闭数量");
            return preview;
        }
        preview.setCanClose(Boolean.TRUE);
        return preview;
    }

    @Transactional
    public StoYtStoreRecord enter(StoYtStoreOrderAddParams params) {
        Long purchaseItemId = params.getPurchaseItemId();
        StoYtStoreRecord stoYtStoreRecord = new StoYtStoreRecord();
        //记录分配了数量的子订单code
        StringBuffer orderSubCode = new StringBuffer();
        //记录分配了数量的子订单code和number的map，给入库单操作记录使用
        List<HashMap<Object, Object>> allocationOrderSubCodeList = new ArrayList<>();
        stoYtStoreRecord.setAllocationOrderSubCodeList(allocationOrderSubCodeList);
        stoYtStoreRecord.setIsPurchaseCreateOperation(true);
        PurYtPurchaseItem purYtPurchaseItem = purYtPurchaseItemMapper.selectById(purchaseItemId);
        if (purYtPurchaseItem == null) {
            throw new BizException(ExceptionCodeEnum.Not_Exists.getCode(), "该采购单已被删除");
        }
        Long orderSubItemId = purYtPurchaseItem.getOrderSubItemId();
        if (orderSubItemId != null) {
            //查找关联的订单是否需要入库，并且判断状态是否需要改变，并且判断是否需要生成发货单
            SalYtOrderSubItem salYtOrderSubItem = salYtOrderSubItemMapper.selectById(orderSubItemId);
            Long orderSubId = salYtOrderSubItem.getOrderSubId();
            SalYtOrderSub salYtOrderSub = salYtOrderSubMapper.selectById(orderSubId);
            // 使用主订单号写入库存历史记录，确保历史列表"订单号"列显示正确
            SalYtOrder mainOrder = salYtOrderMapper.selectById(salYtOrderSub.getOrderId());
            String mainOrderCode = mainOrder != null ? mainOrder.getCode() : salYtOrderSub.getSubCode();
            Integer currentEnterNumber = salYtOrderSubItem.getEnterNumber() != null ? salYtOrderSubItem.getEnterNumber() : 0;
            Integer endReturnNumber = salYtOrderSubItem.getEndReturnNumber() != null ? salYtOrderSubItem.getEndReturnNumber() : 0;
            Integer effectiveTotalNumber = Math.max((salYtOrderSubItem.getNumber() != null ? salYtOrderSubItem.getNumber() : 0) - endReturnNumber, 0);
            //待入库数量
            Integer waitEnterNumber = Math.max(effectiveTotalNumber - currentEnterNumber, 0);
            // 只有实际有待入库数量时才关联订单号，额外入库（waitEnterNumber=0）不应关联订单号
            if (waitEnterNumber > 0) {
                orderSubCode.append(mainOrderCode).append(",");
            }
            //本次入库数量
            Integer number = params.getEnterNumber();
            Integer restNumber=0;
            Integer realEnterNumber=0;
            //因为在前端做了处理，所以只会走待入库数量<入库数量的情况，等于入库数量和大于入库数量两个判断不会走，后续考虑删除前两个判断
            if (waitEnterNumber <= 0) {
                // 关单或已入满后，剩余采购入库量直接作为可用库存，不再回写订单状态
                restNumber = params.getEnterNumber();
            } else if (waitEnterNumber > number) {
                //待入库数量大于入库数量，直接入库
                salYtOrderSubItem.setEnterNumber(currentEnterNumber + number);
                HashMap<Object, Object> map = new HashMap<>();
                map.put("orderCode", mainOrderCode);
                map.put("number", number);
                allocationOrderSubCodeList.add(map);
            } else if (waitEnterNumber.equals(number)) {
                //待入库数量等于入库数量
                salYtOrderSubItem.setEnterNumber(currentEnterNumber + waitEnterNumber);
                if (!OrderSubItemStatusEnum.Closed.getKey().equals(salYtOrderSubItem.getStatus())) {
                    salYtOrderSubItem.setStatus(OrderSubItemStatusEnum.WaitPackage.getKey());
                }
                HashMap<Object, Object> map = new HashMap<>();
                map.put("orderCode", mainOrderCode);
                map.put("number", number);
                allocationOrderSubCodeList.add(map);
            } else {
                //待入库数量小于入库数量
                salYtOrderSubItem.setEnterNumber(effectiveTotalNumber);
                if (!OrderSubItemStatusEnum.Closed.getKey().equals(salYtOrderSubItem.getStatus())) {
                    salYtOrderSubItem.setStatus(OrderSubItemStatusEnum.WaitPackage.getKey());
                }
                if (waitEnterNumber > 0) {
                    // 绑定订单有实际剩余量，记录分配信息，并将溢出部分继续分配给其他在途占用订单
                    HashMap<Object, Object> map = new HashMap<>();
                    map.put("orderCode", mainOrderCode);
                    map.put("number", waitEnterNumber);
                    allocationOrderSubCodeList.add(map);
                    Integer enableEnterNumber = number - waitEnterNumber;
                    restNumber = allocationOccupyTransit(enableEnterNumber, salYtOrderSubItem.getSpecificationId(), orderSubCode, allocationOrderSubCodeList);
                } else {
                    // waitEnterNumber=0：绑定订单已全部入库，额外入库量直接作为可用库存，不自动分配给其他订单
                    restNumber = params.getEnterNumber();
                }
                // TODO 检查这段代码， 感觉有问题，理论上应该是最后剩下来的数量 直接赋值给number
                //该订单入库数量
                number =salYtOrderSubItem.getNumber();
            }
            if (waitEnterNumber > 0) {
                // 关闭订单释放占用时，需要区分“仍在在途”与“已从在途转成占用库存”的数量
                int actualEnteredForOrder = Math.min(waitEnterNumber, params.getEnterNumber());
                int occupyTransitNumber = salYtOrderSubItem.getOccupyTransitNumber() != null ? salYtOrderSubItem.getOccupyTransitNumber() : 0;
                int occupyTransitEnterNumber = salYtOrderSubItem.getOccupyTransitEnterNumber() != null ? salYtOrderSubItem.getOccupyTransitEnterNumber() : 0;
                int restTransitOccupyNumber = Math.max(occupyTransitNumber - occupyTransitEnterNumber, 0);
                int transitEnterChange = Math.min(actualEnteredForOrder, restTransitOccupyNumber);
                salYtOrderSubItem.setOccupyTransitEnterNumber(occupyTransitEnterNumber + transitEnterChange);
            }
            salYtOrderSubItemMapper.updateById(salYtOrderSubItem);
            // 只有实际为绑定订单入库时才记录操作，使用实际入库量避免额外入库产生多余进度记录
            if (waitEnterNumber > 0) {
                int actualEnteredForOrder = Math.min(waitEnterNumber, params.getEnterNumber());
                salYtOrderSubItemOperationManager.enterStoreOperation(actualEnteredForOrder, salYtOrderSubItem.getId());
                stoYtStoreRecord.setIsPurchaseCreateOperation(false);
            }
            stoYtStoreRecord.setRealTransitChange(params.getEnterNumber());
            stoYtStoreRecord.setOccupyTransitChange(params.getEnterNumber()-restNumber);
            stoYtStoreRecord.setEnableTransitChange(restNumber);
//            //占用库存修改
//            stoYtStoreRecord.setOccupyStoreChange(number);

            // 检测是否要生成发货单
            if (mainOrder != null) {
                applicationEventPublisher.publishEvent(new DeliveryEvent(this, mainOrder.getId()));
            }
        } else {
            Long specificationId = purYtPurchaseItem.getSpecificationId();
            Long customerId = purYtPurchaseItem.getCustomerId();
            SalYtCustomerStore salYtCustomerStore = salYtCustomerStoreMapper.selectByCustomerIdAndSpecificationId(customerId, specificationId);
            //如果是独立仓需要将stoYtStoreRecord赋为空，这样才能在入库单入库时，走独立仓采购入库的分支
            stoYtStoreRecord = null;
            if (salYtCustomerStore == null) {
                stoYtStoreRecord = new StoYtStoreRecord();
                stoYtStoreRecord.setIsPurchaseCreateOperation(true);
                //不是来源于订单的采购单分配 但因为不是为独立仓采购的,所以可以为其他订单分配库存
                Integer restNumber = allocationOccupyTransit(params.getEnterNumber(), specificationId, orderSubCode, allocationOrderSubCodeList);
                stoYtStoreRecord.setRealTransitChange(params.getEnterNumber());
                stoYtStoreRecord.setOccupyTransitChange(params.getEnterNumber() - restNumber);
                stoYtStoreRecord.setEnableTransitChange(restNumber);
            }
        }
        if (!orderSubCode.isEmpty() && stoYtStoreRecord != null) {
            orderSubCode.deleteCharAt(orderSubCode.length() - 1);
            stoYtStoreRecord.setOrderSubCode(orderSubCode.toString());
            stoYtStoreRecord.setOrderCode(orderSubCode.toString());
        }
        return stoYtStoreRecord;
    }


    public Integer allocationOccupyTransit(Integer enableEnterNumber, Long specificationId, StringBuffer orderSubCode, List<HashMap<Object, Object>> allocationOrderSubCodeList) {
        List<SalYtOrderSubItem> occupyTransitItemList = salYtOrderSubItemMapper.selectOccupyTransitSpecification(specificationId);
        for (SalYtOrderSubItem occupyTransitItem : occupyTransitItemList) {
            if (enableEnterNumber <= 0) {
                enableEnterNumber = 0;
                break;
            }
            Integer occupyItemWaitEnterNumber = occupyTransitItem.getOccupyTransitNumber() - occupyTransitItem.getOccupyTransitEnterNumber();
            //如果待入库数量小于占用在途待入库数量，则入库待入库数量,也就是说占用在途的待入库数量和全部待入库数量，取小值
            if (occupyTransitItem.getNumber() - occupyTransitItem.getEnterNumber() < occupyItemWaitEnterNumber) {
                occupyItemWaitEnterNumber = occupyTransitItem.getNumber() - occupyTransitItem.getEnterNumber();
            }
            if (occupyItemWaitEnterNumber <= 0) {
                continue;
            }
            SalYtOrder salYtOrder= salYtOrderMapper.selectByOrderItemId(occupyTransitItem.getId());
            if (salYtOrder == null) {
                continue;
            }

            orderSubCode.append(salYtOrder.getCode()).append(",");

            Integer currentEnterNumber=0;
            if (enableEnterNumber >= occupyItemWaitEnterNumber) {
                //可入库数量大于等于待入库数量，则直接入库待入库数量，可入库数量减去本次入库数量后再分配给下一个订单入库
                occupyTransitItem.setEnterNumber(occupyTransitItem.getEnterNumber() + occupyItemWaitEnterNumber);
                occupyTransitItem.setOccupyTransitEnterNumber(occupyTransitItem.getOccupyTransitEnterNumber()+occupyItemWaitEnterNumber);
                //如果入库后数量等于订单数量，则状态变成待打包
                if (occupyTransitItem.getEnterNumber().equals(occupyTransitItem.getNumber())) {
                    occupyTransitItem.setStatus(OrderSubItemStatusEnum.WaitPackage.getKey());
                }
                currentEnterNumber=occupyItemWaitEnterNumber;
                HashMap<Object, Object> map = new HashMap<>();
                map.put("orderCode", salYtOrder.getCode());
                map.put("number", occupyItemWaitEnterNumber);
                allocationOrderSubCodeList.add(map);
            } else {
                //可入库数量小于等于待入库数量，则入库可入库数量，则可入库数量减去本次待入库数量后就会小于0，就会结束分配
                occupyTransitItem.setEnterNumber(occupyTransitItem.getEnterNumber() + enableEnterNumber);
                occupyTransitItem.setOccupyTransitEnterNumber(occupyTransitItem.getOccupyTransitEnterNumber()+enableEnterNumber);
                currentEnterNumber=enableEnterNumber;
                HashMap<Object, Object> map = new HashMap<>();
                map.put("orderCode", salYtOrder.getCode());
                map.put("number", enableEnterNumber);
                allocationOrderSubCodeList.add(map);
            }
            salYtOrderSubItemMapper.updateById(occupyTransitItem);
            enableEnterNumber = enableEnterNumber - currentEnterNumber;
            // 检测是否要生成发货单
//            applicationEventPublisher.publishEvent(new DeliveryEvent(this, occupyTransitItem.getId()));

            //订单操作记录
            salYtOrderSubItemOperationManager.enterStoreOperation(enableEnterNumber, occupyTransitItem.getId());
        }

        return enableEnterNumber;
    }

    /**
     * 对在途占用订单进行精确入库（无采购单关联）
     * 直接更新订单子项的 enter_number 和 occupy_transit_enter_number，然后通过入库单更新库存
     */
    @Transactional
    public void enterTransitOccupy(Long orderSubItemId, Integer enterNumber, List<Long> storeOrderIdList) {
        SalYtOrderSubItem subItem = salYtOrderSubItemMapper.selectById(orderSubItemId);
        if (subItem == null) {
            throw new com.qiaomoyun.Exception.BizException("订单项不存在，数据可能已变更，请刷新后重试");
        }
        int occupyLeft = (subItem.getOccupyTransitNumber() != null ? subItem.getOccupyTransitNumber() : 0)
            - (subItem.getOccupyTransitEnterNumber() != null ? subItem.getOccupyTransitEnterNumber() : 0);
        if (enterNumber > occupyLeft) {
            throw new com.qiaomoyun.Exception.BizException("入库数量超过在途占用数量，数据可能已变更，请刷新后重试");
        }
        subItem.setEnterNumber(subItem.getEnterNumber() + enterNumber);
        subItem.setOccupyTransitEnterNumber(
            (subItem.getOccupyTransitEnterNumber() != null ? subItem.getOccupyTransitEnterNumber() : 0) + enterNumber);
        if (subItem.getEnterNumber().equals(subItem.getNumber())) {
            subItem.setStatus(com.qiaomoyun.eunm.yt.OrderSubItemStatusEnum.WaitPackage.getKey());
        }
        salYtOrderSubItemMapper.updateById(subItem);
        // 订单操作记录
        salYtOrderSubItemOperationManager.enterStoreOperation(enterNumber, orderSubItemId,-1L);
        // 更新库存：在途转库存（占用在途 → 占用库存）
        stoYtStoreManager.reduceTransitStockToStoreStock(
            subItem.getSpecificationId(), 0, enterNumber, enterNumber);

        // 同步扣减入库单的 enterNumber，使欠数正确递减，并生成库存历史记录
        if (storeOrderIdList != null && !storeOrderIdList.isEmpty()) {
            // 获取主订单号
            SalYtOrder relatedOrder = salYtOrderMapper.selectByOrderItemId(orderSubItemId);
            String orderCode = relatedOrder != null ? relatedOrder.getCode() : null;

            // 获取采购单ID（取第一个有效入库单）
            Long purchaseId = null;
            for (Long storeId : storeOrderIdList) {
                StoYtStoreOrder s = stoYtStoreOrderMapper.selectById(storeId);
                if (s != null && s.getPurchaseId() != null) {
                    purchaseId = s.getPurchaseId();
                    break;
                }
            }

            // 发布 StoreChangeEvent，触发库存历史记录写入（与占用订单入库类型保持一致）
            StoYtStoreRecord transitRecord = new StoYtStoreRecord();
            transitRecord.setSpecificationId(subItem.getSpecificationId());
            transitRecord.setOccupyTransitChange(enterNumber);
            transitRecord.setRealTransitChange(enterNumber);
            transitRecord.setEnableTransitChange(0);
            if (orderCode != null) {
                transitRecord.setOrderCode(orderCode);
            }
            applicationEventPublisher.publishEvent(new StoreChangeEvent(
                this, StoreEnterOutTypeEnum.purchaseEnterStore.getKey(), transitRecord, purchaseId));

            int remaining = enterNumber;
            for (Long storeId : storeOrderIdList) {
                if (remaining <= 0) break;
                StoYtStoreOrder storeOrder = stoYtStoreOrderMapper.selectById(storeId);
                if (storeOrder == null) continue;
                int needEnter = storeOrder.getTotalNumber() - storeOrder.getEnterNumber();
                if (needEnter <= 0) continue;
                int actual = Math.min(needEnter, remaining);
                storeOrder.setEnterNumber(storeOrder.getEnterNumber() + actual);
                stoYtStoreOrderMapper.updateById(storeOrder);
                // 生成入库单操作记录
                StoYtStoreOrderOperation operation = new StoYtStoreOrderOperation();
                operation.setStoreOrderId(storeOrder.getId());
                operation.setType(1);
                operation.setNumber(actual);
                List<StoYtStoreOrderOperationDetail> details = new java.util.ArrayList<>();
                if (orderCode != null) {
                    StoYtStoreOrderOperationDetail detail = new StoYtStoreOrderOperationDetail();
                    detail.setOrderId(storeOrder.getId());
                    detail.setOrderCode(orderCode);
                    detail.setNumber(actual);
                    details.add(detail);
                }
                operation.setOperationDetail(details);
                stoYtStoreOrderOperationMapper.insert(operation);
                remaining -= actual;

                //添加采购单操作记录
                purchaseId = storeOrder.getPurchaseId();
                StoYtStoreOrderAddParams stoYtStoreOrderAddParams = new StoYtStoreOrderAddParams();
                stoYtStoreOrderAddParams.setId(storeId);
                stoYtStoreOrderAddParams.setEnterNumber(actual);
                StoYtStoreRecord stoYtStoreRecord = new StoYtStoreRecord();
                stoYtStoreRecord.setIsPurchaseCreateOperation(true);
                purYtPurchaseManager.enter(stoYtStoreOrderAddParams, stoYtStoreRecord);
            }
        }
        SalYtOrder relatedOrder = salYtOrderMapper.selectByOrderItemId(orderSubItemId);
        if (relatedOrder != null) {
            applicationEventPublisher.publishEvent(new DeliveryEvent(this, relatedOrder.getId()));
        }
    }

    @Transactional
    public void delivery(StoYtDelivery params) {
        Long deliveryId = params.getId();
        StoYtDelivery delivery = stoYtDeliveryMapper.selectById(deliveryId);
        List<StoYtDeliveryItem> deliveryItemList = stoYtDeliveryItemMapper.selectGroupOrderSubItemByDeliveryId(deliveryId);
        Set<Long> relatedOrderIds = new HashSet<>();
        for (StoYtDeliveryItem deliveryItem : deliveryItemList) {
            //修改orderSubItem的发货数量并且判断是否需要修改该orderSubItem的状态为已发货
            Long orderSubItemId = deliveryItem.getOrderSubItemId();
            Integer number = deliveryItem.getNumber();
            SalYtOrderSubItem salYtOrderSubItem = salYtOrderSubItemMapper.selectById(orderSubItemId);
            salYtOrderSubItem.setDeliveryNumber(salYtOrderSubItem.getDeliveryNumber() + number);
            if (salYtOrderSubItem.getDeliveryNumber() > (salYtOrderSubItem.getNumber() + salYtOrderSubItem.getDeliveryNumber())) {
                throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(), "发货数量大于了下单数量");
            }
            if (salYtOrderSubItem.getDeliveryNumber().equals(salYtOrderSubItem.getNumber())) {
                //判断订单是不是全部发货完了，订单全部发货完成向父订单表将订单完成时间补充
                 //获取当前子订单id
                Long orderSubId = salYtOrderSubItem.getOrderSubId();
                //获取父订单id
                SalYtOrderSub orderSub = salYtOrderSubMapper.selectById(orderSubId);
                Long orderId = orderSub.getOrderId();
                //查询父订单下其他子订单是否都已经发货完成（排除当前orderSubId）
                List<SalYtOrderSubItem> orderSubItemsList = salYtOrderSubMapper.selectNotDeliveredOrderSubByOrderId(orderId, orderSubId);
                if(!orderSubItemsList.isEmpty()){
                    //不为空说明有多个子订单
                    Boolean isAllDelivered = true;
                    for(SalYtOrderSubItem orderSubItem:orderSubItemsList){
                        if(orderSubItem.getStatus().equals("0")||orderSubItem.getStatus().equals("1")||orderSubItem.getStatus().equals("2")||orderSubItem.getStatus().equals("3")){
                            //只要有一个没有已发货
                            isAllDelivered=false;
                            break;
                        }
                    }
                    if(isAllDelivered){
                        //父订单完成
                        SalYtOrder salYtOrder = salYtOrderMapper.selectById(orderId);
                        salYtOrder.setOrderFinishTime(LocalDateTime.now());
                        if(salYtOrder.getShippingReceiveStatus()!=null) {
                            if (salYtOrder.getShippingReceiveStatus().equals(2)) {
                                //如果订单客户运费回款状态为已完成，则插入客户运费回款完成时间
                                salYtOrder.setShippingReceiveFinishTime(LocalDateTime.now());
                            }
                        }
                        salYtOrderMapper.updateById(salYtOrder);
                    }
                }else{
                    //为空说明只有当前一个子订单，则父订单完成
                    SalYtOrder salYtOrder = salYtOrderMapper.selectById(orderId);
                    salYtOrder.setOrderFinishTime(LocalDateTime.now());
                    if(salYtOrder.getShippingReceiveStatus()!=null) {
                        if (salYtOrder.getShippingReceiveStatus().equals(2)) {
                            //如果订单客户运费回款状态为已完成，则插入客户运费回款完成时间
                            //当客户运费回款完成并且发货完毕才有客户运费回款完成时间
                            salYtOrder.setShippingReceiveFinishTime(LocalDateTime.now());
                        }
                    }
                    salYtOrderMapper.updateById(salYtOrder);
                }

                salYtOrderSubItem.setStatus(OrderSubItemStatusEnum.Delivered.getKey());
            }
            salYtOrderSubItemMapper.updateById(salYtOrderSubItem);

            //添加订单发货的操作记录（携带所属订单号，便于多订单发货单场景区分）
            String orderCode = null;
            Long orderSubId = salYtOrderSubItem.getOrderSubId();
            if (orderSubId != null) {
                SalYtOrderSub orderSub = salYtOrderSubMapper.selectById(orderSubId);
                if (orderSub != null && orderSub.getOrderId() != null) {
                    relatedOrderIds.add(orderSub.getOrderId());
                    SalYtOrder deliveryOrder = salYtOrderMapper.selectById(orderSub.getOrderId());
                    if (deliveryOrder != null) {
                        orderCode = deliveryOrder.getCode();
                    }
                }
            }
            salYtOrderSubItemOperationManager.delivery(deliveryItem, delivery, orderCode);
        }
        for (Long relatedOrderId : relatedOrderIds) {
            applicationEventPublisher.publishEvent(new DeliveryEvent(this, relatedOrderId));
        }
    }

    @Transactional
    public void takePackage(List<StoYtDeliveryBox> params) {
        Long deliveryId = params.get(0).getDeliveryId();
        //修改发货单Item关联订单的状态
        List<StoYtDeliveryItem> deliveryItemList = stoYtDeliveryItemMapper.selectGroupOrderSubItemByDeliveryId(deliveryId);
        for (StoYtDeliveryItem deliveryItem : deliveryItemList) {
            Long orderSubItemId = deliveryItem.getOrderSubItemId();
            Integer packageNumber = stoYtDeliveryItemMapper.selectPackageNumberByOrderSubItemId(orderSubItemId);
            SalYtOrderSubItem salYtOrderSubItem = salYtOrderSubItemMapper.selectById(orderSubItemId);
            if (packageNumber == null) {
                packageNumber = 0;
            }
            //打包中的数量+已经发货的数量=总共打包的数量
            packageNumber = salYtOrderSubItem.getDeliveryNumber() + packageNumber;
            if (packageNumber > salYtOrderSubItem.getNumber()) {
                throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(), "打包数量大于了下单数量");
            }
            if (packageNumber.equals(salYtOrderSubItem.getNumber())) {
                salYtOrderSubItem.setStatus(OrderSubItemStatusEnum.WaitDelivery.getKey());
            }
            salYtOrderSubItemMapper.updateById(salYtOrderSubItem);
        }
    }

    @Transactional
    public void takePackage(Long deliveryId) {
        //修改发货单Item关联订单的状态
        List<StoYtDeliveryItem> deliveryItemList = stoYtDeliveryItemMapper.selectGroupOrderSubItemByDeliveryId(deliveryId);
        for (StoYtDeliveryItem deliveryItem : deliveryItemList) {
            Long orderSubItemId = deliveryItem.getOrderSubItemId();
            Integer packageNumber = stoYtDeliveryItemMapper.selectPackageNumberByOrderSubItemId(orderSubItemId);
            SalYtOrderSubItem salYtOrderSubItem = salYtOrderSubItemMapper.selectById(orderSubItemId);
            if (packageNumber == null) {
                packageNumber = 0;
            }
            //打包中的数量+已经发货的数量=总共打包的数量
            packageNumber = salYtOrderSubItem.getDeliveryNumber() + packageNumber;
            if (packageNumber > salYtOrderSubItem.getNumber()) {
                throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(), "打包数量大于了下单数量");
            }
            if (packageNumber.equals(salYtOrderSubItem.getNumber())) {
                salYtOrderSubItem.setStatus(OrderSubItemStatusEnum.WaitDelivery.getKey());
            }
            salYtOrderSubItemMapper.updateById(salYtOrderSubItem);
        }
    }

    @Transactional
    public void returnWaitPackage(Long deliveryId) {
        List<StoYtDeliveryItem> deliveryItemList = stoYtDeliveryItemMapper.selectGroupOrderSubItemByDeliveryId(deliveryId);
        for (StoYtDeliveryItem deliveryItem : deliveryItemList) {
            Long orderSubItemId = deliveryItem.getOrderSubItemId();
            if (orderSubItemId == null) {
                continue;
            }
            SalYtOrderSubItem salYtOrderSubItem = salYtOrderSubItemMapper.selectById(orderSubItemId);
            if (salYtOrderSubItem == null) {
                continue;
            }
            // 回退订单子项的已发货数量
            Integer itemDeliveryNumber = deliveryItem.getNumber() == null ? 0 : deliveryItem.getNumber();
            Integer currentDeliveryNumber = salYtOrderSubItem.getDeliveryNumber() == null ? 0 : salYtOrderSubItem.getDeliveryNumber();
            salYtOrderSubItem.setDeliveryNumber(Math.max(0, currentDeliveryNumber - itemDeliveryNumber));
            // 只对状态为 WaitDelivery 的子项，将状态改回 WaitPackage
            if (OrderSubItemStatusEnum.WaitDelivery.getKey().equals(salYtOrderSubItem.getStatus())) {
                salYtOrderSubItem.setStatus(OrderSubItemStatusEnum.WaitPackage.getKey());
            }
            salYtOrderSubItemMapper.updateById(salYtOrderSubItem);
        }
    }

    public Object deliveryInfo(SalYtOrderDeliveryParams params) {
        // 根据订单物流信息查询参数查询发货单数据
        List<StoYtDelivery> deliveryList = stoYtDeliveryMapper.selectByDeliveryIdsAndStatus(params);

        for (StoYtDelivery delivery : deliveryList) {
            Long deliveryId = delivery.getId();
            List<StoYtDeliveryBox> boxList = stoYtDeliveryBoxMapper.selectByDeliveryId(deliveryId);
            delivery.setBoxList(boxList);
        }
        return deliveryList;
    }

    public Object packageDetail(Long deliveryBoxId) {
        StoYtDeliveryBox stoYtDeliveryBox = stoYtDeliveryBoxMapper.selectById(deliveryBoxId);
        if (stoYtDeliveryBox == null) {
            throw new BizException(ExceptionCodeEnum.Not_Exists);
        }
        List<StoYtDeliveryBoxItem> itemsByBoxId = stoYtDeliveryBoxItemMapper.getItemsByBoxId(deliveryBoxId);
        stoYtDeliveryBox.setBoxItemList(itemsByBoxId);
        for (StoYtDeliveryBoxItem item : itemsByBoxId) {
            Long specificationId = item.getSpecificationId();
            List<ProYtProductFile> fileListBySpecification = proYtProductManager.getFileListBySpecification(specificationId);
            item.setImageList(fileListBySpecification);
            List<ProYtProductSpecificationItem> itemsListBySpecification = proYtProductManager.getItemsListBySpecification(specificationId);
            item.setItemList(itemsListBySpecification);
        }
        Long boxId = stoYtDeliveryBox.getBoxId();
        StoYtBox stoYtBox = stoYtBoxMapper.selectById(boxId);
        stoYtDeliveryBox.setBox(stoYtBox);

        return stoYtDeliveryBox;
    }

    public void setRate(SalYtOrderUpdateParams params) {
        SysDictionary sysDictionary = sysDictionaryManager.getByCodeAndKey(DictionaryConfigEnum.exchangeRate.getKey(), DictionaryConfigEnum.exchangeRate.getKey());
        BigDecimal exchangeRate = params.getExchangeRate();
        if (exchangeRate != null) {
            sysDictionary.setValue(exchangeRate.toString());
            sysDictionaryManager.save(sysDictionary);
        }

    }

    public Object selectOrderReceiveList(FinYtReceiveQueryParams params) {
        PageHelper.startPage(params.getPageNum(), params.getPageSize());
        List<Map<String, Object>> list = salYtOrderSubReceiveMapper.selectOrderReceiveList(params);
        for (Map<String, Object> map : list) {
            Long orderId = (Long) map.get("id");
            //根据主订单id查询回款明细表中回款信息(只查一条)
            SalYtOrderSubReceive receiveInfo = salYtOrderSubReceiveMapper.selectByOrderId(orderId);
            if(receiveInfo!=null){
                map.put("receiveCurrency", receiveInfo.getCurrency());
            }else{
                //没有回款记录的币种就显示币种为0人民币
                map.put("receiveCurrency", 0);
            }
            SalYtOrder order = salYtOrderMapper.selectById(orderId);
            //查询出订单下所有子订单
            //根据父订单ID查询子订单
            LambdaQueryWrapper<SalYtOrderSub> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SalYtOrderSub::getOrderId, orderId)
                    .eq(SalYtOrderSub::getIsDeleted, 0);
            List<SalYtOrderSub> orderSubList = salYtOrderSubMapper.selectList(queryWrapper);
            BigDecimal totalAmount = BigDecimal.ZERO;
            for(SalYtOrderSub subOrder : orderSubList){
                totalAmount = salYtOrderSubMapper.calculateOrderSubAmount(subOrder.getId());
            }
            if (order.getIsCollectedShippingCost() == 1) {
                // 总金额=商品总额+运费-优惠金额
                totalAmount = totalAmount.add(order.getShippingCost());
            }
            totalAmount = totalAmount.subtract(order.getDiscountAmount());
            if (order.getEndAmount()==null)order.setEndAmount(BigDecimal.ZERO);
            if(order.getEndOtherAmount()==null)order.setEndOtherAmount(BigDecimal.ZERO);
            BigDecimal endAmount = order.getEndAmount();
            BigDecimal endOtherAmount = order.getEndOtherAmount();
            totalAmount = totalAmount.subtract(endAmount).subtract(endOtherAmount);
            map.put("orderSubAmount", totalAmount);
        }
        return new PageResultInfo<>(list);
    }

    @Transactional
    public void confirmOrderReceive(SalYtOrderSubReceive params) {
//        Boolean isCompletedReceive = params.getIsCompletedReceive();
//        SalYtOrderSub salYtOrderSub = new SalYtOrderSub();
//        salYtOrderSub.setId(params.getOrderSubId());
//        Long orderSubId = params.getOrderSubId();
//        SalYtOrderSub salYtOrderSub1 = salYtOrderSubMapper.selectById(orderSubId);
//        SalYtOrder salYtOrder = salYtOrderMapper.selectById(salYtOrderSub1.getOrderId());
//        BigDecimal totalReceiveAmount = salYtOrderSubReceiveMapper.getTotalReceiveAmountByOrderSubId(orderSubId);
//        if(totalReceiveAmount==null){
//            totalReceiveAmount=BigDecimal.ZERO;
//        }
//        BigDecimal orderSubAmount = salYtOrderSubMapper.calculateOrderSubAmount(orderSubId);
//        if(totalReceiveAmount.add(params.getAmount()).compareTo(orderSubAmount) > 0){
//            throw new BizException("回款总金额不能大于订单金额");
//        }
//        if (isCompletedReceive != null && isCompletedReceive) {
//            salYtOrderSub.setReceiveStatus(ReceiveStatusEnum.Completed.getKey());
//            //判断该父订单下其他子订单是否全部回款完成
//            List<SalYtOrderSub> subList = salYtOrderSubMapper.selectByOrderIdExcludeOrderSubId(salYtOrder.getId(), salYtOrderSub.getId());
//            if(!subList.isEmpty()){
//                Boolean isAllCompleted = true;
//                for (SalYtOrderSub sub : subList) {
//                    if(sub.getReceiveStatus().equals(ReceiveStatusEnum.WaitReceive.getKey())||sub.getReceiveStatus().equals(ReceiveStatusEnum.ReceivePart.getKey())){
//                        isAllCompleted = false;
//                    }
//                }
//                if(isAllCompleted){
//                    //给父订单添加回款完成时间
//                    salYtOrder.setReceiveFinishTime(LocalDateTime.now());
//                    salYtOrderMapper.updateById(salYtOrder);
//                }
//            }else {
//                //为空说明该父订单下没有其他子订单
//                //给父订单添加回款完成时间
//                salYtOrder.setReceiveFinishTime(LocalDateTime.now());
//                salYtOrderMapper.updateById(salYtOrder);
//            }
//
//        } else {
//            salYtOrderSub.setReceiveStatus(ReceiveStatusEnum.ReceivePart.getKey());
//        }
//        salYtOrderSubMapper.updateById(salYtOrderSub);
//        salYtOrderSubReceiveMapper.insert(params);

        //查询主订单，判断是否已经处于回款完成状态
        SalYtOrder salOrder = salYtOrderMapper.selectById(params.getOrderSubId());
        if(salOrder.getReceiveStatus().equals(ReceiveStatusEnum.Completed.getKey())){
            throw new BizException("该订单已经回款完成");
        }
        Boolean isCompletedReceive = params.getIsCompletedReceive();
        //因为前端将主订单id绑定到orderSubId上面了
        Long orderId = params.getOrderSubId();

        //判断该订单上次回款币种是什么，需要和上一次币种一致
        SalYtOrderSubReceive salYtOrderSubReceive = salYtOrderSubReceiveMapper.selectByOrderId(orderId);
        if(salYtOrderSubReceive!=null){
            if(!salYtOrderSubReceive.getCurrency().equals(params.getCurrency())){
                throw  new BizException("回款币种不一致");
            }
        }


        SalYtOrder salYtOrder = new SalYtOrder();
        salYtOrder.setId(orderId);

        //和订单同币种的回款需要做不能大于订单金额限制，不同币种的不需要做限制
           //查询出订单
        SalYtOrder order = salYtOrderMapper.selectById(orderId);
        Integer orderCurrency = Integer.valueOf(order.getCurrency());
        if(orderCurrency.equals(params.getCurrency())) {
            //计算该订单下所有子订单的回款金额
            //根据父订单ID查询子订单
            LambdaQueryWrapper<SalYtOrderSub> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SalYtOrderSub::getOrderId, orderId)
                    .eq(SalYtOrderSub::getIsDeleted, 0);
            List<SalYtOrderSub> orderSubList = salYtOrderSubMapper.selectList(queryWrapper);
            //计算所有子订单已经回款的金额
            BigDecimal totalReceiveAmount = BigDecimal.ZERO;
            //计算子订单总金额（包含运费）
            BigDecimal totalOrderAmount = BigDecimal.ZERO;
            for (SalYtOrderSub subOrder : orderSubList) {
                //计算所有子订单已经回款的金额
                BigDecimal subReceiveAmount = salYtOrderSubReceiveMapper.getTotalReceiveAmountByOrderSubId(subOrder.getId());
                if (subReceiveAmount != null) {
                    totalReceiveAmount = totalReceiveAmount.add(subReceiveAmount);
                }

                //计算子订单总金额（包含运费）
                BigDecimal subOrderAmount = totalOrderAmount.add(salYtOrderSubMapper.calculateOrderSubAmount(subOrder.getId()));
                if (subOrderAmount == null) {
                    subOrderAmount = BigDecimal.ZERO;
                }
                totalOrderAmount = totalOrderAmount.add(subOrderAmount);

            }

            if (order.getIsCollectedShippingCost() == 1) {
                // 总金额=商品总额+运费-优惠金额
                totalOrderAmount = totalOrderAmount.add(order.getShippingCost());
            }
            totalOrderAmount = totalOrderAmount.subtract(order.getDiscountAmount());

            //判断本次回款金额加已经回款的金额不能大于订单总金额
            // 统一保留2位小数后再比较（金额必须这样做）
            BigDecimal sum = totalReceiveAmount.add(params.getAmount())
                    .setScale(2, RoundingMode.HALF_UP); // 关键：统一保留2位小数

            if (sum.compareTo(totalOrderAmount) > 0) {
                throw new BizException("回款总金额不能大于订单金额");
            }
        }

        //是否确认回款
        if (isCompletedReceive != null && isCompletedReceive) {
            salYtOrder.setReceiveStatus(ReceiveStatusEnum.Completed.getKey());
            //回款完成时间
            //salYtOrder.setReceiveFinishTime(LocalDateTime.now());
            if(params.getReceiveFinishTime()!=null) {
                salYtOrder.setReceiveFinishTime(params.getReceiveFinishTime());
            }
        } else {
            salYtOrder.setReceiveStatus(ReceiveStatusEnum.ReceivePart.getKey());
        }

        salYtOrderMapper.updateById(salYtOrder);
        params.setOrderId(orderId);
        params.setOrderSubId(null);
        salYtOrderSubReceiveMapper.insert(params);
    }

    public List<SalYtOrderSubReceive> receiveDetail(FinYtReceiveQueryParams params) {
        List<SalYtOrderSubReceive> salYtOrderSubReceives = salYtOrderSubReceiveMapper.listByParams(params);
        Boolean isReceiveFinishTime=false;
        for (SalYtOrderSubReceive receive : salYtOrderSubReceives) {
            //国际站同步
            if(receive.getCreateUser().equals(ReceivePlatformEnum.STATION.getKey())){
                receive.setCreateUserName(ReceivePlatformEnum.STATION.getValue());
                if(receive.getThirdReceiveTime()!=null){
                    receive.setReceiveFinishTime(receive.getThirdReceiveTime());
                }
            }
            //1688同步
            if(receive.getCreateUser().equals(ReceivePlatformEnum.ONESIXEIGHTEIGHT.getKey())){
                receive.setCreateUserName(ReceivePlatformEnum.ONESIXEIGHTEIGHT.getValue());
                if(receive.getThirdReceiveTime()!=null){
                    receive.setReceiveFinishTime(receive.getThirdReceiveTime());
                }
            }

            //不属于第三方的回款，那么需要判断主订单是否有回款完成时间
            Long orderId = receive.getOrderId();
            SalYtOrder salYtOrder = salYtOrderMapper.selectById(orderId);
            if(salYtOrder.getReceiveFinishTime()!=null){
                if(!isReceiveFinishTime) {
                    receive.setReceiveFinishTime(salYtOrder.getReceiveFinishTime());
                    isReceiveFinishTime=true;
                }
            }


            Long id = receive.getId();
            List<ProYtProductFile> proYtProductFiles = proYtProductFileMapper.selectByMasterIdAndType(id, ProductFilesTypeEnum.orderReceiveFile.getKey());
            receive.setFileList(proYtProductFiles);
        }
        return salYtOrderSubReceives;
    }

    public Object selectOrderProfitList(FinYtProfitQueryParams params) {
//        if (params.needPaging()) {
//            PageHelper.startPage(params.getPageNum(), params.getPageSize());
//        }
//        List<SalYtOrderSubVo> list = salYtOrderSubMapper.selectOrderProfitList(params);
//        for (SalYtOrderSubVo salYtOrderSubVo : list) {
//            BigDecimal totalOrderPrice = salYtOrderSubVo.getTotalOrderPrice();
//
//            Integer followRatio = salYtOrderSubVo.getFollowRatio();
//            salYtOrderSubVo.setFollowingPrice(totalOrderPrice.multiply(new BigDecimal(followRatio)).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
//
//            Integer saleRatio = salYtOrderSubVo.getSaleRatio();
//            salYtOrderSubVo.setSalePrice(totalOrderPrice.multiply(new BigDecimal(saleRatio)).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
//        }
//        return new PageResultInfo<>(list);

        if (params.needPaging()) {
            PageHelper.startPage(params.getPageNum(), params.getPageSize());
        }
        List<FinYtProfitOrderProfitListVo> list = salYtOrderSubMapper.selectOrderProfitList(params);
        for (FinYtProfitOrderProfitListVo salYtOrderSubVo : list) {
            //转换前的TotalOrderPrice
            BigDecimal totalOrderPrice = salYtOrderSubVo.getTotalOrderPrice();

            //TODO: 2021/9/7 注释原因：利润需要模糊搜索，必须写在sql 中，不能写在java代码 中
            //判断是否收取运费,如果是没有收取运费，则需要汇总运费回款时候的金额,0代表未收取运费
//            if(salYtOrderSubVo.getIsCollectedShippingCost().equals(0)){
//                //汇总
//                BigDecimal totalShippingPrice = BigDecimal.ZERO;
//                //根据主订单ID查询出所有子订单
//                Long orderId = salYtOrderSubVo.getId();
//                LambdaQueryWrapper<SalYtOrderSub> queryWrapper = new LambdaQueryWrapper<>();
//                queryWrapper.eq(SalYtOrderSub::getOrderId, orderId)
//                                .eq(SalYtOrderSub::getIsDeleted, 0);
//                                List<SalYtOrderSub> subList = salYtOrderSubMapper.selectList(queryWrapper);
//                //下面为获取运费的回款金额方法，
//                for (SalYtOrderSub sub : subList) {
//                    Long subId = sub.getId();
//                    List<StoYtDeliveryReceive> receiveList = stoYtDeliveryManager.selectDeliveryReceiveByOrderSubId(subId);
//                    for (StoYtDeliveryReceive receive : receiveList) {
//                        totalShippingPrice = totalShippingPrice.add(receive.getAmount());
//                    }
//                }
//
//                salYtOrderSubVo.setTotalShippingPrice(totalShippingPrice);
//                //此时业绩需要加上运费金额
//                totalOrderPrice = salYtOrderSubVo.getTotalOrderPrice().add(salYtOrderSubVo.getTotalShippingPrice());
//                //此时收入总计还得加上运费金额
//                salYtOrderSubVo.setTotalOrderPrice(salYtOrderSubVo.getTotalOrderPrice().add(totalShippingPrice));
//            }

            //因为在查询时将美元转换成人民币，前端需要展示美元金额，所以需要将查询结果中的金额转换成美元
            if(salYtOrderSubVo.getCurrency().equals(CurrencyEnum.dollar.getValue())){
                //汇率
                BigDecimal exchangeRate = salYtOrderSubVo.getExchangeRate();
                salYtOrderSubVo.setTotalItemPrice(salYtOrderSubVo.getTotalItemPrice().divide(exchangeRate,4, RoundingMode.HALF_UP));
                salYtOrderSubVo.setTotalOrderPrice(salYtOrderSubVo.getTotalOrderPrice().divide(exchangeRate,4, RoundingMode.HALF_UP));
               // salYtOrderSubVo.setTotalShippingPrice(salYtOrderSubVo.getTotalShippingPrice().divide(exchangeRate,4, RoundingMode.HALF_UP));
            }
           // BigDecimal totalOrderPrice = salYtOrderSubVo.getTotalOrderPrice();

            Integer followRatio = salYtOrderSubVo.getFollowRatio();
            salYtOrderSubVo.setFollowingPrice(totalOrderPrice.multiply(new BigDecimal(followRatio)).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));

            Integer saleRatio = salYtOrderSubVo.getSaleRatio();
            salYtOrderSubVo.setSalePrice(totalOrderPrice.multiply(new BigDecimal(saleRatio)).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));

            //利润率保留两位小数
            salYtOrderSubVo.setProfitRate(salYtOrderSubVo.getProfitRate().setScale(2, RoundingMode.HALF_UP));
        }
        return new PageResultInfo<>(list);
    }

    public FinYtProfitOrderProfitListVo selectOrderProfitDetail(FinYtProfitQueryParams params) {
//        SalYtOrderSubVo salYtOrderSubVo = salYtOrderSubMapper.selectOrderProfitDetail(params);
//        if (salYtOrderSubVo != null) {
//            BigDecimal totalOrderPrice = salYtOrderSubVo.getTotalOrderPrice();
//
//            Integer followRatio = salYtOrderSubVo.getFollowRatio();
//            salYtOrderSubVo.setFollowingPrice(totalOrderPrice.multiply(new BigDecimal(followRatio)).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
//
//            Integer saleRatio = salYtOrderSubVo.getSaleRatio();
//            salYtOrderSubVo.setSalePrice(totalOrderPrice.multiply(new BigDecimal(saleRatio)).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
//
////            // 计算利润率
////            if (totalOrderPrice.compareTo(BigDecimal.ZERO) > 0) {
////                BigDecimal profit = salYtOrderSubVo.getProfit();
////                salYtOrderSubVo.setProfitRate(profit.divide(totalOrderPrice, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
////            }
//        }
//        return salYtOrderSubVo;

        FinYtProfitOrderProfitListVo salYtOrderSubVo = salYtOrderSubMapper.selectOrderProfitDetail(params);
        if (salYtOrderSubVo != null) {
            BigDecimal totalOrderPrice = salYtOrderSubVo.getTotalOrderPrice();
            //判断订单币种
            if(salYtOrderSubVo.getCurrency().equals(CurrencyEnum.dollar.getValue())){
                //因为计算的时候将美元转换成人民币，所以需要将查询结果中的金额转换成美元
                BigDecimal exchangeRate = salYtOrderSubVo.getExchangeRate();
                salYtOrderSubVo.setTotalOrderPrice(salYtOrderSubVo.getTotalOrderPrice().divide(exchangeRate,2, RoundingMode.HALF_UP));
                salYtOrderSubVo.setTotalItemPrice(salYtOrderSubVo.getTotalItemPrice().divide(exchangeRate,2, RoundingMode.HALF_UP));
            }

            Integer followRatio = salYtOrderSubVo.getFollowRatio();
            salYtOrderSubVo.setFollowingPrice(totalOrderPrice.multiply(new BigDecimal(followRatio)).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));

            Integer saleRatio = salYtOrderSubVo.getSaleRatio();
            salYtOrderSubVo.setSalePrice(totalOrderPrice.multiply(new BigDecimal(saleRatio)).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
        }
        return salYtOrderSubVo;
    }

    /**
     * 查询订单利润产品列表
     *
     * @param params 查询参数
     * @return 产品利润列表
     */
    public List<FinYtOrderProfitProductVo> selectOrderProfitProduct(FinYtProfitQueryParams params) {
        List<FinYtOrderProfitProductVo> finYtOrderProfitProductVos = salYtOrderSubMapper.selectOrderProfitProduct(params);
        for (FinYtOrderProfitProductVo product : finYtOrderProfitProductVos) {
            List<ProYtProductSpecificationItem> itemsListBySpecification = proYtProductManager.getItemsListBySpecification(product.getSpecificationId());
            product.setItemList(itemsListBySpecification);

            List<ProYtProductFile> fileListBySpecification = proYtProductManager.getFileListBySpecification(product.getSpecificationId());
            product.setImageList(fileListBySpecification);
            //利润率保留两位小数
            product.setProfitRate(product.getProfitRate().setScale(2, RoundingMode.HALF_UP));
        }
        return finYtOrderProfitProductVos;
    }

    /**
     * 重新计算订单的运费付款
     *
     * @param orderSubId 子订单ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void updatePaymentShipping(Long orderSubId,FinYtPaymentUpdateParams params) {
        // 调用Mapper计算运费付款金额
       // BigDecimal paymentShipping = salYtOrderSubMapper.calculatePaymentShipping(orderSubId);

        //该子订单在发货单item里面的数量
        Long deliveryId=params.getDeliveryId();
        //发货单item里面的总数量
        BigDecimal totalNumbers = stoYtDeliveryItemMapper.selectTotalNumbers(deliveryId);
        //该子订单在发货单item里面的数量
        BigDecimal proportion = stoYtDeliveryItemMapper.selectSubOrderTotalNumbers(deliveryId, orderSubId);
        BigDecimal paymentShipping=params.getAmount().multiply(proportion).divide(totalNumbers, 2, RoundingMode.HALF_UP);
        // 更新子订单的付款运费字段
        //查询该子订单
        SalYtOrderSub salYtOrderSub = salYtOrderSubMapper.selectById(orderSubId);
        if(salYtOrderSub.getPaymentShipping()!= null) {
            salYtOrderSub.setPaymentShipping(salYtOrderSub.getPaymentShipping().add(paymentShipping));
        }else {
            salYtOrderSub.setPaymentShipping(paymentShipping);
        }
        salYtOrderSubMapper.updateById(salYtOrderSub);
    }

    /**
     * 更新指定发货单下所有子订单的付款运费
     *
     * @param
     */
    @Transactional(rollbackFor = Exception.class)
    public void updatePaymentShippingByDeliveryId(FinYtPaymentUpdateParams params) {
        Long deliveryId = params.getDeliveryId();
        // 查询该发货单下所有子订单ID
        List<Map<String, Object>> orderSubList = stoYtDeliveryItemMapper.selectOrderSubIdQuantityByDeliveryId(deliveryId);

        // 循环更新每个子订单的付款运费
        for (Map<String, Object> orderSubMap : orderSubList) {
            Long orderSubId = Long.valueOf(orderSubMap.get("orderSubId").toString());
            updatePaymentShipping(orderSubId,params);
        }
    }

    /**
     * 更新指定发货单下所有未收运费子订单的收款运费
     *
     *
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateReceiveShippingByDeliveryId(StoYtDeliveryReceive params) {
        Long deliveryId=params.getDeliveryId();
        //回款金额
        BigDecimal amount = params.getAmount();
        //客户回款确认状态
        Boolean isCompletedReceive = params.getIsCompletedReceive();
        //币种
        Integer currency = params.getCurrency();
        // 查询该发货单下所有未收运费的子订单ID
        List<Map<String, Object>> orderSubList = stoYtDeliveryItemMapper.selectReceiveOrderSubIdQuantityByDeliveryId(deliveryId);

        // 循环更新每个子订单的收款运费
        for (Map<String, Object> orderSubMap : orderSubList) {
            Long orderSubId = Long.valueOf(orderSubMap.get("order_sub_id").toString());
            updateReceiveShipping(orderSubId,isCompletedReceive,deliveryId, amount, currency);
        }
    }

    /**
     * 重新计算订单的运费回款
     *
     * @param orderSubId 子订单ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateReceiveShipping(Long orderSubId,Boolean isCompletedReceive,Long deliveryId,BigDecimal amount,Integer currency) {
        // 调用Mapper计算运费回款金额
       // BigDecimal receiveShipping = salYtOrderSubMapper.calculateReceiveShipping(orderSubId);

        //TODO: 2026/4/15 待完善,现在回款时，不管回款时什么币种，先分配后再换算成订单对应币种后存入到子订单receiveShipping中

        //该子订单在发货单item里面的数量
        //发货单item里面的总数量
        BigDecimal totalNumbers = stoYtDeliveryItemMapper.selectTotalNumbersNotReceive(deliveryId);
        //该子订单在发货单item里面的数量
        BigDecimal proportion = stoYtDeliveryItemMapper.selectSubOrderTotalNumbers(deliveryId, orderSubId);
        //计算运费占比
        BigDecimal receiveShipping = amount.multiply(proportion).divide(totalNumbers, 2, RoundingMode.HALF_UP);

        //回款分配金额（没有分币种时）
        BigDecimal receiveShippingAmount = receiveShipping;

        SalYtOrderSub salYtOrderSub = salYtOrderSubMapper.selectById(orderSubId);
        //判断回款币种是否和该发货单下的所有订单的币种是否一致，判断子订单所属主订单的币种
           //查询主订单
        SalYtOrder salYtOrder = salYtOrderMapper.selectById(salYtOrderSub.getOrderId());
        String receiveCurrency = String.valueOf(currency);
        //不一致，则需要汇率转换，再加

        //查询此时的汇率
        //查询转换率
        List<SysDictionary> sysDictionaries = sysDictionaryMapper.selectByCode("exchangeRate");
        //美元转人民币汇率
        BigDecimal exchangeRate = new BigDecimal(sysDictionaries.get(0).getValue());

        //查询此时的汇率后转换成订单的币种的金额
        if(!salYtOrder.getCurrency().equals(receiveCurrency)){
            if(salYtOrder.getCurrency().equals(CurrencyEnum.dollar.getValue())){
                //为美元时，说明回款是人民币
                  //人民币转化成美元
                receiveShipping = receiveShipping.divide(exchangeRate, 2, RoundingMode.HALF_UP);
            }else {
                //为人民币时，说明回款是美元
                receiveShipping = receiveShipping.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);
            }
        }

        if(receiveCurrency.equals(CurrencyEnum.dollar.getValue())){
            //如果回款币种是美元，则需要汇率转换
            receiveShippingAmount=receiveShippingAmount.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);
        }
        if(salYtOrderSub.getReceiveShippingBasePrice()!=null){
            salYtOrderSub.setReceiveShippingBasePrice(salYtOrderSub.getReceiveShippingBasePrice().add(receiveShippingAmount));
        }else{
            salYtOrderSub.setReceiveShippingBasePrice(receiveShippingAmount);
        }

        //一致，则不需要汇率转换，直接加

        // 更新子订单的回款运费字段
        if(salYtOrderSub.getReceiveShipping()!=null){
            salYtOrderSub.setReceiveShipping(salYtOrderSub.getReceiveShipping().add(receiveShipping));
        }else {
            salYtOrderSub.setReceiveShipping(receiveShipping);
        }
        salYtOrderSubMapper.updateById(salYtOrderSub);

        //判断是否完成运费收款
        if(isCompletedReceive) {
            //查询出子订单的主订单下的所有子订单(除去该发货单下的子订单)，一个子订单可能在多个发货单item里面，逻辑需要想一想
            SalYtOrderSub orderSub = salYtOrderSubMapper.selectById(orderSubId);
            LambdaQueryWrapper<SalYtOrderSub> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SalYtOrderSub::getOrderId, orderSub.getOrderId())
                    .eq(SalYtOrderSub::getIsDeleted, 0);
            List<SalYtOrderSub> subOrderList = salYtOrderSubMapper.selectList(wrapper);
            SalYtOrder order = salYtOrderMapper.selectById(orderSub.getOrderId());
            //需要排除当前发货单，因为当前发货单已经确认
            Integer shippingReceiveStatus = getOrderShippingReceiveStatus(subOrderList,deliveryId);
            if(shippingReceiveStatus.equals(ReceiveStatusEnum.Completed.getKey())){
                //已完成，更新订单的客户运费收款状态
                order.setShippingReceiveStatus(ReceiveStatusEnum.Completed.getKey());
                //只更新状态而不更新时间，是因为还需要判断订单是否发货完毕
                Boolean isOrderDelivery = getOrderIsDelivery(subOrderList);
                if(isOrderDelivery){
                    order.setShippingReceiveFinishTime(LocalDateTime.now());
                }
                salYtOrderMapper.updateById(order);
            }
        }else{
            //判断传入金额是否为0
            if(amount.compareTo(BigDecimal.ZERO)==0){
                //传入金额为0，则不更新,因为不分配金额
            }else{
                SalYtOrderSub orderSub = salYtOrderSubMapper.selectById(orderSubId);
                SalYtOrder order = salYtOrderMapper.selectById(orderSub.getOrderId());
                order.setShippingReceiveStatus(ReceiveStatusEnum.ReceivePart.getKey());
            }
        }
    }

    public PageResultInfo<FinYtCustomerProfitVo> customerProfitList(FinYtProfitQueryParams request) {
        if (request.needPaging()) {
            PageHelper.startPage(request.getPageNum(), request.getPageSize());
        }
        List<FinYtCustomerProfitVo> list = salYtOrderSubMapper.customerProfitList(request);
        return new PageResultInfo<>(list);
    }

    public FinYtCustomerProfitVo customerProfitDetail(FinYtProfitQueryParams request) {
        FinYtCustomerProfitVo finYtCustomerProfitVo = salYtOrderSubMapper.customerProfitDetail(request);
        //计算总收入金额
      //  BigDecimal customerDiscount = finYtCustomerProfitVo.getCustomerDiscount();
        //sql里面已经减去了优惠金额，所以这里不需要减去
//        BigDecimal customerIncome = finYtCustomerProfitVo.getCustomerIncome();
//        if(customerDiscount!=null && customerIncome!=null){
//            finYtCustomerProfitVo.setCustomerIncome(customerIncome.subtract(customerDiscount));
//        }
        //重新计算利润率和利润金额
        finYtCustomerProfitVo.setCustomerProfit(finYtCustomerProfitVo.getCustomerIncome().subtract(finYtCustomerProfitVo.getCustomerExpense()));
        finYtCustomerProfitVo.setCustomerProfitRate(finYtCustomerProfitVo.getCustomerProfit().divide(finYtCustomerProfitVo.getCustomerIncome(), 4, RoundingMode.HALF_UP));

        return finYtCustomerProfitVo;
    }

    public List<CustomerProductProfitVo> customerProductProfit(FinYtProfitQueryParams request) {
        List<CustomerProductProfitVo> customerProductProfitVoList = salYtOrderSubMapper.customerProductProfit(request);
        BigDecimal totalProfit = salYtOrderSubMapper.customerProductTotalProfit(request);
        for (CustomerProductProfitVo customerProductProfitVo : customerProductProfitVoList) {
            BigDecimal profit = customerProductProfitVo.getProfit();
            if (profit != null && !profit.equals(BigDecimal.ZERO)&&totalProfit.compareTo(BigDecimal.ZERO)!=0) {
                customerProductProfitVo.setProfitContributionRate(profit.divide(totalProfit, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
            }else{
                customerProductProfitVo.setProfitContributionRate(BigDecimal.ZERO);
            }

            //设置成百分比
            if (customerProductProfitVo.getProfitRate() != null) {
                customerProductProfitVo.setProfitRate(customerProductProfitVo.getProfitRate().multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP));
            }
        }
        return customerProductProfitVoList;
    }

    /**
     * 查询父订单详情
     * @param orderId
     * @return
     */
    public SalYtOrderVo orderDetail(Long orderId) {
        //查询父订单详情，判断是否存在
        SalYtOrder order = salYtOrderMapper.selectById(orderId);
        if(order==null){
            throw new RuntimeException("订单不存在");
        }

        SalYtOrderVo orderVo = new SalYtOrderVo();
        //前面复制给后面
        BeanUtils.copyProperties(order, orderVo);

        //客户姓名
        String customerName = salYtCustomerMapper.selectById(order.getCustomerId()).getName();
        orderVo.setCustomerName(customerName);
        //跟进人
        if(order.getFollowEmployeeId()!=null){
            SysUser sysUser = sysUserMapper.selectById(order.getFollowEmployeeId());
            if(sysUser!=null){
                String followEmployeeName = sysUser.getNickName();
                orderVo.setFollowEmployeeName(followEmployeeName);
            }
        }

        //业务员
        String salesEmployeeName = sysUserMapper.selectById(order.getSaleEmployeeId()).getNickName();
        orderVo.setSalesEmployeeName(salesEmployeeName);

        //根据父订单ID查询子订单
        LambdaQueryWrapper<SalYtOrderSub> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SalYtOrderSub::getOrderId, orderId)
                .eq(SalYtOrderSub::getIsDeleted, 0);
        List<SalYtOrderSub> orderSubList = salYtOrderSubMapper.selectList(queryWrapper);

        //获取订单下所有子订单的最小状态
        String minStatus = salYtOrderSubItemMapper.getMinStatus(order.getId());
        //用于计算订单总金额
        BigDecimal totalAmount=BigDecimal.ZERO;
        //用于存储子订单的最小状态
        List<String> minStatusList = new ArrayList<>();
        //用于存储订单预计成本
        BigDecimal orderCostAmount = BigDecimal.ZERO;
        //用于存储订单预计毛利
        BigDecimal orderProfitAmount = BigDecimal.ZERO;
        //判断子订单是否存在半成品订单
        String hasHalfProductOrder ="0";
        //循环子订单，处理子订单数据
        for(SalYtOrderSub subOrder : orderSubList){
            //判断是否半成品订单
            if(subOrder.getOrderType().equals("1")){
                hasHalfProductOrder ="1";
            }

            //设置订单状态(待打包，待确认这些状态)
            // 获取子订单item数据
            QueryWrapper<SalYtOrderSubItem> itemWrapper = new QueryWrapper<>();
            itemWrapper.eq("order_sub_id", subOrder.getId());
            itemWrapper.eq("is_deleted", 0);
            List<SalYtOrderSubItem> itemList = salYtOrderSubItemMapper.selectList(itemWrapper);

            if (subOrder.getOrderType().equals("1") && minStatus != null) {
                Boolean isAllConfirm = true;
                //半成品单，并且已经采购，则要确认产品是否已经全部确认
                for (SalYtOrderSubItem item : itemList) {
                    if (item.getSpecificationId() == null) {
                        if (!validItemConfirm(item, itemList)) {
                            isAllConfirm = false;
                            break;
                        }
                    }
                }
                //如果全部确认
                if(isAllConfirm){
                    //只算有规格的最小状态
                    minStatus = salYtOrderSubMapper.selectConfirmedItemMinStatusByOrderSubId(subOrder.getId());
                }else {
                    //如果还有待确认的
                    Integer status = Integer.parseInt(minStatus);
                    if(status>Integer.parseInt(OrderSubItemStatusEnum.WaitPurchase.getKey())){
                        minStatus = OrderSubItemStatusEnum.WaitConfirm.getKey();
                    }
                }

            }
            if(minStatus!=null){
                //获取到子订单的最小状态，最后从所有子订单中获取最小的状态
                //orderVo.setOrderStatus(minStatus);
                //subOrder.setSubStatus(minStatus);
                minStatusList.add(minStatus);
            }


            //计算订单总金额
            //设置订单总额
            totalAmount = salYtOrderSubMapper.calculateOrderSubAmount(subOrder.getId());

            //计算预计成本和预计毛利
            //设置预计成本价和毛利
            BigDecimal costAmount = BigDecimal.ZERO;
            for (SalYtOrderSubItem item : itemList) {
                Integer number = item.getNumber();
                BigDecimal supplierPrice = item.getSupplierPrice();
                costAmount = costAmount.add(new BigDecimal(number).multiply(supplierPrice));

            }

            BigDecimal profitAmount = totalAmount.subtract(costAmount);
            subOrder.setCostAmount(costAmount);
            subOrder.setProfitAmount(profitAmount);


            //预计成本
            orderCostAmount = orderCostAmount.add(subOrder.getCostAmount());
            //预计毛利
            orderProfitAmount = orderProfitAmount.add(subOrder.getProfitAmount());
        }

        //订单的客户运费回款状态
       // Integer shippingReceiveStatus =getOrderShippingReceiveStatus(orderSubList);
        //设置客户运费回款状态
       // orderVo.setShippingReceiveStatus(shippingReceiveStatus);
        if(orderVo.getShippingReceiveStatus()!=null) {
            if (orderVo.getShippingReceiveStatus().equals(2)) {
                //如果是已回款状态，代表订单运费已回款
                if (orderVo.getShippingReceiveFinishTime() == null) {
                    //为null说明订单还没有发货完
                    orderVo.setShippingReceiveStatus(0);
                }
            }
        }
        if(orderVo.getShippingReceiveStatus()==null){
              orderVo.setShippingReceiveStatus(0);
        }


        if (order.getIsCollectedShippingCost() == 1) {
            // 总金额=商品总额+运费-优惠金额
            totalAmount = totalAmount.add(order.getShippingCost());
            //订单毛利也要+运费-优惠金额
            orderProfitAmount = orderProfitAmount.add(order.getShippingCost());
        }
        totalAmount = totalAmount.subtract(order.getDiscountAmount());
        orderProfitAmount=orderProfitAmount.subtract(order.getDiscountAmount());

        //处理得到的子订单最小状态，得到最小状态
        String orderMinStatus = getMinStatus(minStatusList);
        orderVo.setOrderStatus(orderMinStatus);
        if(order.getEndAmount()==null)order.setEndAmount(BigDecimal.ZERO);
        if(order.getEndOtherAmount()==null)order.setEndOtherAmount(BigDecimal.ZERO);
        totalAmount=totalAmount.subtract(order.getEndAmount()).subtract(order.getEndOtherAmount());
        //将所有的子订单金额累加后的金额设置到父订单中
        orderVo.setOrderAmount(totalAmount);
        //订单总预计成本
        orderVo.setOrderCostAmount(orderCostAmount);
        //订单总预计毛利
        orderVo.setOrderProfitAmount(orderProfitAmount);
        //判断是否有半成品订单
        orderVo.setHasHalfProductOrder(hasHalfProductOrder);

        //判断是否是美元，是就是汇率转换
        if(order.getCurrency().equals("1")) {
            //查询转换率
            List<SysDictionary> sysDictionaries = sysDictionaryMapper.selectByCode("exchangeRate");
            //美元转人民币汇率
            BigDecimal exchangeRate = new BigDecimal(sysDictionaries.get(0).getValue());

            //毛利
            BigDecimal profitAmount1 = orderVo.getOrderAmount().multiply(exchangeRate).subtract(orderVo.getOrderCostAmount());
            //预计毛利
            orderVo.setOrderProfitAmount(profitAmount1);

        }

        //获取成品tab数量
        SalYtOrderSubItem params=new SalYtOrderSubItem();
        params.setOrderId(orderId);
        Map<String,Integer> statusNumberMap=getCompleteTabNumber(params);
        orderVo.setStatusCountMap(statusNumberMap);


        return orderVo;
    }


    public Integer getOrderShippingReceiveStatus(List<SalYtOrderSub> orderSubList,Long deliveryId) {
     //   Integer shippingReceiveStatus=-1;
        //用于判断是否是订单是否都已经发货
        Boolean isOrder=true;
        //用于判断订单客户运费是否已经完成
        Boolean isDelivery=true;
        //用于判断订单客户运费是否是部分收款
        Integer isDeliveryPart=-1;

        //运费回款状态 在订单中，要判断订单下是否所有产品都发货，发货单是否都已经确认完成回款。两个条件都满足时，才能在订单中展示回款状态“已完成”

        for(SalYtOrderSub subOrder : orderSubList) {
            //子订单item-itemList
            //获取子订单item数据
            QueryWrapper<SalYtOrderSubItem> itemWrapper = new QueryWrapper<>();
            itemWrapper.eq("order_sub_id", subOrder.getId());
            itemWrapper.eq("is_deleted", 0);
            List<SalYtOrderSubItem> itemList = salYtOrderSubItemMapper.selectList(itemWrapper);
            //判断是否都已经发货
            for (SalYtOrderSubItem item : itemList) {
                if (item.getDeliveryNumber() < item.getNumber()) {
                    //发货完
                    isOrder=false;
                    break;
                }
            }
            //根据subOrderId查询发货单详情,判断子订单所属的发货单是否都已经完成回款
            List<StoYtDelivery> deliveryList = stoYtDeliveryMapper.selectDeliveryListBySubOrderId(subOrder.getId(),deliveryId);
            for (StoYtDelivery delivery : deliveryList) {
                //判断主订单下的子订单所属发货单的运费回款状态
                if(!delivery.getReceiveStatus().equals(ReceiveStatusEnum.Completed.getKey())){
                    isDelivery=false;
                }
                if(delivery.getReceiveStatus().equals(ReceiveStatusEnum.ReceivePart.getKey())){
                    isDeliveryPart=ReceiveStatusEnum.ReceivePart.getKey();
                }
            }
        }

        if(isOrder&&isDelivery){
            //已完成
            //shippingReceiveStatus=ReceiveStatusEnum.Completed.getKey();
            return   ReceiveStatusEnum.Completed.getKey();
        }
        if(isDeliveryPart.equals(ReceiveStatusEnum.ReceivePart.getKey())){
            //部分回款
            return ReceiveStatusEnum.ReceivePart.getKey();
        }

     //否则是未回款
      return   ReceiveStatusEnum.WaitReceive.getKey();
    }

    //判断订单是否都已经发货
    private Boolean getOrderIsDelivery(List<SalYtOrderSub> orderSubList) {
        //用于判断是否是订单是否都已经发货
        Boolean isOrder=true;

        for(SalYtOrderSub subOrder : orderSubList) {
            //子订单item-itemList
            //获取子订单item数据
            QueryWrapper<SalYtOrderSubItem> itemWrapper = new QueryWrapper<>();
            itemWrapper.eq("order_sub_id", subOrder.getId());
            itemWrapper.eq("is_deleted", 0);
            List<SalYtOrderSubItem> itemList = salYtOrderSubItemMapper.selectList(itemWrapper);
            //判断是否都已经发货
            for (SalYtOrderSubItem item : itemList) {
                if (item.getDeliveryNumber() < item.getNumber()) {
                    //发货完
                    isOrder = false;
                    break;
                }
            }
        }
        return isOrder;
    }

    //得到最小状态
    private String getMinStatus(List<String> statusList) {
        if (statusList == null || statusList.isEmpty()) {
            return null;
        }
        String minStatus = statusList.get(0);
        for (String status : statusList) {
            if (Integer.parseInt(status) < Integer.parseInt(minStatus)) {
                minStatus = status;
            }
        }

        return minStatus;
    }


    //获取成品tab数量
    private Map<String,Integer> getCompleteTabNumber(SalYtOrderSubItem params) {

        //根据父订单id查询子订单详情表
        List<SalYtOrderSubItem> orderSubItemList = salYtOrderSubItemMapper.selectSalYtOrderSubItemLists(params);

        List<SalYtOrderSubItem> resultList = new ArrayList<>();
        orderSubItemList.forEach(item -> {
            Long specificationId = item.getSpecificationId();
            if (specificationId != null) {
                //填充产品code
                Long productId = item.getProductId();
                ProYtProduct proYtProduct = proYtProductMapper.selectById(productId);
                item.setProductCode(proYtProduct.getCode());
                //填充规格图片
                //List<ProYtProductFile> proYtProductFiles = proYtProductFileMapper.selectByMasterIdAndType(specificationId, ProductFilesTypeEnum.specification.getKey());
                // item.setImageList(proYtProductFiles);

                //填充规格项
                List<ProYtProductSpecificationItem> specificationItems = proYtProductSpecificationItemMapper.selectByProductSpecificationId(specificationId);
                item.setItemList(specificationItems);
                resultList.add(item);
            }
        });

        //处理resultList,合并
        List<SalYtOrderSubItem> resultList1=doMergeData(resultList);
        Map<String,Integer> statusNumberMap=new HashMap<>();
        for (SalYtOrderSubItem item : resultList1) {
            String status = item.getStatus();
            if (statusNumberMap.containsKey(status)) {
                statusNumberMap.put(status, statusNumberMap.get(status) + 1);
            }
            else {
                statusNumberMap.put(status, 1);
            }
        }
        return statusNumberMap;
    }

    /**
     * 订单半成品详情
     * @ orderId
     * @return
     */
    public List<SalYtOrderSubItem> orderInCompleteList(SalYtOrderSubItem params) {
        //查询订单,判断订单是否存在
        LambdaQueryWrapper<SalYtOrder> queryWrapper = Wrappers.lambdaQuery(SalYtOrder.class)
                .eq(SalYtOrder::getId, params.getOrderId())
                .eq(SalYtOrder::getIsDeleted, 0);
        SalYtOrder order = salYtOrderMapper.selectOne(queryWrapper);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        //根据订单id查询子订单   salYtOrderSubItemMapper.inCompleteList(params);
        //List<SalYtOrderSubItem> orderSubItemList = salYtOrderSubMapper.selectOrderSubByOrderId(params.getOrderId());
        List<SalYtOrderSubItem> orderSubItemList = salYtOrderSubItemMapper.inCompleteList(params);

        //将产品id和规格id相同的数据进行聚合
        List<SalYtOrderSubItem> itemList=doMerge(orderSubItemList);

        //对聚合后的数据进行处理
        // 对查询结果进行数据填充
        orderSubItemList.forEach(item -> {
            Long productId = item.getProductId();
            if (productId != null) {
                // 填充产品信息
                ProYtProduct proYtProduct = proYtProductMapper.selectById(productId);
                if (proYtProduct != null) {
                    item.setProductCode(proYtProduct.getCode());
                }
            }
            //填充产品图片
            List<ProYtProductFile> proYtProductFiles = proYtProductFileMapper.selectByMasterIdAndType(productId, ProductFilesTypeEnum.product.getKey());
            item.setImageList(proYtProductFiles);

            Integer confirmNumber = item.getConfirmNumber();
            if (confirmNumber == null) {
                item.setConfirmNumber(0);
            }
            if (confirmNumber < item.getNumber()) {
                item.setConfirmStatus(0);
            } else {
                item.setConfirmStatus(1);
            }
        });


        return itemList;
    }

    private List<SalYtOrderSubItem> doMerge(List<SalYtOrderSubItem> orderSubItemList) {
        if (orderSubItemList == null || orderSubItemList.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        java.util.Map<String, SalYtOrderSubItem> mergedMap = new java.util.LinkedHashMap<>();

        for (SalYtOrderSubItem item : orderSubItemList) {
            if (item == null) {
                continue;
            }

            Long productId = item.getProductId();
            Long specificationId = item.getSpecificationId();
            java.math.BigDecimal price = item.getPrice();
            Long labelId = item.getLabelId();

            String key = (productId == null ? "null" : productId.toString()) + "#" +
                    (specificationId == null ? "null" : specificationId.toString()) + "#" +
                    (price == null ? "null" : price.toPlainString()) + "#" +
                    (labelId == null ? "null" : labelId.toString());

            SalYtOrderSubItem merged = mergedMap.get(key);
            if (merged == null) {
                Integer baseConfirm = item.getConfirmNumber() == null ? 0 : item.getConfirmNumber();
                Integer baseNumber = item.getNumber() == null ? 0 : item.getNumber();
                item.setConfirmNumber(baseConfirm);
                item.setNumber(baseNumber);
                // 初始化合并id集合：首次出现时放入自身id（合并前该字段为空）
                if (item.getSalYtOrderSubItemIds() == null) {
                    item.setSalYtOrderSubItemIds(new java.util.ArrayList<>());
                } else {
                    item.getSalYtOrderSubItemIds().clear();
                }
                if (item.getId() != null) {
                    item.getSalYtOrderSubItemIds().add(item.getId());
                }
                mergedMap.put(key, item);
            } else {
                int mergedConfirm = merged.getConfirmNumber() == null ? 0 : merged.getConfirmNumber();
                int mergedNumber = merged.getNumber() == null ? 0 : merged.getNumber();

                int currentConfirm = item.getConfirmNumber() == null ? 0 : item.getConfirmNumber();
                int currentNumber = item.getNumber() == null ? 0 : item.getNumber();

                merged.setConfirmNumber(mergedConfirm + currentConfirm);
                merged.setNumber(mergedNumber + currentNumber);

                // 累加合并id集合
                if (merged.getSalYtOrderSubItemIds() == null) {
                    merged.setSalYtOrderSubItemIds(new java.util.ArrayList<>());
                }
                if (merged.getSalYtOrderSubItemIds().isEmpty() && merged.getId() != null) {
                    merged.getSalYtOrderSubItemIds().add(merged.getId());
                }
                if (item.getId() != null) {
                    merged.getSalYtOrderSubItemIds().add(item.getId());
                }
            }
        }

        return new java.util.ArrayList<>(mergedMap.values());
    }

    /**
     * 查询订单详情中的产品tab
     * @param params
     * @return
     */
    public SalYtOrderVo orderDetailProductTab(SalYtOrderSubItem params) {
        //判断订单是否存在
        LambdaQueryWrapper<SalYtOrder> queryWrapper = Wrappers.lambdaQuery(SalYtOrder.class)
                .eq(SalYtOrder::getId, params.getOrderId())
                .eq(SalYtOrder::getIsDeleted, 0);
        SalYtOrder order = salYtOrderMapper.selectOne(queryWrapper);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        SalYtOrderVo result = new SalYtOrderVo();
        //根据父订单id查询子订单详情表
        List<SalYtOrderSubItem> orderSubItemList = salYtOrderSubItemMapper.selectSalYtOrderSubItemLists(params);

        List<SalYtOrderSubItem> resultList = new ArrayList<>();
        orderSubItemList.forEach(item -> {
            Long specificationId = item.getSpecificationId();
            if (specificationId != null) {
                //填充产品code
                Long productId = item.getProductId();
                ProYtProduct proYtProduct = proYtProductMapper.selectById(productId);
                item.setProductCode(proYtProduct.getCode());
                //填充规格图片
                List<ProYtProductFile> proYtProductFiles = proYtProductFileMapper.selectByMasterIdAndType(specificationId, ProductFilesTypeEnum.specification.getKey());
                item.setImageList(proYtProductFiles);

                //填充规格项
               // List<ProYtProductSpecificationItem> specificationItems = proYtProductSpecificationItemMapper.selectByProductSpecificationsId(specificationId);
                List<ProYtProductSpecificationItem> specificationItems = proYtProductSpecificationItemMapper.selectByProductSpecificationId(specificationId);
                item.setItemList(specificationItems);
                resultList.add(item);
            }
        });

        //处理resultList,合并
        List<SalYtOrderSubItem> resultList1=doMergeData(resultList);
        SalYtOrderSub orderSub = new SalYtOrderSub();
        // 将item数据设置到子订单中
        orderSub.setItemList(resultList1);
        result.setSubOrder(orderSub);
        //赋值币种信息
        result.setCurrency(order.getCurrency());

        return result;
    }

    //合并产品数据
    private List<SalYtOrderSubItem> doMergeData(List<SalYtOrderSubItem> orderSubItemList) {
        if (orderSubItemList == null || orderSubItemList.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        java.util.Map<String, SalYtOrderSubItem> mergedMap = new java.util.LinkedHashMap<>();

        for (SalYtOrderSubItem item : orderSubItemList) {
            if (item == null) {
                continue;
            }

            // itemList 合并条件：categorySpecificationId / categorySpecificationItemId / productSpecificationId 完全一致
            java.util.List<com.qiaomoyun.entity.pro.yt.ProYtProductSpecificationItem> itemList = item.getItemList();
            String itemListKey;
            if (itemList == null) {
                itemListKey = "null";
            } else {
                java.util.List<String> triples = new java.util.ArrayList<>(itemList.size());
                for (com.qiaomoyun.entity.pro.yt.ProYtProductSpecificationItem it : itemList) {
                    if (it == null) {
                        triples.add("null#null#null");
                    } else {
                        Long a = it.getCategorySpecificationId();
                        Long b = it.getCategorySpecificationItemId();
                        Long c = it.getProductSpecificationId();
                        triples.add(
                                (a == null ? "null" : a.toString()) + "#" +
                                        (b == null ? "null" : b.toString()) + "#" +
                                        (c == null ? "null" : c.toString())
                        );
                    }
                }
                java.util.Collections.sort(triples);
                itemListKey = String.join(";", triples);
            }

            java.math.BigDecimal price = item.getPrice();
            java.math.BigDecimal supplierPrice = item.getSupplierPrice();
            String key =
                    (item.getProductId() == null ? "null" : item.getProductId().toString()) + "|" +
                            (item.getSpecificationId() == null ? "null" : item.getSpecificationId().toString()) + "|" +
                            (price == null ? "null" : price.toPlainString()) + "|" +
                            (item.getLabelId() == null ? "null" : item.getLabelId().toString()) + "|" +
                            (item.getRemark() == null ? "null" : item.getRemark()) + "|" +
                            (supplierPrice == null ? "null" : supplierPrice.toPlainString()) + "|" +
                            itemListKey;

            SalYtOrderSubItem merged = mergedMap.get(key);
            if (merged == null) {
                // 统一把需要累加的数值字段置为非空，避免后续 NPE
                item.setDeliveryNumber(item.getDeliveryNumber() == null ? 0 : item.getDeliveryNumber());
                item.setEnterNumber(item.getEnterNumber() == null ? 0 : item.getEnterNumber());
                item.setNumber(item.getNumber() == null ? 0 : item.getNumber());
                if (item.getSalYtOrderSubItemIds() == null) {
                    item.setSalYtOrderSubItemIds(new java.util.ArrayList<>());
                } else {
                    item.getSalYtOrderSubItemIds().clear();
                }
                if (item.getId() != null) {
                    item.getSalYtOrderSubItemIds().add(item.getId());
                }
                mergedMap.put(key, item);
                continue;
            }

            // deliveryNumber / enterNumber / number 累加
            merged.setDeliveryNumber((merged.getDeliveryNumber() == null ? 0 : merged.getDeliveryNumber())
                    + (item.getDeliveryNumber() == null ? 0 : item.getDeliveryNumber()));
            merged.setEnterNumber((merged.getEnterNumber() == null ? 0 : merged.getEnterNumber())
                    + (item.getEnterNumber() == null ? 0 : item.getEnterNumber()));
            merged.setNumber((merged.getNumber() == null ? 0 : merged.getNumber())
                    + (item.getNumber() == null ? 0 : item.getNumber()));
            if (merged.getSalYtOrderSubItemIds() == null) {
                merged.setSalYtOrderSubItemIds(new java.util.ArrayList<>());
            }
            if (merged.getSalYtOrderSubItemIds().isEmpty() && merged.getId() != null) {
                merged.getSalYtOrderSubItemIds().add(merged.getId());
            }
            if (item.getId() != null) {
                merged.getSalYtOrderSubItemIds().add(item.getId());
            }

            // status 取最小值（字符串：优先按数字比较，不可转数字则按字典序）
            String s1 = merged.getStatus();
            String s2 = item.getStatus();
            if (s1 == null) {
                merged.setStatus(s2);
            } else if (s2 != null) {
                java.math.BigInteger n1 = null;
                java.math.BigInteger n2 = null;
                try {
                    n1 = new java.math.BigInteger(s1.trim());
                    n2 = new java.math.BigInteger(s2.trim());
                } catch (Exception ignored) {
                    // ignore
                }
                if (n1 != null && n2 != null) {
                    if (n2.compareTo(n1) < 0) {
                        merged.setStatus(s2);
                    }
                } else {
                    if (s2.compareTo(s1) < 0) {
                        merged.setStatus(s2);
                    }
                }
            }
        }

        return new java.util.ArrayList<>(mergedMap.values());
    }

    /**
     * 修改发货方式
     * @param params
     * @return
     */
    public String updateShippingMethod(SalYtOrderUpdateShippingMethodParams params) {
        //判断订单是否存在
        LambdaQueryWrapper<SalYtOrder> queryWrapper = Wrappers.lambdaQuery(SalYtOrder.class)
                .eq(SalYtOrder::getId, params.getOrderId())
                .eq(SalYtOrder::getIsDeleted, 0);
        SalYtOrder order = salYtOrderMapper.selectOne(queryWrapper);
        if (order == null) {
            throw new BizException("订单不存在");
        }
        order.setShippingMethod(params.getShippingMethod());
        int count=    salYtOrderMapper.updateById(order);
        if(count>0){
            return "修改成功";
        }else{
            return "修改失败";
        }
    }

    /**
     * 订单退货接口2.0
     * @param params
     */
    public void orderReturnItem(SalYtOrderOrderReturnItemParams params) {
        //根据传入的参数查询出sal_yt_order_sub_item表的信息
        List<SalYtOrderSubItem> list = salYtOrderSubItemMapper.selectOrderReturnItemInfo(params);
        if(list.isEmpty()){
            throw new BizException("没有可退货的订单");
        }

        //总数量
        Integer totalNumber = 0;
        //半成品确认数量
        Integer confirmNumber = 0;
        //子订单item的id集合
        List<Long> itemIds = new ArrayList<>();
        //已经发货总数量
        int totalDeliverNumber = 0;
        //计算总共数量
        for (SalYtOrderSubItem item : list) {
            totalNumber += item.getNumber();
            itemIds.add(item.getId());
            if(item.getDeliveryNumber()!=null) {
                totalDeliverNumber += item.getDeliveryNumber();
            }
        }
        if(params.getNumber()>totalNumber){
            throw new BizException("退货数量不能大于可退货数量");
        }

        //判断是否是半成品退货还是成品退货
        if(params.getSpecificationId()!=null){
            //成品退货
            //待发货数量
            Integer waitDeliveryNumber=totalNumber-totalDeliverNumber;
            // 4. 判断退货数量是否大于待发货数量
            if (params.getNumber() > waitDeliveryNumber) {
                throw new BizException(ExceptionCodeEnum.Order_Status_Error.getCode(), "退货数量不能大于待发货数量");
            }

            //判断订单是否发起申购，判断订单状态
                //查询订单
            SalYtOrder order = salYtOrderMapper.selectById(params.getOrderId());
            if (order.getStatus().equals(OrderStatusEnum.Passed.getKey())) {
                //有申购
                //接着判断有没有采购单

                //有

                //没有

            }else{
                //没有申购

                //仅占用库存


                //仅占用在途

                //占用在途+占用库存（优先退占用在途）
            }





            // 5. 计算待入库数量
            Integer waitStockNumber = totalNumber - totalDeliverNumber;
            // 6. 如果退货数量大于待入库数量，需要减少被占用库存并增加可用库存
            if (params.getNumber() > waitStockNumber) {
                // 减少被占用库存到可用库存
                stoYtStoreManager.reduceOccupyStoreStockToEnableStock(params.getSpecificationId(), params.getNumber() - waitStockNumber);
            }

            for(SalYtOrderSubItem orderSubItem : list) {
                //判断是否由半成品确认，如果由半成品确认，需要减少半成品的总数量
                Long confirmItemId = orderSubItem.getConfirmItemId();
                if (confirmItemId != null) {
                    //由半成品确认，减少半成品的总数量
                    SalYtOrderSubItem confirmedOrderSubItem = salYtOrderSubItemMapper.selectById(confirmItemId);
                    if (confirmedOrderSubItem != null) {
                        confirmedOrderSubItem.setNumber(confirmedOrderSubItem.getNumber() - params.getNumber());
                        salYtOrderSubItemMapper.updateById(confirmedOrderSubItem);
                    }
                }
            }



        }else{

            //半成品退货
            //根据itemIds查询出子订单item确认的信息（半成品）
            List<SalYtOrderSubItemConfirm> confirmList = salYtOrderSubItemConfirmMapper.selectBatchIds(itemIds);
            if(!confirmList.isEmpty()) {
                //计算总共确认数量
                for (SalYtOrderSubItemConfirm confirm : confirmList) {
                    confirmNumber += confirm.getNumber();
                }
            }

            if(params.getNumber()>totalNumber-confirmNumber){
                throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(),
                        "退货数量不能大于已确认数量（已确认数量：" + confirmNumber + "）");
            }
        }


        //总共退货数量
        Integer returnNumber = params.getNumber();
        for (SalYtOrderSubItem orderSubItem : list){
             //子订单id
            Long orderSubItemId = orderSubItem.getId();

            //进行退货处理

            //得到子订单item里面数量
            Integer number = orderSubItem.getNumber();

            //判断该子订单item这条数据是否能满足退货要求
            //该item能退货完
            if(number>=returnNumber){
                // 7. 减少子订单item的数量
                orderSubItem.setNumber(number-returnNumber);
                //添加退货记录
                SalYtReturnOrder salYtReturnOrder = new SalYtReturnOrder();
                salYtReturnOrder.setType(1);
                salYtReturnOrder.setOrderSubItemId(orderSubItem.getId());
                salYtReturnOrder.setBeforeReturnNumber(orderSubItem.getNumber());
                salYtReturnOrder.setReturnNumber(returnNumber);
                salYtReturnOrder.setReason(params.getReason());
                salYtReturnOrderMapper.insert(salYtReturnOrder);

                salYtOrderSubItemMapper.updateById(orderSubItem);

                //添加操作记录
                salYtOrderSubItemOperationManager.returnOperation(returnNumber, orderSubItem.getId());

                //减少发货单详情数量
                reduceDeliveryItemNumber(orderSubItemId, returnNumber);

                //退货完成
                break;
            }else{
                //该item不能退货完,继续进行下一条数据处理
                returnNumber -= number;
                //添加退货记录
                SalYtReturnOrder salYtReturnOrder = new SalYtReturnOrder();
                salYtReturnOrder.setType(1);
                salYtReturnOrder.setOrderSubItemId(orderSubItem.getId());
                salYtReturnOrder.setBeforeReturnNumber(orderSubItem.getNumber());
                salYtReturnOrder.setReturnNumber(returnNumber);
                salYtReturnOrder.setReason(params.getReason());
                salYtReturnOrderMapper.insert(salYtReturnOrder);

                // 7. 减少子订单item的数量,因为该条数据不够，所以将数量全退，剩下不够的循环至下条数据接着扣减
                orderSubItem.setNumber(0);

                salYtOrderSubItemMapper.updateById(orderSubItem);

                //添加操作记录
                salYtOrderSubItemOperationManager.returnOperation(returnNumber, orderSubItem.getId());

                //减少发货单详情数量
                reduceDeliveryItemNumber(orderSubItemId, number);
            }


        }
    }

    //减少发货单详情数量
    private void  reduceDeliveryItemNumber(Long orderSubItemId, Integer returnNumber){
        //根据orderSubItemId查询出发货单详情
        StoYtDeliveryItem deliveryItem = stoYtDeliveryItemMapper.selectByOrderSubItemId(orderSubItemId);
        if(deliveryItem!=null){
            //减少发货单详情数量
            deliveryItem.setNumber(deliveryItem.getNumber()-returnNumber);
            stoYtDeliveryItemMapper.updateById(deliveryItem);
        }

    }

    private void releaseCloseOrderOccupy(SalYtOrder order, SalYtOrderSubItem orderItem) {
        Long specificationId = orderItem.getSpecificationId();
        if (specificationId == null) {
            return;
        }

//        Integer occupyStoreNumber = orderItem.getOccupyStoreNumber() != null ? orderItem.getOccupyStoreNumber() : 0;
//        Integer occupyTransitNumber = orderItem.getOccupyTransitNumber() != null ? orderItem.getOccupyTransitNumber() : 0;
//        Integer occupyTransitEnterNumber = orderItem.getOccupyTransitEnterNumber() != null ? orderItem.getOccupyTransitEnterNumber() : 0;
        Integer deliveryNumber = orderItem.getDeliveryNumber() != null ? orderItem.getDeliveryNumber() : 0;
        int releaseOccupyStoreNumber = Math.max(orderItem.getEnterNumber() - deliveryNumber, 0);
        int releaseOccupyTransitNumber = Math.max(orderItem.getNumber() - orderItem.getEnterNumber(), 0);
        if (releaseOccupyStoreNumber <= 0 && releaseOccupyTransitNumber <= 0) {
            return;
        }

        if (releaseOccupyStoreNumber > 0) {
            stoYtStoreManager.reduceOccupyStoreStockToEnableStock(specificationId, releaseOccupyStoreNumber);
        }
        if (releaseOccupyTransitNumber > 0) {
            stoYtStoreManager.reduceOccupyTransitStockToEnableTransitStock(specificationId, releaseOccupyTransitNumber);
        }

        applicationEventPublisher.publishEvent(new StoreChangeEvent(
                this,
                StoreEnterOutTypeEnum.closeOrderRelease.getKey(),
                releaseOccupyStoreNumber,
                releaseOccupyTransitNumber,
                specificationId,
                order.getId(),
                orderItem.getOrderSubId(),
                "关闭订单释放占用"
        ));
    }

    private void cleanupCloseOrderDelivery(SalYtOrderSubItem orderItem) {
        List<StoYtDeliveryItem> deliveryItems = stoYtDeliveryItemMapper.selectBySubItemId(orderItem.getId());
        if (deliveryItems == null || deliveryItems.isEmpty()) {
            return;
        }

        Set<Long> touchedDeliveryIds = new HashSet<>();
        for (StoYtDeliveryItem deliveryItem : deliveryItems) {
            StoYtDelivery delivery = stoYtDeliveryMapper.selectById(deliveryItem.getDeliveryId());
            if (delivery == null || Integer.valueOf(1).equals(delivery.getIsDeleted())) {
                continue;
            }
            if (DeliveryOrderStatusEnum.delivered.getKey().toString().equals(delivery.getStatus())) {
                continue;
            }

            deliveryItem.setIsDeleted(1);
            stoYtDeliveryItemMapper.updateById(deliveryItem);
            touchedDeliveryIds.add(delivery.getId());

            List<StoYtDeliveryBoxItem> boxItems = stoYtDeliveryBoxItemMapper.selectByDeliveryId(delivery.getId());
            for (StoYtDeliveryBoxItem boxItem : boxItems) {
                if (Objects.equals(boxItem.getOrderItemId(), orderItem.getId())) {
                    boxItem.setIsDeleted(1);
                    stoYtDeliveryBoxItemMapper.updateById(boxItem);
                }
            }
        }

        for (Long deliveryId : touchedDeliveryIds) {
            List<StoYtDeliveryItem> restItems = stoYtDeliveryItemMapper.listByDeliveryId(deliveryId);
            if (restItems == null || restItems.isEmpty()) {
                StoYtDelivery delivery = stoYtDeliveryMapper.selectById(deliveryId);
                if (delivery != null && !DeliveryOrderStatusEnum.delivered.getKey().toString().equals(delivery.getStatus())) {
                    delivery.setIsDeleted(1);
                    stoYtDeliveryMapper.updateById(delivery);
                }
            }
        }
    }

    private Map<Long, Integer> buildNumberMap(List<Map<String, Object>> dataList) {
        Map<Long, Integer> resultMap = new HashMap<>();
        if (dataList == null || dataList.isEmpty()) {
            return resultMap;
        }
        for (Map<String, Object> item : dataList) {
            Object idValue = item.get("orderSubItemId");
            Object totalValue = item.get("totalNumber");
            if (idValue == null) {
                continue;
            }
            Long orderSubItemId = Long.valueOf(String.valueOf(idValue));
            Integer totalNumber = totalValue == null ? 0 : Integer.valueOf(String.valueOf(totalValue));
            resultMap.put(orderSubItemId, totalNumber);
        }
        return resultMap;
    }

    /**
     * 订单详情里面的导出物流
     * @param params
     * @param response
     */
    public void exportDelivery(SalYtOrderExportDeliveryParams params, HttpServletResponse response) {
        SalYtOrder order = salYtOrderMapper.selectById(params.getOrderId());
        if (order == null) {
            throw new BizException(ExceptionCodeEnum.Not_Exists.getCode(), "订单不存在");
        }

        StoYtDelivery delivery = stoYtDeliveryMapper.selectById(params.getDeliveryId());
        if (delivery == null) {
            throw new BizException(ExceptionCodeEnum.Not_Exists.getCode(), "发货单不存在");
        }

        SysStorage sysStorage = sysStorageMapper.selectById(delivery.getTransportOrderFileId());
        String url = sysStorage == null ? null : sysStorage.getUrl();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String deliveryTime = delivery.getDeliveryTime() == null ? "" : sdf.format(delivery.getDeliveryTime());
        String packageCode = delivery.getPackageCode();

        String info = "订单编号：" + order.getCode() + "\n" +
                "发货单号：" + delivery.getCode() + "\n" +
                "发货时间：" + deliveryTime + "\n" +
                "发货地址：" + delivery.getAddress() + "\n";
        if (packageCode != null) {
            info += "物流单号：" + packageCode + "\n";
        }

        List<StoYtDeliveryBox> deliveryBoxList = getDeliveryBoxDetailList(params.getDeliveryId());
        if (deliveryBoxList.isEmpty()) {
            throw new BizException(ExceptionCodeEnum.Not_Exists.getCode(), "发货单暂无包裹信息");
        }

        List<SalYtOrderExportDeliveryProductInfoVo> productInfoList = productInfoList(deliveryBoxList);
        List<SalYtOrderExportDeliveryProductInfoVo> productInfoLists = doProductInfoList(productInfoList);

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("物流导出");
        XSSFDrawing drawing = sheet.createDrawingPatriarch();

        XSSFRow titleRow = sheet.createRow(0);
        int infoLineCount = info.split("\n").length;
        int baseHeight = infoLineCount * 300 + 100;
        if (StringUtils.isNotBlank(url)) {
            baseHeight = Math.max(baseHeight, 1000);
        }
        titleRow.setHeight((short) baseHeight);

        XSSFCell titleA = titleRow.createCell(0);
        titleA.setCellValue(info);

        XSSFCell titleI = titleRow.createCell(8);
        if (StringUtils.isNotBlank(url)) {
            try {
                QMYExcelUtil.insertImageToCell(workbook, drawing, url, 0, 8, 0.1, 0.1, 0.9);
            } catch (Exception e) {
                throw new RuntimeException("下载面单图片失败: " + url, e);
            }
        }

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));

        CellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.LEFT);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titleStyle.setBorderTop(BorderStyle.THIN);
        titleStyle.setBorderBottom(BorderStyle.THIN);
        titleStyle.setBorderLeft(BorderStyle.THIN);
        titleStyle.setBorderRight(BorderStyle.THIN);
        titleStyle.setWrapText(true);
        titleA.setCellStyle(titleStyle);
        titleI.setCellStyle(titleStyle);

        XSSFRow headerRow = sheet.createRow(1);
        headerRow.setHeight((short) 500);

        String[] headers = {"箱号", "尺寸", "重量", "产品ID", "本次发货总数", "规格图片", "规格名称", "本次发货数量"};
        for (int i = 0; i < headers.length; i++) {
            XSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeight((short) 240);
            headerStyle.setFont(headerFont);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            cell.setCellStyle(headerStyle);
        }

        sheet.setColumnWidth(0, 18 * 256);
        sheet.setColumnWidth(1, 20 * 256);
        sheet.setColumnWidth(2, 12 * 256);
        sheet.setColumnWidth(3, 18 * 256);
        sheet.setColumnWidth(4, 15 * 256);
        sheet.setColumnWidth(5, 15 * 256);
        sheet.setColumnWidth(6, 25 * 256);
        sheet.setColumnWidth(7, 15 * 256);
        sheet.setColumnWidth(8, 20 * 256);

        int dataRowIndex = 2;
        Map<Long, Integer> boxStartRowMap = new LinkedHashMap<>();
        Map<Long, Integer> boxRowCountMap = new LinkedHashMap<>();
        Map<String, Integer> productStartRowMap = new LinkedHashMap<>();
        Map<String, Integer> productSpecCountMap = new LinkedHashMap<>();
        Map<Integer, Integer> rowHeightMap = new LinkedHashMap<>();

        for (SalYtOrderExportDeliveryProductInfoVo product : productInfoLists) {
            int specCount = product.getSpecsInfos() == null ? 0 : product.getSpecsInfos().size();
            if (specCount <= 0) {
                continue;
            }
            Long deliveryBoxId = product.getDeliveryBoxId();
            String productKey = buildDeliveryExportProductKey(product);
            boxRowCountMap.put(deliveryBoxId, boxRowCountMap.getOrDefault(deliveryBoxId, 0) + specCount);
            productSpecCountMap.put(productKey, specCount);
        }

        for (SalYtOrderExportDeliveryProductInfoVo product : productInfoLists) {
            List<SalYtOrderExportDeliverySpecsVo> specsInfos = product.getSpecsInfos();
            if (specsInfos == null || specsInfos.isEmpty()) {
                continue;
            }

            Long deliveryBoxId = product.getDeliveryBoxId();
            if (!boxStartRowMap.containsKey(deliveryBoxId)) {
                boxStartRowMap.put(deliveryBoxId, dataRowIndex);
            }
            String productKey = buildDeliveryExportProductKey(product);
            productStartRowMap.put(productKey, dataRowIndex);

            for (int i = 0; i < specsInfos.size(); i++) {
                SalYtOrderExportDeliverySpecsVo specs = specsInfos.get(i);
                XSSFRow dataRow = sheet.createRow(dataRowIndex);
                dataRow.setHeight((short) 0);

                boolean isFirstBoxRow = boxStartRowMap.get(deliveryBoxId) == dataRowIndex;

                XSSFCell cellA = dataRow.createCell(0);
                if (isFirstBoxRow) {
                    cellA.setCellValue(product.getBoxCode() != null ? product.getBoxCode() : "");
                }

                XSSFCell cellB = dataRow.createCell(1);
                if (isFirstBoxRow) {
                    cellB.setCellValue(product.getBoxSize() != null ? product.getBoxSize() : "");
                }

                XSSFCell cellC = dataRow.createCell(2);
                if (isFirstBoxRow) {
                    cellC.setCellValue(product.getBoxWeight() != null ? product.getBoxWeight() : "");
                }

                XSSFCell cellD = dataRow.createCell(3);
                if (i == 0) {
                    cellD.setCellValue(product.getProductCode() != null ? product.getProductCode() : "");
                }

                XSSFCell cellE = dataRow.createCell(4);
                if (i == 0) {
                    cellE.setCellValue(product.getTotalNum() != null ? product.getTotalNum().toString() : "");
                }

                XSSFCell cellF = dataRow.createCell(5);
                if (StringUtils.isNotBlank(specs.getSpecsImg())) {
                    rowHeightMap.put(dataRowIndex, 1);
                    try {
                        QMYExcelUtil.insertImageToCell(workbook, drawing, specs.getSpecsImg(), dataRowIndex, 5, 0.1, 0.1, 0.8);
                    } catch (Exception e) {
                        throw new RuntimeException("下载规格图片失败: " + specs.getSpecsImg(), e);
                    }
                }

                XSSFCell cellG = dataRow.createCell(6);
                cellG.setCellValue(specs.getSpecsName() != null ? specs.getSpecsName() : "");

                XSSFCell cellH = dataRow.createCell(7);
                cellH.setCellValue(specs.getSpecsNum() != null ? specs.getSpecsNum().toString() : "");

                CellStyle dataStyle = workbook.createCellStyle();
                dataStyle.setAlignment(HorizontalAlignment.CENTER);
                dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                dataStyle.setBorderTop(BorderStyle.THIN);
                dataStyle.setBorderBottom(BorderStyle.THIN);
                dataStyle.setBorderLeft(BorderStyle.THIN);
                dataStyle.setBorderRight(BorderStyle.THIN);
                dataStyle.setWrapText(true);
                cellA.setCellStyle(dataStyle);
                cellB.setCellStyle(dataStyle);
                cellC.setCellStyle(dataStyle);
                cellD.setCellStyle(dataStyle);
                cellE.setCellStyle(dataStyle);
                cellF.setCellStyle(dataStyle);
                cellG.setCellStyle(dataStyle);
                cellH.setCellStyle(dataStyle);

                dataRowIndex++;
            }
        }

        for (Map.Entry<Long, Integer> entry : boxStartRowMap.entrySet()) {
            Long deliveryBoxId = entry.getKey();
            int startRow = entry.getValue();
            int rowCount = boxRowCountMap.getOrDefault(deliveryBoxId, 0);
            if (rowCount > 1) {
                int endRow = startRow + rowCount - 1;
                sheet.addMergedRegion(new CellRangeAddress(startRow, endRow, 0, 0));
                sheet.addMergedRegion(new CellRangeAddress(startRow, endRow, 1, 1));
                sheet.addMergedRegion(new CellRangeAddress(startRow, endRow, 2, 2));
            }
        }

        for (Map.Entry<String, Integer> entry : productStartRowMap.entrySet()) {
            int startRow = entry.getValue();
            int specCount = productSpecCountMap.getOrDefault(entry.getKey(), 0);
            if (specCount > 1) {
                int endRow = startRow + specCount - 1;
                sheet.addMergedRegion(new CellRangeAddress(startRow, endRow, 3, 3));
                sheet.addMergedRegion(new CellRangeAddress(startRow, endRow, 4, 4));
            }
        }

        int firstDataRow = 2;
        int lastDataRow = dataRowIndex - 1;
        for (int rowNum = firstDataRow; rowNum <= lastDataRow; rowNum++) {
            XSSFRow row = sheet.getRow(rowNum);
            if (row == null) {
                continue;
            }

            int maxChars = 0;
            for (int col = 0; col < 8; col++) {
                XSSFCell cell = row.getCell(col);
                if (cell == null || cell.getCellType() == CellType.BLANK) {
                    continue;
                }

                String cellValue = "";
                if (cell.getCellType() == CellType.STRING) {
                    cellValue = cell.getStringCellValue();
                } else if (cell.getCellType() == CellType.NUMERIC) {
                    cellValue = String.valueOf((int) cell.getNumericCellValue());
                }

                String[] lines = cellValue.split("\n");
                for (String line : lines) {
                    int lineChars = line.length();
                    int lineWidth = sheet.getColumnWidth(col) / 256;
                    if (lineWidth > 0) {
                        int wrappedLines = (int) Math.ceil((double) lineChars / (lineWidth * 0.5));
                        maxChars = Math.max(maxChars, wrappedLines * lineWidth);
                    }
                }
            }

            short height;
            if (rowHeightMap.containsKey(rowNum)) {
                height = (short) 600;
            } else {
                int baseHeight1 = 300;
                int charHeight = maxChars > 0 ? maxChars / 2 : 1;
                height = (short) Math.min(2000, Math.max(baseHeight1, baseHeight1 + charHeight * 50));
            }
            row.setHeight(height);
        }

        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("物流导出" + order.getCode(), "UTF-8") + ".xlsx");
            ServletOutputStream out = response.getOutputStream();
            workbook.write(out);
            workbook.close();
            out.flush();
            out.close();
        } catch (Exception e) {
            throw new BizException(ExceptionCodeEnum.System_Error.getCode(), "导出失败");
        }
    }

    private List<StoYtDeliveryBox> getDeliveryBoxDetailList(Long deliveryId) {
        List<StoYtDeliveryBox> boxList = stoYtDeliveryBoxMapper.selectByDeliveryId(deliveryId);
        if (boxList == null || boxList.isEmpty()) {
            return Collections.emptyList();
        }
        List<StoYtDeliveryBox> detailList = new ArrayList<>();
        for (StoYtDeliveryBox stoYtDeliveryBox : boxList) {
            detailList.add((StoYtDeliveryBox) packageDetail(stoYtDeliveryBox.getId()));
        }
        return detailList;
    }

    private List<SalYtOrderExportDeliveryProductInfoVo> productInfoList(List<StoYtDeliveryBox> stoYtDeliveryBoxes){
        List<SalYtOrderExportDeliveryProductInfoVo> productInfoList = new ArrayList<>();
        for (StoYtDeliveryBox stoYtDeliveryBox : stoYtDeliveryBoxes) {
            List<StoYtDeliveryBoxItem> boxItemList = stoYtDeliveryBox.getBoxItemList();
            if (boxItemList == null || boxItemList.isEmpty()) {
                continue;
            }
            for (StoYtDeliveryBoxItem boxItem : boxItemList) {
                SalYtOrderExportDeliveryProductInfoVo productInfo = new SalYtOrderExportDeliveryProductInfoVo();
                productInfo.setDeliveryBoxId(stoYtDeliveryBox.getId());
                productInfo.setBoxCode(stoYtDeliveryBox.getBoxCode());
                productInfo.setBoxSize(stoYtDeliveryBox.getBoxSize());
                productInfo.setBoxWeight(stoYtDeliveryBox.getBoxWeight() == null ? "" : stoYtDeliveryBox.getBoxWeight().toPlainString() + "kg");
                productInfo.setProductId(boxItem.getProductId());
                productInfo.setProductCode(boxItem.getProductCode());
                productInfo.setSpecsNum(boxItem.getNumber());

                List<ProYtProductFile> imageList = boxItem.getImageList();
                if (imageList != null && !imageList.isEmpty()) {
                    productInfo.setSpecsImg(imageList.get(0).getUrl());
                }

                List<ProYtProductSpecificationItem> itemList = boxItem.getItemList();
                if (itemList != null && !itemList.isEmpty()) {
                    StringBuilder specsName = new StringBuilder();
                    for (ProYtProductSpecificationItem item : itemList) {
                        specsName.append(item.getCategorySpecificationName()).append(":").append(item.getCategorySpecificationItemValue());
                    }
                    productInfo.setSpecsName(specsName.toString());
                }
                productInfo.setSpecsId(boxItem.getSpecificationId());
                productInfoList.add(productInfo);
            }
        }
        return productInfoList;
    }

    private List<SalYtOrderExportDeliveryProductInfoVo> doProductInfoList(List<SalYtOrderExportDeliveryProductInfoVo> productInfoList) {
        Map<String, SalYtOrderExportDeliveryProductInfoVo> productMap = new LinkedHashMap<>();
        for (SalYtOrderExportDeliveryProductInfoVo item : productInfoList) {
            String productKey = buildDeliveryExportProductKey(item);
            SalYtOrderExportDeliveryProductInfoVo product = productMap.get(productKey);
            if (product == null) {
                product = new SalYtOrderExportDeliveryProductInfoVo();
                product.setDeliveryBoxId(item.getDeliveryBoxId());
                product.setBoxCode(item.getBoxCode());
                product.setBoxSize(item.getBoxSize());
                product.setBoxWeight(item.getBoxWeight());
                product.setProductId(item.getProductId());
                product.setProductCode(item.getProductCode());
                product.setSpecsInfos(new ArrayList<>());
                productMap.put(productKey, product);
            }

            SalYtOrderExportDeliverySpecsVo specsVo = new SalYtOrderExportDeliverySpecsVo();
            specsVo.setSpecsImg(item.getSpecsImg());
            specsVo.setSpecsId(item.getSpecsId());
            specsVo.setSpecsName(item.getSpecsName());
            specsVo.setSpecsNum(item.getSpecsNum());
            product.getSpecsInfos().add(specsVo);
            product.setTotalNum((product.getTotalNum() == null ? 0 : product.getTotalNum()) + item.getSpecsNum());
        }
        return new ArrayList<>(productMap.values());
    }

    private String buildDeliveryExportProductKey(SalYtOrderExportDeliveryProductInfoVo productInfo) {
        String deliveryBoxId = productInfo.getDeliveryBoxId() == null ? "null" : productInfo.getDeliveryBoxId().toString();
        String productId = productInfo.getProductId() == null ? "null" : productInfo.getProductId().toString();
        String productCode = productInfo.getProductCode() == null ? "" : productInfo.getProductCode();
        return deliveryBoxId + "#" + productId + "#" + productCode;
    }

    //待发货填完物流点击发货后，给对应业务员和跟进人发消息
    public void sendMessage(StoYtDelivery params) {
        //根据发货id获取发货单信息
        StoYtDelivery delivery = stoYtDeliveryMapper.selectById(params.getId());
        if (delivery == null) {
            throw new BizException(ExceptionCodeEnum.Not_Exists.getCode(), "发货单不存在");
        }
        //物流单号
        String deliveryCode = "-";
        if (delivery.getCode() != null) {
            deliveryCode = delivery.getCode();
        }

        //根据发货单id获取订单信息
        List<SalYtOrder> orders = salYtOrderMapper.selectByDeliveryId(params.getId());

        //数据处理，将订单信息进行分组
        Map<Long, List<SalYtOrder>> saleOrderMap = new HashMap<>();
        Map<Long, List<SalYtOrder>> followOrderMap = new HashMap<>();
        if (!orders.isEmpty()) {
            for (SalYtOrder order : orders) {
                if (!saleOrderMap.containsKey(order.getSaleEmployeeId())) {
                    saleOrderMap.put(order.getSaleEmployeeId(), new ArrayList<>());
                }
                saleOrderMap.get(order.getSaleEmployeeId()).add(order);

                //跟进人可能为空
                if(order.getFollowEmployeeId()!=null) {
                    if (!followOrderMap.containsKey(order.getFollowEmployeeId())) {
                        followOrderMap.put(order.getFollowEmployeeId(), new ArrayList<>());
                    }
                    followOrderMap.get(order.getFollowEmployeeId()).add(order);
                }
            }
        }

        Integer tenantId = delivery.getTenantId();
        String appId = sysTenantConfigMapper.getByTenantIdAndConfigName(tenantId, TenantConfigEnum.FeiShuAppId.getKey()).getConfigValue();
        String appSecret = sysTenantConfigMapper.getByTenantIdAndConfigName(tenantId, TenantConfigEnum.FeiShuAppSecret.getKey()).getConfigValue();

        for (Map.Entry<Long, List<SalYtOrder>> entry : saleOrderMap.entrySet()) {
            Long saleEmployeeId = entry.getKey();
            List<SalYtOrder> ordersList = entry.getValue();

            String orderCodeLists = ordersList.stream().map(SalYtOrder::getCode).collect(Collectors.joining(","));
            String platformOrderCodeLists = ordersList.stream().map(SalYtOrder::getPlatformOrderCode).collect(Collectors.joining(","));
            //有两种情况，一种是国内物流，一种是国际物流
            String messageContent="";
            if(params.getPackageCode()!= null){
                //国内物流
                messageContent = "订单已发货" + "\n"
                        + "订单号：" + orderCodeLists + "\n"
                        + "平台订单号：" + platformOrderCodeLists + "\n"
                        + "发货单号：" +  deliveryCode+ "\n"
                        + "国内物流单号：" + params.getPackageCode();
            }else{
                //国际物流
                //国际物流公司信息
                StoYtTransportCompany stoYtTransportCompany=stoYtTransportCompanyMapper.selectById(params.getTransportCompanyId());
                messageContent = "订单已发货" + "\n"
                        + "订单号：" + orderCodeLists + "\n"
                        + "平台订单号：" + platformOrderCodeLists + "\n"
                        + "发货单号：" +  deliveryCode+ "\n"
                        + "国际物流：" + stoYtTransportCompany.getName();
            }


            //发送飞书消息
            //根据用户id获取用户open_id
            String openId = sysUserMapper.selectById(saleEmployeeId).getFeiShuUserId();
            try {
                FeiShuUtil.sendTextMessage(appId,appSecret,openId,messageContent);
            } catch (Exception e) {
                throw new BizException("发送消息失败：发送对象不属于当前飞书组织");
            }
        }

        for (Map.Entry<Long, List<SalYtOrder>> entry : followOrderMap.entrySet()) {
            Long followEmployeeId = entry.getKey();
            List<SalYtOrder> ordersList = entry.getValue();

            String orderCodeLists = ordersList.stream().map(SalYtOrder::getCode).collect(Collectors.joining(","));
            String platformOrderCodeLists = ordersList.stream().map(SalYtOrder::getPlatformOrderCode).collect(Collectors.joining(","));
            //有两种情况，一种是国内物流，一种是国际物流
            String messageContent="";
            if(params.getPackageCode()!= null){
                //国内物流
                messageContent = "订单已发货" + "\n"
                        + "订单号：" + orderCodeLists + "\n"
                        + "平台订单号：" + platformOrderCodeLists + "\n"
                        + "发货单号：" +  deliveryCode+ "\n"
                        + "国内物流单号：" + params.getPackageCode();
            }else {
                //国际物流
                //国际物流公司信息
                StoYtTransportCompany stoYtTransportCompany=stoYtTransportCompanyMapper.selectById(params.getTransportCompanyId());
                messageContent = "订单已发货" + "\n"
                        + "订单号：" + orderCodeLists + "\n"
                        + "平台订单号：" + platformOrderCodeLists + "\n"
                        + "发货单号：" +  deliveryCode+ "\n"
                        + "国际物流：" + stoYtTransportCompany.getName();
            }

            //发送飞书消息
            //根据用户id获取用户open_id
            String openId = sysUserMapper.selectById(followEmployeeId).getFeiShuUserId();
            try {
                FeiShuUtil.sendTextMessage(appId,appSecret,openId,messageContent);
            } catch (Exception e) {
                throw new BizException("发送消息失败：发送对象不属于当前飞书组织");
            }
        }
    }

    @Transactional
    public List<String> importOrders(MultipartFile file) throws IOException {
        List<String> errorList = new ArrayList<>();


//        InputStream inputStream = file.getInputStream();
//        ExcelReader reader = ExcelUtil.getReader(inputStream);
//        reader.addHeaderAlias("产品分类", "productFenlei");
//        reader.addHeaderAlias("产品编号", "productCode");
//        reader.addHeaderAlias("规格名称", "SpeName");
//        List<Map<String, Object>> dataList = reader.readAll();
//
//        //进行数据处理，用map存储， key为平台单号，value为List<***Vo>
//        //循环处理后的数据
//
//        //循环内*************************
//
//        //  sal_yt_order
//        SalYtOrder salYtOrder = new SalYtOrder();
//           //订单code
//           String code = EntityCodeGenerateUtil.generateUniqueId("D");
//           salYtOrder.setCode(code);
//
//           //平台  1=1688
//           salYtOrder.setSourcePlatform();
//
//           //平台单号 来自 excel
//            salYtOrder.setPlatformOrderCode();
//
//            //币种
//            salYtOrder.setCurrency();
//
//            //客户 来自 excel   需要判断该客户是否存在我们系统中，如果不存在，就将这条数据存入errorList集合中
//               //查询客户表中是否有该客户，有的话，就获取该客户id，没有的话，就将这条数据存入errorList集合中，并continue循环
//
//            //客户id  来自查询出来的客户表信息
//            salYtOrder.setReceiver();
//
//            //收货人 来自 excel
//            salYtOrder.setCustomerAddress();
//
//            //根据客户id和收货人查询出客户地址信息
//               //收货人联系方式  来自客户地址信息
//            salYtOrder.setReceiverPhone();
//               //客户地址id, 来自客户地址信息
//            salYtOrder.setCustomerId();
//
//            //前面查询出来的客户信息
//               //业务员id  来自查询出来的客户信息
//            salYtOrder.setSaleEmployeeId();
//              //业务员比例  来自excel
//            salYtOrder.setSaleRatio();
//              //跟进人id  来自查询出来的客户信息，这个跟进人id可能没有，注意判断是否为空
//            salYtOrder.setFollowEmployeeId();
//              //跟进人比例，需要根据业务员比例计算
//            salYtOrder.setFollowRatio();
//
//            //订单状态  都设置为这个状态
//            salYtOrder.setStatus(OrderStatusEnum.Passed.getKey());
//
//            //订单提交时间 ,来自 excel
//            salYtOrder.setSubmitOrderTime();
//
//            //发货形式,同一整单齐发
//            salYtOrder.setShippingMethod( ShippingMethodEnum.entireOrder.getKey());
//
//            //下单时间 ，来自 excel
//            salYtOrder.setOrderTime();
//
//            //交货时间，来自 excel
//            salYtOrder.setDeliveryTime();
//
//            //优惠金额，来自 excel
//            salYtOrder.setDiscountAmount();
//
//            //是否已收运费 1=已收运费，0=未收运费，本次都是已收运费
//            salYtOrder.setIsCollectedShippingCost(1);
//
//            //运费，来自 excel
//            salYtOrder.setShippingCost();
//
//            //订单完成时间，来自 excel
//            salYtOrder.setOrderFinishTime();
//
//            //回款状态，0=未回款，1=部分回款，2=全部回款
//            salYtOrder.setReceiveStatus(2);
//
//            //回款完成时间=订单完成时间（来自 excel）
//            salYtOrder.setReceiveFinishTime();
//
//            //汇率（按照运行时的汇率）去数据库查询
//             //查询转换率
//             List<SysDictionary> sysDictionaries = sysDictionaryMapper.selectByCode("exchangeRate");
//             //美元转人民币汇率
//              BigDecimal exchangeRate = new BigDecimal(sysDictionaries.get(0).getValue());
//             salYtOrder.setExchangeRate(exchangeRate);
//
//             //客户运费回款状态,2=已回款
//              salYtOrder.setShippingReceiveStatus(2);
//
//              //客户运费回款完成时间=订单完成时间（来自 excel）
//              salYtOrder.setShippingReceiveFinishTime();
//
//              //主订单赋值已经完成
//
//
//        //  sal_yt_order_sub
//        SalYtOrderSub salYtOrderSub = new SalYtOrderSub();
//          //子订单code
//          String subCode = EntityCodeGenerateUtil.generateUniqueId("D");
//          salYtOrderSub.setSubCode(subCode);
//
//          //父订单id,来与新增的父订单
//          salYtOrderSub.setOrderId(salYtOrder.getId());
//
//          //订单类型,都设置为成品单
//          salYtOrderSub.setOrderType("0");
//
//          //子订单赋值已经完成
//
//
//        //  sal_yt_order_sub_item（可能有多个产品，需要注意）
//        SalYtOrderSubItem salYtOrderSubItem = new SalYtOrderSubItem();
//          //子订单id,来与新增的子订单
//          salYtOrderSubItem.setOrderSubId(salYtOrderSub.getId());
//
//          //产品id
//          //根据产品ID（来自exsel）查询出产品信息
//          ProYtProduct proYtProduct = proYtProductMapper.selectByCode();
//          salYtOrderSubItem.setProductId(proYtProduct.getId());
//
//          //规格id
//          //根据规格名称（来自excel）和产品id查询规格信息
//          String name="";
//          ProYtProductSpecification proYtProductSpecification = proYtProductSpecificationMapper.selectByNameAndProductId(proYtProduct.getId(), name);
//          //需要判断规格名称是否为空，为空就要记录，后面手动录入
//          if(proYtProductSpecification==null){
//
//           }
//          salYtOrderSubItem.setSpecificationId(proYtProductSpecification.getId());
//
//          //销售单价(来自exsel)
//          salYtOrderSubItem.setPrice();
//
//          //销售单价本位币
//            //判断订单币种是否是美元，是美元就要销售单价乘以汇率，否则存的就是订单单价
//          salYtOrderSubItem.setBasePrice();
//
//          //数量，来自excel
//          salYtOrderSubItem.setNumber();
//
//          //状态(统一设置为已发货)
//          salYtOrderSubItem.setStatus(OrderSubItemStatusEnum.Delivered.getKey());
//
//          //供应商id
//            //根据供应商名称（来自excel）查询供应商信息
//          PurYtSupplier purYtSupplier = purYtSupplierMapper.selectByName();
//          salYtOrderSubItem .setSupplierId(purYtSupplier.getId());
//
//          //供应商价格，来自excel
//          salYtOrderSubItem.setSupplierPrice();
//
//          //占用库存、占用在途、占用在途入库数量（都设置为0）
//          salYtOrderSubItem.setOccupyStoreNumber(0);
//          salYtOrderSubItem.setOccupyTransitNumber(0);
//          salYtOrderSubItem.setOccupyTransitEnterNumber(0);
//
//          //总入库数量,来自excel里面的数量
//          salYtOrderSubItem.setEnterNumber();
//
//          //发货数量，来自excel里面的数量
//          salYtOrderSubItem.setDeliveryNumber();
//
//          //申购数量（设置为0）
//          salYtOrderSubItem.setApplyPurchaseNumber(0);
//
//          //子订单详情表赋值已经完成
//
//
//        //  sto_yt_delivery
//        StoYtDelivery stoYtDelivery = new StoYtDelivery();
//        //code
//        String deliveryCode = EntityCodeGenerateUtil.generateUniqueId("F");
//        stoYtDelivery.setCode(deliveryCode);
//
//        //客户id 来自查询出来的客户表信息
//        stoYtDelivery.setCustomerId();
//
//        //客户地址id 来自客户地址信息
//        stoYtDelivery.setAddressId();
//
//        //客户地址 来自客户地址信息,需要拼接地址信息
//        stoYtDelivery.setAddress();
//
//        //收货人，来自客户地址信息
//        stoYtDelivery.setConsignee();
//
//        //联系方式，来自客户地址信息
//        stoYtDelivery.setPhone();
//
//        //状态(统一设置为已发货)
//        stoYtDelivery.setStatus("2");
//
//        //打包完成时间
//        stoYtDelivery.setPackageTime();
//
//        //发货时间
//        stoYtDelivery.setDeliveryTime();
//
//        //国内物流单号
//        stoYtDelivery.setPackageCode();
//
//        //回款状态（设置为已完成）
//        stoYtDelivery.setReceiveStatus(2);
//
//        //发货单回款完成时间，来自excel
//        stoYtDelivery.setReceiveFinishTime();
//
//        //物流公司id
//          //根据物流公司名称（来自excel）查询物流公司信息
//          StoYtTransportCompany stoYtTransportCompany = stoYtTransportCompanyMapper.selectByName();
//        stoYtDelivery.setTransportCompanyId(stoYtTransportCompany.getId());
//
//        //发货单表赋值已经完成
//
//
//        // sto_yt_delivery_item(可能有多条，需要注意)
//        StoYtDeliveryItem stoYtDeliveryItem = new StoYtDeliveryItem();
//        //发货单id
//        stoYtDeliveryItem.setDeliveryId(stoYtDelivery.getId());
//
//        //子订单id
//        stoYtDeliveryItem.setOrderSubId();
//
//        //子订单详情id
//        stoYtDeliveryItem.setOrderSubItemId();
//
//        //产品id，上面赋值子订单表时有
//        stoYtDeliveryItem.setProductId();
//
//        //规格id，上面赋值子订单表时有
//        stoYtDeliveryItem.setSpecificationId();
//
//        //库位id（暂时还不明朗）
//        stoYtDeliveryItem.setLocationId();
//
//        //总数量，来自excel里面的数量
//        stoYtDeliveryItem.setNumber();
//
//        //已发货数量，来自excel里面的数量，因为是导入订单，所以每一条数据都是发货完成的
//        stoYtDeliveryItem.setShippedNumber();
//
//        //发货单详情表赋值已经完成
//
//
//
//
//        //循环内*************************


        return errorList;
    }
}
