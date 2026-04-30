/*
 * @author java_deng
 * @date 2025/11/20 10:20
 * @description 客户规格映射控制器
 */
package com.qiaomoyun.controller.sal.yt;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.qiaomoyun.annotation.RequiresPermissions;
import com.qiaomoyun.annotation.RequiresPermissionsDesc;
import com.qiaomoyun.entity.sal.yt.SalYtCustomerSpecificationComparison;
import com.qiaomoyun.info.ResultInfo;
import com.qiaomoyun.manager.sal.yt.SalYtCustomerSpecificationComparisonManager;
import com.qiaomoyun.param.sal.yt.SalYtCustomerSpecificationComparisonQueryParams;
import com.qiaomoyun.vo.pro.yt.ProYtProductVo;
import com.qiaomoyun.vo.sal.yt.SalYtCustomerSpecificationComparisonVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 客户规格映射控制器
 */
@RestController
@RequestMapping("/api/sal/yt/customerSpecificationComparison")
@Validated
@Tag(name = "销售管理", description = "客户规格映射")
public class SalYtCustomerSpecificationComparisonController {
    
    @Autowired
    private SalYtCustomerSpecificationComparisonManager salYtCustomerSpecificationComparisonManager;
    
    /**
     * 新增或编辑客户规格映射
     */
//    @RequiresPermissions("sal:yt:customerSpecificationComparison:create")
//    @RequiresPermissionsDesc(menu = {"销售管理", "客户规格映射"}, button = "新增或编辑")
    @Operation(summary = "新增或编辑")
    @PostMapping("/createOrUpdate")
    public ResultInfo<String> createOrUpdate(@RequestBody @Validated SalYtCustomerSpecificationComparison entity) {
        try {
            // 验证必填字段
            if (entity.getCustomerId() == null) {
                return ResultInfo.error("客户ID不能为空");
            }
            if (entity.getSpecification() == null || entity.getSpecification().trim().isEmpty()) {
                return ResultInfo.error("规格不能为空");
            }
            if (entity.getCustomerSpecification() == null || entity.getCustomerSpecification().trim().isEmpty()) {
                return ResultInfo.error("客户规格不能为空");
            }
            
            salYtCustomerSpecificationComparisonManager.createOrUpdate(entity);
            return ResultInfo.success("操作成功");
        } catch (RuntimeException e) {
            return ResultInfo.error(e.getMessage());
        }
    }

//    @RequiresPermissions("sal:yt:customerSpecificationComparison:createItemNumber")
//    @RequiresPermissionsDesc(menu = {"销售管理", "客户规格映射"}, button = "新增或编辑货号")
    @Operation(summary = "新增或编辑货号")
    @PostMapping("/createOrUpdateItemNumber")
    public ResultInfo<String> createOrUpdateItemNumber(@RequestBody @Validated SalYtCustomerSpecificationComparison entity) {
        try {
            // 验证必填字段
            if (entity.getCustomerId() == null) {
                return ResultInfo.error("客户ID不能为空");
            }
            if (entity.getSpecificationId() == null) {
                return ResultInfo.error("规格id不能为空");
            }
            if (entity.getItemNumber() == null ) {
                return ResultInfo.error("货号不能为空");
            }

            salYtCustomerSpecificationComparisonManager.createOrUpdateItemNumber(entity);
            return ResultInfo.success("操作成功");
        } catch (RuntimeException e) {
            return ResultInfo.error(e.getMessage());
        }
    }
    
    /**
     * 删除客户规格映射
     */
//    @RequiresPermissions("sal:yt:customerSpecificationComparison:delete")
//    @RequiresPermissionsDesc(menu = {"销售管理", "客户规格映射"}, button = "删除")
    @Operation(summary = "删除")
    @PostMapping("/delete")
    public ResultInfo<String> delete(@RequestParam Long id) {
        if (id == null) {
            return ResultInfo.error("ID不能为空");
        }
        salYtCustomerSpecificationComparisonManager.deleteById(id);
        return ResultInfo.success("删除成功");
    }
    
    /**
     * 获取客户规格映射详情
     */
//    @RequiresPermissions("sal:yt:customerSpecificationComparison:read")
//    @RequiresPermissionsDesc(menu = {"销售管理", "客户规格映射"}, button = "详情")
    @Operation(summary = "详情")
    @GetMapping("/detail")
    public ResultInfo<SalYtCustomerSpecificationComparison> detail(@RequestParam Long id) {
        if (id == null) {
            return ResultInfo.error("ID不能为空");
        }
        SalYtCustomerSpecificationComparison detail = salYtCustomerSpecificationComparisonManager.getById(id);
        if (detail == null) {
            return ResultInfo.error("数据不存在");
        }
        return ResultInfo.success(detail);
    }
    
