/*
 * @author java_deng
 * @date 2024/11/20 17:20
 * @description 客户管理Controller
 */
package com.qiaomoyun.controller.sal.yt;

import cn.hutool.core.util.ObjectUtil;
import com.qiaomoyun.Exception.BizException;
import com.qiaomoyun.Exception.ExceptionCodeEnum;
import com.qiaomoyun.annotation.RequiresDataPermissions;
import com.qiaomoyun.annotation.RequiresPermissions;
import com.qiaomoyun.annotation.RequiresPermissionsDesc;
import com.qiaomoyun.entity.pro.yt.ProYtProductLabel;
import com.qiaomoyun.entity.sal.yt.*;
import com.qiaomoyun.info.ResultInfo;
import com.qiaomoyun.manager.sal.yt.SalYtCustomerManager;
import com.qiaomoyun.manager.sal.yt.SalYtCustomerStoreManager;
import com.qiaomoyun.param.sal.yt.SalYtCustomerQueryParams;
import com.qiaomoyun.param.sal.yt.SalYtCustomerUpdateParams;
import com.qiaomoyun.param.sal.yt.CustomerVipParams;
import com.qiaomoyun.service.sal.yt.SalYtCustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 客户管理Controller
 */
@RestController
@RequestMapping("api/sal/yt/customer")
@Validated
@Tag(name = "客户管理",description = "客户管理相关功能")
public class SalYtCustomerController {

    @Autowired
    private SalYtCustomerManager salYtCustomerManager;
    @Autowired
    private SalYtCustomerStoreManager salYtCustomerStoreManager;
    @Autowired
    private SalYtCustomerService salYtCustomerService;

    /**
     * 校验新增客户的联系人信息
     * @param customer 客户信息
     * @return ResultInfo
     */
    @PostMapping("/validateContact")
    @Operation(summary = "校验新增客户的联系人信息")
    public ResultInfo validateContact(@RequestBody SalYtCustomerUpdateParams customer) {
            Map<String, String> result = salYtCustomerManager.validateCustomerContact(customer);
            return ResultInfo.success(result);
    }

    /**
     * 新增客户（包含地址和联系人）
     * @param customer 客户信息
     * @return ResultInfo
     */
    @RequiresPermissions("sal:yt:customer:save")
    @RequiresPermissionsDesc(menu = {"销售管理", "客户管理"}, button = "新增")
    @PostMapping("/save")
    @Operation(summary = "新增客户")
    public ResultInfo save(@RequestBody @Validated SalYtCustomerUpdateParams customer) {
            salYtCustomerManager.saveCustomer(customer);
            return ResultInfo.success("新增成功");
    }

    /**
     * 修改客户主表信息
     * @param customer 客户信息
     * @return ResultInfo
     */
    @RequiresPermissions("sal:yt:customer:update")
    @RequiresPermissionsDesc(menu = {"销售管理", "客户管理"}, button = "修改")
    @PostMapping("/update")
    @Operation(summary = "修改客户主表信息")
    public ResultInfo update(@RequestBody @Validated SalYtCustomer customer) {
        salYtCustomerManager.updateCustomer(customer);
        return ResultInfo.success("修改成功");
    }

    /**
     * 修改客户地址信息
     * @return ResultInfo
     */
    @RequiresPermissions("sal:yt:customer:updateAddress")
    @RequiresPermissionsDesc(menu = {"销售管理", "客户管理"}, button = "编辑地址")
    @PostMapping("/createOrUpdateAddress")
    @Operation(summary = "新增编辑地址信息")
    public ResultInfo createOrUpdateAddress(@RequestBody @Validated SalYtCustomerAddress params) {
        try {
            salYtCustomerManager.updateCustomerAddress(params);
            return ResultInfo.success("修改成功");
        } catch (Exception e) {
            return ResultInfo.error("修改失败：" + e.getMessage());
        }
    }
    
//    @RequiresPermissions("sal:yt:customer:address:delete")
//    @RequiresPermissionsDesc(menu = {"销售管理", "客户列表"}, button = "删除地址")
    @Operation(summary = "删除客户收货地址")
    @GetMapping("/address/delete")
    public Object deleteCustomerAddress( Long addressId) {
        if (ObjectUtil.isEmpty(addressId)) {
            return ResultInfo.error("收货地址ID不能为空");
        }
        salYtCustomerManager.deleteCustomerAddress(addressId);
        return ResultInfo.success();
    }

