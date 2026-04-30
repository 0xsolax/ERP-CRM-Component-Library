package com.qmy.zhongsheng.core.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qmy.zhongsheng.core.user.model.entity.RoleMenuDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色与菜单关联数据访问层。
 *
 * @author AI Coding
 */
@Mapper
public interface RoleMenuDAO extends BaseMapper<RoleMenuDO> {
}
