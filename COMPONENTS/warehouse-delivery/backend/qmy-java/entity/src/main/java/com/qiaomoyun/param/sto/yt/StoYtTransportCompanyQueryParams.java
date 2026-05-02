package com.qiaomoyun.param.sto.yt;

import com.qiaomoyun.param.BasePageQuery;
import lombok.Data;

/**
 * 物流公司查询参数类
 */
@Data
public class StoYtTransportCompanyQueryParams extends BasePageQuery {

    /**
     * 物流公司编号
     */
    private String code;

    /**
     * 物流公司名称
     */
    private String name;

    /**
     * 物流公司类型
     */
    private String type;

    /**
     * 是否提供上门服务
     */
    private Integer isHomeService;
}
