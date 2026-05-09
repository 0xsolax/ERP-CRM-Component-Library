package com.qmy.zhongsheng.core.production.model.condition;

import lombok.Data;

/**
 * 生产组查询条件。
 *
 * @author AI Coding
 */
@Data
public class ProductionGroupQueryCondition {

    private Integer pageNum = 1;

    private Integer pageSize = 20;

    private String keyword;

    private Integer status;
}
