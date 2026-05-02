/*
 * @author java_deng
 * @date 2025/12/18 15:30
 * @description 发货单管理类
 */
package com.qiaomoyun.manager.sto.yt;

import cn.hutool.core.lang.hash.Hash;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.qiaomoyun.Exception.BizException;
import com.qiaomoyun.Exception.ExceptionCodeEnum;
import com.qiaomoyun.entity.pro.yt.ProYtProductFile;
import com.qiaomoyun.entity.pro.yt.ProYtProductSpecificationItem;
import com.qiaomoyun.entity.sal.yt.*;
import com.qiaomoyun.entity.sto.yt.*;
import com.qiaomoyun.eunm.yt.DeliveryOrderStatusEnum;
import com.qiaomoyun.eunm.yt.ProductFilesTypeEnum;
import com.qiaomoyun.eunm.yt.ReceiveStatusEnum;
import com.qiaomoyun.eunm.yt.ShippingMethodEnum;
import com.qiaomoyun.eunm.yt.OrderSubItemStatusEnum;
import com.qiaomoyun.event.yt.DeliveryEvent;
import com.qiaomoyun.info.PageResultInfo;
import com.qiaomoyun.manager.feishu.FeiShuManager;
import com.qiaomoyun.manager.pro.yt.ProYtProductManager;
import com.qiaomoyun.mapper.pro.yt.ProYtProductFileMapper;
import com.qiaomoyun.mapper.sal.yt.*;
import com.qiaomoyun.mapper.sto.yt.*;
import com.qiaomoyun.param.fin.yt.FinYtPaymentQueryParams;
import com.qiaomoyun.param.fin.yt.FinYtPaymentUpdateParams;
import com.qiaomoyun.param.fin.yt.FinYtReceiveQueryParams;
import com.qiaomoyun.param.sto.yt.StoYtDeliveryCompleteParams;
import com.qiaomoyun.param.sto.yt.StoYtDeliveryQueryParams;
import com.qiaomoyun.util.EntityCodeGenerateUtil;
import com.qiaomoyun.util.FeiShuUtil;
import com.qiaomoyun.util.QMYExcelUtil;
import com.qiaomoyun.util.TenantInfoContext;
import com.qiaomoyun.vo.sto.yt.StoYtDeliveryVo;
import com.qiaomoyun.vo.sto.yt.StoYtDeliveryOrderVo;
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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

/**
 * 发货单管理类
 */
@Service
public class StoYtDeliveryManager {
    @Autowired
    private StoYtDeliveryMapper stoYtDeliveryMapper;
    @Autowired
    private StoYtDeliveryItemMapper stoYtDeliveryItemMapper;
    @Autowired
    private StoYtDeliveryBoxMapper stoYtDeliveryBoxMapper;
    @Autowired
    private StoYtDeliveryBoxItemMapper stoYtDeliveryBoxItemMapper;
    @Autowired
    private ProYtProductManager proYtProductManager;
    @Autowired
    private SalYtCustomerAddressMapper salYtCustomerAddressMapper;
    @Autowired
    private SalYtCustomerMapper salYtCustomerMapper;
    @Autowired
    private StoYtBoxMapper stoYtBoxMapper;
    @Autowired
    private StoYtTransportCompanyMapper stoYtTransportCompanyMapper;
    @Autowired
    private StoYtDeliveryReceiveMapper stoYtDeliveryReceiveMapper;
    @Autowired
    private ProYtProductFileMapper proYtProductFileMapper;
    @Autowired
    private SalYtOrderSubMapper salYtOrderSubMapper;
    @Autowired
    private SalYtOrderMapper salYtOrderMapper;
    @Autowired
    private SalYtOrderSubItemMapper salYtOrderSubItemMapper;
    @Autowired
    private FeiShuManager feiShuManager;
    @Autowired
    private SalYtCustomerStoreMapper salYtCustomerStoreMapper;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * 查询发货单列表
     *
     * @param params 查询参数
     * @return 发货单列表
     */
    public Object list(StoYtDeliveryQueryParams params) {
        return stoYtDeliveryMapper.list(params);
    }

    /**
     * 分页查询发货单列表
     *
     * @param params 查询参数
     * @return 分页后的发货单列表
     */
    public PageResultInfo<StoYtDeliveryVo> listByPage(StoYtDeliveryQueryParams params) {
        syncOrderDeliveryWhenSearch(params);
        PageHelper.startPage(params.getPageNum(), params.getPageSize());
        List<StoYtDeliveryVo> list = stoYtDeliveryMapper.list(params);
        if (shouldRetryWaitPackageSearch(params, list)) {
            syncOrderDeliveryWhenSearch(params);
            list = stoYtDeliveryMapper.list(params);
            fillDeliveryPageData(list, params);
            return buildManualPageResult(list, params);
        }
        fillDeliveryPageData(list, params);
        return new PageResultInfo<>(list);
    }

    private boolean shouldRetryWaitPackageSearch(StoYtDeliveryQueryParams params, List<StoYtDeliveryVo> list) {
        if (params == null) {
            return false;
        }
        if (!DeliveryOrderStatusEnum.waitPackage.getKey().toString().equals(params.getStatus())) {
            return false;
        }
        return params.getOrderCode() != null && !params.getOrderCode().trim().isEmpty() && list != null && list.isEmpty();
    }

    private void fillDeliveryPageData(List<StoYtDeliveryVo> list, StoYtDeliveryQueryParams params) {
        for (StoYtDeliveryVo vo : list) {
            Long id = vo.getId();
            StoYtDeliveryQueryParams itemParams = new StoYtDeliveryQueryParams();
            itemParams.setId(id);
            itemParams.setProductComplete(params.getProductComplete());
            itemParams.setPackageComplete(params.getPackageComplete());
            List<StoYtDeliveryItem> itemList = stoYtDeliveryItemMapper.listGroupItem(itemParams);
            for (StoYtDeliveryItem item : itemList) {
                if(item!=null){
                    Boolean productComplete = checkDeliveryProductStatus(item.getOrderId());
                    item.setProductComplete(productComplete);
                    Boolean packageComplete = getOrderPackageComplete(id, item.getOrderId());
                    item.setPackageComplete(packageComplete);
                }
            }
            vo.setItemList(itemList);

            Long addressId = vo.getAddressId();
            SalYtCustomerAddress salYtCustomerAddress = salYtCustomerAddressMapper.selectById(addressId);

            if(salYtCustomerAddress != null){
                if (salYtCustomerAddress.getCountryRegionId().equals(1003285L)) {
                    vo.setIsChina(Boolean.TRUE);
                } else {
                    vo.setIsChina(Boolean.FALSE);
                }
            }


            Long transportCompanyId = vo.getTransportCompanyId();
            if (transportCompanyId != null) {
                StoYtTransportCompany stoYtTransportCompany = stoYtTransportCompanyMapper.selectById(transportCompanyId);
                vo.setStoYtTransportCompany(stoYtTransportCompany);
            }
        }
    }

    private PageResultInfo<StoYtDeliveryVo> buildManualPageResult(List<StoYtDeliveryVo> list, StoYtDeliveryQueryParams params) {
        PageResultInfo<StoYtDeliveryVo> pageResultInfo = new PageResultInfo<>();
        int pageNum = params.getPageNum() == null || params.getPageNum() <= 0 ? 1 : params.getPageNum();
        int pageSize = params.getPageSize() == null || params.getPageSize() <= 0 ? 15 : params.getPageSize();
        int total = list == null ? 0 : list.size();
        int fromIndex = Math.min((pageNum - 1) * pageSize, total);
        int toIndex = Math.min(fromIndex + pageSize, total);
        pageResultInfo.setList(list == null ? Collections.emptyList() : list.subList(fromIndex, toIndex));
        pageResultInfo.setTotal((long) total);
        pageResultInfo.setPageNum(pageNum);
        pageResultInfo.setPageSize(pageSize);
        pageResultInfo.setPages(total == 0 ? 0 : (int) Math.ceil((double) total / pageSize));
        return pageResultInfo;
    }

