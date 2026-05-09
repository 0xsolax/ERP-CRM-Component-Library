package com.qmy.zhongsheng.core.document.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.core.document.model.condition.DocumentActionLogQueryCondition;
import com.qmy.zhongsheng.core.document.model.entity.DocumentActionLogDO;

/**
 * 公共单据动作日志 Manager。
 *
 * @author AI Coding
 */
public interface DocumentActionLogManager {

    Long save(DocumentActionLogDO log);

    Page<DocumentActionLogDO> page(DocumentActionLogQueryCondition condition);

    Page<DocumentActionLogDO> adminPage(DocumentActionLogQueryCondition condition);
}
