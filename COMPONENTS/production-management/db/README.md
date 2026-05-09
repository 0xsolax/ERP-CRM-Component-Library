# DB Snapshot

核心 SQL：

- `qmy-zhongsheng-ai/init-production.sql`

核心表：

- `production_group`
- `production_order`
- `production_order_progress`
- `production_order_batch`

接入公共单据动作后，生产总单还依赖 `lock_state`、`needs_reconfirm`、`reconfirm_scope_json` 等锁定字段口径。
