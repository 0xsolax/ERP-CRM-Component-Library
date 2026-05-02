package com.qiaomoyun.entity.sal.yt;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qiaomoyun.entity.BaseEntity;
import com.qiaomoyun.entity.pro.yt.ProYtProductFile;
import com.qiaomoyun.entity.pro.yt.ProYtProductSpecificationItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单子表-订单商品项
 * @author system
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sal_yt_order_sub_item")
public class SalYtOrderSubItem extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderSubId;
    private Long productId;
    private Long specificationId;
    private BigDecimal price;
    private Integer number;
    private Long labelId;
    private String labelName;
    private String remark;
    private String status;
    private Long supplierId;
    private BigDecimal supplierPrice;
    private Integer occupyStoreNumber;
    private Integer occupyTransitNumber;
    private Long occupyTransitPurchaseItemId;
    private Integer occupyTransitEnterNumber;
    private Integer enterNumber;
    private Integer deliveryNumber;
    private Integer endReturnNumber;
    private Integer applyPurchaseNumber;
    private Long confirmItemId;
    /**
     * 本位币，该字段金额都是人民币
     */
    private BigDecimal basePrice;

    @Schema(description = "父订单id")
    @TableField(exist = false)
    private Long orderId;

    @TableField(exist = false)
    private String productCode;
    @TableField(exist = false)
    private String handProductLevel;
    @TableField(exist = false)
    private Boolean isCustomerStore;
    @TableField(exist = false)
    private Boolean isIncompleteProduct;
    @TableField(exist = false)
    private String specificationName;
    @TableField(exist = false)
    private String minNumber;

    @TableField(exist = false)
    private Integer enabledStore; // 可用库存
    @TableField(exist = false)
    private Integer enabledTransit; //可用在途

    @TableField(exist = false)
    private List<ProYtProductFile> imageList; //规格图片
    @TableField(exist = false)
    private List<ProYtProductSpecificationItem> itemList; //规格项


    //半成品参数
    @TableField(exist = false)
    private Integer confirmNumber;
    @TableField(exist = false)
    private String productCodeOrOrderSubCode;
    @TableField(exist = false)
    private Integer confirmStatus;


    @TableField(exist = false)
    private String reason;

    @TableField(exist = false)
    private Boolean isApply;//标识是否订单申购时是否申购这条item，如独立仓产品就无需申购不用产生出入库记录
    @TableField(exist = false)
    private Long customerStoreId;

    //占用详情参数
    @TableField(exist = false)
    private String orderSubCode; //子订单code
    @TableField(exist = false)
    private String orderCode;
    @TableField(exist = false)
    private String customerName; //客户名称
    @TableField(exist = false)
    private String salesEmployeeName; //业务员
    @TableField(exist = false)
    private java.time.LocalDateTime deliveryTime; //交货时间
    @TableField(exist = false)
    private Integer currentBindNumber; //当前采购单强绑定待入库数量
    @TableField(exist = false)
    private Integer totalBindNumber; //所有采购单强绑定待入库总数量
    @TableField(exist = false)
    private Integer totalOccupyTransitNumber;
    @TableField(exist = false)
    private Integer occupyTransitWaitEnterNumber; //占用在途待入库数量，给入库时用

    //半成品合并的id集合
    @TableField(exist = false)
    private List<Long> salYtOrderSubItemIds;
}