package com.qmy.zhongsheng.core.production.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.api.dto.order.OrderProductSnapshotDTO;
import com.qmy.zhongsheng.api.dto.order.OrderProductSnapshotRequestDTO;
import com.qmy.zhongsheng.api.dto.product.ProductListQueryDTO;
import com.qmy.zhongsheng.api.dto.production.ProductionBatchSaveDTO;
import com.qmy.zhongsheng.api.dto.production.ProductionDeliveryDTO;
import com.qmy.zhongsheng.api.dto.production.ProductionGroupListQueryDTO;
import com.qmy.zhongsheng.api.dto.production.ProductionGroupSaveDTO;
import com.qmy.zhongsheng.api.dto.production.ProductionOrderListQueryDTO;
import com.qmy.zhongsheng.api.dto.production.ProductionOrderSaveDTO;
import com.qmy.zhongsheng.api.reponse.PageResponse;
import com.qmy.zhongsheng.api.reponse.ResultInfo;
import com.qmy.zhongsheng.api.request.IdRequestParam;
import com.qmy.zhongsheng.core.order.service.OrderService;
import com.qmy.zhongsheng.core.product.model.vo.ProductVO;
import com.qmy.zhongsheng.core.production.model.vo.ProductionGroupVO;
import com.qmy.zhongsheng.core.production.model.vo.ProductionOrderVO;
import com.qmy.zhongsheng.core.production.service.ProductionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 生产履约接口。
 *
 * @author AI Coding
 */
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/production")
@Tag(name = "生产履约", description = "生产组、订单唯一生产总单、分批安排与交货进度")
public class ProductionController {

    private final ProductionService productionService;

    private final OrderService orderService;

    @PostMapping("/group/saveOrUpdate")
    @PreAuthorize("@ss.hasPermission(@ss.perm('PRODUCTION_GROUP_SAVE_OR_UPDATE'))")
    @Operation(summary = "保存生产组")
    public ResultInfo<Long> saveGroup(@Valid @RequestBody ProductionGroupSaveDTO dto) {
        return ResultInfo.success(productionService.saveGroup(dto));
    }

    @PostMapping("/group/page")
    @PreAuthorize("@ss.hasPermission(@ss.perm('PRODUCTION_GROUP_PAGE'))")
    @Operation(summary = "分页查询生产组")
    public ResultInfo<PageResponse<ProductionGroupVO>> groupPage(@RequestBody(required = false) ProductionGroupListQueryDTO query) {
        Page<ProductionGroupVO> page = productionService.groupPage(query);
        return ResultInfo.success(PageResponse.of(page.getCurrent(), page.getSize(), page.getTotal(), page.getRecords()));
    }

    @PostMapping("/group/options")
    @PreAuthorize("@ss.hasPermission(@ss.perm('PRODUCTION_GROUP_PAGE'))")
    @Operation(summary = "生产组下拉")
    public ResultInfo<List<ProductionGroupVO>> groupOptions(@RequestBody(required = false) ProductionGroupOptionsQuery query) {
        return ResultInfo.success(productionService.groupOptions(query == null ? null : query.getKeyword()));
    }

    @PostMapping("/group/detail")
    @PreAuthorize("@ss.hasPermission(@ss.perm('PRODUCTION_GROUP_DETAIL'))")
    @Operation(summary = "生产组详情")
    public ResultInfo<ProductionGroupVO> groupDetail(@Valid @RequestBody IdRequestParam idRequestParam) {
        return ResultInfo.success(productionService.groupDetail(idRequestParam.getId()));
    }

    @PostMapping("/group/delete")
    @PreAuthorize("@ss.hasPermission(@ss.perm('PRODUCTION_GROUP_DELETE'))")
    @Operation(summary = "删除生产组")
    public ResultInfo<Boolean> deleteGroup(@Valid @RequestBody IdRequestParam idRequestParam) {
        return ResultInfo.success(productionService.deleteGroup(idRequestParam.getId()));
    }

    @PostMapping("/order/page")
    @PreAuthorize("@ss.hasPermission(@ss.perm('PRODUCTION_ORDER_PAGE'))")
    @Operation(summary = "分页查询生产总单")
    public ResultInfo<PageResponse<ProductionOrderVO>> orderPage(@RequestBody(required = false) ProductionOrderListQueryDTO query) {
        Page<ProductionOrderVO> page = productionService.orderPage(query);
        return ResultInfo.success(PageResponse.of(page.getCurrent(), page.getSize(), page.getTotal(), page.getRecords()));
    }

