package com.qmy.zhongsheng.core.document.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.api.dto.document.DocumentActionLogQueryDTO;
import com.qmy.zhongsheng.api.dto.document.DocumentOwnerAssignDTO;
import com.qmy.zhongsheng.api.dto.document.DocumentUnlockActionDTO;
import com.qmy.zhongsheng.common.constants.ApiPermissionConstants;
import com.qmy.zhongsheng.common.context.LoginUserInfoContext;
import com.qmy.zhongsheng.common.error.DocumentErrorCodeConstants;
import com.qmy.zhongsheng.common.exception.ServiceExceptionUtil;
import com.qmy.zhongsheng.common.login.LoginUserInfo;
import com.qmy.zhongsheng.common.utils.BeanUtils;
import com.qmy.zhongsheng.core.document.manager.DocumentActionLogManager;
import com.qmy.zhongsheng.core.document.manager.DocumentUnlockRequestManager;
import com.qmy.zhongsheng.core.document.model.command.DocumentActionLogCreateCommand;
import com.qmy.zhongsheng.core.document.model.condition.DocumentActionLogQueryCondition;
import com.qmy.zhongsheng.core.document.model.entity.DocumentActionLogDO;
import com.qmy.zhongsheng.core.document.model.entity.DocumentUnlockRequestDO;
import com.qmy.zhongsheng.core.document.model.vo.DocumentActionLogVO;
import com.qmy.zhongsheng.core.document.model.vo.DocumentOwnerAssignResultVO;
import com.qmy.zhongsheng.core.document.model.vo.DocumentUnlockResultVO;
import com.qmy.zhongsheng.core.document.service.DocumentActionService;
import com.qmy.zhongsheng.core.document.support.DocumentActionDiffUtils;
import com.qmy.zhongsheng.core.order.manager.OrdersManager;
import com.qmy.zhongsheng.core.order.model.entity.OrdersDO;
import com.qmy.zhongsheng.core.purchase.manager.PurchaseOrderManager;
import com.qmy.zhongsheng.core.purchase.model.entity.PurchaseOrderDO;
import com.qmy.zhongsheng.core.production.manager.ProductionOrderManager;
import com.qmy.zhongsheng.core.production.model.entity.ProductionOrderDO;
import com.qmy.zhongsheng.core.quote.manager.QuoteManager;
import com.qmy.zhongsheng.core.quote.model.entity.QuoteDO;
import com.qmy.zhongsheng.core.user.manager.UserManager;
import com.qmy.zhongsheng.core.user.model.entity.UserDO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * 公共单据状态动作服务实现。
 *
 * @author AI Coding
 */
@Service
@RequiredArgsConstructor
public class DocumentActionServiceImpl implements DocumentActionService {

    public static final String TYPE_QUOTE = "quote";
    public static final String TYPE_ORDER = "order";
    public static final String TYPE_PURCHASE = "purchase";
    public static final String TYPE_PRODUCTION = "production";

    public static final String LOCK_OPEN = "open";
    public static final String LOCK_LOCKED = "locked";
    public static final String LOCK_PENDING_UNLOCK = "pending_unlock";
    public static final String LOCK_TEMPORARY_UNLOCKED = "temporary_unlocked";

    private static final String REQUEST_TYPE_USER = "request";
    private static final String REQUEST_TYPE_WARNING = "warning";
    private static final String REQUEST_STATUS_PENDING = "pending";
    private static final String REQUEST_STATUS_APPROVED = "approved";
    private static final String REQUEST_STATUS_REJECTED = "rejected";
    private static final String APPROVER_ROLE_ADMIN = "document_admin";
    private static final String ALL_PERMISSION = "*";

    private final DocumentActionLogManager documentActionLogManager;

    private final DocumentUnlockRequestManager documentUnlockRequestManager;

    private final QuoteManager quoteManager;

    private final OrdersManager ordersManager;

    private final PurchaseOrderManager purchaseOrderManager;

    private final ProductionOrderManager productionOrderManager;

    private final UserManager userManager;

