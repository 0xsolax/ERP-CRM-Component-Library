package com.qmy.zhongsheng.core.supplier.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 供应商视图对象。
 *
 * @author AI Coding
 */
@Data
@Schema(description = "供应商视图对象")
public class SupplierVO {

    @Schema(description = "供应商 ID")
    private Long id;

    @Schema(description = "供应商编号")
    private String code;

    @Schema(description = "供应商名称")
    private String name;

    @Schema(description = "联系人")
    private String contact;

    @Schema(description = "电话")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "状态：1 启用，0 停用")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
