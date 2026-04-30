package com.qmy.project.core.base.controller;

import com.qmy.project.api.dto.base.BaseDataListQueryDTO;
import com.qmy.project.api.dto.base.BaseDataQueryByNodeKeyDTO;
import com.qmy.project.api.dto.base.BaseDataSaveDTO;
import com.qmy.project.api.dto.base.BaseTreeNodeListQueryDTO;
import com.qmy.project.api.reponse.ResultInfo;
import com.qmy.project.api.request.IdRequestParam;
import com.qmy.project.core.base.model.vo.BaseDataVO;
import com.qmy.project.core.base.model.vo.BaseTreeNodeVO;
import com.qmy.project.core.base.service.BaseDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 通用基础数据（树节点 id / 多值 / 扩展 JSON）维护接口。
 *
 * @author AI Coding
 */
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/baseData")
@Tag(name = "基础数据", description = "维护 base_data 通用基础数据")
public class BaseDataController {


    private final BaseDataService baseDataService;

    /**
     * 单条保存或更新。
     *
     * @param dto 请求体无 id 为新增，有 id 为更新（仅非 null 字段覆盖）
     * @return 统一响应，data 为记录主键 id
     */
    @PostMapping("/saveOrUpdate")
    @Operation(summary = "单条保存或更新", description = "请求体无 id 为新增；有 id 为更新（仅非 null 字段覆盖）；data 为记录主键 id")
    public ResultInfo<Long> saveOrUpdate(@Valid @RequestBody BaseDataSaveDTO dto) {
        return ResultInfo.success(baseDataService.saveOrUpdate(dto));
    }

    /**
     * 删除单条记录
     *
     * @param idRequestParam 请求参数
     * @return 删除结果
     */
    @PostMapping("/delete")
    @Operation(summary = "删除单条记录", description = "当数据未被引用的时候才允许被删除")
    public ResultInfo<Boolean> delete(@RequestBody @Valid IdRequestParam idRequestParam) {
        return ResultInfo.success(baseDataService.delete(idRequestParam.getId()));
    }

    /**
     * 列表查询。
     *
     * @param query 基础树节点 id 集合；可空表示不加对应条件
     * @return 统一响应，data 为基础数据列表
     */
    @PostMapping("/list")
    @Operation(summary = "列表查询", description = "按基础树节点 id 集合过滤；请求体可空表示不加条件")
    public ResultInfo<List<BaseDataVO>> list(@RequestBody(required = false) BaseDataListQueryDTO query) {
        return ResultInfo.success(baseDataService.list(query));
    }

    /**
     * 通过节点key查询列表。
     *
     * @param dto 节点种子标识列表
     * @return 统一响应，data 为 nodeKey -> 基础数据列表映射
     */
    @PostMapping("/listByNodeKey")
    @Operation(summary = "通过节点查询列表", description = "传入 nodeKey，返回该节点下配置的所有基础数据，用于下拉框等场景")
    public ResultInfo<List<BaseDataVO>> listByNodeKey(@RequestBody BaseDataQueryByNodeKeyDTO dto) {
        return ResultInfo.success(baseDataService.listByNodeKey(dto.getNodeKey()));
    }

    /**
     * 基础树节点列表（已配置项）。
     *
     * @param query 查询参数，bizType 为空时返回全部节点
     * @return 统一响应，data 为树节点列表
     */
    @PostMapping("/treeNodeList")
    @Operation(summary = "基础树节点列表", description = "返回已配置的基础树节点；传入 bizType 可按业务类型过滤")
    public ResultInfo<List<BaseTreeNodeVO>> treeNodeList(@RequestBody(required = false) BaseTreeNodeListQueryDTO query) {
        String bizType = query != null ? query.getBizType() : null;
        return ResultInfo.success(baseDataService.listTreeNodesByBizType(bizType));
    }
}
