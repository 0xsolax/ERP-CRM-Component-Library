package com.qiaomoyun.controller.sal.yt;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qiaomoyun.annotation.RequestLock;
import com.qiaomoyun.annotation.RequiresDataPermissions;
import com.qiaomoyun.annotation.RequiresPermissions;
import com.qiaomoyun.annotation.RequiresPermissionsDesc;
import com.qiaomoyun.entity.sal.yt.SalYtOrder;
import com.qiaomoyun.entity.sal.yt.SalYtOrderSub;
import com.qiaomoyun.entity.sal.yt.SalYtOrderSubItem;
import com.qiaomoyun.entity.sal.yt.SalYtReturnOrder;
import com.qiaomoyun.info.TenantInfo;
import com.qiaomoyun.manager.pro.yt.ProYtProductManager;
import com.qiaomoyun.param.pro.yt.ProYtProductDetailParams;
import com.qiaomoyun.param.sal.yt.*;
import com.qiaomoyun.service.sal.yt.SalYtOrderService;
import com.qiaomoyun.util.TenantInfoContext;
import com.qiaomoyun.vo.pro.yt.ProYtProductVo;
import com.qiaomoyun.vo.sal.yt.SalYtOrderVo;
import com.qiaomoyun.vo.sal.yt.SalYtReturnStatsVo;
import com.qiaomoyun.info.ResultInfo;
import com.qiaomoyun.manager.sal.yt.SalYtOrderManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单Controller
 */
@RestController
@RequestMapping("/api/sal/yt/order")
@Validated
@Tag(name = "销售管理", description = "订单管理相关功能")
public class SalYtOrderController {

    @Autowired
    private SalYtOrderManager salYtOrderManager;
    @Autowired
    private ProYtProductManager proYtProductManager;

    @Autowired
    private SalYtOrderService salYtOrderService;

    /**
     * 新增或编辑订单
     */
    @RequiresPermissions("sal:yt:order:save")
    @RequiresPermissionsDesc(menu = {"销售管理", "订单列表"}, button = "新增/编辑")
    @PostMapping("/saveOrUpdate")
    @Operation(summary = "订单新增或编辑")
    @RequestLock
    public ResultInfo<Object> saveOrUpdate(@RequestBody SalYtOrderUpdateParams params) {
        Long id = salYtOrderManager.saveOrUpdateOrder(params);
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", id);
        return  ResultInfo.success(map);
    }

    /**
     * 订单审核
     */
    @RequiresPermissions("sal:yt:order:audit")
    @RequiresPermissionsDesc(menu = {"销售管理", "订单列表"}, button = "审核")
    @PostMapping("/audit")
    @Operation(summary = "订单审核")
    public ResultInfo<String> audit(@RequestBody SalYtOrderUpdateParams params) {
        // 校验id
        if (params.getId() == null) {
            return ResultInfo.error("订单ID不能为空");
        }
        // 校验status
        if (params.getStatus() == null) {
            return ResultInfo.error("审核状态不能为空");
        }

        boolean result = salYtOrderManager.auditOrder(params);
        return result ? ResultInfo.success("审核操作成功") : ResultInfo.error("审核操作失败");
    }

    /**
     * 查询订单列表
     */
    @RequiresPermissions("sal:yt:order:list")
    @RequiresPermissionsDesc(menu = {"销售管理", "订单列表"}, button = "列表")
    @PostMapping("/list")
    @RequiresDataPermissions("开启数据权限")
    @Operation(summary = "查询订单列表")
    public ResultInfo<Object> list(@RequestBody SalYtOrderQueryParams params) {
        return ResultInfo.success(salYtOrderManager.selectOrderPage(params));
    }

    /**
     * 查询订单详情
     */
    @RequiresPermissions("sal:yt:order:detail")
    @RequiresPermissionsDesc(menu = {"销售管理", "订单列表"}, button = "详情")
    @GetMapping("/listDetail")
    @Operation(summary = "订单列表详情")
    public ResultInfo<Map<String, Object>> detail( Long id) {
        Map<String, Object> orderDetail = salYtOrderManager.selectOrderDetail(id);
        if (orderDetail == null) {
            return ResultInfo.error("订单不存在");
        }
        return ResultInfo.success(orderDetail);
    }

    /**
     * 查询订单详情（包含子订单和按productId分组的商品项）
     */
//    @RequiresPermissions("sal:yt:order:detail")
//    @RequiresPermissionsDesc(menu = {"销售管理", "订单管理"}, button = "订单详情")
    @GetMapping("/detail")
    @Operation(summary = "查询订单详情")
    public ResultInfo<SalYtOrderVo> getDetail(Long id) {
        SalYtOrderVo orderVo = salYtOrderManager.getOrderDetailWithProducts(id);
        if (orderVo == null) {
            return ResultInfo.error("订单不存在");
        }
        return ResultInfo.success(orderVo);
    }

