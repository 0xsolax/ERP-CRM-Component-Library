/*
 * @author java_deng
 * @date 2025/12/18 15:35
 * @description 发货单控制器
 */
package com.qiaomoyun.controller.sto.yt;

import com.qiaomoyun.Exception.BizException;
import com.qiaomoyun.Exception.ExceptionCodeEnum;
import com.qiaomoyun.annotation.RequestLock;
import com.qiaomoyun.annotation.RequiresPermissions;
import com.qiaomoyun.annotation.RequiresPermissionsDesc;
import com.qiaomoyun.entity.sto.yt.StoYtDelivery;
import com.qiaomoyun.entity.sto.yt.StoYtDeliveryBox;
import com.qiaomoyun.info.ResultInfo;
import com.qiaomoyun.manager.sto.yt.StoYtDeliveryManager;
import com.qiaomoyun.param.sto.yt.StoYtDeliveryCompleteParams;
import com.qiaomoyun.param.sto.yt.StoYtDeliveryQueryParams;
import com.qiaomoyun.service.StoYtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * 发货单控制器
 */
@RestController
@RequestMapping("/api/sto/yt/delivery")
@Tag(name = "发货单管理", description = "发货单相关功能")
@Validated
public class StoYtDeliveryController {

    @Autowired
    private StoYtDeliveryManager stoYtDeliveryManager;
    @Autowired
    private StoYtService stoYtService;

    /**
     * 获取发货单列表
     *
     * @param params 查询参数
     * @return 发货单列表
     */
    @RequiresPermissions("sto:yt:delivery:list")
    @RequiresPermissionsDesc(menu = {"仓储管理", "发货列表"}, button = "列表")
    @Operation(summary = "发货单列表", description = "获取发货单列表，包含订单信息")
    @PostMapping("/list")
    public ResultInfo list(@RequestBody StoYtDeliveryQueryParams params) {
        return ResultInfo.success(stoYtDeliveryManager.listByPage(params));
    }

    /**
     * 根据ID获取发货单详情
     *
     * @param params 发货单详情查询参数
     * @return 发货单详情
     */
    @RequiresPermissions("sto:yt:delivery:detail")
    @RequiresPermissionsDesc(menu = {"仓储管理", "发货列表"}, button = "详情")
    @Operation(summary = "发货单详情", description = "根据ID获取发货单详情，支持按产品或子订单分组")
    @PostMapping("/detail")
    public ResultInfo detail(@RequestBody StoYtDeliveryQueryParams params) {
        return ResultInfo.success(stoYtDeliveryManager.getById(params));
    }

    //获取子订单分组
//    @RequiresPermissions("sto:yt:delivery:detailOrderSub")
//    @RequiresPermissionsDesc(menu = {"仓储管理", "发货列表"}, button = "发货单详情子订单")
    @Operation(summary = "发货单详情子订单", description = "子订单分组")
    @PostMapping("/detailOrderSub")
    public ResultInfo detailOrderSub(@RequestBody StoYtDeliveryQueryParams params) {
        Long id = params.getId();
        if(id==null){
            throw new BizException(ExceptionCodeEnum.Param_Exception);
        }
        return ResultInfo.success(stoYtDeliveryManager.detailOrderSub(params));
    }

    //获取订单分组
//    @RequiresPermissions("sto:yt:delivery:detailOrder")
//    @RequiresPermissionsDesc(menu = {"仓储管理", "发货列表"}, button = "发货单详情订单")
    @Operation(summary = "发货单详情订单", description = "订单分组")
    @PostMapping("/detailOrder")
    public ResultInfo detailOrder(@RequestBody StoYtDeliveryQueryParams params) {
        Long id = params.getId();
        if(id==null){
            throw new BizException(ExceptionCodeEnum.Param_Exception);
        }
        return ResultInfo.success(stoYtDeliveryManager.detailOrder(params));
    }

    //获取发货单订单详情
//    @RequiresPermissions("sto:yt:delivery:orderDetail")
//    @RequiresPermissionsDesc(menu = {"仓储管理", "发货单"}, button = "发货单订单详情")
    @Operation(summary = "发货单订单详情", description = "根据发货单ID分组查询涉及的订单")
    @PostMapping("/orderDetail")
    public ResultInfo orderDetail(@RequestBody StoYtDeliveryQueryParams params) {
        Long id = params.getId();
        if(id==null){
            throw new BizException(ExceptionCodeEnum.Param_Exception);
        }
        return ResultInfo.success(stoYtDeliveryManager.listGroupOrderByDeliveryId(id));
    }

    //打印
//    @RequiresPermissions("sto:yt:delivery:export")
//    @RequiresPermissionsDesc(menu = {"仓储管理", "发货单"}, button = "打印")
    @Operation(summary = "发货单打印", description = "打印")
    @PostMapping("/export")
    public void export(@RequestBody StoYtDeliveryQueryParams params, HttpServletResponse response) throws IOException {
        Long id = params.getId();
        if(id == null){
            throw new BizException(ExceptionCodeEnum.Param_Exception);
        }
        stoYtDeliveryManager.export(params, response);
    }

    /**
     * 扫码接口
     * @param params 查询参数
     * @return 扫码结果
     */
//    @RequiresPermissions("sto:yt:delivery:scan")
//    @RequiresPermissionsDesc(menu = {"仓储管理", "发货单"}, button = "扫码")
    @Operation(summary = "扫码", description = "根据发货单id和规格id查询产品信息")
    @PostMapping("/scan")
    public ResultInfo scan(@RequestBody StoYtDeliveryQueryParams params) {
        return ResultInfo.success(stoYtDeliveryManager.scan(params));
    }


