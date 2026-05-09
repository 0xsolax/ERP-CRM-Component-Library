package com.qmy.zhongsheng.core.supplier.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.api.dto.supplier.SupplierInquiryHistoryQueryDTO;
import com.qmy.zhongsheng.api.dto.supplier.SupplierInquiryListQueryDTO;
import com.qmy.zhongsheng.api.dto.supplier.SupplierInquirySaveDTO;
import com.qmy.zhongsheng.api.reponse.PageResponse;
import com.qmy.zhongsheng.api.reponse.ResultInfo;
import com.qmy.zhongsheng.api.request.IdRequestParam;
import com.qmy.zhongsheng.core.supplier.model.vo.SupplierInquiryVO;
import com.qmy.zhongsheng.core.supplier.service.SupplierInquiryService;
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

/**
 * 供应商询价台账接口。
 *
 * @author AI Coding
 */
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/supplier/inquiry")
@Tag(name = "供应商询价台账", description = "记录采购前供应商询价历史，不回写采购单或材料主档")
public class SupplierInquiryController {

    private final SupplierInquiryService supplierInquiryService;

    @PostMapping("/saveOrUpdate")
    @PreAuthorize("@ss.hasPermission(@ss.perm('SUPPLIER_INQUIRY_SAVE_OR_UPDATE'))")
    @Operation(summary = "保存或更新供应商询价记录", description = "无 id 为新增；有 id 为更新")
    public ResultInfo<Long> saveOrUpdate(@Valid @RequestBody SupplierInquirySaveDTO dto) {
        return ResultInfo.success(supplierInquiryService.saveOrUpdate(dto));
    }

    @PostMapping("/page")
    @PreAuthorize("@ss.hasPermission(@ss.perm('SUPPLIER_INQUIRY_PAGE'))")
    @Operation(summary = "分页查询供应商询价台账")
    public ResultInfo<PageResponse<SupplierInquiryVO>> page(@RequestBody SupplierInquiryListQueryDTO query) {
        Page<SupplierInquiryVO> page = supplierInquiryService.page(query);
        return ResultInfo.success(PageResponse.of(page.getCurrent(), page.getSize(), page.getTotal(), page.getRecords()));
    }

    @PostMapping("/history")
    @PreAuthorize("@ss.hasPermission(@ss.perm('SUPPLIER_INQUIRY_PAGE'))")
    @Operation(summary = "查询供应商或询价对象历史记录")
    public ResultInfo<PageResponse<SupplierInquiryVO>> history(@RequestBody SupplierInquiryHistoryQueryDTO query) {
        Page<SupplierInquiryVO> page = supplierInquiryService.history(query);
        return ResultInfo.success(PageResponse.of(page.getCurrent(), page.getSize(), page.getTotal(), page.getRecords()));
    }

    @PostMapping("/detail")
    @PreAuthorize("@ss.hasPermission(@ss.perm('SUPPLIER_INQUIRY_DETAIL'))")
    @Operation(summary = "查询供应商询价记录详情")
    public ResultInfo<SupplierInquiryVO> detail(@RequestBody @Valid IdRequestParam idRequestParam) {
        return ResultInfo.success(supplierInquiryService.detail(idRequestParam.getId()));
    }

    @PostMapping("/delete")
    @PreAuthorize("@ss.hasPermission(@ss.perm('SUPPLIER_INQUIRY_DELETE'))")
    @Operation(summary = "删除供应商询价记录", description = "逻辑删除")
    public ResultInfo<Boolean> delete(@RequestBody @Valid IdRequestParam idRequestParam) {
        return ResultInfo.success(supplierInquiryService.delete(idRequestParam.getId()));
    }
}