    @RequiresPermissions("sal:yt:order:setRate")
    @RequiresPermissionsDesc(menu = {"销售管理", "订单列表"}, button = "汇率设置")
    @PostMapping("/setRate")
    @Operation(summary = "汇率设置")
    public ResultInfo<SalYtOrderVo> setRate(@RequestBody SalYtOrderUpdateParams params) {
        salYtOrderManager.setRate(params);
        return ResultInfo.success();
    }

    /**
     * 查询子订单详情（包含子订单和按productId分组的商品项）
     */
//    @RequiresPermissions("sal:yt:order:subDetail")
//    @RequiresPermissionsDesc(menu = {"销售管理", "订单管理"}, button = "子订单详情")
    @GetMapping("/subDetail")
    @Operation(summary = "查询子订单详情")
    public ResultInfo<SalYtOrderVo> getSubDetail(Long subId) {
        SalYtOrderVo orderVo = salYtOrderManager.getSubDetail(subId);
        if (orderVo == null) {
            return ResultInfo.error("订单不存在");
        }
        return ResultInfo.success(orderVo);
    }


    /**
     * 查询父订单详情
     * @param
     * @return
     */
//    @RequiresPermissions("sal:yt:order:orderDetail")
//    @RequiresPermissionsDesc(menu = {"销售管理", "订单管理"}, button = "订单详情按钮")
    @GetMapping("/orderDetail")
    @Operation(summary = "查询订单详细")
    public ResultInfo<SalYtOrderVo> orderDetail(@RequestParam Long orderId) {
        SalYtOrderVo orderVo = salYtOrderManager.orderDetail(orderId);
        return ResultInfo.success(orderVo);
    }





//    @RequiresPermissions("sal:yt:order:subDetailList")
//    @RequiresPermissionsDesc(menu = {"销售管理", "订单管理"}, button = "子订单详情列表查询")
    @PostMapping("/subDetailList")
    @Operation(summary = "查询子订单详情列表")
    public Object subDetailList(@RequestBody SalYtOrderSubItem params) {
        return ResultInfo.success(salYtOrderManager.subDetailList(params));
    }

//    @RequiresPermissions("pro:yt:product:detail")
//    @RequiresPermissionsDesc(menu = {"产品管理", "产品列表"}, button = "详情")
    @Operation(summary = "产品详情")
    @GetMapping("/productDetail")
    public ResultInfo<ProYtProductVo> detail(ProYtProductDetailParams params) {
        ProYtProductVo product = proYtProductManager.detail(params);
        return ResultInfo.success(product);
    }

    /**
     * 查询订单详情中的产品tab
     */
//    @RequiresPermissions("sal:yt:order:orderDetailProductTab")
//    @RequiresPermissionsDesc(menu = {"销售管理", "订单管理"}, button = "订单详情中的产品tab")
    @PostMapping("/orderDetailProductTab")
    @Operation(summary = "订单详情中的产品tab")
    public ResultInfo<SalYtOrderVo> orderDetailProductTab(@RequestBody SalYtOrderSubItem params) {
        SalYtOrderVo orderDetailProductTab = salYtOrderManager.orderDetailProductTab(params);
        return ResultInfo.success(orderDetailProductTab);
    }

    /**
     * 删除订单
     */
    @RequiresPermissions("sal:yt:order:delete")
    @RequiresPermissionsDesc(menu = {"销售管理", "订单列表"}, button = "删除")
    @GetMapping("/delete")
    @Operation(summary = "删除订单")
    public ResultInfo<String> delete(Long id) {
        boolean result = salYtOrderManager.deleteOrder(id);
        return result ? ResultInfo.success("删除成功") : ResultInfo.error("删除失败");
    }

    /**
     * 导出订单
     */
    @RequiresPermissions("sal:yt:order:export")
    @RequiresPermissionsDesc(menu = {"销售管理", "订单列表"}, button = "导出")
    @PostMapping("/export")
    @Operation(summary = "导出订单")
    public void export(HttpServletResponse response, @RequestBody @Validated SalYtOrderExportParams params) throws Exception {
        salYtOrderManager.export(params, response);
    }

    /**
     * 订单退货接口
     * @param params 退货参数
     * @return 操作结果
     */
    @PostMapping("/returnItem")
    @RequiresPermissions("sal:yt:order:returnItem")
    @RequiresPermissionsDesc(menu = {"销售管理","订单列表"}, button = "退货")
    @Operation(summary="订单退货")
    public ResultInfo returnItem(@RequestBody SalYtOrderSubItem params) {
        salYtOrderManager.returnOrderItem(params);
        return ResultInfo.success();
    }