    /**
     * 修改客户联系人信息
     * @return ResultInfo
     */
    @RequiresPermissions("sal:yt:customer:updateContactPerson")
    @RequiresPermissionsDesc(menu = {"销售管理", "客户管理"}, button = "编辑联系人")
    @PostMapping("/createOrUpdateContactPerson")
    @Operation(summary = "新增编辑联系人信息")
    public ResultInfo createOrUpdateContactPerson(@RequestBody @Validated SalYtContactPerson params) {
        try {
            Long customerId = params.getCustomerId();
            if(customerId==null){
                throw new BizException(ExceptionCodeEnum.Param_Exception);
            }
            salYtCustomerManager.updateCustomerContactPerson(params);
            return ResultInfo.success("修改成功");
        } catch (Exception e) {
            return ResultInfo.error("修改失败：" + e.getMessage());
        }
    }
    
//    @RequiresPermissions("sal:yt:customer:contact:delete")
    @Operation(summary = "删除客户联系人")
    @GetMapping("/contact/delete")
    public Object deleteContactPerson(Long contactId) {
        if (ObjectUtil.isEmpty(contactId)) {
            return ResultInfo.error("联系人ID不能为空");
        }
        salYtCustomerManager.deleteContactPerson(contactId);
        return ResultInfo.success();
    }

