/*
 * @author java_deng
 * @date 2025/11/6 15:18
 * @description
 */
package com.qiaomoyun.entity.sto.yt;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.qiaomoyun.entity.BaseEntity;
import lombok.Data;

@Data
public class StoYtLocation extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
}
