/*
 * @author java_deng
 * @date 2024/11/20 17:00
 * @description 客户联系人Mapper接口
 */
package com.qiaomoyun.mapper.sal.yt;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiaomoyun.entity.sal.yt.SalYtContactPerson;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 客户联系人表Mapper接口
 */
@Mapper
public interface SalYtContactPersonMapper extends BaseMapper<SalYtContactPerson> {
    /**
     * 根据客户ID查询联系人列表
     * @param customerId 客户ID
     * @return 联系人列表
     */
    List<SalYtContactPerson> selectByCustomerId(Long customerId);
    
    /**
     * 根据客户ID删除所有联系人（逻辑删除）
     * @param customerId 客户ID
     */
    void deleteByCustomerId(Long customerId);
    
    /**
     * 批量插入联系人信息
     * @param contactPersonList 联系人列表
     */
    void insertBatch(List<SalYtContactPerson> contactPersonList);
    
    /**
     * 根据供应商ID查询联系人列表
     * @param supplierId 供应商ID
     * @return 联系人列表
     */
    List<SalYtContactPerson> selectBySupplierId(Long supplierId);
    
    /**
     * 根据供应商ID删除所有联系人（逻辑删除）
     * @param supplierId 供应商ID
     */
    void deleteBySupplierId(Long supplierId);

    /**
     * 根据姓名查询是否存在（排除指定的联系人ID）
     * @param name 姓名
     * @param excludeContactId 排除的联系人ID
     * @return 是否存在
     */
    int countByNameExcludeContactId(@Param("name") String name, @Param("excludeContactId") Long excludeContactId);

    /**
     * 根据邮箱查询是否存在（排除指定的联系人ID）
     * @param email 邮箱
     * @param excludeContactId 排除的联系人ID
     * @return 是否存在
     */
    int countByEmailExcludeContactId(@Param("email") String email, @Param("excludeContactId") Long excludeContactId);
}