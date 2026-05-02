/*
 * @author java_deng
 * @date 2025/11/11
 * @description 采购相关控制器
 */
package com.qiaomoyun.controller.pur.yt;

import com.qiaomoyun.Exception.BizException;
import com.qiaomoyun.annotation.RequiresPermissions;
import com.qiaomoyun.annotation.RequiresPermissionsDesc;
import com.qiaomoyun.entity.pro.yt.ProYtProductLabel;
import com.qiaomoyun.entity.pur.yt.PurYtSupplier;
import com.qiaomoyun.entity.pro.yt.ProYtProductSpecificationSupplier;
import com.qiaomoyun.entity.sal.yt.SalYtContactPerson;
import com.qiaomoyun.info.PageResultInfo;
import com.qiaomoyun.info.ResultInfo;
import com.qiaomoyun.manager.pur.yt.PurYtSupplierFollowManager;
import com.qiaomoyun.manager.pur.yt.PurYtSupplierManager;
import com.qiaomoyun.manager.pur.yt.PurYtPurchaseManager;
import com.qiaomoyun.mapper.pro.yt.ProYtProductSpecificationSupplierMapper;
import com.qiaomoyun.param.pur.yt.PurYtPurchaseQueryParams;
import com.qiaomoyun.entity.pur.yt.PurYtSupplierFollow;
import com.qiaomoyun.param.pur.yt.PurYtSupplierQueryParams;
import com.qiaomoyun.param.pur.yt.PurYtSupplierSpecificationQueryParams;
import com.qiaomoyun.param.pur.yt.PurYtSupplierUpdateParams;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/pur/yt/supplier")
@Tag(name = "采购管理", description = "供应商管理相关功能")
@Validated
public class PurYtSupplierController {

    @Autowired
    private PurYtSupplierManager purYtSupplierManager;

    @Autowired
    private PurYtSupplierFollowManager purYtSupplierFollowManager;

    @Autowired
    private ProYtProductSpecificationSupplierMapper proYtProductSpecificationSupplierMapper;

    @Autowired
    private PurYtPurchaseManager purYtPurchaseManager;


    /**
     * 获取供应商下拉框列表
     */
    @GetMapping("/supplierSelect")
//    @RequiresPermissions("pur:yt:purchaseSupplier:select")
//    @RequiresPermissionsDesc(menu = {"采购管理", "供应商"}, button = "供应商下拉选择列表")
    @Operation(summary = "供应商下拉选择列表")
    public ResultInfo<List<PurYtSupplier>> getSupplierDropdown() {
        return ResultInfo.success(purYtSupplierManager.getSupplierDropdown());
    }

    /**
     * 新增供应商
     */
    @PostMapping("/add")
    @RequiresPermissionsDesc(menu = {"采购管理", "供应商列表"}, button = "新增")
    @RequiresPermissions("pur:yt:purchaseSupplier:add")
    @Operation(summary = "新增供应商")
    public ResultInfo<Boolean> addSupplier(@Valid @RequestBody PurYtSupplierUpdateParams params) {
        purYtSupplierManager.addSupplier(params);
        return ResultInfo.success(true);
    }

    @PostMapping("/list")
    @RequiresPermissionsDesc(menu = {"采购管理", "供应商列表"}, button = "列表")
    @RequiresPermissions("pur:yt:purchaseSupplier:list")
    @Operation(summary = "供应商列表")
    public ResultInfo list(@Valid @RequestBody PurYtSupplierQueryParams params) {
        return ResultInfo.success(purYtSupplierManager.list(params));
    }

    /**
     * 新增供应商跟进记录
     */
    @PostMapping("/follow/createOrUpdate")
//    @RequiresPermissionsDesc(menu = {"采购管理", "供应商列表"}, button = "新增跟进")
//    @RequiresPermissions("pur:yt:purchaseSupplier:follow:createOrUpdate")
    @Operation(summary = "新增供应商跟进记录")
    public ResultInfo<Boolean> createOrUpdate(@Valid @RequestBody PurYtSupplierFollow follow) {
        Long id = follow.getId();
        if(id == null){
            purYtSupplierFollowManager.addFollow(follow);
        }else {
            purYtSupplierFollowManager.updateFollow(follow);
        }
        return ResultInfo.success(true);
    }

