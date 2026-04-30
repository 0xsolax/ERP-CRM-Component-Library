package com.qmy.zhongsheng.core.product.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.qmy.zhongsheng.infrastructure.model.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 产品类型关联 DO（一个产品可对应多个类型）。
 *
 * @author 单漪甜
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product_type")
public class ProductTypeDO extends BaseDO {

    private Long productId;

    private Long typeId;

    private String typeName;
}
