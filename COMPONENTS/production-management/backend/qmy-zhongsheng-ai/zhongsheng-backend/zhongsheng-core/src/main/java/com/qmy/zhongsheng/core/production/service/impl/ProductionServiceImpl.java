package com.qmy.zhongsheng.core.production.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.api.dto.order.OrderProductSnapshotDTO;
import com.qmy.zhongsheng.api.dto.production.ProductionBatchItemDTO;
import com.qmy.zhongsheng.api.dto.production.ProductionBatchSaveDTO;
import com.qmy.zhongsheng.api.dto.production.ProductionDeliveryDTO;
import com.qmy.zhongsheng.api.dto.production.ProductionGroupListQueryDTO;
import com.qmy.zhongsheng.api.dto.production.ProductionGroupSaveDTO;
import com.qmy.zhongsheng.api.dto.production.ProductionOrderListQueryDTO;
import com.qmy.zhongsheng.api.dto.production.ProductionOrderProductDTO;
import com.qmy.zhongsheng.api.dto.production.ProductionOrderSaveDTO;
import com.qmy.zhongsheng.common.context.LoginUserInfoContext;
import com.qmy.zhongsheng.common.error.ProductionErrorCodeConstants;
import com.qmy.zhongsheng.common.exception.ServiceExceptionUtil;
import com.qmy.zhongsheng.common.login.LoginUserInfo;
import com.qmy.zhongsheng.common.utils.BeanUtils;
import com.qmy.zhongsheng.core.document.service.BusinessDocumentCodeService;
import com.qmy.zhongsheng.core.order.manager.OrdersManager;
import com.qmy.zhongsheng.core.order.model.entity.OrdersDO;
import com.qmy.zhongsheng.core.production.manager.ProductionGroupManager;
import com.qmy.zhongsheng.core.production.manager.ProductionOrderBatchManager;
import com.qmy.zhongsheng.core.production.manager.ProductionOrderManager;
import com.qmy.zhongsheng.core.production.manager.ProductionOrderProgressManager;
import com.qmy.zhongsheng.core.production.model.condition.ProductionGroupQueryCondition;
import com.qmy.zhongsheng.core.production.model.condition.ProductionOrderQueryCondition;
import com.qmy.zhongsheng.core.production.model.entity.ProductionGroupDO;
import com.qmy.zhongsheng.core.production.model.entity.ProductionOrderBatchDO;
import com.qmy.zhongsheng.core.production.model.entity.ProductionOrderDO;
import com.qmy.zhongsheng.core.production.model.entity.ProductionOrderProgressDO;
import com.qmy.zhongsheng.core.production.model.vo.ProductionGroupVO;
import com.qmy.zhongsheng.core.production.model.vo.ProductionOrderBatchVO;
import com.qmy.zhongsheng.core.production.model.vo.ProductionOrderProgressVO;
import com.qmy.zhongsheng.core.production.model.vo.ProductionOrderVO;
import com.qmy.zhongsheng.core.production.service.ProductionService;
import com.qmy.zhongsheng.core.purchase.manager.PurchaseOrderItemManager;
import com.qmy.zhongsheng.core.purchase.manager.PurchaseOrderManager;
import com.qmy.zhongsheng.core.purchase.model.entity.PurchaseOrderDO;
import com.qmy.zhongsheng.core.purchase.model.entity.PurchaseOrderItemDO;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 生产履约服务实现。
 *
 * @author AI Coding
 */
@Service
@RequiredArgsConstructor
public class ProductionServiceImpl implements ProductionService {

    private static final String ORDER_STATUS_CONFIRMED = "confirmed";
    private static final String ORDER_TYPE_MASTER = "master";
    private static final String ORDER_TYPE_STANDALONE = "standalone";
    private static final String LOCK_OPEN = "open";
    private static final String STATUS_DRAFT = "draft";
    private static final String STATUS_IN_PRODUCTION = "in_production";
    private static final String STATUS_COMPLETED = "completed";
    private static final String STATUS_MANUAL_RECONCILE = "manual_reconcile";
    private static final String PURCHASE_STATUS_CANCELLED = "cancelled";
    private static final String PRODUCTION_TEMPLATE_RESOURCE = "excel/production-order-template.xls";
    private static final String BATCH_STATUS_SCHEDULED = "scheduled";
    private static final String PROGRESS_PENDING = "pending";
    private static final String PROGRESS_RELEASED = "released";
    private static final String PROGRESS_SCHEDULED = "scheduled";
    private static final String PROGRESS_DELIVERING = "delivering";
    private static final String PROGRESS_COMPLETED = "completed";
    private static final String PROGRESS_MANUAL_RECONCILE = "manual_reconcile";
    private static final String ALL_PERMISSION = "*";
    private static final int IMAGE_CONNECT_TIMEOUT_MS = 5000;
    private static final int IMAGE_READ_TIMEOUT_MS = 10000;
    private static final int MAX_IMAGE_BYTES = 5 * 1024 * 1024;
    private static final int PRODUCT_IMAGE_FIRST_ROW = 20;
    private static final int PRODUCT_IMAGE_LAST_ROW = 30;
    private static final int PRODUCT_IMAGE_FIRST_COL = 0;
    private static final int PRODUCT_IMAGE_LAST_COL = 3;
    private static final Pattern ALIYUN_OSS_HOST_PATTERN =
            Pattern.compile("(^|\\.)oss-[a-z0-9-]+\\.aliyuncs\\.com$", Pattern.CASE_INSENSITIVE);
    private static final Pattern IPV4_PATTERN =
            Pattern.compile("^\\d{1,3}(?:\\.\\d{1,3}){3}$");

    private final ProductionGroupManager productionGroupManager;
    private final ProductionOrderManager productionOrderManager;
    private final ProductionOrderProgressManager productionOrderProgressManager;
    private final ProductionOrderBatchManager productionOrderBatchManager;
    private final PurchaseOrderManager purchaseOrderManager;
    private final PurchaseOrderItemManager purchaseOrderItemManager;
    private final OrdersManager ordersManager;
    private final BusinessDocumentCodeService documentCodeService;

    @Value("${oss.base-url:}")
    private String ossBaseUrl;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveGroup(ProductionGroupSaveDTO dto) {
        if (dto == null || !StringUtils.hasText(dto.getName())) {
            throw ServiceExceptionUtil.exception(ProductionErrorCodeConstants.PRODUCTION_GROUP_NAME_REQUIRED);
        }
        ProductionGroupDO group = dto.getId() == null ? new ProductionGroupDO() : productionGroupManager.getById(dto.getId());
        if (dto.getId() != null && group == null) {
            throw ServiceExceptionUtil.exception(ProductionErrorCodeConstants.PRODUCTION_GROUP_NOT_FOUND);
        }
        if (group.getId() == null) {
            group.setId(IdWorker.getId(group));
        }
        String code = firstText(dto.getCode(), buildGroupCode(group.getId()));
        if (productionGroupManager.existsByCode(code, group.getId())) {
            throw ServiceExceptionUtil.exception(ProductionErrorCodeConstants.PRODUCTION_GROUP_CODE_DUPLICATE);
        }
        group.setCode(code);
        group.setName(dto.getName().trim());
        group.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        group.setRemark(trimToNull(dto.getRemark()));
        return productionGroupManager.saveOrUpdate(group);
    }

    @Override
    public Page<ProductionGroupVO> groupPage(ProductionGroupListQueryDTO query) {
        ProductionGroupQueryCondition condition = buildGroupCondition(query);
        Page<ProductionGroupDO> doPage = productionGroupManager.page(condition);
        Page<ProductionGroupVO> voPage = new Page<>(doPage.getCurrent(), doPage.getSize(), doPage.getTotal());
        voPage.setRecords(doPage.getRecords().stream().map(this::toGroupVO).toList());
        return voPage;
    }

    @Override
    public ProductionGroupVO groupDetail(Long id) {
        ProductionGroupDO group = productionGroupManager.getById(id);
        if (group == null) {
            throw ServiceExceptionUtil.exception(ProductionErrorCodeConstants.PRODUCTION_GROUP_NOT_FOUND);
        }
        return toGroupVO(group);
    }

