package com.qmy.project.core.tenant.model.vo;

import com.qmy.project.core.file.model.vo.FileVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author shanyitian
 * @description 租户详细信息返回模型
 * @date 2026/3/26 16:34
 */
@Data
public class TenantInfoVO implements Serializable {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "租户名称")
    private String name;

    @Schema(description = "租户 slogan")
    private String slogan;

    @Schema(description = "租户账号体系标识")
    private String accountSystemKey;

    @Schema(description = "账号体系飞书 AppId")
    private String feiShuAppId;

    @Schema(description = "账号体系钉钉 AppKey")
    private String dingTalkAppKey;

    @Schema(description = "账号体系钉钉 AppSecret")
    private String dingTalkAppSecret;

    /** 租户背景图等文件列表（type=BACKGROUND） */
    private List<FileVO> backgroundFileList;

    /** 登录页logo（type=LOGIN_LOGO） */
    private List<FileVO> loginLogoFileList;

    /** 租户logo-菜单栏收起（type=MENU_COLLAPSED_LOGO） */
    private List<FileVO> menuCollapsedLogoFileList;

    /** 租户logo-菜单栏展开（type=MENU_EXPANDED_LOGO） */
    private List<FileVO> menuExpandedLogoFileList;

}
