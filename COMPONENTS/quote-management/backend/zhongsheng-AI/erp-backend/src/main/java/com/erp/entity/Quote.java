package com.erp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("quote")
public class Quote {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String code;
    private Long customerId;
    private LocalDate quoteDate;
    private LocalDate validDate;
    private BigDecimal totalAmount;
    private String status;
    private String remark;
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String products;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
