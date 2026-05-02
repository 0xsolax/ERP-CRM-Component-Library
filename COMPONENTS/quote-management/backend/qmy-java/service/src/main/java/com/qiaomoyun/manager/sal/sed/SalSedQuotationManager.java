package com.qiaomoyun.manager.sal.sed;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.qiaomoyun.Exception.BizException;
import com.qiaomoyun.entity.pro.sed.ProSedFile;
import com.qiaomoyun.entity.pro.sed.ProSedPacking;
import com.qiaomoyun.entity.pro.sed.ProSedProduct;
import com.qiaomoyun.entity.pro.sed.ProSedProductMatch;
import com.qiaomoyun.entity.pro.sed.ProSedProductMatchSku;
import com.qiaomoyun.entity.pro.sed.ProSedSize;
import com.qiaomoyun.entity.pro.sed.ProSedSkuEffect;
import com.qiaomoyun.entity.pro.sed.ProSedSkuEffectColor;
import com.qiaomoyun.entity.sal.sed.SalSedOrder;
import com.qiaomoyun.entity.sal.sed.SalSedOrderDetail;
import com.qiaomoyun.entity.sal.sed.SalSedOrderOperateRecord;
import com.qiaomoyun.entity.sal.sed.SalSedQuotation;
import com.qiaomoyun.entity.sal.sed.SalSedQuotationSku;
import com.qiaomoyun.entity.sal.sed.SalSedQuotationSkuPacking;
import com.qiaomoyun.entity.sal.yt.SalYtCustomer;
import com.qiaomoyun.entity.sys.SysDictionary;
import com.qiaomoyun.eunm.sed.FileTypeEnum;
import com.qiaomoyun.eunm.sed.OrderGjpSyncStatusEnum;
import com.qiaomoyun.eunm.sed.QuotationStatusEnum;
import com.qiaomoyun.eunm.sys.DictionaryConfigEnum;
import com.qiaomoyun.manager.pro.sed.ProSedFileManager;
import com.qiaomoyun.manager.pro.sed.ProSedProductManager;
import com.qiaomoyun.manager.sys.SysDictionaryManager;
import com.qiaomoyun.mapper.pro.sed.ProSedEffectMapper;
import com.qiaomoyun.mapper.pro.sed.ProSedFittingMapper;
import com.qiaomoyun.mapper.pro.sed.ProSedFittingPartMapper;
import com.qiaomoyun.mapper.pro.sed.ProSedPackingMapper;
import com.qiaomoyun.mapper.pro.sed.ProSedProductMapper;
import com.qiaomoyun.mapper.pro.sed.ProSedProductMatchFittingMapper;
import com.qiaomoyun.mapper.pro.sed.ProSedProductMatchMapper;
import com.qiaomoyun.mapper.pro.sed.ProSedProductMatchSkuMapper;
import com.qiaomoyun.mapper.pro.sed.ProSedSizeMapper;
import com.qiaomoyun.mapper.pro.sed.ProSedSkuEffectColorMapper;
import com.qiaomoyun.mapper.pro.sed.ProSedSkuEffectMapper;
import com.qiaomoyun.mapper.sal.sed.SalSedOrderDetailMapper;
import com.qiaomoyun.mapper.sal.sed.SalSedOrderMapper;
import com.qiaomoyun.mapper.sal.sed.SalSedOrderOperateRecordMapper;
import com.qiaomoyun.mapper.sal.sed.SalSedQuotationMapper;
import com.qiaomoyun.mapper.sal.sed.SalSedQuotationSkuMapper;
import com.qiaomoyun.mapper.sal.sed.SalSedQuotationSkuPackingMapper;
import com.qiaomoyun.mapper.sal.yt.SalYtCustomerMapper;
import com.qiaomoyun.param.sal.sed.SalSedHistoryQuotationInfoParams;
import com.qiaomoyun.param.sal.sed.SalSedQuotationCostDetailParams;
import com.qiaomoyun.param.sal.sed.SalSedQuotationExportParams;
import com.qiaomoyun.param.sal.sed.SalSedQuotationMergeListParams;
import com.qiaomoyun.param.sal.sed.SalSedQuotationMergeToOrderParams;
import com.qiaomoyun.param.sal.sed.SalSedQuotationOneKeyToOrderParams;
import com.qiaomoyun.param.sal.sed.SalSedQuotationSkuToOrderParams;
import com.qiaomoyun.util.EntityCodeGenerateUtil;
import com.qiaomoyun.util.QMYExcelUtil;
import com.qiaomoyun.vo.pro.sed.ProSedFileVO;
import com.qiaomoyun.vo.sal.sed.SalSedHistoryQuotationCustomerVo;
import com.qiaomoyun.vo.sal.sed.SalSedHistoryQuotationInfoVo;
import com.qiaomoyun.vo.sal.sed.SalSedQuotationCostDetailShiftVo;
import com.qiaomoyun.vo.sal.sed.SalSedQuotationCostDetailVo;
import com.qiaomoyun.vo.sal.sed.SalSedQuotationDetailVo;
import com.qiaomoyun.vo.sal.sed.SalSedQuotationExportVo;
import com.qiaomoyun.vo.sal.sed.SalSedQuotationFittingDetailShiftVo;
import com.qiaomoyun.vo.sal.sed.SalSedQuotationFittingDetailVo;
import com.qiaomoyun.vo.sal.sed.SalSedQuotationMergeItemVo;
import com.qiaomoyun.vo.sal.sed.SalSedQuotationMergeSkuItemVo;
import com.qiaomoyun.vo.sal.sed.SalSedQuotationPackagingDetailShiftVo;
import com.qiaomoyun.vo.sal.sed.SalSedQuotationPackagingDetailVo;
import com.qiaomoyun.vo.sal.sed.SalSedQuotationProcurementVo;
import com.qiaomoyun.vo.sal.sed.SalSedQuotationSkuInfoVo;
import com.qiaomoyun.vo.sal.sed.SalSedQuotationSkuVo;
import com.qiaomoyun.vo.sal.sed.SalSedQuotationVo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 报价单管理
 */
@Slf4j
@Component
public class SalSedQuotationManager {

    @Resource
    private SalSedQuotationMapper salSedQuotationMapper;

    @Resource
    private SalSedQuotationSkuPackingMapper salSedQuotationSkuPackingMapper;

    @Resource
    private ProSedFileManager proSedFileManager;

    @Resource
    private SalSedQuotationSkuManager salSedQuotationSkuManager;

    @Resource
    private SalSedQuotationSkuMapper salSedQuotationSkuMapper;

    @Resource
    private ProSedFittingMapper proSedFittingMapper;

    @Resource
    private ProSedFittingPartMapper proSedFittingPartMapper;

    @Resource
    private ProSedProductMatchFittingMapper proSedProductMatchFittingMapper;

    @Resource
    private ProSedPackingMapper proSedPackingMapper;

    @Resource
    private ProSedEffectMapper proSedEffectMapper;

    @Resource
    private ProSedProductMapper proSedProductMapper;

    @Resource
    private SysDictionaryManager sysDictionaryManager;

    @Resource
    private ProSedProductMatchSkuMapper proSedProductMatchSkuMapper;

    @Resource
    private ProSedProductMatchMapper proSedProductMatchMapper;

    @Resource
    private ProSedProductManager proSedProductManager;

    @Resource
    private ProSedSkuEffectMapper proSedSkuEffectMapper;

    @Resource
    private ProSedSkuEffectColorMapper proSedSkuEffectColorMapper;
    @Resource
    private SalYtCustomerMapper salYtCustomerMapper;
    @Resource
    private SalSedOrderMapper salSedOrderMapper;

    @Resource
    private SalSedOrderDetailMapper salSedOrderDetailMapper;

    @Resource
    private ProSedSizeMapper proSedSizeMapper;

    @Resource
    private SalSedOrderOperateRecordMapper salSedOrderOperateRecordMapper;

//    /**
//     * 获取报价单列表
//     * @param params
//     * @return
//     */
//    public PageResultInfo<SalSedQuotationVo> list(SalSedQuotationParams params) {
//        PageHelper.startPage(params.getPageNum(), params.getPageSize());
//        List<SalSedQuotationVo> list = salSedQuotationMapper.list(params);
//        return new PageResultInfo<>(list);
//    }

    /**
     * 获取报价单采购成本详情
     * @param id
     * @return
     */
    public SalSedQuotationVo procurementDetail(Long id) {
        SalSedQuotationVo quotation = new SalSedQuotationVo();

        //查询包材信息
        List<SalSedQuotationProcurementVo> procurementList =salSedQuotationSkuPackingMapper.getProcurementListByQuotationId(id);
        //根据包材的id查询包材的附件信息
        List<ProSedFile> attachmentsLists=new ArrayList<>();
        Map<String,SalSedQuotationProcurementVo> map=new HashMap<>();
        for (SalSedQuotationProcurementVo procurement:procurementList) {
            List<ProSedFile> list=proSedFileManager.selectQuotationPackingFile(procurement.getId());
            attachmentsLists.addAll(list);

            //处理重复数据，将数量加起来，同时去重
            //使用 packingId + packingSize 作为key，区分相同类型名称但不同尺寸的包材
            String key = procurement.getPackingId() + "_" + (procurement.getPackingSize() != null ? procurement.getPackingSize() : "");
            if(!map.containsKey(key)){
                map.put(key,procurement);
            }else{
                SalSedQuotationProcurementVo procurement1 =  map.get(key);
                procurement1.setBoxMum(procurement1.getBoxMum()+procurement.getBoxMum());
            }
        }

        List<SalSedQuotationProcurementVo> procurementList1 = new ArrayList<>(map.values());


        Map<String,List<SalSedQuotationProcurementVo>>  procurementMap=new HashMap<>();
        //包材信息
        procurementMap.put("包材",procurementList1);
        //附件信息
        quotation.setAttachmentsLists(attachmentsLists);
        //配件信息
         //根据报价单id查询报价单的搭配id集合
         List<Long> matchIds=salSedQuotationSkuMapper.getQuotationMatchIdsIds(id);
        List<SalSedQuotationProcurementVo> fittingList= proSedFittingMapper.getFittingListByQuotationId(matchIds);
          //填充尺寸信息
        List<SalSedQuotationProcurementVo> fittingSizeList=groupByZhuIdAndSpliceSize(fittingList);
        //添加一条测试数据
          //---------------------------------------------------
//        SalSedQuotationProcurementVo test=new SalSedQuotationProcurementVo();
//        test.setFittingId(1L);
//        test.setName("测试数据橡皮塞");
//        test.setSize("长：10cm宽5cm");
//        test.setBoxMum(100);
//        test.setCostPrice(new BigDecimal("0.01"));
//        fittingSizeList.add(test);
          //---------------------------------------------------------
        procurementMap.put("配件",fittingSizeList);

        //零件信息

        List<SalSedQuotationProcurementVo> fittingList1= proSedFittingMapper.getFittingListByQuotationIdNoResource(matchIds);
        //获取零件id
        Set<Long> partIds=new HashSet<>();
        for (SalSedQuotationProcurementVo fitting:fittingList1){
            partIds.add(fitting.getFittingId());
        }
        //根据配件id查询配件零件信息
        List<SalSedQuotationProcurementVo> partSizeList=new ArrayList<>();
        if(!partIds.isEmpty()) {
            List<SalSedQuotationProcurementVo> partList = proSedFittingPartMapper.getPartListByPartIds(partIds);
            if(!partList.isEmpty()) {
                partSizeList = mergePartSize(partList);
            }
        }

        procurementMap.put("零件", partSizeList);

        //总计金额(包材+零件+配件)=采购成本
        LambdaQueryWrapper<SalSedQuotation> queryWrapper = Wrappers.lambdaQuery(SalSedQuotation.class)
                .eq(SalSedQuotation::getId,id)
                .select(SalSedQuotation::getProcurementCost);
        SalSedQuotation salSedQuotation = salSedQuotationMapper.selectOne(queryWrapper);
        BigDecimal totalPrice=BigDecimal.ZERO;
        if(salSedQuotation!= null) {
            totalPrice = salSedQuotation.getProcurementCost();
        }


        //封装数据
        quotation.setProcurementMap(procurementMap);
        quotation.setProcurementCost(totalPrice);


        return quotation;

    }