    @Override
    public void recordAction(DocumentActionLogCreateCommand command) {
        if (command == null || command.getDocumentId() == null || !StringUtils.hasText(command.getDocumentType())) {
            return;
        }
        LoginUserInfo loginUser = LoginUserInfoContext.getLoginUserInfo();
        DocumentActionLogDO log = new DocumentActionLogDO();
        log.setDocumentType(normalizeDocumentType(command.getDocumentType()));
        log.setDocumentId(command.getDocumentId());
        log.setBaseCode(trimToNull(command.getBaseCode()));
        log.setSerialCode(trimToNull(command.getSerialCode()));
        log.setActionType(trimToNull(command.getActionType()));
        log.setBeforeStatus(trimToNull(command.getBeforeStatus()));
        log.setAfterStatus(trimToNull(command.getAfterStatus()));
        log.setBeforeLockState(trimToNull(command.getBeforeLockState()));
        log.setAfterLockState(trimToNull(command.getAfterLockState()));
        log.setOperatorId(loginUser == null ? -1L : loginUser.getUserId());
        log.setOperatorName(loginUser == null ? "system" : loginUser.getUserName());
        log.setActionReason(trimToNull(command.getActionReason()));
        log.setDiffSummary(trimToNull(command.getDiffSummary()));
        log.setDiffDetail(trimToNull(command.getDiffDetail()));
        documentActionLogManager.save(log);
    }

