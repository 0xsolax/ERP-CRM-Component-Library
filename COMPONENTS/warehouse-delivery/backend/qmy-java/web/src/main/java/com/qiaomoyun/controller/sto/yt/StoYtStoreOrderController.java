/*
 * @author java_deng
 * @date 2025/12/5 11:16
 * @description 出入库单控制器
 */
package com.qiaomoyun.controller.sto.yt;

import com.qiaomoyun.Exception.BizException;
import com.qiaomoyun.Exception.ExceptionCodeEnum;
import com.qiaomoyun.annotation.RequiresPermissions;
import com.qiaomoyun.annotation.RequiresPermissionsDesc;
import com.qiaomoyun.info.ResultInfo;
import com.qiaomoyun.entity.sto.yt.StoYtStoreOrderOperation;
import com.qiaomoyun.manager.sto.yt.StoYtStoreManager;
import com.qiaomoyun.manager.sto.yt.StoYtStoreOrderManager;
import com.qiaomoyun.param.sto.yt.StoYtStoreBatchEnterParams;
import com.qiaomoyun.param.sto.yt.StoYtStoreOrderAddParams;
import com.qiaomoyun.param.sto.yt.StoYtStoreOrderEnterWithAllocationParams;
import com.qiaomoyun.param.sto.yt.StoYtStoreOrderBatchAddParams;
import com.qiaomoyun.param.sto.yt.StoYtStoreOrderQueryParams;
import com.qiaomoyun.service.StoYtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 出入库单控制器
 */
@RestController
@RequestMapping("/api/sto/yt/store/order")
@Tag(name = "出入库单管理", description = "出入库单相关功能")
@Validated
public class StoYtStoreOrderController {

    @Autowired
    private StoYtStoreOrderManager stoYtStoreOrderManager;
    @Autowired
    private StoYtService stoYtService;

    /**
     * 新增出入库单
     * @param params 新增参数
     * @return 操作结果
     */
    @PostMapping("/add")
//    @RequiresPermissions("sto:yt:order:add")
//    @RequiresPermissionsDesc(menu = {"仓储管理", "出入库单"}, button = "新增")
    @Operation(summary = "新增出入库单", description = "新增出入库单")
    public ResultInfo addStoreOrder(@RequestBody List<StoYtStoreOrderAddParams> params) {
        stoYtStoreOrderManager.addStoreOrder(params);
        return ResultInfo.success("新增出入库单成功");
    }

    @PostMapping("/addStore")
    @RequiresPermissions("sto:yt:order:addStore")
    @RequiresPermissionsDesc(menu = {"仓储管理", "入库列表"}, button = "新增")
    @Operation(summary = "新增出入库", description = "新增出入库")
    public ResultInfo addStore(@RequestBody List<StoYtStoreOrderAddParams> params) {
        //新增出入库是直接修改库存，不是生成出入库单
        stoYtStoreOrderManager.addStore(params);
        return ResultInfo.success("出入库成功");
    }
    /**
     * 出入库单列表查询接口
     * @param params 查询参数
     * @return 出入库单列表
     */
    @PostMapping("/list")
    @RequiresPermissions("sto:yt:order:list")
    @RequiresPermissionsDesc(menu = {"仓储管理", "入库列表"}, button = "列表")
    @Operation(summary = "出入库单列表查询", description = "出入库单列表查询")
    public ResultInfo getStoreOrderList(@RequestBody  StoYtStoreOrderQueryParams params) {
        return ResultInfo.success(stoYtStoreOrderManager.getStoreOrderList(params));
    }

    /**
     * 入库单进度列表
     * @param params 查询参数
     * @return 入库单进度列表
     */
    @PostMapping("/progressList")
//    @RequiresPermissions("sto:yt:order:progress")
//    @RequiresPermissionsDesc(menu = {"仓储管理", "入库列表"}, button = "入库单进度列表")
    @Operation(summary = "入库单进度列表", description = "入库单进度列表")
    public ResultInfo getStoreOrderProgressList(@RequestBody StoYtStoreOrderQueryParams params) {
        List<StoYtStoreOrderOperation> progressList = stoYtStoreOrderManager.getStoreOrderProgressList(params);
        return ResultInfo.success(progressList);
    }

    //入库
    @PostMapping("/enter")
    @RequiresPermissions("sto:yt:order:enter")
    @RequiresPermissionsDesc(menu = {"仓储管理", "入库列表"}, button = "入库")
    @Operation(summary = "入库", description = "入库")
    public ResultInfo enter(@RequestBody StoYtStoreOrderAddParams params) {
        Integer enterNumber = params.getEnterNumber();
        List<Long> storeOrderIdList = params.getStoreOrderIdList();
        if(enterNumber == null || storeOrderIdList==null || storeOrderIdList.isEmpty()){
            throw new BizException(ExceptionCodeEnum.Param_Exception);
        }
        stoYtService.enter(params);
        return ResultInfo.success();
    }

    @PostMapping("/enterWithAllocation")
    @RequiresPermissions("sto:yt:order:enter")
    @RequiresPermissionsDesc(menu = {"仓储管理", "入库列表"}, button = "按订单入库")
    @Operation(summary = "按订单维度入库")
    public ResultInfo enterWithAllocation(@RequestBody StoYtStoreOrderEnterWithAllocationParams params) {
        if (params.getStoreOrderIdList() == null || params.getStoreOrderIdList().isEmpty()) {
            throw new BizException(ExceptionCodeEnum.Param_Exception);
        }
        stoYtService.enterWithAllocation(params);
        return ResultInfo.success();
    }

    //入库
    @PostMapping("/batchEnter")
//    @RequiresPermissions("sto:yt:order:batchEnter")
//    @RequiresPermissionsDesc(menu = {"仓储管理", "入库列表"}, button = "批量入库")
    @Operation(summary = "批量入库", description = "批量入库")
    public ResultInfo batchEnter(@RequestBody StoYtStoreBatchEnterParams params) {
        List<StoYtStoreOrderAddParams> stoYtStoreOrderAddParams = params.getStoYtStoreOrderAddParams();
        if(stoYtStoreOrderAddParams==null || stoYtStoreOrderAddParams.isEmpty()){
            throw new BizException(ExceptionCodeEnum.Param_Exception);
        }
        stoYtService.batchEnter(stoYtStoreOrderAddParams);
        return ResultInfo.success();
    }
}
