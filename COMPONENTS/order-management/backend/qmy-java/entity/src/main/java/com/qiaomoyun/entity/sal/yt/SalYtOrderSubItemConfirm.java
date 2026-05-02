package com.qiaomoyun.entity.sal.yt;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qiaomoyun.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 订单子表商品确认表
 * @author system
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sal_yt_order_sub_item_confirm")
public class SalYtOrderSubItemConfirm extends BaseEntity {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 子订单itemId
     */
    private Long salYtOrderSubItemId;

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 确认的规格id
     */
    private Long specificationId;

    /**
     * 确认数量
     */
    private Integer number;
}