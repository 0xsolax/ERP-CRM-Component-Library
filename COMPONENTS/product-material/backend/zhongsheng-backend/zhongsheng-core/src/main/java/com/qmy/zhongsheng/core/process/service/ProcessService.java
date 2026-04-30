package com.qmy.zhongsheng.core.process.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.api.dto.process.ProcessListQueryDTO;
import com.qmy.zhongsheng.api.dto.process.ProcessSaveDTO;
import com.qmy.zhongsheng.api.request.IdRequestParam;
import com.qmy.zhongsheng.core.process.model.vo.ProcessVO;

import java.util.List;

/**
 * 工序管理服务。
 *
 * @author AI Coding
 */
public interface ProcessService {

    /**
     * 保存或更新工序：无 id 为新增，有 id 为更新。
     * 支持 {@code isDeleted} 字段进行软删除。
     *
     * @param dto 保存请求体
     * @return 记录主键 id
     */
    Long saveOrUpdate(ProcessSaveDTO dto);

    /**
     * 分页查询工序列表，支持名称模糊搜索。
     *
     * @param query 查询条件
     * @return 分页结果
     */
    Page<ProcessVO> page(ProcessListQueryDTO query);

    List<ProcessVO> list();

    Boolean delete(IdRequestParam id);
}