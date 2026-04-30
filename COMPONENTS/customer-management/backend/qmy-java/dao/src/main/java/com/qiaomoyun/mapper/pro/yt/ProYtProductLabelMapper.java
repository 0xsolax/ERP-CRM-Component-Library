/*
 * @author java_deng
 * @date 2025/11/3 14:41
 * @description 优品产品标签Mapper
 */
package com.qiaomoyun.mapper.pro.yt;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiaomoyun.entity.pro.yt.ProYtProductLabel;

import java.util.List;

/**
 * 产品标签Mapper接口
 */
public interface ProYtProductLabelMapper extends BaseMapper<ProYtProductLabel> {
    List<ProYtProductLabel> selectByMasterIdAndType(ProYtProductLabel proYtProductLabel);
    
    /**
     * 根据类型查询去重的标签列表
     */
    List<String> selectDistinctLabelValuesByType(String type);
    
    /**
     * 检查同一master和type下是否存在相同value的标签（排除指定ID）
     */
    ProYtProductLabel selectByMasterIdAndTypeAndValue(Long masterId, String type, String value, Integer excludeId);

    void updateValue(ProYtProductLabel label);
}