package com.qiaomoyun.mapper.pur.yt;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiaomoyun.entity.pur.yt.PurYtPurchasePayment;
import com.qiaomoyun.param.fin.yt.FinYtPaymentQueryParams;

import java.math.BigDecimal;
import java.util.List;

/**
 * 一唐-采购单付款Mapper接口
 */
public interface PurYtPurchasePaymentMapper extends BaseMapper<PurYtPurchasePayment> {

    /**
     * 根据条件查询采购单付款列表
     * @param params 查询条件
     * @return 采购单付款列表
     */
    List<PurYtPurchasePayment> list(FinYtPaymentQueryParams params);

    /**
     * 根据采购单ID查询已付款金额总和
     * @param purchaseId 采购单ID
     * @return 已付款金额总和
     */
    BigDecimal getTotalPaymentByPurchaseId(Long purchaseId);
}
