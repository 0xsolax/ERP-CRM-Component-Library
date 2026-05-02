package com.qiaomoyun.mapper.pur.yt;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiaomoyun.entity.pur.yt.PurYtPurchaseFollow;
import com.qiaomoyun.param.pur.yt.PurYtPurchaseQueryParams;

import java.util.List;

/**
 * 采购跟进Mapper接口
 */
public interface PurYtPurchaseFollowMapper extends BaseMapper<PurYtPurchaseFollow> {

    /**
     * 根据采购订单ID查询跟进记录
     * @param params 查询参数，包含采购订单ID、开始时间和结束时间
     * @return 跟进记录列表
     */
    List<PurYtPurchaseFollow> selectByPurchaseId(PurYtPurchaseQueryParams params);

    /**
     * 批量保存采购跟进记录
     * @param purchaseFollowList 采购跟进记录列表
     */
    void saveBatch(List<PurYtPurchaseFollow> purchaseFollowList);
}