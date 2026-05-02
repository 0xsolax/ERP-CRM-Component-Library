# PERMISSION_CONTRACT｜warehouse-delivery

## 前端路由权限

| 路由 | 权限码 | 说明 |
| :--- | :--- | :--- |
| `/warehouse/inventory` | `sto:yt:store:list` | 实时库存 |
| `/warehouse/inventory/history` | `sto:yt:store:list` | 库存历史 |
| `/warehouse/inbound` | `sto:yt:order:list` | 入库列表 |
| `/warehouse/inbound/record` | `sto:yt:order:list` | 入库记录 |
| `/warehouse/inbound/add` | `sto:yt:order:addStore` | 新增入库 |
| `/warehouse/shipping` | `sto:yt:delivery:list` | 发货列表 |
| `/warehouse/logistics` | `sto:yt:transportCompany:list` | 物流公司 |
| `/warehouse/packing` | `sto:yt:box:list` | 打包箱 |

## 后端权限现状

### 已声明方法权限

- `StoYtStoreController`：`sto:yt:store:list`、`sto:yt:store:history`、`sto:yt:store:setWarning`。
- `StoYtStoreOrderController`：`sto:yt:order:addStore`、`sto:yt:order:list`、`sto:yt:order:enter`。
- `StoYtDeliveryController`：`sto:yt:delivery:list`、`sto:yt:delivery:detail`、`sto:yt:delivery:takePackage`、`sto:yt:delivery:completePackage`、`sto:yt:delivery:returnWaitPackage`。
- `StoYtBoxController`：`sto:yt:box:saveOrUpdate`、`sto:yt:box:list`、`sto:yt:box:detail`、`sto:yt:box:delete`。
- `StoYtTransportCompanyController`：`sto:yt:transportCompany:add`、`update`、`delete`、`get`、`list`。

### 待补齐权限

- `StoYtStoreController`：库存占用详情、在途占用详情、获取预警规则、出入库记录、规格预警、`warning/test`。
- `StoYtStoreOrderController`：新增入库单、入库进度、批量入库。
- `StoYtDeliveryController`：发货单订单/子订单详情、导出、扫码、暂存打包、包裹列表、包裹明细、确认发货、更新物流、完成打包校验、发送打包消息。
- `StoYtBoxController`：箱规下拉。
- `StoYtTransportCompanyController`：分页查询。
- `StoYtLocationController`：库位下拉、新增库位。

## 数据范围要求

当前快照未发现 `@RequiresDataPermissions`。目标项目必须补齐以下口径：

| 视角 | 可见范围 |
| :--- | :--- |
| 本人 | 自己负责订单、采购、发货或仓储操作产生的数据 |
| 部门 | 本部门订单、采购和仓储履约数据 |
| 全公司 | 当前租户内所有仓储发货数据 |
| 老板视角 | 全局可见，但仍需租户隔离和审计 |

## HTTP 方法要求

- `GET /sto/yt/box/delete` 必须改为 `DELETE /sto/yt/box/{id}` 或受控 `POST /delete`。
- `GET /sto/yt/transportCompany/delete/{id}` 必须改为 `DELETE /sto/yt/transportCompany/{id}` 或受控 `POST /delete`。
- 所有状态变更接口必须补操作日志和状态前置校验。

## 接入前验收

- 直接调用无权限接口应被拦截。
- 非本人/非部门数据应按数据范围过滤或拒绝。
- 发货导出、包裹明细、运费信息不能越权读取。
- 删除和确认发货必须写审计记录。
