package com.qmy.zhongsheng.core.material.manager;

import com.qmy.zhongsheng.core.material.model.entity.UmbrellaFrameMaterialDO;

import java.util.List;

/**
 * 伞架材料绑定持久化与查询。
 *
 * @author AI Coding
 */
public interface UmbrellaFrameMaterialManager {

    /**
     * 单条保存或更新：{@code id} 为 {@code null} 时插入，否则按主键更新。
     *
     * @param row 待持久化的实体
     * @return 记录主键 id
     */
    Long saveOrUpdate(UmbrellaFrameMaterialDO row);

    /**
     * 按 id 查询。
     *
     * @param id 主键 id
     * @return 匹配的记录
     */
    UmbrellaFrameMaterialDO getById(Long id);

    /**
     * 按伞架ID查询材料绑定列表。
     *
     * @param umbrellaFrameId 伞架ID
     * @return 材料绑定列表
     */
    List<UmbrellaFrameMaterialDO> listByUmbrellaFrameId(Long umbrellaFrameId);

    /**
     * 批量按伞架ID查询材料绑定列表。
     *
     * @param umbrellaFrameIds 伞架ID列表
     * @return 材料绑定列表
     */
    List<UmbrellaFrameMaterialDO> listByUmbrellaFrameIds(List<Long> umbrellaFrameIds);
}