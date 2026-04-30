package com.qmy.zhongsheng.common.login;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * @author AI Coding
 * @description LoginUserInfo
 * @date 2026/03/20 09:49
 */
@Data
public class LoginUserInfo implements Serializable {

    private Long userId;

    private String userName;

    private String nickname;

    private String email;

    private String mobile;

    private Integer status;

    private Integer gender;

    private String avatarUrl;

    private String tokenId;

    private String token;

    private String loginType;

    private String platform;

    /**
     * 当前请求用户拥有的权限标识（登录解析时填充）；超管为含 {@code *} 的集合。
     */
    private Set<String> permissions;
}
