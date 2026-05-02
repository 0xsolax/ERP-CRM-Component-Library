/*
 * @author java_deng
 * @date 2025/12/2 15:23
 * @description 采购订单控制器
 */
package com.qiaomoyun.controller.pur.yt;

import com.qiaomoyun.Exception.BizException;
import com.qiaomoyun.Exception.ExceptionCodeEnum;
import com.qiaomoyun.annotation.RequestLock;
import com.qiaomoyun.annotation.RequiresPermissions;
import com.qiaomoyun.annotation.RequiresPermissionsDesc;
import com.qiaomoyun.eunm.yt.PurchaseStatusEnum;
import com.qiaomoyun.entity.pur.yt.PurYtPurchaseItem;
import com.qiaomoyun.entity.sal.yt.SalYtReturnOrder;
import com.qiaomoyun.info.ResultInfo;
import com.qiaomoyun.manager.pur.yt.PurYtPurchaseManager;
import com.qiaomoyun.param.pur.yt.PurYtPurchaseProductQueryParams;
import com.qiaomoyun.param.pur.yt.PurYtPurchaseItemNotifyParams;
import com.qiaomoyun.param.pur.yt.PurYtPurchaseQueryParams;
import com.qiaomoyun.param.pur.yt.PurYtPurchaseUpdateParams;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

/**
 * 采购订单控制器
 */
@RestController
@RequestMapping("/api/pur/yt/purchase")
@Tag(name = "采购管理", description = "采购订单相关功能")
public class PurYtPurchaseController {

    @Autowired
    private PurYtPurchaseManager purYtPurchaseManager;

    /**
     * 新增或编辑采购订单
     */
    @PostMapping("/createOrUpdate")
    @RequiresPermissions("pur:yt:purchase:createOrUpdate")
    @RequiresPermissionsDesc(menu = {"采购管理", "已采购列表"}, button = "新增/编辑")
    @Operation(summary = "新增或编辑采购订单")
    @RequestLock
    public ResultInfo<Boolean> createOrUpdate(@Valid @RequestBody PurYtPurchaseUpdateParams params) {
        purYtPurchaseManager.createOrUpdate(params);
        return ResultInfo.success(true);
    }

    /**
     * 获取采购订单详情
     */
    @GetMapping("/detail")
    @RequiresPermissions("pur:yt:purchase:detail")
    @RequiresPermissionsDesc(menu = {"采购管理", "已采购列表"}, button = "详情")
    @Operation(summary = "获取采购订单详情")
    public ResultInfo<Object> detail(@RequestParam Long id) {
        return ResultInfo.success(purYtPurchaseManager.detail(id));
    }

    /**
     * 查询采购订单列表
     */
    @PostMapping("/list")
    @RequiresPermissions("pur:yt:purchase:list")
    @RequiresPermissionsDesc(menu = {"采购管理", "已采购列表"}, button = "列表")
    @Operation(summary = "查询采购订单列表")
    public ResultInfo<Object> list(@RequestBody PurYtPurchaseQueryParams params) {
        return ResultInfo.success(purYtPurchaseManager.list(params));
    }

    /**
     * 获取暂存状态，而且指定是否入库发货、供应商的采购单列表
     */
    @PostMapping("/listTemporary")
//    @RequiresPermissions("pur:yt:purchase:listTemporary")
//    @RequiresPermissionsDesc(menu = {"采购管理", "采购订单"}, button = "采购单追加列表")
    @Operation(summary = "获取可追加采购单")
    public ResultInfo<Object> listTemporary(@RequestBody PurYtPurchaseQueryParams params) {
        Boolean isInboundDelivery = params.getIsInboundDelivery();
        Long supplierId = params.getSupplierId();
        if(isInboundDelivery == null && supplierId == null){
            throw new BizException(ExceptionCodeEnum.Param_Exception);
        }
        // 设置状态为暂存
        params.setStatus(PurchaseStatusEnum.temporary.getKey());
        return ResultInfo.success(purYtPurchaseManager.listTemporary(params));
    }

    /**
     * 获取采购单产品列表
     */
//    @RequiresPermissions("pur:yt:purchase:product:list")
//    @RequiresPermissionsDesc(menu = {"采购管理", "采购订单"}, button = "产品列表")
    @Operation(summary = "采购单产品列表")
    @PostMapping("/productList")
    public ResultInfo<List<PurYtPurchaseItem>> productList(@RequestBody PurYtPurchaseProductQueryParams params) {
        return ResultInfo.success(purYtPurchaseManager.listProductsByParams(params));
    }

    /**
     * 获取采购单半成品产品列表
     */
//    @RequiresPermissions("pur:yt:purchase:semifinished:list")
//    @RequiresPermissionsDesc(menu = {"采购管理", "采购订单"}, button = "半成品列表")
    @Operation(summary = "采购单半成品产品列表")
    @PostMapping("/semiFinishedProductList")
    public ResultInfo<List<PurYtPurchaseItem>> semiFinishedProductList(@RequestBody PurYtPurchaseProductQueryParams params) {
        return ResultInfo.success(purYtPurchaseManager.listSemiFinishedProductsByParams(params));
    }

