/*
 * @author java_deng
 * @date 2024/11/20 16:50
 * @description 客户Mapper接口
 */
package com.qiaomoyun.mapper.sal.yt;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiaomoyun.entity.sal.yt.SalYtCustomer;
import com.qiaomoyun.param.sal.yt.SalYtCustomerQueryParams;
import com.qiaomoyun.param.sal.yt.CustomerVipParams;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 客户表Mapper接口
 */
@Mapper
public interface SalYtCustomerMapper extends BaseMapper<SalYtCustomer> {
    /**
     * 根据ID查询客户详情（包含逻辑删除过滤）
     * @param id 客户ID
     * @return 客户信息
     */
    SalYtCustomer selectById(Long id);

    List<SalYtCustomer> list(SalYtCustomerQueryParams params);

    /**
     * 查询VIP客户列表
     * @param params 查询参数
     * @return VIP客户列表
     */
    List<SalYtCustomer> selectVipCustomers(CustomerVipParams params);

    /**
     * 查询非VIP客户列表
     * @param params 查询参数
     * @return 非VIP客户列表
     */
    List<SalYtCustomer> selectNonVipCustomers(CustomerVipParams params);

    /**
     * 根据报价单ID查询客户信息
     * @param quotationId 报价单ID
     * @return 客户信息
     */
    SalYtCustomer selectCustomerByQuotationId( Long quotationId);

    List<SalYtCustomer> selectByFollowEmployeeIdAndSaleEmployeeId(Long userId);

    /**
     * 根据业务员范围统计客户总数
     */
    Integer countBySalesIds(@Param("salesIds") List<Long> salesIds);
}