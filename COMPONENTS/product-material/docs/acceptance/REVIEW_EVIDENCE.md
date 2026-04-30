# REVIEW_EVIDENCE｜SOL-46 product-material

## 复核入口

| 项 | 值 |
| :--- | :--- |
| 本地仓库路径 | `/Users/solazhu/software/ERP-CRM-Component-Library` |
| 组件目录 | `COMPONENTS/product-material` |
| 关联任务 | `SOL-46 P2｜业务组件：product-material` |
| 当前组件提交 | `aee2ccd P2｜业务组件：product-material` |
| 证据日期 | `2026-04-30` |

## 变更证据

执行命令：

```bash
git show --stat --oneline --name-only aee2ccd -- COMPONENTS/product-material README.md COMPONENTS/README.md wiki/COMP_产品物料基础数据.md wiki/SOURCE_INDEX.md wiki/index.md wiki/log.md
```

关键输出：

```text
aee2ccd P2｜业务组件：product-material
181 files changed, 13714 insertions(+), 27 deletions(-)
```

组件文件数量：

```bash
git ls-files COMPONENTS/product-material | wc -l
# 175
```

本地文件数量：

```bash
find COMPONENTS/product-material -type f | wc -l
# 175
```

## 六大领域抽取对照

| 领域 | 后端证据 | 前端证据 | SQL 证据 |
| :--- | :--- | :--- | :--- |
| 产品 | `backend/zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core/product`，45 个 core 文件；`dto/product` 8 个 DTO | `frontend/qmy-admin/src/views/zs/base-info/product`、`frontend/qmy-admin/src/api/zs/product/index.ts` | `db/zhongsheng-backend/init-product.sql` |
| 伞架 | `backend/zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core/material/controller/UmbrellaFrameController.java` 及 service/manager/dao/entity/VO | `frontend/qmy-admin/src/views/zs/material/umbrella-frame`、`frontend/qmy-admin/src/api/zs/material/umbrella-frame.ts` | `db/zhongsheng-backend/init-umbrella-frame.sql` |
| 面料 | `backend/zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core/material/controller/FabricController.java` 及 service/manager/dao/entity/VO | `frontend/qmy-admin/src/views/zs/material/fabric`、`frontend/qmy-admin/src/api/zs/material/fabric.ts` | `db/zhongsheng-backend/init-fabric.sql` |
| 材料 | `backend/zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core/material/controller/MaterialController.java` 及 category/material service/manager/dao/entity/VO | `frontend/qmy-admin/src/views/zs/material/material`、`frontend/qmy-admin/src/api/zs/material/material.ts` | `db/zhongsheng-backend/init-material.sql` |
| 包材 | `backend/zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core/material/controller/PackagingController.java` 及 service/manager/dao/entity/VO | `frontend/qmy-admin/src/views/zs/material/packaging`、`frontend/qmy-admin/src/api/zs/material/packaging.ts` | `db/zhongsheng-backend/init-packaging.sql` |
| 工序 | `backend/zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core/process`，8 个 core 文件；`dto/process` 2 个 DTO | `frontend/qmy-admin/src/views/zs/base-info/process`、`frontend/qmy-admin/src/api/zs/base-info/process.ts` | `db/zhongsheng-backend/init-process.sql` |

后端 core 文件计数：

```bash
find COMPONENTS/product-material/backend/zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core/product -type f | wc -l
# 45
find COMPONENTS/product-material/backend/zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core/material -type f | wc -l
# 47
find COMPONENTS/product-material/backend/zhongsheng-backend/zhongsheng-core/src/main/java/com/qmy/zhongsheng/core/process -type f | wc -l
# 8
```

前端页面与 API 文件计数：

```bash
find COMPONENTS/product-material/frontend/qmy-admin/src/views/zs COMPONENTS/product-material/frontend/qmy-admin/src/api/zs -type f | wc -l
# 24
```

SQL 清单：

```text
COMPONENTS/product-material/db/zhongsheng-backend/init-fabric.sql
COMPONENTS/product-material/db/zhongsheng-backend/init-material.sql
COMPONENTS/product-material/db/zhongsheng-backend/init-packaging.sql
COMPONENTS/product-material/db/zhongsheng-backend/init-process.sql
COMPONENTS/product-material/db/zhongsheng-backend/init-product.sql
COMPONENTS/product-material/db/zhongsheng-backend/init-umbrella-frame.sql
```

