/*
 * @author java_deng
 * @date 2026/3/23 11:32
 * @description
 */
package com.qiaomoyun.param.sto.yt;

import lombok.Data;

import java.util.List;

@Data
public class StoYtStoreBatchEnterParams {
    List<StoYtStoreOrderAddParams> stoYtStoreOrderAddParams;
}
