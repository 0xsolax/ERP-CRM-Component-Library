package com.qiaomoyun.mapper.pur.yt;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiaomoyun.entity.pur.yt.PurYtPurchase;
import com.qiaomoyun.param.pur.yt.PurYtPurchaseQueryParams;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 采购订单Mapper接口
 */
public interface PurYtPurchaseMapper extends BaseMapper<PurYtPurchase> {

    /**
     * 根据条件查询采购订单列表
     * @param purYtPurchase 查询条件
     * @return 采购订单列表
     */
    List<PurYtPurchase> list(PurYtPurchaseQueryParams purYtPurchase);

    /**
     * 根据编号检查是否存在
     * @param code 订单编号
     * @param excludeId 排除的ID
     * @return 是否存在
     */
    boolean existsByCode(String code, Long excludeId);

    /**
     * 查询供应商在指定日期范围内的月度采购统计
     * @param supplierId 供应商ID
     * @param startTime 起始时间
     * @param endTime 结束时间
     * @return 月度采购统计列表
     */
    List<Map<String, Object>> getMonthlyPurchaseBySupplierId(@Param("supplierId") Long supplierId,
                                                             @Param("startTime") LocalDateTime startTime,
                                                             @Param("endTime") LocalDateTime endTime);

    /**
     * 查询供应商在指定日期范围内的产品分类采购占比
     * @param supplierId 供应商ID
     * @param startTime 起始时间
     * @param endTime 结束时间
     * @return 产品分类采购占比列表
     */
    List<Map<String, Object>> getPurchaseRatioBySupplierId(@Param("supplierId") Long supplierId,
                                                          @Param("startTime") LocalDateTime startTime,
                                                        @Param("endTime") LocalDateTime endTime);
}