    @Override
    public List<ProductionGroupVO> groupOptions(String keyword) {
        return productionGroupManager.listOptions(trimToNull(keyword)).stream().map(this::toGroupVO).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteGroup(Long id) {
        ProductionGroupDO group = productionGroupManager.getById(id);
        if (group == null) {
            throw ServiceExceptionUtil.exception(ProductionErrorCodeConstants.PRODUCTION_GROUP_NOT_FOUND);
        }
        return productionGroupManager.delete(id);
    }

    @Override
    public Page<ProductionOrderVO> orderPage(ProductionOrderListQueryDTO query) {
        ProductionOrderQueryCondition condition = buildOrderCondition(query);
        Page<ProductionOrderDO> doPage = productionOrderManager.page(condition);
        Page<ProductionOrderVO> voPage = new Page<>(doPage.getCurrent(), doPage.getSize(), doPage.getTotal());
        voPage.setRecords(doPage.getRecords().stream().map(this::buildVO).toList());
        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveOrder(ProductionOrderSaveDTO dto) {
        if (dto == null || dto.getProducts() == null || dto.getProducts().isEmpty()) {
            throw ServiceExceptionUtil.exception(ProductionErrorCodeConstants.PRODUCTION_ORDER_PRODUCT_REQUIRED);
        }
        LoginUserInfo loginUser = LoginUserInfoContext.requireLoginUserInfo();
        ProductionOrderDO order = dto.getId() == null ? new ProductionOrderDO() : requireVisibleOrder(dto.getId());
        if (order.getId() != null && StringUtils.hasText(order.getOrderCode())) {
            throw ServiceExceptionUtil.exception(ProductionErrorCodeConstants.PRODUCTION_ORDER_SOURCE_READONLY);
        }
        if (order.getId() == null) {
            Long id = IdWorker.getId(order);
            order.setId(id);
            order.setOrderType(ORDER_TYPE_STANDALONE);
            order.setMasterOrderKey("standalone:" + id);
            order.setStatus(STATUS_DRAFT);
            order.setLockState(LOCK_OPEN);
            order.setNeedsReconfirm(Boolean.FALSE);
            order.setOwnerId(loginUser.getUserId());
            order.setOwnerName(loginUser.getUserName());
        }
        String code = firstText(dto.getCode(), order.getBaseCode(), order.getCode());
        if (!StringUtils.hasText(code)) {
            code = documentCodeService.generateBaseCode();
        } else if (documentCodeService.activeBaseCodeExists(
                code, BusinessDocumentCodeService.DOCUMENT_PRODUCTION, order.getId())) {
            throw ServiceExceptionUtil.exception(ProductionErrorCodeConstants.PRODUCTION_ORDER_CODE_DUPLICATE);
        }
        order.setCode(code);
        order.setBaseCode(code);
        order.setSerialCode(documentCodeService.nextProductionSerialCode(code, order.getId()));
        order.setCustomerId(dto.getCustomerId());
        order.setCustomerName(trimToNull(dto.getCustomerName()));
        order.setDeliveryDate(dto.getDeliveryDate());
        order.setRemark(trimToNull(dto.getRemark()));
        productionOrderManager.saveOrUpdate(order);
        upsertManualProgressRows(order, dto.getProducts());
        recomputeMaster(order.getId());
        return order.getId();
    }

    @Override
    public ProductionOrderVO orderDetail(Long id) {
        Scope scope = currentScope(LoginUserInfoContext.requireLoginUserInfo());
        ProductionOrderDO order = productionOrderManager.getVisibleById(id, scope.userId(), scope.allVisible());
        if (order == null) {
            throw ServiceExceptionUtil.exception(ProductionErrorCodeConstants.PRODUCTION_ORDER_NOT_FOUND);
        }
        return buildVO(order);
    }

    @Override
    public ProductionOrderVO orderDetailByOrderId(Long orderId) {
        Scope scope = currentScope(LoginUserInfoContext.requireLoginUserInfo());
        ProductionOrderDO order = productionOrderManager.getVisibleByOrderId(orderId, scope.userId(), scope.allVisible());
        if (order == null) {
            throw ServiceExceptionUtil.exception(ProductionErrorCodeConstants.PRODUCTION_ORDER_NOT_FOUND);
        }
        return buildVO(order);
    }

    @Override
    public ProductionOrderVO findMasterByOrderId(Long orderId) {
        ProductionOrderDO master = productionOrderManager.getByOrderId(orderId);
        return master == null ? null : buildVO(master);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductionOrderVO ensureMasterForOrder(OrdersDO order) {
        if (order == null || order.getId() == null) {
            throw ServiceExceptionUtil.exception(ProductionErrorCodeConstants.PRODUCTION_ORDER_REQUIRED);
        }
        if (!ORDER_STATUS_CONFIRMED.equals(order.getStatus())) {
            throw ServiceExceptionUtil.exception(ProductionErrorCodeConstants.PRODUCTION_ORDER_ONLY_CONFIRMED);
        }
        ProductionOrderDO master = productionOrderManager.getByOrderId(order.getId());
        if (master == null) {
            master = new ProductionOrderDO();
            Long id = IdWorker.getId(master);
            master.setId(id);
            String baseCode = documentCodeService.firstTextOrGenerate(order.getBaseCode(), order.getCode());
            master.setCode(baseCode);
            master.setBaseCode(baseCode);
            master.setSerialCode(documentCodeService.nextProductionSerialCode(baseCode, id));
            master.setOrderType(ORDER_TYPE_MASTER);
            master.setMasterOrderKey(String.valueOf(order.getId()));
            master.setStatus(STATUS_DRAFT);
            master.setLockState(LOCK_OPEN);
            master.setNeedsReconfirm(Boolean.FALSE);
        }
        master.setOrderId(order.getId());
        master.setOrderCode(order.getCode());
        master.setCustomerId(order.getCustomerId());
        master.setCustomerName(order.getCustomerName());
        master.setDeliveryDate(order.getDeliveryDate());
        master.setOwnerId(order.getOwnerId());
        master.setOwnerName(order.getOwnerName());
        productionOrderManager.saveOrUpdate(master);
        upsertProgressRows(master, parseProducts(order.getProductsJson()));
        syncPurchaseProgress(order.getId());
        recomputeMaster(master.getId());
        return buildVO(productionOrderManager.getById(master.getId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncPurchaseProgress(Long orderId) {
        ProductionOrderDO master = productionOrderManager.getByOrderId(orderId);
        if (master == null) {
            return;
        }
        List<ProductionOrderProgressDO> rows = productionOrderProgressManager.listByProductionOrderId(master.getId());
        if (rows.isEmpty()) {
            return;
        }
        Map<String, List<PurchaseOrderItemDO>> purchaseItems = loadActivePurchaseItemsByLine(orderId);
        for (ProductionOrderProgressDO row : rows) {
            PurchaseCoverage coverage = calculateCoverage(purchaseItems.getOrDefault(row.getLineKey(), List.of()));
            row.setPurchasedQty(coverage.purchasedQty());
            row.setInboundQty(coverage.inboundQty());
            row.setPlannedQty(sumBatchQty(productionOrderBatchManager.listByProgressId(row.getId())));
            row.setProgressStatus(resolveProgressStatus(row));
            productionOrderProgressManager.saveOrUpdate(row);
        }
        recomputeMaster(master.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductionOrderVO arrangeBatches(ProductionBatchSaveDTO dto) {
        if (dto == null || dto.getProductionOrderId() == null) {
            throw ServiceExceptionUtil.exception(ProductionErrorCodeConstants.PRODUCTION_ORDER_REQUIRED);
        }
        if (dto.getBatches() == null || dto.getBatches().isEmpty()) {
            throw ServiceExceptionUtil.exception(ProductionErrorCodeConstants.PRODUCTION_BATCH_REQUIRED);
        }
        ProductionOrderDO order = requireVisibleOrder(dto.getProductionOrderId());
        syncPurchaseProgress(order.getOrderId());
        order = requireVisibleOrder(dto.getProductionOrderId());
        for (ProductionBatchItemDTO item : dto.getBatches()) {
            arrangeBatch(order, item);
        }
        syncPurchaseProgress(order.getOrderId());
        return orderDetail(order.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductionOrderVO recordDelivery(ProductionDeliveryDTO dto) {
        if (dto == null || dto.getProductionOrderId() == null || dto.getProgressId() == null) {
            throw ServiceExceptionUtil.exception(ProductionErrorCodeConstants.PRODUCTION_PROGRESS_NOT_FOUND);
        }
        ProductionOrderDO order = requireVisibleOrder(dto.getProductionOrderId());
        ProductionOrderProgressDO progress = productionOrderProgressManager.getById(dto.getProgressId());
        if (progress == null || !Objects.equals(progress.getProductionOrderId(), order.getId())) {
            throw ServiceExceptionUtil.exception(ProductionErrorCodeConstants.PRODUCTION_PROGRESS_NOT_FOUND);
        }
        BigDecimal deliveryQty = safeDecimal(dto.getDeliveryQty());
        BigDecimal remaining = remainingDeliveryQty(progress);
        if (deliveryQty.compareTo(BigDecimal.ZERO) <= 0 || deliveryQty.compareTo(remaining) > 0) {
            throw ServiceExceptionUtil.exception(ProductionErrorCodeConstants.PRODUCTION_DELIVERY_QTY_INVALID);
        }
        BigDecimal delivered = safeDecimal(progress.getDeliveredQty()).add(deliveryQty).setScale(2, RoundingMode.HALF_UP);
        progress.setDeliveredQty(delivered);
        if (safeDecimal(progress.getProducedQty()).compareTo(delivered) < 0) {
            progress.setProducedQty(delivered);
        }
        progress.setRemark(trimToNull(dto.getRemark()));
        progress.setProgressStatus(resolveProgressStatus(progress));
        productionOrderProgressManager.saveOrUpdate(progress);
        recomputeMaster(order.getId());
        return orderDetail(order.getId());
    }

    @Override
    public byte[] buildExportExcel(Long id) {
        ProductionOrderVO order = orderDetail(id);
        try (Workbook workbook = loadProductionTemplateWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            List<ProductionOrderProgressVO> rows = order.getProgressRows() == null ? List.of() : order.getProgressRows();
            List<Sheet> sheets = prepareProductionSheets(workbook, Math.max(rows.size(), 1));
            OrdersDO sourceOrder = order.getOrderId() == null ? null : ordersManager.getById(order.getOrderId());

            if (rows.isEmpty()) {
                fillProductionSheet(workbook, sheets.get(0), order, sourceOrder, null, 0);
            }
            for (int i = 0; i < rows.size(); i++) {
                ProductionOrderProgressVO progress = rows.get(i);
                fillProductionSheet(workbook, sheets.get(i), order, sourceOrder, progress, i);
            }
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException ex) {
            throw new IllegalStateException("生产单导出失败", ex);
        }
    }

    private List<Sheet> prepareProductionSheets(Workbook workbook, int sheetCount) {
        if (workbook.getNumberOfSheets() == 0) {
            workbook.createSheet("生产单");
        }
        while (workbook.getNumberOfSheets() < sheetCount) {
            workbook.cloneSheet(0);
        }
        while (workbook.getNumberOfSheets() > sheetCount) {
            workbook.removeSheetAt(workbook.getNumberOfSheets() - 1);
        }
        List<Sheet> sheets = new ArrayList<>();
        Set<String> usedNames = new HashSet<>();
        for (int i = 0; i < sheetCount; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            sheet.setDisplayGridlines(false);
            sheets.add(sheet);
            workbook.setSheetName(i, safeSheetName("生产单" + (sheetCount > 1 ? "-" + (i + 1) : ""), usedNames));
        }
        return sheets;
    }

    private void fillProductionSheet(Workbook workbook, Sheet sheet, ProductionOrderVO order, OrdersDO sourceOrder,
                                     ProductionOrderProgressVO progress,
                                     int productIndex) {
        Map<String, Object> snapshot = progress == null ? Map.of() : parseSnapshot(progress.getSourceSnapshotJson());
        ExportMaterials materials = resolveExportMaterials(snapshot);
        CreationHelper creationHelper = workbook.getCreationHelper();
        Drawing<?> drawing = resetDrawing(sheet);

        writeTemplateCell(sheet, 1, 0, "单号：" + exportText(order.getCode()));
        writeTemplateCell(sheet, 1, 2, "品名: " + resolveExportProductName(progress, snapshot, productIndex));
        writeTemplateCell(sheet, 1, 5, "下单日期：" + formatChineseDate(resolveOrderDate(order, sourceOrder)));
        String productionQty = formatProductionQty(progress);
        writeTemplateCell(sheet, 2, 0, "数量:" + (StringUtils.hasText(productionQty) ? productionQty + "支" : ""));
        writeTemplateCell(sheet, 2, 2, "规格：" + resolveExportSpec(progress, snapshot));
        writeTemplateCell(sheet, 2, 5, "交货日期：" + formatChineseDate(order.getDeliveryDate()));

        writeTemplateCell(sheet, 4, 1, valueOrNone(firstText(materials.frame(), resolveExportProductName(progress, snapshot, productIndex))));
        writeTemplateCell(sheet, 5, 1, valueOrNone(materials.handle()));
        writeTemplateCell(sheet, 6, 1, valueOrNone(materials.cap()));
        writeTemplateCell(sheet, 7, 1, valueOrNone(materials.bead()));
        writeTemplateCell(sheet, 8, 1, valueOrNone(materials.fabric()));
        writeTemplateCell(sheet, 9, 1, valueOrNone(materials.strap()));
        writeTemplateCell(sheet, 10, 1, valueOrNone(materials.flower()));
        writeTemplateCell(sheet, 11, 1, valueOrNone(materials.topLabel()));
        writeTemplateCell(sheet, 12, 1, valueOrNone(summarizeSnapshotRows(snapshot.get("materialData"), "布标", "label")));
        writeTemplateCell(sheet, 13, 1, valueOrNone(summarizeSnapshotRows(snapshot.get("materialData"), "织标", "woven")));
        writeTemplateCell(sheet, 14, 1, valueOrNone(summarizeSnapshotRows(snapshot.get("materialData"), "吊牌", "吊卡", "tag")));
        writeTemplateCell(sheet, 15, 1, valueOrNone(materials.cover()));
        writeTemplateCell(sheet, 16, 1, valueOrNone(summarizeSnapshotRows(snapshot.get("materialData"), "关封")));
        writeTemplateCell(sheet, 17, 1, valueOrNone(resolvePackagingText(snapshot, materials, "袋", "bag", "OPP")));
        writeTemplateCell(sheet, 18, 1, valueOrNone(resolvePackagingText(snapshot, materials, "箱", "carton")));

        clearTemplateCell(sheet, 20, 0);
        writeTemplateCell(sheet, 21, 4, valueOrNone(sourceOrder == null ? "" : sourceOrder.getMarkingRemark()));
        writeTemplateCell(sheet, 32, 0, buildProductionRequirement(order, progress));
        writeTemplateCell(sheet, 32, 4, "无");
        writeTemplateCell(sheet, 43, 0, exportText(order.getOwnerName()));
        writeTemplateCell(sheet, 43, 2, "");
        writeTemplateCell(sheet, 43, 4, formatChineseDate(LocalDate.now()));

        insertImageIfPresent(workbook, creationHelper, drawing, PRODUCT_IMAGE_FIRST_ROW, PRODUCT_IMAGE_FIRST_COL,
                PRODUCT_IMAGE_LAST_ROW, PRODUCT_IMAGE_LAST_COL, resolveImageText(snapshot));
    }

    private Drawing<?> resetDrawing(Sheet sheet) {
        if (sheet instanceof HSSFSheet hssfSheet) {
            HSSFPatriarch patriarch = hssfSheet.getDrawingPatriarch();
            if (patriarch != null) {
                patriarch.clear();
            }
        }
        return sheet.createDrawingPatriarch();
    }

    private String safeSheetName(String value, Set<String> usedNames) {
        String base = WorkbookUtil.createSafeSheetName(firstText(value, "生产单"));
        if (base.length() > 31) {
            base = base.substring(0, 31);
        }
        String candidate = base;
        int index = 2;
        while (usedNames.contains(candidate)) {
            String suffix = "-" + index++;
            candidate = base.substring(0, Math.min(base.length(), 31 - suffix.length())) + suffix;
        }
        usedNames.add(candidate);
        return candidate;
    }

    private LocalDate resolveOrderDate(ProductionOrderVO order, OrdersDO sourceOrder) {
        if (sourceOrder != null && sourceOrder.getOrderDate() != null) {
            return sourceOrder.getOrderDate();
        }
        LocalDateTime createTime = order.getCreateTime();
        return createTime == null ? null : createTime.toLocalDate();
    }

    private String resolveExportProductName(ProductionOrderProgressVO progress, Map<String, Object> snapshot, int productIndex) {
        String description = snapshotText(snapshot, "description");
        return firstText(
                extractAfterLabel(description, "品名", "名称", "Name"),
                firstLine(description),
                progress == null ? "" : progress.getProductName(),
                progress == null ? "" : progress.getProductCode(),
                "产品" + (productIndex + 1));
    }

    private String resolveExportSpec(ProductionOrderProgressVO progress, Map<String, Object> snapshot) {
        String description = snapshotText(snapshot, "description");
        String structureSummary = snapshotText(snapshot, "structureSummary");
        return firstText(
                extractAfterLabel(description, "规格", "尺寸", "Size"),
                extractAfterLabel(structureSummary, "规格", "尺寸", "Size"),
                progress == null ? "" : progress.getProductCode());
    }

    private String formatProductionQty(ProductionOrderProgressVO progress) {
        if (progress == null) {
            return "";
        }
        return formatQty(resolveExportQty(progress));
    }

    private String resolvePackagingText(Map<String, Object> snapshot, ExportMaterials materials, String... keywords) {
        return firstText(
                summarizeSnapshotRows(snapshot.get("packagingData"), keywords),
                matchTextSegment(materials.packaging(), keywords));
    }

    private String buildColorAndPrintText(Map<String, Object> snapshot, ExportMaterials materials) {
        List<String> lines = new ArrayList<>();
        lines.add("颜色分配：" + valueOrNone(materials.fabric()));
        String printText = firstText(
                summarizeSnapshotRows(snapshot.get("printData")),
                snapshotText(snapshot, "printDescription"));
        lines.add(StringUtils.hasText(printText) ? "印刷：" + printText : "无印刷。");
        return String.join("\n", lines);
    }

    private String buildProductionRequirement(ProductionOrderVO order, ProductionOrderProgressVO progress) {
        return firstText(
                progress == null ? "" : progress.getRemark(),
                order.getRemark(),
                "按确认样生产；拉边、合片、缝伞、包装等按生产单要求执行。");
    }

    private String matchTextSegment(String text, String... keywords) {
        if (!StringUtils.hasText(text) || keywords == null || keywords.length == 0) {
            return "";
        }
        for (String segment : text.split("[；;\\n]")) {
            if (matchKeywords(segment, keywords)) {
                return segment.trim();
            }
        }
        return "";
    }

    private String extractAfterLabel(String source, String... labels) {
        if (!StringUtils.hasText(source) || labels == null || labels.length == 0) {
            return "";
        }
        for (String segment : source.split("[\\r\\n；;]")) {
            String trimmed = segment.trim();
            if (!StringUtils.hasText(trimmed)) {
                continue;
            }
            for (String label : labels) {
                if (!StringUtils.hasText(label)) {
                    continue;
                }
                int index = trimmed.toLowerCase(Locale.ROOT).indexOf(label.toLowerCase(Locale.ROOT));
                if (index < 0) {
                    continue;
                }
                int colon = firstColonIndex(trimmed, index + label.length());
                String value = colon >= 0 ? trimmed.substring(colon + 1) : trimmed.substring(index + label.length());
                return value.trim();
            }
        }
        return "";
    }

    private int firstColonIndex(String text, int fromIndex) {
        int chineseColon = text.indexOf('：', fromIndex);
        int colon = text.indexOf(':', fromIndex);
        if (chineseColon < 0) {
            return colon;
        }
        if (colon < 0) {
            return chineseColon;
        }
        return Math.min(chineseColon, colon);
    }

    private String valueOrNone(String value) {
        return StringUtils.hasText(value) ? value.trim() : "无";
    }

    private void arrangeBatch(ProductionOrderDO order, ProductionBatchItemDTO item) {
        if (item == null || item.getProgressId() == null) {
            throw ServiceExceptionUtil.exception(ProductionErrorCodeConstants.PRODUCTION_PROGRESS_NOT_FOUND);
        }
        ProductionOrderProgressDO progress = productionOrderProgressManager.getById(item.getProgressId());
        if (progress == null || !Objects.equals(progress.getProductionOrderId(), order.getId())) {
            throw ServiceExceptionUtil.exception(ProductionErrorCodeConstants.PRODUCTION_PROGRESS_NOT_FOUND);
        }
        ProductionGroupDO group = productionGroupManager.getById(item.getProductionGroupId());
        if (group == null || group.getStatus() == null || group.getStatus() != 1) {
            throw ServiceExceptionUtil.exception(ProductionErrorCodeConstants.PRODUCTION_GROUP_NOT_FOUND);
        }
        BigDecimal qty = safeDecimal(item.getBatchQty());
        BigDecimal available = availablePlanQty(progress);
        if (qty.compareTo(BigDecimal.ZERO) <= 0 || qty.compareTo(available) > 0) {
            throw ServiceExceptionUtil.exception(ProductionErrorCodeConstants.PRODUCTION_BATCH_QTY_INVALID);
        }
        ProductionOrderBatchDO batch = new ProductionOrderBatchDO();
        batch.setId(IdWorker.getId(batch));
        batch.setProductionOrderId(order.getId());
        batch.setProgressId(progress.getId());
        batch.setOrderId(order.getOrderId());
        batch.setOrderCode(order.getOrderCode());
        batch.setLineKey(progress.getLineKey());
        batch.setProductId(progress.getProductId());
        batch.setProductCode(progress.getProductCode());
        batch.setProductName(progress.getProductName());
        batch.setProductionGroupId(group.getId());
        batch.setProductionGroupName(group.getName());
        batch.setBatchQty(qty);
        batch.setPlannedDeliveryDate(item.getPlannedDeliveryDate());
        batch.setStatus(BATCH_STATUS_SCHEDULED);
        batch.setRemark(trimToNull(item.getRemark()));
        productionOrderBatchManager.save(batch);
        progress.setPlannedQty(sumBatchQty(productionOrderBatchManager.listByProgressId(progress.getId())));
        progress.setProgressStatus(resolveProgressStatus(progress));
        productionOrderProgressManager.saveOrUpdate(progress);
    }

    private void upsertProgressRows(ProductionOrderDO master, List<OrderProductSnapshotDTO> products) {
        int index = 0;
        for (OrderProductSnapshotDTO product : products) {
            if (product == null) {
                continue;
            }
            String lineKey = firstText(product.getLineKey(), "order-product-" + firstText(String.valueOf(product.getProductId()), "0")
                    + "-" + index + "-" + IdUtil.fastSimpleUUID());
            ProductionOrderProgressDO progress = productionOrderProgressManager.getByOrderLine(master.getId(), lineKey);
            if (progress == null) {
                progress = new ProductionOrderProgressDO();
                progress.setId(IdWorker.getId(progress));
                progress.setProductionOrderId(master.getId());
                progress.setLineKey(lineKey);
                progress.setPlannedQty(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
                progress.setPurchasedQty(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
                progress.setInboundQty(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
                progress.setProducedQty(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
                progress.setDeliveredQty(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            }
            progress.setOrderId(master.getOrderId());
            progress.setOrderCode(master.getOrderCode());
            progress.setProductId(product.getProductId());
            progress.setProductCode(trimToNull(product.getProductCode()));
            progress.setProductName(resolveProductName(product));
            progress.setOrderQty(safeDecimal(product.getQuantity()));
            progress.setSourceSnapshotJson(JSONUtil.toJsonStr(product));
            progress.setProgressStatus(resolveProgressStatus(progress));
            productionOrderProgressManager.saveOrUpdate(progress);
            index++;
        }
    }

    private void upsertManualProgressRows(ProductionOrderDO order, List<ProductionOrderProductDTO> products) {
        int index = 0;
        for (ProductionOrderProductDTO product : products) {
            if (product == null) {
                continue;
            }
            String lineKey = firstText(product.getLineKey(), "manual-product-" + firstText(String.valueOf(product.getProductId()), "0")
                    + "-" + index + "-" + IdUtil.fastSimpleUUID());
            ProductionOrderProgressDO progress = productionOrderProgressManager.getByOrderLine(order.getId(), lineKey);
            if (progress == null) {
                progress = new ProductionOrderProgressDO();
                progress.setId(IdWorker.getId(progress));
                progress.setProductionOrderId(order.getId());
                progress.setLineKey(lineKey);
                progress.setPlannedQty(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
                progress.setPurchasedQty(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
                progress.setInboundQty(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
                progress.setProducedQty(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
                progress.setDeliveredQty(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            }
            Map<String, Object> snapshot = parseSnapshot(product.getSourceSnapshotJson());
            progress.setOrderId(null);
            progress.setOrderCode(null);
            progress.setProductId(product.getProductId());
            progress.setProductCode(trimToNull(product.getProductCode()));
            progress.setProductName(resolveManualProductName(product, snapshot));
            progress.setOrderQty(safeDecimal(product.getQuantity()));
            progress.setSourceSnapshotJson(firstText(product.getSourceSnapshotJson(), JSONUtil.toJsonStr(product)));
            progress.setRemark(trimToNull(product.getRemark()));
            progress.setProgressStatus(resolveProgressStatus(progress));
            productionOrderProgressManager.saveOrUpdate(progress);
            index++;
        }
    }

    private String resolveManualProductName(ProductionOrderProductDTO product, Map<String, Object> snapshot) {
        return firstText(product.getProductName(),
                firstLine(snapshotText(snapshot, "descriptionEn")),
                firstLine(snapshotText(snapshot, "description")),
                product.getProductCode());
    }

    private void recomputeMaster(Long productionOrderId) {
        ProductionOrderDO order = productionOrderManager.getById(productionOrderId);
        if (order == null) {
            return;
        }
        List<ProductionOrderProgressDO> rows = productionOrderProgressManager.listByProductionOrderId(productionOrderId);
        if (rows.isEmpty()) {
            order.setStatus(STATUS_DRAFT);
        } else if (rows.stream().anyMatch(row -> PROGRESS_MANUAL_RECONCILE.equals(row.getProgressStatus()))) {
            order.setStatus(STATUS_MANUAL_RECONCILE);
        } else if (rows.stream().allMatch(row -> PROGRESS_COMPLETED.equals(row.getProgressStatus()))) {
            order.setStatus(STATUS_COMPLETED);
        } else if (rows.stream().anyMatch(this::hasProgressActivity)) {
            order.setStatus(STATUS_IN_PRODUCTION);
        } else {
            order.setStatus(STATUS_DRAFT);
        }
        productionOrderManager.saveOrUpdate(order);
    }

    private Map<String, List<PurchaseOrderItemDO>> loadActivePurchaseItemsByLine(Long orderId) {
        List<PurchaseOrderDO> purchaseOrders = purchaseOrderManager.listByOrderId(orderId).stream()
                .filter(order -> !PURCHASE_STATUS_CANCELLED.equals(order.getStatus()))
                .toList();
        if (purchaseOrders.isEmpty()) {
            return Map.of();
        }
        Set<Long> purchaseOrderIds = purchaseOrders.stream().map(PurchaseOrderDO::getId).collect(Collectors.toSet());
        return purchaseOrderItemManager.listByPurchaseOrderIds(new ArrayList<>(purchaseOrderIds)).stream()
                .filter(item -> item.getOrderId() != null)
                .filter(item -> StringUtils.hasText(item.getLineKey()))
                .collect(Collectors.groupingBy(item -> item.getLineKey().trim(), LinkedHashMap::new, Collectors.toList()));
    }

    private PurchaseCoverage calculateCoverage(List<PurchaseOrderItemDO> items) {
        if (items == null || items.isEmpty()) {
            return new PurchaseCoverage(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        }
        Map<String, List<PurchaseOrderItemDO>> byModule = items.stream()
                .filter(item -> StringUtils.hasText(item.getModuleKey()))
                .collect(Collectors.groupingBy(item -> item.getModuleKey().trim(), LinkedHashMap::new, Collectors.toList()));
        if (byModule.isEmpty()) {
            BigDecimal purchased = sum(items, PurchaseOrderItemDO::getBatchQty);
            BigDecimal inbound = sum(items, PurchaseOrderItemDO::getReceivedQty);
            return new PurchaseCoverage(purchased, inbound);
        }
        BigDecimal purchased = null;
        BigDecimal inbound = null;
        for (List<PurchaseOrderItemDO> moduleItems : byModule.values()) {
            BigDecimal modulePurchased = sum(moduleItems, PurchaseOrderItemDO::getBatchQty);
            BigDecimal moduleInbound = sum(moduleItems, PurchaseOrderItemDO::getReceivedQty);
            purchased = purchased == null ? modulePurchased : purchased.min(modulePurchased);
            inbound = inbound == null ? moduleInbound : inbound.min(moduleInbound);
        }
        return new PurchaseCoverage(defaultScale(purchased), defaultScale(inbound));
    }

    private ProductionOrderDO requireVisibleOrder(Long id) {
        Scope scope = currentScope(LoginUserInfoContext.requireLoginUserInfo());
        ProductionOrderDO order = productionOrderManager.getVisibleById(id, scope.userId(), scope.allVisible());
        if (order == null) {
            throw ServiceExceptionUtil.exception(ProductionErrorCodeConstants.PRODUCTION_ORDER_NOT_FOUND);
        }
        return order;
    }

    private ProductionGroupQueryCondition buildGroupCondition(ProductionGroupListQueryDTO query) {
        if (query == null) {
            query = new ProductionGroupListQueryDTO();
        }
        ProductionGroupQueryCondition condition = new ProductionGroupQueryCondition();
        condition.setPageNum(normalizePageNum(query.getPageNum()));
        condition.setPageSize(normalizePageSize(query.getPageSize()));
        condition.setKeyword(trimToNull(query.getKeyword()));
        condition.setStatus(query.getStatus());
        return condition;
    }

    private ProductionOrderQueryCondition buildOrderCondition(ProductionOrderListQueryDTO query) {
        if (query == null) {
            query = new ProductionOrderListQueryDTO();
        }
        Scope scope = currentScope(LoginUserInfoContext.requireLoginUserInfo());
        ProductionOrderQueryCondition condition = new ProductionOrderQueryCondition();
        condition.setPageNum(normalizePageNum(query.getPageNum()));
        condition.setPageSize(normalizePageSize(query.getPageSize()));
        condition.setKeyword(trimToNull(query.getKeyword()));
        condition.setStatus(normalizeOrderStatus(query.getStatus()));
        condition.setOrderId(query.getOrderId());
        condition.setProductionGroupId(query.getProductionGroupId());
        condition.setOwnerId(scope.userId());
        condition.setAllVisible(scope.allVisible());
        return condition;
    }

    private ProductionGroupVO toGroupVO(ProductionGroupDO group) {
        return BeanUtils.toBean(group, ProductionGroupVO.class);
    }

    private ProductionOrderVO buildVO(ProductionOrderDO order) {
        ProductionOrderVO vo = BeanUtils.toBean(order, ProductionOrderVO.class);
        List<ProductionOrderProgressDO> rows = productionOrderProgressManager.listByProductionOrderId(order.getId());
        List<ProductionOrderBatchDO> batches = productionOrderBatchManager.listByProductionOrderId(order.getId());
        vo.setProgressRows(rows.stream().map(row -> toProgressVO(row, hasPurchaseDependency(row, batches))).toList());
        vo.setBatches(batches.stream().map(this::toBatchVO).toList());
        vo.setTotalOrderQty(sumProgress(vo.getProgressRows(), ProductionOrderProgressVO::getOrderQty));
        vo.setTotalPlannedQty(sumProgress(vo.getProgressRows(), ProductionOrderProgressVO::getPlannedQty));
        vo.setTotalInboundQty(sumProgress(vo.getProgressRows(), ProductionOrderProgressVO::getInboundQty));
        vo.setTotalDeliveredQty(sumProgress(vo.getProgressRows(), ProductionOrderProgressVO::getDeliveredQty));
        vo.setTotalRemainingDeliveryQty(sumProgress(vo.getProgressRows(), ProductionOrderProgressVO::getRemainingDeliveryQty));
        return vo;
    }

    private boolean hasPurchaseDependency(ProductionOrderProgressDO row, List<ProductionOrderBatchDO> batches) {
        return safeDecimal(row.getPurchasedQty()).compareTo(BigDecimal.ZERO) > 0;
    }

    private ProductionOrderProgressVO toProgressVO(ProductionOrderProgressDO progress, boolean hasPurchaseDependency) {
        ProductionOrderProgressVO vo = BeanUtils.toBean(progress, ProductionOrderProgressVO.class);
        vo.setOrderQty(safeDecimal(progress.getOrderQty()));
        vo.setPlannedQty(safeDecimal(progress.getPlannedQty()));
        vo.setPurchasedQty(safeDecimal(progress.getPurchasedQty()));
        vo.setInboundQty(safeDecimal(progress.getInboundQty()));
        vo.setProducedQty(safeDecimal(progress.getProducedQty()));
        vo.setDeliveredQty(safeDecimal(progress.getDeliveredQty()));
        vo.setReleasedQty(releasedQty(progress, hasPurchaseDependency));
        vo.setAvailablePlanQty(availablePlanQty(progress));
        vo.setRemainingDeliveryQty(remainingDeliveryQty(progress));
        return vo;
    }

    private ProductionOrderBatchVO toBatchVO(ProductionOrderBatchDO batch) {
        return BeanUtils.toBean(batch, ProductionOrderBatchVO.class);
    }

    private String resolveProgressStatus(ProductionOrderProgressDO progress) {
        if (isProgressOverDemand(progress)) {
            return PROGRESS_MANUAL_RECONCILE;
        }
        if (safeDecimal(progress.getDeliveredQty()).compareTo(BigDecimal.ZERO) > 0
                && safeDecimal(progress.getDeliveredQty()).compareTo(safeDecimal(progress.getOrderQty())) >= 0) {
            return PROGRESS_COMPLETED;
        }
        if (safeDecimal(progress.getDeliveredQty()).compareTo(BigDecimal.ZERO) > 0) {
            return PROGRESS_DELIVERING;
        }
        if (safeDecimal(progress.getPlannedQty()).compareTo(BigDecimal.ZERO) > 0) {
            return PROGRESS_SCHEDULED;
        }
        if (safeDecimal(progress.getInboundQty()).compareTo(BigDecimal.ZERO) > 0
                || safeDecimal(progress.getPurchasedQty()).compareTo(BigDecimal.ZERO) > 0) {
            return PROGRESS_RELEASED;
        }
        return PROGRESS_PENDING;
    }

    private boolean isProgressOverDemand(ProductionOrderProgressDO progress) {
        BigDecimal orderQty = safeDecimal(progress.getOrderQty());
        BigDecimal purchasedQty = safeDecimal(progress.getPurchasedQty());
        BigDecimal inboundQty = safeDecimal(progress.getInboundQty());
        BigDecimal plannedQty = safeDecimal(progress.getPlannedQty());
        if (purchasedQty.compareTo(BigDecimal.ZERO) > 0 && plannedQty.compareTo(inboundQty) > 0) {
            return true;
        }
        return plannedQty.compareTo(orderQty) > 0
                || purchasedQty.compareTo(orderQty) > 0
                || inboundQty.compareTo(orderQty) > 0
                || safeDecimal(progress.getProducedQty()).compareTo(orderQty) > 0
                || safeDecimal(progress.getDeliveredQty()).compareTo(orderQty) > 0;
    }

    private boolean hasProgressActivity(ProductionOrderProgressDO progress) {
        return safeDecimal(progress.getPurchasedQty()).compareTo(BigDecimal.ZERO) > 0
                || safeDecimal(progress.getInboundQty()).compareTo(BigDecimal.ZERO) > 0
                || safeDecimal(progress.getPlannedQty()).compareTo(BigDecimal.ZERO) > 0
                || safeDecimal(progress.getProducedQty()).compareTo(BigDecimal.ZERO) > 0
                || safeDecimal(progress.getDeliveredQty()).compareTo(BigDecimal.ZERO) > 0;
    }

    private BigDecimal releasedQty(ProductionOrderProgressDO progress, boolean hasPurchaseDependency) {
        return hasPurchaseDependency ? safeDecimal(progress.getInboundQty()) : safeDecimal(progress.getOrderQty());
    }

    private BigDecimal availablePlanQty(ProductionOrderProgressDO progress) {
        boolean hasPurchaseDependency = safeDecimal(progress.getPurchasedQty()).compareTo(BigDecimal.ZERO) > 0;
        return releasedQty(progress, hasPurchaseDependency).subtract(safeDecimal(progress.getPlannedQty()))
                .max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal remainingDeliveryQty(ProductionOrderProgressDO progress) {
        return safeDecimal(progress.getOrderQty()).subtract(safeDecimal(progress.getDeliveredQty()))
                .max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal sumBatchQty(List<ProductionOrderBatchDO> batches) {
        return sum(batches == null ? List.of() : batches, ProductionOrderBatchDO::getBatchQty);
    }

    private <T> BigDecimal sum(List<T> items, Function<T, BigDecimal> getter) {
        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return items.stream().map(getter).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal sumProgress(List<ProductionOrderProgressVO> items, Function<ProductionOrderProgressVO, BigDecimal> getter) {
        return sum(items, getter);
    }

    private List<OrderProductSnapshotDTO> parseProducts(String json) {
        if (!StringUtils.hasText(json)) {
            return List.of();
        }
        return JSONUtil.toList(json, OrderProductSnapshotDTO.class);
    }

    private String resolveProductName(OrderProductSnapshotDTO product) {
        return firstText(firstLine(product.getDescriptionEn()), firstLine(product.getDescription()), product.getProductCode());
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseSnapshot(String json) {
        if (!StringUtils.hasText(json)) {
            return Map.of();
        }
        try {
            return JSONUtil.toBean(json, Map.class);
        } catch (Exception ignored) {
            return Map.of();
        }
    }

    private String snapshotText(Map<String, Object> snapshot, String key) {
        Object value = snapshot.get(key);
        if (value == null) {
            return "";
        }
        String text = String.valueOf(value);
        if (!StringUtils.hasText(text)) {
            return "";
        }
        return text.length() > 180 ? text.substring(0, 180) + "..." : text;
    }

    private String resolveImageText(Map<String, Object> snapshot) {
        return firstText(
                snapshotText(snapshot, "imageUrl"),
                firstImageUrl(snapshot.get("images")),
                firstImageUrl(snapshot.get("productSnapshot")));
    }

    private ExportMaterials resolveExportMaterials(Map<String, Object> snapshot) {
        String frame = summarizeSnapshotRows(snapshot.get("umbrellaFrameData"), this::frameRowText);
        String fabric = summarizeSnapshotRows(snapshot.get("fabricData"), this::fabricRowText);
        String handle = summarizeSnapshotRows(snapshot.get("materialData"), this::materialRowText,
                "伞头", "手柄", "handle");
        String cap = summarizeSnapshotRows(snapshot.get("materialData"), this::materialRowText,
                "伞尾", "伞帽", "cap", "tail");
        String bead = summarizeSnapshotRows(snapshot.get("materialData"), this::materialRowText,
                "伞珠", "bead");
        String flower = summarizeSnapshotRows(snapshot.get("materialData"), this::materialRowText,
                "帽花", "flower");
        String strap = summarizeSnapshotRows(snapshot.get("materialData"), this::materialRowText,
                "伞攀", "伞带", "strap");
        String topLabel = summarizeSnapshotRows(snapshot.get("materialData"), this::materialRowText,
                "顶标", "top label", "top");
        String cover = summarizeSnapshotRows(snapshot.get("materialData"), this::materialRowText,
                "布套", "伞套", "cover");
        String packaging = summarizeSnapshotRows(snapshot.get("packagingData"), this::packagingRowText);
        return new ExportMaterials(frame, fabric, handle, cap, bead, flower, strap, topLabel, cover, packaging);
    }

    private String summarizeSnapshotRows(Object source, String... keywords) {
        return summarizeSnapshotRows(source, this::snapshotRowText, keywords);
    }

    private String summarizeSnapshotRows(Object source, Function<JSONObject, String> mapper, String... keywords) {
        List<String> labels = objectList(source).stream()
                .filter(row -> keywords == null || keywords.length == 0 || matchKeywords(row.toString(), keywords))
                .map(mapper)
                .filter(StringUtils::hasText)
                .distinct()
                .toList();
        return String.join("；", labels);
    }

    private String snapshotRowText(JSONObject row) {
        return joinParts("，",
                fieldText(row, "name", "materialName", "fabricName", "typeName", "label"),
                fieldText(row, "size", "materialSize", "color", "spec", "model", "modelName"));
    }

    private String frameRowText(JSONObject row) {
        return joinParts("，",
                fieldText(row, "lengthName", "length", "size"),
                fieldText(row, "typeName", "type"),
                fieldText(row, "materialName", "material"),
                fieldText(row, "specificAttribute", "attribute", "property"),
                fieldText(row, "functionName", "function"),
                fieldText(row, "diameterName", "diameter"),
                fieldText(row, "ribCountName", "ribCount"));
    }

    private String materialRowText(JSONObject row) {
        String text = joinParts("，",
                fieldText(row, "name", "materialName", "label"),
                fieldText(row, "size", "materialSize", "color", "spec", "model"));
        return appendQtyIfNeeded(text, fieldText(row, "quantity", "materialQty", "count"), fieldText(row, "unit"));
    }

    private String fabricRowText(JSONObject row) {
        String text = joinParts("，",
                fieldText(row, "typeName", "fabricName", "name"),
                fieldText(row, "modelName", "fabricCode", "model"),
                fieldText(row, "widthName", "fabricWidth", "width"));
        return appendQtyIfNeeded(text, fieldText(row, "usage", "useQty", "quantity"), fieldText(row, "unit"));
    }

    private String packagingRowText(JSONObject row) {
        String boxCount = fieldText(row, "boxCount", "count", "packingQty");
        String boxCountText = StringUtils.hasText(boxCount) ? boxCount + "支/箱" : "";
        return joinParts("，",
                fieldText(row, "typeName", "packagingType", "type"),
                fieldText(row, "name", "materialName", "label"),
                fieldText(row, "size", "spec"),
                boxCountText);
    }

    private String firstImageUrl(Object source) {
        if (source == null) {
            return "";
        }
        if (source instanceof Iterable<?> iterable) {
            for (Object item : iterable) {
                String imageUrl = firstImageUrl(item);
                if (StringUtils.hasText(imageUrl)) {
                    return imageUrl;
                }
            }
            return "";
        }
        if (source instanceof CharSequence textSource) {
            String text = textSource.toString().trim();
            if (!StringUtils.hasText(text)) {
                return "";
            }
            try {
                if (text.startsWith("[")) {
                    return firstImageUrl(JSONUtil.parseArray(text));
                }
                if (text.startsWith("{")) {
                    return firstImageUrl(JSONUtil.parseObj(text));
                }
            } catch (Exception ignored) {
                return "";
            }
            return text.startsWith("http://") || text.startsWith("https://") ? text : "";
        }
        try {
            JSONObject json = source instanceof JSONObject object ? object : JSONUtil.parseObj(source);
            String directUrl = firstText(fieldText(json, "url"), fieldText(json, "imageUrl"));
            if (StringUtils.hasText(directUrl)) {
                return directUrl;
            }
            return firstText(firstImageUrl(json.get("images")), firstImageUrl(json.get("product")));
        } catch (Exception ignored) {
            return "";
        }
    }

    private String appendQtyIfNeeded(String text, String qty, String unit) {
        if (!StringUtils.hasText(text) || !StringUtils.hasText(qty)) {
            return text;
        }
        try {
            BigDecimal value = new BigDecimal(qty.trim());
            if (value.compareTo(BigDecimal.ONE) <= 0) {
                return text;
            }
            return text + " x" + value.stripTrailingZeros().toPlainString() + exportText(unit);
        } catch (Exception ignored) {
            return text;
        }
    }

    private String fieldText(JSONObject row, String... fields) {
        if (row == null || fields == null) {
            return "";
        }
        for (String field : fields) {
            Object value = row.get(field);
            if (value != null && StringUtils.hasText(String.valueOf(value))) {
                return String.valueOf(value).trim();
            }
        }
        return "";
    }

    private String joinParts(String delimiter, String... values) {
        List<String> parts = new ArrayList<>();
        for (String value : values) {
            if (!StringUtils.hasText(value)) {
                continue;
            }
            String text = value.trim();
            if (!parts.contains(text)) {
                parts.add(text);
            }
        }
        return String.join(delimiter, parts);
    }

    private List<JSONObject> objectList(Object source) {
        if (source == null) {
            return List.of();
        }
        try {
            if (source instanceof JSONArray array) {
                return jsonArrayToObjects(array);
            }
            if (source instanceof Iterable<?> iterable) {
                List<JSONObject> rows = new ArrayList<>();
                for (Object item : iterable) {
                    rows.add(JSONUtil.parseObj(item));
                }
                return rows;
            }
            if (source instanceof Map<?, ?> || source instanceof JSONObject) {
                return List.of(JSONUtil.parseObj(source));
            }
            String text = String.valueOf(source);
            if (!StringUtils.hasText(text)) {
                return List.of();
            }
            String trimmed = text.trim();
            if (trimmed.startsWith("[")) {
                return jsonArrayToObjects(JSONUtil.parseArray(trimmed));
            }
            if (trimmed.startsWith("{")) {
                return List.of(JSONUtil.parseObj(trimmed));
            }
            return List.of();
        } catch (Exception ignored) {
            return List.of();
        }
    }

    private List<JSONObject> jsonArrayToObjects(JSONArray array) {
        List<JSONObject> rows = new ArrayList<>();
        for (Object item : array) {
            rows.add(JSONUtil.parseObj(item));
        }
        return rows;
    }

    private boolean matchKeywords(String source, String... keywords) {
        if (!StringUtils.hasText(source) || keywords == null || keywords.length == 0) {
            return false;
        }
        String normalized = source.toLowerCase(Locale.ROOT);
        for (String keyword : keywords) {
            if (StringUtils.hasText(keyword) && normalized.contains(keyword.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    private BigDecimal resolveExportQty(ProductionOrderProgressVO progress) {
        if (safeDecimal(progress.getPlannedQty()).compareTo(BigDecimal.ZERO) > 0) {
            return progress.getPlannedQty();
        }
        return progress.getOrderQty();
    }

    private String buildGroupCode(Long id) {
        String suffix = String.valueOf(id);
        return "PG" + suffix.substring(Math.max(0, suffix.length() - 6));
    }

    private Integer normalizePageNum(Integer value) {
        return value == null || value <= 0 ? 1 : value;
    }

    private Integer normalizePageSize(Integer value) {
        if (value == null || value <= 0) {
            return 20;
        }
        return Math.min(value, 200);
    }

    private String normalizeOrderStatus(String status) {
        if (!StringUtils.hasText(status)) {
            return null;
        }
        String normalized = status.trim().toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case STATUS_DRAFT, STATUS_IN_PRODUCTION, STATUS_COMPLETED, STATUS_MANUAL_RECONCILE -> normalized;
            default -> null;
        };
    }

    private BigDecimal safeDecimal(BigDecimal value) {
        return value == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : value.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal defaultScale(BigDecimal value) {
        return value == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : value.setScale(2, RoundingMode.HALF_UP);
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private String firstText(String... values) {
        for (String value : values) {
            if (StringUtils.hasText(value)) {
                return value.trim();
            }
        }
        return "";
    }

    private String firstLine(String value) {
        if (!StringUtils.hasText(value)) {
            return "";
        }
        return value.strip().lines().findFirst().orElse("");
    }

    private String exportText(Object value) {
        if (value == null) {
            return "";
        }
        String text = String.valueOf(value);
        return StringUtils.hasText(text) ? text.trim() : "";
    }

    private String formatDate(LocalDate value) {
        return value == null ? "" : value.toString();
    }

    private String formatChineseDate(LocalDate value) {
        return value == null ? "" : value.getYear() + "." + value.getMonthValue() + "." + value.getDayOfMonth();
    }

    private String formatQty(BigDecimal value) {
        return safeDecimal(value).stripTrailingZeros().toPlainString();
    }

    private void insertImageIfPresent(Workbook workbook, CreationHelper creationHelper, Drawing<?> drawing,
                                      int firstRow, int firstCol, int lastRow, int lastCol, String imageUrl) {
        DownloadedImage image = downloadImage(imageUrl);
        if (image == null) {
            return;
        }
        int pictureIndex = workbook.addPicture(image.bytes(), image.pictureType());
        ClientAnchor anchor = createProductImageAnchor(workbook, creationHelper, firstRow, firstCol, lastRow, lastCol);
        drawing.createPicture(anchor, pictureIndex);
    }

    private ClientAnchor createProductImageAnchor(Workbook workbook, CreationHelper creationHelper,
                                                  int firstRow, int firstCol, int lastRow, int lastCol) {
        if (workbook instanceof HSSFWorkbook) {
            HSSFClientAnchor anchor = new HSSFClientAnchor();
            anchor.setCol1(firstCol);
            anchor.setRow1(firstRow);
            anchor.setCol2(lastCol + 1);
            anchor.setRow2(lastRow + 1);
            anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
            return anchor;
        }
        ClientAnchor anchor = creationHelper.createClientAnchor();
        anchor.setCol1(firstCol);
        anchor.setRow1(firstRow);
        anchor.setCol2(lastCol + 1);
        anchor.setRow2(lastRow + 1);
        anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
        return anchor;
    }

    private DownloadedImage downloadImage(String imageUrl) {
        if (!StringUtils.hasText(imageUrl)) {
            return null;
        }
        try {
            URI imageUri = createImageUri(imageUrl);
            if (imageUri == null || !isTrustedImageUri(imageUri)) {
                return null;
            }
            HttpURLConnection connection = (HttpURLConnection) imageUri.toURL().openConnection();
            connection.setInstanceFollowRedirects(false);
            connection.setConnectTimeout(IMAGE_CONNECT_TIMEOUT_MS);
            connection.setReadTimeout(IMAGE_READ_TIMEOUT_MS);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "image/*");

            int status = connection.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                return null;
            }

            String contentType = exportText(connection.getContentType()).toLowerCase(Locale.ROOT);
            if (!contentType.startsWith("image/")) {
                return null;
            }

            int contentLength = connection.getContentLength();
            if (contentLength > MAX_IMAGE_BYTES) {
                return null;
            }

            try (InputStream inputStream = connection.getInputStream();
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[8192];
                int total = 0;
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    total += read;
                    if (total > MAX_IMAGE_BYTES) {
                        return null;
                    }
                    outputStream.write(buffer, 0, read);
                }
                byte[] bytes = outputStream.toByteArray();
                if (bytes.length == 0) {
                    return null;
                }
                Integer pictureType = resolvePictureType(contentType, imageUri.toString());
                return pictureType == null ? null : new DownloadedImage(bytes, pictureType);
            } finally {
                connection.disconnect();
            }
        } catch (Exception ignored) {
            return null;
        }
    }

    private URI createImageUri(String imageUrl) {
        try {
            return URI.create(imageUrl.trim().replace(" ", "%20"));
        } catch (Exception ignored) {
            return null;
        }
    }

    private boolean isTrustedImageUri(URI imageUri) {
        if (!StringUtils.hasText(imageUri.getScheme()) || !StringUtils.hasText(imageUri.getHost())) {
            return false;
        }
        String scheme = imageUri.getScheme().toLowerCase(Locale.ROOT);
        if (!"http".equals(scheme) && !"https".equals(scheme)) {
            return false;
        }
        String host = imageUri.getHost().toLowerCase(Locale.ROOT);
        if (isBlockedHost(host)) {
            return false;
        }
        if (StringUtils.hasText(ossBaseUrl)) {
            try {
                URI baseUri = URI.create(ossBaseUrl.trim());
                return StringUtils.hasText(baseUri.getHost())
                        && Objects.equals(host, baseUri.getHost().toLowerCase(Locale.ROOT));
            } catch (Exception ignored) {
                return false;
            }
        }
        return ALIYUN_OSS_HOST_PATTERN.matcher(host).find();
    }

    private boolean isBlockedHost(String host) {
        if (!StringUtils.hasText(host)) {
            return true;
        }
        String normalized = host.toLowerCase(Locale.ROOT);
        if ("localhost".equals(normalized) || normalized.endsWith(".localhost") || normalized.endsWith(".local")) {
            return true;
        }
        if (IPV4_PATTERN.matcher(normalized).matches() || normalized.contains(":")) {
            try {
                InetAddress address = InetAddress.getByName(normalized);
                return address.isAnyLocalAddress()
                        || address.isLoopbackAddress()
                        || address.isLinkLocalAddress()
                        || address.isSiteLocalAddress()
                        || "169.254.169.254".equals(address.getHostAddress());
            } catch (Exception ignored) {
                return true;
            }
        }
        return false;
    }

    private Integer resolvePictureType(String contentType, String url) {
        String source = (exportText(contentType) + " " + exportText(url)).toLowerCase(Locale.ROOT);
        if (source.contains("png")) {
            return Workbook.PICTURE_TYPE_PNG;
        }
        if (source.contains("jpg") || source.contains("jpeg")) {
            return Workbook.PICTURE_TYPE_JPEG;
        }
        return null;
    }

    private Scope currentScope(LoginUserInfo loginUser) {
        boolean allVisible = loginUser.getPermissions() != null && loginUser.getPermissions().contains(ALL_PERMISSION);
        return new Scope(loginUser.getUserId(), allVisible);
    }

    private Workbook loadProductionTemplateWorkbook() throws IOException {
        ClassPathResource resource = new ClassPathResource(PRODUCTION_TEMPLATE_RESOURCE);
        if (resource.exists()) {
            try (InputStream inputStream = resource.getInputStream()) {
                return WorkbookFactory.create(inputStream);
            }
        }
        return new HSSFWorkbook();
    }

    private void writeTemplateCell(Sheet sheet, int rowIndex, int column, String value) {
        Row row = getOrCreateRow(sheet, rowIndex);
        Cell cell = row.getCell(column) == null ? row.createCell(column) : row.getCell(column);
        cell.setCellValue(value == null ? "" : value);
    }

    private void clearTemplateCell(Sheet sheet, int rowIndex, int column) {
        writeTemplateCell(sheet, rowIndex, column, "");
    }

    private Row getOrCreateRow(Sheet sheet, int rowIndex) {
        Row row = sheet.getRow(rowIndex);
        return row == null ? sheet.createRow(rowIndex) : row;
    }

    private record Scope(Long userId, boolean allVisible) {
    }

    private record DownloadedImage(byte[] bytes, int pictureType) {
    }

    private record ExportMaterials(
            String frame,
            String fabric,
            String handle,
            String cap,
            String bead,
            String flower,
            String strap,
            String topLabel,
            String cover,
            String packaging) {
    }

    private record PurchaseCoverage(BigDecimal purchasedQty, BigDecimal inboundQty) {
    }
}
