package com.qmy.zhongsheng.core.material.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.api.dto.material.PackagingListQueryDTO;
import com.qmy.zhongsheng.api.dto.material.PackagingSaveDTO;
import com.qmy.zhongsheng.core.material.model.vo.PackagingVO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 包材管理服务。
 *
 * @author AI Coding
 */
public interface PackagingService {

    /**
     * 保存或更新包材：无 id 为新增，有 id 为更新。
     * 支持 {@code isDeleted} 字段进行软删除。
     *
     * @param dto 保存请求体
     * @return 记录主键 id
     */
    Long saveOrUpdate(PackagingSaveDTO dto);

    /**
     * 批量保存或更新默认纸箱包材：无 id 为新增，有 id 为更新。
     * @param dto 保存请求体
     * @return 记录主键 id
     */
    List<Long> saveOrUpdateDefaultPaperBox(@Valid List<PackagingSaveDTO> dto);

    /**
     * 分页查询包材列表，支持类型、尺寸搜索。
     *
     * @param query 查询条件
     * @return 分页结果
     */
    Page<PackagingVO> page(PackagingListQueryDTO query);

    /**
     * 删除包材：逻辑删除。
     *
     * @param id 主键 id
     * @return 删除结果
     */
    Boolean delete(Long id);

}