    /**
     * 订单退货接口2.0
     */
    @PostMapping("/orderReturnItem")
//    @RequiresPermissions("sal:yt:order:orderReturnItem")
//    @RequiresPermissionsDesc(menu = {"销售管理","订单管理"}, button = "订单的退货")
    @Operation(summary="订单的退货")
    public ResultInfo orderReturnItem(@RequestBody @Validated SalYtOrderOrderReturnItemParams params) {
        salYtOrderManager.orderReturnItem(params);
        return ResultInfo.success();
    }

    /**
     * 获取退货记录列表
     * @param params 查询参数
     * @return 退货记录列表
     */
    @PostMapping("/returnOrderList")
//    @RequiresPermissions("sal:yt:order:returnOrderList")
//    @RequiresPermissionsDesc(menu = {"销售管理","订单管理"}, button = "退货记录列表")
    @Operation(summary="获取退货记录列表")
    public ResultInfo getReturnOrderList(@RequestBody SalYtReturnOrderQueryParams params) {
        try {
            List<SalYtReturnOrder> returnOrderList = salYtOrderManager.getReturnOrdersByParams(params);
            return ResultInfo.success(returnOrderList);
        } catch (Exception e) {
            return ResultInfo.error("获取退货记录列表失败：" + e.getMessage());
        }
    }

    @PostMapping("/inCompleteList")
//    @RequiresPermissions("sal:yt:order:inCompleteList")
//    @RequiresPermissionsDesc(menu = {"销售管理","订单管理"}, button = "半成品子订单详情")
    @Operation(summary="半成品子订单详情")
    public ResultInfo inCompleteList(@RequestBody SalYtOrderSubItem params) {
        return ResultInfo.success(salYtOrderManager.inCompleteList(params));
    }


    /**
     * 订单半成品详情
     *  orderId
     * @return
     */
    @PostMapping("/orderInCompleteList")
//    @RequiresPermissions("sal:yt:order:orderInCompleteList")
//    @RequiresPermissionsDesc(menu = {"销售管理","订单管理"}, button = "订单半成品详情")
    @Operation(summary="订单半成品详情")
    public ResultInfo<List<SalYtOrderSubItem>> orderInCompleteList(@RequestBody  SalYtOrderSubItem params) {
        return ResultInfo.success(salYtOrderManager.orderInCompleteList(params));
    }

    @PostMapping("/confirmInComplete")
//    @RequiresPermissions("sal:yt:order:confirmInComplete")
//    @RequiresPermissionsDesc(menu = {"销售管理","订单管理"}, button = "确认半成品")
    @Operation(summary="确认半成品")
    @RequestLock
    public ResultInfo confirmInComplete(@RequestBody SalYtConfirmIncompleteParams params) {
        salYtOrderManager.confirmInComplete(params);
        return ResultInfo.success();
    }

    @GetMapping("/itemOperation")
//    @RequiresPermissions("sal:yt:order:itemOperation")
//    @RequiresPermissionsDesc(menu = {"销售管理","订单管理"}, button = "操作记录")
    @Operation(summary="操作记录")
    public ResultInfo itemOperation(Long itemId) {
        return ResultInfo.success(salYtOrderManager.itemOperation(itemId));
    }

    /**
     * 获取退货统计信息
     * @param params 查询参数
     * @return 退货统计信息列表
     */
    @PostMapping("/return/stats")
//    @RequiresPermissions("sal:yt:order:returnStats")
//    @RequiresPermissionsDesc(menu = {"销售管理","订单管理"}, button = "退货统计信息")
    @Operation(summary="获取退货统计信息")
    public ResultInfo<List<SalYtReturnStatsVo>> getReturnStatsByOrderSubItemId(
            @RequestBody SalYtReturnOrderQueryParams params) {
            List<SalYtReturnStatsVo> result = salYtOrderManager.getReturnStats(params);
            return ResultInfo.success(result);
    }

    @PostMapping("/return/statsData")
//    @RequiresPermissions("sal:yt:order:returnStats1")
//    @RequiresPermissionsDesc(menu = {"销售管理","订单管理"}, button = "退货统计信息1")
    @Operation(summary="获取退货统计信息1")
    public ResultInfo<List<SalYtReturnStatsVo>> getReturnStatsByOrderSubItemId1(
            @RequestBody SalYtReturnOrderQueryParams params) {
        List<SalYtReturnStatsVo> result = salYtOrderManager.getReturnStats1(params);
        return ResultInfo.success(result);
    }

