# ACCEPTANCE｜product-material

复核证据见：[REVIEW_EVIDENCE.md](REVIEW_EVIDENCE.md)。

## SOL-46 快照结论

- [x] 快照级验收已完成：六大领域来源、后端、前端、SQL、契约、权限、来源映射和污染扫描均已闭环。
- [x] 复核证据已补充到组件目录，需随本次修正一起纳入 Git，避免只存在本地未跟踪文件。
- [x] 接入级验收不作为 SOL-46 快照任务关闭条件；它依赖目标基座、数据库、权限、基础数据和文件 OSS 装配。
- [x] 已知接口差异已给出处理策略，见“接口差异处理策略”。

## 快照验收

- [x] `backend/zhongsheng-backend/` 包含产品、材料、面料、包材、伞架、工序 controller/service/manager/dao/entity/VO/DTO。
- [x] `backend/zhongsheng-backend/` 包含产品物料相关错误码、权限常量和基础数据节点枚举引用。
- [x] `frontend/qmy-admin/` 包含产品、工价、伞架、面料、材料、包材页面和 API。
- [x] `frontend/qmy-admin/` 包含产品/工序/材料路由入口，且路由权限不再使用 `sys:role:list` 占位。
- [x] `db/` 包含产品、产品 BOM、材料、面料、包材、伞架、工序 SQL。
- [x] `docs/contracts/` 覆盖 API、数据、权限契约。
- [x] `SOURCE_MAP.md` 标明来源、已复制范围、依赖和已知缺口。
- [x] 不包含 `.git/`、`.DS_Store`、`target/`、`node_modules/`、`build/`、`dist/`。
- [x] 不包含真实数据库连接、token、密钥或本地环境配置。

## 接口差异处理策略

- [x] `/fabric/deteil` 保留来源兼容，同时新增标准路径 `/fabric/detail`，两者共用 `FabricController.getDetail`。
- [x] `/packaging/typeList` 前端未被页面使用；快照已移除该 API 封装，包材类型继续通过 `base-data` 的 `listByNodeKey({ nodeKey: 'PACKAGING_TYPE' })` 获取。
- [x] `/box-price/list` 前端未被页面使用；快照已移除该 API 封装，纸箱单价列表使用 `/packaging/page` 加 `defaultTypeFlag = 1` 查询。
- [x] 纸箱单价保存继续使用 `/packaging/saveOrUpdateDefaultPaperBox`。

## 接入验收（后续装配演练）

- [ ] 已执行 `base-data`、`file-oss` 和本组件 SQL。
- [ ] 基础数据节点已包含产品类型、面料、包材、伞架规格、印刷方式、对齐方式。
- [ ] 已登录有权限用户可以新增产品，并保存产品主档、伞架、材料、面料、印刷、包材、工序。
- [ ] 产品详情能回显所有 BOM 子项和图片。
- [ ] 产品分页能按产品编号、产品类型、伞架、面料、印刷方式等条件筛选。
- [ ] 伞架、面料、材料、包材、工序的新增、分页、删除可用。
- [ ] 无权限用户访问产品、材料、工序接口被后端拒绝。
- [ ] 产品、材料、伞架图片能通过 `file-oss` 上传并保存文件记录。
- [ ] 删除产品或物料时不会破坏历史报价、订单或采购引用。
- [ ] 报价组件接入时复制产品和 BOM 快照，不直接依赖产品实时成本。

## 业务待确认项

- [ ] 包材成本公式是否进入产品主档，或仅进入报价组件。
- [ ] 百货、机械等非伞类产品是否与伞类产品共表，或按业务线拆分。

## 当前快照未运行验证

- 未对抽取快照做独立编译，因为它依赖 `base-data`、`file-oss`、`auth-permission` 和目标基座。
- 未执行后端接口联调，因为 SOL-46 范围是组件快照抽取，不是目标基座装配。
- 未执行前端浏览器流程，因为需要目标项目路由、权限、基础数据和文件上传配置。
