/*
 * @author java_deng
 * @date 2025/12/1 13:09
 * @description
 */
package com.qiaomoyun.param.pur.yt;

import com.github.pagehelper.page.PageParams;
import com.qiaomoyun.param.BasePageQuery;
import lombok.Data;

@Data
public class PurYtSupplierQueryParams extends BasePageQuery {
    private String code;
    private String name;
}
