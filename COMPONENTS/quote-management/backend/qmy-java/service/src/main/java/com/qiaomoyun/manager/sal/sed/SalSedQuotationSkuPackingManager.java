package com.qiaomoyun.manager.sal.sed;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.qiaomoyun.entity.sal.sed.SalSedQuotationSkuPacking;
import com.qiaomoyun.mapper.sal.sed.SalSedQuotationSkuPackingMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class SalSedQuotationSkuPackingManager {

    @Resource
    private SalSedQuotationSkuPackingMapper salSedQuotationSkuPackingMapper;


    /**
     * 计算总运箱数
     * @param id
     * @return
     */
    public Integer calculationBox(Long id) {
        LambdaQueryWrapper<SalSedQuotationSkuPacking> queryWrapper = Wrappers.lambdaQuery(SalSedQuotationSkuPacking.class)
                .eq(SalSedQuotationSkuPacking::getQuotationId, id)
                .eq(SalSedQuotationSkuPacking::getIsDeleted, 0);
        List<SalSedQuotationSkuPacking> quotationSkuPackingList = salSedQuotationSkuPackingMapper.selectList(queryWrapper);
        //计算集合中的箱数之和(注意：因为opp袋和条码这种没有装箱数，数据库的字段为null,所以加上filter(Objects::nonNull))
        Integer totalBox = quotationSkuPackingList.stream().map(SalSedQuotationSkuPacking::getBoxMum).filter(Objects::nonNull).reduce(0, Integer::sum);
        return totalBox;
    }
}
