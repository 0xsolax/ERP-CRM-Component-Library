/*
 * @author java_deng
 * @date 2024/11/20 17:10
 * @description 联系人社交账号Mapper接口
 */
package com.qiaomoyun.mapper.sal.yt;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiaomoyun.entity.sal.yt.SalYtContactPersonSocial;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 联系人社交账号表Mapper接口
 */
@Mapper
public interface SalYtContactPersonSocialMapper extends BaseMapper<SalYtContactPersonSocial> {
    /**
     * 根据联系人ID查询社交账号列表
     * @param contactId 联系人ID
     * @return 社交账号列表
     */
    List<SalYtContactPersonSocial> selectByContactId(Long contactId);
    
    /**
     * 根据联系人ID删除所有社交账号（逻辑删除）
     * @param contactId 联系人ID
     */
    void deleteByContactId(Long contactId);
    
    /**
     * 批量插入社交账号信息
     * @param socialList 社交账号列表
     */
    void insertBatch(List<SalYtContactPersonSocial> socialList);
}