/*
 * @author java_deng
 * @date 2024/12/15 10:15
 * @description 箱规管理控制器
 */
package com.qiaomoyun.controller.sto.yt;

import com.qiaomoyun.annotation.RequiresPermissions;
import com.qiaomoyun.annotation.RequiresPermissionsDesc;
import com.qiaomoyun.entity.sto.yt.StoYtBox;
import com.qiaomoyun.manager.sto.yt.StoYtBoxManager;
import com.qiaomoyun.param.BasePageQuery;
import com.qiaomoyun.info.ResultInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 箱规管理控制器
 */
@RestController
@RequestMapping("/api/sto/yt/box")
@Tag(name = "仓储管理", description = "打包箱管理相关功能")
@Validated
public class StoYtBoxController {

    @Autowired
    private StoYtBoxManager stoYtBoxManager;

    /**
     * 增加或编辑箱规
     */
    @RequiresPermissions("sto:yt:box:saveOrUpdate")
    @RequiresPermissionsDesc(menu = {"仓储管理", "打包箱管理"}, button = "新增或编辑")
    @Operation(summary = "增加或编辑")
    @PostMapping("/saveOrUpdate")
    public ResultInfo saveOrUpdate(@RequestBody StoYtBox stoYtBox) {
        stoYtBoxManager.saveOrUpdate(stoYtBox);
        return ResultInfo.success();
    }

    /**
     * 获取箱规列表
     */
    @RequiresPermissions("sto:yt:box:list")
    @RequiresPermissionsDesc(menu = {"仓储管理", "打包箱管理"}, button = "列表")
    @Operation(summary = "获取打包箱列表")
    @GetMapping("/list")
    public ResultInfo list(StoYtBox stoYtBox, BasePageQuery query) {
        return ResultInfo.success(stoYtBoxManager.list(stoYtBox, query.getPageNum(), query.getPageSize()));
    }

    /**
     * 获取箱规详情
     */
    @RequiresPermissions("sto:yt:box:detail")
    @RequiresPermissionsDesc(menu = {"仓储管理", "打包箱管理"}, button = "详情")
    @Operation(summary = "获取打包箱详情")
    @GetMapping("/detail/{id}")
    public ResultInfo detail(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResultInfo.error("参数错误");
        }
        StoYtBox box = stoYtBoxManager.detail(id);
        if (box == null) {
            return ResultInfo.error("打包箱不存在或已删除");
        }
        return ResultInfo.success(box);
    }

    /**
     * 删除箱规
     */
    @RequiresPermissions("sto:yt:box:delete")
    @RequiresPermissionsDesc(menu = {"仓储管理", "打包箱管理"}, button = "删除")
    @Operation(summary = "删除打包箱")
    @GetMapping("/delete")
    public ResultInfo delete( Long id) {
        if (id == null || id <= 0) {
            return ResultInfo.error("参数错误");
        }
        stoYtBoxManager.delete(id);
        return ResultInfo.success();
    }

    /**
     * 打包箱下拉框接口
     */
//    @RequiresPermissions("sto:yt:box:list")
//    @RequiresPermissionsDesc(menu = {"仓储管理", "打包箱管理"}, button = "打包箱下拉框")
    @Operation(summary = "打包箱下拉框")
    @GetMapping("/listForSelect")
    public ResultInfo listForSelect() {
        List<StoYtBox> boxList = stoYtBoxManager.listForSelect();
        return ResultInfo.success(boxList);
    }
}