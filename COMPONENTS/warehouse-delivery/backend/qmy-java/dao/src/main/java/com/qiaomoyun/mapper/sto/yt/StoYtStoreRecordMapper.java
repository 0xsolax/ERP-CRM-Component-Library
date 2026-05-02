package com.qiaomoyun.mapper.sto.yt;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qiaomoyun.entity.sto.yt.StoYtStoreRecord;
import com.qiaomoyun.param.sto.yt.StoYtStoreRecordQueryParams;

import java.util.List;

/**
 * 库存记录Mapper接口
 */
public interface StoYtStoreRecordMapper extends BaseMapper<StoYtStoreRecord> {

    /**
     * 分页查询库存历史流向记录
     *
     * @param params 查询条件
     * @return 库存历史流向记录列表
     */
    List<StoYtStoreRecord> listByPage( StoYtStoreRecordQueryParams params);
}