    /**
     * 查询客户规格映射列表
     */
//    @RequiresPermissions("sal:yt:customerSpecificationComparison:list")
//    @RequiresPermissionsDesc(menu = {"销售管理", "客户规格映射"}, button = "规格列表")
    @Operation(summary = "列表")
    @PostMapping("/list")
    public ResultInfo<List<SalYtCustomerSpecificationComparison>> list(@RequestBody(required = false) SalYtCustomerSpecificationComparisonQueryParams queryParam) {
        // 如果没有传入查询参数，创建一个空的查询参数对象
        if (queryParam == null) {
            queryParam = new SalYtCustomerSpecificationComparisonQueryParams();
        }
        // 确保客户ID不为空，因为客户规格映射必须按客户ID区分
        if (queryParam.getCustomerId() == null) {
            return ResultInfo.error("客户ID不能为空");
        }
        List<SalYtCustomerSpecificationComparison> result = salYtCustomerSpecificationComparisonManager.list(queryParam);
        return ResultInfo.success(result);
    }
    
    /**
     * 检查规格是否已存在
     */
    @Operation(summary = "检查规格是否已存在")
    @GetMapping("/checkSpecification")
    public ResultInfo<Boolean> checkSpecification(@RequestParam Long customerId, @RequestParam String specification, @RequestParam(required = false) Long excludeId) {
        if (customerId == null) {
            return ResultInfo.error("客户ID不能为空");
        }
        boolean exists = salYtCustomerSpecificationComparisonManager.existsByName(customerId, specification, excludeId);
        return ResultInfo.success(exists);
    }
    
    /**
     * 导出客户规格映射表
     */
//    @RequiresPermissions("sal:yt:customerSpecificationComparison:export")
//    @RequiresPermissionsDesc(menu = {"销售管理", "客户规格映射"}, button = "导出")
    @Operation(summary = "导出")
    @GetMapping("/export")
    public void export(@RequestParam Long customerId, HttpServletResponse response) throws Exception {
        if (customerId == null) {
            response.setContentType("application/json");
            response.getWriter().write("{\"code\":400,\"message\":\"客户ID不能为空\"}");
            return;
        }
        
        // 获取指定客户的规格映射数据
        SalYtCustomerSpecificationComparisonQueryParams queryParams = new SalYtCustomerSpecificationComparisonQueryParams();
        queryParams.setCustomerId(customerId);
        List<SalYtCustomerSpecificationComparison> list = salYtCustomerSpecificationComparisonManager.mappedItems(queryParams);
        
        // 创建ExcelWriter
        ExcelWriter writer = ExcelUtil.getWriter();
        
        // 设置表头别名
        writer.addHeaderAlias("specification", "规格");
        writer.addHeaderAlias("customerSpecification", "客户规格");
        
        // 设置只输出设置了别名的字段
        writer.setOnlyAlias(true);
        
        // 写入数据
        if (list != null && !list.isEmpty()) {
            writer.write(list, true);
        } else {
            // 当列表为空时，创建一个空的对象来确保表头被生成
            List<SalYtCustomerSpecificationComparison> emptyList = new ArrayList<>();
            emptyList.add(new SalYtCustomerSpecificationComparison());
            writer.write(emptyList, true);
            // 删除空数据行
            writer.getSheet().removeRow(writer.getSheet().getRow(1));
        }
        
        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("客户规格映射表", "UTF-8") + ".xlsx";
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        
        // 输出Excel
        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        writer.close();
        out.close();
    }
    
    /**
     * 导入客户规格映射表
     */
//    @RequiresPermissions("sal:yt:customerSpecificationComparison:import")
//    @RequiresPermissionsDesc(menu = {"销售管理", "客户规格映射"}, button = "导入")
    @Operation(summary = "导入")
    @PostMapping("/import")
    public ResultInfo<String> importData(@RequestParam Long customerId, MultipartFile file) throws IOException {
        if (customerId == null) {
            return ResultInfo.error("客户ID不能为空");
        }
        if (file == null || file.isEmpty()) {
            return ResultInfo.error("文件不能为空");
        }
        
        // 读取Excel文件
        try (InputStream inputStream = file.getInputStream();
             ExcelReader reader = ExcelUtil.getReader(inputStream)) {
            reader.addHeaderAlias("规格", "specification");
            reader.addHeaderAlias("客户规格", "customerSpecification");
            // 读取数据，忽略标题行
            List<SalYtCustomerSpecificationComparison> dataList = reader.readAll(SalYtCustomerSpecificationComparison.class);
            
            // 检查数据
            if (dataList.isEmpty()) {
                return ResultInfo.error("文件中没有数据");
            }
            
            // 设置客户ID
            for (SalYtCustomerSpecificationComparison entity : dataList) {
                entity.setCustomerId(customerId);
            }
            
            // 批量导入数据
            salYtCustomerSpecificationComparisonManager.batchImport(dataList);
            
            return ResultInfo.success("导入成功，共导入" + dataList.size() + "条数据");
        } catch (Exception e) {
            return ResultInfo.error("导入失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取客户规格列表
     */
 //   @RequiresPermissions("sal:yt:customerSpecificationComparison:productList")
 //   @RequiresPermissionsDesc(menu = {"销售管理", "客户规格映射"}, button = "产品规格列表")
    @Operation(summary = "获取产品规格列表")
    @PostMapping("/getCustomerSpecification")
    public ResultInfo<Object> getCustomerSpecification(@RequestBody SalYtCustomerSpecificationComparisonQueryParams queryParams) {
        if (queryParams == null || queryParams.getCustomerId() == null) {
            return ResultInfo.error("客户ID不能为空");
        }
        return ResultInfo.success(salYtCustomerSpecificationComparisonManager.getCustomerSpecification(queryParams));
    }
}