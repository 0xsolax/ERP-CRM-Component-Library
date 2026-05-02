package com.qiaomoyun.controller.sal.sed;

import com.qiaomoyun.annotation.RequiresDataPermissions;
import com.qiaomoyun.annotation.RequiresPermissions;
import com.qiaomoyun.annotation.RequiresPermissionsDesc;
import com.qiaomoyun.info.PageResultInfo;
import com.qiaomoyun.info.ResultInfo;
import com.qiaomoyun.param.sal.sed.*;
import com.qiaomoyun.service.sal.sed.SalSedQuotationService;
import com.qiaomoyun.vo.sal.sed.*;
import com.qiaomoyun.vo.sys.DictionaryOptionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * 报价单管理
 */
@RestController
@RequestMapping("api/sal/sed/quotation")
@Validated
@Tag(name = "报价单管理",description = "报价单管理相关功能")
public class SalSedQuotationController {


    @Resource
      private SalSedQuotationService salSedQuotationService;

     /**
     * 获取报价单列表
     */
     @PostMapping("/list")
     @RequiresPermissions("sal:sed:quotation:quotation:list")
     @RequiresPermissionsDesc(menu = {"销售管理", "报价单管理"}, button = "获取报价单信息")
     @Operation(summary = "获取报价单列表信息")
     @RequiresDataPermissions("开启数据权限")
     public ResultInfo<PageResultInfo<SalSedQuotationVo>> list(@RequestBody SalSedQuotationParams params) {
         return ResultInfo.success(salSedQuotationService.list(params));
     }

     /**
     * 根据报价单id获取报价单采购成本详情
     */
     @GetMapping("/procurementDetail")
     @RequiresPermissions("sal:sed:quotation:procurementDetail")
     @RequiresPermissionsDesc(menu = {"销售管理", "报价单管理"}, button = "获取报价单采购成本详情")
      @Operation(summary = "获取报价单采购成本详情")
      public ResultInfo<SalSedQuotationVo> procurementDetail(@RequestParam Long  id) {
         return ResultInfo.success(salSedQuotationService.procurementDetail(id));
     }



    /**
     * 采购成本确认
     */
    @PostMapping("/procurementConfirm")
    @RequiresPermissions("sal:sed:quotation:procurementConfirm")
    @RequiresPermissionsDesc(menu = {"销售管理", "报价单管理"}, button = "采购成本确认")
    @Operation(summary = "采购成本确认")
    public ResultInfo<String> procurementConfirm(@RequestBody SalSedQuotationOperateParams params) {
        return ResultInfo.success(salSedQuotationService.procurementConfirm(params));
    }




     /**
     * 获取报价单物流成本详情
     */
     @GetMapping("/logisticsDetail")
     @RequiresPermissions("sal:sed:quotation:logisticsDetail")
     @RequiresPermissionsDesc(menu = {"销售管理", "报价单管理"}, button = "获取报价单物流成本详情")
     @Operation(summary = "获取报价单物流成本详情")
      public ResultInfo<SalSedQuotationLogisticsVo> logisticsDetail(@RequestParam Long  id) {
         return ResultInfo.success(salSedQuotationService.logisticsDetail(id));
     }


    /**
     * 物流成本确认
     * @param params
     * @return
     */
    @PostMapping("/logisticsConfirm")
    @RequiresPermissions("sal:sed:quotation:logisticsConfirm")
    @RequiresPermissionsDesc(menu = {"销售管理", "报价单管理"}, button = "物流成本确认")
    @Operation(summary = "物流成本确认")
    public ResultInfo<String> logisticsConfirm(@RequestBody SalSedQuotationLogisticsParams params) {
         return ResultInfo.success(salSedQuotationService.logisticsConfirm(params));
    }


    /**
     * 新增报价单、编辑修改和再次创建报价单（是同一种接口，传id就是修改，没有就是新增）
     * @param params
     * @return
     */
    @PostMapping("/saveOrUpdate")
    @RequiresPermissions("sal:sed:quotation:saveOrUpdate")
    @RequiresPermissionsDesc(menu = {"销售管理", "报价单管理"}, button = "新增报价单、再次创建报价单")
    @Operation(summary = "新增报价单、编辑修改和再次创建报价单")
    public ResultInfo<String> saveOrUpdate(@RequestBody @Validated SalSedQuotationSaveOrUpdateParams params) {
        return ResultInfo.success(salSedQuotationService.saveOrUpdate(params));
    }



