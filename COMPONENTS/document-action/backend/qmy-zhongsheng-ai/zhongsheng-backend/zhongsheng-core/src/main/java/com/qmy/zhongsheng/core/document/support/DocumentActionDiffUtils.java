package com.qmy.zhongsheng.core.document.support;

import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 公共单据动作 diff 文案工具。
 *
 * @author AI Coding
 */
public final class DocumentActionDiffUtils {

    private DocumentActionDiffUtils() {
    }

    public record DiffContent(String summary, String detail) {
    }

    public static DiffContent buildStatusLockDiff(String summary,
                                                  String beforeStatus,
                                                  String afterStatus,
                                                  String beforeLockState,
                                                  String afterLockState,
                                                  List<String> extraLines) {
        List<String> lines = new ArrayList<>();
        if (!sameText(beforeStatus, afterStatus)) {
            lines.add("状态：" + statusLabel(beforeStatus) + " -> " + statusLabel(afterStatus));
        }
        if (!sameText(beforeLockState, afterLockState)) {
            lines.add("锁定：" + lockLabel(beforeLockState) + " -> " + lockLabel(afterLockState));
        }
        if (extraLines != null) {
            extraLines.stream().filter(StringUtils::hasText).forEach(lines::add);
        }
        return build(summary, lines);
    }

    public static DiffContent buildConfirmDiff(String documentLabel,
                                               String code,
                                               String customerName,
                                               String ownerName,
                                               Integer productCount,
                                               BigDecimal totalAmount) {
        List<String> lines = new ArrayList<>();
        lines.add("单据号：" + display(code));
        lines.add("客户：" + display(customerName));
        lines.add("负责人：" + display(ownerName));
        lines.add("产品行数：" + (productCount == null ? 0 : productCount));
        lines.add("总金额：" + money(totalAmount));
        return build("确认" + documentLabel, lines);
    }

    public static DiffContent buildConvertedDiff(String quoteCode, String orderCode, Long orderId) {
        List<String> lines = new ArrayList<>();
        lines.add("报价号：" + display(quoteCode));
        lines.add("订单号：" + display(orderCode));
        lines.add("订单 ID：" + display(orderId));
        return build("报价转订单", lines);
    }

    public static DiffContent buildUpdateAfterUnlockDiff(String documentLabel,
                                                         String beforeCustomer,
                                                         String afterCustomer,
                                                         BigDecimal beforeTotal,
                                                         BigDecimal afterTotal,
                                                         Integer beforeProductCount,
                                                         Integer afterProductCount) {
        return buildUpdateAfterUnlockDiff(documentLabel, beforeCustomer, afterCustomer, beforeTotal, afterTotal,
                beforeProductCount, afterProductCount, null);
    }

    public static DiffContent buildUpdateAfterUnlockDiff(String documentLabel,
                                                         String beforeCustomer,
                                                         String afterCustomer,
                                                         BigDecimal beforeTotal,
                                                         BigDecimal afterTotal,
                                                         Integer beforeProductCount,
                                                         Integer afterProductCount,
                                                         List<String> extraLines) {
        List<String> lines = new ArrayList<>();
        if (!sameText(beforeCustomer, afterCustomer)) {
            lines.add("客户：" + display(beforeCustomer) + " -> " + display(afterCustomer));
        }
        if (!sameNumber(beforeTotal, afterTotal)) {
            lines.add("总金额：" + money(beforeTotal) + " -> " + money(afterTotal));
        }
        if ((beforeProductCount == null ? 0 : beforeProductCount) != (afterProductCount == null ? 0 : afterProductCount)) {
            lines.add("产品行数：" + (beforeProductCount == null ? 0 : beforeProductCount)
                    + " -> " + (afterProductCount == null ? 0 : afterProductCount));
        }
        if (extraLines != null) {
            extraLines.stream().filter(StringUtils::hasText).forEach(lines::add);
        }
        if (lines.isEmpty()) {
            lines.add("已保存解锁后的字段调整");
        }
        return build("解锁后更新" + documentLabel, lines);
    }

    public static DiffContent buildOwnerAssignmentDiff(String beforeOwnerName,
                                                       String afterOwnerName,
                                                       boolean synced,
                                                       String reason) {
        List<String> lines = new ArrayList<>();
        lines.add("负责人：" + display(beforeOwnerName) + " -> " + display(afterOwnerName));
        if (StringUtils.hasText(reason)) {
            lines.add("原因：" + reason.trim());
        }
        if (synced) {
            lines.add("同步改派关联单据");
        }
        return build(synced ? "同步改派负责人" : "改派负责人", lines);
    }

    public static DiffContent build(String summary, List<String> detailLines) {
        List<String> lines = detailLines == null ? List.of() : detailLines.stream()
                .filter(StringUtils::hasText)
                .toList();
        return new DiffContent(summary, String.join("\n", lines));
    }

    public static String buildReconfirmScopeJson(String... scopeLabels) {
        List<String> labels = new ArrayList<>();
        if (scopeLabels != null) {
            for (String label : scopeLabels) {
                if (StringUtils.hasText(label)) {
                    labels.add(label.trim());
                }
            }
        }
        if (labels.isEmpty()) {
            labels.add("核心字段");
        }
        return "[\"" + String.join("\",\"", labels) + "\"]";
    }

    private static boolean sameText(String left, String right) {
        return display(left).equals(display(right));
    }

    private static boolean sameNumber(BigDecimal left, BigDecimal right) {
        BigDecimal safeLeft = left == null ? BigDecimal.ZERO : left;
        BigDecimal safeRight = right == null ? BigDecimal.ZERO : right;
        return safeLeft.compareTo(safeRight) == 0;
    }

    private static String statusLabel(String status) {
        return switch (display(status).toLowerCase(Locale.ROOT)) {
            case "draft" -> "草稿";
            case "confirmed" -> "已确认";
            case "converted" -> "已转订单";
            case "purchasing" -> "采购中";
            case "completed" -> "已完成";
            case "cancelled" -> "已取消";
            default -> display(status);
        };
    }

    private static String lockLabel(String lockState) {
        return switch (display(lockState).toLowerCase(Locale.ROOT)) {
            case "open" -> "未锁定";
            case "locked" -> "已锁定";
            case "pending_unlock" -> "待解锁审批";
            case "temporary_unlocked" -> "已解锁";
            default -> display(lockState);
        };
    }

    private static String display(Object value) {
        if (value == null) {
            return "-";
        }
        String text = String.valueOf(value);
        return StringUtils.hasText(text) ? text.trim() : "-";
    }

    private static String money(BigDecimal value) {
        return value == null ? "0.00" : value.stripTrailingZeros().toPlainString();
    }
}
