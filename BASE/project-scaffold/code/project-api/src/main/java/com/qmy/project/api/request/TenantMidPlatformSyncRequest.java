package com.qmy.project.api.request;

import com.qmy.project.api.dto.file.SystemFileDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 中台系统同步当前实例租户配置与租户维度系统文件；对应本服务
 * {@code POST .../external/tenant/mid-platform/sync} 的请求体。
 *
 * @author AI Coding
 */
@Data
@Schema(description = "中台租户配置与文件同步请求")
public class TenantMidPlatformSyncRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "租户 id 不能为空")
    @Schema(description = "租户 id，须与 tenant_config 中 tenant.id 一致", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "租户名称")
    private String name;

    @Schema(description = "业务域名")
    private String domainName;

    @Schema(description = "租户状态")
    private String status;

    @Schema(description = "租户 slogan")
    private String slogan;

    // ==================== 账号系统相关 =========================

    @Schema(description = "账号体系 Key，如 DingDing")
    private String accountSystemKey;

    @Schema(description = "租户超级管理员姓名")
    private String superAdmin;

    @Schema(description = "租户超级管理员在第三方账号体系中的用户 id；与 accountSystemKey 配合使用（如 DingDing 对应钉钉 userId，FeiShu 对应飞书 open_id 等）")
    private String thirdBindAdminUserId;

    @Schema(description = "钉钉 AppKey")
    private String dingTalkAppKey;

    @Schema(description = "钉钉 AppSecret")
    private String dingTalkAppSecret;

    @Schema(description = "飞书 AppId")
    private String feiShuAppId;

    @Schema(description = "飞书 AppSecret")
    private String feiShuAppSecret;


    // ===================== 租户文件相关 =============================

    @Schema(description = "登录页 Logo 文件列表")
    private List<SystemFileDTO> loginLogoFileList;

    @Schema(description = "菜单栏收起 Logo 文件列表")
    private List<SystemFileDTO> menuCollapsedLogoFileList;

    @Schema(description = "菜单栏展开 Logo 文件列表")
    private List<SystemFileDTO> menuExpandedLogoFileList;

    @Schema(description = "背景图文件列表")
    private List<SystemFileDTO> backgroundFileList;
}
