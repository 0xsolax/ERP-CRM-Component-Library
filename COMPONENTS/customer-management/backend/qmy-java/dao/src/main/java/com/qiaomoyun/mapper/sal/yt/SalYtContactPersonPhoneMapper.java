package com.qiaomoyun.mapper.sal.yt;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiaomoyun.entity.sal.yt.SalYtContactPersonPhone;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 联系人电话Mapper接口
 */
public interface SalYtContactPersonPhoneMapper extends BaseMapper<SalYtContactPersonPhone> {
    
    /**
     * 根据联系人ID查询电话号码列表
     * @param contactId 联系人ID
     * @return 电话号码列表
     */
    List<SalYtContactPersonPhone> selectByContactId(@Param("contactId") Long contactId);
    
    /**
     * 根据联系人ID删除电话号码（逻辑删除）
     * @param contactId 联系人ID
     * @return 删除成功的记录数
     */
    int deleteByContactId(@Param("contactId") Long contactId);
    
    /**
     * 批量插入联系人电话号码
     * @param phoneList 电话号码列表
     * @return 插入成功的记录数
     */
    int batchInsert(@Param("list") List<SalYtContactPersonPhone> phoneList);

    /**
     * 根据电话号码查询是否存在（排除指定的联系人ID）
     * @param phone 电话号码
     * @param excludeContactId 排除的联系人ID
     * @return 是否存在
     */
    int countByPhoneExcludeContactId(@Param("phone") String phone, @Param("excludeContactId") Long excludeContactId);
}