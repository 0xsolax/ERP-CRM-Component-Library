/*
 * @author java_deng
 * @date 2025/12/1 13:09
 * @description 库存预警控制器
 */
package com.qiaomoyun.controller.pur.yt;

import com.qiaomoyun.annotation.RequiresPermissions;
import com.qiaomoyun.annotation.RequiresPermissionsDesc;
import com.qiaomoyun.entity.pur.yt.PurYtStoreWarning;
import com.qiaomoyun.info.ResultInfo;
import com.qiaomoyun.info.PageResultInfo;
import com.qiaomoyun.service.PurYtStoreWarningService;
import com.qiaomoyun.param.pur.yt.PurYtStoreWarningQueryParams;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pur/yt/storeWarning")
@Tag(name = "采购管理", description = "库存预警相关功能")
@Validated
public class PurYtStoreWarningController {

    @Autowired
    private PurYtStoreWarningService purYtStoreWarningService;

    /**
     * 库存预警列表
     */
    @PostMapping("/list")
    @Operation(summary = "库存预警列表")
    @RequiresPermissions("pur:yt:storeWarning:list")
    @RequiresPermissionsDesc(menu = {"采购管理", "库存预警"}, button = "列表")
    public Object list(@RequestBody PurYtStoreWarningQueryParams params) {
        return ResultInfo.success(purYtStoreWarningService.list(params));
    }

    @PostMapping("/applyDetail")
    @Operation(summary = "申购详情")
    @RequiresPermissions("pur:yt:storeWarning:applyDetail")
    @RequiresPermissionsDesc(menu = {"采购管理", "库存预警"}, button = "详情")
    public Object applyDetail(@RequestBody PurYtStoreWarningQueryParams params) {
        return ResultInfo.success(purYtStoreWarningService.applyDetail(params));
    }

    /**
     * 提交申购
     * @param storeWarningList 库存预警数据
     * @return 操作结果
     */
    @PostMapping("/submitApplyPurchase")
    @Operation(summary = "提交申购")
    @RequiresPermissions("pur:yt:storeWarning:submitApplyPurchase")
    @RequiresPermissionsDesc(menu = {"采购管理", "库存预警"}, button = "申购")
    public Object submitApplyPurchase(@RequestBody List<PurYtStoreWarning> storeWarningList) {
        return ResultInfo.success(purYtStoreWarningService.submitApplyPurchase(storeWarningList));
    }

    /**
     * 生成库存预警记录
     * @return 操作结果
     */
    @GetMapping("/generateWarning")
    @Operation(summary = "生成库存预警记录")
    public Object generateWarning() {
        return ResultInfo.success(purYtStoreWarningService.generateWarning());
    }
}