    /**
     * 删除供应商跟进记录
     */
    @GetMapping("/follow/delete")
//    @RequiresPermissionsDesc(menu = {"采购管理", "供应商跟进"}, button = "删除跟进")
//    @RequiresPermissions("pur:yt:purchaseSupplier:follow:delete")
    @Operation(summary = "删除供应商跟进记录")
    public ResultInfo<Boolean> deleteFollow(@RequestParam Long id) {
        purYtSupplierFollowManager.deleteFollow(id);
        return ResultInfo.success(true);
    }

    /**
     * 更新供应商信息
     */
    @PostMapping("/update")
    @RequiresPermissionsDesc(menu = {"采购管理", "供应商列表"}, button = "更新")
    @RequiresPermissions("pur:yt:purchaseSupplier:update")
    @Operation(summary = "更新供应商信息")
    public ResultInfo<Boolean> updateSupplier(@RequestBody PurYtSupplierUpdateParams params) {
            purYtSupplierManager.updateSupplier(params);
            return ResultInfo.success(true);
    }

    /**
     * 获取供应商详情
     */
    @GetMapping("/detail")
    @RequiresPermissionsDesc(menu = {"采购管理", "供应商列表"}, button = "详情")
    @RequiresPermissions("pur:yt:purchaseSupplier:detail")
    @Operation(summary = "获取供应商详情")
    public ResultInfo<PurYtSupplier> getSupplierDetail(@RequestParam Long id) {
        if (id == null) {
            return ResultInfo.error("供应商ID不能为空");
        }
        PurYtSupplier supplier = purYtSupplierManager.detail(id);
        if (supplier == null) {
            return ResultInfo.error("供应商不存在");
        }
        return ResultInfo.success(supplier);
    }

    /**
     * 新增供应商标签
     */
    @PostMapping("/label/add")
//    @RequiresPermissionsDesc(menu = {"采购管理", "供应商列表"}, button = "新增标签")
//    @RequiresPermissions("pur:yt:purchaseSupplier:label:add")
    @Operation(summary = "新增供应商标签")
    public ResultInfo<Boolean> addSupplierLabel(@Valid @RequestBody ProYtProductLabel label) {
        try {
            if (label.getMasterId() == null) {
                return ResultInfo.error("供应商ID不能为空");
            }
            purYtSupplierManager.addSupplierLabel(label);
            return ResultInfo.success(true);
        } catch (Exception e) {
            return ResultInfo.error("新增标签失败：" + e.getMessage());
        }
    }

    /**
     * 批量新增供应商标签
     */
    @PostMapping("/label/batchAdd")
//    @RequiresPermissionsDesc(menu = {"采购管理", "供应商"}, button = "批量新增标签")
//    @RequiresPermissions("pur:yt:purchaseSupplier:label:batchAdd")
    @Operation(summary = "批量新增供应商标签")
    public ResultInfo<Boolean> batchAddSupplierLabels(@RequestParam Long supplierId, @RequestBody List<ProYtProductLabel> labels) {
        try {
            if (supplierId == null) {
                return ResultInfo.error("供应商ID不能为空");
            }
            if (labels == null || labels.isEmpty()) {
                return ResultInfo.error("标签列表不能为空");
            }
            purYtSupplierManager.batchAddSupplierLabels(supplierId, labels);
            return ResultInfo.success(true);
        } catch (Exception e) {
            return ResultInfo.error("批量新增标签失败：" + e.getMessage());
        }
    }

