package com.qiaomoyun.mapper.sal.yt;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiaomoyun.entity.sal.yt.SalYtOrder;
import com.qiaomoyun.entity.sal.yt.SalYtOrderSubItem;
import com.qiaomoyun.param.sal.yt.SalYtOrderOrderReturnItemParams;
import com.qiaomoyun.param.sto.yt.StoYtStoreRecordQueryParams;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单子表-订单商品项Mapper接口
 * @author system
 */
public interface SalYtOrderSubItemMapper extends BaseMapper<SalYtOrderSubItem> {

    /**
     * 查询订单子表-订单商品项列表
     * @param salYtOrderSubItem 订单子表-订单商品项
     * @return 订单子表-订单商品项集合
     */
    List<SalYtOrderSubItem> selectSalYtOrderSubItemList(SalYtOrderSubItem salYtOrderSubItem);

    /**
     * 根据子表ID查询订单商品项
     * @param subId 子表ID
     * @return 订单商品项集合
     */
    List<SalYtOrderSubItem> selectSalYtOrderSubItemByOrderSubId(Long subId);

    /**
     * 根据子表ID集合查询订单商品项
     * @param subIds 子表ID
     * @return 订单商品项
     */
    List<SalYtOrderSubItem> selectSalYtOrderSubItemByOrderSubIds(@Param("subIds") List<Long> subIds);

    /**
     * 根据子表ID删除订单商品项
     * @param subId 子表ID
     * @return 结果
     */
    int deleteSalYtOrderSubItemBySubId(Long subId);

    /**
     * 根据订单ID删除订单商品项
     * @param orderId 订单ID
     * @return 结果
     */
    int deleteSalYtOrderSubItemByOrderId(Long orderId);

    List<SalYtOrderSubItem> inCompleteList(SalYtOrderSubItem params);

    /**
     * 根据confirm_item_id查询订单子项
     * @param confirmItemId 确认项ID
     * @return 订单子项列表
     */
    List<SalYtOrderSubItem> selectByConfirmItemId(Long confirmItemId);

    /**
     * 查询占用在途数量-占用在途入库数量>0并且订单数量-入库数量>0的子订单item
     * @param specificationId 规格ID
     * @return 订单子项列表
     */
    List<SalYtOrderSubItem> selectOccupyTransitSpecification(Long specificationId);

    /**
     * 查询指定客户的订单下子订单的子订单item等于该规格的，并且入库数量小于订单数量的
     * @param params 包含customerId和specificationId的参数
     * @return 订单子项列表
     */
    List<SalYtOrderSubItem> selectByCustomerAndSpecificationAndEnterNumberLessThanNumber(Map<String, Object> params);

    /**
     * 查询同一子订单下相同产品且规格不为空的商品项
     * @param params 包含orderSubId和productId的参数
     * @return 订单子项列表
     */
    List<SalYtOrderSubItem> selectByOrderSubIdAndProductIdAndSpecificationNotNull(Map<String, Object> params);

    /**
     * 查询同一子订单下相同规格且规格不为空的商品项
     * @param params 包含orderSubId和specificationId的参数
     * @return 订单子项列表
     */
    List<SalYtOrderSubItem> selectByOrderSubIdAndSpecificationId(Map<String, Object> params);

    /**
     * 查询指定规格ID的库存占用详情
     * @param specificationId 规格ID
     * @return 订单子项列表
     */
    List<SalYtOrderSubItem> selectStoreOccupyDetail(StoYtStoreRecordQueryParams params);

    /**
     * 查询指定规格ID的在途占用详情
     * @param specificationId 规格ID
     * @return 订单子项列表
     */
    List<SalYtOrderSubItem> selectTransitOccupyDetail(StoYtStoreRecordQueryParams params);

    /**
     * 统计指定规格在指定时间段内的订单单数和下单数量
     * @return 包含orderCount和productCount的Map
     */
    HashMap<String, Object> countOrderAndProductBySpecificationId(@Param("specificationId") Long specificationId , @Param("months")Integer months);

    HashMap<String, Object> countOrderAndItemByCustomerId(Long customerId, Integer customerLevelMonthRangeA);

    List<SalYtOrderSubItem> selectOrderSubCompletedItemByOrderSubId(Long orderSubId);

    /**
     * 根据订单id获取订单子项的最小状态
     * @param orderId
     * @return
     */
    String getMinStatus(Long orderId);

    /**
     * 查询子订单详情表，根据状态，父订单id等筛选（可选）
     * @param params
     * @return
     */
    List<SalYtOrderSubItem> selectSalYtOrderSubItemLists(SalYtOrderSubItem params);

    /**
     * 查询退货子项信息
     * @param params
     * @return
     */
    List<SalYtOrderSubItem> selectOrderReturnItemInfo(SalYtOrderOrderReturnItemParams params);

    /**
     * 根据子订单id集合查询子订单详情
     * @param orderSubIds
     * @return
     */
    List<SalYtOrderSubItem> selectSalYtOrderSubItemByOrderSubIdList(@Param("orderSubIds")List<Long> orderSubIds);

    SalYtOrder selectOrderById(Long orderSubItemId);

    /**
     * 根据发货单id和规格id查询仓库数量
     * @param deliveryId
     * @param specificationId
     * @return
     */
    Integer selectEnterNumberBySpecificationIdAndDeliveryId(@Param("deliveryId") Long deliveryId,@Param("specificationId") Long specificationId);

    /**
     * 根据订单id查询子订单详情
     * @param
     * @return
     */
    List<SalYtOrderSubItem> selectSalYtOrderSubItemListByOrderId(Long orderId);
}