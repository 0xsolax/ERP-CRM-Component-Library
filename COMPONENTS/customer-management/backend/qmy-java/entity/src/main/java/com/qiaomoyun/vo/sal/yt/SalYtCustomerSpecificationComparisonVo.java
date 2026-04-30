/*
 * @author java_deng
 * @date 2025/11/18 17:46
 * @description
 */
package com.qiaomoyun.vo.sal.yt;

import com.qiaomoyun.entity.pro.yt.ProYtProductFile;
import com.qiaomoyun.entity.pro.yt.ProYtProductSpecificationItem;
import com.qiaomoyun.entity.sal.yt.SalYtCustomerSpecificationComparison;
import lombok.Data;

import java.util.List;

@Data
public class SalYtCustomerSpecificationComparisonVo extends SalYtCustomerSpecificationComparison {
    private String productCode;
    private List<ProYtProductFile> fileList;
    private List<ProYtProductSpecificationItem> itemList;
    private List<SalYtCustomerSpecificationComparisonVo> customerSpecificationList;
}