    /**
     * 删除供应商标签
     */
    @PostMapping("/label/delete")
//    @RequiresPermissionsDesc(menu = {"采购管理", "供应商"}, button = "删除标签")
//    @RequiresPermissions("pur:yt:purchaseSupplier:label:delete")
    @Operation(summary = "删除供应商标签")
    public ResultInfo<Boolean> deleteSupplierLabel(@RequestParam Integer labelId) {
            if (labelId == null) {
                return ResultInfo.error("标签ID不能为空");
            }
            purYtSupplierManager.deleteSupplierLabel(labelId);
            return ResultInfo.success(true);
    }

    /**
     * 获取供应商标签列表
     */
    @GetMapping("/label/list")
//    @RequiresPermissionsDesc(menu = {"采购管理", "供应商"}, button = "标签列表")
//    @RequiresPermissions("pur:yt:purchaseSupplier:label:list")
    @Operation(summary = "获取供应商标签列表")
    public ResultInfo<List<ProYtProductLabel>> getSupplierLabels(@RequestParam Long supplierId) {
        try {
            if (supplierId == null) {
                return ResultInfo.error("供应商ID不能为空");
            }
            List<ProYtProductLabel> labels = purYtSupplierManager.getSupplierLabels(supplierId);
            return ResultInfo.success(labels);
        } catch (Exception e) {
            return ResultInfo.error("获取标签列表失败：" + e.getMessage());
        }
    }

