package com.qiaomoyun.service.sal.sed;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.qiaomoyun.Exception.BizException;
import com.qiaomoyun.Exception.ExceptionCodeEnum;
import com.qiaomoyun.entity.pro.sed.ProSedFile;
import com.qiaomoyun.entity.pro.sed.ProSedProductMatchSku;
import com.qiaomoyun.entity.sal.sed.SalSedOrderDetail;
import com.qiaomoyun.entity.sal.sed.SalSedQuotation;
import com.qiaomoyun.entity.sal.sed.SalSedQuotationHistory;
import com.qiaomoyun.entity.sal.sed.SalSedQuotationSku;
import com.qiaomoyun.entity.sal.sed.SalSedQuotationSkuPacking;
import com.qiaomoyun.entity.sal.yt.SalYtCustomer;
import com.qiaomoyun.entity.sal.yt.SalYtCustomerAddress;
import com.qiaomoyun.entity.sys.SysDictionary;
import com.qiaomoyun.entity.sys.SysRole;
import com.qiaomoyun.entity.sys.SysTenant;
import com.qiaomoyun.entity.sys.SysTenantConfig;
import com.qiaomoyun.entity.sys.SysUser;
import com.qiaomoyun.eunm.sed.FileTypeEnum;
import com.qiaomoyun.eunm.sed.QuotationJointAuditActionEnum;
import com.qiaomoyun.eunm.sed.QuotationOperationEnum;
import com.qiaomoyun.eunm.sed.QuotationStatusEnum;
import com.qiaomoyun.eunm.sys.DictionaryConfigEnum;
import com.qiaomoyun.eunm.sys.TenantConfigEnum;
import com.qiaomoyun.eunm.yt.AccountSystemEnum;
import com.qiaomoyun.info.PageResultInfo;
import com.qiaomoyun.manager.pro.sed.ProSedFileManager;
import com.qiaomoyun.manager.sal.sed.SalSedQuotationManager;
import com.qiaomoyun.manager.sal.sed.SalSedQuotationSkuManager;
import com.qiaomoyun.manager.sal.sed.SalSedQuotationSkuPackingManager;
import com.qiaomoyun.manager.sys.SysDictionaryManager;
import com.qiaomoyun.manager.sys.SysPermissionManager;
import com.qiaomoyun.manager.sys.SysRoleManager;
import com.qiaomoyun.mapper.pro.sed.ProSedFittingMapper;
import com.qiaomoyun.mapper.pro.sed.ProSedPackingMapper;
import com.qiaomoyun.mapper.pro.sed.ProSedPartMapper;
import com.qiaomoyun.mapper.pro.sed.ProSedProductMatchSkuMapper;
import com.qiaomoyun.mapper.sal.sed.SalSedOrderDetailMapper;
import com.qiaomoyun.mapper.sal.sed.SalSedQuotationHistoryMapper;
import com.qiaomoyun.mapper.sal.sed.SalSedQuotationMapper;
import com.qiaomoyun.mapper.sal.sed.SalSedQuotationSkuMapper;
import com.qiaomoyun.mapper.sal.sed.SalSedQuotationSkuPackingMapper;
import com.qiaomoyun.mapper.sal.yt.SalYtCustomerAddressMapper;
import com.qiaomoyun.mapper.sal.yt.SalYtCustomerMapper;
import com.qiaomoyun.mapper.sys.SysTenantConfigMapper;
import com.qiaomoyun.mapper.sys.SysTenantMapper;
import com.qiaomoyun.mapper.sys.SysUserMapper;
import com.qiaomoyun.mapper.sys.SysUserRoleMapper;
import com.qiaomoyun.param.sal.sed.SalSedHistoryQuotationInfoParams;
import com.qiaomoyun.param.sal.sed.SalSedQuotationAuditParams;
import com.qiaomoyun.param.sal.sed.SalSedQuotationCostDetailParams;
import com.qiaomoyun.param.sal.sed.SalSedQuotationExportParams;
import com.qiaomoyun.param.sal.sed.SalSedQuotationHistoryImportParams;
import com.qiaomoyun.param.sal.sed.SalSedQuotationJointAuditParams;
import com.qiaomoyun.param.sal.sed.SalSedQuotationLogisticsParams;
import com.qiaomoyun.param.sal.sed.SalSedQuotationMergeListParams;
import com.qiaomoyun.param.sal.sed.SalSedQuotationMergeSkuListParams;
import com.qiaomoyun.param.sal.sed.SalSedQuotationMergeToOrderParams;
import com.qiaomoyun.param.sal.sed.SalSedQuotationOneKeyToOrderParams;
import com.qiaomoyun.param.sal.sed.SalSedQuotationOperateParams;
import com.qiaomoyun.param.sal.sed.SalSedQuotationPackingParams;
import com.qiaomoyun.param.sal.sed.SalSedQuotationParams;
import com.qiaomoyun.param.sal.sed.SalSedQuotationSaveOrUpdateParams;
import com.qiaomoyun.param.sal.sed.SalSedQuotationSkuPackingParams;
import com.qiaomoyun.param.sal.sed.SalSedQuotationSkuParams;
import com.qiaomoyun.param.sal.sed.SalSedQuotationSkuToOrderParams;
import com.qiaomoyun.util.EntityCodeGenerateUtil;
import com.qiaomoyun.util.FeiShuUtil;
import com.qiaomoyun.util.LoginUserInfoContext;
import com.qiaomoyun.util.TenantInfoContext;
import com.qiaomoyun.vo.pro.sed.ProSedFileVO;
import com.qiaomoyun.vo.sal.sed.SalSedCustomerAddressVo;
import com.qiaomoyun.vo.sal.sed.SalSedCustomerVo;
import com.qiaomoyun.vo.sal.sed.SalSedHistoryQuotationInfoVo;
import com.qiaomoyun.vo.sal.sed.SalSedQuotationCostDetailShiftVo;
import com.qiaomoyun.vo.sal.sed.SalSedQuotationDetailVo;
import com.qiaomoyun.vo.sal.sed.SalSedQuotationHistoryImportVo;
import com.qiaomoyun.vo.sal.sed.SalSedQuotationHistoryVo;
import com.qiaomoyun.vo.sal.sed.SalSedQuotationLogisticsVo;
import com.qiaomoyun.vo.sal.sed.SalSedQuotationMergeItemVo;
import com.qiaomoyun.vo.sal.sed.SalSedQuotationMergeSkuItemVo;
import com.qiaomoyun.vo.sal.sed.SalSedQuotationSkuVo;
import com.qiaomoyun.vo.sal.sed.SalSedQuotationVo;
import com.qiaomoyun.vo.sys.DictionaryOptionVO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SalSedQuotationService {

    /** 财务角色 ID（与 sys_role 主键一致，见角色管理） */
    private static final int FINANCE_ROLE_ID = 29;
    /** 总裁角色 ID（与 sys_role 主键一致，见角色管理） */
    private static final int PRESIDENT_ROLE_ID = 30;
    /** 销售角色 ID（与 sys_role 主键一致，见角色管理） */
    private static final int SALES_ROLE_ID = 28;

    @Resource
    private SalSedQuotationManager salSedQuotationManager;


    @Resource
    private SalSedQuotationMapper salSedQuotationMapper;

    @Resource
    private SalSedQuotationHistoryMapper salSedQuotationHistoryMapper;

    @Resource
    private SalSedQuotationSkuManager salSedQuotationSkuManager;

    @Resource
    private SalSedQuotationSkuPackingManager salSedQuotationSkuPackingManager;

    @Resource
    private SalYtCustomerMapper salYtCustomerMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private SalYtCustomerAddressMapper salYtCustomerAddressMapper;

    @Resource
    private SalSedQuotationSkuMapper salSedQuotationSkuMapper;

    @Resource
    private SalSedQuotationSkuPackingMapper salSedQuotationSkuPackingMapper;

    @Resource
    private ProSedFileManager proSedFileManager;

    @Resource
    private ProSedPackingMapper proSedPackingMapper;

    @Resource
    private ProSedProductMatchSkuMapper proSedProductMatchSkuMapper;
    @Resource
    private ProSedFittingMapper proSedFittingMapper;

    @Resource
    private ProSedPartMapper proSedPartMapper;

    @Resource
    private SysDictionaryManager sysDictionaryManager;

    @Resource
    private SalSedOrderDetailMapper salSedOrderDetailMapper;

    @Resource
    private SysRoleManager sysRoleManager;

    @Resource
    private SysPermissionManager sysPermissionManager;

    @Resource
    private SysTenantMapper sysTenantMapper;

    @Resource
    private SysTenantConfigMapper sysTenantConfigMapper;

    @Resource
    private SysUserRoleMapper sysUserRoleMapper;

    @Value("${feishu.finance-audit-url:}")
    private String feishuFinanceAuditUrl;

    @Value("${feishu.president-audit-url:}")
    private String feishuPresidentAuditUrl;

    /**
     * 获取报价单列表
     * @param params
     * @return
     */
    public PageResultInfo<SalSedQuotationVo> list(SalSedQuotationParams params) {
        PageHelper.startPage(params.getPageNum(), params.getPageSize());

        List<SalSedQuotationVo> list = salSedQuotationMapper.list(params);
        //关闭数据权限
        LoginUserInfoContext.setIsOrganizeData(false);
       //动态计算成本
        for (SalSedQuotationVo quotationVo: list){
            //判断采购成本、物流成本、sku成本是否都确认，只要有一样没确认就不计算总成本
            Boolean isConfirm = salSedQuotationManager.costConfirm(quotationVo.getId(), quotationVo);
            if(isConfirm) {
                //计算成本中才计算总成本
                if(quotationVo.getStatus().equals(QuotationStatusEnum.CALCULATING.getCode())) {
                    salSedQuotationManager.calculateTotalCost(quotationVo.getId(), quotationVo);
                }
            }
            enrichJointAuditUi(quotationVo);
        }
        return new PageResultInfo<>(list);

    }

    /**
     * 根据报价单id获取报价单采购成本详情
     * @param id
     * @return
     */

    public SalSedQuotationVo procurementDetail(Long id) {
        if(id == null){
            throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(),"请传入报价单id");
        }


        //判断该报价单是否存在
        LambdaQueryWrapper<SalSedQuotation> queryWrapper = Wrappers.lambdaQuery(SalSedQuotation.class)
                .eq(SalSedQuotation::getId, id)
                .eq(SalSedQuotation::getIsDeleted, 0);
        SalSedQuotation quotation = salSedQuotationMapper.selectOne(queryWrapper);
        if(quotation == null) {
            throw new BizException(400,"报价单不存在");
        }


        return salSedQuotationManager.procurementDetail(id);
    }

    /**
     * 采购成本确认
     * @param params
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String procurementConfirm(SalSedQuotationOperateParams params) {

        if(params.getId()== null){
            throw new BizException(400,"请传入报价单id");
        }
        if(params.getSalesmanId()==null){
            throw new BizException(400,"请传入业务员id");
        }
        if(params.getProcurementCost()==null){
            throw new BizException(400,"请传入采购成本");
        }
        for(SalSedQuotationPackingParams packingInfo: params.getPackingInfo()){
            if(packingInfo.getPackingId()==null||packingInfo.getCostPrice()==null){
                throw new BizException(400,"包材id和成本单价数据不完整");
            }

        }

        //判断该报价单是否存在
        LambdaQueryWrapper<SalSedQuotation> queryWrapper = Wrappers.lambdaQuery(SalSedQuotation.class)
                .eq(SalSedQuotation::getId, params.getId())
                .eq(SalSedQuotation::getIsDeleted, 0);
        SalSedQuotation quotation = salSedQuotationMapper.selectOne(queryWrapper);
        if(quotation == null) {
            throw new BizException(400,"报价单不存在");
        }


        //1.更改报价单采购成本状态和采购成本
        salSedQuotationMapper.procurementConfirm(params.getId(),params.getProcurementCost());

        //2.修改配置表里面的包材成本单价
        if(!params.getPackingInfo().isEmpty()) {
            // 遍历包材信息，更新 sal_sed_quotation_sku_packing 表的 cost 字段
            for (SalSedQuotationPackingParams packingParams : params.getPackingInfo()) {
                // 根据报价单 ID 和包材 ID 查询报价单 SKU 包材记录（可能有多条）
                LambdaQueryWrapper<SalSedQuotationSkuPacking> packingQueryWrapper = Wrappers.lambdaQuery(SalSedQuotationSkuPacking.class)
                        .eq(SalSedQuotationSkuPacking::getQuotationId, params.getId())
                        .eq(SalSedQuotationSkuPacking::getPackingId, packingParams.getPackingId())
                        .eq(SalSedQuotationSkuPacking::getIsDeleted, 0);
                List<SalSedQuotationSkuPacking> skuPackingList = salSedQuotationSkuPackingMapper.selectList(packingQueryWrapper);

                // 更新所有匹配的记录
                for (SalSedQuotationSkuPacking skuPacking : skuPackingList) {
                    skuPacking.setCost(packingParams.getCostPrice());
                    salSedQuotationSkuPackingMapper.updateById(skuPacking);
                }
            }
        }
        //3.修改配置表里面的零件成本单价
        if(!params.getPartInfo().isEmpty()) {
            proSedPartMapper.updateByIds(params.getPartInfo());
        }
        //4.修改配置表里面的配件成本单价
        if(!params.getFittingInfo().isEmpty()) {
            proSedFittingMapper.updateByIds(params.getFittingInfo());
        }

        //5.往历史记录表增加一条数据（谁干了嘛）
        SalSedQuotationHistory salSedQuotationHistory = new SalSedQuotationHistory();
        salSedQuotationHistory.setQuotationId(params.getId());
        salSedQuotationHistory.setContext(QuotationOperationEnum.confirm_purchase.getCode());
        salSedQuotationHistory.setCreateUser(params.getSalesmanId());
        salSedQuotationHistoryMapper.insert(salSedQuotationHistory);

        //判断物流成本是否被确认，如果确认，就判断sku成本是否被确认，如果确认，计算总成本
       // Boolean status=salSedQuotationManager.judgeCostConfirm(params.getId(),quotation);
      //  if(status){
            //计算总成本
      //      salSedQuotationManager.calculateTotalCost(params.getId());
     //   }


        return "确认成功";
    }

    /**
     * 获取报价单物流成本详情
     * @param id
     * @return
     */
    public SalSedQuotationLogisticsVo logisticsDetail(Long id) {
        //判断该报价单是否存在
        LambdaQueryWrapper<SalSedQuotation> queryWrapper = Wrappers.lambdaQuery(SalSedQuotation.class)
                .eq(SalSedQuotation::getId, id)
                .eq(SalSedQuotation::getIsDeleted, 0);
        SalSedQuotation quotation = salSedQuotationMapper.selectOne(queryWrapper);
        if(quotation == null) {
            throw new BizException(400,"报价单不存在");
        }

        //计算总运输体积
        BigDecimal totalTransportVolume = salSedQuotationSkuManager.calculationVolume(id);
        //计算总运箱数
        Integer totalTransportBox = salSedQuotationSkuPackingManager.calculationBox(id);
        //根据报价单id获取报价单收货地址
        LambdaQueryWrapper<SalSedQuotation> queryWrapper1 = Wrappers.lambdaQuery(SalSedQuotation.class)
                .select(SalSedQuotation::getReceiveAddress)
                .eq(SalSedQuotation::getId, id)
                .eq(SalSedQuotation::getIsDeleted, 0);

        SalSedQuotation salSedQuotation = salSedQuotationMapper.selectOne(queryWrapper1);
        String receiveAddress = "";
        //判断收货地址是否为空
        if(StringUtils.isNotBlank(salSedQuotation.getReceiveAddress())){
             receiveAddress = salSedQuotation.getReceiveAddress();
        }


        //封装数据
        SalSedQuotationLogisticsVo logisticsDetail = new SalSedQuotationLogisticsVo();
        logisticsDetail.setTotalTransportVolume(totalTransportVolume);
        logisticsDetail.setTotalTransportBox(totalTransportBox);
        logisticsDetail.setReceiveAddress(receiveAddress);


        return logisticsDetail;
    }

    /**
     * 物流成本确认
     * @param params
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String logisticsConfirm(SalSedQuotationLogisticsParams params) {
        if(params.getId()== null){
            throw new BizException(400,"请传入报价单id");
        }
        if(params.getSalesmanId()==null){
            throw new BizException(400,"请传入业务员id");
        }
        if(params.getLogisticsCost()==null){
            throw new BizException(400,"请传入物流成本");
        }

        //判断该报价单是否存在
        LambdaQueryWrapper<SalSedQuotation> queryWrapper = Wrappers.lambdaQuery(SalSedQuotation.class)
                .eq(SalSedQuotation::getId, params.getId())
                .eq(SalSedQuotation::getIsDeleted, 0);
        SalSedQuotation quotation = salSedQuotationMapper.selectOne(queryWrapper);
        if(quotation == null) {
            throw new BizException(400,"报价单不存在");
        }


        //将物流成本和物流备注更新和物流成本确认状态
        salSedQuotationMapper.logisticsConfirm(params);

        //向历史记录表增加一条数据（谁干了嘛）
        SalSedQuotationHistory salSedQuotationHistory = new SalSedQuotationHistory();
        salSedQuotationHistory.setQuotationId(params.getId());
        salSedQuotationHistory.setContext(QuotationOperationEnum.confirm_logistics.getCode());
        salSedQuotationHistory.setCreateUser(params.getSalesmanId());
        salSedQuotationHistoryMapper.insert(salSedQuotationHistory);

        //判断物流成本是否被确认，如果确认，就判断sku成本是否被确认，如果确认，计算总成本
       // Boolean status=salSedQuotationManager.judgeCostConfirm1(params.getId(),quotation);
      //  if(status){
            //计算总成本
      //      salSedQuotationManager.calculateTotalCost(params.getId());
      //  }
        return "确认成功";
    }

    /**
     * 新增报价单和编辑修改报价单（是同一种接口，传id就是修改，没有就是新增）
     * @param params
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String saveOrUpdate(SalSedQuotationSaveOrUpdateParams params) {
        SalSedQuotation salSedQuotation = new SalSedQuotation();
        if(params.getId()== null) {
        //向报价单表插入数据

            //报价单编号
            salSedQuotation.setQuotationCode(EntityCodeGenerateUtil.generateUniqueId("B"));
            salSedQuotation.setCustomerId(params.getCustomerId());
            salSedQuotation.setSalesmanId(params.getSalesmanId());
            //采购成本在采购成本确认是再存入
            salSedQuotation.setProcurementCostState("0");
            salSedQuotation.setLogisticsCostState("0");
            salSedQuotation.setTotalCostState("0");

            //订单金额计算=sum(数量*报价)-优惠金额
            BigDecimal amount=BigDecimal.ZERO;
            List<SalSedQuotationSkuParams> skuList= params.getSkuList();
            for (SalSedQuotationSkuParams sku: skuList) {
                //校验必填字段
                if(sku.getQuantity()==null){
                    throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(), "数量不能为空");
                }
                if(sku.getQuotationPrice()==null){
                    throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(), "报价不能为空");
                }
                if(sku.getVolume()==null){
                    throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(), "体积不能为空");
                }
                //sum(数量*报价)
                amount=amount.add(sku.getQuotationPrice().multiply(new BigDecimal(sku.getQuantity())));
            }
            //优惠金额不强制填写，没填写就默认为0
            if(params.getDiscountAmount()== null){
                salSedQuotation.setDiscountAmount(BigDecimal.ZERO);
                salSedQuotation.setOrderAmount(amount);
            }else {
                salSedQuotation.setDiscountAmount(params.getDiscountAmount());
                salSedQuotation.setOrderAmount(amount.subtract(params.getDiscountAmount()));
            }

            //收货地址id
            salSedQuotation.setReceiveAddressId(params.getReceiveAddressId());
            //根据收货地址id获取收货地址
            LambdaQueryWrapper<SalYtCustomerAddress> queryWrapper = Wrappers.lambdaQuery(SalYtCustomerAddress.class)
                    .eq(SalYtCustomerAddress::getId, params.getReceiveAddressId());
            SalYtCustomerAddress salYtCustomerAddress = salYtCustomerAddressMapper.selectOne(queryWrapper);
            //将收货地址省市区详细地址拼接起来
            salSedQuotation.setReceiveAddress(salYtCustomerAddress.getProvince()+salYtCustomerAddress.getCity()+salYtCustomerAddress.getCounty()+salYtCustomerAddress.getDetail());
            //特殊要求
            if(StringUtils.isNotBlank(params.getSpecialRequirements())) {
                salSedQuotation.setSpecialRequirements(params.getSpecialRequirements());
            }
            //币种
            if(StringUtils.isNotBlank(params.getCurrency())) {
                salSedQuotation.setCurrency(params.getCurrency());
            }
            //是否含税
            if(StringUtils.isNotBlank(params.getTax())) {
                salSedQuotation.setTax(params.getTax());
            }
            //装运港
            if(StringUtils.isNotBlank(params.getFob())) {
                salSedQuotation.setFob(params.getFob());
            }
            //指定地点
            if(StringUtils.isNotBlank(params.getExw())) {
                salSedQuotation.setExw(params.getExw());
            }
            //汇率
            if(params.getExchangeRate() != null) {
                salSedQuotation.setExchangeRate(params.getExchangeRate());
            }
            //报价单状态
            salSedQuotation.setStatus(params.getStatus());
            //转换状态、
            salSedQuotation.setShiftStatus("0");
            //新增报价单
             salSedQuotationMapper.insert(salSedQuotation);

            //获取新增的报价单id
                Long quotationId = salSedQuotation.getId();


        //向报价单-sku表插入数据
            //报价单-sku 信息
            List<SalSedQuotationSkuParams> skuList1= params.getSkuList();
            if(!skuList1.isEmpty()) {
                for (SalSedQuotationSkuParams sku : skuList1) {
                    //校验包材信息不能为空
                    if(sku.getPacking() == null || sku.getPacking().isEmpty()){
                        throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(), "包材名称不能为空");
                    }
                    SalSedQuotationSku salSedQuotationSku = new SalSedQuotationSku();
                    //报价单 id
                    salSedQuotationSku.setQuotationId(quotationId);
                    //产品 id
                    salSedQuotationSku.setProductId(sku.getProductId());
                    //搭配 id
                    salSedQuotationSku.setMatchId(sku.getMatchId());
                    //SKUid
                    salSedQuotationSku.setSkuId(sku.getSkuId());
                    //报价
                    salSedQuotationSku.setQuotationPrice(sku.getQuotationPrice());
                    //基础报价：取产品SKU的basicPrice
                    if (sku.getSkuId() != null) {
                        ProSedProductMatchSku productSku = proSedProductMatchSkuMapper.selectById(sku.getSkuId());
                        if (productSku != null && productSku.getBasicPrice() != null) {
                            salSedQuotationSku.setQuotationBasePrice(productSku.getBasicPrice());
                        }
                    }
                    //数量
                    salSedQuotationSku.setQuantity(sku.getQuantity());
                    //体积
                    salSedQuotationSku.setVolume(sku.getVolume());
                    //备注
                    salSedQuotationSku.setRemark(sku.getRemark());
                    //向报价单-sku 表插入数据
                    salSedQuotationSkuMapper.insert(salSedQuotationSku);

                    //获取新增的报价单-sku的id
                    Long quotationSkuId = salSedQuotationSku.getId();

                    //向报价单-sku-包材表插入数据
                    //SKU数量
                    Integer quantity = sku.getQuantity();
                    //报价单-sku-包材信息
                    List<SalSedQuotationSkuPackingParams> packing = sku.getPacking();
                    for (SalSedQuotationSkuPackingParams packingParams : packing) {
                        SalSedQuotationSkuPacking salSedQuotationSkuPacking = new SalSedQuotationSkuPacking();
                        //报价单id
                        salSedQuotationSkuPacking.setQuotationId(quotationId);
                        //报价单-sku的id
                        salSedQuotationSkuPacking.setQuotationSkuId(quotationSkuId);
                        //包材id
                        salSedQuotationSkuPacking.setPackingId(packingParams.getPackingId());
                        //装箱数
                        salSedQuotationSkuPacking.setPackingNum(packingParams.getPackingNum());
                        //包材尺寸
                        salSedQuotationSkuPacking.setPackingSize(packingParams.getPackingSize());
                        //成本
                        salSedQuotationSkuPacking.setCost(packingParams.getCost());
                        //所需包材数量
                        Integer boxMum;
                        if (packingParams.getPackingNum() != null) {
                            //包材数量除以装箱数，向上取整就是所需包材数量 (a + b - 1) / b
                            boxMum = (quantity + packingParams.getPackingNum() - 1) / packingParams.getPackingNum();
                        } else {
                            //没有装箱数，则所需包材数量就是SKU数量
                            boxMum = quantity;
                        }
                        salSedQuotationSkuPacking.setBoxMum(boxMum);
                        //向报价单-sku-包材表插入数据
                        salSedQuotationSkuPackingMapper.insert(salSedQuotationSkuPacking);
                        //处理包材附件
                        if (!packingParams.getAttachmentList().isEmpty()) {
                            proSedFileManager.batchSaveOrUpdate(BeanUtil.copyToList(packingParams.getAttachmentList(), ProSedFileVO.class), salSedQuotationSkuPacking.getId(), FileTypeEnum.quotationPackingFile);
                        }
                    }
                }
            }

            //向历史记录表增加一条数据（谁干了嘛）
            SalSedQuotationHistory salSedQuotationHistory = new SalSedQuotationHistory();
            salSedQuotationHistory.setQuotationId(quotationId);
            salSedQuotationHistory.setContext(params.getOperation());
            salSedQuotationHistory.setCreateUser(params.getSalesmanId());
            salSedQuotationHistoryMapper.insert(salSedQuotationHistory);

            return "创建成功";


        }else{
            //编辑
            return updateQuotation( params);

        }

    }

    public String updateQuotation(SalSedQuotationSaveOrUpdateParams params) {
        SalSedQuotation salSedQuotation = new SalSedQuotation();
        //修改报价单表数据
        salSedQuotation.setId(params.getId());
        salSedQuotation.setQuotationCode(params.getQuotationCode());
        salSedQuotation.setCustomerId(params.getCustomerId());
        salSedQuotation.setSalesmanId(params.getSalesmanId());
        // operation=5 "确认修改，重新进入成本核算环节" → 重置三个成本状态为待确认
        // operation=2 "提交审核" → 保持原有成本状态不变
        if (QuotationOperationEnum.modify_quotation.getCode().equals(params.getOperation())) {
            salSedQuotation.setProcurementCostState("0");
            salSedQuotation.setLogisticsCostState("0");
            salSedQuotation.setTotalCostState("0");
        }
        //订单金额计算=sum(数量*报价)-优惠金额
        BigDecimal amount=BigDecimal.ZERO;
        //获取SKU信息
        List<SalSedQuotationSkuParams> skuList= params.getSkuList();
        if(!skuList.isEmpty()) {
            for (SalSedQuotationSkuParams sku : skuList) {
                //校验必填字段
                if (sku.getQuantity() == null) {
                    throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(), "数量不能为空");
                }
                if (sku.getQuotationPrice() == null) {
                    throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(), "报价不能为空");
                }
                if (sku.getVolume() == null) {
                    throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(), "体积不能为空");
                }
                //sum(数量*报价)
                amount = amount.add(sku.getQuotationPrice().multiply(new BigDecimal(sku.getQuantity())));
            }
        }
        //优惠金额不强制填写，没填写就默认为0
        if(params.getDiscountAmount()== null){
            salSedQuotation.setDiscountAmount(BigDecimal.ZERO);
            salSedQuotation.setOrderAmount(amount);
        }else {
            salSedQuotation.setDiscountAmount(params.getDiscountAmount());
            salSedQuotation.setOrderAmount(amount.subtract(params.getDiscountAmount()));
        }
        //收货地址id
        salSedQuotation.setReceiveAddressId(params.getReceiveAddressId());
        //根据收货地址id获取收货地址
        LambdaQueryWrapper<SalYtCustomerAddress> queryWrapper1 = Wrappers.lambdaQuery(SalYtCustomerAddress.class)
                .eq(SalYtCustomerAddress::getId, params.getReceiveAddressId());
        SalYtCustomerAddress salYtCustomerAddress = salYtCustomerAddressMapper.selectOne(queryWrapper1);
        //将收货地址省市区详细地址拼接起来
        salSedQuotation.setReceiveAddress(salYtCustomerAddress.getProvince()+salYtCustomerAddress.getCity()+salYtCustomerAddress.getCounty()+salYtCustomerAddress.getDetail());
        if(StringUtils.isNotBlank(params.getSpecialRequirements())) {
            salSedQuotation.setSpecialRequirements(params.getSpecialRequirements());
        }
        //币种
        if(StringUtils.isNotBlank(params.getCurrency())) {
            salSedQuotation.setCurrency(params.getCurrency());
        }
        //是否含税
        if(StringUtils.isNotBlank(params.getTax())) {
            salSedQuotation.setTax(params.getTax());
        }
        //装运港
        if(StringUtils.isNotBlank(params.getFob())) {
            salSedQuotation.setFob(params.getFob());
        }
        //指定地点
        if(StringUtils.isNotBlank(params.getExw())) {
            salSedQuotation.setExw(params.getExw());
        }
        //汇率
        if(params.getExchangeRate() != null) {
            salSedQuotation.setExchangeRate(params.getExchangeRate());
        }
        //修改报价单表状态
        salSedQuotation.setStatus(params.getStatus());
        //修改报价单表数据
        LambdaUpdateWrapper<SalSedQuotation> updateWrapper2 = Wrappers.lambdaUpdate(SalSedQuotation.class)
                .eq(SalSedQuotation::getId, params.getId());
                salSedQuotationMapper.update(salSedQuotation, updateWrapper2);
       // salSedQuotationMapper.updateById(salSedQuotation);

        //判断报价单-sku的id是否存在，存在就是修改，不存在就是新增
        if(!skuList.isEmpty()) {
            for (SalSedQuotationSkuParams sku : skuList) {
                if (sku.getId() == null) {
                    //校验包材信息不能为空
                    if(sku.getPacking() == null || sku.getPacking().isEmpty()){
                        throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(), "包材名称不能为空");
                    }
                    //新增报价单-sku
                    SalSedQuotationSku salSedQuotationSku = new SalSedQuotationSku();
                    //报价单 id
                    salSedQuotationSku.setQuotationId(params.getId());
                    //产品 id
                    salSedQuotationSku.setProductId(sku.getProductId());
                    //搭配 id
                    salSedQuotationSku.setMatchId(sku.getMatchId());
                    //SKUid
                    salSedQuotationSku.setSkuId(sku.getSkuId());
                    //报价
                    salSedQuotationSku.setQuotationPrice(sku.getQuotationPrice());
                    //基础报价：取产品SKU的basicPrice
                    if (sku.getSkuId() != null) {
                        ProSedProductMatchSku productSku = proSedProductMatchSkuMapper.selectById(sku.getSkuId());
                        if (productSku != null && productSku.getBasicPrice() != null) {
                            salSedQuotationSku.setQuotationBasePrice(productSku.getBasicPrice());
                        }
                    }
                    //数量
                    salSedQuotationSku.setQuantity(sku.getQuantity());
                    //体积
                    salSedQuotationSku.setVolume(sku.getVolume());
                    //备注
                    salSedQuotationSku.setRemark(sku.getRemark());
                    //向报价单-sku 表插入数据
                    salSedQuotationSkuMapper.insert(salSedQuotationSku);

                    //获得新增的报价单-sku的id
                    Long quotationSkuId = salSedQuotationSku.getId();

                    //向报价单-sku-包材表插入数据
                    //SKU数量
                    Integer quantity = sku.getQuantity();
                    //报价单-sku-包材信息
                    List<SalSedQuotationSkuPackingParams> packing = sku.getPacking();
                    for (SalSedQuotationSkuPackingParams packingParams : packing) {
                        SalSedQuotationSkuPacking salSedQuotationSkuPacking = new SalSedQuotationSkuPacking();
                        //报价单id
                        salSedQuotationSkuPacking.setQuotationId(params.getId());
                        //报价单-sku的id
                        salSedQuotationSkuPacking.setQuotationSkuId(quotationSkuId);
                        //包材id
                        salSedQuotationSkuPacking.setPackingId(packingParams.getPackingId());
                        //装箱数
                        salSedQuotationSkuPacking.setPackingNum(packingParams.getPackingNum());
                        //包材尺寸
                        salSedQuotationSkuPacking.setPackingSize(packingParams.getPackingSize());
                        //成本
                        salSedQuotationSkuPacking.setCost(packingParams.getCost());
                        //所需包材数量
                        Integer boxMum;
                        if (packingParams.getPackingNum() != null) {
                            //包材数量除以装箱数，向上取整就是所需包材数量 (a + b - 1) / b
                            boxMum = (quantity + packingParams.getPackingNum() - 1) / packingParams.getPackingNum();
                        } else {
                            //没有装箱数，则所需包材数量就是SKU数量
                            boxMum = quantity;
                        }
                        salSedQuotationSkuPacking.setBoxMum(boxMum);
                        //向报价单-sku-包材表插入数据
                        salSedQuotationSkuPackingMapper.insert(salSedQuotationSkuPacking);

                        //处理包材附件
                        if (!packingParams.getAttachmentList().isEmpty()) {
                            proSedFileManager.batchSaveOrUpdate(BeanUtil.copyToList(packingParams.getAttachmentList(), ProSedFileVO.class), salSedQuotationSkuPacking.getId(), FileTypeEnum.quotationPackingFile);
                        }
                    }
                } else {
                    //修改报价单-sku 表
                    SalSedQuotationSku salSedQuotationSku = new SalSedQuotationSku();
                    salSedQuotationSku.setSkuId(sku.getSkuId());
                    salSedQuotationSku.setQuotationId(params.getId());
                    salSedQuotationSku.setProductId(sku.getProductId());
                    salSedQuotationSku.setMatchId(sku.getMatchId());
                    salSedQuotationSku.setQuotationPrice(sku.getQuotationPrice());
                    //基础报价：取产品SKU的basicPrice
                    if (sku.getSkuId() != null) {
                        ProSedProductMatchSku productSku = proSedProductMatchSkuMapper.selectById(sku.getSkuId());
                        if (productSku != null && productSku.getBasicPrice() != null) {
                            salSedQuotationSku.setQuotationBasePrice(productSku.getBasicPrice());
                        }
                    }
                    salSedQuotationSku.setQuantity(sku.getQuantity());
                    salSedQuotationSku.setVolume(sku.getVolume());
                    //备注
                    salSedQuotationSku.setRemark(sku.getRemark());
                    //删除状态
                    if (sku.getIsDeleted() != null) {
                        salSedQuotationSku.setIsDeleted(sku.getIsDeleted());
                    }
                    LambdaUpdateWrapper<SalSedQuotationSku> updateWrapper = Wrappers.lambdaUpdate(SalSedQuotationSku.class)
                            .eq(SalSedQuotationSku::getId, sku.getId());
                    salSedQuotationSkuMapper.update(salSedQuotationSku, updateWrapper);

                    //判断报价单-sku-包材的id是否存在，存在就是修改，不存在就是新增
                    //校验包材信息不能为空
                    if(sku.getPacking() == null || sku.getPacking().isEmpty()){
                        throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(), "包材名称不能为空");
                    }
                    for (SalSedQuotationSkuPackingParams packing : sku.getPacking()) {
                        if (packing.getId() == null) {
                            //新增
                            //新增sku-包材表数据
                            SalSedQuotationSkuPacking salSedQuotationSkuPacking = new SalSedQuotationSkuPacking();
                            salSedQuotationSkuPacking.setQuotationId(params.getId());
                            salSedQuotationSkuPacking.setQuotationSkuId(sku.getId());
                            salSedQuotationSkuPacking.setPackingId(packing.getPackingId());
                            salSedQuotationSkuPacking.setPackingNum(packing.getPackingNum());
                            //包材尺寸
                            salSedQuotationSkuPacking.setPackingSize(packing.getPackingSize());
                            //成本
                            salSedQuotationSkuPacking.setCost(packing.getCost());
                            //所需包材数量
                            Integer boxMum;
                            if (packing.getPackingNum() != null) {
                                //包材数量除以装箱数，向上取整就是所需包材数量 (a + b - 1) / b
                                boxMum = (sku.getQuantity() + packing.getPackingNum() - 1) / packing.getPackingNum();
                            } else {
                                //没有装箱数，则所需包材数量就是SKU数量
                                boxMum = sku.getQuantity();
                            }
                            salSedQuotationSkuPacking.setBoxMum(boxMum);
                            //向报价单-sku-包材表插入数据
                            salSedQuotationSkuPackingMapper.insert(salSedQuotationSkuPacking);
                            //处理包材附件
                            if (!packing.getAttachmentList().isEmpty()) {
                                proSedFileManager.batchSaveOrUpdate(BeanUtil.copyToList(packing.getAttachmentList(), ProSedFileVO.class), salSedQuotationSkuPacking.getId(), FileTypeEnum.quotationPackingFile);

                            }
                        } else {
                            //修改
                            SalSedQuotationSkuPacking salSedQuotationSkuPacking = new SalSedQuotationSkuPacking();
                            salSedQuotationSkuPacking.setQuotationId(params.getId());
                            salSedQuotationSkuPacking.setQuotationSkuId(sku.getId());
                            salSedQuotationSkuPacking.setPackingId(packing.getPackingId());
                            salSedQuotationSkuPacking.setPackingNum(packing.getPackingNum());
                            //包材尺寸
                            salSedQuotationSkuPacking.setPackingSize(packing.getPackingSize());
                            //成本
                            salSedQuotationSkuPacking.setCost(packing.getCost());
                            //删除标识
                            if (packing.getIsDeleted() != null) {
                                salSedQuotationSkuPacking.setIsDeleted(packing.getIsDeleted());
                            }
                            //所需包材数量
                            Integer boxMum;
                            if (packing.getPackingNum() != null) {
                                //包材数量除以装箱数，向上取整就是所需包材数量 (a + b - 1) / b
                                boxMum = (sku.getQuantity() + packing.getPackingNum() - 1) / packing.getPackingNum();
                            } else {
                                //没有装箱数，则所需包材数量就是SKU数量
                                boxMum = sku.getQuantity();
                            }
                            salSedQuotationSkuPacking.setBoxMum(boxMum);
                            LambdaUpdateWrapper<SalSedQuotationSkuPacking> updateWrapper1 = Wrappers.lambdaUpdate(SalSedQuotationSkuPacking.class)
                                    .eq(SalSedQuotationSkuPacking::getId, packing.getId());
                            salSedQuotationSkuPackingMapper.update(salSedQuotationSkuPacking, updateWrapper1);
                            if (!packing.getAttachmentList().isEmpty()) {
                                proSedFileManager.batchSaveOrUpdate(BeanUtil.copyToList(packing.getAttachmentList(), ProSedFileVO.class), salSedQuotationSkuPacking.getId(), FileTypeEnum.quotationPackingFile);
                            }
                        }
                    }

                }
            }
        }
        //向历史记录表增加一条数据（谁干了嘛）
        SalSedQuotationHistory salSedQuotationHistory = new SalSedQuotationHistory();
        salSedQuotationHistory.setQuotationId(params.getId());
        salSedQuotationHistory.setContext(params.getOperation());
        salSedQuotationHistory.setCreateUser(params.getSalesmanId());
        salSedQuotationHistoryMapper.insert(salSedQuotationHistory);

    return  "修改成功";
    }










    /**
     * 导出报价单
     * @param params
     * @return
     */
    public void exportQuotation(HttpServletResponse response, SalSedQuotationExportParams params) throws UnsupportedEncodingException {

              salSedQuotationManager.exportQuotation(response, params);

    }






    /**
     * 提交审核
     * @param params
     * @return
     */
    public String submitAudit(SalSedQuotationOperateParams params) {
        if(params.getId()== null){
            throw new BizException(400,"请传入报价单id");
        }
        if(params.getSalesmanId()==null){
            throw new BizException(400,"请传入业务员id");
        }
        //判断id对应的报价单是否存在
        LambdaQueryWrapper<SalSedQuotation> queryWrapper = Wrappers.lambdaQuery(SalSedQuotation.class)
                .eq(SalSedQuotation::getId, params.getId())
                .eq(SalSedQuotation::getIsDeleted, 0);
                SalSedQuotation quotation = salSedQuotationMapper.selectOne(queryWrapper);
        if(quotation == null) {
             throw new BizException(400,"报价单不存在");
        }


        //修改报价单表数据，将状态改为财务未审核
        LambdaUpdateWrapper<SalSedQuotation> updateWrapper = Wrappers.lambdaUpdate(SalSedQuotation.class)
                .eq(SalSedQuotation::getId, params.getId())
                .eq(SalSedQuotation::getIsDeleted, 0)
                .set(SalSedQuotation::getStatus, QuotationStatusEnum.AUDIT_FINANCE_PASSED.getCode());
        salSedQuotationMapper.update(updateWrapper);

        //向历史记录表增加一条数据（谁干了嘛）
        SalSedQuotationHistory salSedQuotationHistory = new SalSedQuotationHistory();
        salSedQuotationHistory.setQuotationId(params.getId());
        salSedQuotationHistory.setContext(QuotationOperationEnum.commit_audit.getCode());
        salSedQuotationHistory.setCreateUser(params.getSalesmanId());
        salSedQuotationHistoryMapper.insert(salSedQuotationHistory);

        SalSedQuotationDetailVo detailForNotify = salSedQuotationMapper.getQuotationDetailById(params.getId());
        Integer tenantId = TenantInfoContext.getCurrentTenantId();
        sendQuotationAuditFeishuNotify(quotation, detailForNotify, tenantId);

        return "提交审核成功";
    }


    /**
     * 审核（仅保留驳回；通过须走 {@link #jointAudit(SalSedQuotationJointAuditParams)} 会签）
     */
    public String audit(SalSedQuotationAuditParams params) {
        if (QuotationStatusEnum.AUDIT_PASSED.getCode().equals(params.getAuditResult())) {
            throw new BizException(400, "审核通过请使用会签接口 jointAudit（财务与总裁均通过后方可完结）");
        }
        LambdaQueryWrapper<SalSedQuotation> queryWrapper = Wrappers.lambdaQuery(SalSedQuotation.class)
                .eq(SalSedQuotation::getId, params.getId())
                .eq(SalSedQuotation::getIsDeleted, 0);
        SalSedQuotation quotation = salSedQuotationMapper.selectOne(queryWrapper);
        if (quotation == null) {
            throw new BizException(400, "报价单不存在");
        }
        if (!QuotationStatusEnum.AUDIT_REJECTED.getCode().equals(params.getAuditResult())) {
            throw new BizException(400, "仅支持驳回（auditResult=-1），通过请使用 jointAudit");
        }
        assertCanJointReject(quotation.getId());
        if (params.getRejectReason() == null || params.getRejectReason().isBlank()) {
            throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(), "驳回原因不能为空");
        }

        // 驳回原因拼接驳回人角色和用户名
        Long userId = LoginUserInfoContext.getUserId();
        String rejectRoleName = "";
        if (userId != null) {
            List<SysRole> roles = sysRoleManager.getByUserId(userId);
            if (roles != null && !roles.isEmpty()) {
                for (SysRole role : roles) {
                    if (role.getId() != null
                            && (role.getId().equals(FINANCE_ROLE_ID) || role.getId().equals(PRESIDENT_ROLE_ID))) {
                        SysUser user = sysUserMapper.selectById(userId);
                        String userName = user != null && user.getNickName() != null ? user.getNickName() : "";
                        rejectRoleName = "[" + role.getName() + " " + userName + "] ";
                        break;
                    }
                }
            }
        }
        String fullRejectReason = rejectRoleName + params.getRejectReason();

        LambdaUpdateWrapper<SalSedQuotation> updateWrapper = Wrappers.lambdaUpdate(SalSedQuotation.class)
                .eq(SalSedQuotation::getId, params.getId())
                .set(SalSedQuotation::getRejectReason, fullRejectReason)
                .set(SalSedQuotation::getStatus, QuotationStatusEnum.AUDIT_REJECTED.getCode());
        salSedQuotationMapper.update(updateWrapper);

        SalSedQuotationHistory salSedQuotationHistory = new SalSedQuotationHistory();
        salSedQuotationHistory.setQuotationId(params.getId());
        salSedQuotationHistory.setContext(QuotationOperationEnum.reject_audit.getCode());
        salSedQuotationHistory.setCreateUser(params.getSalesmanId());
        salSedQuotationHistoryMapper.insert(salSedQuotationHistory);
        return "审核成功";
    }

    /**
     * 会签审核：财务通过 / 总裁通过 / 驳回（双签均通过后置为审核通过）
     */
    @Transactional(rollbackFor = Exception.class)
    public String jointAudit(SalSedQuotationJointAuditParams params) {
        if (params.getAction() == null || params.getAction().isBlank()) {
            throw new BizException(400, "您没有审核权限");
        }
        QuotationJointAuditActionEnum action = QuotationJointAuditActionEnum.fromCode(params.getAction());
        if (action == null) {
            throw new BizException(400, "无效的审核操作，应为 FINANCE_PASS / PRESIDENT_PASS");
        }
        Long userId = LoginUserInfoContext.getUserId();
        if (userId == null) {
            throw new BizException(400, "未登录");
        }
        List<Integer> roleIds = loadRoleIds(userId);
        boolean superAdmin = !roleIds.isEmpty() && sysPermissionManager.checkSuperPermission(roleIds);
        boolean finance = superAdmin || roleIds.contains(FINANCE_ROLE_ID);
        boolean president = superAdmin || roleIds.contains(PRESIDENT_ROLE_ID);

        LambdaQueryWrapper<SalSedQuotation> queryWrapper = Wrappers.lambdaQuery(SalSedQuotation.class)
                .eq(SalSedQuotation::getId, params.getId())
                .eq(SalSedQuotation::getIsDeleted, 0);
        SalSedQuotation quotation = salSedQuotationMapper.selectOne(queryWrapper);
        if (quotation == null) {
            throw new BizException(400, "报价单不存在");
        }
        String st = quotation.getStatus();
        String newStatus;
        QuotationOperationEnum historyOp;

        if (action == QuotationJointAuditActionEnum.FINANCE_PASS) {
            if (!finance) {
                throw new BizException(400, "您没有财务审核权限");
            }
            if (!isFinanceApprovableStatus(st)) {
                throw new BizException(400, "当前状态不可进行财务审核");
            }
            // 状态 5：总裁未审核，财务未审核 -> 总裁未审核，财务审核通过（状态7）
            // 状态 6：总裁审核通过，财务未审核 -> 审核通过（状态4）
            // 状态 8：总裁微信审核通过，财务未审核 -> 审核通过（状态4）
            if (QuotationStatusEnum.AUDIT_FINANCE_PASSED.getCode().equals(st)) {
                newStatus = QuotationStatusEnum.FINANCE_PASSED_PRESIDENT_PENDING.getCode();
            } else if (QuotationStatusEnum.PRESIDENT_PASSED_FINANCE_PENDING.getCode().equals(st)) {
                newStatus = QuotationStatusEnum.AUDIT_PASSED.getCode();
            } else if (QuotationStatusEnum.PRESIDENT_WX_PASSED.getCode().equals(st)) {
                newStatus = QuotationStatusEnum.AUDIT_PASSED.getCode();
            } else {
                throw new BizException(400, "当前状态不可进行财务审核");
            }
            historyOp = QuotationOperationEnum.finance_pass_audit;
        } else {
            if (!president) {
                throw new BizException(400, "您没有总裁审核权限");
            }
            if (!isPresidentApprovableStatus(st)) {
                throw new BizException(400, "当前状态不可进行总裁审核");
            }
            // 总裁审核：总裁未审核，财务未审核 -> 总裁审核通过，财务未审核；总裁未审核，财务审核通过 -> 审核通过
            if (QuotationStatusEnum.AUDIT_FINANCE_PASSED.getCode().equals(st)) {
                newStatus = QuotationStatusEnum.PRESIDENT_PASSED_FINANCE_PENDING.getCode();
            } else if (QuotationStatusEnum.FINANCE_PASSED_PRESIDENT_PENDING.getCode().equals(st)) {
                newStatus = QuotationStatusEnum.AUDIT_PASSED.getCode();
            } else {
                throw new BizException(400, "当前状态不可进行总裁审核");
            }
            historyOp = QuotationOperationEnum.president_pass_audit;
        }

        LambdaUpdateWrapper<SalSedQuotation> updateWrapper = Wrappers.lambdaUpdate(SalSedQuotation.class)
                .eq(SalSedQuotation::getId, params.getId())
                .set(SalSedQuotation::getStatus, newStatus);
        salSedQuotationMapper.update(updateWrapper);

        SalSedQuotationHistory stepHistory = new SalSedQuotationHistory();
        stepHistory.setQuotationId(params.getId());
        stepHistory.setContext(historyOp.getCode());
        stepHistory.setCreateUser(userId);
        salSedQuotationHistoryMapper.insert(stepHistory);

        if (QuotationStatusEnum.AUDIT_PASSED.getCode().equals(newStatus)) {
            SalSedQuotationHistory passHistory = new SalSedQuotationHistory();
            passHistory.setQuotationId(params.getId());
            passHistory.setContext(QuotationOperationEnum.pass_audit.getCode());
            passHistory.setCreateUser(userId);
            salSedQuotationHistoryMapper.insert(passHistory);
        }

        return "操作成功";
    }



    private List<Integer> loadRoleIds(Long userId) {
        List<SysRole> roles = sysRoleManager.getByUserId(userId);
        if (roles == null || roles.isEmpty()) {
            return Collections.emptyList();
        }
        return roles.stream().map(SysRole::getId).collect(Collectors.toList());
    }

    private void assertCanJointReject(Long quotationId) {
        LambdaQueryWrapper<SalSedQuotation> queryWrapper = Wrappers.lambdaQuery(SalSedQuotation.class)
                .eq(SalSedQuotation::getId, quotationId)
                .eq(SalSedQuotation::getIsDeleted, 0);
        SalSedQuotation quotation = salSedQuotationMapper.selectOne(queryWrapper);
        if (quotation == null) {
            throw new BizException(400, "报价单不存在");
        }
        String status = quotation.getStatus();
        if (!isJointRejectableStatus(status)) {
            throw new BizException(400, "当前状态不可驳回");
        }
        Long userId = LoginUserInfoContext.getUserId();
        if (userId == null) {
            throw new BizException(400, "未登录");
        }
        List<Integer> roleIds = loadRoleIds(userId);
        boolean superAdmin = !roleIds.isEmpty() && sysPermissionManager.checkSuperPermission(roleIds);
        boolean finance = superAdmin || roleIds.contains(FINANCE_ROLE_ID);
        boolean president = superAdmin || roleIds.contains(PRESIDENT_ROLE_ID);
        if (!finance && !president) {
            throw new BizException(400, "您没有审核权限");
        }
    }

    /** 构造驳回原因前缀，包含角色和名字 */
    private String buildRejectReasonInfo() {
        Long userId = LoginUserInfoContext.getUserId();
        String rolePrefix = "";
        String userName = "";
        if (userId != null) {
            List<Integer> roleIds = loadRoleIds(userId);
            if (roleIds.contains(FINANCE_ROLE_ID)) {
                rolePrefix = "[财务] ";
            } else if (roleIds.contains(PRESIDENT_ROLE_ID)) {
                rolePrefix = "[总裁] ";
            }
            SysUser user = sysUserMapper.selectById(userId);
            if (user != null && user.getNickName() != null) {
                userName = user.getNickName() + " ";
            }
        }
        return rolePrefix + userName;
    }

    private void enrichJointAuditUi(SalSedQuotationVo vo) {
        vo.setStatusLabel(getStatusLabel(vo.getStatus()));
        Long uid = LoginUserInfoContext.getUserId();
        if (uid == null) {
            vo.setCanFinanceApprove(false);
            vo.setCanPresidentApprove(false);
            return;
        }
        List<Integer> roleIds = loadRoleIds(uid);
        boolean superAdmin = !roleIds.isEmpty() && sysPermissionManager.checkSuperPermission(roleIds);
        boolean finance = superAdmin || roleIds.contains(FINANCE_ROLE_ID);
        boolean president = superAdmin || roleIds.contains(PRESIDENT_ROLE_ID);
        String st = vo.getStatus();
        vo.setCanFinanceApprove(finance && isFinanceApprovableStatus(st));
        vo.setCanPresidentApprove(president && isPresidentApprovableStatus(st));
    }

    private void enrichJointAuditUi(SalSedQuotationDetailVo vo) {
        vo.setStatusLabel(getStatusLabel(vo.getStatus()));
        Long uid = LoginUserInfoContext.getUserId();
        if (uid == null) {
            vo.setCanFinanceApprove(false);
            vo.setCanPresidentApprove(false);
            vo.setCanPresidentWxApprove(false);
            return;
        }
        List<Integer> roleIds = loadRoleIds(uid);
        boolean superAdmin = !roleIds.isEmpty() && sysPermissionManager.checkSuperPermission(roleIds);
        boolean finance = superAdmin || roleIds.contains(FINANCE_ROLE_ID);
        boolean president = superAdmin || roleIds.contains(PRESIDENT_ROLE_ID);
        // 总裁微信审核：仅销售角色可操作，且报价单处于已提交审核状态
        boolean sales = superAdmin || roleIds.contains(SALES_ROLE_ID);
        String st = vo.getStatus();
        vo.setCanFinanceApprove(finance && isFinanceApprovableStatus(st));
        vo.setCanPresidentApprove(president && isPresidentApprovableStatus(st));
        vo.setCanPresidentWxApprove(sales && isPresidentWxApprovableStatus(st));
    }

    /** 根据库表 status 值取展示文案 */
    private String getStatusLabel(String dbValue) {
        if (dbValue == null) {
            return "";
        }
        for (QuotationStatusEnum e : QuotationStatusEnum.values()) {
            if (e.getCode().equals(dbValue)) {
                return e.getCode();
            }
        }
        return dbValue;
    }

    /** 当前状态是否允许财务通过（总裁未审核，财务未审核 或 总裁审核通过，财务未审核 或 总裁微信审核通过时可操作） */
    private boolean isFinanceApprovableStatus(String st) {
        return QuotationStatusEnum.AUDIT_FINANCE_PASSED.getCode().equals(st)
                || QuotationStatusEnum.PRESIDENT_PASSED_FINANCE_PENDING.getCode().equals(st)
                || QuotationStatusEnum.PRESIDENT_WX_PASSED.getCode().equals(st);
    }

    /** 当前状态是否允许总裁通过（总裁未审核，财务未审核 或 总裁未审核，财务审核通过时可操作） */
    private boolean isPresidentApprovableStatus(String st) {
        return QuotationStatusEnum.AUDIT_FINANCE_PASSED.getCode().equals(st)
                || QuotationStatusEnum.FINANCE_PASSED_PRESIDENT_PENDING.getCode().equals(st);
    }

    /** 当前状态是否允许总裁微信审核通过（仅在已提交审核状态下可操作） */
    private boolean isPresidentWxApprovableStatus(String st) {
        return QuotationStatusEnum.AUDIT_FINANCE_PASSED.getCode().equals(st)
                || QuotationStatusEnum.FINANCE_PASSED_PRESIDENT_PENDING.getCode().equals(st);
    }

    /** 会签流程中是否允许驳回（总裁未审核，财务未审核 或 总裁审核通过，财务未审核 或 总裁未审核，财务审核通过 或 总裁微信审核通过） */
    private boolean isJointRejectableStatus(String st) {
        return QuotationStatusEnum.AUDIT_FINANCE_PASSED.getCode().equals(st)
                || QuotationStatusEnum.PRESIDENT_PASSED_FINANCE_PENDING.getCode().equals(st)
                || QuotationStatusEnum.FINANCE_PASSED_PRESIDENT_PENDING.getCode().equals(st)
                || QuotationStatusEnum.PRESIDENT_WX_PASSED.getCode().equals(st);
    }



    /**
     *  报价单详细、编辑内容、再次创建报价单
     * @param id
     * @return
     */
    public SalSedQuotationDetailVo quotationDetail(Long id) {
        //判断id对应报价单是否存在
        LambdaQueryWrapper<SalSedQuotation> queryWrapper = Wrappers.lambdaQuery(SalSedQuotation.class)
                .eq(SalSedQuotation::getId, id)
                .eq(SalSedQuotation::getIsDeleted, 0);
                SalSedQuotation quotation = salSedQuotationMapper.selectOne(queryWrapper);
                if(quotation == null) {
                    throw new BizException(400,"报价单不存在");
                }

        //根据报价单id查询报价单相关信息
        SalSedQuotationDetailVo quotationDetailVo = salSedQuotationManager.quotationDetail(id);

        //根据报价单id查询报价单中的SKU信息
        List<SalSedQuotationSkuVo> skuList = salSedQuotationSkuManager.getQuotationSkuList(id);

        // 毛利率 = 【订单金额 - sum(配件成本+工艺成本) - 采购成本 - 运输成本】÷ 订单金额 × 100%
        // 人民币：订单金额直接参与计算；美元：订单总金额×汇率=人民币订单金额，其余按人民币同样计算
        BigDecimal sum = BigDecimal.ZERO;
        for (SalSedQuotationSkuVo sku : skuList) {
            sum = sum.add(sku.getTotalCost() != null ? sku.getTotalCost() : BigDecimal.ZERO);
        }

        // 订单金额(人民币)：用于毛利率和物流占订单比例计算
        // 人民币(1)：直接用订单金额；美元(2)：订单金额 × 汇率
        BigDecimal orderAmountInRMB = quotationDetailVo.getOrderAmount();
        if (orderAmountInRMB == null) {
            orderAmountInRMB = BigDecimal.ZERO;
        }
        // currency: 1=人民币, 2=美元
        boolean isUsd = "2".equals(quotationDetailVo.getCurrency());
        if (isUsd && quotationDetailVo.getExchangeRate() != null && quotationDetailVo.getOrderAmount() != null) {
            orderAmountInRMB = quotationDetailVo.getOrderAmount().multiply(quotationDetailVo.getExchangeRate());
        }

        // 毛利率 = (订单金额 - 总成本) / 订单金额 * 100%
        // 订单金额需要大于0
        if (orderAmountInRMB.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal totalCost = sum.add(quotationDetailVo.getProcurementCost() != null ? quotationDetailVo.getProcurementCost() : BigDecimal.ZERO)
                                     .add(quotationDetailVo.getLogisticsCost() != null ? quotationDetailVo.getLogisticsCost() : BigDecimal.ZERO);
            BigDecimal grossProfitMargin = orderAmountInRMB.subtract(totalCost)
                    .divide(orderAmountInRMB, 5, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .setScale(4, RoundingMode.HALF_UP);
            quotationDetailVo.setGrossProfitMargin(grossProfitMargin);
        }

        // 物流占订单比例 = 物流成本 ÷ 订单金额(人民币) × 100%
        // 人民币：直接用订单金额；美元：订单金额 × 汇率
        if (quotationDetailVo.getLogisticsCost() != null && orderAmountInRMB.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal logisticsProportion = quotationDetailVo.getLogisticsCost().divide(orderAmountInRMB, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
            quotationDetailVo.setLogisticsProportion(logisticsProportion);
        }


        //根据报价单id查询历史记录信息
        List<SalSedQuotationHistoryVo> historyList= salSedQuotationHistoryMapper.getQuotationHistoryList(id);

        //封装数据
        quotationDetailVo.setSkuList(skuList);
        quotationDetailVo.setHistoryList(historyList);

        //设置每个SKU的转订单状态
        for (SalSedQuotationSkuVo skuVo : skuList) {
            //查询订单详情表，看该SKU是否已转订单
            Long count = salSedOrderDetailMapper.countByQuotationSkuId(skuVo.getQuotationSkuId());
            skuVo.setShiftStatus(count != null && count > 0 ? "1" : "0");
        }

        enrichJointAuditUi(quotationDetailVo);
        return quotationDetailVo;
    }

    /**
     * 根据业务员id查询客户的信息
     * @param id
     * @return
     */
    public List<SalSedCustomerVo> getUserInfo(Long id) {
        //判断id对应的业务员是否存在
        LambdaQueryWrapper<SysUser> queryWrapper = Wrappers.lambdaQuery(SysUser.class)
                .eq(SysUser::getUserId, id)
                .eq(SysUser::getIsDeleted, 0);
        SysUser sysUser = sysUserMapper.selectOne(queryWrapper);
        if(sysUser == null) {
            throw new BizException(400,"当前业务员不存在");
        }

        //根据业务员id查询客户信息
        LambdaQueryWrapper<SalYtCustomer> queryWrapper1 = Wrappers.lambdaQuery(SalYtCustomer.class)
                .select(SalYtCustomer::getId, SalYtCustomer::getName)
                .eq(SalYtCustomer::getBelongEmployeeId, id)
                .eq(SalYtCustomer::getIsDeleted, 0);
        List<SalYtCustomer> list = salYtCustomerMapper.selectList(queryWrapper1);
        List<SalSedCustomerVo> customerlist = new ArrayList<>();

        //封装成SalSedCustomerVo
        for (SalYtCustomer customer : list){
            SalSedCustomerVo customerVo = new SalSedCustomerVo();
            customerVo.setId(customer.getId());
            customerVo.setName(customer.getName());
            customerlist.add(customerVo);
        }
        return customerlist;
    }

    /**
     * 搜索历史报价单
     * @param params
     * @return
     */
    public PageResultInfo<SalSedQuotationHistoryImportVo> getHistoryQuotation(SalSedQuotationHistoryImportParams params) {
        PageHelper.startPage(params.getPageNum(), params.getPageSize());

        //根据订单编号、客户名称、创建时间进行搜索
       List<SalSedQuotationHistoryImportVo> list =  salSedQuotationMapper.getHistoryQuotation(params);


        return new PageResultInfo<>(list);
    }


    /**
     * 根据客户id查询客户收货地址信息
     * @param id
     * @return
     */

    public List<SalSedCustomerAddressVo> getCustomerAddress(Long id) {
        //根据客户id查询客户收货地址信息
        LambdaQueryWrapper<SalYtCustomerAddress> queryWrapper = Wrappers.lambdaQuery(SalYtCustomerAddress.class)
                .eq(SalYtCustomerAddress::getCustomerId, id)
                .eq(SalYtCustomerAddress::getIsDeleted, 0);
        List<SalYtCustomerAddress> CustomerAddressList = salYtCustomerAddressMapper.selectList(queryWrapper);

        List<SalSedCustomerAddressVo> customerAddressVoList = new ArrayList<>();
        for (SalYtCustomerAddress customerAddress : CustomerAddressList){
            SalSedCustomerAddressVo customerAddressVo = new SalSedCustomerAddressVo();
            BeanUtils.copyProperties(customerAddress, customerAddressVo);
            customerAddressVoList.add(customerAddressVo);
        }

        return customerAddressVoList;
    }

    /**
     * 根据报价单id返回更加详细的历史报价单信息
     * @param ids
     * @return
     */
    public List<SalSedQuotationSkuVo> getHistoryQuotationDetail(List<Long> ids) {

            //根据报价单ids查询历史报价单中的SKU信息
            List<SalSedQuotationSkuVo> skuList = salSedQuotationSkuMapper.getHistoryQuotationSkuList(ids);
           //循环查询得到sku图片地址，然后再赋值
            for (SalSedQuotationSkuVo quotationSku : skuList) {
                //根据报价单id查询客户id
                Long customerId = salSedQuotationMapper.selectById(quotationSku.getQuotationId()).getCustomerId();
                quotationSku.setCustomerId(customerId);
              //调用图片地址查询方法
              List<ProSedFile> skuImageList = proSedFileManager.selectSkuImage(quotationSku.getSkuId());
                for(ProSedFile file : skuImageList){
                    if(quotationSku.getPic()==null){
                        quotationSku.setPic(new ArrayList<>());
                    }
                    quotationSku.getPic().add(file.getUrl());
                }

            }
        List<SalSedQuotationSkuVo> newSkuList;
            //选择历史报价单时，只选择一个历史报价单
            if(ids.size()==1){
                newSkuList=skuList;
            }else {
                List<SalSedQuotationSkuVo> newSkuList1 = getHistoryQuotation(skuList);
                return newSkuList1;
            }

        return newSkuList;
    }


    public List<SalSedQuotationSkuVo> getHistoryQuotation(List<SalSedQuotationSkuVo> skuList) {
        //判断历史报价单中是否有相同的SKU，判断skuId是否相等
        //得到处理后的历史报价单sku信息
        List<SalSedQuotationSkuVo>  newSkuList = removeDuplicateBySkuIdAndSetPrice(skuList);
        List<SalSedQuotationSkuVo> newSkuList1 = new ArrayList<>();
        //包材信息等设置为空
        for (SalSedQuotationSkuVo quotationSku : newSkuList){
            //根据产品id查询推荐包装
            List<SalSedQuotationSkuVo> list =proSedPackingMapper.getProductPackingListByProductId(quotationSku.getProductId());
            for(SalSedQuotationSkuVo vo : list){
                SalSedQuotationSkuVo quotationSkuVo = new SalSedQuotationSkuVo();
                //将quotationSku的值复制给quotationSkuVo,a->b
                BeanUtils.copyProperties(quotationSku,quotationSkuVo);
                quotationSkuVo.setPackageId(vo.getPackageId());
                quotationSkuVo.setPackageName(vo.getPackageName());
                if(vo.getPackageSize()!=null) {
                    quotationSkuVo.setPackageSize(vo.getPackageSize());
                }
                quotationSkuVo.setPackingNumber(vo.getPackingNumber());
                newSkuList1.add(quotationSkuVo);
            }
        }
        return newSkuList1;
    }



    public  List<SalSedQuotationSkuVo> removeDuplicateBySkuIdAndSetPrice(List<SalSedQuotationSkuVo> skuList) {
        // 1. 判空处理
        if (skuList == null || skuList.isEmpty()) {
            return new ArrayList<>();
        }
        // 2. Key：skuId；Value：保留的SKU对象（仅存储首次出现的元素）
        Map<Long, SalSedQuotationSkuVo> skuMap = new HashMap<>();

        // 3. 遍历原始列表，手动去重并赋值price
        for (SalSedQuotationSkuVo skuVo : skuList) {
            // 跳过null元素或skuId为null的元素
            if (skuVo == null || skuVo.getSkuId() == null) {
                continue;
            }
            Long skuId = skuVo.getSkuId();
            // 仅当skuId不存在时，才存入Map（避免覆盖，保留首次出现的SKU）
            if (!skuMap.containsKey(skuId)) {
                //数量设置为空
                skuVo.setNumber(null);
                //报价为产品中设置的售价
                  //根据skuId查询sku的基础售价
                BigDecimal price = proSedProductMatchSkuMapper.selectById(skuId).getBasicPrice();
                skuVo.setPrice(price);
                skuMap.put(skuId, skuVo);
            }
        }

        // 4. 将HashMap的值转换为List并返回
        return new ArrayList<>(skuMap.values());
    }


    /**
     * 历史报价信息
     * @param params
     * @return
     */
    public SalSedHistoryQuotationInfoVo getHistoryQuotationInfo(SalSedHistoryQuotationInfoParams params) {

        return  salSedQuotationManager.getHistoryQuotationInfo(params);
    }

    /**
     * 成本明细
     * @param params
     * @return
     */
    public SalSedQuotationCostDetailShiftVo getCostDetail(SalSedQuotationCostDetailParams params) {
      return   salSedQuotationManager.getCostDetail(params);
    }

    /**
     * 一键转订单
     * @param params
     * @return
     */
    public String oneKeyToOrder(SalSedQuotationOneKeyToOrderParams params) {
        //判断该报价单是否存在
        SalSedQuotation quotation = salSedQuotationMapper.selectById(params.getQuotationId());
        if(quotation==null){
            throw new BizException(ExceptionCodeEnum.Not_Exists);
        }
        //判断该报价单是否被转为订单
          //根据报价单id查询订单
        //判断该报价单是否已转为订单（通过 shiftStatus 字段判断）
        if("1".equals(quotation.getShiftStatus())){
            throw new BizException(400,"该报价单已转为订单，请勿重复转换");
        }
        //将报价单转换状态修改为已转换 0=没转换，1=已转换
        SalSedQuotation quotation1 = new SalSedQuotation();
        quotation1.setShiftStatus("1");
        LambdaUpdateWrapper<SalSedQuotation> updateWrapper = Wrappers.lambdaUpdate(SalSedQuotation.class)
                .eq(SalSedQuotation::getId,params.getQuotationId());
                salSedQuotationMapper.update(quotation1,updateWrapper);
        return salSedQuotationManager.oneKeyToOrder(params);
    }

    /**
     * 单个 SKU 转订单
     * @param params
     * @return
     */
    public String skuToOrder(SalSedQuotationSkuToOrderParams params) {
        //判断该报价单是否存在
        SalSedQuotation quotation = salSedQuotationMapper.selectById(params.getQuotationId());
        if(quotation==null){
            throw new BizException(ExceptionCodeEnum.Not_Exists);
        }
        //判断报价单 SKU 是否存在
        SalSedQuotationSku quotationSku = salSedQuotationSkuMapper.selectById(params.getQuotationSkuId());
        if(quotationSku==null){
            throw new BizException(ExceptionCodeEnum.Not_Exists);
        }
        //判断报价单状态，只有通过审核的报价单才能转订单
        if(!quotation.getStatus().equals("4")){
            throw new BizException(400,"只有通过审核的报价单才能转订单");
        }
        //判断该报价单是否已通过一键转订单转换（如果已一键转订单，则不能再单个 SKU 转订单）
        if("1".equals(quotation.getShiftStatus())){
            throw new BizException(400,"该报价单已通过一键转订单转换，请勿重复转换");
        }
        //判断当前 SKU 是否已经转过订单（通过订单明细表查询）
        Long existingCount = salSedOrderDetailMapper.selectCount(
            Wrappers.lambdaQuery(SalSedOrderDetail.class)
                .eq(SalSedOrderDetail::getQuotationSkuId, params.getQuotationSkuId())
                .eq(SalSedOrderDetail::getIsDeleted, 0)
        );
        if(existingCount > 0) {
            throw new BizException(400,"该 SKU 已转过订单，请勿重复转换");
        }
        return salSedQuotationManager.skuToOrder(params);
    }

    /**
     * 合并转订单-请选择产品：仅返回审核通过的报价单，支持报价单编号、SKU名称、搭配名称筛选
     * @param params 筛选参数
     * @return 报价单列表（含其下 SKU 列表）
     */
    public PageResultInfo<SalSedQuotationMergeItemVo> listForMerge(SalSedQuotationMergeListParams params) {
        if (params == null) {
            params = new SalSedQuotationMergeListParams();
        }
        PageHelper.startPage(params.getPageNum(), params.getPageSize());
        List<SalSedQuotationMergeItemVo> list = salSedQuotationManager.listForMerge(params);
        return new PageResultInfo<>(list);
    }

    /**
     * 合并转订单-获取报价单下的SKU列表（用于展开行时加载）
     * @param params 报价单ID及筛选条件
     * @return SKU列表
     */
    public List<SalSedQuotationMergeSkuItemVo> getMergeSkuList(SalSedQuotationMergeSkuListParams params) {
        return salSedQuotationManager.getMergeSkuList(params.getQuotationId(), params.getSkuName(), params.getMatchName());
    }

    /**
     * 合并转订单：将选中的多个报价单 SKU 合并生成一个订单（业务员与客户须一致，且不能选重复 SKU）
     * @param params 选中的 quotationSkuIds、订单来源、交货日期等
     * @return 成功提示
     */
    @Transactional(rollbackFor = Exception.class)
    public String mergeToOrder(SalSedQuotationMergeToOrderParams params) {
        return salSedQuotationManager.mergeToOrder(params);
    }

    /**
     * 获取币种列表
     * @return 币种列表
     */
    public List<DictionaryOptionVO> getCurrencyList() {
        List<SysDictionary> dictionaryList = sysDictionaryManager.getByCode(DictionaryConfigEnum.currency.getKey());
        return dictionaryList.stream()
                .map(dic -> {
                    DictionaryOptionVO option = new DictionaryOptionVO();
                    option.setLabel(dic.getValue());
                    option.setValue(dic.getKey());
                    return option;
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取是否含税列表
     * @return 是否含税列表
     */
    public List<DictionaryOptionVO> getTaxList() {
        List<SysDictionary> dictionaryList = sysDictionaryManager.getByCode(DictionaryConfigEnum.tax.getKey());
        return dictionaryList.stream()
                .map(dic -> {
                    DictionaryOptionVO option = new DictionaryOptionVO();
                    option.setLabel(dic.getValue());
                    option.setValue(dic.getKey());
                    return option;
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取 FOB 列表
     * @return FOB 列表
     */
    public List<String> getFobList() {
        List<SysDictionary> dictionaryList = sysDictionaryManager.getByCode(DictionaryConfigEnum.fob.getKey());
        return dictionaryList.stream()
                .map(SysDictionary::getValue)
                .collect(Collectors.toList());
    }

    /**
     * 获取 EXW 列表
     * @return EXW 列表
     */
    public List<String> getExwList() {
        List<SysDictionary> dictionaryList = sysDictionaryManager.getByCode(DictionaryConfigEnum.exw.getKey());
        return dictionaryList.stream()
                .map(SysDictionary::getValue)
                .collect(Collectors.toList());
    }

    /**
     * 根据币种判断需要显示的字段
     * @param currency 币种
     * @return 需要显示的字段列表
     */
    public Map<String, Boolean> getDisplayFieldsByCurrency(String currency) {
        return salSedQuotationManager.getDisplayFieldsByCurrency(currency);
    }

    // ==================== 私有方法：飞书通知 ====================

    /**
     * 报价单提交审核后，通过飞书机器人分别向财务和总裁账号发 IM 通知（失败仅打日志，不影响主流程）。
     */
    private void sendQuotationAuditFeishuNotify(SalSedQuotation quotation, SalSedQuotationDetailVo detail, Integer tenantId) {
        try {
            Map<String, String> financeOpenIds = loadAuditRoleUserOpenIds(tenantId, FINANCE_ROLE_ID, "财务");
            Map<String, String> presidentOpenIds = loadAuditRoleUserOpenIds(tenantId, PRESIDENT_ROLE_ID, "总裁");
            sendToFinanceAndPresident(quotation, detail, tenantId, financeOpenIds, "财务");
            sendToFinanceAndPresident(quotation, detail, tenantId, presidentOpenIds, "总裁");
        } catch (Exception e) {
            log.warn("报价单提交审核飞书通知发送失败, quotationId={}", quotation != null ? quotation.getId() : null, e);
        }
    }

    /**
     * 查询拥有指定角色 ID 的所有用户飞书 open_id（去重、过滤空值）。
     * @param tenantId     租户 ID
     * @param roleId       角色主键
     * @param roleName     角色名称（仅用于日志）
     */
    private Map<String, String> loadAuditRoleUserOpenIds(Integer tenantId, int roleId, String roleName) {
        Map<String, String> openIds = new java.util.HashMap<>();
        SysTenant sysTenant = sysTenantMapper.selectById(tenantId);
        if (sysTenant == null || !AccountSystemEnum.FeiShu.getKey().equals(sysTenant.getAccountSystemKey())) {
            return openIds;
        }
        List<SysUser> users = sysUserMapper.selectByRoleId(roleId);
        for (SysUser user : users) {
            String openId = user.getFeiShuUserId();
            if (openId != null && !openId.isBlank()) {
                openIds.put(openId, user.getUserName() != null ? user.getUserName() : openId);
            }
        }
        return openIds;
    }

    /**
     * 向指定角色的所有飞书账号发送通知。
     * @param quotation   报价单实体
     * @param detail      报价单详情 VO
     * @param tenantId    租户 ID
     * @param openIdMap   open_id → 用户名（用于日志）
     * @param roleTag     角色标识：财务 / 总裁
     */
    private void sendToFinanceAndPresident(SalSedQuotation quotation, SalSedQuotationDetailVo detail,
                                           Integer tenantId, Map<String, String> openIdMap, String roleTag) {
        if (openIdMap == null || openIdMap.isEmpty()) {
            log.info("报价单[quotationId={}]提交审核，{}角色下无可发送通知的飞书账号，跳过", quotation != null ? quotation.getId() : null, roleTag);
            return;
        }
        String cardContent = buildQuotationAuditFeishuCard(quotation, detail, roleTag);
        for (Map.Entry<String, String> entry : openIdMap.entrySet()) {
            sendFeishuCardToOpenId(entry.getKey(), cardContent, tenantId);
        }
    }

    /**
     * 向指定飞书 open_id 发送交互卡片消息（通过 tenantId 从租户配置读取凭证）。
     */
    private void sendFeishuCardToOpenId(String openId, String cardContent, Integer tenantId) {
        if (openId == null || openId.isEmpty() || cardContent == null || cardContent.isEmpty() || tenantId == null) {
            return;
        }
        SysTenant sysTenant = sysTenantMapper.selectById(tenantId);
        if (sysTenant == null || !AccountSystemEnum.FeiShu.getKey().equals(sysTenant.getAccountSystemKey())) {
            return;
        }
        SysTenantConfig appIdConfig = sysTenantConfigMapper.getByTenantIdAndConfigName(tenantId, TenantConfigEnum.FeiShuAppId.getKey());
        SysTenantConfig appSecretConfig = sysTenantConfigMapper.getByTenantIdAndConfigName(tenantId, TenantConfigEnum.FeiShuAppSecret.getKey());
        if (appIdConfig == null || appSecretConfig == null) {
            return;
        }
        FeiShuUtil.sendCardMessage(appIdConfig.getConfigValue(), appSecretConfig.getConfigValue(), openId, cardContent);
    }

    /**
     * 构建报价单审核飞书交互卡片（用 fastjson2 API 构造，确保输出合法 JSON）。
     * 卡片顶部展示报价单详情，底部带「立即审核」按钮，点击跳转至审核页。
     */
    private String buildQuotationAuditFeishuCard(SalSedQuotation quotation, SalSedQuotationDetailVo detail, String roleTag) {
        String code = quotation != null && quotation.getQuotationCode() != null && !quotation.getQuotationCode().isEmpty()
                ? quotation.getQuotationCode() : "-";
        String customer = detail != null && detail.getCustomerName() != null && !detail.getCustomerName().isEmpty()
                ? detail.getCustomerName() : "-";
        String salesman = detail != null && detail.getSalesmanName() != null && !detail.getSalesmanName().isEmpty()
                ? detail.getSalesmanName() : "-";
        String orderAmountLine = formatOrderAmountForFeishu(quotation);
        String submitTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String remark = quotation != null && quotation.getSpecialRequirements() != null && !quotation.getSpecialRequirements().isEmpty()
                ? quotation.getSpecialRequirements() : "无";
        String greeting = "财务".equals(roleTag) ? "财务，您好！" : "老板，您好！";
        String auditUrl = buildFeishuCardAuditUrl(quotation, roleTag);

        // header
        JSONObject headerTitle = new JSONObject();
        headerTitle.put("tag", "plain_text");
        headerTitle.put("content", "\uD83D\uDCCB 报价单审核提醒");

        JSONObject header = new JSONObject();
        header.put("title", headerTitle);
        header.put("template", "blue");

        // markdown 内容（使用 Unicode 转义避免 emoji 解析问题）
        StringBuilder mdContent = new StringBuilder();
        mdContent.append(greeting).append("\n\n");
        mdContent.append("**【报价单详情】**\n\n");
        mdContent.append("> \uD83D\uDCCB 报价单编号：").append(escapeForMarkdown(code)).append("\n");
        mdContent.append("> \uD83D\uDC64\uFE0F 客户名称：").append(escapeForMarkdown(customer)).append("\n");
        mdContent.append("> \uD83D\uDCB0 订单金额：").append(escapeForMarkdown(orderAmountLine)).append("\n");
        mdContent.append("> \uD83D\uDC68\u200D\uD83D\uDCBC 提交人：").append(escapeForMarkdown(salesman)).append("\n");
        mdContent.append("> \uD83D\uDD50 提交时间：").append(escapeForMarkdown(submitTime)).append("\n");
        mdContent.append("> \uD83D\uDCDD 备注：").append(escapeForMarkdown(remark));

        JSONObject markdown = new JSONObject();
        markdown.put("tag", "markdown");
        markdown.put("content", mdContent.toString());

        // 分隔线
        JSONObject hr = new JSONObject();
        hr.put("tag", "hr");

        // 按钮
        JSONObject buttonText = new JSONObject();
        buttonText.put("tag", "plain_text");
        buttonText.put("content", "立即审核 \u2705");

        JSONObject button = new JSONObject();
        button.put("tag", "button");
        button.put("text", buttonText);
        button.put("type", "primary");
        button.put("url", auditUrl);

        JSONObject action = new JSONObject();
        action.put("tag", "action");
        JSONArray actions = new JSONArray();
        actions.add(button);
        action.put("actions", actions);

        // 底部备注
        JSONObject noteText = new JSONObject();
        noteText.put("tag", "plain_text");
        noteText.put("content", "\u2014\u2014 盛尔达自动化通知");

        JSONObject note = new JSONObject();
        note.put("tag", "note");
        JSONArray noteElements = new JSONArray();
        noteElements.add(noteText);
        note.put("elements", noteElements);

        // 拼装卡片根对象
        JSONArray elements = new JSONArray();
        elements.add(markdown);
        elements.add(hr);
        elements.add(action);
        elements.add(note);

        JSONObject card = new JSONObject();
        card.put("header", header);
        card.put("elements", elements);

        return card.toJSONString();
    }

    /**
     * 将字符串中的特殊字符转义，防止破坏 Markdown 内容结构。
     */
    private static String escapeForMarkdown(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                 .replace("*", "\\*")
                 .replace("_", "\\_")
                 .replace("`", "\\`")
                 .replace("\n", " ")
                 .replace("\r", "");
    }

    /**
     * 飞书卡片审核跳转链接，根据角色返回对应的审核页面链接。
     * @param quotation 报价单实体
     * @param roleTag  角色标识：财务 / 总裁
     * @return 对应角色的审核页面 URL
     */
    private String buildFeishuCardAuditUrl(SalSedQuotation quotation, String roleTag) {
        if ("财务".equals(roleTag)) {
            return feishuFinanceAuditUrl;
        } else if ("总裁".equals(roleTag)) {
            return feishuPresidentAuditUrl;
        }
        return "";
    }

    private static String formatOrderAmountForFeishu(SalSedQuotation q) {
        if (q == null || q.getOrderAmount() == null) {
            return "-";
        }
        BigDecimal amt = q.getOrderAmount().stripTrailingZeros();
        String num = amt.toPlainString();
        if ("2".equals(q.getCurrency())) {
            return num + " 美元";
        }
        return num + "元";
    }

    /**
     * 总裁微信审核通过（仅销售角色可操作，同时上传审核凭证图片）
     */
    public String presidentWxAudit(SalSedQuotationOperateParams params) {
        if (params.getId() == null) {
            throw new BizException(400, "请传入报价单id");
        }
        if (params.getImageList() == null || params.getImageList().isEmpty()) {
            throw new BizException(400, "请上传审核凭证图片");
        }
        Long userId = LoginUserInfoContext.getUserId();
        if (userId == null) {
            throw new BizException(400, "未登录");
        }
        List<Integer> roleIds = loadRoleIds(userId);
        boolean superAdmin = !roleIds.isEmpty() && sysPermissionManager.checkSuperPermission(roleIds);
        boolean sales = superAdmin || roleIds.contains(SALES_ROLE_ID);
        if (!sales) {
            throw new BizException(400, "您没有审核权限");
        }
        LambdaQueryWrapper<SalSedQuotation> queryWrapper = Wrappers.lambdaQuery(SalSedQuotation.class)
                .eq(SalSedQuotation::getId, params.getId())
                .eq(SalSedQuotation::getIsDeleted, 0);
        SalSedQuotation quotation = salSedQuotationMapper.selectOne(queryWrapper);
        if (quotation == null) {
            throw new BizException(400, "报价单不存在");
        }
        String st = quotation.getStatus();
        if (!isPresidentWxApprovableStatus(st)) {
            throw new BizException(400, "当前状态不可进行总裁微信审核");
        }
        // 保存总裁微信审核凭证图片
        proSedFileManager.batchSaveOrUpdate(params.getImageList(), params.getId(), FileTypeEnum.presidentWxAuditImage);
        // 修改状态为总裁微信审核通过
        LambdaUpdateWrapper<SalSedQuotation> updateWrapper = Wrappers.lambdaUpdate(SalSedQuotation.class)
                .eq(SalSedQuotation::getId, params.getId())
                .set(SalSedQuotation::getStatus, QuotationStatusEnum.PRESIDENT_WX_PASSED.getCode());
        salSedQuotationMapper.update(updateWrapper);

        // 记录历史
        SalSedQuotationHistory history = new SalSedQuotationHistory();
        history.setQuotationId(params.getId());
        history.setContext(QuotationOperationEnum.president_wx_pass_audit.getCode());
        history.setCreateUser(params.getSalesmanId());
        salSedQuotationHistoryMapper.insert(history);
        return "总裁微信审核通过";
    }
}
