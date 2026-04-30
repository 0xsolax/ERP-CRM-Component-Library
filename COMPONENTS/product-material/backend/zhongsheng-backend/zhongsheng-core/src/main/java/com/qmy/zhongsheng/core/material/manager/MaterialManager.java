package com.qmy.zhongsheng.core.material.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.core.material.model.entity.MaterialDO;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * @author AI Coding
 */
public interface MaterialManager {

    Long saveOrUpdate(MaterialDO row);

    MaterialDO getById(Long id);

    Page<MaterialDO> page(Long categoryId, String likeName, String likeSize, Integer pageNum, Integer pageSize);

    List<MaterialDO> listByCategoryIds(List<Long> categoryIds);

    List<MaterialDO> listByIds(List<Long> ids);

    List<MaterialDO> listByCategoryId(Long id);

    Boolean deleted(Long id);

    /**
     * 按分类 ID 和名称查询材料（用于重复性校验）
     *
     * @param categoryId 分类 ID
     * @param name 材料名称
     * @return 匹配的记录
     */
    MaterialDO getByCategoryIdAndName(Long categoryId, String name);
}