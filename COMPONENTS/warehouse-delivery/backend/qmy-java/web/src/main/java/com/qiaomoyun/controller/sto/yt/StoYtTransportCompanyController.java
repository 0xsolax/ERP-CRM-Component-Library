/*
 * @author java_deng
 * @date 2025/12/5 11:16
 * @description 物流公司控制器
 */
package com.qiaomoyun.controller.sto.yt;

import com.qiaomoyun.annotation.RequiresPermissions;
import com.qiaomoyun.annotation.RequiresPermissionsDesc;
import com.qiaomoyun.entity.sto.yt.StoYtTransportCompany;
import com.qiaomoyun.info.PageResultInfo;
import com.qiaomoyun.info.ResultInfo;
import com.qiaomoyun.manager.sto.yt.StoYtTransportCompanyManager;
import com.qiaomoyun.param.sto.yt.StoYtTransportCompanyAddParams;
import com.qiaomoyun.param.sto.yt.StoYtTransportCompanyQueryParams;
import com.qiaomoyun.param.sto.yt.StoYtTransportCompanyUpdateParams;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 物流公司控制器
 */
@RestController
@RequestMapping("/api/sto/yt/transportCompany")
@Tag(name = "物流公司管理", description = "物流公司相关功能")
@Validated
public class StoYtTransportCompanyController {

    @Autowired
    private StoYtTransportCompanyManager stoYtTransportCompanyManager;

    /**
     * 新增物流公司
     * @param params 新增参数
     * @return 操作结果
     */
    @PostMapping("/add")
    @RequiresPermissions("sto:yt:transportCompany:add")
    @RequiresPermissionsDesc(menu = {"仓储管理", "物流公司"}, button = "新增")
    @Operation(summary = "新增物流公司", description = "新增物流公司")
    public ResultInfo addTransportCompany(@RequestBody @Valid StoYtTransportCompanyAddParams params) {
        StoYtTransportCompany company = new StoYtTransportCompany();
        BeanUtils.copyProperties(params, company);
        stoYtTransportCompanyManager.addTransportCompany(company);
        return ResultInfo.success("新增物流公司成功");
    }

    /**
     * 更新物流公司
     * @param params 更新参数
     * @return 操作结果
     */
    @PostMapping("/update")
    @RequiresPermissions("sto:yt:transportCompany:update")
    @RequiresPermissionsDesc(menu = {"仓储管理", "物流公司"}, button = "更新")
    @Operation(summary = "更新物流公司", description = "更新物流公司")
    public ResultInfo updateTransportCompany(@RequestBody @Valid StoYtTransportCompanyUpdateParams params) {
        StoYtTransportCompany company = new StoYtTransportCompany();
        BeanUtils.copyProperties(params, company);
        stoYtTransportCompanyManager.updateTransportCompany(company);
        return ResultInfo.success("更新物流公司成功");
    }

    /**
     * 删除物流公司
     * @param id 物流公司ID
     * @return 操作结果
     */
    @GetMapping("/delete/{id}")
    @RequiresPermissions("sto:yt:transportCompany:delete")
    @RequiresPermissionsDesc(menu = {"仓储管理", "物流公司"}, button = "删除")
    @Operation(summary = "删除物流公司", description = "删除物流公司")
    public ResultInfo deleteTransportCompany(@PathVariable Long id) {
        stoYtTransportCompanyManager.deleteTransportCompany(id);
        return ResultInfo.success("删除物流公司成功");
    }

    /**
     * 根据ID查询物流公司
     * @param id 物流公司ID
     * @return 物流公司信息
     */
    @GetMapping("/get/{id}")
    @RequiresPermissions("sto:yt:transportCompany:get")
    @RequiresPermissionsDesc(menu = {"仓储管理", "物流公司"}, button = "详情")
    @Operation(summary = "根据ID查询物流公司", description = "根据ID查询物流公司详情")
    public ResultInfo<StoYtTransportCompany> getTransportCompanyById(@PathVariable Long id) {
        StoYtTransportCompany company = stoYtTransportCompanyManager.getTransportCompanyById(id);
        return ResultInfo.success(company);
    }

    /**
     * 查询所有物流公司
     * @return 物流公司列表
     */
    @GetMapping("/list")
    @RequiresPermissions("sto:yt:transportCompany:list")
    @RequiresPermissionsDesc(menu = {"仓储管理", "物流公司"}, button = "列表")
    @Operation(summary = "查询所有物流公司", description = "查询所有物流公司列表")
    public ResultInfo<List<StoYtTransportCompany>> getAllTransportCompanies() {
        List<StoYtTransportCompany> companies = stoYtTransportCompanyManager.getAllTransportCompanies();
        return ResultInfo.success(companies);
    }

    /**
     * 分页查询物流公司
     * @param params 查询参数
     * @return 分页结果
     */
    @GetMapping("/page")
//    @RequiresPermissions("sto:yt:transportCompany:page")
//    @RequiresPermissionsDesc(menu = {"仓储管理", "物流公司管理"}, button = "分页查询物流公司")
    @Operation(summary = "分页查询物流公司", description = "分页查询物流公司")
    public ResultInfo<PageResultInfo<StoYtTransportCompany>> getTransportCompaniesByPage(StoYtTransportCompanyQueryParams params) {
        PageResultInfo<StoYtTransportCompany> result = stoYtTransportCompanyManager.getTransportCompaniesByPage(params);
        return ResultInfo.success(result);
    }
}