    private void syncOrderDeliveryWhenSearch(StoYtDeliveryQueryParams params) {
        if (params == null || params.getOrderCode() == null || params.getOrderCode().trim().isEmpty()) {
            return;
        }
        if (DeliveryOrderStatusEnum.delivered.getKey().toString().equals(params.getStatus())) {
            return;
        }
        LambdaQueryWrapper<SalYtOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SalYtOrder::getIsDeleted, 0);
        queryWrapper.like(SalYtOrder::getCode, params.getOrderCode().trim());
        List<SalYtOrder> orderList = salYtOrderMapper.selectList(queryWrapper);
//        for (SalYtOrder order : orderList) {
//            applicationEventPublisher.publishEvent(new DeliveryEvent(this, order.getId()));
//        }
    }

    /**
     * 根据详情参数获取发货单详情，支持按产品或子订单分组
     *
     * @param params 发货单详情查询参数
     * @return 发货单详情
     */
    public Object getById(StoYtDeliveryQueryParams params) {
        // 1. 查询发货单主信息
        StoYtDelivery delivery = stoYtDeliveryMapper.selectById(params.getId());
        if (delivery == null) {
            throw new BizException(ExceptionCodeEnum.Not_Exists);
        }
        if (DeliveryOrderStatusEnum.waitPackage.getKey().toString().equals(delivery.getStatus())) {
            List<StoYtDeliveryOrderVo> orderList = stoYtDeliveryItemMapper.listGroupOrderByDeliveryId(delivery.getId());
//            for (StoYtDeliveryOrderVo orderVo : orderList) {
//                if (orderVo.getOrderId() != null) {
//                    applicationEventPublisher.publishEvent(new DeliveryEvent(this, orderVo.getOrderId()));
//                }
//            }
            delivery = stoYtDeliveryMapper.selectById(params.getId());
            if (delivery == null) {
                throw new BizException(ExceptionCodeEnum.Not_Exists);
            }
        }
        StoYtDeliveryVo stoYtDeliveryVo = new StoYtDeliveryVo();
        BeanUtils.copyProperties(delivery, stoYtDeliveryVo);
        Long customerId = delivery.getCustomerId();
        // 2. 查询发货单条目
        List<StoYtDeliveryItem> itemList = stoYtDeliveryItemMapper.getDeliveryItemsByProduct(params);
        for (StoYtDeliveryItem item : itemList) {
            Long specificationId = item.getSpecificationId();
            List<ProYtProductFile> fileListBySpecification = proYtProductManager.getFileListBySpecification(specificationId);
            item.setImageList(fileListBySpecification);
            List<ProYtProductSpecificationItem> itemsListBySpecification = proYtProductManager.getItemsListBySpecification(specificationId);
            item.setItemList(itemsListBySpecification);

            Boolean isCustomerStore=false;
            if(customerId!=null){
                SalYtCustomerStore salYtCustomerStore = salYtCustomerStoreMapper.selectByCustomerIdAndSpecificationId(customerId, specificationId);
                if(salYtCustomerStore!=null){
                    isCustomerStore=true;
                }
            }
            item.setIsCustomerStore(isCustomerStore);
        }
        // 3. 设置条目列表
        stoYtDeliveryVo.setItemList(itemList);

        //4.设置订单分类时需要的数据
        if (params.getOrderId() != null) {
            Long orderId = params.getOrderId();
            SalYtOrder order = salYtOrderMapper.selectById(orderId);
            stoYtDeliveryVo.setOrderRemark(order.getRemark());
            stoYtDeliveryVo.setOrderShippingMethod(order.getShippingMethod());
            //设置产品状态和打包状态
            Boolean productComplete = checkDeliveryProductStatus(orderId);
            stoYtDeliveryVo.setOrderProductComplete(productComplete);
            // 设置打包状态
            boolean packageComplete = getOrderPackageComplete(params.getId(), orderId);
            stoYtDeliveryVo.setOrderPackageComplete(packageComplete);

        }

        //设置打印需要的参数
        Long addressId = delivery.getAddressId();
        SalYtCustomerAddress salYtCustomerAddress = salYtCustomerAddressMapper.selectById(addressId);
        SalYtCustomer customer = salYtCustomerMapper.selectById(customerId);
        HashMap<String, Object> groupOrder = stoYtDeliveryMapper.selectMapByGroupOrder(params.getId());
        Object orderCodeObject = groupOrder != null ? groupOrder.get("orderCode") : "";
        List<String> sales = stoYtDeliveryMapper.selectOrderSubSales(params.getId());
        //收货人
        String consignee = salYtCustomerAddress.getConsignee();
        //客户姓名
        String customerName = customer.getName();
        //订单号
        String orderCode = orderCodeObject.toString();
        //业务员姓名
        String salesEmployeeName = String.join(",", sales);

        stoYtDeliveryVo.setConsignee(consignee);
        stoYtDeliveryVo.setCustomerName(customerName);
        stoYtDeliveryVo.setOrderCode(orderCode);
        stoYtDeliveryVo.setSalesEmployeeName(salesEmployeeName);
        return stoYtDeliveryVo;
    }

    /**
     * 保存发货单
     *
     * @param delivery 发货单信息
     */
    @Transactional
    public void save(StoYtDelivery delivery) {
        if (delivery.getId() == null) {
            // 新增
            delivery.setCode(EntityCodeGenerateUtil.generateUniqueId("F"));
            stoYtDeliveryMapper.insert(delivery);
        } else {
            // 更新
            stoYtDeliveryMapper.updateById(delivery);
        }
    }

    /**
     * 删除发货单
     *
     * @param id 发货单ID
     */
    public void deleteById(Long id) {
        stoYtDeliveryMapper.deleteById(id);
    }

    public void export(StoYtDeliveryQueryParams params, HttpServletResponse response) throws IOException {
        String code = TenantInfoContext.getCurrentTenant().getCode();
        String templatePath = "excel-template/" + code + "-deliveryExport.xlsx";
        ClassPathResource resource = new ClassPathResource(templatePath);
        Workbook workbook = new XSSFWorkbook(resource.getInputStream());
        Long id = params.getId();

        StoYtDelivery delivery = stoYtDeliveryMapper.selectById(id);
        if (delivery == null) {
            throw new BizException(ExceptionCodeEnum.Not_Exists);
        }
        Long addressId = delivery.getAddressId();
        SalYtCustomerAddress salYtCustomerAddress = salYtCustomerAddressMapper.selectById(addressId);
        Long customerId = delivery.getCustomerId();
        SalYtCustomer customer = salYtCustomerMapper.selectById(customerId);
        HashMap<String, Object> groupOrderSub = stoYtDeliveryMapper.selectMapByGroupOrderSub(id);
        Object orderSubCodeObject = groupOrderSub.get("orderSubCode");
        List<String> sales = stoYtDeliveryMapper.selectOrderSubSales(id);
        //收货人
        String consignee = salYtCustomerAddress.getConsignee();
        //客户姓名
        String customerName = customer.getName();
        //订单号
        String orderSubCode = orderSubCodeObject.toString();
        //业务员姓名
        String salesEmployeeName = String.join(",", sales);

        //替换填充字符
        Sheet sheet = workbook.getSheetAt(0);
        for (org.apache.poi.ss.usermodel.Row row : sheet) {
            if (row != null) {
                // 遍历所有单元格
                for (org.apache.poi.ss.usermodel.Cell cell : row) {
                    if (cell != null && cell.getCellType() == org.apache.poi.ss.usermodel.CellType.STRING) {
                        QMYExcelUtil.replaceCellValue("{receiveName}", consignee, cell);
                        QMYExcelUtil.replaceCellValue("{customerName}", customerName, cell);
                        QMYExcelUtil.replaceCellValue("{salesEmployeeName}", salesEmployeeName, cell);
                        QMYExcelUtil.replaceCellValue("{orderSubCode}", orderSubCode, cell);
                    }
                }
            }
        }
        StoYtDeliveryQueryParams stoYtDeliveryQueryParams = new StoYtDeliveryQueryParams();
        stoYtDeliveryQueryParams.setId(delivery.getId());
        List<StoYtDeliveryItem> stoYtDeliveryItemList = stoYtDeliveryItemMapper.getDeliveryItemsByProduct(stoYtDeliveryQueryParams);
        HashMap<Long, List<StoYtDeliveryItem>> itemMap = new HashMap<>();
        for (StoYtDeliveryItem item : stoYtDeliveryItemList) {
            Long productId = item.getProductId();
            List<StoYtDeliveryItem> itemMapList = itemMap.get(productId);
            if (itemMapList == null) {
                itemMapList = new ArrayList<>();
            }
            itemMapList.add(item);
            itemMap.put(productId, itemMapList);
        }
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        Integer startRow = 2;
        for (Map.Entry<Long, List<StoYtDeliveryItem>> entry : itemMap.entrySet()) {
            Long productId = entry.getKey();
            List<StoYtDeliveryItem> exportList = entry.getValue();
            Integer rowIndex = startRow;
            for (StoYtDeliveryItem item : exportList) {
                Row row = sheet.createRow(rowIndex);
                //产品code
                Cell cell0 = row.createCell(0);
                cell0.setCellValue(item.getProductCode());

                //规格名称
                Cell cell1 = row.createCell(1);
                Long specificationId = item.getSpecificationId();
                List<ProYtProductSpecificationItem> itemList = proYtProductManager.getItemsListBySpecification(specificationId);
                String speci = String.join("\n", itemList.stream().map(ProYtProductSpecificationItem::getCategorySpecificationItemValue).toList());
                cell1.setCellValue(speci);

                //库位
                Cell cell2 = row.createCell(2);
                cell2.setCellValue(item.getLocationName());

                //本次发货数量
                Cell cell3 = row.createCell(3);
                cell3.setCellValue(item.getNumber());

                //定制化属性
                Cell cell4 = row.createCell(4);
                cell4.setCellValue(item.getCategorySpecificationItemName());

                String qrContent = specificationId.toString();
                // 二维码配置（尺寸、边距）
                QrConfig qrConfig = new QrConfig(200, 200);
                // 纠错级别
                //                qrConfig.setErrorCorrection(com.google.zxing.client.j2se.MatrixToImageConfig.DEFAULT_ERROR_CORRECTION);
                // 生成二维码图片
                BufferedImage qrImage = QrCodeUtil.generate(qrContent, qrConfig);
                Drawing<?> drawing = sheet.createDrawingPatriarch();
                // 设置图片位置（第rowIndex行，第5列）
                ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 5, rowIndex, 6, rowIndex + 1);
                // 图片随单元格移动和调整大小
                anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);

                // 将 BufferedImage 转换为字节数组
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(qrImage, "PNG", baos);
                byte[] imageBytes = baos.toByteArray();

                // 插入图片到 Excel
                int pictureIdx = workbook.addPicture(imageBytes, Workbook.PICTURE_TYPE_PNG);
                drawing.createPicture(anchor, pictureIdx);

                row.setHeight((short) 1000);

                rowIndex++;
            }
            if (startRow != rowIndex - 1) {
                CellRangeAddress mergedRegion = new CellRangeAddress(startRow, rowIndex - 1, 0, 0);
                sheet.addMergedRegion(mergedRegion);
            }

            startRow = rowIndex;
        }


        // 6. 设置响应头，输出Excel文件
        String fileName = "发货导出_" + System.currentTimeMillis() + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + java.net.URLEncoder.encode(fileName, "UTF-8"));

        // 获取输出流
        ServletOutputStream outputStream = response.getOutputStream();

        // 输出到响应流
        workbook.write(outputStream);
        outputStream.flush();


    }

    /**
     * 扫码查询
     *
     * @param params 查询参数，包含发货单id和规格id
     * @return 查询结果
     */
    public Object scan(StoYtDeliveryQueryParams params) {
        // 1. 验证参数
        if (params.getId() == null || params.getSpecificationId() == null) {
            throw new BizException(ExceptionCodeEnum.Param_Exception);
        }

        // 2. 查询发货单信息
        StoYtDelivery delivery = stoYtDeliveryMapper.selectById(params.getId());
        if (delivery == null) {
            throw new BizException(ExceptionCodeEnum.Not_Exists);
        }

        // 3. 查询发货单条目，根据发货单id和规格id
        List<StoYtDeliveryItem> itemList = stoYtDeliveryItemMapper.getDeliveryItemsByProduct(params);

        // 4. 组装数据，添加规格详情和图片列表
        for (StoYtDeliveryItem item : itemList) {
            Long specificationId = item.getSpecificationId();
            // 添加图片列表
            List<ProYtProductFile> fileListBySpecification = proYtProductManager.getFileListBySpecification(specificationId);
            item.setImageList(fileListBySpecification);
            // 添加规格详情列表
            List<ProYtProductSpecificationItem> itemsListBySpecification = proYtProductManager.getItemsListBySpecification(specificationId);
            item.setItemList(itemsListBySpecification);
        }

        return itemList;
    }

    /**
     * 检查发货产品状态
     */
    public Boolean checkDeliveryProductStatus(Long orderId) {
        SalYtOrder salYtOrder = salYtOrderMapper.selectById(orderId);
        Long customerId = salYtOrder.getCustomerId();
        // 获取发货方式
        String shippingMethod = salYtOrder.getShippingMethod();
        List<SalYtOrderSub> salYtOrderSubs = salYtOrderSubMapper.selectSalYtOrderSubByOrderId(orderId);
        boolean canDeliver = true;
        for (SalYtOrderSub salYtOrderSub : salYtOrderSubs) {
            // 检查是否是半成品单，且子订单是否已经都确认完毕
//        if (salYtOrderSub.getOrderType().equals("1")) {
//            boolean isCompleted = validOrderSubCompleted(salYtOrderSub);
//            if (!isCompleted) {
//                item.setProductComplete(false);
//                return;
//            }
//        }
            Long orderSubId = salYtOrderSub.getId();
            List<SalYtOrderSubItem> salYtOrderSubItems = salYtOrderSubItemMapper.selectOrderSubCompletedItemByOrderSubId(orderSubId);
            for (SalYtOrderSubItem item : salYtOrderSubItems) {
                Long productId = item.getProductId();
                Long specificationId = item.getSpecificationId();

                // 检查是否符合发货条件
                if (ShippingMethodEnum.entireOrder.getKey().equals(shippingMethod)) {
                    // 整单齐发：查询这个子订单的所有规格不为空的item的下单数量是否大于了入库数量
                    List<SalYtOrderSubItem> allItems = salYtOrderSubItemMapper.selectSalYtOrderSubItemByOrderSubId(orderSubId);
                    canDeliver = allItems != null && !allItems.isEmpty() &&
                            allItems.stream().filter(subItem -> subItem.getSpecificationId() != null).allMatch(subItem -> {
                                Integer number = subItem.getNumber();
                                Integer enterNumber = subItem.getEnterNumber() != null ? subItem.getEnterNumber() : 0;
                                return number <= enterNumber;
                            });
                } else if (ShippingMethodEnum.entireProduct.getKey().equals(shippingMethod)) {
                    // 单款齐发：查询这个子订单该item的相同product，但是规格不为空的item的下单数量是否大于了入库数量
                    Map<String, Object> params = new HashMap<>();
                    params.put("orderSubId", orderSubId);
                    params.put("productId", productId);
                    List<SalYtOrderSubItem> sameProductItems = salYtOrderSubItemMapper.selectByOrderSubIdAndProductIdAndSpecificationNotNull(params);
                    canDeliver = sameProductItems != null && !sameProductItems.isEmpty() &&
                            sameProductItems.stream().allMatch(subItem -> {
                                Integer number = subItem.getNumber();
                                Integer enterNumber = subItem.getEnterNumber() != null ? subItem.getEnterNumber() : 0;
                                return number <= enterNumber;
                            });
                } else if (ShippingMethodEnum.entireSpecification.getKey().equals(shippingMethod)) {
                    // 单规格齐发：查询这个子订单该item的相同规格，但是规格不为空的item的下单数量是否大于了入库数量
                    Map<String, Object> params = new HashMap<>();
                    params.put("orderSubId", orderSubId);
                    params.put("specificationId", specificationId);
                    List<SalYtOrderSubItem> sameSpecificationItems = salYtOrderSubItemMapper.selectByOrderSubIdAndSpecificationId(params);
                    canDeliver = sameSpecificationItems != null && !sameSpecificationItems.isEmpty() &&
                            sameSpecificationItems.stream().allMatch(subItem -> {
                                Integer number = subItem.getNumber();
                                Integer enterNumber = subItem.getEnterNumber() != null ? subItem.getEnterNumber() : 0;
                                return number <= enterNumber;
                            });
                } else if (ShippingMethodEnum.hasStock.getKey().equals(shippingMethod)) {
                    // 有货就发：同样要求所有规格入库量 >= 需求量，才算产品齐全
                    List<SalYtOrderSubItem> allItems = salYtOrderSubItemMapper.selectSalYtOrderSubItemByOrderSubId(orderSubId);
                    canDeliver = allItems != null && !allItems.isEmpty() &&
                            allItems.stream().filter(subItem -> subItem.getSpecificationId() != null).allMatch(subItem -> {
                                Integer number = subItem.getNumber();
                                Integer enterNumber = subItem.getEnterNumber() != null ? subItem.getEnterNumber() : 0;
                                return number <= enterNumber;
                            });
                }

                // 设置产品状态
                if (!canDeliver) {
                    return canDeliver;
                }
            }

        }
        return canDeliver;
    }

    /**
     * 获取订单的打包状态
     *
     * @param deliveryId 发货单ID
     * @param orderId    订单ID
     * @return 打包状态（true: 已打包完成, false: 未打包完成）
     */
    public boolean getOrderPackageComplete(Long deliveryId, Long orderId) {
        if (deliveryId == null || orderId == null) {
            return false;
        }

        // 计算入库数量-发货数量
        int enterMinusDelivery = 0;
        List<SalYtOrderSub> orderSubList = salYtOrderSubMapper.selectSalYtOrderSubByOrderId(orderId);
        for (SalYtOrderSub orderSub : orderSubList) {
            List<SalYtOrderSubItem> itemList = salYtOrderSubItemMapper.selectSalYtOrderSubItemByOrderSubId(orderSub.getId());
            for (SalYtOrderSubItem item : itemList) {
                if (item.getSpecificationId() != null) {
                    int enterNumber = item.getEnterNumber() != null ? item.getEnterNumber() : 0;
                    int deliveryNumber = item.getDeliveryNumber() != null ? item.getDeliveryNumber() : 0;
                    enterMinusDelivery += (enterNumber - deliveryNumber);
                }
            }
        }

        // 计算打包数量
        int packageNumber = 0;
        Map<String, Long> params = new HashMap<>();
        params.put("deliveryId", deliveryId);
        params.put("orderId", orderId);
        List<StoYtDeliveryBoxItem> boxItems = stoYtDeliveryBoxItemMapper.selectByDeliveryIdAndOrderId(params);
        for (StoYtDeliveryBoxItem boxItem : boxItems) {
            packageNumber += boxItem.getNumber();
        }

        // 打包状态：如果入库数量-发货数量大于打包数量则为false，否则为true
        return enterMinusDelivery <= packageNumber;
    }

    /**
     * 验证子订单是否已完成（用于半成品单）
     *
     * @param salYtOrderSub 子订单
     * @return 是否完成
     */
    private Boolean validOrderSubCompleted(SalYtOrderSub salYtOrderSub) {
        Boolean result = true;
        Long id = salYtOrderSub.getId();
        List<SalYtOrderSubItem> salYtOrderSubItems = salYtOrderSubItemMapper.selectSalYtOrderSubItemByOrderSubId(id);
        for (SalYtOrderSubItem item : salYtOrderSubItems) {
            Long specificationId = item.getSpecificationId();
            if (specificationId == null) {
                // 说明是半成品需要判断是否确认
                Long itemId = item.getId();
                // 遍历查找confirmItemId等于当前itemId的item
                int totalConfirmNumber = 0;
                for (SalYtOrderSubItem subItem : salYtOrderSubItems) {
                    if (itemId.equals(subItem.getConfirmItemId())) {
                        totalConfirmNumber += subItem.getNumber();
                    }
                }
                // 判断确认数量总和是否和当前item的number相等
                if (item.getNumber() > totalConfirmNumber) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * 打包操作
     *
     * @param params 发货箱列表，包含每个箱子的物品信息
     */
    @Transactional
    public void takePackage(List<StoYtDeliveryBox> params) {
        // 1. 验证参数
        if (params == null || params.isEmpty()) {
            throw new BizException(ExceptionCodeEnum.Param_Exception);
        }
        Long deliveryId = params.get(0).getDeliveryId();
        // 2. 遍历发货箱列表
        for (StoYtDeliveryBox box : params) {
            // 2.1 验证单个发货箱参数
            if (box.getDeliveryId() == null) {
                throw new BizException(ExceptionCodeEnum.Param_Exception);
            }

            // 2.2 保存或修改发货箱信息
            if (box.getId() != null) {
                // 更新现有发货箱
                stoYtDeliveryBoxMapper.updateById(box);
            } else {
                // 插入新发货箱
                stoYtDeliveryBoxMapper.insert(box);
            }

            // 2.3 保存或修改发货箱物品信息
            List<StoYtDeliveryBoxItem> boxItemList = box.getBoxItemList();
            if (boxItemList != null && !boxItemList.isEmpty()) {
                for (StoYtDeliveryBoxItem item : boxItemList) {
                    // 设置关联的发货箱ID
                    item.setDeliveryBoxId(box.getId());
                    // 保存或修改发货箱物品
                    if (item.getId() != null) {
                        // 更新现有物品
                        stoYtDeliveryBoxItemMapper.updateById(item);
                    } else {
                        // 插入新物品
                        stoYtDeliveryBoxItemMapper.insert(item);
                    }
                }
            }
        }
        StoYtDelivery delivery = new StoYtDelivery();
        delivery.setId(deliveryId);
        delivery.setStatus(DeliveryOrderStatusEnum.waitDelivery.getKey().toString());
        delivery.setPackageTime(new Date());
        stoYtDeliveryMapper.updateById(delivery);
    }

    @Transactional
    public void savePackage(List<StoYtDeliveryBox> params) {
        // 1. 验证参数
        if (params == null || params.isEmpty()) {
            throw new BizException(ExceptionCodeEnum.Param_Exception);
        }
        Long deliveryId = params.get(0).getDeliveryId();
        if (deliveryId == null) {
            throw new BizException(ExceptionCodeEnum.Param_Exception);
        }
//        List<StoYtDeliveryBox> stoYtDeliveryBoxes = stoYtDeliveryBoxMapper.selectByDeliveryId(deliveryId);
//        for (StoYtDeliveryBox deliveryBox : stoYtDeliveryBoxes) {
//            stoYtDeliveryBoxItemMapper.deleteByDeliveryBoxId(deliveryBox.getId());
//        }
        stoYtDeliveryBoxItemMapper.deleteByDeliveryId(deliveryId);
        stoYtDeliveryBoxMapper.deleteByDeliveryId(deliveryId);

        // 查询发货单item表，根据规格id为key存在map中，value为List<StoYtDeliveryItem>
        List<StoYtDeliveryItem> deliveryItems = stoYtDeliveryItemMapper.listByDeliveryId(deliveryId);
        // 创建规格id到StoYtDeliveryItem列表的映射
        Map<Long, List<StoYtDeliveryItem>> specificationItemMap = new HashMap<>();
        for (StoYtDeliveryItem deliveryItem : deliveryItems) {
            Long specificationId = deliveryItem.getSpecificationId();
            if (specificationId != null) {
                specificationItemMap.computeIfAbsent(specificationId, k -> new ArrayList<>()).add(deliveryItem);
            }
        }
        // 订单号 → 订单ID 缓存，避免重复查询
        Map<String, Long> orderCodeToIdMap = new HashMap<>();

        // 2. 遍历发货箱列表
        for (StoYtDeliveryBox box : params) {
            // 2.1 验证单个发货箱参数
            if (box.getDeliveryId() == null) {
                throw new BizException(ExceptionCodeEnum.Param_Exception);
            }

            //确保是新增
            box.setId(null);
            stoYtDeliveryBoxMapper.insert(box);

            // 2.3 保存或修改发货箱物品信息
            List<StoYtDeliveryBoxItem> boxItemList = box.getBoxItemList();
            if (boxItemList != null && !boxItemList.isEmpty()) {
                for (StoYtDeliveryBoxItem item : boxItemList) {
                    // 根据item的规格id,从发货单itemMap中找到这个规格的StoYtDeliveryItem,然后遍历判断数量是否还够打包
                    Long specificationId = item.getSpecificationId();

                    List<StoYtDeliveryItem> sameSpecItems = specificationItemMap.get(specificationId);
                    if (sameSpecItems == null || sameSpecItems.isEmpty()) {
                        throw new BizException(ExceptionCodeEnum.Param_Exception);
                    }
                    List<StoYtDeliveryItem> candidateItems = new ArrayList<>(sameSpecItems);
                    // 若前端传入了 orderNo，按订单过滤 delivery item，确保只消耗该订单的库存
                    String orderNo = item.getOrderNo();
                    if (orderNo != null && !orderNo.isEmpty()) {
                        Long orderId = orderCodeToIdMap.computeIfAbsent(orderNo, code -> {
                            SalYtOrder order = salYtOrderMapper.selectOne(
                                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<SalYtOrder>()
                                    .eq("code", code).eq("is_deleted", 0));
                            return order != null ? order.getId() : null;
                        });
                        if (orderId != null) {
                            final Long finalOrderId = orderId;
                            List<StoYtDeliveryItem> filtered = candidateItems.stream()
                                .filter(di -> finalOrderId.equals(di.getOrderId()))
                                .collect(java.util.stream.Collectors.toList());
                            if (!filtered.isEmpty()) {
                                candidateItems = filtered;
                            }
                        }
                    }
                    // 同订单下相同规格可能被拆成多行，这里优先按定制属性和库位精确匹配，
                    // 避免 orderItemId 关联到错误的发货条目，导致完成打包后原单残留。
                    candidateItems = sortDeliveryItemsByMatchPriority(candidateItems, item);
                    Integer remainingQuantity = item.getNumber();
                    for (StoYtDeliveryItem deliveryItem : candidateItems) {
                        if (remainingQuantity <= 0) {
                            break;
                        }
                        Integer availableQuantity = deliveryItem.getNumber();
                        if (availableQuantity > 0) {
                            if (availableQuantity >= remainingQuantity) {
                                // 当前item数量足够
                                StoYtDeliveryBoxItem newItem = new StoYtDeliveryBoxItem();
                                BeanUtils.copyProperties(item, newItem);
                                newItem.setDeliveryBoxId(box.getId());
                                newItem.setId(null);
                                newItem.setNumber(remainingQuantity);
                                newItem.setOrderItemId(deliveryItem.getOrderSubItemId());
                                newItem.setLocationId(deliveryItem.getLocationId());
                                stoYtDeliveryBoxItemMapper.insert(newItem);

                                // 更新发货单item数量
                                deliveryItem.setNumber(deliveryItem.getNumber() - remainingQuantity);
                                remainingQuantity = 0;
                                break;
                            } else {
                                // 当前item数量不足，需要拆分
                                StoYtDeliveryBoxItem newItem = new StoYtDeliveryBoxItem();
                                BeanUtils.copyProperties(item, newItem);
                                newItem.setDeliveryBoxId(box.getId());
                                newItem.setId(null);
                                newItem.setNumber(availableQuantity);
                                newItem.setLocationId(deliveryItem.getLocationId());
                                newItem.setOrderItemId(deliveryItem.getOrderSubItemId());
                                stoYtDeliveryBoxItemMapper.insert(newItem);

                                // 更新发货单item数量为0（已用完）
                                deliveryItem.setNumber(0);
                                remainingQuantity -= availableQuantity;
                            }
                        }
                    }
                    if (remainingQuantity > 0) {
                        throw new BizException("打包数据与发货单条目不匹配，请刷新后重试");
                    }

                }
            }
        }
        // 不修改发货单状态，只保存包裹信息
    }

    private boolean isSameCategorySpecification(Long left, Long right) {
        return Objects.equals(normalizeNullableId(left), normalizeNullableId(right));
    }

    private boolean isSameLocation(Long left, Long right) {
        return Objects.equals(normalizeNullableId(left), normalizeNullableId(right));
    }

    private List<StoYtDeliveryItem> sortDeliveryItemsByMatchPriority(List<StoYtDeliveryItem> deliveryItems, StoYtDeliveryBoxItem boxItem) {
        if (deliveryItems == null || deliveryItems.isEmpty()) {
            return Collections.emptyList();
        }
        return deliveryItems.stream()
            .sorted(Comparator
                .comparingInt((StoYtDeliveryItem item) -> getMatchPriority(item, boxItem))
                .thenComparing(StoYtDeliveryItem::getId, Comparator.nullsLast(Long::compareTo)))
            .collect(Collectors.toList());
    }

    private List<StoYtDeliveryItem> sortDeliveryItemsByMatchPriority(List<StoYtDeliveryItem> deliveryItems, StoYtDeliveryItem targetItem) {
        if (deliveryItems == null || deliveryItems.isEmpty()) {
            return Collections.emptyList();
        }
        return deliveryItems.stream()
            .sorted(Comparator
                .comparingInt((StoYtDeliveryItem item) -> getMatchPriority(item, targetItem))
                .thenComparing(StoYtDeliveryItem::getId, Comparator.nullsLast(Long::compareTo)))
            .collect(Collectors.toList());
    }

    private int getMatchPriority(StoYtDeliveryItem deliveryItem, StoYtDeliveryBoxItem boxItem) {
        boolean sameCategory = isSameCategorySpecification(deliveryItem.getCategorySpecificationItemId(), boxItem.getCategorySpecificationItemId());
        boolean sameLocation = isSameLocation(deliveryItem.getLocationId(), boxItem.getLocationId());
        if (sameCategory && sameLocation) {
            return 0;
        }
        if (sameCategory) {
            return 1;
        }
        if (sameLocation) {
            return 2;
        }
        return 3;
    }

    private int getMatchPriority(StoYtDeliveryItem deliveryItem, StoYtDeliveryItem targetItem) {
        boolean sameCategory = isSameCategorySpecification(deliveryItem.getCategorySpecificationItemId(), targetItem.getCategorySpecificationItemId());
        boolean sameLocation = isSameLocation(deliveryItem.getLocationId(), targetItem.getLocationId());
        if (sameCategory && sameLocation) {
            return 0;
        }
        if (sameCategory) {
            return 1;
        }
        if (sameLocation) {
            return 2;
        }
        return 3;
    }

    private Long normalizeNullableId(Long value) {
        if (value == null || value <= 0) {
            return null;
        }
        return value;
    }

    @Transactional
    public void returnWaitPackage(StoYtDelivery params) {
        Long deliveryId = params.getId();
        StoYtDelivery delivery = stoYtDeliveryMapper.selectById(deliveryId);
        if (delivery == null || Integer.valueOf(1).equals(delivery.getIsDeleted())) {
            throw new BizException(ExceptionCodeEnum.Not_Exists.getCode(), "发货单不存在");
        }
        if (!DeliveryOrderStatusEnum.waitDelivery.getKey().toString().equals(delivery.getStatus())) {
            throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(), "仅待发货状态的发货单支持退回");
        }

        StoYtDelivery mergeTargetDelivery = findOtherWaitPackageDelivery(delivery);
        if (mergeTargetDelivery != null) {
            mergeReturnedDelivery(delivery, mergeTargetDelivery);
            return;
        }

        LambdaUpdateWrapper<StoYtDelivery> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(StoYtDelivery::getId, deliveryId)
                .set(StoYtDelivery::getStatus, DeliveryOrderStatusEnum.waitPackage.getKey().toString())
                .set(StoYtDelivery::getPackageTime, null)
                .set(StoYtDelivery::getPackageCode, null)
                .set(StoYtDelivery::getTransportCompanyId, null)
                .set(StoYtDelivery::getTransportOrderFileId, null)
                .set(StoYtDelivery::getDeliveryTime, null);
        stoYtDeliveryMapper.update(null, updateWrapper);
    }

    private StoYtDelivery findOtherWaitPackageDelivery(StoYtDelivery delivery) {
        LambdaQueryWrapper<StoYtDelivery> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StoYtDelivery::getIsDeleted, 0)
                .eq(StoYtDelivery::getStatus, DeliveryOrderStatusEnum.waitPackage.getKey().toString())
                .eq(StoYtDelivery::getCustomerId, delivery.getCustomerId())
                .eq(StoYtDelivery::getAddressId, delivery.getAddressId())
                .ne(StoYtDelivery::getId, delivery.getId())
                .orderByAsc(StoYtDelivery::getCreateTime);
        List<StoYtDelivery> deliveryList = stoYtDeliveryMapper.selectList(queryWrapper);
        return deliveryList == null || deliveryList.isEmpty() ? null : deliveryList.get(0);
    }

    private void mergeReturnedDelivery(StoYtDelivery sourceDelivery, StoYtDelivery targetDelivery) {
        //待发货的
        List<StoYtDeliveryItem> sourceItemList = stoYtDeliveryItemMapper.listByDeliveryId(sourceDelivery.getId());
        //待打包的
        List<StoYtDeliveryItem> targetItemList = stoYtDeliveryItemMapper.listByDeliveryId(targetDelivery.getId());

        // 使用 List 而非 Map，以正确处理同一 orderSubItemId 有多条记录的情况
        List<StoYtDeliveryItem> availableTargetItems = new ArrayList<>();
        if (targetItemList != null) {
            availableTargetItems.addAll(targetItemList);
        }

        if (sourceItemList != null) {
            for (StoYtDeliveryItem sourceItem : sourceItemList) {
                if (sourceItem.getIsDeleted() != null && sourceItem.getIsDeleted().equals(1)) {
                    continue;
                }
                Long orderSubItemId = sourceItem.getOrderSubItemId();
                Integer sourceNumber = sourceItem.getNumber() == null ? 0 : sourceItem.getNumber();
                if (sourceNumber <= 0) {
                    sourceItem.setIsDeleted(1);
                    stoYtDeliveryItemMapper.updateById(sourceItem);
                    continue;
                }

                // 查找所有匹配的可用目标项
                List<StoYtDeliveryItem> matchedTargets = new ArrayList<>();
                List<StoYtDeliveryItem> unmatchedTargets = new ArrayList<>();
                for (StoYtDeliveryItem targetItem : availableTargetItems) {
                    if (targetItem.getIsDeleted() != null && targetItem.getIsDeleted().equals(1)) {
                        continue;
                    }
                    if (orderSubItemId != null && orderSubItemId.equals(targetItem.getOrderSubItemId())) {
                        matchedTargets.add(targetItem);
                    } else {
                        unmatchedTargets.add(targetItem);
                    }
                }

                if (!matchedTargets.isEmpty()) {
                    // 按优先级匹配：先匹配定制属性，再匹配库位
                    List<StoYtDeliveryItem> sortedMatches = sortDeliveryItemsByMatchPriority(new ArrayList<>(matchedTargets), sourceItem);
                    int remainingSourceNumber = sourceNumber;

                    for (StoYtDeliveryItem targetItem : sortedMatches) {
                        if (remainingSourceNumber <= 0) {
                            break;
                        }
                        Integer targetNumber = targetItem.getNumber() == null ? 0 : targetItem.getNumber();
                        targetItem.setNumber(targetNumber + remainingSourceNumber);
                        stoYtDeliveryItemMapper.updateById(targetItem);
                        remainingSourceNumber = 0;
                        break;
                    }

                    if (remainingSourceNumber > 0) {
                        throw new BizException("订单子项合并时数量超过上限，数据可能已变更，请刷新后重试");
                    }

                    // 标记源项已删除
                    sourceItem.setIsDeleted(1);
                    stoYtDeliveryItemMapper.updateById(sourceItem);

                    // 更新可用目标项列表（移除已耗尽的项目）
                    availableTargetItems.clear();
                    availableTargetItems.addAll(unmatchedTargets);
                    for (StoYtDeliveryItem matched : sortedMatches) {
                        if (matched.getNumber() != null && matched.getNumber() > 0) {
                            availableTargetItems.add(matched);
                        }
                    }
                } else {
                    // 未找到匹配项，将源项直接转移到目标发货单
                    sourceItem.setDeliveryId(targetDelivery.getId());
                    stoYtDeliveryItemMapper.updateById(sourceItem);
                    availableTargetItems.add(sourceItem);
                }
            }
        }

        // 处理打包箱：将源发货单的打包箱转移到目标发货单，并清理空箱子
        List<StoYtDeliveryBox> sourceBoxList = stoYtDeliveryBoxMapper.selectByDeliveryId(sourceDelivery.getId());
        if (sourceBoxList != null) {
            for (StoYtDeliveryBox sourceBox : sourceBoxList) {
                if (sourceBox.getIsDeleted() != null && sourceBox.getIsDeleted() == 1) {
                    continue;
                }
                sourceBox.setDeliveryId(targetDelivery.getId());
                stoYtDeliveryBoxMapper.updateById(sourceBox);

                // 清理空打包箱物品
                List<StoYtDeliveryBoxItem> boxItems = stoYtDeliveryBoxItemMapper.getItemsByBoxId(sourceBox.getId());
                if (boxItems != null) {
                    for (StoYtDeliveryBoxItem boxItem : boxItems) {
                        if (boxItem.getNumber() == null || boxItem.getNumber() <= 0) {
                            boxItem.setIsDeleted(1);
                            stoYtDeliveryBoxItemMapper.updateById(boxItem);
                        }
                    }
                }
            }
        }

        // 清理目标发货单中的空箱子
        List<StoYtDeliveryBox> targetBoxList = stoYtDeliveryBoxMapper.selectByDeliveryId(targetDelivery.getId());
        if (targetBoxList != null) {
            for (StoYtDeliveryBox box : targetBoxList) {
                if (box.getIsDeleted() != null && box.getIsDeleted() == 1) {
                    continue;
                }
                List<StoYtDeliveryBoxItem> boxItems = stoYtDeliveryBoxItemMapper.getItemsByBoxId(box.getId());
                if (boxItems == null || boxItems.isEmpty()) {
                    box.setIsDeleted(1);
                    stoYtDeliveryBoxMapper.updateById(box);
                }
            }
        }

        sourceDelivery.setIsDeleted(1);
        stoYtDeliveryMapper.updateById(sourceDelivery);
    }

    /**
     * 获取包裹列表
     *
     * @param params 查询参数，包含发货单ID
     * @return 包裹列表
     */
    public List<StoYtDeliveryBox> packageList(StoYtDeliveryQueryParams params) {
        // 使用Wrapper构建查询条件
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<StoYtDeliveryBox> wrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        wrapper.eq("delivery_id", params.getId());
        wrapper.eq("is_deleted", 0);

        // 查询包裹列表
        List<StoYtDeliveryBox> stoYtDeliveryBoxes = stoYtDeliveryBoxMapper.selectList(wrapper);
        for (StoYtDeliveryBox box : stoYtDeliveryBoxes) {
            Long boxId = box.getBoxId();
            StoYtBox stoYtBox = stoYtBoxMapper.selectById(boxId);
            box.setBox(stoYtBox);
        }
        return stoYtDeliveryBoxes;
    }

    /**
     * 获取包裹物品列表
     *
     * @param params 查询参数，包含发货单ID和发货箱ID
     * @return 包裹物品列表，包含产品code、库位名称、规格详情和图片列表
     */
    public List<StoYtDeliveryBoxItem> packageItemList(StoYtDeliveryQueryParams params) {
        // 1. 查询包裹物品列表，包含产品code和库位名称
        List<StoYtDeliveryBoxItem> itemList = stoYtDeliveryBoxItemMapper.getItemsByBoxId(params.getDeliveryBoxId());

        // 2. 组装数据，添加规格详情和图片列表
        for (StoYtDeliveryBoxItem item : itemList) {
            Long specificationId = item.getSpecificationId();
            if (specificationId != null) {
                // 添加图片列表
                List<ProYtProductFile> fileListBySpecification = proYtProductManager.getFileListBySpecification(specificationId);
                item.setImageList(fileListBySpecification);

                // 添加规格详情列表
                List<ProYtProductSpecificationItem> itemsListBySpecification = proYtProductManager.getItemsListBySpecification(specificationId);
                item.setItemList(itemsListBySpecification);
            }
        }

        return itemList;
    }

    public Object detailOrderSub(StoYtDeliveryQueryParams params) {
        return stoYtDeliveryItemMapper.selectOrderSubByDeliveryId(params);
    }

    @Transactional
    public void confirmDelivery(StoYtDelivery params) {
        params.setDeliveryTime(new Date());
        params.setStatus(DeliveryOrderStatusEnum.delivered.getKey().toString());
        //修改主表
        stoYtDeliveryMapper.updateById(params);
    }

    public void updateTransport(StoYtDelivery params) {
        LambdaUpdateWrapper<StoYtDelivery> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(StoYtDelivery::getId, params.getId());
        updateWrapper.set(StoYtDelivery::getTransportOrderFileId, params.getTransportOrderFileId());
        updateWrapper.set(StoYtDelivery::getPackageCode, params.getPackageCode());
        updateWrapper.set(StoYtDelivery::getTransportCompanyId, params.getTransportCompanyId());
        stoYtDeliveryMapper.update(null, updateWrapper);
    }

    public Object deliveryPaymentList(FinYtPaymentQueryParams params) {
//        PageHelper.startPage(params.getPageNum(), params.getPageSize());
//        StoYtDeliveryQueryParams stoYtDeliveryQueryParams = new StoYtDeliveryQueryParams();
//        stoYtDeliveryQueryParams.setCode(params.getDeliveryCode());
//        stoYtDeliveryQueryParams.setPackageCode(params.getPackageCode());
//        stoYtDeliveryQueryParams.setCustomerName(params.getCustomerName());
//        stoYtDeliveryQueryParams.setTransportCompanyName(params.getTransportCompanyName());
//        stoYtDeliveryQueryParams.setSubCode(params.getOrderSubCode());
//        stoYtDeliveryQueryParams.setOrderCode(params.getOrderCode());
//        stoYtDeliveryQueryParams.setStatus(DeliveryOrderStatusEnum.delivered.getKey().toString());
//        List<StoYtDeliveryVo> list = stoYtDeliveryMapper.list(stoYtDeliveryQueryParams);
//        for (StoYtDeliveryVo vo : list) {
//            Long id = vo.getId();
//            // 根据发货单id从sto_yt_delivery_item中查询出，不同的orderSubId的产品占据的数量比例
//            List<Map<String, Object>> orderSubIdQuantityList = stoYtDeliveryItemMapper.selectOrderSubIdQuantityByDeliveryId(id);
//            if (orderSubIdQuantityList != null && !orderSubIdQuantityList.isEmpty()) {
//                // 计算总数量
//                int totalQuantity = 0;
//                for (Map<String, Object> map : orderSubIdQuantityList) {
//                    Integer quantity = Integer.parseInt(map.get("quantity").toString());
//                    totalQuantity += quantity;
//                }
//                // 计算各orderSubId的产品数量比例
//                List<Map<String, Object>> orderSubIdQuantityRatioMap = new ArrayList<>();
//                for (Map<String, Object> map : orderSubIdQuantityList) {
//                    Integer quantity = Integer.parseInt(map.get("quantity").toString());
//                    if (vo.getDeliveryAmount() != null) {
//                        map.put("amount", vo.getDeliveryAmount().setScale(2, RoundingMode.HALF_UP));
//                    }
//                    orderSubIdQuantityRatioMap.add(map);
//                }
//                vo.setOrderSubAmountList(orderSubIdQuantityRatioMap);
//            }
//        }
//        return new PageResultInfo<>(list);

        PageHelper.startPage(params.getPageNum(), params.getPageSize());
        StoYtDeliveryQueryParams stoYtDeliveryQueryParams = new StoYtDeliveryQueryParams();
        stoYtDeliveryQueryParams.setCode(params.getDeliveryCode());
        stoYtDeliveryQueryParams.setPackageCode(params.getPackageCode());
        stoYtDeliveryQueryParams.setCustomerName(params.getCustomerName());
        stoYtDeliveryQueryParams.setTransportCompanyName(params.getTransportCompanyName());
        stoYtDeliveryQueryParams.setSubCode(params.getOrderSubCode());
        stoYtDeliveryQueryParams.setOrderCode(params.getOrderCode());
        stoYtDeliveryQueryParams.setStatus(DeliveryOrderStatusEnum.delivered.getKey().toString());
        List<StoYtDeliveryVo> list = stoYtDeliveryMapper.list(stoYtDeliveryQueryParams);
        for (StoYtDeliveryVo vo : list) {
            Long id = vo.getId();
            // 根据发货单id从sto_yt_delivery_item中查询出，不同的orderSubId的产品占据的数量比例
            List<Map<String, Object>> orderSubIdQuantityList = stoYtDeliveryItemMapper.selectOrderSubIdQuantityByDeliveryId(id);
            if (orderSubIdQuantityList != null && !orderSubIdQuantityList.isEmpty()) {
                // 计算总数量
                int totalQuantity = 0;
                for (Map<String, Object> map : orderSubIdQuantityList) {
                    Integer quantity = Integer.parseInt(map.get("quantity").toString());
                    totalQuantity += quantity;
                }
                // 计算各orderSubId的产品数量比例
                List<Map<String, Object>> orderSubIdQuantityRatioMap = new ArrayList<>();
                for (Map<String, Object> map : orderSubIdQuantityList) {
                    Integer quantity = Integer.parseInt(map.get("quantity").toString());
//                    if (vo.getDeliveryAmount() != null) {
//                        map.put("amount", vo.getDeliveryAmount().setScale(2, RoundingMode.HALF_UP));
//                    }

                    //获取到子订单编号
                    Long orderSubId=Long.parseLong(map.get("orderSubId").toString());

                    //amount为应付款-被分配金额（这个金额在子订单payment_shipping字段）
//                    SalYtOrderSub salYtOrderSub=salYtOrderSubMapper.selectById(orderSubId);
//                    if(salYtOrderSub!=null){
//                        if(salYtOrderSub.getPaymentShipping()!=null){
//                            map.put("amount",salYtOrderSub.getPaymentShipping().setScale(2, RoundingMode.HALF_UP));
//                        }
//                    }
                    //计算被分配运费金额
                    //发货单运费金额
                    if(vo.getDeliveryAmount()!=null) {
                        BigDecimal deliveryAmount = vo.getDeliveryAmount();
                        //该子订单在发货单item里面的数量
                        //发货单item里面的总数量
                        BigDecimal totalNumbers = stoYtDeliveryItemMapper.selectTotalNumbers(vo.getId());
                        //该子订单在发货单item里面的数量
                        BigDecimal proportion = stoYtDeliveryItemMapper.selectSubOrderTotalNumbers(vo.getId(), orderSubId);

                        BigDecimal amount = deliveryAmount.multiply(proportion).divide(totalNumbers, 2, RoundingMode.HALF_UP);
                        map.put("amount", amount);
                    }


                    //根据子订单id查询主订单信息
                    SalYtOrder salYtOrder=salYtOrderMapper.selectSalYtOrderBySubOrderId(orderSubId);
                    map.put("orderCode",salYtOrder.getCode());
                    orderSubIdQuantityRatioMap.add(map);
                }

                vo.setOrderSubAmountList(orderSubIdQuantityRatioMap);
            }
        }
        return new PageResultInfo<>(list);
    }

    public Object deliveryReceiveList(FinYtReceiveQueryParams params) {
        PageHelper.startPage(params.getPageNum(), params.getPageSize());
        StoYtDeliveryQueryParams stoYtDeliveryQueryParams = new StoYtDeliveryQueryParams();
        stoYtDeliveryQueryParams.setCode(params.getDeliveryCode());
        stoYtDeliveryQueryParams.setPackageCode(params.getPackageCode());
        stoYtDeliveryQueryParams.setSubCode(params.getSubCode());
        stoYtDeliveryQueryParams.setReceiveStatus(params.getReceiveStatus());
        stoYtDeliveryQueryParams.setSalesEmployeeName(params.getSalesEmployeeName());
        stoYtDeliveryQueryParams.setStatus(DeliveryOrderStatusEnum.delivered.getKey().toString());
        stoYtDeliveryQueryParams.setIsCollectedShippingCost(false);
        stoYtDeliveryQueryParams.setOrderCode(params.getOrderCode());
        //List<StoYtDeliveryVo> list = stoYtDeliveryMapper.list(stoYtDeliveryQueryParams);
        List<StoYtDeliveryVo> list = stoYtDeliveryMapper.list1(stoYtDeliveryQueryParams);
        //用于计算未收运费订单里面被分配运费金额总和
        BigDecimal deliveryAmount = BigDecimal.ZERO;
        for (StoYtDeliveryVo vo : list) {
            //根据发货单id查询上一次发货单回款所用币种、
            LambdaQueryWrapper<StoYtDeliveryReceive> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(StoYtDeliveryReceive::getDeliveryId, vo.getId());
            queryWrapper.eq(StoYtDeliveryReceive::getIsDeleted, 0);
            queryWrapper.last("limit 1");
            StoYtDeliveryReceive deliveryReceive = stoYtDeliveryReceiveMapper.selectOne(queryWrapper);
            if(deliveryReceive!=null){
                vo.setDeliveryReceiveCurrency(deliveryReceive.getCurrency());
            }


            Long id = vo.getId();
            //计算回款金额
            BigDecimal receiveAmount = stoYtDeliveryReceiveMapper.getTotalReceiveByDeliveryId(id);
            vo.setReceiveAmount(receiveAmount);
            //发货单item里面的总数量
            BigDecimal totalNumbers = stoYtDeliveryItemMapper.selectTotalNumbers(id);
            // 根据发货单id从sto_yt_delivery_item中查询出，不同的orderSubId的产品占据的数量比例
            List<Map<String, Object>> orderQuantityList = stoYtDeliveryItemMapper.selectOrderIdQuantityByDeliveryId(id);
           // if (vo.getDeliveryAmount() != null) {

                if (orderQuantityList != null && !orderQuantityList.isEmpty()) {
                    // 计算总数量
                    int totalQuantity = 0;
                    for (Map<String, Object> map : orderQuantityList) {
                        Object quantityObject = map.get("quantity");
                        if (quantityObject != null) {
                            Integer quantity = Integer.parseInt(quantityObject.toString());
                            totalQuantity += quantity;
                        }
                    }

                    // 计算各orderSubId的产品数量比例
                    List<Map<String, Object>> orderIdQuantityRatioMap = new ArrayList<>();
                    for (Map<String, Object> map : orderQuantityList) {
                        double percentage = 0D;
                        Object quantityObject = map.get("quantity");
                        if (quantityObject != null) {
                            Integer quantity = Integer.parseInt(quantityObject.toString());
                            percentage = totalQuantity > 0 ? (quantity * 1.0) / totalQuantity : 0;
                        }
                        //deliveryAmount->被分配运费金额
                        //receiveAmount->回款分配金额
                        if (vo.getDeliveryAmount() != null) {
                            //map.put("deliveryAmount", vo.getDeliveryAmount().multiply(new BigDecimal(percentage)).setScale(2, RoundingMode.HALF_UP));
                            map.put("deliveryAmount", vo.getDeliveryAmount().multiply(new BigDecimal(quantityObject.toString())).divide(totalNumbers,2,RoundingMode.HALF_UP));
                            //计算未收运费订单里面被分配运费金额总和
                            deliveryAmount=deliveryAmount.add(new BigDecimal(map.get("deliveryAmount").toString()));
                        }else{
                            map.put("deliveryAmount", BigDecimal.ZERO);
                        }
                        map.put("receiveAmount", vo.getReceiveAmount().multiply(new BigDecimal(percentage)).setScale(2, RoundingMode.HALF_UP));

                        orderIdQuantityRatioMap.add(map);
                    }
                    vo.setOrderAmountList(orderIdQuantityRatioMap);
                }
                vo.setDeliveryAmount(deliveryAmount);
            //}
        }
        return new PageResultInfo<>(list);
    }

    public void confirmDeliveryReceive(StoYtDeliveryReceive params) {
        Boolean isCompletedReceive = params.getIsCompletedReceive();
        StoYtDelivery delivery = new StoYtDelivery();
        delivery.setId(params.getDeliveryId());
        if (isCompletedReceive != null && isCompletedReceive) {
            delivery.setReceiveStatus(ReceiveStatusEnum.Completed.getKey());
            //添加发货单运费回款完成时间
            if(params.getReceiveFinishTime()!=null) {
                delivery.setReceiveFinishTime(params.getReceiveFinishTime());
            }
        } else {
            delivery.setReceiveStatus(ReceiveStatusEnum.ReceivePart.getKey());
        }
        stoYtDeliveryMapper.updateById(delivery);
        stoYtDeliveryReceiveMapper.insert(params);
    }

    public List<StoYtDeliveryReceive> deliveryReceiveDetail(FinYtReceiveQueryParams params) {
        List<StoYtDeliveryReceive> deliveryReceives = stoYtDeliveryReceiveMapper.list(params);
        //用于处理是否需要赋值回款完成时间
        Boolean needSetReceiveFinishTime = true;
        for (StoYtDeliveryReceive receive : deliveryReceives) {
            if(receive.getReceiveFinishTime()!=null){
                if(!needSetReceiveFinishTime){
                    //除开第一个数据后，其他数据都清空回款完成时间
                    receive.setReceiveFinishTime(null);
                }
                needSetReceiveFinishTime=false;
            }


            Long id = receive.getId();
            List<ProYtProductFile> proYtProductFiles = proYtProductFileMapper.selectByMasterIdAndType(id, ProductFilesTypeEnum.deliveryReceiveFile.getKey());
            receive.setFileList(proYtProductFiles);
        }
        return deliveryReceives;
    }

    /**
     * 根据发货单ID分组查询涉及的订单
     *
     * @param deliveryId 发货单ID
     * @return 按订单分组的信息列表
     */
    public List<StoYtDeliveryOrderVo> listGroupOrderByDeliveryId(Long deliveryId) {
        List<StoYtDeliveryOrderVo> orderList = stoYtDeliveryItemMapper.listGroupOrderByDeliveryId(deliveryId);
        for (StoYtDeliveryOrderVo orderVo : orderList) {
            // 设置产品状态
            boolean productComplete = checkDeliveryProductStatus(orderVo.getOrderId());
            orderVo.setProductComplete(productComplete);
        }
        return orderList;
    }

    /**
     * 根据物流公司单号或发货单号更新发货金额
     *
     * @param packageCode  物流公司单号
     * @param deliveryCode 发货单号
     * @param amount       发货金额
     */
    public void updateDeliveryAmountByCode(String packageCode, String deliveryCode, BigDecimal amount) {
        if (packageCode == null && deliveryCode == null) {
            throw new BizException(ExceptionCodeEnum.Param_Exception);
        }

        QueryWrapper<StoYtDelivery> queryWrapper = new QueryWrapper<>();
        if (packageCode != null && !packageCode.isEmpty()) {

            queryWrapper.eq("package_code", packageCode);
        } else {
            queryWrapper.eq("code", deliveryCode);
        }

        StoYtDelivery delivery = stoYtDeliveryMapper.selectOne(queryWrapper);
        if (delivery == null) {
            //  throw new BizException(ExceptionCodeEnum.Not_Exists);
            throw new BizException(512, "导入失败");
        }

        delivery.setDeliveryAmount(amount);
        stoYtDeliveryMapper.updateById(delivery);
    }

    /**
     * 批量导入客户运费数据
     *
     * @param importList 导入数据列表
     * @return 导入结果
     */
    public String importCustomerDeliveryCost(List<Map<String, Object>> importList) {
        if (importList == null || importList.isEmpty()) {
            return "导入数据为空";
        }

        StringBuilder errorMsg = new StringBuilder();
        int successCount = 0;
        int failCount = 0;

        for (int i = 0; i < importList.size(); i++) {
            Map<String, Object> dataMap = importList.get(i);
            int rowNum = i + 2; // Excel行号从1开始，标题行占1行

            //      try {

            String packageCode = String.valueOf(dataMap.get("logisticsOrderCode"));
            String deliveryCode = String.valueOf(dataMap.get("deliveryCode"));
            Long amount1 = (Long) dataMap.get("amount");
            // 验证数据是否存在
            if (amount1 == null) {
                throw new BizException(501, "导入失败");
            }
            BigDecimal amount = BigDecimal.valueOf(amount1);


            // 验证数据
            if (packageCode == null && deliveryCode == null) {
                failCount++;
                errorMsg.append("第").append(rowNum).append("行：物流公司单号和发货单号不能同时为空；");
                continue;
            }

            if (amount == null) {
                failCount++;
                errorMsg.append("第").append(rowNum).append("行：金额不能为空；");
                continue;
            }

            // 更新发货金额
            updateDeliveryAmountByCode(packageCode, deliveryCode, amount);
            successCount++;
//            } catch (BizException e) {
//                failCount++;
//                errorMsg.append("第").append(rowNum).append("行：").append(e.getMessage()).append("；");
//            } catch (Exception e) {
//                failCount++;
//                errorMsg.append("第").append(rowNum).append("行：数据格式错误；");
//            }
        }

        return String.format("成功导入%d条，失败%d条。%s", successCount, failCount, errorMsg.length() > 0 ? errorMsg.toString() : "");
    }

    public void deliveryPayment(FinYtPaymentUpdateParams params) {
        Long deliveryId = params.getDeliveryId();
        StoYtDelivery delivery = new StoYtDelivery();
        delivery.setId(deliveryId);
        delivery.setDeliveryAmount(params.getAmount());
        stoYtDeliveryMapper.updateById(delivery);
    }

    /**
     * 根据子订单ID查询相关发货单
     *
     * @param subId 子订单ID
     * @return 发货单列表
     */
    public List<StoYtDelivery> selectDeliveryByOrderSubId(Long subId) {
        // 查询发货单ID列表
        List<StoYtDelivery> deliveryInfoList = stoYtDeliveryItemMapper.selectDeliveryByOrderSubIdAndNotPackage(subId);
        if (deliveryInfoList == null || deliveryInfoList.isEmpty()) {
            return Collections.emptyList();
        }
        for (StoYtDelivery delivery : deliveryInfoList) {
            //该子订单在发货单item里面的数量
            //发货单item里面的总数量
            BigDecimal totalNumbers = stoYtDeliveryItemMapper.selectTotalNumbers(delivery.getId());
            //该子订单在发货单item里面的数量
            BigDecimal proportion = stoYtDeliveryItemMapper.selectSubOrderTotalNumbers(delivery.getId(), subId);
            //占比
            if (delivery.getDeliveryAmount()!=null) {
               // delivery.setDeliveryProportionAmount(delivery.getDeliveryAmount().multiply(delivery.getDeliveryProportion()));
                //必须先金额乘以子订单数量然后再除以总数量，不能先计算子订单数量/总数量，然后再乘以金额，会有精度缺失
                delivery.setDeliveryProportionAmount(delivery.getDeliveryAmount().multiply(proportion).divide(totalNumbers, 2, RoundingMode.HALF_UP));

            }
        }


        return deliveryInfoList;
    }

    /**
     * 根据子订单ID查询相关运费收款列表
     *
     * @param subId 子订单ID
     * @return 运费收款列表
     */
    public List<StoYtDeliveryReceive> selectDeliveryReceiveByOrderSubId(Long subId) {
        // 查询发货单ID列表
        List<StoYtDelivery> deliveryInfoList = stoYtDeliveryItemMapper.selectDeliveryByOrderSubId(subId);
        if (deliveryInfoList == null || deliveryInfoList.isEmpty()) {
            return Collections.emptyList();
        }

        List<StoYtDeliveryReceive> deliveryReceiveInfoList = new ArrayList<>();
        for (StoYtDelivery stoYtDelivery : deliveryInfoList) {
            Long deliveryId = stoYtDelivery.getId();
            QueryWrapper<StoYtDeliveryReceive> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("delivery_id", deliveryId);
            queryWrapper.eq("is_deleted", 0);

            List<StoYtDeliveryReceive> stoYtDeliveryReceives = stoYtDeliveryReceiveMapper.selectList(queryWrapper);
            if (stoYtDeliveryReceives != null && !stoYtDeliveryReceives.isEmpty()) {
                for (StoYtDeliveryReceive receive : stoYtDeliveryReceives) {
                    BigDecimal amount = receive.getAmount();
                    BigDecimal deliveryProportion = stoYtDelivery.getDeliveryProportion();
                    //该子订单在发货单item里面的数量
                    //发货单item里面的总数量（已收运费的订单的数量不会在这个里面）
                    BigDecimal totalNumbers = stoYtDeliveryItemMapper.selectTotalNumbersNotReceive(deliveryId);
                    //该子订单在发货单item里面的数量
                    BigDecimal proportion = stoYtDeliveryItemMapper.selectSubOrderTotalNumbers(deliveryId, subId);
                   // receive.setAmount(amount.multiply(deliveryProportion));
                    receive.setAmount(amount.multiply(proportion).divide(totalNumbers, 2, RoundingMode.HALF_UP));
                    deliveryReceiveInfoList.add(receive);
                }
            }
        }
        return deliveryReceiveInfoList;

    }

    public List<Map<String, Object>> detailOrder(StoYtDeliveryQueryParams params) {
        //发货单id
        Long id = params.getId();

        List<Map<String, Object>> maps = stoYtDeliveryItemMapper.selectOrderByDeliveryId(params);

        for(Map<String, Object> map : maps){
            Long orderId = Long.parseLong(map.get("orderId").toString());
            Boolean productComplete = checkDeliveryProductStatus(orderId);
            boolean packageComplete = getOrderPackageComplete(id, orderId);

            boolean isComplete = productComplete && packageComplete;

            map.put("productComplete", productComplete);
            map.put("packageComplete", packageComplete);
            map.put("isComplete", isComplete);
        }

        //按照完成顺序排序
        maps.sort((m1, m2) -> {
            int score1 = (Boolean.TRUE.equals(m1.get("productComplete")) ? 0 : 1) + (Boolean.TRUE.equals(m1.get("packageComplete")) ? 0 : 2);
            int score2 = (Boolean.TRUE.equals(m2.get("productComplete")) ? 0 : 1) + (Boolean.TRUE.equals(m2.get("packageComplete")) ? 0 : 2);
            return Integer.compare(score2, score1);
        });

        return maps;
    }

    public HashMap<String,Object> validCompletePackage(StoYtDeliveryCompleteParams params) {
        Long deliveryId = params.getDeliveryId();
        StoYtDeliveryQueryParams stoYtDeliveryQueryParams = new StoYtDeliveryQueryParams();
        stoYtDeliveryQueryParams.setId(deliveryId);
        List<StoYtDeliveryItem> itemList = stoYtDeliveryItemMapper.listGroupItem(stoYtDeliveryQueryParams);
        Boolean isComplete=Boolean.TRUE;
        Integer completeNumber=0;
        for (StoYtDeliveryItem item : itemList) {
            //设置产品状态
            Boolean productComplete = checkDeliveryProductStatus(item.getOrderId());
            //设置打包状态
            Boolean packageComplete = getOrderPackageComplete(deliveryId, item.getOrderId());

            if(!productComplete || !packageComplete) {
                isComplete = Boolean.FALSE;
            }else {
                completeNumber++;
            }
        }
        HashMap<String, Object> result = new HashMap<>();
        result.put("isComplete", isComplete);
        result.put("completeNumber", completeNumber);
        return result;
    }

    public void sendPackageMessage(StoYtDeliveryCompleteParams params) {
        List<String> orderCodes = params.getOrderCodes();
        if (orderCodes == null || orderCodes.isEmpty()) {
            return;
        }
        // 根据前端传入的订单号列表，逐个查询订单并发送通知
        for (String orderCode : orderCodes) {
            SalYtOrder salYtOrder = salYtOrderMapper.selectOne(
                new QueryWrapper<SalYtOrder>().eq("code", orderCode).eq("is_deleted", 0));
            if (salYtOrder == null) {
                continue;
            }
            Long saleEmployeeId = salYtOrder.getSaleEmployeeId();
            ArrayList<Long> userIdList = new ArrayList<>();
            userIdList.add(saleEmployeeId);
            String message = "订单号：" + salYtOrder.getCode()
                + "\n发货形式：" + ShippingMethodEnum.getValueByKey(salYtOrder.getShippingMethod())
                + "\n仓库人员请求修改发货形式";
            feiShuManager.sengMessageToUser(message, userIdList);
        }
    }
}
