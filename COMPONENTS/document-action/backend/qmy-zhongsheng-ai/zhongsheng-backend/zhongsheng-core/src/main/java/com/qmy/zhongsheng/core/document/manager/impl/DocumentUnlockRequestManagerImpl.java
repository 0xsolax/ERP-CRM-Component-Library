package com.qmy.zhongsheng.core.document.manager.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.qmy.zhongsheng.core.document.dao.DocumentUnlockRequestDAO;
import com.qmy.zhongsheng.core.document.manager.DocumentUnlockRequestManager;
import com.qmy.zhongsheng.core.document.model.entity.DocumentUnlockRequestDO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 公共单据解锁申请 Manager 实现。
 *
 * @author AI Coding
 */
@Component
@RequiredArgsConstructor
public class DocumentUnlockRequestManagerImpl implements DocumentUnlockRequestManager {

    private static final String REQUEST_STATUS_PENDING = "pending";

    private final DocumentUnlockRequestDAO documentUnlockRequestDAO;

    @Override
    public Long save(DocumentUnlockRequestDO request) {
        documentUnlockRequestDAO.insert(request);
        return request.getId();
    }

    @Override
    public void update(DocumentUnlockRequestDO request) {
        documentUnlockRequestDAO.updateById(request);
    }

    @Override
    public DocumentUnlockRequestDO getLatestPending(String documentType, Long documentId) {
        return documentUnlockRequestDAO.selectOne(Wrappers.<DocumentUnlockRequestDO>lambdaQuery()
                .eq(DocumentUnlockRequestDO::getIsDeleted, 0)
                .eq(DocumentUnlockRequestDO::getDocumentType, documentType)
                .eq(DocumentUnlockRequestDO::getDocumentId, documentId)
                .eq(DocumentUnlockRequestDO::getRequestStatus, REQUEST_STATUS_PENDING)
                .orderByDesc(DocumentUnlockRequestDO::getCreateTime)
                .orderByDesc(DocumentUnlockRequestDO::getId)
                .last("LIMIT 1"));
    }
}
