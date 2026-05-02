package com.qiaomoyun.mapper.sal.sed;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiaomoyun.entity.sal.sed.SalSedOrderOperateRecord;
import com.qiaomoyun.vo.sal.sed.SalSedOrderOperateRecordVo;

import java.util.List;

/**
 * 订单操作记录Mapper接口
 */
public interface SalSedOrderOperateRecordMapper extends BaseMapper<SalSedOrderOperateRecord> {

    List<SalSedOrderOperateRecordVo> selectOrderOperationLogsWithUser(Long orderId);

}
