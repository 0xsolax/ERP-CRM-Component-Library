/*
 * @author java_deng
 * @date 2024/11/20 16:55
 * @description 客户地址Mapper接口
 */
package com.qiaomoyun.mapper.sal.yt;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiaomoyun.entity.sal.yt.SalYtCustomerAddress;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 客户地址表Mapper接口
 */
@Mapper
public interface SalYtCustomerAddressMapper extends BaseMapper<SalYtCustomerAddress> {
    /**
     * 根据客户ID查询地址列表
     * @param customerId 客户ID
     * @return 地址列表
     */
    List<SalYtCustomerAddress> selectByCustomerId(Long customerId);

    /**
     * 根据客户ID查询地址列表，排除默认地址
     */
    List<SalYtCustomerAddress> selectByCustomerIdExcludeDefault(Long customerId);

    /**
 * 根据客户ID删除所有地址（逻辑删除）
 * @param customerId 客户ID
 */
void deleteByCustomerId(Long customerId);
    
/**
 * 批量插入地址信息
 * @param addressList 地址列表
 */
void insertBatch(List<SalYtCustomerAddress> addressList);
}