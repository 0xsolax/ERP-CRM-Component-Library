package com.qmy.zhongsheng.core.document.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.core.document.dao.DocumentActionLogDAO;
import com.qmy.zhongsheng.core.document.manager.DocumentActionLogManager;
import com.qmy.zhongsheng.core.document.model.condition.DocumentActionLogQueryCondition;
import com.qmy.zhongsheng.core.document.model.entity.DocumentActionLogDO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * 公共单据动作日志 Manager 实现。
 *
 * @author AI Coding
 */
@Component
@RequiredArgsConstructor
public class DocumentActionLogManagerImpl implements DocumentActionLogManager {

    private final DocumentActionLogDAO documentActionLogDAO;

    @Override
    public Long save(DocumentActionLogDO log) {
        documentActionLogDAO.insert(log);
        return log.getId();
    }

    @Override
    public Page<DocumentActionLogDO> page(DocumentActionLogQueryCondition condition) {
        Page<DocumentActionLogDO> page = new Page<>(condition.getPageNum(), condition.getPageSize());
        LambdaQueryWrapper<DocumentActionLogDO> query = Wrappers.<DocumentActionLogDO>lambdaQuery()
                .eq(DocumentActionLogDO::getIsDeleted, 0)
                .eq(DocumentActionLogDO::getDocumentType, condition.getDocumentType())
                .eq(DocumentActionLogDO::getDocumentId, condition.getDocumentId())
                .orderByDesc(DocumentActionLogDO::getCreateTime)
                .orderByDesc(DocumentActionLogDO::getId);
        return documentActionLogDAO.selectPage(page, query);
    }

    @Override
    public Page<DocumentActionLogDO> adminPage(DocumentActionLogQueryCondition condition) {
        Page<DocumentActionLogDO> page = new Page<>(condition.getPageNum(), condition.getPageSize());
        LambdaQueryWrapper<DocumentActionLogDO> query = Wrappers.<DocumentActionLogDO>lambdaQuery()
                .eq(DocumentActionLogDO::getIsDeleted, 0)
                .eq(StringUtils.hasText(condition.getDocumentType()),
                        DocumentActionLogDO::getDocumentType, condition.getDocumentType())
                .in(!CollectionUtils.isEmpty(condition.getDocumentTypes()),
                        DocumentActionLogDO::getDocumentType, condition.getDocumentTypes())
                .eq(condition.getOperatorId() != null, DocumentActionLogDO::getOperatorId, condition.getOperatorId())
                .like(StringUtils.hasText(condition.getOperatorKeyword()),
                        DocumentActionLogDO::getOperatorName, condition.getOperatorKeyword())
                .eq(StringUtils.hasText(condition.getActionType()),
                        DocumentActionLogDO::getActionType, condition.getActionType())
                .ge(condition.getStartTime() != null, DocumentActionLogDO::getCreateTime, condition.getStartTime())
                .le(condition.getEndTime() != null, DocumentActionLogDO::getCreateTime, condition.getEndTime());
        if (StringUtils.hasText(condition.getKeyword())) {
            String keyword = condition.getKeyword();
            query.and(wrapper -> wrapper
                    .like(DocumentActionLogDO::getBaseCode, keyword)
                    .or()
                    .like(DocumentActionLogDO::getSerialCode, keyword)
                    .or()
                    .like(DocumentActionLogDO::getActionReason, keyword)
                    .or()
                    .like(DocumentActionLogDO::getDiffSummary, keyword)
                    .or()
                    .like(DocumentActionLogDO::getDiffDetail, keyword));
        }
        query.orderByDesc(DocumentActionLogDO::getCreateTime)
                .orderByDesc(DocumentActionLogDO::getId);
        return documentActionLogDAO.selectPage(page, query);
    }
}
