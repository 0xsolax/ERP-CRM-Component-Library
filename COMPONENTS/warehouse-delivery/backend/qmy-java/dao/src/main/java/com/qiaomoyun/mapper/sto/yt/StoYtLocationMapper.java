package com.qiaomoyun.mapper.sto.yt;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiaomoyun.entity.sto.yt.StoYtLocation;

import java.util.List;

public interface StoYtLocationMapper extends BaseMapper<StoYtLocation> {
    /**
     * 查询库位下拉框列表
     */
    List<StoYtLocation> selectForDropdown();

    /**
     * 根据条件查询库位列表
     */
    List<StoYtLocation> selectList(StoYtLocation query);

}
