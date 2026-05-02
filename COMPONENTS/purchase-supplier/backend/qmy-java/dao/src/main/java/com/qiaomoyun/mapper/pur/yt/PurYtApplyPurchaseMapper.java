package com.qiaomoyun.mapper.pur.yt;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiaomoyun.entity.pur.yt.PurYtApplyPurchase;
import com.qiaomoyun.param.sal.yt.PurYtApplyPurchaseQueryParams;
import com.qiaomoyun.vo.pur.yt.PurYtApplyPurchaseListVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 采购申请Mapper接口
 */
@Mapper
public interface PurYtApplyPurchaseMapper extends BaseMapper<PurYtApplyPurchase> {

    /**
     * 根据条件查询采购申请列表
     * @param paramMap 查询参数
     * @return 采购申请列表
     */
    List<PurYtApplyPurchase> selectByMap(Map<String, Object> paramMap);

    /**
     * 根据产品ID查询采购申请
     * @param productId 产品ID
     * @return 采购申请列表
     */
    List<PurYtApplyPurchase> selectByProductId(Long productId);

    /**
     * 根据供应商ID查询采购申请
     * @param supplierId 供应商ID
     * @return 采购申请列表
     */
    List<PurYtApplyPurchase> selectBySupplierId(Long supplierId);

    /**
     * 根据客户ID查询采购申请
     * @param customerId 客户ID
     * @return 采购申请列表
     */
    List<PurYtApplyPurchase> selectByCustomerId(Long customerId);

    /**
     * 批量插入采购申请
     * @param applyPurchaseList 采购申请列表
     * @return 插入成功的记录数
     */
    int insertBatch(List<PurYtApplyPurchase> applyPurchaseList);

    /**
     * 批量更新采购申请
     * @param applyPurchaseList 采购申请列表
     * @return 更新成功的记录数
     */
    int updateBatch(List<PurYtApplyPurchase> applyPurchaseList);

    /**
     * 根据条件查询采购申请列表（按供应商分组）
     * @param params 查询参数
     * @return 按供应商分组的采购申请列表
     */
    List<PurYtApplyPurchaseListVo> list(PurYtApplyPurchaseQueryParams params);

    List<PurYtApplyPurchase> selectBySupplierIdAndIsInboundDelivery(PurYtApplyPurchaseQueryParams params);

    /**
     * 根据ID列表查询采购申请，并获取产品Code、客户名称、供应商规格、供应商单价、供应商起订量
     * @param ids ID列表
     * @return 采购申请列表
     */
    List<PurYtApplyPurchase> selectByIdsWithDetails(List<Long> ids);

    PurYtApplyPurchase selectByOrderSubItemId(Long orderSubItemId);

    List<Map<String, Object>> sumActiveNumberByOrderSubItemIds(@Param("orderSubItemIds") List<Long> orderSubItemIds);

    /**
     * 根据产品规格id获取待采购订单信息
     */
    List<PurYtApplyPurchase> selectByProductSpecificationId(Long productSpecificationId);

    /**
     * 根据子订单id获取订单编号
     * @param orderSubId
     * @return
     */
    String getOrderCodeByOrderSubId(Long orderSubId);

    /**
     * 根据子订单id获取订单备注
     * @param orderSubId
     * @return
     */
    String getOrderNoteByOrderSubId(Long orderSubId);
}