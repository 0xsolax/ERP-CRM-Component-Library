package com.qmy.zhongsheng.core.document.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qmy.zhongsheng.core.document.model.entity.DocumentActionLogDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 公共单据动作日志 DAO。
 *
 * @author AI Coding
 */
@Mapper
public interface DocumentActionLogDAO extends BaseMapper<DocumentActionLogDO> {
}
