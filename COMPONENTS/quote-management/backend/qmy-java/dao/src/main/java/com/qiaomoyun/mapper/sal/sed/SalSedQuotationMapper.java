package com.qiaomoyun.mapper.sal.sed;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiaomoyun.entity.sal.sed.SalSedQuotation;
import com.qiaomoyun.param.sal.sed.SalSedQuotationHistoryImportParams;
import com.qiaomoyun.param.sal.sed.SalSedQuotationLogisticsParams;
import com.qiaomoyun.param.sal.sed.SalSedQuotationMergeListParams;
import com.qiaomoyun.param.sal.sed.SalSedQuotationParams;
import com.qiaomoyun.vo.sal.sed.SalSedQuotationDetailVo;
import com.qiaomoyun.vo.sal.sed.SalSedQuotationExportVo;
import com.qiaomoyun.vo.sal.sed.SalSedQuotationHistoryImportVo;
import com.qiaomoyun.vo.sal.sed.SalSedQuotationMergeItemVo;
import com.qiaomoyun.vo.sal.sed.SalSedQuotationVo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 报价单Mapper接口
 */

public interface SalSedQuotationMapper extends BaseMapper<SalSedQuotation> {

    /**
     * 获取报价单列表
     * @param params
     * @return
     */
    List<SalSedQuotationVo> list(SalSedQuotationParams params);


    /**
     * 根据报价单id修改报价单采购成本
     * @param id
     * @param procurementCost
     */
    void procurementConfirm(@Param("id") Long id, @Param("procurementCost") BigDecimal procurementCost);

    /**
     * 根据报价单id修改报价单物流成本和物流备注
     * @param params
     */
    void logisticsConfirm(SalSedQuotationLogisticsParams params);

    /**
     * 根据订单编号、客户名称、创建时间进行搜索历史订单
     * @param params
     * @return
     */
    List<SalSedQuotationHistoryImportVo> getHistoryQuotation(SalSedQuotationHistoryImportParams params);


    /**
     * 根据报价单id查询报价单详情
     * @param id
     * @return
     */
    SalSedQuotationDetailVo getQuotationDetailById(Long id);

    /**
     * 根据报价单id查询报价单产品信息
     * @param quotationId
     * @return
     */
    List<SalSedQuotationExportVo> getQuotationProductInfo(Long quotationId);

    /**
     * 合并转订单-列表（仅审核通过的报价单，支持报价单编号筛选）
     * @param params 筛选参数
     * @return 报价单简要信息列表
     */
    List<SalSedQuotationMergeItemVo> listForMerge(SalSedQuotationMergeListParams params);
}
