package com.qiaomoyun.mapper.sal.sed;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiaomoyun.entity.sal.sed.SalSedQuotationSkuPacking;
import com.qiaomoyun.vo.sal.sed.SalSedQuotationProcurementVo;

import java.util.List;

/**
 * 报价单-SKU-包材表Mapper接口
 */
public interface SalSedQuotationSkuPackingMapper extends BaseMapper<SalSedQuotationSkuPacking> {

    /**
     * 根据报价单id获取报价单采购成本信息的包材信息、根据报价单id去查询包材的信息
     * @param id
     * @return
     */
    List<SalSedQuotationProcurementVo> getProcurementListByQuotationId(Long id);

    /**
     * 根据报价单id删除报价单采购成本信息的包材信息
     * @param idsToDelete
     */
    void deleteByQuotationSkuId(List<Long> idsToDelete);


}