    //打包
    @RequiresPermissions("sto:yt:delivery:takePackage")
    @RequiresPermissionsDesc(menu = {"仓储管理", "发货列表"}, button = "打包")
    @Operation(summary = "打包", description = "打包")
    @PostMapping("/package")
    public ResultInfo takePackage(@RequestBody List<StoYtDeliveryBox> params) {
        stoYtService.takePackage(params);
        return ResultInfo.success();
    }

    //打包暂存
//    @RequiresPermissions("sto:yt:delivery:savePackage")
//    @RequiresPermissionsDesc(menu = {"仓储管理", "发货列表"}, button = "打包暂存")
    @Operation(summary = "打包暂存", description = "保存包裹信息，不校验数量是否齐全也不修改发货单和订单的状态")
    @PostMapping("/savePackage")
    @RequestLock
    public ResultInfo savePackage(@RequestBody List<StoYtDeliveryBox> params) {
        stoYtService.savePackage(params);
        return ResultInfo.success();
    }

    //打包包裹列表
//    @RequiresPermissions("sto:yt:delivery:packageList")
//    @RequiresPermissionsDesc(menu = {"仓储管理", "发货单"}, button = "包裹列表")
    @Operation(summary = "打包包裹列表", description = "包裹列表")
    @PostMapping("/packageList")
    public ResultInfo packageList(@RequestBody StoYtDeliveryQueryParams params) {
        Long id = params.getId();
        if(id == null){
            throw new BizException(ExceptionCodeEnum.Param_Exception);
        }
        return ResultInfo.success(stoYtDeliveryManager.packageList(params));
    }

//    @RequiresPermissions("sto:yt:delivery:packageItemList")
//    @RequiresPermissionsDesc(menu = {"仓储管理", "发货单"}, button = "包裹物品列表")
    @Operation(summary = "包裹物品列表", description = "包裹物品列表")
    @PostMapping("/packageItemList")
    public ResultInfo packageItemList(@RequestBody StoYtDeliveryQueryParams params) {
        Long id = params.getId();
        if(id == null || params.getDeliveryBoxId() == null){
            throw new BizException(ExceptionCodeEnum.Param_Exception);
        }
        return ResultInfo.success(stoYtDeliveryManager.packageItemList(params));
    }

    //确认发货
//    @RequiresPermissions("sto:yt:delivery:confirmDelivery")
//    @RequiresPermissionsDesc(menu = {"仓储管理", "发货单"}, button = "确认发货")
    @Operation(summary = "确认发货", description = "确认发货")
    @PostMapping("/confirmDelivery")
    @RequestLock
    public ResultInfo confirmDelivery(@RequestBody StoYtDelivery params) {
        Long id = params.getId();
        if(id == null ){
            throw new BizException(ExceptionCodeEnum.Param_Exception);
        }
        stoYtService.delivery(params);
        return ResultInfo.success();
    }

    //修改物流信息
//    @RequiresPermissions("sto:yt:delivery:updateTransport")
//    @RequiresPermissionsDesc(menu = {"仓储管理", "发货单"}, button = "修改物流信息")
    @Operation(summary = "修改物流信息", description = "修改物流信息")
    @PostMapping("/updateTransport")
    public ResultInfo updateTransport(@RequestBody StoYtDelivery params) {
        Long id = params.getId();
        if(id == null ){
            throw new BizException(ExceptionCodeEnum.Param_Exception);
        }
        stoYtDeliveryManager.updateTransport(params);
        return ResultInfo.success();
    }

    //打包完成
//    @RequiresPermissions("sto:yt:delivery:validCompletePackage")
//    @RequiresPermissionsDesc(menu = {"仓储管理", "发货列表"}, button = "校验打包完成")
    @Operation(summary = "校验打包完成", description = "校验打包完成")
    @PostMapping("/validCompletePackage")
    public ResultInfo validCompletePackage(@RequestBody StoYtDeliveryCompleteParams params) {
        return ResultInfo.success(stoYtDeliveryManager.validCompletePackage(params));
    }

    //给业务员发消息
//    @RequiresPermissions("sto:yt:delivery:sendPackageMessage")
//    @RequiresPermissionsDesc(menu = {"仓储管理", "发货列表"}, button = "发送打包消息")
    @Operation(summary = "发送打包消息", description = "发送打包消息")
    @PostMapping("/sendPackageMessage")
    public ResultInfo sendPackageMessage(@RequestBody StoYtDeliveryCompleteParams params) {
        stoYtDeliveryManager.sendPackageMessage(params);
        return ResultInfo.success();
    }

    //打包完成
    @RequiresPermissions("sto:yt:delivery:completePackage")
    @RequiresPermissionsDesc(menu = {"仓储管理", "发货列表"}, button = "打包完成")
    @Operation(summary = "打包完成", description = "根据订单ID列表和发货单ID，将已打包的产品生成新的发货单")
    @PostMapping("/completePackage")
    @RequestLock
    public ResultInfo completePackage(@RequestBody StoYtDeliveryCompleteParams params) {
        stoYtService.completePackage(params);
        return ResultInfo.success();
    }

    // 退回待打包
    @RequiresPermissions("sto:yt:delivery:returnWaitPackage")
    @RequiresPermissionsDesc(menu = {"仓储管理", "发货列表"}, button = "退回待打包")
    @Operation(summary = "退回待打包", description = "将待发货发货单退回到待打包暂存状态")
    @PostMapping("/returnWaitPackage")
    @RequestLock
    public ResultInfo returnWaitPackage(@RequestBody StoYtDelivery params) {
        Long id = params.getId();
        if (id == null) {
            throw new BizException(ExceptionCodeEnum.Param_Exception);
        }
        stoYtService.returnWaitPackage(params);
        return ResultInfo.success();
    }
}
