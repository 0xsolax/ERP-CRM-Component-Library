# DB Snapshot

核心 SQL：

- `qmy-zhongsheng-ai/init-document-action.sql`

核心表：

- `document_action_log`
- `document_unlock_request`

业务单据自身还需要保存 `lock_state`、`needs_reconfirm` 和 `reconfirm_scope_json` 等字段。
