package com.qmy.zhongsheng.core.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qmy.zhongsheng.core.user.model.entity.UserRoleDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户与角色关联数据访问层。
 *
 * @author AI Coding
 */
@Mapper
public interface UserRoleDAO extends BaseMapper<UserRoleDO> {
}
