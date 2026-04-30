package com.qmy.zhongsheng.core.process.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.api.dto.process.ProcessListQueryDTO;
import com.qmy.zhongsheng.api.dto.process.ProcessSaveDTO;
import com.qmy.zhongsheng.api.request.IdRequestParam;
import com.qmy.zhongsheng.common.error.ProcessErrorCodeConstants;
import com.qmy.zhongsheng.common.exception.ServiceExceptionUtil;
import com.qmy.zhongsheng.common.utils.BeanUtils;
import com.qmy.zhongsheng.core.process.manager.ProcessManager;
import com.qmy.zhongsheng.core.process.model.entity.ProcessDO;
import com.qmy.zhongsheng.core.process.model.vo.ProcessVO;
import com.qmy.zhongsheng.core.process.service.ProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author AI Coding
 */
@Service
@RequiredArgsConstructor
public class ProcessServiceImpl implements ProcessService {

    private final ProcessManager processManager;

    @Override
    public Long saveOrUpdate(ProcessSaveDTO dto) {
        // 校验名称必填
        if (!StringUtils.hasText(dto.getName())) {
            throw ServiceExceptionUtil.exception(ProcessErrorCodeConstants.PROCESS_NAME_REQUIRED);
        }
        // 新增时检查名称重复
        if (dto.getId() == null) {
            if (processManager.existsByName(dto.getName(), null)) {
                throw ServiceExceptionUtil.exception(ProcessErrorCodeConstants.PROCESS_NAME_DUPLICATE);
            }
        } else {
            // 更新时检查名称重复（排除自身）
            ProcessDO existing = processManager.getById(dto.getId());
            if (existing == null) {
                throw ServiceExceptionUtil.exception(ProcessErrorCodeConstants.PROCESS_NOT_FOUND);
            }
            if (processManager.existsByName(dto.getName(), dto.getId())) {
                throw ServiceExceptionUtil.exception(ProcessErrorCodeConstants.PROCESS_NAME_DUPLICATE);
            }
        }
        ProcessDO row = BeanUtils.toBean(dto, ProcessDO.class);
        return processManager.saveOrUpdate(row);
    }

    @Override
    public Page<ProcessVO> page(ProcessListQueryDTO query) {
        Page<ProcessDO> doPage = processManager.page(query.getLikeName(), query.getPageNum(), query.getPageSize());
        Page<ProcessVO> voPage = new Page<>(doPage.getCurrent(), doPage.getSize(), doPage.getTotal());
        voPage.setRecords(BeanUtils.toBean(doPage.getRecords(), ProcessVO.class));
        return voPage;
    }

    @Override
    public List<ProcessVO> list() {
        return BeanUtils.toBean(processManager.list(), ProcessVO.class);
    }

    @Override
    public Boolean delete(IdRequestParam id) {
        return processManager.delete(id.getId());
    }
}