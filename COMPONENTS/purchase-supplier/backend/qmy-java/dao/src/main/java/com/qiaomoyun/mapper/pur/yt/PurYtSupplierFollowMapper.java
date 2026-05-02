package com.qiaomoyun.mapper.pur.yt;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiaomoyun.entity.pur.yt.PurYtSupplierFollow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 供应商跟进Mapper接口
 * 处理供应商跟进记录的数据库操作
 */
@Mapper
public interface PurYtSupplierFollowMapper extends BaseMapper<PurYtSupplierFollow> {

    /**
     * 根据供应商ID查询跟进记录列表
     * @param supplierId 供应商ID
     * @return 供应商跟进记录列表
     */
    List<PurYtSupplierFollow> selectBySupplierId(@Param("supplierId") Long supplierId);

    /**
     * 根据ID删除供应商跟进记录（逻辑删除）
     * @param id 跟进记录ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
}
