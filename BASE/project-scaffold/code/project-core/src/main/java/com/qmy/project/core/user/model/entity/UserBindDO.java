package com.qmy.project.core.user.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.qmy.project.infrastructure.model.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户与第三方账号绑定。同一 {@link #userId} 下可绑定条数由库表唯一键决定：
 * {@code (user_id, platform, is_deleted, deleted_time)} 时为每平台一条（一对多）；
 * 仅 {@code (user_id, is_deleted, deleted_time)} 时为每用户全局一条（一对一）。
 *
 * @author AI Coding
 * @description
 * @date 2026/03/20 09:49
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_bind")
public class UserBindDO extends BaseDO {

    @TableField("user_id")
    private Long userId;

    @TableField("platform")
    private String platform;

    @TableField("union_id")
    private String unionId;

    @TableField("open_id")
    private String openId;

    @TableField("third_user_id")
    private String thirdUserId;

    /** 第三方账号昵称，与 {@code user.nick_name} 区分 */
    @TableField("third_nickname")
    private String thirdNickname;

    @TableField("raw_info")
    private String rawInfo;

    @TableField("last_auth_time")
    private LocalDateTime lastAuthTime;
}
