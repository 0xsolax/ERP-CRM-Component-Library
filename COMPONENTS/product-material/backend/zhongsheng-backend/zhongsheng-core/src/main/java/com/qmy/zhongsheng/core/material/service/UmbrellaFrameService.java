package com.qmy.zhongsheng.core.material.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.api.dto.material.UmbrellaFrameListQueryDTO;
import com.qmy.zhongsheng.api.dto.material.UmbrellaFrameSaveDTO;
import com.qmy.zhongsheng.api.dto.material.UmbrellaFrameSelectQueryDTO;
import com.qmy.zhongsheng.core.material.model.vo.UmbrellaFrameDetailVO;
import com.qmy.zhongsheng.core.material.model.vo.UmbrellaFrameMaterialVO;
import com.qmy.zhongsheng.core.material.model.vo.UmbrellaFrameVO;

import java.util.List;

/**
 * 伞架管理服务。
 *
 * @author AI Coding
 */
public interface UmbrellaFrameService {

    /**
     * 保存或更新伞架：无 id 为新增，有 id 为更新。
     * 支持 {@code isDeleted} 字段进行软删除。
     *
     * @param dto 保存请求体
     * @return 记录主键 id
     */
    Long saveOrUpdate(UmbrellaFrameSaveDTO dto);

    /**
     * 分页查询伞架列表，支持功能、类型、尺寸、材料筛选。
     *
     * @param query 查询条件
     * @return 分页结果
     */
    Page<UmbrellaFrameDetailVO> page(UmbrellaFrameListQueryDTO query);

    /**
     * 删除伞架（逻辑删除）。
     *
     * @param id 伞架 ID
     * @return 是否删除成功
     */
    Boolean delete(Long id);

    /**
     * 查询伞架绑定的材料列表。
     *
     * @param umbrellaFrameId 伞架ID
     * @return 材料绑定列表
     */
    List<UmbrellaFrameMaterialVO> listMaterials(Long umbrellaFrameId);

    /**
     * 查询伞架详情（包含绑定的材料列表）。
     *
     * @param umbrellaFrameId 伞架 ID
     * @return 伞架详情
     */
    UmbrellaFrameDetailVO getDetail(Long umbrellaFrameId);

    /**
     * 查询伞架列表（用于下拉框选择）；可按功能、类型、尺寸、材料等 baseDataId 筛选，{@code query} 为 {@code null} 或未传字段表示不按该条件过滤；无分页与关键词。
     *
     * @param query 筛选条件，可为 {@code null}
     * @return 伞架列表
     */
    List<UmbrellaFrameVO> listForSelect(UmbrellaFrameSelectQueryDTO query);
}