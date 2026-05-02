package com.qiaomoyun.mapper.sal.sed;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiaomoyun.entity.sal.sed.SalSedQuotationHistory;
import com.qiaomoyun.vo.sal.sed.SalSedQuotationHistoryVo;

import java.util.List;

/**
 * 报价单历史表Mapper接口
 */
public interface SalSedQuotationHistoryMapper extends BaseMapper<SalSedQuotationHistory> {

    /**
     * 根据报价单id查询历史记录信息
     * @param id
     * @return
     */
    List<SalSedQuotationHistoryVo> getQuotationHistoryList(Long id);
}