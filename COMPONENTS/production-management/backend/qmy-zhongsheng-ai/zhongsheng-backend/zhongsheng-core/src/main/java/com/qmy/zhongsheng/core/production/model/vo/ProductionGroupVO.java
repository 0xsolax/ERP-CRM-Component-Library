package com.qmy.zhongsheng.core.production.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 生产组 VO。
 *
 * @author AI Coding
 */
@Data
public class ProductionGroupVO {

    private Long id;

    private String code;

    private String name;

    private Integer status;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
