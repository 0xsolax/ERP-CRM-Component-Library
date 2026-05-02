package com.qiaomoyun.mapper.pur.yt;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiaomoyun.entity.pur.yt.PurYtSupplier;
import com.qiaomoyun.param.pur.yt.PurYtSupplierQueryParams;

import java.util.Date;
import java.util.List;

public interface PurYtSupplierMapper extends BaseMapper<PurYtSupplier> {
    /**
     * 查询供应商下拉框列表
     */
    List<PurYtSupplier> selectForDropdown();

    /**
     * 查询供应商列表
     */
    List<PurYtSupplier> list(PurYtSupplierQueryParams queryParams);

    /**
     * 查询供应商最近采购时间
     * @param supplierId 供应商ID
     * @return 最近采购时间
     */
    Date selectRecentPurchaseTime(Long supplierId);

    /**
     * 查询供应商最近跟进时间
     * @param supplierId 供应商ID
     * @return 最近跟进时间
     */
    Date selectRecentFollowTime(Long supplierId);

    /**
     * 根据供应商名称查询供应商信息
     * @param name
     * @return
     */
    PurYtSupplier selectByName(String name);
}