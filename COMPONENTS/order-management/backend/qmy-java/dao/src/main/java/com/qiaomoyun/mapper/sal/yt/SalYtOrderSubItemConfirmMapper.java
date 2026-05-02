package com.qiaomoyun.mapper.sal.yt;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiaomoyun.entity.sal.yt.SalYtOrderSubItemConfirm;
import java.util.List;

/**
 * 订单子表商品确认表Mapper接口
 * @author system
 */
public interface SalYtOrderSubItemConfirmMapper extends BaseMapper<SalYtOrderSubItemConfirm> {

    /**
     * 查询订单子表商品确认表列表
     * @param salYtOrderSubItemConfirm 订单子表商品确认表
     * @return 订单子表商品确认表集合
     */
    List<SalYtOrderSubItemConfirm> selectSalYtOrderSubItemConfirmList(SalYtOrderSubItemConfirm salYtOrderSubItemConfirm);

    /**
     * 根据子订单商品ID查询确认信息
     * @param salYtOrderSubItemId 子订单商品ID
     * @return 订单子表商品确认表
     */
    SalYtOrderSubItemConfirm selectByOrderSubItemId(Long salYtOrderSubItemId);

    /**
     * 批量插入订单子表商品确认信息
     * @param confirmList 订单子表商品确认表集合
     * @return 结果
     */
    int batchInsert(List<SalYtOrderSubItemConfirm> confirmList);

    /**
     * 根据子订单商品ID删除确认信息
     * @param salYtOrderSubItemId 子订单商品ID
     * @return 结果
     */
    int deleteByOrderSubItemId(Long salYtOrderSubItemId);
}