    /**
     * 根据业务员id查询客户的信息
     * @param id
     * @return
     */
    @GetMapping("/getUserInfo")
    @RequiresPermissions("sal:sed:quotation:getUserInfo")
    @RequiresPermissionsDesc(menu = {"销售管理", "报价单管理"}, button = "根据业务员id查询用户信息")
    @Operation(summary = "根据业务员id查询用户信息")
    public ResultInfo<List<SalSedCustomerVo>> getUserInfo(@RequestParam Long id) {

        return ResultInfo.success(salSedQuotationService.getUserInfo(id));
    }



    /**
     * 根据客户id查询客户收货地址信息
     */
    @GetMapping("/getCustomerAddress")
    @RequiresPermissions("sal:sed:quotation:getCustomerAddress")
    @RequiresPermissionsDesc(menu = {"销售管理", "报价单管理"}, button = "根据客户id查询客户收货地址信息")
    @Operation(summary = "根据客户id查询客户收货地址信息")
    public ResultInfo<List<SalSedCustomerAddressVo>> getCustomerAddress(@RequestParam Long id) {
        return ResultInfo.success(salSedQuotationService.getCustomerAddress(id));
    }







    //导出报价单（飞书第七次调研四.2的exsel表格）
    /**
     * 导出报价单
     */
    @PostMapping("/exportQuotation")
    @RequiresPermissions("sal:sed:quotation:exportQuotation")
    @RequiresPermissionsDesc(menu = {"销售管理", "报价单管理"}, button = "导出报价单")
    @Operation(summary = "导出报价单")
    public void exportQuotation(HttpServletResponse response, @RequestBody @Validated SalSedQuotationExportParams params) throws UnsupportedEncodingException {
        salSedQuotationService.exportQuotation(response,params);
    }



    /**
     * 提交审核
     * @param  params
     * @return
     */
    @PostMapping("/submitAudit")
    @RequiresPermissions("sal:sed:quotation:submitAudit")
    @RequiresPermissionsDesc(menu = {"销售管理", "报价单管理"}, button = "提交审核")
    @Operation(summary = "提交审核")
    public ResultInfo<String> submitAudit(@RequestBody SalSedQuotationOperateParams params) {
        return ResultInfo.success(salSedQuotationService.submitAudit(params));
    }





    /**
     * 审核（驳回/通过）
     */
    @PostMapping("/audit")
    @RequiresPermissions("sal:sed:quotation:audit")
    @RequiresPermissionsDesc(menu = {"销售管理", "报价单管理"}, button = "审核")
    @Operation(summary = "审核（仅驳回；通过请使用 jointAudit 会签）")
    public ResultInfo<String> audit(@RequestBody @Validated SalSedQuotationAuditParams params) {
        return ResultInfo.success(salSedQuotationService.audit(params));

    }

    /**
     * 会签审核：财务通过 / 总裁通过 / 驳回
     */
    @PostMapping("/jointAudit")
    @RequiresPermissions("sal:sed:quotation:audit")
    @RequiresPermissionsDesc(menu = {"销售管理", "报价单管理"}, button = "会签审核")
    @Operation(summary = "会签审核（财务/总裁）")
    public ResultInfo<String> jointAudit(@RequestBody @Validated SalSedQuotationJointAuditParams params) {
        return ResultInfo.success(salSedQuotationService.jointAudit(params));
    }




    /**
     * 报价单详细、编辑内容、再次创建报价单数据的返回接口
     * @param id
     * @return
     */
    @GetMapping("/quotationDetail")
    @RequiresPermissions("sal:sed:quotation:quotationDetail")
    @RequiresPermissionsDesc(menu = {"销售管理", "报价单管理"}, button = "报价单详细")
    @Operation(summary = "报价单详细")
    public ResultInfo<SalSedQuotationDetailVo> quotationDetail(@RequestParam Long  id) {
        return ResultInfo.success(salSedQuotationService.quotationDetail(id));
    }

