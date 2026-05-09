package com.qmy.zhongsheng.core.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.api.dto.system.SystemOperationLogQueryDTO;
import com.qmy.zhongsheng.core.system.model.vo.SystemOperationLogVO;

/**
 * 系统操作日志服务。
 *
 * @author AI Coding
 */
public interface SystemOperationLogService {

    Page<SystemOperationLogVO> page(SystemOperationLogQueryDTO query);
}
