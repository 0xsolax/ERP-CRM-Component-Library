/*
 * @author java_deng
 * @date 2025/12/2 14:45
 * @description
 */
package com.qiaomoyun.controller.sto.yt;

import com.qiaomoyun.annotation.RequiresPermissions;
import com.qiaomoyun.annotation.RequiresPermissionsDesc;
import com.qiaomoyun.info.ResultInfo;
import com.qiaomoyun.manager.sto.yt.StoYtStoreManager;
import com.qiaomoyun.param.sto.yt.StoYtStoreRecordQueryParams;
import com.qiaomoyun.service.sto.yt.StoYtStoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 库存管理控制器
 */
@RestController
@RequestMapping("/api/sto/yt/store")
@Tag(name = "库存管理", description = "库存相关功能")
@Validated
public class StoYtStoreController {

    @Autowired
    private StoYtStoreManager stoYtStoreManager;

    @Autowired
    private StoYtStoreService StoYtStoreService;

    /**
     * 根据产品ID获取该产品下所有规格库存信息
     * @param productId 产品ID
     * @return 规格库存信息列表
     */
    @RequiresPermissions("sto:yt:store:list")
    @RequiresPermissionsDesc(menu = {"仓储管理", "实时库存"}, button = "列表")
    @Operation(summary = "产品库存详情", description = "产品库存详情")
    @GetMapping("/product")
    public ResultInfo getStockByProductId(Long productId) {
        if (productId == null || productId <= 0) {
            return ResultInfo.error("产品ID不能为空且必须大于0");
        }
        return ResultInfo.success(stoYtStoreManager.getStockByProductId(productId));
    }

    /**
     * 库存历史流向记录
     * @param params 查询参数（包含规格id、子订单号、采购单号、类型等，支持分页）
     * @return 分页后的库存历史记录列表
     */
    @PostMapping("/history")
    @RequiresPermissions("sto:yt:store:history")
    @RequiresPermissionsDesc(menu = {"仓储管理", "实时库存"}, button = "历史流向")
    @Operation(summary = "历史流向")
    public ResultInfo<Object> getStoreHistory(@RequestBody StoYtStoreRecordQueryParams params) {
        return ResultInfo.success(stoYtStoreManager.getStoreHistory(params));
    }

    // 库存占用详情
    @PostMapping("/storeOccupyDetail")
//    @RequiresPermissions("sto:yt:store:storeOccupyDetail")
//    @RequiresPermissionsDesc(menu = {"仓储管理", "实时库存"}, button = "库存占用详情")
    @Operation(summary = "库存占用详情")
    public ResultInfo<Object> storeOccupyDetail(@RequestBody StoYtStoreRecordQueryParams params) {
        return ResultInfo.success(stoYtStoreManager.storeOccupyDetail(params));
    }
    // 在途占用详情
    @PostMapping("/transitOccupyDetail")
//    @RequiresPermissions("sto:yt:store:transitOccupyDetail")
//    @RequiresPermissionsDesc(menu = {"仓储管理", "实时库存"}, button = "在途占用详情")
    @Operation(summary = "在途占用详情")
    public ResultInfo<Object> transitOccupyDetail(@RequestBody StoYtStoreRecordQueryParams params) {
        return ResultInfo.success(stoYtStoreManager.transitOccupyDetail(params));
    }

    /**
     * 设置库存预警规则
     * @param value 预警规则值
     * @return 设置结果
     */
    @RequiresPermissions("sto:yt:store:setWarning")
    @RequiresPermissionsDesc(menu = {"仓储管理", "实时库存"}, button = "设置预警规则")
    @Operation(summary = "设置预警规则", description = "设置库存预警规则")
    @PostMapping("/warning/rule")
    public ResultInfo setStoreWarningRule(@RequestParam String value) {
        stoYtStoreManager.setStoreWarningRule(value);
        return ResultInfo.success("设置成功");
    }

    /**
     * 获取库存预警规则
     * @return 预警规则值
     */
//    @RequiresPermissions("sto:yt:store:getWarning")
//    @RequiresPermissionsDesc(menu = {"仓储管理", "实时库存"}, button = "获取预警规则")
    @Operation(summary = "获取预警规则", description = "获取库存预警规则")
    @GetMapping("/warning/rule")
    public ResultInfo getStoreWarningRule() {
        return ResultInfo.success(stoYtStoreManager.getStoreWarningRule());
    }

    /**
     * 获取出入库记录
     * @param params 查询参数
     * @return 分页后的出入库记录列表
     */
//    @RequiresPermissions("sto:yt:store:enterOutRecord")
//    @RequiresPermissionsDesc(menu = {"仓储管理", "实时库存"}, button = "出入库记录")
    @Operation(summary = "获取出入库记录", description = "获取出入库记录")
    @PostMapping("/enterOutRecords")
    public ResultInfo<Object> getStoreEnterOutRecords(@RequestBody StoYtStoreRecordQueryParams params) {
        return ResultInfo.success(stoYtStoreManager.getStoreEnterOutRecords(params));
    }

    /**
     * 设置规格预警规则
     * @param storeId 库存ID
     * @param warningNumber 预警数量
     * @return 设置结果
     */
//    @RequiresPermissions("sto:yt:store:warning")
//    @RequiresPermissionsDesc(menu = {"仓储管理", "实时库存"}, button = "设置规格预警规则")
    @Operation(summary = "设置规格预警规则", description = "设置规格预警规则")
    @PostMapping("/warning/specification")
    public ResultInfo setSpecificationWarningRule(@RequestParam Long storeId, @RequestParam Integer warningNumber) {
        stoYtStoreManager.setSpecificationWarningRule(storeId, warningNumber);
        return ResultInfo.success("设置成功");
    }

    /**
     * 获取规格预警规则
     * @param storeId 库存ID
     * @return 规格预警数量
     */
//    @RequiresPermissions("sto:yt:store:warning")
//    @RequiresPermissionsDesc(menu = {"仓储管理", "库存"}, button = "获取规格预警规则")
    @Operation(summary = "获取规格预警规则", description = "获取规格预警规则")
    @GetMapping("/warning/specification")
    public ResultInfo getSpecificationWarningRule(@RequestParam Long storeId) {
        return ResultInfo.success(stoYtStoreManager.getSpecificationWarningRule(storeId));
    }

    /**
     * 库存预警测试按钮
     */
    @Operation(summary = "库存预警测试按钮", description = "库存预警测试按钮")
    @GetMapping("/warning/test")
    public ResultInfo<String> warningTest() {
        return ResultInfo.success(StoYtStoreService.warningTest());
    }
}
