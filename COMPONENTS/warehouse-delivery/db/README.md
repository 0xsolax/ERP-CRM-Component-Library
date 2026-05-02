# db｜warehouse-delivery

本组件未找到 qmy-java 仓储发货完整 DDL。当前数据库材料只包含从 entity/mapper/controller 推导的表结构说明，不能作为正式迁移脚本使用。

## 文件

- [qmy-java/sto-yt-warehouse-schema-notes.md](qmy-java/sto-yt-warehouse-schema-notes.md)

## 接入前必须补齐

- 正式建表 SQL。
- 租户字段、软删除字段、审计字段和索引。
- 库存唯一键：至少应明确公共仓/客户仓、产品、规格、库位的唯一约束。
- 库存扣减和流水写入的事务边界。
- 发货单、包裹、包裹明细、收款/运费导入的跨组件外键或逻辑关联。
