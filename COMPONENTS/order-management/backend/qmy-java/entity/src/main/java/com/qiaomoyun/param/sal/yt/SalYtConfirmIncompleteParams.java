/*
 * @author java_deng
 * @date 2025/11/29 15:50
 * @description
 */
package com.qiaomoyun.param.sal.yt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class SalYtConfirmIncompleteParams {
    private Long id;
    private Long specificationId;
    private Integer number;

    private List<Long> itemIdList;
    private List<SalYtConfirmIncompleteParams> specificationList;

}
