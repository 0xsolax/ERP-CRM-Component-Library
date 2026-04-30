package com.qmy.zhongsheng.security;

import com.qmy.zhongsheng.common.constants.ApiPermissionConstants;
import com.qmy.zhongsheng.common.context.LoginUserInfoContext;
import com.qmy.zhongsheng.common.login.LoginUserInfo;
import com.qmy.zhongsheng.common.utils.ValidityUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * 方法安全 SpEL Bean（{@code ss}）。权限串只在 {@link ApiPermissionConstants} 定义一处；
 * 启动时扫描该类所有 {@code public static final String} 缓存，Controller 写
 * {@code @PreAuthorize("@ss.hasPermission(@ss.perm('MATERIAL_PAGE'))")}，参数为常量字段名。
 *
 * @author AI Coding
 */
@Component("ss")
@SuppressWarnings("unused")
public class SpElPermissionService {

    private static final String ALL_PERMISSION = "*";

    private Map<String, String> permByConstantName = Map.of();

    @PostConstruct
    void loadPermissionsFromConstants() {
        this.permByConstantName = ApiPermissionConstants.scanAllEntries();
    }

    /**
     * 按 {@link ApiPermissionConstants} 的字段名取权限串，供 SpEL {@code @ss.perm('XXX')} 使用。
     *
     * @param constantName 与 {@code ApiPermissionConstants} 中 {@code public static final String} 字段名一致
     * @return 权限标识
     */
    public String perm(String constantName) {
        if (ValidityUtils.isBlank(constantName)) {
            return "";
        }
        String key = constantName.trim();
        String value = permByConstantName.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Unknown ApiPermissionConstants field: " + key);
        }
        return value;
    }

    /**
     * 已登录即可（不校验菜单权限），用于退出登录等场景。
     */
    public boolean isLogin() {
        LoginUserInfo user = LoginUserInfoContext.getLoginUserInfo();
        return user != null && user.getUserId() != null;
    }

    /**
     * 拥有任一权限即通过；入参为空视为不限制。
     */
    public boolean hasAnyPermission(String... permissions) {
        return switch (basisForPermissionCheck(permissions)) {
            case PermissionBasis.Unrestricted() -> true;
            case PermissionBasis.AllGranted() -> true;
            case PermissionBasis.Rejected() -> false;
            case PermissionBasis.MatchUserCodes(var codes) -> {
                for (String p : permissions) {
                    if (ValidityUtils.isNotBlank(p) && codes.contains(p.trim())) {
                        yield true;
                    }
                }
                yield false;
            }
        };
    }

    /**
     * 必须拥有指定权限；入参为空或全为空白视为不限制。多参数时为「且」关系。
     */
    public boolean hasPermission(String... permissions) {
        return switch (basisForPermissionCheck(permissions)) {
            case PermissionBasis.Unrestricted() -> true;
            case PermissionBasis.AllGranted() -> true;
            case PermissionBasis.Rejected() -> false;
            case PermissionBasis.MatchUserCodes(var codes) -> {
                for (String p : permissions) {
                    if (p == null || p.isBlank()) {
                        continue;
                    }
                    if (!codes.contains(p.trim())) {
                        yield false;
                    }
                }
                yield true;
            }
        };
    }

    private static PermissionBasis basisForPermissionCheck(String... permissions) {
        if (permissions == null || permissions.length == 0) {
            return new PermissionBasis.Unrestricted();
        }
        Set<String> codes = currentPermissionCodes();
        if (codes == null || codes.isEmpty()) {
            return new PermissionBasis.Rejected();
        }
        if (codes.contains(ALL_PERMISSION)) {
            return new PermissionBasis.AllGranted();
        }
        return new PermissionBasis.MatchUserCodes(codes);
    }

    private sealed interface PermissionBasis
            permits PermissionBasis.Unrestricted,
                    PermissionBasis.Rejected,
                    PermissionBasis.AllGranted,
                    PermissionBasis.MatchUserCodes {

        record Unrestricted() implements PermissionBasis {}

        record Rejected() implements PermissionBasis {}

        record AllGranted() implements PermissionBasis {}

        record MatchUserCodes(Set<String> codes) implements PermissionBasis {}
    }

    private static Set<String> currentPermissionCodes() {
        LoginUserInfo user = LoginUserInfoContext.getLoginUserInfo();
        if (user == null || user.getUserId() == null) {
            return null;
        }
        return user.getPermissions();
    }
}
