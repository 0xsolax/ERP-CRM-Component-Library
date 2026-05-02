/*
 * @author java_deng
 * @date 2024/11/21 16:30
 * @description 库存Mapper接口
 */
package com.qiaomoyun.mapper.sto.yt;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiaomoyun.entity.sto.yt.StoYtStore;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 库存Mapper接口
 */
@Mapper
public interface StoYtStoreMapper extends BaseMapper<StoYtStore> {

    /**
     * 根据规格ID查询库存信息
     * @param specificationId 规格ID
     * @return 库存信息
     */
    StoYtStore selectBySpecificationId(@Param("specificationId") Long specificationId);

    /**
     * 根据产品ID查询库存信息
     * @param productId 产品ID
     * @return 库存信息列表
     */
    java.util.List<StoYtStore> selectByProductId(@Param("productId") Long productId);

    /**
     * 批量插入库存信息
     * @param storeList 库存信息列表
     * @return 插入成功的数量
     */
    int batchInsert(@Param("storeList") java.util.List<StoYtStore> storeList);

    /**
     * 根据规格ID更新库存数量
     * @param specificationId 规格ID
     * @param realStore 实际库存
     * @param enableStore 可用库存
     * @param occupyStore 占用库存
     * @return 更新成功的数量
     */
    int updateStoreBySpecificationId(@Param("specificationId") Long specificationId,
                                     @Param("realStore") Integer realStore,
                                     @Param("enableStore") Integer enableStore,
                                     @Param("occupyStore") Integer occupyStore);
}