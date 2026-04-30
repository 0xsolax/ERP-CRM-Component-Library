# backend

放置后端代码快照。

建议按来源项目保留必要分层，例如：

```text
backend/
  controller/
  service/
  manager/
  dao/
  dto/
  vo/
  domain/
  config/
```

## 要求

- 只放与当前组件相关的代码。
- 保留必要包路径说明，避免脱离来源后无法定位。
- 对跨组件依赖写入 `component.yaml` 的 `dependencies`。
- 如后端代码只作为参考，必须在 README 或 `SOURCE_MAP.md` 中标记。