## 字段契约与 SQL 可追溯

字段契约文件：

- `docs/contracts/DATA_CONTRACT.md`
- `docs/contracts/API_CONTRACT.md`
- `docs/contracts/PERMISSION_CONTRACT.md`
- `docs/spec/COMPONENT_SPEC.md`
- `SOURCE_MAP.md`

关键 SQL 追溯：

| SQL 表 | 证据 |
| :--- | :--- |
| `product` | `db/zhongsheng-backend/init-product.sql` |
| `product_type` | `db/zhongsheng-backend/init-product.sql` |
| `product_umbrella_frame` | `db/zhongsheng-backend/init-product.sql` |
| `product_material` | `db/zhongsheng-backend/init-product.sql` |
| `product_fabric` | `db/zhongsheng-backend/init-product.sql` |
| `product_printing` | `db/zhongsheng-backend/init-product.sql` |
| `product_packaging` | `db/zhongsheng-backend/init-product.sql` |
| `product_process_price` | `db/zhongsheng-backend/init-product.sql` |
| `umbrella_frame`、`umbrella_frame_material` | `db/zhongsheng-backend/init-umbrella-frame.sql` |
| `fabric` | `db/zhongsheng-backend/init-fabric.sql` |
| `material_category`、`material` | `db/zhongsheng-backend/init-material.sql` |
| `packaging` | `db/zhongsheng-backend/init-packaging.sql` |
| `process` | `db/zhongsheng-backend/init-process.sql` |

## 成本字段归属

已在 `docs/contracts/DATA_CONTRACT.md` 与 `docs/spec/COMPONENT_SPEC.md` 明确：

| 字段 | 归属 | 说明 |
| :--- | :--- | :--- |
| `product.total_cost` | 产品主档 | 产品默认总成本参考值 |
| `product.selling_price` | 产品主档 | 产品默认售价参考值 |
| `product_* price` | 产品 BOM 子项 | 伞架、材料、面料、印刷、包材、工序的组成项参考价 |
| `product.loss_fee` | 产品主档 | 损耗或杂费参考值 |
| `product_printing.plate_fee` | 产品印刷 | 印刷版费 |
| 客户、数量、汇率、税费、利润、折扣、物流 | 报价上下文 | 不属于产品主档，应由后续 `quote-management` 保存报价时点快照 |

## 权限证据

后端权限常量：

- `backend/zhongsheng-backend/zhongsheng-common/src/main/java/com/qmy/zhongsheng/common/constants/ApiPermissionConstants.java`

关键权限码：

```text
product:product:save
product:product:page
product:product:detail
product:product:remove
material:category:save
material:category:list
material:category:remove
material:material:save
material:material:page
material:material:remove
material:material:list
material:fabric:save
material:fabric:page
material:fabric:list
material:fabric:detail
material:fabric:remove
material:umbrella:save
material:umbrella:page
material:umbrella:detail
material:umbrella:remove
material:umbrella:list
material:packaging:save
material:packaging:saveDefault
material:packaging:page
material:packaging:remove
process:process:save
process:process:page
process:process:list
process:process:remove
```

前端路由证据：

- `frontend/qmy-admin/src/views/zs/router/async-modules/base-info.ts`
- `frontend/qmy-admin/src/views/zs/router/async-modules/material.ts`

快照路由中已无来源占位权限 `sys:role:list`。

## 污染与敏感信息扫描

污染文件扫描：

```bash
find COMPONENTS/product-material \( -name .git -o -name .DS_Store -o -name target -o -name node_modules -o -name build -o -name dist \) -print
# 无输出
```

环境配置扫描：

```bash
find COMPONENTS/product-material -type f \( -name 'application-local.yml' -o -name 'application-dev.yml' -o -name 'application-prod.yml' -o -name '.env' -o -name '*.pem' -o -name '*.key' \) -print
# 无输出
```

## 当前限制

- 本组件是快照证据包，不是独立可编译模块。
- 编译和接口联调需要与 `BASE/project-scaffold`、`base-data`、`file-oss`、`auth-permission` 以及目标项目基座一起装配。
- `/fabric/deteil`、`/packaging/typeList`、`/box-price/list` 为来源事实或前端遗留封装，已在 `API_CONTRACT.md` 和 `ACCEPTANCE.md` 中标为待验证项。