    /**
     * 查询客户详情（包含地址、联系人和跟进记录）
     * @param id 客户ID
     * @return ResultInfo
     */
    @RequiresPermissions("sal:yt:customer:detail")
    @RequiresPermissionsDesc(menu = {"销售管理", "客户管理"}, button = "详情")
    @GetMapping("/detail")
    @Operation(summary = "查询客户详情")
    @RequiresDataPermissions(value = "开启数据权限",conditions = {
            @RequiresDataPermissions.Condition(field = "belong_employee_id", logic = RequiresDataPermissions.LogicType.OR),
            @RequiresDataPermissions.Condition(field = "follow_employee_id", logic = RequiresDataPermissions.LogicType.OR)
    })
    public ResultInfo detail(Long id) {
            Map<String, Object> detail = salYtCustomerManager.getCustomerDetail(id);
            if (detail == null) {
                return ResultInfo.error("客户不存在");
            }

            return ResultInfo.success(detail);
    }

//    @RequiresPermissions("sal:yt:customer:addressList")
//    @RequiresPermissionsDesc(menu = {"销售管理", "客户管理"}, button = "查询客户地址")
    @GetMapping("/addressList")
    @Operation(summary = "查询客户地址")
    public ResultInfo addressList(Long id) {
        try {
            if(id==null){
                throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(), "不存在该客户");
            }
            return ResultInfo.success(salYtCustomerManager.getCustomerAddressByCustomerId(id));
        } catch (Exception e) {
            return ResultInfo.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 删除客户
     * @param id 客户ID
     * @return ResultInfo
     */
    @RequiresPermissions("sal:yt:customer:delete")
    @RequiresPermissionsDesc(menu = {"销售管理", "客户管理"}, button = "删除")
    @GetMapping("/delete")
    @Operation(summary = "删除客户")
    public ResultInfo delete(Long id) {
        try {
            salYtCustomerManager.deleteCustomer(id);
            return ResultInfo.success("删除成功");
        } catch (Exception e) {
            return ResultInfo.error("删除失败：" + e.getMessage());
        }
    }

    /**
     * 批量删除客户
     * @param ids 客户ID列表
     * @return ResultInfo
     */
//    @RequiresPermissions("sal:yt:customer:batchDelete")
//    @RequiresPermissionsDesc(menu = {"销售管理", "客户管理"}, button = "批量删除客户")
    @PostMapping("/batchDelete")
    @Operation(summary = "批量删除客户")
    public ResultInfo batchDelete(@RequestBody List<Long> ids) {
        try {
            salYtCustomerManager.deleteBatchCustomer(ids);
            return ResultInfo.success("批量删除成功");
        } catch (Exception e) {
            return ResultInfo.error("批量删除失败：" + e.getMessage());
        }
    }

    /**
     * 查询客户管理
     * @return ResultInfo
     */
    @RequiresPermissions("sal:yt:customer:list")
    @RequiresPermissionsDesc(menu = {"销售管理", "客户管理"}, button = "列表")
    @PostMapping("/list")
    @RequiresDataPermissions(value = "开启数据权限",conditions = {
        @RequiresDataPermissions.Condition(field = "belong_employee_id", logic = RequiresDataPermissions.LogicType.OR),
        @RequiresDataPermissions.Condition(field = "follow_employee_id", logic = RequiresDataPermissions.LogicType.OR)
    })
    @Operation(summary = "查询客户列表")
    public ResultInfo list(@RequestBody SalYtCustomerQueryParams params) {
        return ResultInfo.success(salYtCustomerManager.list(params));
    }

    @RequiresPermissions("sal:yt:customer:list")
//    @RequiresPermissionsDesc(menu = {"销售管理", "客户管理"}, button = "客户下拉框选择列表")
    @PostMapping("/selectList")
    @Operation(summary = "客户下拉框选择列表")
    @RequiresDataPermissions(value = "开启数据权限",conditions = {
            @RequiresDataPermissions.Condition(field = "belong_employee_id", logic = RequiresDataPermissions.LogicType.OR),
            @RequiresDataPermissions.Condition(field = "follow_employee_id", logic = RequiresDataPermissions.LogicType.OR)
    })
    public ResultInfo selectList(@RequestBody SalYtCustomerQueryParams params) {
        return ResultInfo.success(salYtCustomerManager.selectList(params));
    }

    /**
     * 给客户贴标签
     * @param label 标签信息
     * @return
     */
//    @RequiresPermissions("sal:yt:customer:addLabel")
//    @RequiresPermissionsDesc(menu = {"销售管理", "客户管理"}, button = "贴标签")
    @PostMapping("/addLabel")
    @Operation(summary = "给客户贴标签")
    public ResultInfo addLabel(@RequestBody ProYtProductLabel label) {
        try {
            salYtCustomerManager.addLabel(label);
            return ResultInfo.success("标签添加成功");
        } catch (Exception e) {
            return ResultInfo.error("标签添加失败：" + e.getMessage());
        }
    }

    /**
     * 删除客户标签
     * @param labelId 标签ID
     * @return ResultInfo
     */
//    @RequiresPermissions("sal:yt:customer:deleteLabel")
//    @RequiresPermissionsDesc(menu = {"销售管理", "客户管理"}, button = "删除标签")
    @GetMapping("/deleteLabel")
    @Operation(summary = "删除客户标签")
    public ResultInfo deleteLabel(Integer labelId) {
        try {
            salYtCustomerManager.deleteLabel(labelId);
            return ResultInfo.success("标签删除成功");
        } catch (Exception e) {
            return ResultInfo.error("标签删除失败：" + e.getMessage());
        }
    }

    /**
     * 新增或编辑客户跟进记录
     * @param follow 跟进记录信息
     * @return ResultInfo
     */
//    @RequiresPermissions("sal:yt:customer:follow")
//    @RequiresPermissionsDesc(menu = {"销售管理", "客户管理"}, button = "跟进记录")
    @PostMapping("/follow")
    @Operation(summary = "新增或编辑客户跟进记录")
    public ResultInfo follow(@RequestBody SalYtCustomerFollow follow) {
        salYtCustomerManager.saveOrUpdateFollow(follow);
        return ResultInfo.success("操作成功");
    }
    
//    @RequiresPermissions("sal:yt:customer:follow:delete")
    @Operation(summary = "删除客户跟进记录")
    @GetMapping("/follow/delete")
    public Object deleteCustomerFollow(Long followId) {
        if (ObjectUtil.isEmpty(followId)) {
            return ResultInfo.error("跟进记录ID不能为空");
        }
        salYtCustomerManager.deleteCustomerFollow(followId);
        return ResultInfo.success();
    }

    @RequiresPermissions("sal:yt:customer:enableStore")
    @RequiresPermissionsDesc(menu = {"销售管理", "客户管理"}, button = "启动独立仓")
    @GetMapping("/enableStore")
    @Operation(summary = "启用独立仓")
    public ResultInfo enableStore(Long customerId) {
            salYtCustomerManager.enableStore(customerId);
            return ResultInfo.success("发送成功");
    }

    /**
     * 独立仓审核
     */
    @RequiresPermissions("sal:yt:customer:auditStore")
    @RequiresPermissionsDesc(menu = {"销售管理", "客户管理"}, button = "审核独立仓")
    @PostMapping("/auditStore")
    @Operation(summary = "审核独立仓")
    public ResultInfo auditStore(@RequestParam Long customerId, @RequestParam Integer auditResult) {
            salYtCustomerManager.auditStore(customerId, auditResult);
            return ResultInfo.success("审核成功");
    }

//    @RequiresPermissions("sal:yt:customer:specificationList")
//    @RequiresPermissionsDesc(menu = {"销售管理", "客户管理"}, button = "独立仓产品规格")
    @PostMapping("/specificationList")
    @Operation(summary = "独立仓产品规格")
    public ResultInfo specificationList(@RequestBody SalYtCustomerQueryParams params) {

            Long customerId = params.getCustomerId();
            if(customerId == null){
                throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(), "customerId必填");
            }
            return ResultInfo.success(salYtCustomerStoreManager.specificationList(params));
    }

//    @RequiresPermissions("sal:yt:customer:customerStoreWarning")
//    @RequiresPermissionsDesc(menu = {"销售管理", "客户管理"}, button = "设置客户独立仓预警")
    @PostMapping("/setCustomerStoreWarning")
    @Operation(summary = "设置客户独立仓预警")
    public ResultInfo setCustomerStoreWarning(@RequestBody SalYtCustomerStore params) {
        Long customerId = params.getCustomerId();
        if(customerId == null){
            throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(), "customerId必填");
        }
        salYtCustomerStoreManager.setCustomerStoreWarning(params);
        return ResultInfo.success();
    }

