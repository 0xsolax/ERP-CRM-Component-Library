/*
 * @author java_deng
 * @date 2025/11/24 19:56
 * @description
 */
package com.qiaomoyun.controller.pur.yt;


import com.qiaomoyun.Exception.BizException;
import com.qiaomoyun.Exception.ExceptionCodeEnum;
import com.qiaomoyun.annotation.RequestLock;
import com.qiaomoyun.annotation.RequiresPermissions;
import com.qiaomoyun.annotation.RequiresPermissionsDesc;
import com.qiaomoyun.entity.pro.yt.ProYtProductSpecificationSupplier;
import com.qiaomoyun.entity.pur.yt.PurYtApplyPurchase;
import com.qiaomoyun.entity.pur.yt.PurYtSupplier;
import com.qiaomoyun.info.ResultInfo;
import com.qiaomoyun.manager.pur.yt.PurYtApplyPurchaseManager;
import com.qiaomoyun.param.sal.yt.PurYtApplyPurchaseQueryParams;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pur/yt/applyPurchase")
@Tag(name = "采购管理", description = "申购单管理相关功能")
@Validated
public class PurYtApplyPurchaseController {
    @Autowired
    private PurYtApplyPurchaseManager purYtApplyPurchaseManager;

    @PostMapping("/saveOrUpdate")
    @RequiresPermissionsDesc(menu = {"采购管理", "待采购列表"}, button = "新增/编辑")
    @RequiresPermissions("pur:yt:applyPurchase:saveOrUpdate")
    @Operation(summary = "申购单新增编辑")
    @RequestLock
    public ResultInfo saveOrUpdate(@RequestBody List<PurYtApplyPurchase> paramsList) {
        purYtApplyPurchaseManager.saveOrUpdate(paramsList);
        return ResultInfo.success();
    }

    @PostMapping("/list")
    @RequiresPermissionsDesc(menu = {"采购管理", "待采购列表"}, button = "列表")
    @RequiresPermissions("pur:yt:applyPurchase:list")
    @Operation(summary = "申购单列表")
    public ResultInfo list(@RequestBody PurYtApplyPurchaseQueryParams paramsList) {
        return ResultInfo.success(purYtApplyPurchaseManager.list(paramsList));
    }

    @PostMapping("/saveDetail")
    @RequiresPermissionsDesc(menu = {"采购管理", "待采购列表"}, button = "详情")
    @RequiresPermissions("pur:yt:applyPurchase:saveDetail")
    @Operation(summary = "新增采购单详情")
    public ResultInfo saveDetail(@RequestBody PurYtApplyPurchaseQueryParams paramsList) {
        List<Long> applyPurchaseIdList = paramsList.getApplyPurchaseIdList();
        if(applyPurchaseIdList == null || applyPurchaseIdList.isEmpty()){
            throw new BizException(ExceptionCodeEnum.Param_Exception);
        }
        return ResultInfo.success(purYtApplyPurchaseManager.saveDetail(paramsList));
    }

    @PostMapping("/replaceSupplier")
    @RequiresPermissionsDesc(menu = {"采购管理", "待采购列表"}, button = "更换供应商")
    @RequiresPermissions("pur:yt:applyPurchase:replaceSupplier")
    @Operation(summary = "更换供应商")
    public ResultInfo replaceSupplier(@RequestBody PurYtApplyPurchaseQueryParams paramsList) {
        Long supplierId = paramsList.getSupplierId();
        if(supplierId==null){
            throw new BizException(ExceptionCodeEnum.Param_Exception);
        }
        purYtApplyPurchaseManager.replaceSupplier(paramsList);
        return ResultInfo.success();
    }

    /**
     * 根据规格ID列表获取可更换的供应商
     * @param specificationIds 规格ID列表
     * @return 可更换的供应商列表
     */
    @PostMapping("/listReplaceableSuppliers")
//    @RequiresPermissionsDesc(menu = {"采购管理", "待采购列表"}, button = "获取可更换供应商")
//    @RequiresPermissions("pur:yt:applyPurchase:listReplaceableSuppliers")
    @Operation(summary = "获取可更换供应商")
    public ResultInfo listReplaceableSuppliers(@RequestBody List<Long> specificationIds) {
        List<ProYtProductSpecificationSupplier> suppliers = purYtApplyPurchaseManager.listReplaceableSuppliers(specificationIds);
        return ResultInfo.success(suppliers);
    }

    @PostMapping("/addPurchase")
    @RequiresPermissionsDesc(menu = {"采购管理", "待采购列表"}, button = "追加")
    @RequiresPermissions("pur:yt:applyPurchase:addPurchase")
    @Operation(summary = "追加采购单")
    public ResultInfo addPurchase(@RequestBody PurYtApplyPurchaseQueryParams paramsList) {
        purYtApplyPurchaseManager.addPurchase(paramsList);
        return ResultInfo.success();
    }

    @Operation(summary = "退回申购记录")
    @PostMapping("/withdraw")
    public ResultInfo withdraw(@RequestBody java.util.Map<String, Long> params) {
        Long id = params.get("id");
        if (id == null) {
            throw new BizException(ExceptionCodeEnum.Param_Exception);
        }
        purYtApplyPurchaseManager.withdraw(id);
        return ResultInfo.success();
    }

}
