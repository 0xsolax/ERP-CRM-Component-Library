# ACCEPTANCE｜base-data

## 快照验收

- [ ] `backend/project-scaffold/` 包含 DTO、枚举、初始化器、错误码、controller/service/manager/dao/entity/VO。
- [ ] `backend/zhongsheng-backend/` 包含中圣业务节点、权限注解、重复校验和默认值保护。
- [ ] `frontend/qmy-admin/` 包含基础数据 API、字段管理页面、可编辑下拉组件和路由入口。
- [ ] `db/` 包含 `base_tree_node`、`base_data` DDL。
- [ ] `docs/contracts/` 覆盖 API、数据、权限契约。
- [ ] 不包含 `.git/`、`.DS_Store`、`target/`、`node_modules/`、`build/`、`dist/`。
- [ ] 不包含真实数据库密码、token、密钥或本地环境配置。

## 接入验收

- [ ] 应用启动后 `BaseTreeNodeDataInitializer` 能幂等补齐节点。
- [ ] `treeNodeList({ bizType: 'FIELD_MGMT' })` 返回字段管理树。
- [ ] `list({ nodeIds: [...] })` 返回对应节点下未删除数据。
- [ ] `listByNodeKey({ nodeKey })` 能支撑下拉选项。
- [ ] `saveOrUpdate` 新增时必须校验 `nodeId`。
- [ ] 不允许在 `data_bind_flag = 0` 的节点上挂数据。
- [ ] 启用仅叶子绑定时，不允许在非叶子节点上挂数据。
- [ ] 空节点、空列表、无数据返回为空集合而不是异常。
- [ ] 删除使用逻辑删除，并且列表不再返回已删除数据。
- [ ] 业务已引用的基础数据删除前有引用保护。

## 权限验收

- [ ] 无权限用户不能新增、编辑、删除基础数据。
- [ ] 只有配置人员能进入字段管理页面。
- [ ] 普通业务用户只能使用被授权的下拉查询能力。
- [ ] 前端按钮隐藏和后端接口拒绝同时生效。
- [ ] 路由权限不再使用来源占位值 `sys:role:list`。

## 当前快照未验证

- 未对抽取快照做编译，因为它是多来源拼装证据包，不是单一工程。
- 未执行浏览器字段管理页面，因为 SOL-44 目标是组件快照抽取。
- `batchSaveOrUpdate` 前端封装未匹配到中圣后端接口，接入时需处理。
