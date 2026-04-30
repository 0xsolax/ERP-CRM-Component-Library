package com.qmy.zhongsheng.core.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qmy.zhongsheng.core.user.model.entity.RoleDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统角色数据访问层。
 *
 * @author AI Coding
 */
@Mapper
public interface RoleDAO extends BaseMapper<RoleDO> {
}
