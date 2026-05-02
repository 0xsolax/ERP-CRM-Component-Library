package com.qiaomoyun.entity.sto.yt;

import cn.hutool.core.lang.hash.Hash;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qiaomoyun.entity.BaseEntity;
import com.qiaomoyun.entity.pro.yt.ProYtProductFile;
import com.qiaomoyun.entity.pro.yt.ProYtProductSpecificationItem;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 库存记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sto_yt_store_record")
public class StoYtStoreRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String code;

    /**
     * 仓库ID
     */
    private Long storeId;

    /**
     * 规格ID
     */
    private Long specificationId;

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 采购单ID
     */
    private Long purchaseId;

    /**
     * 订单子ID
     */
    private Long orderSubId;

    /**
     * 业务类型
     */
    private Integer type;

    /**
     * 实际库存期初
     */
    private Integer realStoreInit;

    /**
     * 实际库存变动
     */
    private Integer realStoreChange;

    /**
     * 可用库存期初
     */
    private Integer enableStoreInit;

    /**
     * 可用库存变动
     */
    private Integer enableStoreChange;

    /**
     * 占用库存期初
     */
    private Integer occupyStoreInit;

    /**
     * 占用库存变动
     */
    private Integer occupyStoreChange;

    /**
     * 实际在途期初
     */
    private Integer realTransitInit;

    /**
     * 实际在途变动
     */
    private Integer realTransitChange;

    /**
     * 可用在途期初
     */
    private Integer enableTransitInit;

    /**
     * 可用在途变动
     */
    private Integer enableTransitChange;

    /**
     * 占用在途期初
     */
    private Integer occupyTransitInit;

    /**
     * 占用在途变动
     */
    private Integer occupyTransitChange;

    private String orderSubCode;
    private Long orderId;
    private String orderCode;
    private Long customerId;
    private Long customerStoreId;
    private String remark;

    //记录入库时分配了数量的子订单code和number的map，给入库单操作记录使用
    @TableField(exist = false)
    private List<HashMap<Object,Object>> allocationOrderSubCodeList;

    //记录了入库时是否需要采购单创建操作记录
    @TableField(exist = false)
    private Boolean isPurchaseCreateOperation;

    @TableField(exist = false)
    private String purchaseCode;


    //入库单出入库记录需要返回参数
    @TableField(exist = false)
    private List<ProYtProductFile> imageList;
    @TableField(exist = false)
    private List<ProYtProductSpecificationItem> itemList;
    @TableField(exist = false)
    private String createUserName;
    @TableField(exist = false)
    private String customerName;
    @TableField(exist = false)
    private String productCode;
    @TableField(exist = false)
    private Integer operationNumber;
    @TableField(exist = false)
    private String locationName;
}