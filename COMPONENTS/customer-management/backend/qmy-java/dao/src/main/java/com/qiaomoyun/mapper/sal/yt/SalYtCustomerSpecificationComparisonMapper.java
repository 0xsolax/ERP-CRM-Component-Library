/*
 * @author java_deng
 * @date 2025/11/20 10:05
 * @description 客户规格映射Mapper
 */
package com.qiaomoyun.mapper.sal.yt;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiaomoyun.entity.sal.yt.SalYtCustomerSpecificationComparison;
import com.qiaomoyun.param.sal.yt.SalYtCustomerSpecificationComparisonQueryParams;

import java.util.List;

/**
 * 客户规格映射Mapper接口
 */
public interface SalYtCustomerSpecificationComparisonMapper extends BaseMapper<SalYtCustomerSpecificationComparison> {

    /**
     * 根据客户ID和条件查询规格映射列表
     * @param queryParams 查询参数（包含customerId、specification等）
     * @return 规格映射列表
     */
    List<SalYtCustomerSpecificationComparison> list(SalYtCustomerSpecificationComparisonQueryParams queryParams);

    /**
     * 检查规格是否已存在（根据客户ID和规格名称）
     * @param customerId 客户ID
     * @param specification 规格名称
     * @param excludeId 排除的ID（用于编辑时）
     * @return 是否存在
     */
    boolean existsByCustomerAndSpecification(Long customerId, String specification, Long excludeId);

    /**
     * 根据客户ID删除所有规格映射
     * @param customerId 客户ID
     * @return 删除行数
     */
    int deleteByCustomerId(Long customerId);

    /**
     * 批量插入规格映射
     * @param list 规格映射列表
     * @return 插入行数
     */
    int batchInsert(List<SalYtCustomerSpecificationComparison> list);

    List<SalYtCustomerSpecificationComparison> selectUnmappedSpecificationItems(SalYtCustomerSpecificationComparisonQueryParams queryParams);


    SalYtCustomerSpecificationComparison selectBySpecificationAndCustomer(String specification, Long customerId);

    SalYtCustomerSpecificationComparison selectBySpecificationIdAndCustomerId(Long specificationId, Long customerId);
}