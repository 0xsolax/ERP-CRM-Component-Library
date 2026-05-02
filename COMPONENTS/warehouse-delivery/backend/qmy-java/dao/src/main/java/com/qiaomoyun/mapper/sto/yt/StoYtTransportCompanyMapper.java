/*
 * @author java_deng
 * @date 2024/11/21 16:30
 * @description 物流公司Mapper接口
 */
package com.qiaomoyun.mapper.sto.yt;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qiaomoyun.entity.sto.yt.StoYtTransportCompany;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 物流公司Mapper接口
 */
@Mapper
public interface StoYtTransportCompanyMapper extends BaseMapper<StoYtTransportCompany> {


    /**
     * 根据名称查询物流公司
     * @return
     */
    StoYtTransportCompany selectByName(@Param("name") String name);
}