    //历史报价，只返回页面显示的报价单编号、客户名称、创建时间
    //根据业务员id查询，记得分页
    /**
     * 搜索历史报价单
     */
    @PostMapping("/getHistoryQuotation")
    @RequiresPermissions("sal:sed:quotation:getHistoryQuotation")
    @RequiresPermissionsDesc(menu = {"销售管理", "报价单管理"}, button = "历史报价导入")
    @Operation(summary = "历史报价")
    public ResultInfo<PageResultInfo<SalSedQuotationHistoryImportVo>> getHistoryQuotation(@RequestBody SalSedQuotationHistoryImportParams params) {
        return ResultInfo.success(salSedQuotationService.getHistoryQuotation(params));
    }

    /**
     * 根据报价单id返回更加详细的历史报价单信息
     * @param ids
     * @return
     */
    @GetMapping("/getHistoryQuotationDetail")
    @RequiresPermissions("sal:sed:quotation:getHistoryQuotationDetail")
    @RequiresPermissionsDesc(menu = {"销售管理", "报价单管理"}, button = "历史报价详细")
    @Operation(summary = "历史报价详细")
    public ResultInfo<List<SalSedQuotationSkuVo>> getHistoryQuotationDetail(@RequestParam List<Long> ids) {
        return ResultInfo.success(salSedQuotationService.getHistoryQuotationDetail(ids));

    }



    //历史报价
    /**
     * 历史报价
     */
    @PostMapping("/getHistoryQuotationInfo")
    @RequiresPermissions("sal:sed:quotation:getHistoryQuotationInfo")
    @RequiresPermissionsDesc(menu = {"销售管理", "报价单管理"}, button = "历史报价")
    @Operation(summary = "历史报价信息")
    public ResultInfo<SalSedHistoryQuotationInfoVo> getHistoryQuotationInfo(@RequestBody @Validated SalSedHistoryQuotationInfoParams params) {
        return ResultInfo.success(salSedQuotationService.getHistoryQuotationInfo(params));
    }



    //成本明细
    /**
     * 成本明细
     */
    @PostMapping("/getCostDetail")
    @RequiresPermissions("sal:sed:quotation:getCostDetail")
    @RequiresPermissionsDesc(menu = {"销售管理", "报价单管理"}, button = "成本明细")
    @Operation(summary = "成本明细")
    public ResultInfo<SalSedQuotationCostDetailShiftVo> getCostDetail(@RequestBody @Validated SalSedQuotationCostDetailParams params) {
        return ResultInfo.success(salSedQuotationService.getCostDetail(params));
    }


    //一键转订单
    /**
     * 一键转订单
     */
    @PostMapping("/oneKeyToOrder")
    @RequiresPermissions("sal:sed:quotation:oneKeyToOrder")
    @RequiresPermissionsDesc(menu = {"销售管理", "报价单管理"}, button = "一键转订单")
    @Operation(summary = "一键转订单")
    public ResultInfo<String> oneKeyToOrder(@RequestBody @Validated SalSedQuotationOneKeyToOrderParams params) {
        return ResultInfo.success(salSedQuotationService.oneKeyToOrder(params));
    }

    /**
     * 单个 SKU 转订单
     */
    @PostMapping("/skuToOrder")
    @RequiresPermissions("sal:sed:quotation:skuToOrder")
    @RequiresPermissionsDesc(menu = {"销售管理", "报价单管理"}, button = "单个SKU转订单")
    @Operation(summary = "单个SKU转订单")
    public ResultInfo<String> skuToOrder(@RequestBody @Validated SalSedQuotationSkuToOrderParams params) {
        return ResultInfo.success(salSedQuotationService.skuToOrder(params));
    }

    /**
     * 合并转订单-请选择产品：仅返回审核通过的报价单，支持报价单编号、SKU名称、搭配名称筛选
     */
    @PostMapping("/mergeList")
    @RequiresPermissions("sal:sed:quotation:mergeList")
    @RequiresPermissionsDesc(menu = {"销售管理", "报价单管理"}, button = "合并转订单-选择产品列表")
    @Operation(summary = "合并转订单-请选择产品列表")
    public ResultInfo<PageResultInfo<SalSedQuotationMergeItemVo>> listForMerge(@RequestBody(required = false) SalSedQuotationMergeListParams params) {
        return ResultInfo.success(salSedQuotationService.listForMerge(params));
    }

