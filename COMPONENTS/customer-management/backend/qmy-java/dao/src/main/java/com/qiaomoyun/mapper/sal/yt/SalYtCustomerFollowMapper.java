package com.qiaomoyun.mapper.sal.yt;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiaomoyun.entity.sal.yt.SalYtCustomerFollow;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 客户跟进记录Mapper接口
 */
public interface SalYtCustomerFollowMapper extends BaseMapper<SalYtCustomerFollow> {

    /**
     * 根据客户ID查询跟进记录列表
     * @param customerId 客户ID
     * @return 跟进记录列表
     */
    List<SalYtCustomerFollow> selectByCustomerId(@Param("customerId") Long customerId);

    SalYtCustomerFollow selectByCustomerIdFinally(Long id);
}