/*
 * @author java_deng
 * @date 2025/10/27 22:45
 * @description 用户数据权限Mapper
 */
package com.qiaomoyun.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiaomoyun.entity.sys.SysUserDataPermission;

import java.util.List;

/**
 * 用户数据权限Mapper接口
 */
public interface SysUserDataPermissionMapper extends BaseMapper<SysUserDataPermission> {

    /**
     * 根据用户ID查询数据权限
     */
    SysUserDataPermission getByUserId(Long userId);

    /**
     * 根据用户ID列表查询数据权限列表
     */
    List<SysUserDataPermission> getByUserIds(List<Long> userIds);

    /**
     * 批量更新用户数据权限
     */
    int updateBatch(List<SysUserDataPermission> permissions);

    /**
     * 根据用户ID删除数据权限
     */
    int deleteByUserId(Long userId);

    SysUserDataPermission getByUserIdAndPermission(Long userId, String permission);
}