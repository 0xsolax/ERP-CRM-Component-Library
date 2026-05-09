package com.qmy.zhongsheng.core.document.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.qmy.zhongsheng.core.order.dao.OrdersDAO;
import com.qmy.zhongsheng.core.order.model.entity.OrdersDO;
import com.qmy.zhongsheng.core.production.dao.ProductionOrderDAO;
import com.qmy.zhongsheng.core.production.model.entity.ProductionOrderDO;
import com.qmy.zhongsheng.core.purchase.dao.PurchaseOrderDAO;
import com.qmy.zhongsheng.core.purchase.model.entity.PurchaseOrderDO;
import com.qmy.zhongsheng.core.quote.dao.QuoteDAO;
import com.qmy.zhongsheng.core.quote.model.entity.QuoteDO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * 销售到履约主链路的统一业务单号服务。
 *
 * <p>对用户展示的 {@code code/base_code} 在同一业务链路中保持一致，
 * {@code serial_code} 只用于内部区分单据阶段与同阶段多张单据。</p>
 *
 * @author AI Coding
 */
@Component
@RequiredArgsConstructor
public class BusinessDocumentCodeService {

    public static final String DOCUMENT_QUOTE = "quote";
    public static final String DOCUMENT_ORDER = "order";
    public static final String DOCUMENT_PRODUCTION = "production";
    public static final String DOCUMENT_PURCHASE = "purchase";

    public static final int STAGE_QUOTE = 0;
    public static final int STAGE_ORDER = 1;
    public static final int STAGE_PRODUCTION = 2;
    public static final int STAGE_PURCHASE = 3;

    private static final DateTimeFormatter BASE_CODE_DATE = DateTimeFormatter.BASIC_ISO_DATE;

    private final QuoteDAO quoteDAO;
    private final OrdersDAO ordersDAO;
    private final ProductionOrderDAO productionOrderDAO;
    private final PurchaseOrderDAO purchaseOrderDAO;

    public String generateBaseCode() {
        for (int i = 0; i < 10; i++) {
            String candidate = rawBaseCode();
            if (!activeBaseCodeExists(candidate, null, null)) {
                return candidate;
            }
        }
        throw new IllegalStateException("业务主单号生成失败，请稍后重试");
    }

    public String buildSerialCode(String baseCode, int stageNo) {
        return buildSerialCode(baseCode, stageNo, 1);
    }

    public String buildSerialCode(String baseCode, int stageNo, int sequenceNo) {
        if (!StringUtils.hasText(baseCode)) {
            throw new IllegalArgumentException("业务主单号不能为空");
        }
        String normalizedBase = baseCode.trim();
        String serialCode = normalizedBase + "_" + stageNo;
        return sequenceNo <= 1 ? serialCode : serialCode + "_" + sequenceNo;
    }

    public String quoteSerialCode(String baseCode) {
        return buildSerialCode(baseCode, STAGE_QUOTE);
    }

    public String orderSerialCode(String baseCode) {
        return buildSerialCode(baseCode, STAGE_ORDER);
    }

    public String nextProductionSerialCode(String baseCode, Long excludeId) {
        List<String> serialCodes = productionOrderDAO.selectList(Wrappers.<ProductionOrderDO>lambdaQuery()
                        .eq(ProductionOrderDO::getIsDeleted, 0)
                        .eq(ProductionOrderDO::getBaseCode, baseCode))
                .stream()
                .filter(row -> excludeId == null || !excludeId.equals(row.getId()))
                .map(ProductionOrderDO::getSerialCode)
                .toList();
        return nextSerialCode(baseCode, STAGE_PRODUCTION, serialCodes);
    }

    public String nextPurchaseSerialCode(String baseCode, Long excludeId) {
        List<String> serialCodes = purchaseOrderDAO.selectList(Wrappers.<PurchaseOrderDO>lambdaQuery()
                        .eq(PurchaseOrderDO::getIsDeleted, 0)
                        .eq(PurchaseOrderDO::getBaseCode, baseCode))
                .stream()
                .filter(row -> excludeId == null || !excludeId.equals(row.getId()))
                .map(PurchaseOrderDO::getSerialCode)
                .toList();
        return nextSerialCode(baseCode, STAGE_PURCHASE, serialCodes);
    }

    public String firstTextOrGenerate(String... candidates) {
        String value = firstText(candidates);
        return StringUtils.hasText(value) ? value : generateBaseCode();
    }

