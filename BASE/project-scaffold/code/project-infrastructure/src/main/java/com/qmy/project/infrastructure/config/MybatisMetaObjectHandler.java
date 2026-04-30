package com.qmy.project.infrastructure.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.qmy.project.common.context.LoginUserInfoContext;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 插入/更新自动填充：{@code createUser} / {@code updateUser} 优先取当前登录用户 id，无登录态时为 {@code -1L}（与库表 NOT NULL 一致）；{@code isDeleted} 与 {@code deleted_time} 联动。
 *
 * @author AI Coding
 * @description
 * @date 2026/03/20 09:49
 */
@Component
public class MybatisMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        long uid = LoginUserInfoContext.currentUserIdOrDefault(-1L);
        this.strictInsertFill(metaObject, "createUser", Long.class, uid);
        this.strictInsertFill(metaObject, "updateUser", Long.class, uid);
        this.strictInsertFill(metaObject, "id", Long.class, null);
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "isDeleted", Integer.class, 0);
        syncDeletedTimeWithDeletedFlag(metaObject, true);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        long uid = LoginUserInfoContext.currentUserIdOrDefault(-1L);
        this.strictUpdateFill(metaObject, "updateUser", Long.class, uid);
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        syncDeletedTimeWithDeletedFlag(metaObject, false);
    }

    /**
     * @param insert {@code true} 为插入：{@code isDeleted==1} 时写删除时间；{@code false} 为更新：
     *               {@code isDeleted==null} 不处理 {@code deletedTime}；{@code isDeleted==1} 且原时间为空时打时间戳；
     *               否则清空 {@code deletedTime}（含恢复未删）.
     */
    private void syncDeletedTimeWithDeletedFlag(MetaObject metaObject, boolean insert) {
        if (!metaObject.hasSetter("deletedTime")) {
            return;
        }
        Object delVal = metaObject.getValue("isDeleted");
        if (!(delVal instanceof Integer isDeleted)) {
            return;
        }
        if (insert) {
            if (isDeleted == 1) {
                metaObject.setValue("deletedTime", LocalDateTime.now());
            }
            return;
        }
        if (isDeleted == 1) {
            Object old = metaObject.getValue("deletedTime");
            if (old == null) {
                metaObject.setValue("deletedTime", LocalDateTime.now());
            }
        } else {
            metaObject.setValue("deletedTime", null);
        }
    }
}