    /**
     * 退货接口
     * 如果是半成品需要判断退货数量是否大于了待确认数量
     * 如果是成品需要确认退货数量是否大于了待入库数量
     */
    @RequiresPermissions("pur:yt:purchase:return")
    @RequiresPermissionsDesc(menu = {"采购管理", "已采购列表"}, button = "退货")
    @Operation(summary = "采购单退货")
    @PostMapping("/return")
    public ResultInfo<Boolean> returnPurchase(@Valid @RequestBody SalYtReturnOrder params) {
        purYtPurchaseManager.returnPurchase(params);
        return ResultInfo.success(true);
    }

    /**
     * 获取采购单退货记录
     */
//    @RequiresPermissions("pur:yt:purchase:return:record")
//    @RequiresPermissionsDesc(menu = {"采购管理", "采购订单"}, button = "退货记录")
    @Operation(summary = "采购单退货记录")
    @PostMapping("/returnRecord")
    public ResultInfo<List<SalYtReturnOrder>> returnRecord(@RequestBody PurYtPurchaseQueryParams params) {
        return ResultInfo.success(purYtPurchaseManager.listReturnRecords(params));
    }

    /**
     * 采购单退货统计,按采购单item的规格分组去查询总退货数量
     */
//    @RequiresPermissions("pur:yt:purchase:return:stats")
//    @RequiresPermissionsDesc(menu = {"采购管理", "采购订单"}, button = "退货流水")
    @Operation(summary = "采购单退货流水")
    @PostMapping("/returnStats")
    public ResultInfo<Object> returnStats(@RequestBody PurYtPurchaseQueryParams params) {
        return ResultInfo.success(purYtPurchaseManager.getReturnStats(params));
    }

//    @RequiresPermissions("pur:yt:purchase:return:detail")
//    @RequiresPermissionsDesc(menu = {"采购管理", "采购订单"}, button = "退货详情")
    @Operation(summary = "采购单退货详情")
    @PostMapping("/returnDetail")
    public ResultInfo<Object> returnDetail(@RequestBody PurYtPurchaseQueryParams params) {
        return ResultInfo.success(purYtPurchaseManager.returnDetail(params));
    }

    /**
     * 采购单跟进记录
     */
//    @RequiresPermissions("pur:yt:purchase:follow:list")
//    @RequiresPermissionsDesc(menu = {"采购管理", "采购订单"}, button = "跟进记录")
    @Operation(summary = "采购单跟进记录")
    @PostMapping("/follow/list")
    public ResultInfo<Object> followList(@RequestBody PurYtPurchaseQueryParams params) {
        return ResultInfo.success(purYtPurchaseManager.getPurchaseFollowList(params));
    }

    /**
     * 跟进采购单
     */
    @RequiresPermissions("pur:yt:purchase:follow:createOrUpdate")
    @RequiresPermissionsDesc(menu = {"采购管理", "已采购列表"}, button = "跟进")
    @Operation(summary = "跟进采购单")
    @PostMapping("/follow/createOrUpdate")
    public ResultInfo<Boolean> followCreateOrUpdate(@Valid @RequestBody com.qiaomoyun.entity.pur.yt.PurYtPurchaseFollow follow) {
        purYtPurchaseManager.followPurchase(follow);
        return ResultInfo.success(true);
    }

//    @RequiresPermissions("pur:yt:purchase:follow:itemOperation")
//    @RequiresPermissionsDesc(menu = {"采购管理", "采购订单"}, button = "操作记录")
    @Operation(summary = "操作记录")
    @GetMapping("/itemOperation")
    public ResultInfo itemOperation(Long itemId) {
        return ResultInfo.success(purYtPurchaseManager.itemOperation(itemId));
    }

    /**
     * 通知接口：修改采购订单子项为已通知，并更新供应商单价
     */
//    @RequiresPermissions("pur:yt:purchase:notify")
//    @RequiresPermissionsDesc(menu = {"采购管理", "采购订单"}, button = "通知")
    @Operation(summary = "采购订单子项通知")
    @PostMapping("/notify")
    public ResultInfo<Boolean> notifyPurchaseItem(@Valid @RequestBody PurYtPurchaseItemNotifyParams params) {
        purYtPurchaseManager.notifyPurchaseItem(params.getPurchaseItemId(), params.getSupplierPrice());
        return ResultInfo.success(true);
    }

    @RequiresPermissions("pur:yt:purchase:export")
    @RequiresPermissionsDesc(menu = {"采购管理", "已采购列表"}, button = "导出")
    @Operation(summary = "导出")
    @GetMapping("/export")
    public void export(HttpServletResponse response, Long purchaseId) throws IOException {
        purYtPurchaseManager.export(response,purchaseId);
    }

    @Operation(summary = "删除暂存采购单")
    @PostMapping("/delete")
    public ResultInfo deletePurchase(@RequestBody Map<String, Long> params) {
        purYtPurchaseManager.deletePurchase(params.get("id"));
        return ResultInfo.success();
    }

}
