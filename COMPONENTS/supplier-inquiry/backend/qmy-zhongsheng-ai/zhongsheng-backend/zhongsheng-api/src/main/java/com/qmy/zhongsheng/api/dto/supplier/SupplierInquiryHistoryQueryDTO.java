package com.qmy.zhongsheng.api.dto.supplier;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 供应商询价历史查询。
 *
 * @author AI Coding
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "供应商询价历史查询")
public class SupplierInquiryHistoryQueryDTO extends SupplierInquiryListQueryDTO {
}
