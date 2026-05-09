package com.qmy.zhongsheng.core.document.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qmy.zhongsheng.core.document.model.entity.DocumentUnlockRequestDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 公共单据解锁申请 DAO。
 *
 * @author AI Coding
 */
@Mapper
public interface DocumentUnlockRequestDAO extends BaseMapper<DocumentUnlockRequestDO> {
}