    @Override
    public Page<DocumentActionLogVO> pageLogs(DocumentActionLogQueryDTO query) {
        String documentType = normalizeDocumentType(query.getDocumentType());
        requireDocument(documentType, query.getDocumentId(), currentScope(false));
        DocumentActionLogQueryCondition condition = new DocumentActionLogQueryCondition();
        condition.setPageNum(query.getPageNum());
        condition.setPageSize(query.getPageSize());
        condition.setDocumentType(documentType);
        condition.setDocumentId(query.getDocumentId());
        Page<DocumentActionLogDO> doPage = documentActionLogManager.page(condition);
        Page<DocumentActionLogVO> voPage = new Page<>(doPage.getCurrent(), doPage.getSize(), doPage.getTotal());
        voPage.setRecords(BeanUtils.toBean(doPage.getRecords(), DocumentActionLogVO.class));
        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DocumentUnlockResultVO requestUnlock(DocumentUnlockActionDTO dto) {
        String reason = normalizeReason(dto.getReason(), true);
        DocumentRecord document = requireDocument(normalizeDocumentType(dto.getDocumentType()), dto.getDocumentId(), currentScope(false));
        if (LOCK_OPEN.equals(document.lockState()) || LOCK_TEMPORARY_UNLOCKED.equals(document.lockState())) {
            throw ServiceExceptionUtil.exception(DocumentErrorCodeConstants.DOCUMENT_NOT_LOCKED);
        }
        if (documentUnlockRequestManager.getLatestPending(document.type(), document.id()) != null) {
            throw ServiceExceptionUtil.exception(DocumentErrorCodeConstants.DOCUMENT_UNLOCK_ALREADY_PENDING);
        }
        String beforeLock = document.lockState();
        DocumentUnlockRequestDO request = createUnlockRequest(document, REQUEST_TYPE_USER, REQUEST_STATUS_PENDING, reason);
        DocumentRecord latest = saveDocumentState(document, LOCK_PENDING_UNLOCK, document.needsReconfirm(), document.reconfirmScopeJson());
        DocumentActionDiffUtils.DiffContent diff = DocumentActionDiffUtils.buildStatusLockDiff(
                "申请解锁", document.status(), latest.status(), beforeLock, latest.lockState(), List.of("原因：" + reason));
        writeDocumentLog(latest, "request_unlock", document.status(), latest.status(), beforeLock, latest.lockState(), reason, diff);
        return buildUnlockResult(latest, request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DocumentUnlockResultVO warningUnlock(DocumentUnlockActionDTO dto) {
        String reason = normalizeReason(dto.getReason(), true);
        DocumentRecord document = requireDocument(normalizeDocumentType(dto.getDocumentType()), dto.getDocumentId(), currentScope(true));
        if (LOCK_OPEN.equals(document.lockState()) || LOCK_TEMPORARY_UNLOCKED.equals(document.lockState())) {
            throw ServiceExceptionUtil.exception(DocumentErrorCodeConstants.DOCUMENT_NOT_LOCKED);
        }
        String beforeLock = document.lockState();
        DocumentUnlockRequestDO request = createUnlockRequest(document, REQUEST_TYPE_WARNING, REQUEST_STATUS_APPROVED, reason);
        DocumentRecord latest = saveDocumentState(document, LOCK_TEMPORARY_UNLOCKED, Boolean.TRUE,
                DocumentActionDiffUtils.buildReconfirmScopeJson("核心字段", "产品快照"));
        DocumentActionDiffUtils.DiffContent diff = DocumentActionDiffUtils.buildStatusLockDiff(
                "解锁", document.status(), latest.status(), beforeLock, latest.lockState(), List.of("原因：" + reason));
        writeDocumentLog(latest, "warning_unlock", document.status(), latest.status(), beforeLock, latest.lockState(), reason, diff);
        return buildUnlockResult(latest, request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DocumentUnlockResultVO approveUnlock(DocumentUnlockActionDTO dto) {
        DocumentRecord document = requireDocument(normalizeDocumentType(dto.getDocumentType()), dto.getDocumentId(), currentScope(true));
        DocumentUnlockRequestDO pending = requirePendingUnlock(document);
        LoginUserInfo operator = LoginUserInfoContext.requireLoginUserInfo();
        pending.setRequestStatus(REQUEST_STATUS_APPROVED);
        pending.setApproverId(operator.getUserId());
        pending.setApproverName(operator.getUserName());
        pending.setDecisionRemark(trimToNull(dto.getReason()));
        pending.setProcessedAt(LocalDateTime.now());
        documentUnlockRequestManager.update(pending);

        String beforeLock = document.lockState();
        DocumentRecord latest = saveDocumentState(document, LOCK_TEMPORARY_UNLOCKED, Boolean.TRUE,
                DocumentActionDiffUtils.buildReconfirmScopeJson("核心字段", "产品快照"));
        DocumentActionDiffUtils.DiffContent diff = DocumentActionDiffUtils.buildStatusLockDiff(
                "同意解锁", document.status(), latest.status(), beforeLock, latest.lockState(),
                List.of("审批意见：" + display(dto.getReason())));
        writeDocumentLog(latest, "approve_unlock", document.status(), latest.status(), beforeLock, latest.lockState(),
                trimToNull(dto.getReason()), diff);
        return buildUnlockResult(latest, pending);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DocumentUnlockResultVO rejectUnlock(DocumentUnlockActionDTO dto) {
        DocumentRecord document = requireDocument(normalizeDocumentType(dto.getDocumentType()), dto.getDocumentId(), currentScope(true));
        DocumentUnlockRequestDO pending = requirePendingUnlock(document);
        LoginUserInfo operator = LoginUserInfoContext.requireLoginUserInfo();
        pending.setRequestStatus(REQUEST_STATUS_REJECTED);
        pending.setApproverId(operator.getUserId());
        pending.setApproverName(operator.getUserName());
        pending.setDecisionRemark(trimToNull(dto.getReason()));
        pending.setProcessedAt(LocalDateTime.now());
        documentUnlockRequestManager.update(pending);

        String beforeLock = document.lockState();
        DocumentRecord latest = saveDocumentState(document, LOCK_LOCKED, document.needsReconfirm(), document.reconfirmScopeJson());
        DocumentActionDiffUtils.DiffContent diff = DocumentActionDiffUtils.buildStatusLockDiff(
                "拒绝解锁", document.status(), latest.status(), beforeLock, latest.lockState(),
                List.of("审批意见：" + display(dto.getReason())));
        writeDocumentLog(latest, "reject_unlock", document.status(), latest.status(), beforeLock, latest.lockState(),
                trimToNull(dto.getReason()), diff);
        return buildUnlockResult(latest, pending);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DocumentUnlockResultVO reconfirm(DocumentUnlockActionDTO dto) {
        DocumentRecord document = requireDocument(normalizeDocumentType(dto.getDocumentType()), dto.getDocumentId(), currentScope(false));
        if (!Boolean.TRUE.equals(document.needsReconfirm()) && !LOCK_TEMPORARY_UNLOCKED.equals(document.lockState())) {
            throw ServiceExceptionUtil.exception(DocumentErrorCodeConstants.DOCUMENT_RECONFIRM_NOT_REQUIRED);
        }
        String beforeLock = document.lockState();
        DocumentRecord latest = saveDocumentState(document, LOCK_LOCKED, Boolean.FALSE, null);
        DocumentActionDiffUtils.DiffContent diff = DocumentActionDiffUtils.buildStatusLockDiff(
                "重新确认", document.status(), latest.status(), beforeLock, latest.lockState(),
                List.of("待重新确认：是 -> 否", "说明：" + display(dto.getReason())));
        writeDocumentLog(latest, "reconfirm", document.status(), latest.status(), beforeLock, latest.lockState(),
                trimToNull(dto.getReason()), diff);
        return buildUnlockResult(latest, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DocumentOwnerAssignResultVO assignOwner(DocumentOwnerAssignDTO dto) {
        DocumentRecord document = requireDocument(normalizeDocumentType(dto.getDocumentType()), dto.getDocumentId(), currentScope(true));
        if (dto.getOwnerId() == null) {
            throw ServiceExceptionUtil.exception(DocumentErrorCodeConstants.DOCUMENT_OWNER_REQUIRED);
        }
        UserDO owner = userManager.getById(dto.getOwnerId());
        if (owner == null) {
            throw ServiceExceptionUtil.exception(DocumentErrorCodeConstants.DOCUMENT_OWNER_NOT_FOUND);
        }
        String reason = trimToNull(dto.getReason());
        DocumentOwnerAssignResultVO result = new DocumentOwnerAssignResultVO();
        result.setDocumentType(document.type());
        result.setDocumentId(document.id());
        result.setOwnerId(owner.getId());
        result.setOwnerName(resolveUserName(owner));

        boolean changed = !Objects.equals(document.ownerId(), owner.getId())
                || !Objects.equals(display(document.ownerName()), display(resolveUserName(owner)));
        if (changed) {
            DocumentRecord latest = saveOwner(document, owner);
            writeOwnerLog(latest, document.ownerName(), latest.ownerName(), reason, false);
        }
        result.getSyncedDocuments().addAll(syncLinkedOwner(document, owner, reason));
        return result;
    }

    private DocumentUnlockRequestDO createUnlockRequest(DocumentRecord document,
                                                        String requestType,
                                                        String requestStatus,
                                                        String reason) {
        LoginUserInfo operator = LoginUserInfoContext.requireLoginUserInfo();
        DocumentUnlockRequestDO request = new DocumentUnlockRequestDO();
        request.setDocumentType(document.type());
        request.setDocumentId(document.id());
        request.setBaseCode(document.baseCode());
        request.setSerialCode(document.serialCode());
        request.setRequestType(requestType);
        request.setRequesterId(operator.getUserId());
        request.setRequesterName(operator.getUserName());
        request.setApproverRoleKey(APPROVER_ROLE_ADMIN);
        request.setApproverId(REQUEST_STATUS_PENDING.equals(requestStatus) ? null : operator.getUserId());
        request.setApproverName(REQUEST_STATUS_PENDING.equals(requestStatus) ? null : operator.getUserName());
        request.setRequestReason(reason);
        request.setRequestStatus(requestStatus);
        request.setDecisionRemark(REQUEST_STATUS_PENDING.equals(requestStatus) ? null : reason);
        request.setProcessedAt(REQUEST_STATUS_PENDING.equals(requestStatus) ? null : LocalDateTime.now());
        documentUnlockRequestManager.save(request);
        return request;
    }

    private DocumentUnlockRequestDO requirePendingUnlock(DocumentRecord document) {
        DocumentUnlockRequestDO pending = documentUnlockRequestManager.getLatestPending(document.type(), document.id());
        if (pending == null) {
            throw ServiceExceptionUtil.exception(DocumentErrorCodeConstants.DOCUMENT_UNLOCK_REQUEST_NOT_FOUND);
        }
        return pending;
    }

    private DocumentRecord saveDocumentState(DocumentRecord document,
                                             String lockState,
                                             Boolean needsReconfirm,
                                             String reconfirmScopeJson) {
        if (TYPE_QUOTE.equals(document.type())) {
            QuoteDO quote = document.quote();
            quote.setLockState(lockState);
            quote.setNeedsReconfirm(needsReconfirm);
            quote.setReconfirmScopeJson(reconfirmScopeJson);
            quoteManager.saveOrUpdate(quote);
            return fromQuote(quote);
        }
        if (TYPE_ORDER.equals(document.type())) {
            OrdersDO order = document.order();
            order.setLockState(lockState);
            order.setNeedsReconfirm(needsReconfirm);
            order.setReconfirmScopeJson(reconfirmScopeJson);
            ordersManager.saveOrUpdate(order);
            return fromOrder(order);
        }
        if (TYPE_PURCHASE.equals(document.type())) {
            PurchaseOrderDO purchase = document.purchase();
            purchase.setLockState(lockState);
            purchase.setNeedsReconfirm(needsReconfirm);
            purchase.setReconfirmScopeJson(reconfirmScopeJson);
            purchaseOrderManager.saveOrUpdate(purchase);
            return fromPurchase(purchase);
        }
        if (TYPE_PRODUCTION.equals(document.type())) {
            ProductionOrderDO production = document.production();
            production.setLockState(lockState);
            production.setNeedsReconfirm(needsReconfirm);
            production.setReconfirmScopeJson(reconfirmScopeJson);
            productionOrderManager.saveOrUpdate(production);
            return fromProduction(production);
        }
        throw ServiceExceptionUtil.exception(DocumentErrorCodeConstants.DOCUMENT_TYPE_UNSUPPORTED);
    }

    private DocumentRecord saveOwner(DocumentRecord document, UserDO owner) {
        String ownerName = resolveUserName(owner);
        if (TYPE_QUOTE.equals(document.type())) {
            QuoteDO quote = document.quote();
            quote.setOwnerId(owner.getId());
            quote.setOwnerName(ownerName);
            quoteManager.saveOrUpdate(quote);
            return fromQuote(quote);
        }
        if (TYPE_ORDER.equals(document.type())) {
            OrdersDO order = document.order();
            order.setOwnerId(owner.getId());
            order.setOwnerName(ownerName);
            ordersManager.saveOrUpdate(order);
            return fromOrder(order);
        }
        if (TYPE_PURCHASE.equals(document.type())) {
            PurchaseOrderDO purchase = document.purchase();
            purchase.setOwnerId(owner.getId());
            purchase.setOwnerName(ownerName);
            purchaseOrderManager.saveOrUpdate(purchase);
            return fromPurchase(purchase);
        }
        if (TYPE_PRODUCTION.equals(document.type())) {
            ProductionOrderDO production = document.production();
            production.setOwnerId(owner.getId());
            production.setOwnerName(ownerName);
            productionOrderManager.saveOrUpdate(production);
            return fromProduction(production);
        }
        throw ServiceExceptionUtil.exception(DocumentErrorCodeConstants.DOCUMENT_TYPE_UNSUPPORTED);
    }

    private List<String> syncLinkedOwner(DocumentRecord document, UserDO owner, String reason) {
        List<String> synced = new ArrayList<>();
        String ownerName = resolveUserName(owner);
        if (TYPE_QUOTE.equals(document.type()) && document.quote().getConvertedOrderId() != null) {
            OrdersDO order = ordersManager.getById(document.quote().getConvertedOrderId());
            if (order != null && !Objects.equals(order.getOwnerId(), owner.getId())) {
                String beforeOwner = order.getOwnerName();
                order.setOwnerId(owner.getId());
                order.setOwnerName(ownerName);
                ordersManager.saveOrUpdate(order);
                DocumentRecord latest = fromOrder(order);
                writeOwnerLog(latest, beforeOwner, latest.ownerName(), reason, true);
                synced.add(TYPE_ORDER + ":" + latest.id());
            }
        }
        if (TYPE_ORDER.equals(document.type()) && document.order().getQuoteId() != null) {
            QuoteDO quote = quoteManager.getById(document.order().getQuoteId());
            if (quote != null && !Objects.equals(quote.getOwnerId(), owner.getId())) {
                String beforeOwner = quote.getOwnerName();
                quote.setOwnerId(owner.getId());
                quote.setOwnerName(ownerName);
                quoteManager.saveOrUpdate(quote);
                DocumentRecord latest = fromQuote(quote);
                writeOwnerLog(latest, beforeOwner, latest.ownerName(), reason, true);
                synced.add(TYPE_QUOTE + ":" + latest.id());
            }
        }
        return synced;
    }

    private void writeOwnerLog(DocumentRecord document, String beforeOwnerName, String afterOwnerName,
                               String reason, boolean synced) {
        DocumentActionDiffUtils.DiffContent diff = DocumentActionDiffUtils.buildOwnerAssignmentDiff(
                beforeOwnerName, afterOwnerName, synced, reason);
        writeDocumentLog(document, synced ? "sync_assign_owner" : "assign_owner",
                document.status(), document.status(), document.lockState(), document.lockState(), reason, diff);
    }

    private void writeDocumentLog(DocumentRecord document,
                                  String actionType,
                                  String beforeStatus,
                                  String afterStatus,
                                  String beforeLockState,
                                  String afterLockState,
                                  String reason,
                                  DocumentActionDiffUtils.DiffContent diff) {
        recordAction(DocumentActionLogCreateCommand.builder()
                .documentType(document.type())
                .documentId(document.id())
                .baseCode(document.baseCode())
                .serialCode(document.serialCode())
                .actionType(actionType)
                .beforeStatus(beforeStatus)
                .afterStatus(afterStatus)
                .beforeLockState(beforeLockState)
                .afterLockState(afterLockState)
                .actionReason(reason)
                .diffSummary(diff == null ? null : diff.summary())
                .diffDetail(diff == null ? null : diff.detail())
                .build());
    }

    private DocumentUnlockResultVO buildUnlockResult(DocumentRecord document, DocumentUnlockRequestDO request) {
        DocumentUnlockResultVO result = new DocumentUnlockResultVO();
        result.setDocumentType(document.type());
        result.setDocumentId(document.id());
        result.setStatus(document.status());
        result.setLockState(document.lockState());
        result.setNeedsReconfirm(Boolean.TRUE.equals(document.needsReconfirm()));
        if (request != null) {
            result.setUnlockRequestId(request.getId());
            result.setRequestStatus(request.getRequestStatus());
        }
        return result;
    }

    private DocumentRecord requireDocument(String documentType, Long documentId, Scope scope) {
        if (TYPE_QUOTE.equals(documentType)) {
            QuoteDO quote = quoteManager.getVisibleById(documentId, scope.userId(), scope.allVisible());
            if (quote == null) {
                throw ServiceExceptionUtil.exception(DocumentErrorCodeConstants.DOCUMENT_NOT_FOUND);
            }
            return fromQuote(quote);
        }
        if (TYPE_ORDER.equals(documentType)) {
            OrdersDO order = ordersManager.getVisibleById(documentId, scope.userId(), scope.allVisible());
            if (order == null) {
                throw ServiceExceptionUtil.exception(DocumentErrorCodeConstants.DOCUMENT_NOT_FOUND);
            }
            return fromOrder(order);
        }
        if (TYPE_PURCHASE.equals(documentType)) {
            PurchaseOrderDO purchase = purchaseOrderManager.getVisibleById(documentId, scope.userId(), scope.allVisible());
            if (purchase == null) {
                throw ServiceExceptionUtil.exception(DocumentErrorCodeConstants.DOCUMENT_NOT_FOUND);
            }
            return fromPurchase(purchase);
        }
        if (TYPE_PRODUCTION.equals(documentType)) {
            ProductionOrderDO production = productionOrderManager.getVisibleById(documentId, scope.userId(), scope.allVisible());
            if (production == null) {
                throw ServiceExceptionUtil.exception(DocumentErrorCodeConstants.DOCUMENT_NOT_FOUND);
            }
            return fromProduction(production);
        }
        throw ServiceExceptionUtil.exception(DocumentErrorCodeConstants.DOCUMENT_TYPE_UNSUPPORTED);
    }

    private DocumentRecord fromQuote(QuoteDO quote) {
        return new DocumentRecord(TYPE_QUOTE, quote.getId(), quote.getBaseCode(), quote.getSerialCode(), quote.getStatus(),
                normalizeLockState(quote.getLockState()), quote.getOwnerId(), quote.getOwnerName(),
                Boolean.TRUE.equals(quote.getNeedsReconfirm()), quote.getReconfirmScopeJson(), quote, null, null, null);
    }

    private DocumentRecord fromOrder(OrdersDO order) {
        return new DocumentRecord(TYPE_ORDER, order.getId(), order.getBaseCode(), order.getSerialCode(), order.getStatus(),
                normalizeLockState(order.getLockState()), order.getOwnerId(), order.getOwnerName(),
                Boolean.TRUE.equals(order.getNeedsReconfirm()), order.getReconfirmScopeJson(), null, order, null, null);
    }

    private DocumentRecord fromPurchase(PurchaseOrderDO purchase) {
        return new DocumentRecord(TYPE_PURCHASE, purchase.getId(), purchase.getBaseCode(), purchase.getSerialCode(), purchase.getStatus(),
                normalizeLockState(purchase.getLockState()), purchase.getOwnerId(), purchase.getOwnerName(),
                Boolean.TRUE.equals(purchase.getNeedsReconfirm()), purchase.getReconfirmScopeJson(), null, null, purchase, null);
    }

    private DocumentRecord fromProduction(ProductionOrderDO production) {
        return new DocumentRecord(TYPE_PRODUCTION, production.getId(), production.getBaseCode(), production.getSerialCode(), production.getStatus(),
                normalizeLockState(production.getLockState()), production.getOwnerId(), production.getOwnerName(),
                Boolean.TRUE.equals(production.getNeedsReconfirm()), production.getReconfirmScopeJson(), null, null, null, production);
    }

    private Scope currentScope(boolean adminAction) {
        LoginUserInfo loginUser = LoginUserInfoContext.requireLoginUserInfo();
        Set<String> permissions = loginUser.getPermissions();
        boolean superAdmin = permissions != null && permissions.contains(ALL_PERMISSION);
        boolean documentAdmin = adminAction && permissions != null
                && (permissions.contains(ApiPermissionConstants.DOCUMENT_UNLOCK_APPROVE)
                || permissions.contains(ApiPermissionConstants.DOCUMENT_REASSIGN_OWNER));
        return new Scope(loginUser.getUserId(), loginUser.getUserName(), superAdmin || documentAdmin);
    }

    private String normalizeDocumentType(String type) {
        if (!StringUtils.hasText(type)) {
            throw ServiceExceptionUtil.exception(DocumentErrorCodeConstants.DOCUMENT_TYPE_UNSUPPORTED);
        }
        String normalized = type.trim().toLowerCase(Locale.ROOT);
        if (TYPE_QUOTE.equals(normalized) || TYPE_ORDER.equals(normalized) || TYPE_PURCHASE.equals(normalized)
                || TYPE_PRODUCTION.equals(normalized)) {
            return normalized;
        }
        throw ServiceExceptionUtil.exception(DocumentErrorCodeConstants.DOCUMENT_TYPE_UNSUPPORTED);
    }

    private String normalizeLockState(String lockState) {
        return StringUtils.hasText(lockState) ? lockState.trim().toLowerCase(Locale.ROOT) : LOCK_OPEN;
    }

    private String normalizeReason(String reason, boolean required) {
        String normalized = trimToNull(reason);
        if (required && normalized == null) {
            throw ServiceExceptionUtil.exception(DocumentErrorCodeConstants.DOCUMENT_UNLOCK_REASON_REQUIRED);
        }
        return normalized;
    }

    private String resolveUserName(UserDO user) {
        if (StringUtils.hasText(user.getNickName())) {
            return user.getNickName().trim();
        }
        return user.getUserName();
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private String display(Object value) {
        if (value == null) {
            return "-";
        }
        String text = String.valueOf(value);
        return StringUtils.hasText(text) ? text.trim() : "-";
    }

    private record Scope(Long userId, String userName, boolean allVisible) {
    }

    private record DocumentRecord(
            String type,
            Long id,
            String baseCode,
            String serialCode,
            String status,
            String lockState,
            Long ownerId,
            String ownerName,
            Boolean needsReconfirm,
            String reconfirmScopeJson,
            QuoteDO quote,
            OrdersDO order,
            PurchaseOrderDO purchase,
            ProductionOrderDO production
    ) {
    }
}
