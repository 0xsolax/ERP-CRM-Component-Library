package com.qmy.zhongsheng.core.base.service;

import com.qmy.zhongsheng.api.dto.base.BaseDataBatchSaveDTO;
import com.qmy.zhongsheng.api.dto.base.BaseDataListQueryDTO;
import com.qmy.zhongsheng.api.dto.base.BaseDataSaveDTO;
import com.qmy.zhongsheng.core.base.model.vo.BaseDataVO;
import com.qmy.zhongsheng.core.base.model.vo.BaseTreeNodeVO;

import java.util.List;
import java.util.Map;

/**
 * 通用基础数据维护。
 *
 * @author AI Coding
 */
public interface BaseDataService {

    /**
     * 单条保存或更新：无 id 为新增，有 id 为按非 null 字段更新。
     *
     * @param dto 保存请求体
     * @return 记录主键 id
     */
    Long saveOrUpdate(BaseDataSaveDTO dto);

    /**
     * 按基础树节点 id 集合过滤列表。
     *
     * @param query 查询条件，可空表示不加过滤
     * @return 基础数据列表
     */
    List<BaseDataVO> list(BaseDataListQueryDTO query);

    /**
     * 通过节点种子标识批量查询基础数据。
     * @param nodeKey 节点 key
     * @return 基础数据列表
     */
    List<BaseDataVO> listByNodeKey(String nodeKey);

    /**
     * 按业务类型查询基础树节点。
     *
     * @param bizType 业务类型值（对应 BaseTreeBizTypeEnum.value），为空时返回全部
     * @return 树节点列表
     */
    List<BaseTreeNodeVO> listNodeByBizTypeOrNodeKey(String bizType,String nodeKey);

    /**
     * 删除
     *
     * @param id 记录主键 id
     * @return 删除结果
     */
    Boolean delete(Long id);
}
