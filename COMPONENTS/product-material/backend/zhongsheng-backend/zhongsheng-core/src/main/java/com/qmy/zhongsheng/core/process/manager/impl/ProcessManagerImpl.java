package com.qmy.zhongsheng.core.process.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.core.process.dao.ProcessDAO;
import com.qmy.zhongsheng.core.process.manager.ProcessManager;
import com.qmy.zhongsheng.core.process.model.entity.ProcessDO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author AI Coding
 */
@Component
@RequiredArgsConstructor
public class ProcessManagerImpl implements ProcessManager {

    private final ProcessDAO processDAO;

    @Override
    public Long saveOrUpdate(ProcessDO row) {
        if (row.getId() == null) {
            processDAO.insert(row);
            return row.getId();
        }
        processDAO.updateById(row);
        return row.getId();
    }

    @Override
    public ProcessDO getById(Long id) {
        return processDAO.selectById(id);
    }

    @Override
    public boolean existsByName(String name, Long excludeId) {
        return processDAO.exists(Wrappers.<ProcessDO>lambdaQuery()
                .eq(ProcessDO::getName, name)
                .ne(excludeId != null, ProcessDO::getId, excludeId));
    }

    @Override
    public Page<ProcessDO> page(String name, Integer pageNum, Integer pageSize) {
        Page<ProcessDO> page = new Page<>(pageNum, pageSize);
        return processDAO.selectPage(page, Wrappers.<ProcessDO>lambdaQuery()
                .eq(ProcessDO::getIsDeleted, 0)
                .like(StringUtils.hasText(name), ProcessDO::getName, name));
    }

    @Override
    public List<ProcessDO> list() {
        LambdaQueryWrapper<ProcessDO> query = Wrappers.<ProcessDO>lambdaQuery()
                .eq(ProcessDO::getIsDeleted, 0);
        return processDAO.selectList(query);
    }

    @Override
    public Boolean delete(Long id) {
        LambdaUpdateWrapper<ProcessDO> update = Wrappers.<ProcessDO>lambdaUpdate().eq(ProcessDO::getId, id).set(ProcessDO::getIsDeleted, 1).set(ProcessDO::getDeletedTime, LocalDateTime.now());
        processDAO.update(update);
        return Boolean.TRUE;
    }
}