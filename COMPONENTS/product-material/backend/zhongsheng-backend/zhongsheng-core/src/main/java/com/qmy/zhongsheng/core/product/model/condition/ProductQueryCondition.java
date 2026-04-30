package com.qmy.zhongsheng.core.product.model.condition;

import com.qmy.zhongsheng.common.condition.PageQueryCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

/**
 * 产品分页查询入参（由 {@link com.qmy.zhongsheng.api.dto.product.ProductListQueryDTO} 在 Service 层转换后传入 Manager）。
 *
 * @author 单漪甜
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ProductQueryCondition extends PageQueryCondition {

    /**
     * 限制查询的产品主键集合；{@code null} 表示不按 ID 集合筛选；空集合表示前置筛选无命中，结果为空。
     */
    private Set<Long> ids;

    /**
     * 模糊关键词：匹配主表产品编号、货品描述（中/英文）及关联表 {@code product_type.type_name}（与原先独立查询再求并集语义一致）。
     */
    private String keywords;

}
