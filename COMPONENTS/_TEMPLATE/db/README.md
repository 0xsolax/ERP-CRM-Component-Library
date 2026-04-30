# db

放置当前组件需要的数据结构、初始化数据和迁移片段。

建议结构：

```text
db/
  schema.sql
  seed.sql
  migration-notes.md
```

## 要求

- 表结构应标注来源 SQL。
- 初始化数据应区分必需数据和演示数据。
- 逻辑删除、唯一键、状态字段、审计字段要写入 `docs/contracts/DATA_CONTRACT.md`。
- 涉及迁移时，必须说明是否可重复执行。

