package com.qiaomoyun.util;

import com.qiaomoyun.info.LoginUserInfo;
import org.springframework.core.NamedThreadLocal;

/**
 * headerinfo 的信息holder类
 */
public abstract class LoginUserInfoContext {

    private static final ThreadLocal<LoginUserInfo> LoginUserInfoThreadLocalHolder = new NamedThreadLocal("loginUserInfo");

    public LoginUserInfoContext() {
    }

    public static void setLoginUserInfo(LoginUserInfo info) {
        LoginUserInfoThreadLocalHolder.set(info);
    }

    public static LoginUserInfo getLoginUserInfo() {
        return LoginUserInfoThreadLocalHolder.get();
    }

    public static String getToken() {
        return LoginUserInfoThreadLocalHolder.get().getToken();
    }

    public static void setToken(String token) {
        LoginUserInfoThreadLocalHolder.get().setToken(token);
    }

    public static Long getUserId() {
        LoginUserInfo loginUserInfo = LoginUserInfoThreadLocalHolder.get();
        String userid = loginUserInfo==null?null:loginUserInfo.getUserid();
        return userid==null?null:Long.parseLong(userid);
    }

    public static void setUserId(String userId) {
        LoginUserInfoThreadLocalHolder.get().setUserid(userId);
    }

    public static String getIp() {
        LoginUserInfo info = LoginUserInfoThreadLocalHolder.get();
        return info != null ? info.getIp() : null;
    }

    public static String getUa() {
        LoginUserInfo info = LoginUserInfoThreadLocalHolder.get();
        return info != null ? info.getUa() : null;
    }

    /**
     * 清理ThreadLocal，避免内存泄漏
     */
    public static void clear() {
        LoginUserInfoThreadLocalHolder.remove();
    }

    public static void setIsOrganizeData(Boolean isOrganizeData) {
        LoginUserInfo info = LoginUserInfoThreadLocalHolder.get();
        info.setIsOrganizeData(isOrganizeData);
    }

    public static Boolean getIsisOrganizeData() {
        LoginUserInfo info = LoginUserInfoThreadLocalHolder.get();
        return info.getIsOrganizeData();
    }

}
