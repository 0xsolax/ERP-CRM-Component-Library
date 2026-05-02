/*
 * @author java_deng
 * @date 2024/12/15 10:05
 * @description 箱规管理Mapper接口
 */
package com.qiaomoyun.mapper.sto.yt;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiaomoyun.entity.sto.yt.StoYtBox;

import java.util.List;

/**
 * 箱规管理Mapper接口
 */
public interface StoYtBoxMapper extends BaseMapper<StoYtBox> {

    /**
     * 获取箱规列表
     */
    List<StoYtBox> list(StoYtBox stoYtBox);

    /**
     * 检查箱规代码是否已存在
     */
    boolean existsByCode(String code, Long excludeId);
}
