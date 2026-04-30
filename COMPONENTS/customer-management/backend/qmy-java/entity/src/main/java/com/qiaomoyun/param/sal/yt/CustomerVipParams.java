package com.qiaomoyun.param.sal.yt;

import lombok.Data;

import java.util.List;

/**
 * 设置VIP客户参数类
 */
@Data
public class CustomerVipParams {
    /**
     * 新增VIP客户的ID列表
     */
    private List<Long> addVipIds;

    /**
     * 移除VIP客户的ID列表
     */
    private List<Long> removeVipIds;

    private String nonVipCustomerName;
    private String vipCustomerName;
}
