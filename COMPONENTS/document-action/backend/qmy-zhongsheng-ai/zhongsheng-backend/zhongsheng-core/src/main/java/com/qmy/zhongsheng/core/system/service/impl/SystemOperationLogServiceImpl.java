package com.qmy.zhongsheng.core.system.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.api.dto.system.SystemOperationLogQueryDTO;
import com.qmy.zhongsheng.core.document.manager.DocumentActionLogManager;
import com.qmy.zhongsheng.core.document.model.condition.DocumentActionLogQueryCondition;
import com.qmy.zhongsheng.core.document.model.entity.DocumentActionLogDO;
import com.qmy.zhongsheng.core.system.model.vo.SystemOperationLogVO;
import com.qmy.zhongsheng.core.system.service.SystemOperationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Locale;

/**
 * 系统操作日志服务实现。
 *
 * @author AI Coding
 */
@Service
@RequiredArgsConstructor
public class SystemOperationLogServiceImpl implements SystemOperationLogService {

    private final DocumentActionLogManager documentActionLogManager;

    @Override
    public Page<SystemOperationLogVO> page(SystemOperationLogQueryDTO query) {
        if (query == null) {
            query = new SystemOperationLogQueryDTO();
        }
        DocumentActionLogQueryCondition condition = new DocumentActionLogQueryCondition();
        condition.setPageNum(query.getPageNum());
        condition.setPageSize(query.getPageSize());
        condition.setDocumentType(normalize(query.getDocumentType()));
        condition.setDocumentTypes(resolveDocumentTypes(query.getModuleType(), condition.getDocumentType()));
        condition.setActionType(normalize(query.getActionType()));
        condition.setOperatorId(query.getOperatorId());
        condition.setOperatorKeyword(trimToNull(query.getOperatorKeyword()));
        condition.setKeyword(trimToNull(query.getKeyword()));
        condition.setStartTime(query.getStartTime());
        condition.setEndTime(query.getEndTime());

        Page<DocumentActionLogDO> doPage = documentActionLogManager.adminPage(condition);
        Page<SystemOperationLogVO> voPage = new Page<>(doPage.getCurrent(), doPage.getSize(), doPage.getTotal());
        voPage.setRecords(doPage.getRecords().stream().map(this::toVO).toList());
        return voPage;
    }

    private SystemOperationLogVO toVO(DocumentActionLogDO row) {
        String documentType = normalize(row.getDocumentType());
        SystemOperationLogVO vo = new SystemOperationLogVO();
        vo.setId(idText(row.getId()));
        vo.setModuleType(moduleType(documentType));
        vo.setModuleLabel(moduleLabel(vo.getModuleType()));
        vo.setDocumentType(documentType);
        vo.setDocumentTypeLabel(documentTypeLabel(documentType));
        vo.setDocumentId(idText(row.getDocumentId()));
        vo.setDocumentCode(firstText(row.getBaseCode(), row.getSerialCode(), row.getDocumentId()));
        vo.setActionType(row.getActionType());
        vo.setActionLabel(actionLabel(row.getActionType()));
        vo.setBeforeStatus(row.getBeforeStatus());
        vo.setAfterStatus(row.getAfterStatus());
        vo.setBeforeLockState(row.getBeforeLockState());
        vo.setAfterLockState(row.getAfterLockState());
        vo.setOperatorId(idText(row.getOperatorId()));
        vo.setOperatorName(firstText(row.getOperatorName(), "system"));
        vo.setActionReason(row.getActionReason());
        vo.setDiffSummary(row.getDiffSummary());
        vo.setDiffDetail(row.getDiffDetail());
        vo.setCreateTime(row.getCreateTime());
        return vo;
    }

    private List<String> resolveDocumentTypes(String moduleType, String documentType) {
        if (StringUtils.hasText(documentType)) {
            return null;
        }
        String normalized = normalize(moduleType);
        if ("sales".equals(normalized)) {
            return List.of("quote", "order");
        }
        if ("purchase".equals(normalized)) {
            return List.of("purchase");
        }
        if ("production".equals(normalized)) {
            return List.of("production");
        }
        return null;
    }

    private String moduleType(String documentType) {
        if (!StringUtils.hasText(documentType)) {
            return "other";
        }
        return switch (documentType) {
            case "quote", "order" -> "sales";
            case "purchase" -> "purchase";
            case "production" -> "production";
            default -> "other";
        };
    }

    private String moduleLabel(String moduleType) {
        return switch (moduleType) {
            case "sales" -> "销售管理";
            case "purchase" -> "采购管理";
            case "production" -> "生产管理";
            default -> "其他";
        };
    }

    private String documentTypeLabel(String documentType) {
        return switch (documentType) {
            case "quote" -> "报价单";
            case "order" -> "订单";
            case "purchase" -> "采购单";
            case "production" -> "生产总单";
            default -> firstText(documentType, "-");
        };
    }

    private String actionLabel(String actionType) {
        String normalized = normalize(actionType);
        if (!StringUtils.hasText(normalized)) {
            return "-";
        }
        return switch (normalized) {
            case "confirm" -> "确认";
            case "cancel" -> "取消";
            case "delete" -> "删除";
            case "convert_order" -> "转订单";
            case "create_from_quote" -> "报价转单";
            case "request_unlock" -> "申请解锁";
            case "warning_unlock" -> "解锁";
            case "approve_unlock" -> "同意解锁";
            case "reject_unlock" -> "拒绝解锁";
            case "update_after_unlock" -> "解锁后修改";
            case "adjust_inbound", "adjust_inbound_after_unlock" -> "入库调整";
            case "reconfirm" -> "重新确认";
            case "assign_owner" -> "改派负责人";
            case "sync_assign_owner" -> "同步改派负责人";
            case "inbound" -> "入库";
            case "complete" -> "完单";
            default -> firstText(actionType, "-");
        };
    }

    private String normalize(String value) {
        return StringUtils.hasText(value) ? value.trim().toLowerCase(Locale.ROOT) : null;
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private String firstText(Object... values) {
        if (values == null) {
            return null;
        }
        for (Object value : values) {
            if (value == null) {
                continue;
            }
            String text = String.valueOf(value);
            if (StringUtils.hasText(text)) {
                return text.trim();
            }
        }
        return null;
    }

    private String idText(Long id) {
        return id == null ? null : String.valueOf(id);
    }
}