//    @RequiresPermissions("sal:yt:customer:customerStoreWarning")
//    @RequiresPermissionsDesc(menu = {"销售管理", "客户管理"}, button = "设置客户独立仓产品预警")
    @PostMapping("/setProductStoreWarning")
    @Operation(summary = "设置客户独立仓产品预警")
    public ResultInfo setProductStoreWarning(@RequestBody SalYtCustomerStore params) {
        Long customerId = params.getCustomerId();
        if(customerId == null){
            throw new BizException(ExceptionCodeEnum.Param_Exception.getCode(), "customerId必填");
        }
        salYtCustomerStoreManager.setProductStoreWarning(params);
        return ResultInfo.success();
    }

//    @RequiresPermissions("sal:yt:customer:updateStoreStatus")
//    @RequiresPermissionsDesc(menu = {"销售管理", "客户管理"}, button = "启用或关闭独立仓")
    @PostMapping("/updateStoreStatus")
    @Operation(summary = "启用或关闭独立仓")
    public ResultInfo updateStoreStatus(@RequestBody SalYtCustomerStore store) {
            // 参数校验
            if (store == null) {
                return ResultInfo.error("请求参数不能为空");
            }
            if (store.getCustomerId() == null) {
                return ResultInfo.error("客户ID不能为空");
            }
            if (store.getSpecificationId() == null) {
                return ResultInfo.error("规格ID不能为空");
            }
            if (store.getStatus() == null) {
                return ResultInfo.error("状态值不能为空");
            }
            if (!"0".equals(store.getStatus()) && !"1".equals(store.getStatus())) {
                return ResultInfo.error("状态值只能是0(关闭)或1(开启)");
            }
            
            salYtCustomerStoreManager.updateStoreStatus(store);
            return ResultInfo.success("操作成功");
    }
    
    /**
     * 查询客户独立仓预警数量
     * @param customerId 客户ID
     * @return ResultInfo
     */
//    @RequiresPermissions("sal:yt:customer:warningCount")
//    @RequiresPermissionsDesc(menu = {"销售管理", "客户管理"}, button = "查询客户独立仓预警数量")
    @GetMapping("/storeWarningCount")
    @Operation(summary = "查询客户独立仓预警数量")
    public ResultInfo getStoreWarningCount(Long customerId) {
            if (customerId == null) {
                return ResultInfo.error("客户ID不能为空");
            }

            return ResultInfo.success(salYtCustomerStoreManager.getWarningCountByCustomerId(customerId));
    }
    
    /**
     * 查询客户独立仓产品预警数量
     * @param customerId 客户ID
     * @param specificationId 规格ID
     * @return ResultInfo
     */
