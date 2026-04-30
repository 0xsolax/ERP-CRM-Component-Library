package com.qmy.zhongsheng.core.material.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.api.dto.material.FabricListQueryDTO;
import com.qmy.zhongsheng.api.dto.material.FabricSaveDTO;
import com.qmy.zhongsheng.api.dto.material.FabricSelectQueryDTO;
import com.qmy.zhongsheng.core.material.model.vo.FabricVO;

import java.util.List;

/**
 * 面料管理服务。
 *
 * @author AI Coding
 */
public interface FabricService {

    /**
     * 保存或更新面料：无 id 为新增，有 id 为更新。
     * 支持 {@code isDeleted} 字段进行软删除。
     *
     * @param dto 保存请求体
     * @return 记录主键 id
     */
    Long saveOrUpdate(FabricSaveDTO dto);

    /**
     * 分页查询面料列表，支持种类、型号模糊搜索。
     *
     * @param query 查询条件
     * @return 分页结果
     */
    Page<FabricVO> page(FabricListQueryDTO query);

    /**
     * 查询面料列表（不分页），用于产品保存时下拉选择；可按种类、型号 baseDataId 筛选。
     *
     * @param query 筛选条件，可为 {@code null}；字段为 {@code null} 时不按该维度过滤
     * @return {@link FabricVO} 列表
     */
    List<FabricVO> listForSelect(FabricSelectQueryDTO query);

    /**
     * 删除面料
     *
     * @param id 记录主键 id
     * @return 是否成功
     */
    Boolean delete(Long id);

    /**
     * 获取面料详情
     * @param id 面料 id
     * @return 详情
     */
    FabricVO getDetail(Long id);
}