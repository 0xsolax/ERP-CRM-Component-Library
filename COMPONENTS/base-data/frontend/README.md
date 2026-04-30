# frontend

## 内容

| 目录 | 说明 |
| :--- | :--- |
| `qmy-admin/src/api/zs/base-info/base-data.ts` | 基础数据 API 封装 |
| `qmy-admin/src/views/zs/base-info/field` | 字段管理页面 |
| `qmy-admin/src/components/zs-label-select` | 可编辑基础数据下拉组件 |
| `qmy-admin/src/views/zs/router/async-modules/base-info.ts` | 基础信息路由入口 |

## 页面能力

- `treeNodeList({ bizType: 'FIELD_MGMT' })` 拉取字段管理树。
- 二级节点作为 Tab。
- 三级或可绑定节点作为字段列。
- 普通字段用 `value1` 存值。
- 面料用量节点使用 `value1`、`value2`、`value3`、`value4` 表达尺寸、用量和收缩系数。
- 下拉组件支持新增、重命名、删除基础数据行。

## 接入注意

- API 前缀当前为 `/baseData/*`，接入时需与后端代理统一。
- 路由权限已替换为 `base:data:list`、`base:data:save`；接入目标项目时按本地权限模型映射。
- 可编辑下拉允许即时新增数据，目标项目应确认哪些字段允许前端新增。
