# Backend Snapshot

后端快照包含：

- `core/document/**`
- `core/system/**`
- `api/dto/document/**`
- `api/dto/system/**`
- `DocumentErrorCodeConstants.java`

业务单据调用本组件时仍需在自身 service 内写入动作日志，并实现对应的可见性与状态校验。