//    @RequiresPermissions("sal:yt:customer:productWarningCount")
//    @RequiresPermissionsDesc(menu = {"销售管理", "客户管理"}, button = "查询客户独立仓产品预警数量")
    @GetMapping("/productStoreWarningCount")
    @Operation(summary = "查询客户独立仓产品预警数量")
    public ResultInfo getProductStoreWarningCount(Long customerId, Long specificationId) {
            if (customerId == null) {
                return ResultInfo.error("客户ID不能为空");
            }
            if (specificationId == null) {
                return ResultInfo.error("规格ID不能为空");
            }
            return ResultInfo.success(salYtCustomerStoreManager.getWarningCountByCustomerIdAndSpecificationId(customerId, specificationId));
    }

    /**
     * 查询VIP客户名单
     * @return ResultInfo 包含VIP客户和非VIP客户名单
     */
//    @RequiresPermissions("sal:yt:customer:vipList")
//    @RequiresPermissionsDesc(menu = {"销售管理", "客户管理"}, button = "VIP客户名单")
    @Operation(summary = "查询VIP客户名单", description = "查询VIP客户名单，返回VIP客户和非VIP客户列表")
    @PostMapping("/vipList")
    public ResultInfo getVipCustomerList(@RequestBody CustomerVipParams params) {
            return ResultInfo.success(salYtCustomerManager.getVipCustomerList(params));
    }
    
    /**
     * 设置VIP客户
     * @param params 设置VIP客户参数
     * @return ResultInfo
     */
//    @RequiresPermissions("sal:yt:customer:setVip")
//    @RequiresPermissionsDesc(menu = {"销售管理", "客户管理"}, button = "设置VIP客户")
    @Operation(summary = "设置VIP客户", description = "设置VIP客户，包括新增和移除VIP客户")
    @PostMapping("/setVip")
    public ResultInfo setVipCustomers(@RequestBody CustomerVipParams params) {
            salYtCustomerManager.setVipCustomers(params.getAddVipIds(), params.getRemoveVipIds());
            return ResultInfo.success();
    }

    //设置客户自动层级
    @RequiresPermissions("sal:yt:customer:setAutoLevel")
    @RequiresPermissionsDesc(menu = {"销售管理", "客户管理"}, button = "设置自动层级")
    @Operation(summary = "设置客户自动层级", description = "设置客户自动层级")
    @PostMapping("/setAutoLevel")
    public ResultInfo setAutoLevel(@RequestBody Map<String, Integer> params) {
        salYtCustomerManager.setAutoLevel(params);
        return ResultInfo.success();
    }

    //获取客户消费趋势
//    @RequiresPermissions("sal:yt:customer:getConsumptionTrends")
//    @RequiresPermissionsDesc(menu = {"销售管理", "客户管理"}, button = "获取客户消费趋势")
    @Operation(summary = "获取客户消费趋势", description = "获取客户消费趋势")
    @PostMapping("/getConsumptionTrends")
    public ResultInfo getConsumptionTrends(@RequestBody SalYtCustomerQueryParams params) {
        return ResultInfo.success(salYtCustomerManager.getConsumptionTrends(params));
    }

    //获取客户消费占比
//    @RequiresPermissions("sal:yt:customer:getConsumptionRatio")
//    @RequiresPermissionsDesc(menu = {"销售管理", "客户管理"}, button = "获取客户消费占比")
    @Operation(summary = "获取客户消费占比", description = "获取客户消费占比")
    @PostMapping("/getConsumptionRatio")
    public ResultInfo getConsumptionRatio(@RequestBody SalYtCustomerQueryParams params) {
        return ResultInfo.success(salYtCustomerManager.getConsumptionRatio(params));
    }

    //独立仓出入库记录
//    @RequiresPermissions("sal:yt:customer:storeRecord")
//    @RequiresPermissionsDesc(menu = {"销售管理", "客户管理"}, button = "独立仓历史流向")
    @Operation(summary = "独立仓历史流向", description = "独立仓历史流向")
    @PostMapping("/storeRecord")
    public ResultInfo storeRecord(@RequestBody SalYtCustomerQueryParams params) {
        Long customerStoreId = params.getCustomerStoreId();
        if(customerStoreId==null){
            throw new BizException(ExceptionCodeEnum.Param_Exception);
        }
        return ResultInfo.success(salYtCustomerService.storeRecord(params));
    }
}