    /**
     * 根据报价单id查询报价单相关信息
     * @param id
     * @return
     */
    public SalSedQuotationDetailVo quotationDetail(Long id) {
        SalSedQuotationDetailVo quotationDetailVo = salSedQuotationMapper.getQuotationDetailById(id);
        //运输体积
        quotationDetailVo.setVolume( salSedQuotationSkuManager.calculationVolume(id));

        //如果币种是美元，需要把订单金额换算成人民币后再计算毛利率和物流占订单比例
        BigDecimal orderAmountRmb = quotationDetailVo.getOrderAmount();
        if ("USD".equals(quotationDetailVo.getCurrency()) && quotationDetailVo.getExchangeRate() != null && quotationDetailVo.getOrderAmount() != null) {
            //订单金额(人民币) = 订单金额(美元) * 汇率
            orderAmountRmb = quotationDetailVo.getOrderAmount().multiply(quotationDetailVo.getExchangeRate());
        }

        // 物流占订单比例、毛利率 在 Service.quotationDetail 中按公式统一计算（含 sum(配件+工艺)，美元时订单金额先乘汇率再算）
        // 此处仅当无采购/物流成本时保留物流占订单比例，避免详情页完全无比例
        if (quotationDetailVo.getLogisticsCost() != null && orderAmountRmb != null && orderAmountRmb.compareTo(BigDecimal.ZERO) > 0) {
            quotationDetailVo.setLogisticsProportion(quotationDetailVo.getLogisticsCost().divide(orderAmountRmb, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")));
        }

        // 查询总裁微信审核凭证图片列表
        List<ProSedFile> presidentWxAuditImageList = proSedFileManager.selectByMasterIdAndType(id, FileTypeEnum.presidentWxAuditImage.getKey());
        quotationDetailVo.setPresidentWxAuditImageList(BeanUtil.copyToList(presidentWxAuditImageList, ProSedFileVO.class));

        return quotationDetailVo;
    }

    public static List<SalSedQuotationProcurementVo> groupByZhuIdAndSpliceSize(List<SalSedQuotationProcurementVo> originalList) {
        // 1. 判空处理，避免空指针异常
        if (originalList == null || originalList.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 第一步：按zhuId分组，key=zhuId，value=该zhuId对应的所有原始Vo对象
        Map<Long, List<SalSedQuotationProcurementVo>> zhuIdGroupMap = originalList.stream()
                .collect(Collectors.groupingBy(SalSedQuotationProcurementVo::getFittingId));

        // 3. 第二步：遍历每个分组，拼接尺寸并构建唯一Vo对象
        List<SalSedQuotationProcurementVo> finalVoList = new ArrayList<>();
        for (Map.Entry<Long, List<SalSedQuotationProcurementVo>> entry : zhuIdGroupMap.entrySet()) {
            Long zhuId = entry.getKey();
            List<SalSedQuotationProcurementVo> sameZhuIdVoList = entry.getValue();

            // 3.1 构建当前zhuId对应的唯一Vo对象（复用原始Vo的公共属性）
            SalSedQuotationProcurementVo uniqueVo = new SalSedQuotationProcurementVo();
            if (!sameZhuIdVoList.isEmpty()) {
                // 取分组内第一个Vo的公共属性（name/boxMum/costPrice，同一zhuId属性一致）
                SalSedQuotationProcurementVo firstVo = sameZhuIdVoList.get(0);
                uniqueVo.setFittingId(zhuId);
                uniqueVo.setName(firstVo.getName());
                uniqueVo.setBoxMum(firstVo.getBoxMum());
                uniqueVo.setCostPrice(firstVo.getCostPrice());
            }

            // 3.2 拼接尺寸：遍历该分组下所有Vo，拼接 "latitude：value"，用制表符\t分隔（和你的示例格式一致）
            String splicedSize = sameZhuIdVoList.stream()
                    // 过滤掉latitude或value为null的情况，避免拼接出无效字符串
                    .filter(vo -> vo.getLatitude() != null && vo.getValue() != null)
                    // 将每个Vo转为 "纬度：数值" 格式
                    .map(vo -> vo.getLatitude() + "：" + vo.getValue())
                    // 拼接所有字符串，分隔符可选（\t/空格/逗号）
                    .collect(Collectors.joining("\t"));

            // 3.3 将拼接后的尺寸赋值给uniqueVo的size字段
            uniqueVo.setSize(splicedSize);
            // 纬度和数值字段无需保留（若需保留，可按需赋值，默认null即可）
            uniqueVo.setLatitude(null);
            uniqueVo.setValue(null);

            // 3.4 将唯一Vo添加到最终列表
            finalVoList.add(uniqueVo);
        }

        return finalVoList;
    }


    public static List<SalSedQuotationProcurementVo> mergePartSize(List<SalSedQuotationProcurementVo> originalList) {
        // 1. 空值处理：避免空指针
        if (CollectionUtils.isEmpty(originalList)) {
            return new ArrayList<>();
        }

        // 2. 核心Map：key=partId，value=[0]原始对象(第一条)、[1]拼接的size字符串
        Map<Long, Object[]> partMap = new HashMap<>();

        // 3. 遍历原始列表，分组+拼接尺寸
        for (SalSedQuotationProcurementVo vo : originalList) {
            Long partId = vo.getPartId();
            // 跳过partId为空的无效数据
            if (partId == null) {
                continue;
            }

            // 初始化分组：key不存在时，存入第一条原始对象+空size
            partMap.computeIfAbsent(partId, k -> new Object[]{vo, ""});

            // 取出当前分组的size字符串，拼接新的尺寸（仅处理非空的纬度/数值）
            String currentSize = (String) partMap.get(partId)[1];
            if (StringUtils.hasText(vo.getLatitude()) && StringUtils.hasText(vo.getValue())) {
                currentSize += vo.getLatitude() + "：" + vo.getValue();
            }
            // 更新拼接后的size
            partMap.get(partId)[1] = currentSize;
        }

        // 4. 转换为最终列表：给每个partId的原始对象设置拼接后的size
        List<SalSedQuotationProcurementVo> resultList = new ArrayList<>();
        for (Map.Entry<Long, Object[]> entry : partMap.entrySet()) {
            // 取出该partId的第一条原始对象
            SalSedQuotationProcurementVo targetVo = (SalSedQuotationProcurementVo) entry.getValue()[0];
            // 仅更新size字段，其他属性完全保留原始值
            targetVo.setSize((String) entry.getValue()[1]);
            resultList.add(targetVo);
        }

        return resultList;
    }

    /**
     * 成本明细
     */
    public SalSedQuotationCostDetailShiftVo getCostDetail(SalSedQuotationCostDetailParams params) {
        SalSedQuotationCostDetailVo quotationCostDetailVo = new SalSedQuotationCostDetailVo();

        //配件明细
        //配件总成本
        BigDecimal fittingCost=BigDecimal.ZERO;
        //根据搭配id查询配件信息
        List<SalSedQuotationFittingDetailVo> fittingDetail= proSedProductMatchFittingMapper.getFittingListByMatchId(params.getMatchId());
        //查询配件的图片地址,可能会有多个图片
        for (SalSedQuotationFittingDetailVo quotationFittingDetail : fittingDetail) {
            List<ProSedFile> fittingImageList = proSedFileManager.selectByMasterIdAndType(quotationFittingDetail.getFittingId(), FileTypeEnum.fitting.getKey());
            for(ProSedFile fittingImage : fittingImageList){
                if(quotationFittingDetail.getPic()==null){
                    quotationFittingDetail.setPic(new ArrayList<>());
                }
                quotationFittingDetail.getPic().add(fittingImage.getUrl());
            }
            //配件成本=单个成本*搭配配件数量（这个地方只算单个sku的配件成本，所以跟客户要的sku数量无关）
            if(quotationFittingDetail.getUnitCost()==null){
                quotationFittingDetail.setUnitCost(BigDecimal.ZERO);
            }
            fittingCost =fittingCost.add( quotationFittingDetail.getUnitCost().multiply(BigDecimal.valueOf(quotationFittingDetail.getFittingNum())));
        }
        quotationCostDetailVo.setFittingDetail(fittingDetail);
        //配件总成本
        quotationCostDetailVo.setFittingCost(fittingCost);



        //效果明细
        //根据skuId查询效果明细(效果id、效果名称、人工工序量、油漆工序量)
        SalSedQuotationCostDetailVo effectInfo = proSedEffectMapper.getEffectListBySkuId(params.getSkuId());
        if (effectInfo != null) {
            quotationCostDetailVo.setEffectId(effectInfo.getEffectId());
            quotationCostDetailVo.setEffectName(effectInfo.getEffectName());
            quotationCostDetailVo.setManualProcessQuantity(effectInfo.getManualProcessQuantity());
            quotationCostDetailVo.setPaintingProcessQuantity(effectInfo.getPaintingProcessQuantity());
        }
        //计算人工成本和油漆成本   calculateCraftCost();
        //查询SKU的信息
        ProSedProductMatchSku sku = proSedProductMatchSkuMapper.selectById(params.getSkuId());
        ProSedProduct product = null;
        if (sku != null) {
            BigDecimal manualProcessQuantity = sku.getManualProcessQuantity();
            BigDecimal paintingProcessQuantity = sku.getPaintingProcessQuantity();
            if(manualProcessQuantity!=null){
                quotationCostDetailVo.setManualProcessQuantity(manualProcessQuantity);
            }
            if(paintingProcessQuantity!=null){
                quotationCostDetailVo.setPaintingProcessQuantity(paintingProcessQuantity);
            }
            //查询SKU的对应搭配信息
            ProSedProductMatch proSedProductMatch = proSedProductMatchMapper.selectById(sku.getMatchId());
            if(proSedProductMatch != null) {
                //查询sku对应的产品信息
                product = proSedProductMapper.selectById(proSedProductMatch.getProductId());
            }
        }
        SysDictionary painting = sysDictionaryManager.getByCodeAndKey(DictionaryConfigEnum.paintingPrice.getKey(), DictionaryConfigEnum.paintingPrice.getKey());
        if(product != null && quotationCostDetailVo.getManualProcessQuantity()!=null && quotationCostDetailVo.getPaintingProcessQuantity()!=null){
            Map<String,BigDecimal> craftCostMap = proSedProductManager.calculateCraftCost(quotationCostDetailVo.getManualProcessQuantity(),quotationCostDetailVo.getPaintingProcessQuantity(),product.getArea(),painting.getValue());
            //得到人工成本（面积不在配置区间内则为null）
            BigDecimal manualCost = craftCostMap.get("manualCost");
            //得到油漆成本（面积不在配置区间内则为null）
            BigDecimal paintingCost = craftCostMap.get("paintingCost");
            quotationCostDetailVo.setManualProcessCost(manualCost);
            quotationCostDetailVo.setPaintingProcessCost(paintingCost);
            //效果总成本（两个都为null时总成本也为null，否则null当0处理）
            if (manualCost == null && paintingCost == null) {
                quotationCostDetailVo.setEffectCost(null);
            } else {
                BigDecimal effectCost = (manualCost == null ? BigDecimal.ZERO : manualCost)
                        .add(paintingCost == null ? BigDecimal.ZERO : paintingCost);
                quotationCostDetailVo.setEffectCost(effectCost);
            }
        }else {
            quotationCostDetailVo.setManualProcessCost(BigDecimal.ZERO);
            quotationCostDetailVo.setPaintingProcessCost(BigDecimal.ZERO);
            quotationCostDetailVo.setEffectCost(BigDecimal.ZERO);
        }

        //工艺明细
         //根据skuId查询工艺明细
        List<ProSedSkuEffect> effectList=proSedSkuEffectMapper.selectBySkuId(params.getSkuId());
        for(ProSedSkuEffect effect:effectList){
            Long id = effect.getId();
            List<ProSedSkuEffectColor> proSedSkuEffectColors = proSedSkuEffectColorMapper.selectBySkuEffectId(id);
            effect.setColorList(proSedSkuEffectColors);
        }
        quotationCostDetailVo.setEffectDetail(effectList);


        //包材明细
        //根据报价单-sku表的id查询包材信息
        List<SalSedQuotationPackagingDetailVo> packagingDetail= proSedPackingMapper.getPackagingListByIds(params.getQuotationSkuId());
        //这里的成本等于单个包材成本/装箱数
        BigDecimal packagingCost=BigDecimal.ZERO;
        for (SalSedQuotationPackagingDetailVo quotationPackagingDetail : packagingDetail) {
            if(quotationPackagingDetail.getCost()==null){
                quotationPackagingDetail.setCost(BigDecimal.ZERO);
            }
            quotationPackagingDetail.setCost(quotationPackagingDetail.getCost().divide(BigDecimal.valueOf(quotationPackagingDetail.getPackagingNum()),2,RoundingMode.HALF_UP));
            packagingCost=packagingCost.add(quotationPackagingDetail.getCost());
        }
        quotationCostDetailVo.setPackagingDetail(packagingDetail);
        //包材总成本
        quotationCostDetailVo.setPackagingCost(packagingCost);

        //转换
        SalSedQuotationCostDetailShiftVo quotationCostDetailShiftVo = new SalSedQuotationCostDetailShiftVo();
        shiftCostDetailVo(quotationCostDetailShiftVo,quotationCostDetailVo);

        return quotationCostDetailShiftVo;
    }


    public void shiftCostDetailVo(SalSedQuotationCostDetailShiftVo quotationCostDetailShiftVo,SalSedQuotationCostDetailVo quotationCostDetailVo){
        //复制主属性
        BeanUtils.copyProperties(quotationCostDetailVo,quotationCostDetailShiftVo);
        //配件总成本
        if (quotationCostDetailVo.getFittingCost() != null) {
            quotationCostDetailShiftVo.setFittingCost(quotationCostDetailVo.getFittingCost().setScale(4, RoundingMode.HALF_UP).toString());
        }
        //效果总成本
        if (quotationCostDetailVo.getEffectCost() != null) {
            quotationCostDetailShiftVo.setEffectCost(quotationCostDetailVo.getEffectCost().setScale(4, RoundingMode.HALF_UP).toString());
        }
        //包材总成本
        if (quotationCostDetailVo.getPackagingCost() != null) {
            quotationCostDetailShiftVo.setPackagingCost(quotationCostDetailVo.getPackagingCost().setScale(4, RoundingMode.HALF_UP).toString());
        }
        //复制配件集合属性
        if (quotationCostDetailVo.getFittingDetail() != null && !quotationCostDetailVo.getFittingDetail().isEmpty()) {
            List<SalSedQuotationFittingDetailShiftVo> fittingDetailShiftVoList = new ArrayList<>();
            for (SalSedQuotationFittingDetailVo fittingDetail : quotationCostDetailVo.getFittingDetail()) {
                SalSedQuotationFittingDetailShiftVo fittingDetailShiftVo = new SalSedQuotationFittingDetailShiftVo();
                BeanUtils.copyProperties(fittingDetail,fittingDetailShiftVo);
                //将成本单价转换成String
                if (fittingDetail.getUnitCost() != null) {
                    fittingDetailShiftVo.setUnitCost(fittingDetail.getUnitCost().setScale(4, RoundingMode.HALF_UP).toString());
                }

                fittingDetailShiftVoList.add(fittingDetailShiftVo);
            }
            //加入配件明细
            quotationCostDetailShiftVo.setFittingDetail(fittingDetailShiftVoList);
        }

        //复制包材明细
        if (quotationCostDetailVo.getPackagingDetail() != null && !quotationCostDetailVo.getPackagingDetail().isEmpty()) {
            List<SalSedQuotationPackagingDetailShiftVo> packagingDetailShiftVoList = new ArrayList<>();
            for (SalSedQuotationPackagingDetailVo packagingDetail : quotationCostDetailVo.getPackagingDetail()) {
                SalSedQuotationPackagingDetailShiftVo packagingDetailShiftVo = new SalSedQuotationPackagingDetailShiftVo();
                BeanUtils.copyProperties(packagingDetail,packagingDetailShiftVo);
                //将成本单价转换成String
                if (packagingDetail.getCost() != null) {
                    packagingDetailShiftVo.setCost(packagingDetail.getCost().setScale(4, RoundingMode.HALF_UP).toString());
                }

                packagingDetailShiftVoList.add(packagingDetailShiftVo);
            }
            quotationCostDetailShiftVo.setPackagingDetail(packagingDetailShiftVoList);

        }


    }

    /**
     * 历史报价信息
     */
    public SalSedHistoryQuotationInfoVo getHistoryQuotationInfo(SalSedHistoryQuotationInfoParams params) {
        SalSedHistoryQuotationInfoVo historyQuotationInfoVo = new SalSedHistoryQuotationInfoVo();
        //得到sku的图片地址
         List<ProSedFile> picList = proSedFileManager.selectSkuImage(params.getSkuId());
         historyQuotationInfoVo.setPic(picList);
        //根据产品id得到产品的型号
        ProSedProduct proSedProduct = proSedProductMapper.selectById(params.getProductId());
        historyQuotationInfoVo.setProductCode(proSedProduct.getCode());

        //效果明细
        //工艺明细
        //根据skuId查询工艺明细
        List<ProSedSkuEffect> effectList=proSedSkuEffectMapper.selectBySkuId(params.getSkuId());
        for(ProSedSkuEffect effect:effectList){
            Long id = effect.getId();
            List<ProSedSkuEffectColor> proSedSkuEffectColors = proSedSkuEffectColorMapper.selectBySkuEffectId(id);
            effect.setColorList(proSedSkuEffectColors);
        }
        historyQuotationInfoVo.setEffectDetail(effectList);

        //当前报价单币种（本次报价按该币种返回，前端展示¥或$）
        if (params.getQuotationSkuId() != null) {
            SalSedQuotationSku sku = salSedQuotationSkuMapper.selectById(params.getQuotationSkuId());
            if (sku != null) {
                SalSedQuotation quotation = salSedQuotationMapper.selectById(sku.getQuotationId());
                if (quotation != null) {
                    historyQuotationInfoVo.setCurrency(quotation.getCurrency());
                }
            }
        }
        if (historyQuotationInfoVo.getCurrency() == null) {
            historyQuotationInfoVo.setCurrency("CNY");
        }

        //报价相关信息（本次报价=报价单币种；平均报价/报价中位数=人民币，美元已按汇率折算）
        Map<String,BigDecimal> historyQuotesCost = getHistoryQuotesCost(params);
           //本次报价(解决新增报价单时没有报价单-sku表的id的情况)，保持报价单原币种
        if(historyQuotesCost.containsKey("thisPrice")) {
            historyQuotationInfoVo.setThisPrice(historyQuotesCost.get("thisPrice"));
        }
           //平均报价（始终为人民币）
        if(historyQuotesCost.containsKey("averagePrice")) {
            historyQuotationInfoVo.setAveragePrice(historyQuotesCost.get("averagePrice"));
        }
           //报价中位数（始终为人民币）
        if(historyQuotesCost.containsKey("medianPrice")) {
            historyQuotationInfoVo.setMedianPrice(historyQuotesCost.get("medianPrice"));
        }


        //毛利率相关信息
        Map<String,BigDecimal> historyQuotesMargin = getHistoryQuotesMargin(params);
          //本次毛利率(解决新增报价单时没有报价单-sku表的id的情况)
        if(historyQuotesMargin.containsKey("grossProfitRate")) {
            historyQuotationInfoVo.setGrossProfitRate(historyQuotesMargin.get("grossProfitRate"));
        }
          //平均毛利率
        if(historyQuotesMargin.containsKey("averageGrossProfitRate")) {
            historyQuotationInfoVo.setAverageGrossProfitRate(historyQuotesMargin.get("averageGrossProfitRate"));
        }
          //毛利率中位数
        if(historyQuotesMargin.containsKey("medianGrossProfitRate")) {
            historyQuotationInfoVo.setMedianGrossProfitRate(historyQuotesMargin.get("medianGrossProfitRate"));
        }


        //全部客户报价趋势
           //根据skuId和搭配Id查询报价单-sku表的平均报价
        List<SalSedHistoryQuotationCustomerVo> allQuotationTrend=salSedQuotationSkuMapper.getAllAvgQuotationPriceList(params.getSkuId(),params.getMatchId());
        historyQuotationInfoVo.setAllQuotationTrend(allQuotationTrend);

        //本客户报价趋势
        List<SalSedHistoryQuotationCustomerVo> thisQuotationTrend=salSedQuotationSkuMapper.getOneAvgQuotationPriceList(params.getSkuId(),params.getMatchId(),params.getCustomerId());
        historyQuotationInfoVo.setThisQuotationTrend(thisQuotationTrend);

        //全部客户
        List<SalSedHistoryQuotationCustomerVo> allCustomer=salSedQuotationSkuMapper.getAllQuotationPriceList(params.getSkuId(),params.getMatchId());
        historyQuotationInfoVo.setAllCustomer(allCustomer);

        //本客户
        List<SalSedHistoryQuotationCustomerVo> thisCustomer=salSedQuotationSkuMapper.getOneQuotationPriceList(params.getSkuId(),params.getMatchId(),params.getCustomerId());
        historyQuotationInfoVo.setThisCustomer(thisCustomer);

       return historyQuotationInfoVo;
    }


    //获取历史报价信息方法
    public Map<String,BigDecimal> getHistoryQuotesCost(SalSedHistoryQuotationInfoParams params) {
        Map<String, BigDecimal> map = new HashMap<>();
        //本次报价（保持报价单原币种，不折算）
        if (params.getQuotationSkuId() != null) {
            map.put("thisPrice", salSedQuotationSkuMapper.selectById(params.getQuotationSkuId()).getQuotationPrice());
        }
        //平均报价、报价中位数：统一按人民币计算，美元报价乘汇率后参与
        List<SalSedQuotationSkuInfoVo> salSedQuotationSkuInfoVo = salSedQuotationSkuMapper.selectQuotationPriceListBySkuIdAndMatchId(params.getSkuId(), params.getMatchId());
        if (!salSedQuotationSkuInfoVo.isEmpty()) {
            List<BigDecimal> priceRmbList = new ArrayList<>();
            for (SalSedQuotationSkuInfoVo vo : salSedQuotationSkuInfoVo) {
                BigDecimal price = vo.getQuotationPrice();
                if (price == null) continue;
                BigDecimal rmb = "USD".equals(vo.getCurrency()) && vo.getExchangeRate() != null && vo.getExchangeRate().compareTo(BigDecimal.ZERO) > 0
                        ? price.multiply(vo.getExchangeRate())
                        : price;
                priceRmbList.add(rmb);
            }
            int totalCount = priceRmbList.size();
            if (totalCount > 0) {
                BigDecimal sum = priceRmbList.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal averagePrice = sum.divide(BigDecimal.valueOf(totalCount), 2, RoundingMode.HALF_UP);
                map.put("averagePrice", averagePrice);
                BigDecimal medianPrice = calculateMedian(priceRmbList);
                if (medianPrice != null) {
                    map.put("medianPrice", medianPrice.setScale(2, RoundingMode.HALF_UP));
                }
            }
        }
        return map;
    }

    //获取历史报价毛利率方法
    public Map<String,BigDecimal> getHistoryQuotesMargin(SalSedHistoryQuotationInfoParams params) {
        Map<String,BigDecimal> map = new HashMap<>();
        //单个sku的成本（工艺成本+配件成本）
        BigDecimal skuCost =   calculateSingleSkuCost(params.getSkuId(),params.getMatchId());
        //本次毛利率=【报价-成本（配件成本+工艺成本）】/报价*100%
          //本次的报价
        if(params.getQuotationSkuId()!=null) {
            BigDecimal thisQuotes = salSedQuotationSkuMapper.selectById(params.getQuotationSkuId()).getQuotationPrice();
            //计算本次毛利率【报价-成本（配件成本+工艺成本）】/报价*100%
            BigDecimal grossProfitRate = thisQuotes.subtract(skuCost).divide(thisQuotes, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
            map.put("grossProfitRate", grossProfitRate);
        }


        //平均毛利率
           //根据skuId和搭配Id查询报价单-sku表的报价（得到该类型的sku的信息） 因为是同一中sku,搭配id,所以他们的sku单个成本一样
        List<SalSedQuotationSkuInfoVo> salSedQuotationSkuInfoVo=salSedQuotationSkuMapper.selectQuotationPriceListBySkuIdAndMatchId(params.getSkuId(),params.getMatchId());
           //毛利率总和
        BigDecimal grossProfitRateSum=BigDecimal.ZERO;
           //创建集合将毛利率都存起来，方便获取毛利率中位数
        List<BigDecimal> grossProfitRateList=new ArrayList<>();
         for(SalSedQuotationSkuInfoVo quotationSkuInfoVo:salSedQuotationSkuInfoVo){
             BigDecimal quotes=quotationSkuInfoVo.getQuotationPrice();
             BigDecimal cost=skuCost;
             BigDecimal margin=quotes.subtract(cost).divide(quotes,4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
             quotationSkuInfoVo.setGrossProfitRate(margin);
             grossProfitRateSum=grossProfitRateSum.add(margin);
             grossProfitRateList.add(margin);
         }
            //平均毛利率
        if(!salSedQuotationSkuInfoVo.isEmpty()) {
            BigDecimal averageGrossProfitRate = grossProfitRateSum.divide(BigDecimal.valueOf(salSedQuotationSkuInfoVo.get(0).getTotalCount()), 4, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
            map.put("averageGrossProfitRate", averageGrossProfitRate);


            //毛利率中位数
            BigDecimal medianGrossProfitRate = calculateMedian(grossProfitRateList).setScale(2, RoundingMode.HALF_UP);
            map.put("medianGrossProfitRate", medianGrossProfitRate);
        }
        return map;
    }


    //计算单个sku的成本（工艺成本+配件成本）
    public BigDecimal calculateSingleSkuCost(Long skuId,Long matchId) {
        //查询SKU的信息
        ProSedProductMatchSku sku = proSedProductMatchSkuMapper.selectById(skuId);
        //查询SKU的对应搭配信息
        ProSedProductMatch proSedProductMatch = proSedProductMatchMapper.selectById(sku.getMatchId());
        //查询sku对应的产品信息
        ProSedProduct product = proSedProductMapper.selectById(proSedProductMatch.getProductId());
        SysDictionary painting = sysDictionaryManager.getByCodeAndKey(DictionaryConfigEnum.paintingPrice.getKey(), DictionaryConfigEnum.paintingPrice.getKey());
        //成本=配件成本+工艺成本
        //工艺成本
        if(sku.getEffectId()!=null){
            proSedProductManager.hangleProcessAndCost(sku,product.getArea(),painting.getValue());
        }

        //配件成本
        //根据搭配id得到配件相关成本单价和搭配数量 ，得到搭配成本总和
        List<SalSedQuotationFittingDetailVo> fittingDetail= proSedProductMatchFittingMapper.getFittingListByMatchId(matchId);
        //单个sku的配件成本
        BigDecimal fittingCost = BigDecimal.ZERO;
        for (SalSedQuotationFittingDetailVo quotationFittingDetail : fittingDetail) {
            if(quotationFittingDetail.getUnitCost()== null){
                quotationFittingDetail.setUnitCost(BigDecimal.ZERO);
            }
            fittingCost = fittingCost.add(quotationFittingDetail.getUnitCost().multiply(BigDecimal.valueOf(quotationFittingDetail.getFittingNum())));
        }

        //工艺成本为null时（如面积不在配置区间内）按0计算
        BigDecimal craftCost = sku.getCraftCost() == null ? BigDecimal.ZERO : sku.getCraftCost();
        BigDecimal skuCost = craftCost.add(fittingCost);
        return skuCost;
    }

    //计算一个集合的中位数
    public  BigDecimal calculateMedian(List<BigDecimal> grossProfitRateList) {
        // 1. 预处理：过滤null值，创建新列表（避免修改原集合）
        List<BigDecimal> validList = new ArrayList<>();
        if (grossProfitRateList == null || grossProfitRateList.isEmpty()) {
            return BigDecimal.ZERO;
        }
        for (BigDecimal num : grossProfitRateList) {
            if (num != null) {
                validList.add(num);
            }
        }
        // 过滤后仍为空，返回null
        if (validList.isEmpty()) {
            return null;
        }

        // 2. 排序：升序排列
        Collections.sort(validList);

        int size = validList.size();
        BigDecimal median;

        // 3. 计算中位数（分奇偶）
        if (size % 2 == 1) {
            // 奇数：取中间位置的数（(n+1)/2 对应索引为 (size-1)/2）
            int middleIndex = (size - 1) / 2;
            median = validList.get(middleIndex);
        } else {
            // 偶数：取中间两个数的平均值
            int middleIndex1 = size / 2 - 1;
            int middleIndex2 = size / 2;
            BigDecimal num1 = validList.get(middleIndex1);
            BigDecimal num2 = validList.get(middleIndex2);
            // 平均值：除以2，保留2位小数（可根据业务调整小数位数和舍入模式）
            median = num1.add(num2).divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP);
        }

        return median;
    }


    /**
     * 一键转订单
     * @param params
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String oneKeyToOrder(SalSedQuotationOneKeyToOrderParams params) {
        SalSedOrder salSedOrder = new SalSedOrder();
        //根据报价单id查询报价单相关信息
        SalSedQuotationDetailVo quotationDetailVo =salSedQuotationMapper.getQuotationDetailById(params.getQuotationId());
        //订单编号
        salSedOrder.setCode(EntityCodeGenerateUtil.generateUniqueId("O"));
        //订单来源
        salSedOrder.setOrderResource(params.getOrderSource());
       //客户Id
        salSedOrder.setCustomerId(quotationDetailVo.getCustomerId());
        //跟进人
           //根据客户id查询客户的跟进人
        salSedOrder.setUserId(salYtCustomerMapper.selectById(quotationDetailVo.getCustomerId()).getFollowEmployeeId());
        //订单金额
        salSedOrder.setOrderMoney(quotationDetailVo.getOrderAmount());
        //特殊要求
        salSedOrder.setSpecialRequirements(quotationDetailVo.getSpecialRequirements());
        //优惠金额
        salSedOrder.setDiscountAmount(quotationDetailVo.getDiscountAmount());
        //币种
        salSedOrder.setCurrency(quotationDetailVo.getCurrency());
        //是否含税
        salSedOrder.setTax(quotationDetailVo.getTax());
        //装运港
        salSedOrder.setFob(quotationDetailVo.getFob());
        //指定地点
        salSedOrder.setExw(quotationDetailVo.getExw());
        //汇率
        salSedOrder.setExchangeRate(quotationDetailVo.getExchangeRate());
        //业务员Id
        salSedOrder.setSalesmanId(quotationDetailVo.getSalesmanId());
        //报价单id
        salSedOrder.setQuotationId(params.getQuotationId());
        //交货日期
        salSedOrder.setDeliveryDate(params.getDeliveryDate());
        //收货地址Id
        salSedOrder.setReceiveAddressId(quotationDetailVo.getReceiveAddressId());
        //收货地址
        salSedOrder.setReceiveAddress(quotationDetailVo.getReceiveAddress());
        //管家婆同步状态
        salSedOrder.setSyncStatus(OrderGjpSyncStatusEnum.UNSYNCHRONIZED.getInfo());
        // 设置创建人
        salSedOrder.setCreateUser(params.getCreateUser());
        // 合同编号
        salSedOrder.setContractNumber(params.getContractNumber());
        //将报价单转订单新增进订单表
       salSedOrderMapper.insert(salSedOrder);
       //得到新增的订单id
        Long orderId = salSedOrder.getId();

        // 记录订单创建操作
        SalSedOrderOperateRecord operateRecord = new SalSedOrderOperateRecord();
        operateRecord.setOrderId(orderId);
        operateRecord.setOperateContent("创建订单");
        operateRecord.setIsDeleted(0);
        // 使用参数中的创建人作为操作人
        operateRecord.setCreateUser(params.getCreateUser());
        salSedOrderOperateRecordMapper.insert(operateRecord);

        //存入数据到订单详细表
        InsertToOrderDetail(params.getQuotationId(),orderId);

        return "一键转订单成功";
    }

    /**
     * 单个 SKU 转订单
     * @param params
     * @return
     */
    public String skuToOrder(SalSedQuotationSkuToOrderParams params) {
        SalSedOrder salSedOrder = new SalSedOrder();
        //根据报价单 id 查询报价单相关信息
        SalSedQuotationDetailVo quotationDetailVo =salSedQuotationMapper.getQuotationDetailById(params.getQuotationId());

        //根据报价单 SKU id 查询 SKU 信息
        SalSedQuotationSku quotationSku = salSedQuotationSkuMapper.selectById(params.getQuotationSkuId());
        if(quotationSku == null) {
            throw new BizException(400,"报价单 SKU 不存在");
        }

        //订单编号
        salSedOrder.setCode(EntityCodeGenerateUtil.generateUniqueId("O"));
        //订单来源
        salSedOrder.setOrderResource(params.getOrderSource());
        //客户 Id
        salSedOrder.setCustomerId(quotationDetailVo.getCustomerId());
        //跟进人
        salSedOrder.setUserId(salYtCustomerMapper.selectById(quotationDetailVo.getCustomerId()).getFollowEmployeeId());
        //订单金额 = SKU 报价 * 数量
        BigDecimal orderMoney = quotationSku.getQuotationPrice()
                .multiply(BigDecimal.valueOf(quotationSku.getQuantity()));
        salSedOrder.setOrderMoney(orderMoney);
        //特殊要求
        salSedOrder.setSpecialRequirements(quotationDetailVo.getSpecialRequirements());
        //优惠金额
        salSedOrder.setDiscountAmount(BigDecimal.ZERO);
        //币种
        salSedOrder.setCurrency(quotationDetailVo.getCurrency());
        //是否含税
        salSedOrder.setTax(quotationDetailVo.getTax());
        //装运港
        salSedOrder.setFob(quotationDetailVo.getFob());
        //指定地点
        salSedOrder.setExw(quotationDetailVo.getExw());
        //汇率
        salSedOrder.setExchangeRate(quotationDetailVo.getExchangeRate());
        //业务员 Id
        salSedOrder.setSalesmanId(quotationDetailVo.getSalesmanId());
        //报价单 id
        salSedOrder.setQuotationId(params.getQuotationId());
        //交货日期
        salSedOrder.setDeliveryDate(params.getDeliveryDate());
        //收货地址 Id
        salSedOrder.setReceiveAddressId(quotationDetailVo.getReceiveAddressId());
        //收货地址
        salSedOrder.setReceiveAddress(quotationDetailVo.getReceiveAddress());
        //管家婆同步状态
        salSedOrder.setSyncStatus(OrderGjpSyncStatusEnum.UNSYNCHRONIZED.getInfo());
        // 设置创建人
        salSedOrder.setCreateUser(params.getCreateUser());
        // 合同编号
        salSedOrder.setContractNumber(params.getContractNumber());
        //将报价单转订单新增进订单表
        salSedOrderMapper.insert(salSedOrder);
        //得到新增的订单 id
        Long orderId = salSedOrder.getId();

        // 记录订单创建操作
        SalSedOrderOperateRecord operateRecord = new SalSedOrderOperateRecord();
        operateRecord.setOrderId(orderId);
        operateRecord.setOperateContent("创建订单");
        operateRecord.setIsDeleted(0);
        operateRecord.setCreateUser(params.getCreateUser());
        salSedOrderOperateRecordMapper.insert(operateRecord);

        //存入数据到订单详细表（只转当前 SKU）
        InsertSingleSkuToOrderDetail(params.getQuotationSkuId(), orderId);

        //检查是否所有 SKU 都已转换完成
        checkAllSkuConverted(params.getQuotationId());

        return "单个 SKU 转订单成功";
    }


    //一键转订单将信息存入订单详细表
    private void InsertToOrderDetail(Long quotationId,Long orderId){
      //根据报价单id查询报价单-sku和报价单-sku-包材表的信息
        List<SalSedOrderDetail> salSedOrderDetail = salSedQuotationSkuMapper.getQuotationSkuAndPackingInfoByQuotationId(quotationId);
        for(SalSedOrderDetail orderDetail : salSedOrderDetail){
            orderDetail.setOrderId(orderId);
            salSedOrderDetailMapper.insert(orderDetail);
        }
    }

    //单个 SKU 转订单将信息存入订单详细表
    private void InsertSingleSkuToOrderDetail(Long quotationSkuId, Long orderId){
        //根据报价单 SKU id 查询报价单-sku 和报价单-sku-包材表的信息
        List<SalSedOrderDetail> salSedOrderDetail = salSedQuotationSkuMapper.getQuotationSkuAndPackingInfoBySkuId(quotationSkuId);
        for(SalSedOrderDetail orderDetail : salSedOrderDetail){
            orderDetail.setOrderId(orderId);
            salSedOrderDetailMapper.insert(orderDetail);
        }
    }

    /**
     * 检查报价单是否所有 SKU 都已转换完成
     * @param quotationId 报价单 ID
     */
    private void checkAllSkuConverted(Long quotationId) {
        //查询该报价单下所有的 SKU 数量
        Long totalSkuCount = salSedQuotationSkuMapper.selectCount(
            Wrappers.lambdaQuery(SalSedQuotationSku.class)
                .eq(SalSedQuotationSku::getQuotationId, quotationId)
                .eq(SalSedQuotationSku::getIsDeleted, 0)
        );

        //查询该报价单下已转订单的 SKU 数量（通过订单明细表统计不重复的 quotation_sku_id）
        Long convertedSkuCount = salSedOrderDetailMapper.countDistinctQuotationSkuIdByQuotationId(quotationId);

        //如果所有 SKU 都已转换，设置 shiftStatus = "1"
        if(totalSkuCount > 0 && totalSkuCount.equals(convertedSkuCount)) {
            SalSedQuotation quotation = new SalSedQuotation();
            quotation.setShiftStatus("1");
            LambdaUpdateWrapper<SalSedQuotation> updateWrapper = Wrappers.lambdaUpdate(SalSedQuotation.class)
                    .eq(SalSedQuotation::getId, quotationId);
            salSedQuotationMapper.update(quotation, updateWrapper);
        }
    }

    /**
     * 合并转订单-请选择产品：仅返回审核通过的报价单，支持报价单编号、SKU名称、搭配名称筛选
     * @param params 筛选参数
     * @return 报价单列表（含其下 SKU 列表，用于勾选）
     */
    public List<SalSedQuotationMergeItemVo> listForMerge(SalSedQuotationMergeListParams params) {
        List<SalSedQuotationMergeItemVo> list = salSedQuotationMapper.listForMerge(params);
        return list;
    }

    /**
     * 合并转订单-获取报价单下的SKU列表（用于展开行时加载）
     * @param quotationId 报价单ID
     * @param skuName SKU名称（筛选）
     * @param matchName 搭配名称（筛选）
     * @return SKU列表
     */
    public List<SalSedQuotationMergeSkuItemVo> getMergeSkuList(Long quotationId, String skuName, String matchName) {
        List<SalSedQuotationSkuVo> skuVoList = salSedQuotationSkuMapper.getQuotationSkuListForMerge(
                quotationId, skuName, matchName);
        List<SalSedQuotationMergeSkuItemVo> mergeSkuList = new ArrayList<>();
        for (SalSedQuotationSkuVo vo : skuVoList) {
            SalSedQuotationMergeSkuItemVo mergeSku = new SalSedQuotationMergeSkuItemVo();
            mergeSku.setQuotationSkuId(vo.getQuotationSkuId());
            mergeSku.setModelName(vo.getModelName());
            mergeSku.setCombinationName(vo.getCombinationName());
            mergeSku.setSkuName(vo.getSkuName());
            mergeSku.setQuotationPrice(vo.getPrice());
            mergeSku.setQuantity(vo.getNumber());
            mergeSku.setPic(vo.getPic());
            mergeSkuList.add(mergeSku);
        }
        return mergeSkuList;
    }

    /**
     * 合并转订单：将选中的多个报价单 SKU 合并生成一个订单
     * 限制：1）所选 SKU 所属报价单的客户、业务员必须一致；2）所选 SKU 不能存在完全相同（同一 productId+matchId+skuId）的重复项
     * @param params 选中的 quotationSkuIds、订单来源、交货日期等
     * @return 成功提示
     */
    @Transactional(rollbackFor = Exception.class)
    public String mergeToOrder(SalSedQuotationMergeToOrderParams params) {
        List<Long> quotationSkuIds = params.getQuotationSkuIds();
        if (CollectionUtils.isEmpty(quotationSkuIds)) {
            throw new BizException(400, "请至少选择一个 SKU");
        }
        List<SalSedQuotationSku> skuList = salSedQuotationSkuMapper.selectBatchIds(quotationSkuIds);
        if (CollectionUtils.isEmpty(skuList) || skuList.size() != quotationSkuIds.size()) {
            throw new BizException(400, "所选报价单 SKU 不存在或已删除");
        }
        Set<Long> quotationIds = skuList.stream().map(SalSedQuotationSku::getQuotationId).collect(Collectors.toSet());
        Long firstQuotationId = skuList.get(0).getQuotationId();
        SalSedQuotationDetailVo firstDetail = salSedQuotationMapper.getQuotationDetailById(firstQuotationId);
        if (firstDetail == null) {
            throw new BizException(400, "报价单不存在");
        }
        for (Long qid : quotationIds) {
            SalSedQuotationDetailVo detail = salSedQuotationMapper.getQuotationDetailById(qid);
            if (detail == null) {
                throw new BizException(400, "报价单不存在");
            }
            if (!java.util.Objects.equals(detail.getCustomerId(), firstDetail.getCustomerId())
                    || !java.util.Objects.equals(detail.getSalesmanId(), firstDetail.getSalesmanId())) {
                throw new BizException(400, "合并订单限制条件：只有业务员与客户都相同的报价单才能合并转订单");
            }
        }
        Set<String> skuKeySet = new HashSet<>();
        for (SalSedQuotationSku sku : skuList) {
            String key = sku.getProductId() + "_" + sku.getMatchId() + "_" + sku.getSkuId();
            if (!skuKeySet.add(key)) {
                throw new BizException(400, "SKU 完全相同不能合并，请勿选择重复的 SKU（型号+搭配+SKU 一致视为相同）");
            }
        }
        // 检查是否存在一个订单包含所有选中的 SKU
        // 思路：找出每个 SKU 存在的订单，然后找交集
        Set<Long> commonOrderIds = null;
        for (SalSedQuotationSku sku : skuList) {
            List<Long> orderIds = salSedOrderDetailMapper.findOrderIdsBySkuTriplet(
                    sku.getProductId(), sku.getMatchId(), sku.getSkuId());
            if (CollectionUtils.isEmpty(orderIds)) {
                commonOrderIds = null;
                break;
            }
            if (commonOrderIds == null) {
                commonOrderIds = new HashSet<>(orderIds);
            } else {
                commonOrderIds.retainAll(orderIds);
            }
            if (CollectionUtils.isEmpty(commonOrderIds)) {
                break;
            }
        }
        if (commonOrderIds != null && !commonOrderIds.isEmpty()) {
            // 找到包含所有 SKU 的订单，获取订单编号
            Long orderId = commonOrderIds.iterator().next();
            SalSedOrder order = salSedOrderMapper.selectById(orderId);
            String orderCode = order != null ? order.getCode() : "";
            throw new BizException(400, "该订单已存在相同的 SKU（订单编号：" + orderCode + "），请检查后再合并");
        }
        BigDecimal orderMoney = BigDecimal.ZERO;
        for (SalSedQuotationSku sku : skuList) {
            orderMoney = orderMoney.add(
                    (sku.getQuotationPrice() != null ? sku.getQuotationPrice() : BigDecimal.ZERO)
                            .multiply(BigDecimal.valueOf(sku.getQuantity() != null ? sku.getQuantity() : 0)));
        }
        SalSedOrder order = new SalSedOrder();
        order.setCode(EntityCodeGenerateUtil.generateUniqueId("O"));
        order.setOrderResource(params.getOrderSource());
        order.setCustomerId(firstDetail.getCustomerId());
        order.setUserId(salYtCustomerMapper.selectById(firstDetail.getCustomerId()).getFollowEmployeeId());
        order.setOrderMoney(orderMoney);
        order.setSpecialRequirements(firstDetail.getSpecialRequirements());
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setCurrency(firstDetail.getCurrency());
        order.setTax(firstDetail.getTax());
        order.setFob(firstDetail.getFob());
        order.setExw(firstDetail.getExw());
        order.setExchangeRate(firstDetail.getExchangeRate());
        order.setSalesmanId(firstDetail.getSalesmanId());
        order.setQuotationId(null);
        order.setDeliveryDate(params.getDeliveryDate());
        order.setReceiveAddressId(firstDetail.getReceiveAddressId());
        order.setReceiveAddress(firstDetail.getReceiveAddress());
        order.setSyncStatus(OrderGjpSyncStatusEnum.UNSYNCHRONIZED.getInfo());
        order.setCreateUser(params.getCreateUser());
        order.setContractNumber(params.getContractNumber());
        salSedOrderMapper.insert(order);
        Long orderId = order.getId();
        SalSedOrderOperateRecord operateRecord = new SalSedOrderOperateRecord();
        operateRecord.setOrderId(orderId);
        operateRecord.setOperateContent("创建订单（合并转订单）");
        operateRecord.setIsDeleted(0);
        operateRecord.setCreateUser(params.getCreateUser());
        salSedOrderOperateRecordMapper.insert(operateRecord);
        for (Long quotationSkuId : quotationSkuIds) {
            InsertSingleSkuToOrderDetail(quotationSkuId, orderId);
        }
        return "合并转订单成功";
    }

    /**
     * 报价单导出
     * @param response
     * @param params
     */
    public void exportQuotation(HttpServletResponse response, SalSedQuotationExportParams params) throws UnsupportedEncodingException {
        // 1. 设置响应头
      //  response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
     //   response.setCharacterEncoding("UTF-8");
        // 文件名编码（防止中文乱码）
     //   String fileName = "报价单导出"+ System.currentTimeMillis();
       // String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
       // response.setHeader("Content-disposition", "attachment;filename=''" + encodedFileName + ".xlsx");

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("报价单导出", "UTF-8") + ".xlsx";
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

        //查询客户信息
        SalYtCustomer customer = salYtCustomerMapper.selectCustomerByQuotationId(params.getQuotationId());

        //客户名称
        String customerName = customer.getName();
        //客户邮箱  ********************************************************************
        String customerEmail = "";
        //报价日期
        LocalDateTime quoteDate = customer.getCreateTime();

        //查询报价单获取币种
        SalSedQuotation quotation = salSedQuotationMapper.selectById(params.getQuotationId());
        String currency = quotation != null ? quotation.getCurrency() : "USD";

        //客户报价单产品信息
        List<SalSedQuotationExportVo> productLists =getQuotationProductInfo(params.getQuotationId(), currency);

        try {
            byte[] excelBytes = generateQuoteExcel(
                    customerName,
                    customerEmail,
                    quoteDate,
                    productLists,
                    currency
            );
            response.getOutputStream().write(excelBytes);
            response.getOutputStream().flush();
        } catch (IOException e) {
            log.error("导出失败" , e);
        }
    }


    public List<SalSedQuotationExportVo> getQuotationProductInfo(Long quotationId, String currency){
        List<SalSedQuotationExportVo> productList = new ArrayList<>();
        List<SalSedQuotationExportVo> productLists = salSedQuotationMapper.getQuotationProductInfo(quotationId);

        // 批量查询优化：提取所有需要的ID，减少数据库查询次数
        if (!productLists.isEmpty()) {
            // 提取所需ID集合
            Set<Long> skuIds = new HashSet<>();
            Set<Long> productIds = new HashSet<>();
            Set<Long> matchIds = new HashSet<>();
            List<Long> quotationSkuIds = new ArrayList<>();

            for (SalSedQuotationExportVo product : productLists) {
                skuIds.add(product.getSkuId());
                productIds.add(product.getProductId());
                matchIds.add(product.getMatchId());
                quotationSkuIds.add(product.getQuotationSkuId());
            }

            // 1. 批量查询图片信息，按skuId分组
            Map<Long, String> skuImageMap = new HashMap<>();
            for (Long skuId : skuIds) {
                List<ProSedFile> imagesUrl = proSedFileManager.selectSkuImage(skuId);
                if (!imagesUrl.isEmpty()) {
                    skuImageMap.put(skuId, imagesUrl.get(0).getUrl());
                }
            }

            // 2. 批量查询产品维度信息，按productId分组
            Map<Long, Map<String, Double>> productSizeMap = new HashMap<>();
            for (Long productId : productIds) {
                List<ProSedSize> sizeList = proSedSizeMapper.getSizeInfoByProductIdAndType(productId, "1");
                Map<String, Double> sizeMap = new HashMap<>();
                for (ProSedSize size : sizeList) {
                    String latitude = size.getLatitude();
                    if (latitude == null || size.getValue() == null || size.getValue().isBlank()) {
                        continue;
                    }
                    double numVal;
                    try {
                        numVal = Double.parseDouble(size.getValue().trim());
                    } catch (NumberFormatException ignored) {
                        continue;
                    }
                    String lat = latitude.trim();
                    String latLower = lat.toLowerCase();
                    if (lat.contains("长度") || "长".equals(lat) || latLower.contains("length")) {
                        sizeMap.put("length", numVal);
                    } else if (lat.contains("宽度") || "宽".equals(lat) || latLower.contains("width")) {
                        sizeMap.put("width", numVal);
                    } else if (lat.contains("上口径") || (lat.contains("口径") && !lat.contains("下") && !lat.contains("底"))) {
                        sizeMap.put("diameterTop", numVal);
                    } else if (lat.contains("下口径") || lat.contains("底口径")) {
                        sizeMap.put("diameterBottom", numVal);
                    } else if (lat.contains("高")) {
                        sizeMap.put("height", numVal);
                    }
                }
                productSizeMap.put(productId, sizeMap);
            }

            // 3. 批量查询配件重量，按matchId分组
            Map<Long, BigDecimal> fittingWeightMap = new HashMap<>();
            for (Long matchId : matchIds) {
                BigDecimal weight = proSedProductMatchFittingMapper.sumFittingWeight(matchId);
                fittingWeightMap.put(matchId, weight);
            }

            // 4. 批量查询报价单-sku-包材表的信息
            LambdaQueryWrapper<SalSedQuotationSkuPacking> queryWrapper = Wrappers.lambdaQuery(SalSedQuotationSkuPacking.class)
                    .in(SalSedQuotationSkuPacking::getQuotationSkuId, quotationSkuIds)
                    .eq(SalSedQuotationSkuPacking::getIsDeleted, 0);
            List<SalSedQuotationSkuPacking> allQuotationSkuPackings = salSedQuotationSkuPackingMapper.selectList(queryWrapper);

            // 5. 提取包材ID并批量查询包材信息
            Set<Long> packingIds = new HashSet<>();
            for (SalSedQuotationSkuPacking packing : allQuotationSkuPackings) {
                packingIds.add(packing.getPackingId());
            }
            Map<Long, ProSedPacking> packingMap = new HashMap<>();
            if (!packingIds.isEmpty()) {
                List<ProSedPacking> allPackings = proSedPackingMapper.selectBatchIds(new ArrayList<>(packingIds));
                for (ProSedPacking packing : allPackings) {
                    packingMap.put(packing.getId(), packing);
                }
            }

            // 6. 按quotationSkuId分组包材信息
            Map<Long, List<SalSedQuotationSkuPacking>> quotationSkuPackingMap = new HashMap<>();
            for (SalSedQuotationSkuPacking packing : allQuotationSkuPackings) {
                quotationSkuPackingMap.computeIfAbsent(packing.getQuotationSkuId(), k -> new ArrayList<>())
                        .add(packing);
            }

            // 遍历处理每个产品
            for(SalSedQuotationExportVo product : productLists){
            SalSedQuotationExportVo productVo = new SalSedQuotationExportVo();
            productVo.setItem(product.getItem());
            productVo.setMoq(product.getMoq());
            productVo.setRemarks(product.getRemarks());
            // 单盆克重：产品的重量（从SQL查询直接获取）
            productVo.setWeightPerUnit(product.getWeightPerUnit());
            // 报价金额原值
            productVo.setFobNingboPrice(product.getFobNingboPrice());
            // CBM 与报价单明细「体积(m³)」一致，取自 sal_sed_quotation_sku.volume
            if (product.getVolume() != null) {
                productVo.setCbm(product.getVolume().doubleValue());
            }

            // 直接使用已批量查询好的图片信息
            String productPhoto = skuImageMap.get(product.getSkuId());
            productVo.setProductPhoto(productPhoto);

            // 直接使用已批量查询好的产品维度信息
            Map<String, Double> sizeInfo = productSizeMap.get(product.getProductId());
            if (sizeInfo != null) {
                productVo.setDiameterTop(sizeInfo.get("diameterTop"));
                productVo.setDiameterBottom(sizeInfo.get("diameterBottom"));
                productVo.setHeight(sizeInfo.get("height"));
                productVo.setLength(sizeInfo.get("length"));
                productVo.setWidth(sizeInfo.get("width"));
            }

            // 直接使用已批量查询好的包材信息
            List<SalSedQuotationSkuPacking> currentPackings = quotationSkuPackingMap.get(product.getQuotationSkuId());
            if (currentPackings != null) {
                for (SalSedQuotationSkuPacking quotationSkuPacking : currentPackings) {
                    ProSedPacking packing = packingMap.get(quotationSkuPacking.getPackingId());
                    // 只要有 packing_size 就设置，不管 packing 是否存在
                    if (quotationSkuPacking.getPackingSize() != null) {
                        // CARTON SIZE 使用 packing_size (如 20*30*40)
                        productVo.setCartonSize(quotationSkuPacking.getPackingSize());
                    }
                    // 装箱数
                    if (quotationSkuPacking.getPackingNum() != null) {
                        productVo.setQtyPerCarton(quotationSkuPacking.getPackingNum());
                    }
                }
            }

            // 单盆克重：产品的重量（从SQL查询直接获取）
            productVo.setWeightPerUnit(product.getWeightPerUnit());


            if(productVo.getCbm()!=null&&productVo.getQtyPerCarton()!=null){
                //26/纸箱体积（立方米）*装箱数
              int cbm = (int) (26.0/productVo.getCbm());
              productVo.setQtyPer20ft(cbm*productVo.getQtyPerCarton());


                //66/纸箱体积（立方米）*装箱数
              int cbm1 = (int) (66.0/productVo.getCbm());
              productVo.setQtyPer40ft(cbm1*productVo.getQtyPerCarton());

            }

            productList.add(productVo);
        }





        }
        return productList;
    }

    /**
     * 报价单导出列布局：全表为「长×宽」型产品时少一列（无 HEIGHT 列），后续列左移。
     */
    private static final class QuotationExportColumnLayout {
        /** 整张表是否使用 length(cm)+width(cm) 两列（否则为上/下口径+高 三列） */
        final boolean lengthWidthSheet;
        /** 最后一列索引（含），0-based */
        final int lastCol;
        final int qtyPerCarton;
        final int cartonSize;
        final int cbm;
        final int moq;
        final int weight;
        final int bossQuote;
        final int remarks;
        final int unitPrice;
        final int qty20ft;
        final int qty40hq;

        private QuotationExportColumnLayout(boolean lengthWidthSheet, int lastCol,
                                            int qtyPerCarton, int cartonSize, int cbm, int moq, int weight,
                                            int bossQuote, int remarks, int unitPrice, int qty20ft, int qty40hq) {
            this.lengthWidthSheet = lengthWidthSheet;
            this.lastCol = lastCol;
            this.qtyPerCarton = qtyPerCarton;
            this.cartonSize = cartonSize;
            this.cbm = cbm;
            this.moq = moq;
            this.weight = weight;
            this.bossQuote = bossQuote;
            this.remarks = remarks;
            this.unitPrice = unitPrice;
            this.qty20ft = qty20ft;
            this.qty40hq = qty40hq;
        }

        static QuotationExportColumnLayout resolve(List<SalSedQuotationExportVo> list) {
            boolean lwSheet = list != null && !list.isEmpty()
                    && list.stream().allMatch(SalSedQuotationManager::isLengthWidthOnlyRow);
            if (lwSheet) {
                return new QuotationExportColumnLayout(true, 13,
                        4, 5, 6, 7, 8, 9, 10, 11, 12, 13);
            }
            return new QuotationExportColumnLayout(false, 14,
                    5, 6, 7, 8, 9, 10, 11, 12, 13, 14);
        }
    }

    /** 行数据为「仅有长/宽、无上/下口径与高」——用于整表两列模式判定；混合报价单中单行仍可用三列表头下的前两格展示长宽 */
    private static boolean isLengthWidthOnlyRow(SalSedQuotationExportVo vo) {
        if (vo == null) {
            return false;
        }
        boolean hasLw = vo.getLength() != null || vo.getWidth() != null;
        boolean hasDia = vo.getDiameterTop() != null || vo.getDiameterBottom() != null || vo.getHeight() != null;
        if (hasDia) {
            return false;
        }
        return hasLw;
    }

    public static byte[] generateQuoteExcel(String customerName, String customerEmail,
                                            LocalDateTime quoteDate, List<SalSedQuotationExportVo> quoteDataList, String currency) throws IOException {
        // 创建工作簿，使用SXSSFWorkbook提高大数据量下的性能
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");

            QuotationExportColumnLayout layout = QuotationExportColumnLayout.resolve(quoteDataList);

            // 设置列宽
            setColumnWidths(sheet, layout);

            // 创建单元格样式
            CellStyle companyNameStyle = createCompanyNameStyle(workbook);
            CellStyle companyInfoStyle = createCompanyInfoStyle(workbook);
            CellStyle customerInfoStyle = createCustomerInfoStyle(workbook);
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataCellStyle = createDataCellStyle(workbook);

            // 填充公司信息（第1-3行，不含联系人行）
            fillCompanyInfo(sheet, companyNameStyle, companyInfoStyle, layout.lastCol);

            // 填充客户信息（第4-5行）
            fillCustomerInfo(sheet, customerEmail, quoteDate, customerInfoStyle, layout.lastCol);

            // 填充表头（第6行）
            fillHeader(sheet, headerStyle, layout);

            // 填充数据内容（第7行开始）
            fillDataContent(workbook, sheet, quoteDataList, dataCellStyle, currency, layout);

            // 设置冻结窗格（冻结前2列，从第7行开始）
            sheet.createFreezePane(2, 6);

            // 写入输出流
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                workbook.write(outputStream);
                return outputStream.toByteArray();
            }
        }
    }

    /**
     * 设置列宽
     */
    private static void setColumnWidths(Sheet sheet, QuotationExportColumnLayout layout) {
        int[] columnWidths = layout.lengthWidthSheet
                ? new int[]{8, 25, 15, 12, 18, 10, 10, 15, 15, 15, 25, 12, 12, 8}
                : new int[]{8, 25, 15, 12, 12, 18, 10, 10, 15, 15, 15, 25, 12, 12, 8};
        for (int i = 0; i < columnWidths.length; i++) {
            sheet.setColumnWidth(i, columnWidths[i] * 256); // 256为单位
        }
    }

    /**
     * 创建公司名称样式
     */
    private static CellStyle createCompanyNameStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 14);
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        // 设置背景色
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    /**
     * 创建公司信息样式
     */
    private static CellStyle createCompanyInfoStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        // 新增：设置浅灰色背景
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    /**
     * 创建联系人样式
     */
    private static CellStyle createContactPersonStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 11);
        font.setBold(true);
        font.setColor(IndexedColors.BLUE.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        // 2. 背景色：浅蓝色（核心修正，替换原浅灰色）
        style.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex()); // 使用新增的浅蓝常量
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND); // 实心填充（必须配置，否则背景色不生效）
        return style;
    }

    /**
     * 创建客户信息样式
     */
    private static CellStyle createCustomerInfoStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        //设置背景颜色为粉色
        style.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        //设置字体颜色为蓝色
        font.setColor(IndexedColors.BLUE.getIndex());



        return style;
    }

    /**
     * 创建表头样式
     */
    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);
        style.setFont(font);
        //设置宽度

        // 核心修复1：启用单元格自动换行（解决文本不换行问题）
        style.setWrapText(true);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        // 设置边框
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    /**
     * 创建数据单元格样式
     */
    private static CellStyle createDataCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        // 设置边框
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    /**
     * 填充公司信息（不含联系人蓝色行）
     */
    private static void fillCompanyInfo(Sheet sheet, CellStyle companyNameStyle,
                                        CellStyle companyInfoStyle, int mergeEndColInclusive) {
        // 第1行：公司名称（合并至表体最后一列）
        Row row1 = sheet.createRow(0);
        Cell cell1 = row1.createCell(0);
        cell1.setCellValue("TAIZHOU SHENGERDA PLASTIC CO., LTD.");
        cell1.setCellStyle(companyNameStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, mergeEndColInclusive));

        // 第2行：公司地址
        Row row2 = sheet.createRow(1);
        Cell cell2 = row2.createCell(0);
        cell2.setCellValue("ADDRESS: Jin 'ao industrial area, daxi town, wenling city, zhejiang province, China");
        cell2.setCellStyle(companyInfoStyle);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, mergeEndColInclusive));

        // 第3行：联系方式
        Row row3 = sheet.createRow(2);
        Cell cell3 = row3.createCell(0);
        cell3.setCellValue("Tel:86-576-86331003  Fax:86-576-86335290  Website: http://www.sedsl.com/www.leizisure.com");
        cell3.setCellStyle(companyInfoStyle);
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, mergeEndColInclusive));
    }

    /**
     * 填充客户信息（To Customer 后不显示客户名）
     */
    private static void fillCustomerInfo(Sheet sheet, String customerEmail,
                                         LocalDateTime quoteDate, CellStyle style, int lastColInclusive) {
        int colCount = lastColInclusive + 1;
        // 第4行：To Customer 与报价日期
        Row row4 = sheet.createRow(3);

        for (int col = 0; col < colCount; col++) {
            Cell cell = row4.createCell(col);
            cell.setCellStyle(style);
        }

        Cell cell4_0 = row4.createCell(0);
        cell4_0.setCellValue("To Customer : ");
        cell4_0.setCellStyle(style);

        Cell cell4_2 = row4.createCell(2);
        String dateStr = quoteDate != null ? quoteDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "";
        cell4_2.setCellValue("Quotation date: " + dateStr);
        cell4_2.setCellStyle(style);
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 2, 4));

        // 第5行：客户邮箱
        Row row5 = sheet.createRow(4);

        for (int col = 0; col < colCount; col++) {
            Cell cell = row5.createCell(col);
            cell.setCellStyle(style);
        }

        Cell cell5_0 = row5.createCell(0);
        cell5_0.setCellValue("Email: " + (customerEmail != null ? customerEmail : ""));
        cell5_0.setCellStyle(style);
    }

    /**
     * 填充表头
     */
    private static void fillHeader(Sheet sheet, CellStyle style, QuotationExportColumnLayout layout) {
        Row row6 = sheet.createRow(5);
        String[] headers = layout.lengthWidthSheet
                ? new String[]{
                "ITEM", "PRODUCT   PHOTO      ", "length\n(cm)", "width\n(cm)",
                "QTY/CTN\n(pcs)", "CARTON SIZE\n(cbm)", "CBM", "MOQ ", "单盆克重",
                "王总报价", "备注", "UNIT PRICE\nFOB NINGBO ",
                "QTY/20FT", "QTY/40HQ"
        }
                : new String[]{
                "ITEM", "PRODUCT   PHOTO      ", "Top DIAMETER\n(cm)", "Bottom diameter\n(cm)", "HEIGHT\n(cm)",
                "QTY/CTN\n(pcs)", "CARTON SIZE\n(cbm)", "CBM", "MOQ ", "单盆克重",
                "王总报价", "备注", "UNIT PRICE\nFOB NINGBO ",
                "QTY/20FT", "QTY/40HQ"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = row6.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }
    }

    /**
     * 填充数据内容
     */
    private static void fillDataContent(Workbook workbook, Sheet sheet, List<SalSedQuotationExportVo> quoteDataList, CellStyle style, String currency,
                                        QuotationExportColumnLayout layout) {
        if (quoteDataList == null || quoteDataList.isEmpty()) {
            return;
        }

        // 判断币种符号：人民币显示¥，美元显示$
        String currencySymbol;
        if ("1".equals(currency) || "人民币".equals(currency)) {
            currencySymbol = "¥";
        } else {
            currencySymbol = "$";
        }

        for (int i = 0; i < quoteDataList.size(); i++) {
            int rowNum = 6 + i; // 从第7行开始（索引6）
            Row row = sheet.createRow(rowNum);

            SalSedQuotationExportVo data = quoteDataList.get(i);

            // 根据图片动态设置行高
            float rowHeight = 80f; // 默认高度
            if (data.getProductPhoto() != null && !data.getProductPhoto().isEmpty()) {
                rowHeight = calculateRowHeightFromImage(data.getProductPhoto());
            }
            row.setHeightInPoints(rowHeight);

            // 创建绘图对象（用于插入图片）
            Drawing<?> drawing = sheet.createDrawingPatriarch();

            // 填充基础数据
            setCellValue(row, 0, data.getItem(), style);          // ITEM

            // 插入产品图片
            if (data.getProductPhoto() != null && !data.getProductPhoto().isEmpty()) {
                QMYExcelUtil.insertImageToCell(workbook, drawing, data.getProductPhoto(),
                        rowNum, 1, 0, 0, 1.0); // 行号, 列号(第2列), 水平偏移0, 垂直偏移0, 宽度比例1.0
            } else {
                setCellValue(row, 1, "", style); // 无图片时留空
            }

            if (layout.lengthWidthSheet) {
                setCellValue(row, 2, data.getLength(), style);
                setCellValue(row, 3, data.getWidth(), style);
            } else if (isLengthWidthOnlyRow(data)) {
                // 混合报价单：表头仍为三列口径，长宽型行填前两列，第三列留空
                setCellValue(row, 2, data.getLength(), style);
                setCellValue(row, 3, data.getWidth(), style);
                setCellValue(row, 4, "", style);
            } else {
                setCellValue(row, 2, data.getDiameterTop(), style);
                setCellValue(row, 3, data.getDiameterBottom(), style);
                setCellValue(row, 4, data.getHeight(), style);
            }

            setCellValue(row, layout.qtyPerCarton, data.getQtyPerCarton(), style);
            setCellValue(row, layout.cartonSize, data.getCartonSize(), style);
            setCellValue(row, layout.cbm, data.getCbm(), style);
            setCellValue(row, layout.moq, data.getMoq(), style);
            setCellValue(row, layout.weight, data.getWeightPerUnit(), style);
            setCellValue(row, layout.bossQuote, "", style);
            setCellValue(row, layout.remarks, data.getRemarks(), style);
            if (data.getFobNingboPrice() != null) {
                setCellValue(row, layout.unitPrice, currencySymbol + data.getFobNingboPrice(), style);
            } else {
                setCellValue(row, layout.unitPrice, "", style);
            }
            setCellValue(row, layout.qty20ft, "", style);
            setCellValue(row, layout.qty40hq, "", style);
        }

        // 自动调整列宽（根据内容）
        autoSizeColumns(sheet, quoteDataList, layout);
    }

    /**
     * 自动调整列宽
     * @param sheet 工作表
     * @param quoteDataList 数据列表
     */
    private static void autoSizeColumns(Sheet sheet, List<SalSedQuotationExportVo> quoteDataList, QuotationExportColumnLayout layout) {
        int[] columnWidths = layout.lengthWidthSheet
                ? new int[]{0, 25, 15, 12, 18, 10, 10, 25, 25, 15, 25, 12, 12, 8}
                : new int[]{0, 25, 15, 12, 12, 18, 10, 10, 25, 25, 15, 25, 12, 12, 8};

        for (int col = 0; col <= layout.lastCol; col++) {
            int columnWidth;

            if (columnWidths[col] > 0) {
                columnWidth = columnWidths[col] * 256;
            } else {
                int maxWidth = 0;

                Row headerRow = sheet.getRow(5);
                if (headerRow != null) {
                    Cell headerCell = headerRow.getCell(col);
                    if (headerCell != null) {
                        String headerValue = headerCell.getStringCellValue();
                        maxWidth = Math.max(maxWidth, getStringWidth(headerValue));
                    }
                }

                for (SalSedQuotationExportVo data : quoteDataList) {
                    String cellValue = getCellValueForColumn(data, col, layout);
                    if (cellValue != null && !cellValue.isEmpty()) {
                        maxWidth = Math.max(maxWidth, getStringWidth(cellValue));
                    }
                }

                columnWidth = Math.min(maxWidth, 200) * 256;
                columnWidth = Math.max(columnWidth, 8 * 256);
            }

            sheet.setColumnWidth(col, columnWidth);
        }
    }

    /**
     * 根据列索引获取对应的值（与 {@link QuotationExportColumnLayout} 一致）
     */
    private static String getCellValueForColumn(SalSedQuotationExportVo data, int col, QuotationExportColumnLayout layout) {
        if (layout.lengthWidthSheet) {
            return switch (col) {
                case 0 -> data.getItem() != null ? data.getItem() : "";
                case 1 -> data.getProductPhoto() != null ? data.getProductPhoto() : "";
                case 2 -> data.getLength() != null ? data.getLength().toString() : "";
                case 3 -> data.getWidth() != null ? data.getWidth().toString() : "";
                case 4 -> data.getQtyPerCarton() != null ? data.getQtyPerCarton().toString() : "";
                case 5 -> data.getCartonSize() != null ? data.getCartonSize() : "";
                case 6 -> data.getCbm() != null ? data.getCbm().toString() : "";
                case 7 -> data.getMoq() != null ? data.getMoq().toString() : "";
                case 8 -> data.getWeightPerUnit() != null ? data.getWeightPerUnit().toString() : "";
                case 9 -> "";
                case 10 -> data.getRemarks() != null ? data.getRemarks() : "";
                case 11 -> data.getFobNingboPrice() != null ? data.getFobNingboPrice().toString() : "";
                case 12, 13 -> "";
                default -> "";
            };
        }
        if (col <= 4) {
            if (isLengthWidthOnlyRow(data)) {
                return switch (col) {
                    case 2 -> data.getLength() != null ? data.getLength().toString() : "";
                    case 3 -> data.getWidth() != null ? data.getWidth().toString() : "";
                    case 4 -> "";
                    default -> col == 0 ? (data.getItem() != null ? data.getItem() : "")
                            : col == 1 ? (data.getProductPhoto() != null ? data.getProductPhoto() : "") : "";
                };
            }
            return switch (col) {
                case 0 -> data.getItem() != null ? data.getItem() : "";
                case 1 -> data.getProductPhoto() != null ? data.getProductPhoto() : "";
                case 2 -> data.getDiameterTop() != null ? data.getDiameterTop().toString() : "";
                case 3 -> data.getDiameterBottom() != null ? data.getDiameterBottom().toString() : "";
                case 4 -> data.getHeight() != null ? data.getHeight().toString() : "";
                default -> "";
            };
        }
        return switch (col) {
            case 5 -> data.getQtyPerCarton() != null ? data.getQtyPerCarton().toString() : "";
            case 6 -> data.getCartonSize() != null ? data.getCartonSize() : "";
            case 7 -> data.getCbm() != null ? data.getCbm().toString() : "";
            case 8 -> data.getMoq() != null ? data.getMoq().toString() : "";
            case 9 -> data.getWeightPerUnit() != null ? data.getWeightPerUnit().toString() : "";
            case 10 -> "";
            case 11 -> data.getRemarks() != null ? data.getRemarks() : "";
            case 12 -> data.getFobNingboPrice() != null ? data.getFobNingboPrice().toString() : "";
            case 13, 14 -> "";
            default -> "";
        };
    }

    /**
     * 计算字符串显示宽度（中文字符按2倍计算）
     */
    private static int getStringWidth(String str) {
        if (str == null || str.isEmpty()) {
            return 0;
        }
        int width = 0;
        for (char c : str.toCharArray()) {
            // 中文字符、全角字符按2个字符计算
            if (c > 127) {
                width += 2;
            } else {
                width += 1;
            }
        }
        return width + 2; // 加2个字符的左右边距
    }

    /**
     * 根据图片URL计算合适的行高
     * @param imageUrl 图片URL
     * @return 行高（单位：磅）
     */
    private static float calculateRowHeightFromImage(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            BufferedImage image = ImageIO.read(url);
            if (image == null) {
                return 80f;
            }

            int imageWidth = image.getWidth();   // 图片宽度（像素）
            int imageHeight = image.getHeight(); // 图片高度（像素）

            // 第1列（PRODUCT PHOTO）的宽度约25字符，转换为像素：25 * 7 ≈ 175像素
            int columnWidthPx = 175;

            // 根据宽度比例计算需要的行高
            // 如果图片宽度大于列宽，按比例缩放高度
            float aspectRatio = (float) imageHeight / imageWidth;

            // 计算显示后的高度（考虑图片可能会被压缩适应列宽）
            float displayHeight = columnWidthPx * aspectRatio;

            // 转换为Excel行高（1像素 ≈ 0.75磅）
            float rowHeight = displayHeight * 0.75f;

            // 设置最小和最大高度限制（避免过小或过大）
            rowHeight = Math.max(60f, Math.min(rowHeight, 200f));

            return rowHeight;
        } catch (Exception e) {
            // 图片加载失败，使用默认高度
            return 80f;
        }
    }

    /**
     * 设置单元格值（支持字符串和数值类型）
     */
    private static void setCellValue(Row row, int columnIndex, Object value, CellStyle style) {
        Cell cell = row.createCell(columnIndex);
        cell.setCellStyle(style);

        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else if (value instanceof java.math.BigDecimal) {
            cell.setCellValue(((java.math.BigDecimal) value).doubleValue());
        } else {
            cell.setCellValue(value.toString());
        }
    }


    //计算总成本（物流+采购+sku成本）
    public void calculateTotalCost(Long id,SalSedQuotationVo quotationVo) {
        SalSedQuotationVo salSedQuotationVo=  procurementDetail(id);
        //只需要包材+零件，这里去掉配件是因为这里是外采部分，所有配件成本在成本明细里的方法有
        Map<String, List<SalSedQuotationProcurementVo>> procurementMap = salSedQuotationVo.getProcurementMap();
        List<SalSedQuotationProcurementVo> procurementList = procurementMap.get("包材");
        //计算包材成本
        BigDecimal totalPackingCost = BigDecimal.ZERO;
        for(SalSedQuotationProcurementVo procurement: procurementList){
            if(procurement.getCostPrice()==null){
                procurement.setCostPrice(BigDecimal.ZERO);
            }
            BigDecimal costPrice = procurement.getCostPrice();
            BigDecimal boxMum = BigDecimal.valueOf(procurement.getBoxMum());
            BigDecimal packingCost = costPrice.multiply(boxMum);
            totalPackingCost = totalPackingCost.add(packingCost);
        }
        //计算零件成本
        List<SalSedQuotationProcurementVo> procurementPartList = procurementMap.get("零件");
        BigDecimal totalPartCost = BigDecimal.ZERO;
        for(SalSedQuotationProcurementVo procurement: procurementPartList){
            BigDecimal costPrice = procurement.getCostPrice();
            BigDecimal boxMum = BigDecimal.valueOf(procurement.getBoxMum());
            BigDecimal partCost = costPrice.multiply(boxMum);
            totalPartCost = totalPartCost.add(partCost);
        }

        //采购部分
        BigDecimal totalCost = totalPackingCost.add(totalPartCost);

        //物流部分
        LambdaQueryWrapper<SalSedQuotation> queryWrapper = Wrappers.lambdaQuery(SalSedQuotation.class)
                .eq(SalSedQuotation::getId, id)
                .eq(SalSedQuotation::getIsDeleted, 0);
                SalSedQuotation quotation = salSedQuotationMapper.selectOne(queryWrapper);
        BigDecimal logisticsCost = quotation.getLogisticsCost();

        //根据报价单id获取报价单的sku信息
        LambdaQueryWrapper<SalSedQuotationSku> queryWrapper1 = Wrappers.lambdaQuery(SalSedQuotationSku.class)
                .eq(SalSedQuotationSku::getQuotationId, id)
                .eq(SalSedQuotationSku::getIsDeleted, 0);
        List<SalSedQuotationSku> skuList = salSedQuotationSkuMapper.selectList(queryWrapper1);
        //SKU成本（配件+工艺）
        BigDecimal totalSkuCost= BigDecimal.ZERO;
        for (SalSedQuotationSku sku: skuList){
            totalSkuCost=totalSkuCost.add(calculateSingleSkuCost(sku.getSkuId(), sku.getMatchId()));
        }

        BigDecimal total = totalCost.add(logisticsCost).add(totalSkuCost);
        //更改报价单的总成本和总成本状态和状态
        SalSedQuotation salSedQuotation = new SalSedQuotation();
        salSedQuotation.setId(id);
        salSedQuotation.setTotalCost(total);
        salSedQuotation.setTotalCostState("1");
        //计算成本中的才改变状态
        if(quotation.getStatus().equals(QuotationStatusEnum.CALCULATING.getCode())) {
            salSedQuotation.setStatus(QuotationStatusEnum.CALCULATED.getCode());
            //赋值返回的数据
            quotationVo.setStatus(QuotationStatusEnum.CALCULATED.getCode());
        }
        salSedQuotationMapper.updateById(salSedQuotation);

        //赋值返回的数据
        quotationVo.setTotalCost(total);
        quotationVo.setTotalCostState("1");
    }


    public Boolean judgeSkuCostConfirm(Long id) {
        //判断sku成本是否被确认
        //根据报价单id获取报价单的sku信息
        LambdaQueryWrapper<SalSedQuotationSku> queryWrapper1 = Wrappers.lambdaQuery(SalSedQuotationSku.class)
                .eq(SalSedQuotationSku::getQuotationId, id)
                .eq(SalSedQuotationSku::getIsDeleted, 0);
        List<SalSedQuotationSku> skuList = salSedQuotationSkuMapper.selectList(queryWrapper1);
        List<Long> skuIds =new ArrayList<>();
        for(SalSedQuotationSku sku: skuList){
            skuIds.add(sku.getSkuId());
        }
        if(!skuIds.isEmpty()) {
            List<ProSedProductMatchSku> productMatchSkuList = proSedProductMatchSkuMapper.selectBatchIds(skuIds);
            for (ProSedProductMatchSku productMatchSku : productMatchSkuList) {
                if (productMatchSku.getIsConfirm().equals(false)) {
                    //代表工艺成本未确认
                    return false;
                }
            }
        }
        return true;
    }

    //判断采购成本、物流成本、sku成本是否都确认
    public Boolean costConfirm(Long id, SalSedQuotationVo quotationVo) {
        if(quotationVo.getLogisticsCostState().equals("0")||quotationVo.getProcurementCostState().equals("0")){
            return false;
        }else{
            return  judgeSkuCostConfirm(id);
        }
    }

    /**
     * 根据币种判断需要显示的字段
     * @param currency 币种（美元/人民币）
     * @return 需要显示的字段列表
     */
    public Map<String, Boolean> getDisplayFieldsByCurrency(String currency) {
        Map<String, Boolean> displayFields = new HashMap<>();

        if ("美元".equals(currency) || "USD".equals(currency)) {
            // 币种为美元时，显示汇率，不显示是否含税
            displayFields.put("showExchangeRate", true);
            displayFields.put("showTax", false);
        } else if ("人民币".equals(currency) || "CNY".equals(currency)) {
            // 币种为人民币时，显示是否含税，不显示汇率
            displayFields.put("showExchangeRate", false);
            displayFields.put("showTax", true);
        } else {
            // 默认情况，都不显示
            displayFields.put("showExchangeRate", false);
            displayFields.put("showTax", false);
        }

        return displayFields;
    }
}
