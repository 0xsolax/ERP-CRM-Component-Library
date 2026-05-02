package com.qiaomoyun.mapper.sto.yt;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiaomoyun.entity.sto.yt.StoYtStoreOrderOperation;
import com.qiaomoyun.param.sto.yt.StoYtStoreOrderQueryParams;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 入库单操作表 Mapper 接口
 * </p>
 *
 * @author 系统
 * @since 2023-01-01
 */
@Mapper
public interface StoYtStoreOrderOperationMapper extends BaseMapper<StoYtStoreOrderOperation> {

    List<StoYtStoreOrderOperation> selectByStoreOrderId(Long storeOrderId);

    List<StoYtStoreOrderOperation> selectStoreOrderProgressList(StoYtStoreOrderQueryParams params);
}
