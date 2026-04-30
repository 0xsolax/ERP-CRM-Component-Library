# SOURCE_MAP｜base-data

## 来源摘要

| 来源 | 用途 |
| :--- | :--- |
| `RAW/PROJECTs/project-scaffold` | 通用基础数据基座、DDL、节点规则、初始化器 |
| `RAW/PROJECTs/zhongsheng-backend` | 中圣业务版基础数据后端、权限注解、重复性校验、业务节点种子 |
| `RAW/PROJECTs/qmy-admin` | 字段管理页面、基础数据 API、可编辑下拉组件、路由入口 |

## 已复制范围

### project-scaffold

| 来源 | 快照 | 说明 |
| :--- | :--- | :--- |
| `project-api/.../dto/base` | `backend/project-scaffold/project-api/.../dto/base` | 保存、批量保存、列表、nodeKey、树节点查询 DTO |
| `project-application/.../BaseTreeNodeDataInitializer.java` | `backend/project-scaffold/project-application/.../BaseTreeNodeDataInitializer.java` | 启动时幂等补齐 `base_tree_node` |
| `project-common/.../BaseTreeBizTypeEnum.java` | `backend/project-scaffold/project-common/.../BaseTreeBizTypeEnum.java` | 业务类型与叶子绑定规则 |
| `project-common/.../BaseTreeNodeSeedEnum.java` | `backend/project-scaffold/project-common/.../BaseTreeNodeSeedEnum.java` | 通用种子示例 |
| `project-common/.../BaseDataErrorCodeConstants.java` | `backend/project-scaffold/project-common/.../BaseDataErrorCodeConstants.java` | 基础数据错误码 |
| `project-core/.../core/base` | `backend/project-scaffold/project-core/.../core/base` | controller/service/manager/dao/entity/VO |
| `docs/sql/init-base-data.sql` | `db/project-scaffold/init-base-data.sql` | 通用基础数据 DDL |

### zhongsheng-backend

| 来源 | 快照 | 说明 |
| :--- | :--- | :--- |
| `zhongsheng-api/.../dto/base` | `backend/zhongsheng-backend/zhongsheng-api/.../dto/base` | 中圣 DTO |
| `zhongsheng-application/.../BaseTreeNodeDataInitializer.java` | `backend/zhongsheng-backend/zhongsheng-application/.../BaseTreeNodeDataInitializer.java` | 中圣节点种子初始化器 |
| `zhongsheng-common/.../BaseDataConstants.java` | `backend/zhongsheng-backend/zhongsheng-common/.../BaseDataConstants.java` | 默认标记常量 |
| `zhongsheng-common/.../ApiPermissionConstants.java` | `backend/zhongsheng-backend/zhongsheng-common/.../ApiPermissionConstants.java` | 基础数据权限码来源 |
| `zhongsheng-common/.../BaseTreeBizTypeEnum.java` | `backend/zhongsheng-backend/zhongsheng-common/.../BaseTreeBizTypeEnum.java` | 中圣业务类型 |
| `zhongsheng-common/.../BaseTreeNodeSeedEnum.java` | `backend/zhongsheng-backend/zhongsheng-common/.../BaseTreeNodeSeedEnum.java` | 字段、面料、包材、产品等节点种子 |
| `zhongsheng-common/.../BaseDataErrorCodeConstants.java` | `backend/zhongsheng-backend/zhongsheng-common/.../BaseDataErrorCodeConstants.java` | 中圣错误码 |
| `zhongsheng-core/.../core/base` | `backend/zhongsheng-backend/zhongsheng-core/.../core/base` | controller/service/manager/dao/entity/VO |
| `docs/sql/init-base-data.sql` | `db/zhongsheng-backend/init-base-data.sql` | 中圣基础数据 DDL |

### qmy-admin

| 来源 | 快照 | 说明 |
| :--- | :--- | :--- |
| `src/api/zs/base-info/base-data.ts` | `frontend/qmy-admin/src/api/zs/base-info/base-data.ts` | 基础数据 API 封装 |
| `src/views/zs/base-info/field` | `frontend/qmy-admin/src/views/zs/base-info/field` | 字段管理页面 |
| `src/components/zs-label-select` | `frontend/qmy-admin/src/components/zs-label-select` | 可编辑基础数据下拉组件 |
| `src/views/zs/router/async-modules/base-info.ts` | `frontend/qmy-admin/src/views/zs/router/async-modules/base-info.ts` | 基础信息路由入口 |

## 已排除或清理

| 内容 | 处理 | 原因 |
| :--- | :--- | :--- |
| `.git/`、`.DS_Store`、`target/`、`node_modules/`、`build/`、`dist/` | 未复制 | 污染文件或构建缓存 |
| `application-local.yml`、`application-dev.yml`、`application-prod.yml`、`.env` | 未复制 | 可能包含真实环境配置 |
| `qmy-admin` 产品/工价/其他基础信息页面 | 未复制 | SOL-44 范围只要求字段/分类和基础数据入口 |
| 业务模块对 `baseDataId` 的引用代码 | 未复制 | 作为后续产品物料组件的接口边界，不并入基础组件 |

## 事实与判断

- `base_tree_node` 是分类树，`base_data` 是挂在节点上的数据行，二者通过 `base_data.node_id` 关联。
- `node_key` 是前后端稳定绑定锚点，适合在新项目中作为下拉配置 key。
- `data_bind_flag = 1` 才允许挂数据；中圣版 `BaseTreeBizTypeEnum` 对多个业务类型启用仅叶子节点可绑定。
- 中圣版 `BaseDataServiceImpl` 增加了重复校验、默认数据保护和逻辑删除。
- `qmy-admin` 字段管理页面使用 `FIELD_MGMT` 加载节点，面料用量节点用 `value1`、`value2`、`value3`、`value4` 表达尺寸、数量和系数。
- 来源前端路由权限使用 `sys:role:list`；本快照已替换为 `base:data:*`，不再保留占位权限。
- 来源服务层对空请求体和未知 `nodeKey` 防御不足；本快照已补齐空态防御。
