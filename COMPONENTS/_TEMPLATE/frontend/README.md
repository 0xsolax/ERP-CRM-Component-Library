# frontend

放置前端代码快照。

建议按来源项目保留必要分层，例如：

```text
frontend/
  views/
  api/
  router/
  store/
  components/
  hooks/
  constants/
```

## 要求

- 页面、API 封装、路由和私有组件要成组抽取。
- 组件依赖的通用 UI 组件应写入 `component.yaml` 的依赖项。
- 如果只有前端页面、没有后端或数据契约，组件状态应保持 `draft` 或 `reference`。

