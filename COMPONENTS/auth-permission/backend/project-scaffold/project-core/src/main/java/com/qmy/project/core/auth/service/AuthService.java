package com.qmy.project.core.auth.service;

import com.qmy.project.api.dto.PasswordLoginDTO;
import com.qmy.project.api.dto.ScanLoginDTO;
import com.qmy.project.core.auth.model.vo.UserLoginVO;

/**
 * @author AI Coding
 * @description
 * @date 2026/03/20 09:49
 */
public interface AuthService {

    UserLoginVO loginByPassword(PasswordLoginDTO loginDTO);

    UserLoginVO loginByScan(ScanLoginDTO param, String referer);

    void logout();
}
