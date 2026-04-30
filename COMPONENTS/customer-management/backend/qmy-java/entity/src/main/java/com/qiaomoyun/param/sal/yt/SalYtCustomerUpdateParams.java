/*
 * @author java_deng
 * @date 2025/11/14 11:02
 * @description
 */
package com.qiaomoyun.param.sal.yt;

import com.qiaomoyun.entity.pro.yt.ProYtProductLabel;
import com.qiaomoyun.entity.sal.yt.SalYtContactPerson;
import com.qiaomoyun.entity.sal.yt.SalYtCustomerAddress;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SalYtCustomerUpdateParams {
    private Long id;
    private String code;
    @NotBlank(message = "客户姓名不能为空")
    private String name;
    @NotNull(message = "请选择业务员")
    private Long belongEmployeeId;
    // @NotNull(message = "请选择跟进人")
    private Long followEmployeeId;
    @NotNull(message = "请选择客户类型")
    private String type;
    @NotNull(message = "请选择手动层级")
    private String handLevel;
    @NotNull(message = "请选择国家地区")
    private String countryRegion;
    @NotNull(message = "请选择公司官网")
    private String companyWebsite;
    @NotNull(message = "请选择公司地址")
    private String companyAddress;
    private String companyName;
    private String remark;
    private String customerSource;
    @Schema(description = "订单默认备注")
    private String orderDefaultRemark;

    @NotNull(message = "客户地址不能为空")
    private List<SalYtCustomerAddress> customerAddressList;
    @NotNull(message = "客户联系人不能为空")
    private List<SalYtContactPerson> contactPersonList;
    @NotNull(message = "标签不能为空")
    private List<ProYtProductLabel> labelList;
}
