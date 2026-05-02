/*
 * @author java_deng
 * @date 2025/12/1 10:08
 * @description
 */
package com.qiaomoyun.param.pur.yt;

import com.qiaomoyun.entity.pro.yt.ProYtProductLabel;
import com.qiaomoyun.entity.pur.yt.PurYtSupplier;
import com.qiaomoyun.entity.sal.yt.SalYtContactPerson;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class PurYtSupplierUpdateParams extends PurYtSupplier {
    @NotNull(message = "客户联系人不能为空")
    private List<SalYtContactPerson> contactPersonList;
    @NotNull(message = "标签不能为空")
    private List<ProYtProductLabel> labelList;

}
