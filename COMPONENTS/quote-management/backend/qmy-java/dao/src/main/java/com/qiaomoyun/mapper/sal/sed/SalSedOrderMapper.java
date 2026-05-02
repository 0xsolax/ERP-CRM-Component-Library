package com.qiaomoyun.mapper.sal.sed;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiaomoyun.entity.sal.sed.SalSedOrder;
import com.qiaomoyun.param.sal.sed.SalSedOrderParams;
import com.qiaomoyun.vo.sal.sed.SalSedOrderVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

import java.util.List;

/**
 * 订单Mapper接口
 */
@Mapper
public interface SalSedOrderMapper extends BaseMapper<SalSedOrder> {
    /**
     * 查询订单主表列表
     * @return 订单主表集合
     */


    List<SalSedOrderVo> list(SalSedOrderParams params);

    SalSedOrderVo orderDetail(Long id);

    /**
     * 根据客户ID查询月度消费情况
     * @param customerId 客户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 月度消费数据列表
     */
    List<Map<String, Object>> getMonthlyConsumptionByCustomerId(@Param("customerId") Long customerId,
                                                             @Param("startTime") LocalDateTime startTime,
                                                             @Param("endTime") LocalDateTime endTime);

}