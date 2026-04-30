# 来源文档｜base-data

## 原始规范来源

| 来源 | 说明 |
| :--- | :--- |
| `RAW/PROJECTs/project-scaffold/README.md` | 描述 `base_tree_node`、`base_data`、叶子节点绑定、接口清单和错误码 |
| `RAW/PROJECTs/project-scaffold/docs/sql/init-base-data.sql` | 通用基础数据 DDL |
| `RAW/PROJECTs/zhongsheng-backend/docs/sql/init-base-data.sql` | 中圣基础数据 DDL |
| `RAW/PROJECTs/zhongsheng-backend/zhongsheng-common/.../BaseTreeNodeSeedEnum.java` | 中圣业务节点种子 |
| `RAW/PROJECTs/qmy-admin/src/views/zs/base-info/field/index.vue` | 字段管理页面行为 |

## 抽取判断

- `project-scaffold` 是通用基座参考，节点种子较少，适合新项目重建。
- `zhongsheng-backend` 是业务版参考，字段、面料、包材、产品节点更完整。
- `qmy-admin` 字段管理页面可作为“基础数据前端维护台”的第一版参考。

## 待验证

- 来源前端路由权限是否曾被真实权限系统替换过，本快照只保留现状并标记为占位。
- `batchSaveOrUpdate` 前端 API 存在，但中圣 controller 快照未暴露对应接口，接入时应二选一：补后端批量接口或删除前端未用封装。
