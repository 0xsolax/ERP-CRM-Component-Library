package com.qiaomoyun.param.sal.sed;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.qiaomoyun.param.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 报价单参数类,模糊搜索时
 */
@Data
public class SalSedQuotationParams extends BasePageQuery {
    @Schema(description = "报价单编号")
    private String quotationCode;

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "创建开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createStartTime;

    @Schema(description = "创建结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createEndTime;


    @Schema(description = "上次编辑开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime lastEditStartTime;

    @Schema(description = "上次编辑结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime lastEditEndTime;

    @Schema(description = "报价单状态 0=暂存，1=计算成本中，2=计算完成，5=总裁未审核，财务未审核，6=总裁审核通过，财务未审核，7=总裁未审核，财务审核通过，8=总裁微信审核通过，财务未审核，4=审核通过，-1=审核驳回")
    private String status;

    @Schema(description = "财务未审核筛选：true-筛选财务未审核的状态（状态5、6、8）")
    private Boolean financePending;

    @Schema(description = "总裁未审核筛选：true-筛选总裁未审核的状态（状态5、7、8）")
    private Boolean presidentPending;


}
