package com.qmy.project.core.file.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qmy.project.infrastructure.model.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * @author AI Coding
 * @description 系统文件（列名与 {@link com.qmy.project.core.file.model.vo.FileVO} 一致：name、url、size、type）
 * @date 2026/03/26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("system_file")
public class SystemFileDO extends BaseDO {

    @TableField("main_type")
    private String mainType;

    @TableField("sub_type")
    private String subType;

    @TableField("master_id")
    private Long masterId;

    @TableField("name")
    private String name;

    /**
     * 文件存储路径（Key），URL中域名后面的路径部分
     * 例如：uploads/2026/04/file.jpg
     */
    @TableField("file_key")
    private String fileKey;

    /**
     * OSS Endpoint（域名部分）
     * 例如：https://qmcloud-oss-test.oss-cn-hangzhou.aliyuncs.com 这种的格式
     */
    @TableField("endpoint")
    private String endpoint;

    @TableField("size")
    private Long size;

    @TableField("type")
    private String type;

    @TableField("url")
    public String url;

}