    /**
     * 合并转订单：将选中的多个报价单 SKU 合并生成一个订单（业务员与客户须一致，SKU 不能重复）
     */
    @PostMapping("/mergeToOrder")
    @RequiresPermissions("sal:sed:quotation:mergeToOrder")
    @RequiresPermissionsDesc(menu = {"销售管理", "报价单管理"}, button = "合并转订单")
    @Operation(summary = "合并转订单")
    public ResultInfo<String> mergeToOrder(@RequestBody @Validated SalSedQuotationMergeToOrderParams params) {
        return ResultInfo.success(salSedQuotationService.mergeToOrder(params));
    }

    /**
     * 合并转订单-获取报价单下的SKU列表（用于展开行时加载）
     */
    @PostMapping("/mergeSkuList")
    @RequiresPermissions("sal:sed:quotation:mergeList")
    @Operation(summary = "合并转订单-SKU列表")
    public ResultInfo<List<SalSedQuotationMergeSkuItemVo>> getMergeSkuList(@RequestBody @Validated SalSedQuotationMergeSkuListParams params) {
        return ResultInfo.success(salSedQuotationService.getMergeSkuList(params));
    }

    /**
     * 获取币种列表
     */
    @GetMapping("/getCurrencyList")
    @RequiresPermissions("sal:sed:quotation:getCurrencyList")
    @RequiresPermissionsDesc(menu = {"销售管理", "报价单管理"}, button = "获取币种列表")
    @Operation(summary = "获取币种列表")
    public ResultInfo<List<DictionaryOptionVO>> getCurrencyList() {
        return ResultInfo.success(salSedQuotationService.getCurrencyList());
    }

    /**
     * 获取是否含税列表
     */
    @GetMapping("/getTaxList")
    @RequiresPermissions("sal:sed:quotation:getTaxList")
    @RequiresPermissionsDesc(menu = {"销售管理", "报价单管理"}, button = "获取是否含税列表")
    @Operation(summary = "获取是否含税列表")
    public ResultInfo<List<DictionaryOptionVO>> getTaxList() {
        return ResultInfo.success(salSedQuotationService.getTaxList());
    }

    /**
     * 获取 FOB 列表
     */
    @GetMapping("/getFobList")
    @RequiresPermissions("sal:sed:quotation:getFobList")
    @RequiresPermissionsDesc(menu = {"销售管理", "报价单管理"}, button = "获取 FOB 列表")
    @Operation(summary = "获取 FOB 列表")
    public ResultInfo<List<String>> getFobList() {
        return ResultInfo.success(salSedQuotationService.getFobList());
    }

    /**
     * 获取 EXW 列表
     */
    @GetMapping("/getExwList")
    @RequiresPermissions("sal:sed:quotation:getExwList")
    @RequiresPermissionsDesc(menu = {"销售管理", "报价单管理"}, button = "获取 EXW 列表")
    @Operation(summary = "获取 EXW 列表")
    public ResultInfo<List<String>> getExwList() {
        return ResultInfo.success(salSedQuotationService.getExwList());
    }

    /**
     * 根据币种获取需要显示的字段
     */
    @GetMapping("/getDisplayFieldsByCurrency")
    @RequiresPermissions("sal:sed:quotation:getDisplayFieldsByCurrency")
    @RequiresPermissionsDesc(menu = {"销售管理", "报价单管理"}, button = "根据币种获取需要显示的字段")
    @Operation(summary = "根据币种获取需要显示的字段")
    public ResultInfo<Map<String, Boolean>> getDisplayFieldsByCurrency(String currency) {
        return ResultInfo.success(salSedQuotationService.getDisplayFieldsByCurrency(currency));
    }

    /**
     * 总裁微信审核通过（仅销售角色可操作）
     */
    @PostMapping("/presidentWxAudit")
    @RequiresPermissions("sal:sed:quotation:presidentWxAudit")
    @RequiresPermissionsDesc(menu = {"销售管理", "报价单管理"}, button = "总裁微信审核通过")
    @Operation(summary = "总裁微信审核通过")
    public ResultInfo<String> presidentWxAudit(@RequestBody SalSedQuotationOperateParams params) {
        return ResultInfo.success(salSedQuotationService.presidentWxAudit(params));
    }

}
