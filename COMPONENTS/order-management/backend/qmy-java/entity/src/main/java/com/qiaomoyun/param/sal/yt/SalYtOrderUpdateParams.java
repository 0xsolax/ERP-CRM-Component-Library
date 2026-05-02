/*
 * @author java_deng
 * @date 2025/11/20 15:04
 * @description
 */
package com.qiaomoyun.param.sal.yt;


import com.qiaomoyun.entity.sal.yt.SalYtOrder;
import com.qiaomoyun.entity.sal.yt.SalYtOrderSub;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SalYtOrderUpdateParams extends SalYtOrder {
    private List<SalYtOrderSub> orderSubList;

    //汇率设置参数
    private BigDecimal exchangeRate;
}
