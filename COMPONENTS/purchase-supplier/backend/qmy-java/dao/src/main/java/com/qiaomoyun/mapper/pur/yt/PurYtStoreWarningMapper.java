package com.qiaomoyun.mapper.pur.yt;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiaomoyun.entity.pur.yt.PurYtStoreWarning;
import com.qiaomoyun.param.pur.yt.PurYtStoreWarningQueryParams;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PurYtStoreWarningMapper extends BaseMapper<PurYtStoreWarning> {
    /**
     * 删除前一天的库存预警数据
     */
    void deleteYesterdayData();
    /**
     * 删除所有未删除的库存预警数据
     */
    void deleteAllWarningData();
    /**
     * 根据客户ID查询库存预警列表
     */
    List<PurYtStoreWarning> selectByCustomerId(@Param("customerId") Long customerId);

    /**
     * 根据产品ID查询库存预警列表
     */
    List<PurYtStoreWarning> selectByProductId(@Param("productId") Long productId);

    /**
     * 根据规格ID查询库存预警列表
     */
    List<PurYtStoreWarning> selectBySpecificationId(@Param("specificationId") Long specificationId);

    /**
     * 根据客户ID、产品ID和规格ID查询库存预警
     */
    List<PurYtStoreWarning> selectByParams(@Param("customerId") Long customerId,
                                   @Param("productId") Long productId,
                                   @Param("specificationId") Long specificationId);

    List<PurYtStoreWarning> list(PurYtStoreWarningQueryParams params);

    /**
     * 根据id列表查询库存预警列表
     */
    List<PurYtStoreWarning> selectByIds(@Param("ids") List<Long> ids);
}