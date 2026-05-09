package com.qmy.zhongsheng.core.document.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.api.dto.document.DocumentActionLogQueryDTO;
import com.qmy.zhongsheng.api.dto.document.DocumentOwnerAssignDTO;
import com.qmy.zhongsheng.api.dto.document.DocumentUnlockActionDTO;
import com.qmy.zhongsheng.core.document.model.command.DocumentActionLogCreateCommand;
import com.qmy.zhongsheng.core.document.model.vo.DocumentActionLogVO;
import com.qmy.zhongsheng.core.document.model.vo.DocumentOwnerAssignResultVO;
import com.qmy.zhongsheng.core.document.model.vo.DocumentUnlockResultVO;

/**
 * 公共单据状态动作服务。
 *
 * @author AI Coding
 */
public interface DocumentActionService {

    void recordAction(DocumentActionLogCreateCommand command);

    Page<DocumentActionLogVO> pageLogs(DocumentActionLogQueryDTO query);

    DocumentUnlockResultVO requestUnlock(DocumentUnlockActionDTO dto);

    DocumentUnlockResultVO warningUnlock(DocumentUnlockActionDTO dto);

    DocumentUnlockResultVO approveUnlock(DocumentUnlockActionDTO dto);

    DocumentUnlockResultVO rejectUnlock(DocumentUnlockActionDTO dto);

    DocumentUnlockResultVO reconfirm(DocumentUnlockActionDTO dto);

    DocumentOwnerAssignResultVO assignOwner(DocumentOwnerAssignDTO dto);
}
