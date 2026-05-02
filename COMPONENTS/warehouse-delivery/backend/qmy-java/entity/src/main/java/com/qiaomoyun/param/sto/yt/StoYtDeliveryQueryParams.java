package com.qiaomoyun.param.sto.yt;

import com.baomidou.mybatisplus.annotation.TableField;
import com.qiaomoyun.param.BasePageQuery;
import lombok.Data;

/**
 * 发货单查询参数
 */
@Data
public class StoYtDeliveryQueryParams extends BasePageQuery {
    private Long id;
    private Long orderSubId;
    private String specificationName;
    private String productCode;
    /**
     * 发货单编号
     */
    private String code;

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 规格ID
     */
    private Long specificationId;

    /**
     * 子订单号
     */
    private String subCode;

    /**
     * 状态
     */
    private String status;

    private Long addressId;

    private Long deliveryBoxId;
    private String packageCode;
    private String transportCompanyName;
    private Integer receiveStatus;
    private String salesEmployeeName; // 业务员姓名

    private Long categorySpecificationItemId;

    /**
     * 产品状态（true: 产品齐全, false: 产品不齐全）
     */
    private Boolean productComplete;

    /**
     * 打包状态（true: 已打包完成, false: 未打包完成）
     */
    private Boolean packageComplete;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单号
     */
    private String orderCode;

    private Boolean isCollectedShippingCost;
}