    public String firstText(String... candidates) {
        if (candidates == null) {
            return null;
        }
        for (String candidate : candidates) {
            if (StringUtils.hasText(candidate)) {
                return candidate.trim();
            }
        }
        return null;
    }

    public boolean activeBaseCodeExists(String baseCode, String selfDocumentType, Long selfDocumentId) {
        if (!StringUtils.hasText(baseCode)) {
            return false;
        }
        String normalized = baseCode.trim();
        if (existsQuote(normalized, selfDocumentType, selfDocumentId)) {
            return true;
        }
        if (existsOrder(normalized, selfDocumentType, selfDocumentId)) {
            return true;
        }
        if (existsProduction(normalized, selfDocumentType, selfDocumentId)) {
            return true;
        }
        return existsPurchase(normalized, selfDocumentType, selfDocumentId);
    }

    private String rawBaseCode() {
        String random = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase(Locale.ROOT);
        return "DOC" + LocalDate.now().format(BASE_CODE_DATE) + random;
    }

    private String nextSerialCode(String baseCode, int stageNo, List<String> existingSerialCodes) {
        int nextSequence = 1;
        for (String serialCode : existingSerialCodes) {
            Integer sequence = parseStageSequence(serialCode, baseCode, stageNo);
            if (sequence != null) {
                nextSequence = Math.max(nextSequence, sequence + 1);
            }
        }
        return buildSerialCode(baseCode, stageNo, nextSequence);
    }

    private Integer parseStageSequence(String serialCode, String baseCode, int stageNo) {
        if (!StringUtils.hasText(serialCode) || !StringUtils.hasText(baseCode)) {
            return null;
        }
        String prefix = baseCode.trim() + "_" + stageNo;
        String normalized = serialCode.trim();
        if (prefix.equals(normalized)) {
            return 1;
        }
        String sequencePrefix = prefix + "_";
        if (!normalized.startsWith(sequencePrefix)) {
            return null;
        }
        try {
            int sequence = Integer.parseInt(normalized.substring(sequencePrefix.length()));
            return sequence > 1 ? sequence : null;
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private boolean existsQuote(String baseCode, String selfDocumentType, Long selfDocumentId) {
        Long count = quoteDAO.selectCount(Wrappers.<QuoteDO>lambdaQuery()
                .eq(QuoteDO::getIsDeleted, 0)
                .and(w -> w.eq(QuoteDO::getBaseCode, baseCode).or().eq(QuoteDO::getCode, baseCode))
                .ne(DOCUMENT_QUOTE.equals(selfDocumentType) && selfDocumentId != null, QuoteDO::getId, selfDocumentId));
        return count != null && count > 0;
    }

    private boolean existsOrder(String baseCode, String selfDocumentType, Long selfDocumentId) {
        Long count = ordersDAO.selectCount(Wrappers.<OrdersDO>lambdaQuery()
                .eq(OrdersDO::getIsDeleted, 0)
                .and(w -> w.eq(OrdersDO::getBaseCode, baseCode).or().eq(OrdersDO::getCode, baseCode))
                .ne(DOCUMENT_ORDER.equals(selfDocumentType) && selfDocumentId != null, OrdersDO::getId, selfDocumentId));
        return count != null && count > 0;
    }

    private boolean existsProduction(String baseCode, String selfDocumentType, Long selfDocumentId) {
        Long count = productionOrderDAO.selectCount(Wrappers.<ProductionOrderDO>lambdaQuery()
                .eq(ProductionOrderDO::getIsDeleted, 0)
                .and(w -> w.eq(ProductionOrderDO::getBaseCode, baseCode).or().eq(ProductionOrderDO::getCode, baseCode))
                .ne(DOCUMENT_PRODUCTION.equals(selfDocumentType) && selfDocumentId != null,
                        ProductionOrderDO::getId, selfDocumentId));
        return count != null && count > 0;
    }

    private boolean existsPurchase(String baseCode, String selfDocumentType, Long selfDocumentId) {
        Long count = purchaseOrderDAO.selectCount(Wrappers.<PurchaseOrderDO>lambdaQuery()
                .eq(PurchaseOrderDO::getIsDeleted, 0)
                .and(w -> w.eq(PurchaseOrderDO::getBaseCode, baseCode).or().eq(PurchaseOrderDO::getCode, baseCode))
                .ne(DOCUMENT_PURCHASE.equals(selfDocumentType) && selfDocumentId != null,
                        PurchaseOrderDO::getId, selfDocumentId));
        return count != null && count > 0;
    }
}
