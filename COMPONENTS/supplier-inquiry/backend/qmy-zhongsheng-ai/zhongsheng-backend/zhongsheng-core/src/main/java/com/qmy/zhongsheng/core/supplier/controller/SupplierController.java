package com.qmy.zhongsheng.core.supplier.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.api.dto.supplier.SupplierListQueryDTO;
import com.qmy.zhongsheng.api.dto.supplier.SupplierSaveDTO;
import com.qmy.zhongsheng.api.reponse.PageResponse;
import com.qmy.zhongsheng.api.reponse.ResultInfo;
import com.qmy.zhongsheng.api.request.IdRequestParam;
import com.qmy.zhongsheng.core.supplier.model.vo.SupplierOptionVO;
import com.qmy.zhongsheng.core.supplier.model.vo.SupplierVO;
import com.qmy.zhongsheng.core.supplier.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 供应商管理接口。
 *
 * @author AI Coding
 */
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/supplier")
@Tag(name = "供应商管理", description = "维护供应商主档，为采购链路提供下拉来源")
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping("/saveOrUpdate")
    @PreAuthorize("@ss.hasPermission(@ss.perm('SUPPLIER_SAVE_OR_UPDATE'))")
    @Operation(summary = "保存或更新供应商", description = "无 id 为新增；有 id 为更新")
    public ResultInfo<Long> saveOrUpdate(@Valid @RequestBody SupplierSaveDTO dto) {
        return ResultInfo.success(supplierService.saveOrUpdate(dto));
    }

    @PostMapping("/page")
    @PreAuthorize("@ss.hasPermission(@ss.perm('SUPPLIER_PAGE'))")
    @Operation(summary = "分页查询供应商", description = "支持供应商名称、编号、联系人模糊搜索")
    public ResultInfo<PageResponse<SupplierVO>> page(@RequestBody SupplierListQueryDTO query) {
        Page<SupplierVO> page = supplierService.page(query);
        return ResultInfo.success(PageResponse.of(page.getCurrent(), page.getSize(), page.getTotal(), page.getRecords()));
    }

    @PostMapping("/list")
    @PreAuthorize("@ss.hasPermission(@ss.perm('SUPPLIER_PAGE'))")
    @Operation(summary = "供应商下拉列表", description = "用于采购供应商下拉")
    public ResultInfo<List<SupplierOptionVO>> list(@RequestBody(required = false) SupplierListQueryDTO query) {
        return ResultInfo.success(supplierService.listOptions(query));
    }

    @PostMapping("/detail")
    @PreAuthorize("@ss.hasPermission(@ss.perm('SUPPLIER_DETAIL'))")
    @Operation(summary = "查询供应商详情")
    public ResultInfo<SupplierVO> detail(@RequestBody @Valid IdRequestParam idRequestParam) {
        return ResultInfo.success(supplierService.detail(idRequestParam.getId()));
    }

    @PostMapping("/delete")
    @PreAuthorize("@ss.hasPermission(@ss.perm('SUPPLIER_DELETE'))")
    @Operation(summary = "删除供应商", description = "逻辑删除")
    public ResultInfo<Boolean> delete(@RequestBody @Valid IdRequestParam idRequestParam) {
        return ResultInfo.success(supplierService.delete(idRequestParam.getId()));
    }
}
