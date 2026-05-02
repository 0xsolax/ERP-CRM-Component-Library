package com.qiaomoyun.param.sto.yt;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.Date;
import java.util.List;

/**
 * 出入库单新增参数类
 */
@Data
public class StoYtStoreOrderAddParams {
    private Long id;

    /**
     * 订单编号
     */
    @NotBlank(message = "订单编号不能为空")
    private String code;

    /**
     * 订单类型
     */
    @NotBlank(message = "订单类型不能为空")
    private String type;

    /**
     * 产品ID
     */
    @NotNull(message = "产品ID不能为空")
    private Long productId;

    /**
     * 规格ID
     */
    @NotNull(message = "规格ID不能为空")
    private Long specificationId;

    /**
     * 供应商ID
     */
    private Long supplierId;

    /**
     * 采购单ID
     */
    private Long purchaseId;

    /**
     * 采购单明细ID
     */
    private Long purchaseItemId;

    /**
     * 位置ID
     */
    private Long locationId;

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 总数
     */
    @NotNull(message = "总数不能为空")
    private Integer totalNumber;

    /**
     * 入库数
     */
    @NotNull(message = "入库数不能为空")
    private Integer enterNumber;

    /**
     * 备注
     */
    private String remark;


    /**
     * 入库时接收的多个入库单的id
     */
    private List<Long> storeOrderIdList;
}