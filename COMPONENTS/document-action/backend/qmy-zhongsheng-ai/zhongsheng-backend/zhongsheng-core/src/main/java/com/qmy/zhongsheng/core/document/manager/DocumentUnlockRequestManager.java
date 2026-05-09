package com.qmy.zhongsheng.core.document.manager;

import com.qmy.zhongsheng.core.document.model.entity.DocumentUnlockRequestDO;

/**
 * 公共单据解锁申请 Manager。
 *
 * @author AI Coding
 */
public interface DocumentUnlockRequestManager {

    Long save(DocumentUnlockRequestDO request);

    void update(DocumentUnlockRequestDO request);

    DocumentUnlockRequestDO getLatestPending(String documentType, Long documentId);
}