    /**
     * 根据子订单ID和规格ID获取指定规格的退货记录
     */
    @GetMapping("/return/returnListBySpec")
//    @RequiresPermissions("sal:yt:order:returnListBySpec")
//    @RequiresPermissionsDesc(menu = {"销售管理","订单管理"}, button = "指定规格退货记录")
    @Operation(summary="获取指定item的退货记录")
    public ResultInfo<List<SalYtReturnOrder>> getReturnOrdersByOrderSubItemAndSpec(
            @RequestParam("orderSubItemId") Long orderSubItemId) {
        try {
            List<SalYtReturnOrder> result = salYtOrderManager.getReturnOrdersByOrderSubItem(orderSubItemId);
            return ResultInfo.success(result);
        } catch (Exception e) {
            return ResultInfo.error("获取退货记录失败：" + e.getMessage());
        }
    }

    /**
     * 确认发货接口
     */
    @PostMapping("/confirmDelivery")
    @RequiresPermissions("sal:yt:order:confirmDelivery")
    @RequiresPermissionsDesc(menu = {"销售管理","订单管理"}, button = "确认发货")
    @Operation(summary="确认发货")
    public ResultInfo confirmDelivery(@Validated @RequestBody List<SalYtOrderDeliveryParams> params) {
        salYtOrderManager.confirmDelivery(params);
        return ResultInfo.success();
    }

    /**
     * 确认完成接口
     */
    @GetMapping("/confirmComplete")
    @RequiresPermissions("sal:yt:order:confirmComplete")
    @RequiresPermissionsDesc(menu = {"销售管理","订单管理"}, button = "确认完成")
    @Operation(summary="确认完成")
    public ResultInfo confirmComplete(@RequestParam("orderId") Long orderId) {
        salYtOrderManager.confirmComplete(orderId);
        return ResultInfo.success();
    }

    /**
     * 关闭订单
     */
    @PostMapping("/close")
    @Operation(summary = "关闭订单")
    public ResultInfo close(@RequestBody @Validated SalYtOrderCloseParams params) {
        salYtOrderManager.closeOrder(params);
        return ResultInfo.success();
    }

    /**
     * 关闭订单预览
     */
    @GetMapping("/closePreview")
    @Operation(summary = "关闭订单预览")
    public ResultInfo closePreview(@RequestParam("orderId") Long orderId) {
        return ResultInfo.success(salYtOrderManager.getCloseOrderPreview(orderId));
    }

    //订单物流信息
    @PostMapping("/deliveryInfo")
//    @RequiresPermissions("sal:yt:order:deliveryInfo")
//    @RequiresPermissionsDesc(menu = {"销售管理","订单管理"}, button = "获取订单物流信息")
    @Operation(summary="获取订单物流信息")
    public ResultInfo deliveryInfo(@RequestBody SalYtOrderDeliveryParams params) {
        return ResultInfo.success(salYtOrderManager.deliveryInfo(params));
    }

    //获取物流包裹详情
    @GetMapping("/packageDetail")
//    @RequiresPermissions("sal:yt:order:packageDetail")
//    @RequiresPermissionsDesc(menu = {"销售管理","订单管理"}, button = "获取物流包裹详情")
    @Operation(summary="获取物流包裹详情")
    public ResultInfo packageDetail( Long deliveryBoxId) {
        return ResultInfo.success(salYtOrderManager.packageDetail(deliveryBoxId));
    }

    /**
     * 修改发货方式
     */
    @PostMapping("/updateShippingMethod")
    @RequiresPermissions("sal:yt:order:updateShippingMethod")
    @RequiresPermissionsDesc(menu = {"销售管理","订单管理"}, button = "修改发货方式")
    @Operation(summary="修改发货方式")
    public ResultInfo<String> updateShippingMethod(@Validated @RequestBody SalYtOrderUpdateShippingMethodParams params) {
        return ResultInfo.success(salYtOrderManager.updateShippingMethod(params));
    }

    /**
     * 订单详情里面的物流导出
     */
    @PostMapping("/exportDelivery")
    @RequiresPermissions("sal:yt:order:exportDelivery")
    @RequiresPermissionsDesc(menu = {"销售管理","订单管理"}, button = "物流导出")
    @Operation(summary="物流导出")
    public void exportDelivery(@Validated @RequestBody SalYtOrderExportDeliveryParams params, HttpServletResponse response) throws Exception {
        salYtOrderService.exportDelivery(params, response);
    }

    /**
     * 订单导入
     */
    @PostMapping("/importOrders")
    @Operation(summary="订单导入")
    public ResultInfo<List<String>> importOrders(@RequestParam("file") MultipartFile file) throws IOException {
        return ResultInfo.success(salYtOrderManager.importOrders(file));
    }

}