# db

## 内容

- `zhongsheng-AI/init-customer.sql`：从 legacy `init.sql` 抽取的 `customer` 表和客户扩展字段。
- `docs/contracts/DATA_CONTRACT.md`：根据 `qmy-java` 实体、Mapper XML 和前端字段整理的客户管理数据契约。

## 重要差异

- `zhongsheng-AI` legacy `customer` 表只有主档基础字段，未覆盖联系人、收货地址、标签、跟进记录。
- `zhongsheng-AI` legacy `Customer` 实体有 `owner`、`follower`，但 SQL 未建列。
- `qmy-java` 提供更完整实体和 Mapper，但当前 RAW 来源未提供完整建表 SQL。

## 接入建议

正式项目应以 `DATA_CONTRACT.md` 为目标结构，再由目标技术栈生成迁移脚本。不要直接把 legacy `customer` 表当作完整 CRM 客户模型。

