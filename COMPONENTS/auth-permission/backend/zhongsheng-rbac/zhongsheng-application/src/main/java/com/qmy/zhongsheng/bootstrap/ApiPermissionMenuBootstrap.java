package com.qmy.zhongsheng.bootstrap;

import com.qmy.zhongsheng.common.constants.ApiPermissionConstants;
import com.qmy.zhongsheng.common.enums.MenuTypeEnum;
import com.qmy.zhongsheng.core.menu.manager.SystemMenuManager;
import com.qmy.zhongsheng.core.menu.model.entity.SystemMenuDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 启动时将 {@link ApiPermissionConstants} 中尚未出现在 {@code system_menu} 的权限串插入占位行，
 * 便于角色分配与前端后续补全路由、图标等；占位行 {@code type=-1} 表示未知类型。
 *
 * @author AI Coding
 */
@Slf4j
@Component
@Order(100)
@RequiredArgsConstructor
public class ApiPermissionMenuBootstrap implements ApplicationRunner {

    private static final int PLACEHOLDER_SORT = 999;

    /** {@code system_menu.name} 长度上限，超长时截断。 */
    private static final int NAME_MAX_LEN = 50;

    private final SystemMenuManager systemMenuManager;

    @Override
    public void run(ApplicationArguments args) {
        Set<String> permissionCodes = new LinkedHashSet<>(ApiPermissionConstants.scanAllEntries().values());
        int inserted = 0;
        for (String permission : permissionCodes) {
            if (permission == null || permission.isBlank()) {
                continue;
            }
            try {
                if (systemMenuManager.getByPermission(permission, null) != null) {
                    continue;
                }
                systemMenuManager.saveOrUpdate(buildPlaceholder(permission));
                inserted++;
            } catch (Exception ex) {
                log.warn("ApiPermissionMenuBootstrap: skip permission [{}] — {}", permission, ex.getMessage());
            }
        }
        if (inserted > 0) {
            log.info("ApiPermissionMenuBootstrap: inserted {} placeholder menu row(s).", inserted);
        }
    }

    private static SystemMenuDO buildPlaceholder(String permission) {
        SystemMenuDO row = new SystemMenuDO();
        row.setPermission(permission);
        row.setName(truncateForName(permission));
        row.setType(MenuTypeEnum.UNKNOWN.getCode());
        row.setSort(PLACEHOLDER_SORT);
        row.setParentId(0L);
        row.setStatus(0);
        row.setVisible(0);
        row.setKeepAlive(1);
        return row;
    }

    private static String truncateForName(String permission) {
        if (permission.length() <= NAME_MAX_LEN) {
            return permission;
        }
        return permission.substring(0, NAME_MAX_LEN);
    }
}
