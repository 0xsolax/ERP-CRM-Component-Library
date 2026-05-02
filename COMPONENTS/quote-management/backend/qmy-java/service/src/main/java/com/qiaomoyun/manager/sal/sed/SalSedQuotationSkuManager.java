package com.qiaomoyun.manager.sal.sed;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.qiaomoyun.entity.pro.sed.*;
import com.qiaomoyun.entity.sal.sed.SalSedQuotationSku;
import com.qiaomoyun.entity.sys.SysDictionary;
import com.qiaomoyun.eunm.sys.DictionaryConfigEnum;
import com.qiaomoyun.manager.pro.sed.ProSedFileManager;
import com.qiaomoyun.manager.pro.sed.ProSedProductManager;
import com.qiaomoyun.manager.sys.SysDictionaryManager;
import com.qiaomoyun.mapper.pro.sed.ProSedProductMapper;
import com.qiaomoyun.mapper.pro.sed.ProSedProductMatchFittingMapper;
import com.qiaomoyun.mapper.pro.sed.ProSedProductMatchMapper;
import com.qiaomoyun.mapper.pro.sed.ProSedProductMatchSkuMapper;
import com.qiaomoyun.mapper.sal.sed.SalSedQuotationSkuMapper;
import com.qiaomoyun.mapper.sys.SysStorageMapper;
import com.qiaomoyun.vo.sal.sed.SalSedQuotationFittingDetailVo;
import com.qiaomoyun.vo.sal.sed.SalSedQuotationSkuVo;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class SalSedQuotationSkuManager {

    @Resource
    private SalSedQuotationSkuMapper salSedQuotationSkuMapper;

    @Resource
    private ProSedFileManager proSedFileManager;

    @Resource
    private ProSedProductManager proSedProductManager;

    @Resource
    private ProSedProductMatchSkuMapper proSedProductMatchSkuMapper;

    @Resource
    private ProSedProductMatchMapper proSedProductMatchMapper;

    @Resource
    private ProSedProductMapper proSedProductMapper;

    @Resource
    private SysDictionaryManager sysDictionaryManager;

    @Resource
    private ProSedProductMatchFittingMapper proSedProductMatchFittingMapper;

    @Autowired
    private SysStorageMapper sysStorageMapper;

    /**
     * 计算总运输体积
     *
     * @param id
     * @return
     */
    public BigDecimal calculationVolume(Long id) {
        //根据报价单id查询报价单-sku表信息
        LambdaQueryWrapper<SalSedQuotationSku> queryWrapper = Wrappers.lambdaQuery(SalSedQuotationSku.class)
                .eq(SalSedQuotationSku::getQuotationId, id)
                .eq(SalSedQuotationSku::getIsDeleted, 0);
        List<SalSedQuotationSku> quotationSkuList = salSedQuotationSkuMapper.selectList(queryWrapper);
        //计算集合中的体积之和
        BigDecimal totalVolume = quotationSkuList.stream().map(SalSedQuotationSku::getVolume).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalVolume;
    }

    /**
     * 根据报价单id查询报价单中的SKU信息
     *
     * @param id
     * @return
     */
    public List<SalSedQuotationSkuVo> getQuotationSkuList(Long id) {
        List<SalSedQuotationSkuVo> quotationSkuList = salSedQuotationSkuMapper.getQuotationSkuList(id);
        //循环查询得到sku图片地址，然后再赋值
        for (SalSedQuotationSkuVo quotationSku : quotationSkuList) {
            //调用图片地址查询方法
            List<ProSedFile> skuImageList = proSedFileManager.selectSkuImage(quotationSku.getSkuId());
            for (ProSedFile file : skuImageList) {
                if (quotationSku.getPic() == null) {
                    quotationSku.setPic(new ArrayList<>());
                }
                quotationSku.getPic().add(file.getUrl());
            }
            //查询sku的包材的附件信息  报价单-skuId
            List<ProSedFile> packageImageList = proSedFileManager.selectQuotationPackingFile(quotationSku.getQuotationSkuId());
            //查询附件的文件名称
            for (ProSedFile file : packageImageList) {
                file.setFileName(sysStorageMapper.selectById(file.getStorageId()).getName());
            }
            quotationSku.setAttachmentList(packageImageList);
            //查询SKU的信息
            ProSedProductMatchSku sku = proSedProductMatchSkuMapper.selectById(quotationSku.getSkuId());
            //查询SKU的对应搭配信息
            ProSedProductMatch proSedProductMatch = proSedProductMatchMapper.selectById(sku.getMatchId());
            //查询sku对应的产品信息
            ProSedProduct product = proSedProductMapper.selectById(proSedProductMatch.getProductId());
            SysDictionary painting = sysDictionaryManager.getByCodeAndKey(DictionaryConfigEnum.paintingPrice.getKey(), DictionaryConfigEnum.paintingPrice.getKey());
            //成本=配件成本+工艺成本
            //工艺成本
            if (sku.getEffectId() != null) {
                proSedProductManager.hangleProcessAndCost(sku, product.getArea(), painting.getValue());
            }
            //单个工艺成本*数量
            BigDecimal craftCost;
            if (null == sku.getCraftCost()) {
                craftCost = BigDecimal.ZERO;
                sku.setCraftCost(BigDecimal.ZERO);
            } else {
                craftCost = sku.getCraftCost().multiply(BigDecimal.valueOf(quotationSku.getNumber()));
            }

            //配件成本   quotationSku.getNumber()--SKU数量
            //根据搭配id得到配件相关成本单价和搭配数量 ，得到搭配成本总和
            List<SalSedQuotationFittingDetailVo> fittingDetail = proSedProductMatchFittingMapper.getFittingListByMatchId(quotationSku.getCombinationId());
            //单个sku的配件成本
            BigDecimal fittingCost = BigDecimal.ZERO;
            for (SalSedQuotationFittingDetailVo quotationFittingDetail : fittingDetail) {
                if (quotationFittingDetail.getUnitCost() == null) {
                    quotationFittingDetail.setUnitCost(BigDecimal.ZERO);
                }
                fittingCost = fittingCost.add(quotationFittingDetail.getUnitCost().multiply(BigDecimal.valueOf(quotationFittingDetail.getFittingNum())));
            }

            //配件总成本呢等于单个sku的配件成本*配件数量
            BigDecimal fittingCosts = fittingCost.multiply(BigDecimal.valueOf(quotationSku.getNumber()));

            quotationSku.setCost(sku.getCraftCost().add(fittingCost));
            quotationSku.setTotalCost(craftCost.add(fittingCosts));
        }
        return quotationSkuList;
    }

}
