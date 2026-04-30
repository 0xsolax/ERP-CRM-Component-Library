package com.qmy.zhongsheng.core.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qmy.zhongsheng.core.user.model.entity.UserDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author AI Coding
 * @description UserDAO
 * @date 2026/03/20 09:49
 */
@Mapper
public interface UserDAO extends BaseMapper<UserDO> {
}
