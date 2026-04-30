package com.qmy.project.common.login;

import lombok.Data;

import java.io.Serializable;

/**
 * @author AI Coding
 * @description
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

    private String loginType;

    private String platform;
}