    /**
     * 新增/编辑供应商联系人
     */
    @PostMapping("/contact/saveOrUpdate")
//    @RequiresPermissionsDesc(menu = {"采购管理", "供应商"}, button = "保存联系人")
//    @RequiresPermissions("pur:yt:purchaseSupplier:contact:saveOrUpdate")
    @Operation(summary = "新增/编辑供应商联系人")
    public ResultInfo<Boolean> saveOrUpdateContact(@Valid @RequestBody SalYtContactPerson contactPerson) {
        try {
            if (contactPerson.getSupplierId() == null) {
                return ResultInfo.error("供应商ID不能为空");
            }
            // 编辑联系人
            purYtSupplierManager.saveOrUpdateSupplierContactPerson(contactPerson);


            return ResultInfo.success(true);
        } catch (IllegalArgumentException e) {
            return ResultInfo.error(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.error("保存联系人失败：" + e.getMessage());
        }
    }
    @GetMapping("/contact/delete")
//    @RequiresPermissionsDesc(menu = {"采购管理", "供应商"}, button = "删除联系人")
//    @RequiresPermissions("pur:yt:purchaseSupplier:contact:delete")
    @Operation(summary = "删除联系人")
    public ResultInfo<Boolean> deleteContact(Long id) {
            if (id == null) {
                return ResultInfo.error("联系人ID不能为空");
            }
            purYtSupplierManager.deleteContactByContactId(id);
            return ResultInfo.success(true);
    }

    /**
     * 获取供应商联系人列表
     */
    @GetMapping("/contact/list")
//    @RequiresPermissionsDesc(menu = {"采购管理", "供应商"}, button = "联系人列表")
//    @RequiresPermissions("pur:yt:purchaseSupplier:contact:list")
    @Operation(summary = "获取供应商联系人列表")
    public ResultInfo<List<SalYtContactPerson>> getContactList(@RequestParam Long supplierId) {
        try {
            if (supplierId == null) {
                return ResultInfo.error("供应商ID不能为空");
            }
            List<SalYtContactPerson> contacts = purYtSupplierManager.getSupplierContactPersons(supplierId);
            return ResultInfo.success(contacts);
        } catch (Exception e) {
            return ResultInfo.error("获取联系人列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取联系人详情
     */
    @GetMapping("/contact/detail")
//    @RequiresPermissionsDesc(menu = {"采购管理", "供应商"}, button = "联系人详情")
//    @RequiresPermissions("pur:yt:purchaseSupplier:contact:detail")
    @Operation(summary = "获取联系人详情")
    public ResultInfo<SalYtContactPerson> getContactDetail(@RequestParam Long contactPersonId) {
        try {
            if (contactPersonId == null) {
                return ResultInfo.error("联系人ID不能为空");
            }
            SalYtContactPerson contact = purYtSupplierManager.getContactPersonById(contactPersonId);
            if (contact == null) {
                return ResultInfo.error("联系人不存在");
            }
            return ResultInfo.success(contact);
        } catch (Exception e) {
            return ResultInfo.error("获取联系人详情失败：" + e.getMessage());
        }
    }

    /**
     * 供应商产品信息对照表接口
     * @return 供应商产品信息对照表
     */
    @PostMapping("/supplierSpecification")
//    @RequiresPermissionsDesc(menu = {"采购管理", "供应商"}, button = "供应商产品信息对照")
//    @RequiresPermissions("pur:yt:purchaseSupplier:supplierSpecification")
    @Operation(summary = "供应商产品信息对照")
    public ResultInfo<PageResultInfo<ProYtProductSpecificationSupplier>> getProductSpecificationSupplier(
            @RequestBody PurYtSupplierSpecificationQueryParams params) {
        return ResultInfo.success(purYtSupplierManager.specificationList(params));
    }

    /**
     * 根据供应商规格id查找详情
     * @param id 供应商规格ID
     * @return 供应商规格详情
     */
    @GetMapping("/supplierSpecification/detail")
//    @RequiresPermissionsDesc(menu = {"采购管理", "供应商"}, button = "供应商规格详情")
//    @RequiresPermissions("pur:yt:purchaseSupplier:supplierSpecification:detail")
    @Operation(summary = "根据供应商规格id查找详情")
    public ResultInfo<ProYtProductSpecificationSupplier> getSpecificationSupplierDetail(@RequestParam Long id) {
        if (id == null) {
            return ResultInfo.error("供应商规格ID不能为空");
        }
        ProYtProductSpecificationSupplier supplier = purYtSupplierManager.getSpecificationSupplierDetail(id);
        if (supplier == null) {
            return ResultInfo.error("供应商规格不存在");
        }
        return ResultInfo.success(supplier);
    }

    /**
     * 编辑供应商规格
     * @param supplier 供应商规格信息
     * @return 编辑结果
     */
    @PostMapping("/supplierSpecification/update")
//    @RequiresPermissionsDesc(menu = {"采购管理", "供应商"}, button = "编辑供应商规格")
//    @RequiresPermissions("pur:yt:purchaseSupplier:supplierSpecification:update")
    @Operation(summary = "编辑供应商规格")
    public ResultInfo<Boolean> updateSpecificationSupplier(@RequestBody ProYtProductSpecificationSupplier supplier) {
        try {
            purYtSupplierManager.updateSpecificationSupplier(supplier);
            return ResultInfo.success(true);
        } catch (BizException e) {
            return ResultInfo.error(e.getMessage());
        } catch (Exception e) {
            return ResultInfo.error("编辑供应商规格失败：" + e.getMessage());
        }
    }

    /**
     * 获取采购趋势
     */
    @RequiresPermissions("pur:yt:purchaseSupplier:getPurchaseTrends")
//    @RequiresPermissionsDesc(menu = {"采购管理", "供应商"}, button = "获取采购趋势")
//    @Operation(summary = "获取采购趋势", description = "获取采购趋势")
    @PostMapping("/getPurchaseTrends")
    public ResultInfo getPurchaseTrends(@RequestBody PurYtPurchaseQueryParams params) {
        return ResultInfo.success(purYtPurchaseManager.getPurchaseTrends(params));
    }

    /**
     * 获取采购占比
     */
//    @RequiresPermissions("pur:yt:purchaseSupplier:getPurchaseRatio")
//    @RequiresPermissionsDesc(menu = {"采购管理", "供应商"}, button = "获取采购占比")
    @Operation(summary = "获取采购占比", description = "获取采购占比")
    @PostMapping("/getPurchaseRatio")
    public ResultInfo getPurchaseRatio(@RequestBody PurYtPurchaseQueryParams params) {
        return ResultInfo.success(purYtPurchaseManager.getPurchaseRatio(params));
    }
}