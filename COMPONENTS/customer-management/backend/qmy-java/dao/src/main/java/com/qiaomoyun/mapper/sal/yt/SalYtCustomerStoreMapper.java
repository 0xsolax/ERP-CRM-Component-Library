/*
 * @author java_deng
 * @date 2024/11/21 16:35
 * @description 客户独立仓Mapper接口
 */
package com.qiaomoyun.mapper.sal.yt;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiaomoyun.entity.pro.yt.ProYtProductSpecification;
import com.qiaomoyun.entity.sal.yt.SalYtCustomerStore;
import com.qiaomoyun.param.sal.yt.SalYtCustomerQueryParams;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 客户独立仓Mapper接口
 */
@Mapper
public interface SalYtCustomerStoreMapper extends BaseMapper<SalYtCustomerStore> {
    
    /**
     * 查询独立仓产品列表
     */
    List<Map<String, Object>> selectProductList(SalYtCustomerQueryParams params);
    
    /**
     * 查询客户是否有某个产品的独立仓
     */
    Integer selectStoreExists(@Param("customerId") Long customerId, @Param("productId") Long productId);
    
    /**
     * 根据客户ID和产品ID查询独立仓数据
     */
    List<SalYtCustomerStore> selectByCustomerIdAndProductId(@Param("customerId") Long customerId, @Param("productId") Long productId);
    
    /**
     * 根据规格信息查询客户独立仓
     */
    SalYtCustomerStore selectBySpecificationAndCustomer( ProYtProductSpecification specification);
    
    /**
     * 根据客户ID和规格ID查询独立仓数据
     */
    SalYtCustomerStore selectByCustomerIdAndSpecificationId(@Param("customerId") Long customerId, @Param("specificationId") Long specificationId);
    
    /**
     * 更新独立仓状态
     */
    void updateStatus(@Param("id") Long id, @Param("status") String status);
    
    /**
     * 查询客户独立仓预警数量
     * @param customerId 客户ID
     * @return 预警产品数量
     */
    Integer selectWarningCountByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 查询客户独立仓产品预警数量
     * @param customerId 客户ID
     * @param productId 产品ID
     * @return 预警规格数量
     */
    Integer selectWarningCountByCustomerIdAndProductId(@Param("customerId") Long customerId, @Param("productId") Long productId);

    SalYtCustomerStore selectLocationById(Long customerStoreId);
}