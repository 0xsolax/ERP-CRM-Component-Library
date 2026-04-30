package com.qmy.zhongsheng.core.product.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.api.dto.product.ProductListQueryDTO;
import com.qmy.zhongsheng.api.dto.product.ProductSaveDTO;
import com.qmy.zhongsheng.api.reponse.PageResponse;
import com.qmy.zhongsheng.api.reponse.ResultInfo;
import com.qmy.zhongsheng.api.request.IdRequestParam;
import com.qmy.zhongsheng.core.product.model.vo.ProductDetailVO;
import com.qmy.zhongsheng.core.product.model.vo.ProductVO;
import com.qmy.zhongsheng.core.product.service.ProductService;
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
 * 产品管理接口。
 *
 * @author 单漪甜
 */
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/product")
@Tag(name = "产品管理", description = "维护 product 产品数据")
public class ProductController {

    private final ProductService productService;

    /**
     * 保存或更新产品。
     *
     * @param dto 请求体无 id 为新增；有 id 为更新
     * @return 统一响应，data 为记录主键 id
     */
    @PostMapping("/saveOrUpdate")
    @PreAuthorize("@ss.hasPermission(@ss.perm('PRODUCT_SAVE_OR_UPDATE'))")
    @Operation(summary = "保存或更新产品", description = "无 id 为新增；有 id 为更新")
    public ResultInfo<Long> saveOrUpdate(@Valid @RequestBody ProductSaveDTO dto) {
        return ResultInfo.success(productService.saveOrUpdate(dto));
    }

    /**
     * 分页查询产品列表。
     *
     * @param query 查询请求，支持多维度筛选与关键词模糊搜索
     * @return 统一响应，data 为分页结果
     */
    @PostMapping("/page")
    @PreAuthorize("@ss.hasPermission(@ss.perm('PRODUCT_PAGE'))")
    @Operation(summary = "分页查询产品列表", description = "支持多维度筛选：产品类型、伞架类型/尺寸/功能/材料、面料种类、印刷方式、对齐方式；关键词可匹配产品编号、类型名称、货品描述")
    public ResultInfo<PageResponse<ProductVO>> page(@RequestBody ProductListQueryDTO query) {
        Page<ProductVO> page = productService.page(query);
        return ResultInfo.success(PageResponse.of(page.getCurrent(), page.getSize(), page.getTotal(), page.getRecords()));
    }

    /**
     * 查询产品详情。
     *
     * @param idRequestParam 产品 ID 请求参数
     * @return 统一响应，data 为产品详情（包含伞架、材料、面料、包材、印刷、工价列表）
     */
    @PostMapping("/detail")
    @PreAuthorize("@ss.hasPermission(@ss.perm('PRODUCT_DETAIL'))")
    @Operation(summary = "查询产品详情", description = "查询产品详细信息及所有关联数据")
    public ResultInfo<ProductDetailVO> detail(@RequestBody @Valid IdRequestParam idRequestParam) {
        return ResultInfo.success(productService.detail(idRequestParam.getId()));
    }

    /**
     * 删除产品。
     */
    @PostMapping("/delete")
    @PreAuthorize("@ss.hasPermission(@ss.perm('PRODUCT_DELETE'))")
    @Operation(summary = "删除产品", description = "逻辑删除")
    public ResultInfo<Boolean> delete(@RequestBody IdRequestParam idRequestParam) {
        return ResultInfo.success(productService.delete(idRequestParam.getId()));
    }
}
