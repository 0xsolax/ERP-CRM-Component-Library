package com.qiaomoyun.mapper.sal.sed;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiaomoyun.entity.sal.sed.SalSedOrderDetail;
import com.qiaomoyun.entity.sal.sed.SalSedQuotationSku;
import com.qiaomoyun.vo.sal.sed.SalSedHistoryQuotationCustomerVo;
import com.qiaomoyun.vo.sal.sed.SalSedQuotationSkuInfoVo;
import com.qiaomoyun.vo.sal.sed.SalSedQuotationSkuVo;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 报价单-SKU表Mapper接口
 */
public interface SalSedQuotationSkuMapper extends BaseMapper<SalSedQuotationSku> {
    /**
     * 根据报价单id查询报价单中的SKU信息
     * @param id
     * @return
     */
    List<SalSedQuotationSkuVo> getQuotationSkuList(Long id);

    /**
     * 合并转订单-根据报价单id查询SKU列表（支持SKU名称、搭配名称筛选）
     * @param quotationId 报价单id
     * @param skuName SKU名称，可选
     * @param matchName 搭配名称，可选
     * @return SKU列表
     */
    List<SalSedQuotationSkuVo> getQuotationSkuListForMerge(@Param("quotationId") Long quotationId,
                                                           @Param("skuName") String skuName,
                                                           @Param("matchName") String matchName);

    /**
     * 根据报价单id查询历史报价单中的SKU信息
     * @param ids
     * @return
     */
    List<SalSedQuotationSkuVo> getHistoryQuotationSkuList(List<Long> ids);

    /**
     * 根据报价单id查询报价单中的搭配id集合
     * @param id
     * @return
     */
    List<Long> getQuotationMatchIdsIds(Long id);


    /**
     * 根据skuId和搭配Id查询报价单-sku表的报价
     * @param skuId
     * @param matchId
     * @return
     */
    List<SalSedQuotationSkuInfoVo> selectQuotationPriceListBySkuIdAndMatchId(@Param("skuId") Long skuId, @Param("matchId") Long matchId);

    /**
     * 根据skuId和搭配Id查询报价单-sku表的全部客户平均报价,按照时间顺序排序
     * @param skuId
     * @param matchId
     * @return
     */
    List<SalSedHistoryQuotationCustomerVo> getAllAvgQuotationPriceList(@Param("skuId") Long skuId,@Param("matchId") Long matchId);

    /**
     * 根据skuId和搭配Id和客户id查询报价单-sku表单个客户平均报价
     * @param skuId
     * @param matchId
     * @param customerId
     * @return
     */
    List<SalSedHistoryQuotationCustomerVo> getOneAvgQuotationPriceList(@Param("skuId") Long skuId, @Param("matchId") Long matchId,@Param("customerId") Long customerId);

    /**
     * 根据skuId和搭配Id和客户id查询报价单-sku表全部客户报价
     * @param skuId
     * @param matchId
     * @return
     */
    List<SalSedHistoryQuotationCustomerVo> getAllQuotationPriceList(@Param("skuId") Long skuId,@Param("matchId") Long matchId);


    /**
     * 根据skuId和搭配Id和客户id查询报价单-sku表单个客户报价
     * @param skuId
     * @param matchId
     * @param customerId
     * @return
     */
    List<SalSedHistoryQuotationCustomerVo> getOneQuotationPriceList(@Param("skuId") Long skuId, @Param("matchId") Long matchId,@Param("customerId") Long customerId);

    /**
     *根据报价单id查询报价单-sku和报价单-sku-包材表的信息
     * @param quotationId
     * @return
     */
    List<SalSedOrderDetail> getQuotationSkuAndPackingInfoByQuotationId(Long quotationId);

    /**
     * 根据报价单 SKU id 查询报价单-sku 和报价单-sku-包材表的信息
     * @param quotationSkuId 报价单 SKU id
     * @return 报价单 SKU 和包材信息列表
     */
    List<SalSedOrderDetail> getQuotationSkuAndPackingInfoBySkuId(Long quotationSkuId);
}