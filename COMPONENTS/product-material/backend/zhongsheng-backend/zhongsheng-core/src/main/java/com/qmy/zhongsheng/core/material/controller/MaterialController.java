package com.qmy.zhongsheng.core.material.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.api.dto.material.MaterialCategoryListQueryDTO;
import com.qmy.zhongsheng.api.dto.material.MaterialCategorySaveDTO;
import com.qmy.zhongsheng.api.dto.material.MaterialListQueryDTO;
import com.qmy.zhongsheng.api.dto.material.MaterialSaveDTO;
import com.qmy.zhongsheng.api.reponse.PageResponse;
import com.qmy.zhongsheng.api.reponse.ResultInfo;
import com.qmy.zhongsheng.api.request.IdRequestParam;
import com.qmy.zhongsheng.core.material.model.vo.MaterialCategoryVO;
import com.qmy.zhongsheng.core.material.model.vo.MaterialSimpleVO;
import com.qmy.zhongsheng.core.material.model.vo.MaterialVO;
import com.qmy.zhongsheng.core.material.service.MaterialCategoryService;
import com.qmy.zhongsheng.core.material.service.MaterialService;
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
 * 材料管理接口（含分类）。
 *
 * @author AI Coding
 */
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/material")
@Tag(name = "材料管理", description = "维护材料分类和材料数据")
public class MaterialController {

    private final MaterialService materialService;
    private final MaterialCategoryService materialCategoryService;

    // ==================== 材料分类 ====================

    /**
     * 保存或更新材料分类。
     */
    @PostMapping("/category/saveOrUpdate")
    @PreAuthorize("@ss.hasPermission(@ss.perm('MATERIAL_CATEGORY_SAVE'))")
    @Operation(summary = "材料分类-保存或更新", description = "无 id 为新增；有 id 为更新；isDeleted=1 时执行软删除")
    public ResultInfo<Long> saveOrUpdateCategory(@Valid @RequestBody MaterialCategorySaveDTO dto) {
        return ResultInfo.success(materialCategoryService.saveOrUpdate(dto));
    }

    /**
     * 查询材料分类列表。
     */
    @PostMapping("/category/list")
    @PreAuthorize("@ss.hasPermission(@ss.perm('MATERIAL_CATEGORY_LIST'))")
    @Operation(summary = "材料分类-查询列表", description = "返回所有材料分类，按排序号升序；支持按名称模糊查询")
    public ResultInfo<List<MaterialCategoryVO>> listCategory(@RequestBody MaterialCategoryListQueryDTO query) {
        return ResultInfo.success(materialCategoryService.listByLikeName(query));
    }

    /**
     * 删除材料分类
     */
    @PostMapping("/category/delete")
    @PreAuthorize("@ss.hasPermission(@ss.perm('MATERIAL_CATEGORY_DELETE'))")
    @Operation(summary = "材料分类-删除", description = "逻辑删除")
    public ResultInfo<Boolean> deleteCategory(@RequestBody IdRequestParam idRequestParam){
        return ResultInfo.success(materialCategoryService.delete(idRequestParam.getId()));
    }


    // ==================== 材料 ====================

    /**
     * 保存或更新材料。
     */
    @PostMapping("/saveOrUpdate")
    @PreAuthorize("@ss.hasPermission(@ss.perm('MATERIAL_SAVE_OR_UPDATE'))")
    @Operation(summary = "材料-保存或更新", description = "无 id 为新增；有 id 为更新；isDeleted=1 时执行软删除")
    public ResultInfo<Long> saveOrUpdate(@Valid @RequestBody MaterialSaveDTO dto) {
        return ResultInfo.success(materialService.saveOrUpdate(dto));
    }

    /**
     * 分页查询材料列表。
     */
    @PostMapping("/page")
    @PreAuthorize("@ss.hasPermission(@ss.perm('MATERIAL_PAGE'))")
    @Operation(summary = "材料-分页查询列表", description = "支持材料分类筛选")
    public ResultInfo<PageResponse<MaterialVO>> page(@RequestBody MaterialListQueryDTO query) {
        Page<MaterialVO> page = materialService.page(query);
        return ResultInfo.success(PageResponse.of(page.getCurrent(), page.getSize(), page.getTotal(), page.getRecords()));
    }

    /**
     * 材料删除
     */
    @PostMapping("/delete")
    @PreAuthorize("@ss.hasPermission(@ss.perm('MATERIAL_DELETE'))")
    @Operation(summary = "材料-删除", description = "逻辑删除")
    public ResultInfo<Boolean> delete(@RequestBody IdRequestParam idRequestParam){
        return ResultInfo.success(materialService.delete(idRequestParam.getId()));
    }

    /**
     * 按分类ID查询材料列表。
     */
    @PostMapping("/listByCategoryId")
    @PreAuthorize("@ss.hasPermission(@ss.perm('MATERIAL_LIST_BY_CATEGORY'))")
    @Operation(summary = "材料-按分类ID查询列表", description = "用于获取下拉选项等场景")
    public ResultInfo<List<MaterialSimpleVO>> listByCategoryId(@RequestBody IdRequestParam idRequestParam) {
        return ResultInfo.success(materialService.listByCategoryId(idRequestParam));
    }
}