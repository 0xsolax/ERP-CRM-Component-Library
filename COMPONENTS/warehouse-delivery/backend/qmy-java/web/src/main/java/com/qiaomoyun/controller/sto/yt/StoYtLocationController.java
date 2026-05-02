/*
 * @author java_deng
 * @date 2025/11/7 11:12
 * @description
 */
package com.qiaomoyun.controller.sto.yt;

import com.qiaomoyun.annotation.RequiresPermissions;
import com.qiaomoyun.annotation.RequiresPermissionsDesc;
import com.qiaomoyun.entity.sto.yt.StoYtLocation;
import com.qiaomoyun.info.ResultInfo;
import com.qiaomoyun.manager.sto.yt.StoYtLocationManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.qiaomoyun.info.ResultInfo.success;

@RestController
@RequestMapping("/api/sto/yt/location")
@Tag(name = "仓储管理", description = "库位管理相关功能")
@Validated
public class StoYtLocationController {
    @Autowired
    private StoYtLocationManager stoYtLocationManager;

    /**
     * 获取库位下拉框列表
     */
    @GetMapping("/locationSelect")
//    @RequiresPermissionsDesc(menu = {"仓储管理", "库位"}, button = "库位下拉选择列表")
//    @RequiresPermissions("sto:yt:storeLocation:select")
    @Operation(summary = "库位下拉选择列表")
    public ResultInfo<List<StoYtLocation>> getLocationDropdown() {
        return success(stoYtLocationManager.getLocationDropdown());
    }

    /**
     * 新增库位
     *
     * @param name 库位名称
     */
    @PostMapping("/location/add")
//    @RequiresPermissionsDesc(menu = {"仓储管理", "库位"}, button = "新增库位")
//    @RequiresPermissions("sto:yt:location:add")
    @Operation(summary = "新增库位")
    public ResultInfo<Long> getLocationIdByName(@RequestParam String name) {
        return ResultInfo.success( stoYtLocationManager.getLocationIdByName(name));

    }


}