    @PostMapping("/order/saveOrUpdate")
    @PreAuthorize("@ss.hasPermission(@ss.perm('PRODUCTION_ORDER_SAVE_OR_UPDATE'))")
    @Operation(summary = "保存手工生产总单")
    public ResultInfo<Long> saveOrder(@Valid @RequestBody ProductionOrderSaveDTO dto) {
        return ResultInfo.success(productionService.saveOrder(dto));
    }

    @PostMapping("/order/product/page")
    @PreAuthorize("@ss.hasPermission(@ss.perm('PRODUCTION_ORDER_SAVE_OR_UPDATE'))")
    @Operation(summary = "生产单选品分页")
    public ResultInfo<PageResponse<ProductVO>> orderProductPage(@RequestBody(required = false) ProductListQueryDTO query) {
        Page<ProductVO> page = orderService.productPage(query);
        return ResultInfo.success(PageResponse.of(page.getCurrent(), page.getSize(), page.getTotal(), page.getRecords()));
    }

    @PostMapping("/order/productSnapshot")
    @PreAuthorize("@ss.hasPermission(@ss.perm('PRODUCTION_ORDER_SAVE_OR_UPDATE'))")
    @Operation(summary = "生成生产单产品快照")
    public ResultInfo<List<OrderProductSnapshotDTO>> orderProductSnapshot(@Valid @RequestBody OrderProductSnapshotRequestDTO dto) {
        return ResultInfo.success(orderService.buildProductSnapshots(dto));
    }

    @PostMapping("/order/detail")
    @PreAuthorize("@ss.hasPermission(@ss.perm('PRODUCTION_ORDER_DETAIL'))")
    @Operation(summary = "生产总单详情")
    public ResultInfo<ProductionOrderVO> orderDetail(@Valid @RequestBody IdRequestParam idRequestParam) {
        return ResultInfo.success(productionService.orderDetail(idRequestParam.getId()));
    }

    @PostMapping("/order/detailByOrder")
    @PreAuthorize("@ss.hasPermission(@ss.perm('PRODUCTION_ORDER_DETAIL'))")
    @Operation(summary = "按来源订单查询生产总单")
    public ResultInfo<ProductionOrderVO> orderDetailByOrder(@Valid @RequestBody OrderIdRequest request) {
        return ResultInfo.success(productionService.orderDetailByOrderId(request.getOrderId()));
    }

    @PostMapping("/order/batch")
    @PreAuthorize("@ss.hasPermission(@ss.perm('PRODUCTION_ORDER_PROGRESS'))")
    @Operation(summary = "生产总单分批安排")
    public ResultInfo<ProductionOrderVO> arrangeBatches(@Valid @RequestBody ProductionBatchSaveDTO dto) {
        return ResultInfo.success(productionService.arrangeBatches(dto));
    }

    @PostMapping("/order/progress/delivery")
    @PreAuthorize("@ss.hasPermission(@ss.perm('PRODUCTION_ORDER_DELIVERY'))")
    @Operation(summary = "生产产品行本次交货")
    public ResultInfo<ProductionOrderVO> recordDelivery(@Valid @RequestBody ProductionDeliveryDTO dto) {
        return ResultInfo.success(productionService.recordDelivery(dto));
    }

    @PostMapping("/order/export")
    @PreAuthorize("@ss.hasPermission(@ss.perm('PRODUCTION_ORDER_EXPORT'))")
    @Operation(summary = "导出生产总单 Excel")
    public void export(@Valid @RequestBody IdRequestParam idRequestParam, HttpServletResponse response) throws IOException {
        ProductionOrderVO order = productionService.orderDetail(idRequestParam.getId());
        byte[] bytes = productionService.buildExportExcel(idRequestParam.getId());
        writeExcel(response, bytes, "Production_" + order.getCode() + ".xls");
    }

    private void writeExcel(HttpServletResponse response, byte[] bytes, String fileName) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition.attachment().filename(fileName, StandardCharsets.UTF_8).build().toString());
        response.setContentLength(bytes.length);
        response.getOutputStream().write(bytes);
        response.flushBuffer();
    }

    @Data
    public static class ProductionGroupOptionsQuery {
        private String keyword;
    }

    @Data
    public static class OrderIdRequest {
        private Long orderId;
    }
}
