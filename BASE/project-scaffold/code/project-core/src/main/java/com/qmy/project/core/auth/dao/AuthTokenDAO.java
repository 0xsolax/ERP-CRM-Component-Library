package com.qmy.project.core.auth.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qmy.project.core.auth.model.entity.AuthTokenDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 09:49
 */
@Mapper
public interface AuthTokenDAO extends BaseMapper<AuthTokenDO> {
}
