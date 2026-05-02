# API_CONTRACT

## 路径前缀

| 来源 | 前缀 | 说明 |
| :--- | :--- | :--- |
| `zhongsheng-AI` | `/api/customer` | 简版客户 CRUD |
| `qmy-admin` 前端 | `/sal/yt/customer` | 前端 API 封装路径 |
| `qmy-java` 后端 | `api/sal/yt/customer` | 与前端 API 语义匹配，运行时通常由网关或代理补齐 `/api` |

## legacy 客户接口

| 能力 | 方法 | 路径 | 权限/数据范围 |
| :--- | :--- | :--- | :--- |
| 客户分页 | GET | `/api/customer/page` | `isAdmin=true` 看全部，否则按 `owner=username` |
| 客户详情 | GET | `/api/customer/{id}` | 未见接口级权限 |
| 新增客户 | POST | `/api/customer` | 未见接口级权限 |
| 编辑客户 | PUT | `/api/customer` | 未见接口级权限 |
| 删除客户 | DELETE | `/api/customer/{id}` | 未见接口级权限 |

## qmy-java 客户主链接口

| 能力 | 方法 | 路径 | 后端权限 | 数据权限 |
| :--- | :--- | :--- | :--- | :--- |
| 客户列表 | POST | `/sal/yt/customer/list` | `sal:yt:customer:list` | `belong_employee_id OR follow_employee_id` |
| 客户下拉 | POST | `/sal/yt/customer/selectList` | `sal:yt:customer:list` | `belong_employee_id OR follow_employee_id` |
| 客户详情 | GET | `/sal/yt/customer/detail` | `sal:yt:customer:detail` | `belong_employee_id OR follow_employee_id` |
| 新增客户 | POST | `/sal/yt/customer/save` | `sal:yt:customer:save` | 无额外数据范围 |
| 修改客户主表 | POST | `/sal/yt/customer/update` | `sal:yt:customer:update` | `belong_employee_id OR follow_employee_id` |
| 删除客户 | DELETE | `/sal/yt/customer/delete` | `sal:yt:customer:delete` | `belong_employee_id OR follow_employee_id` |
| 校验联系人 | POST | `/sal/yt/customer/validateContact` | 未见权限注解 | 无 |
| 批量删除客户 | POST | `/sal/yt/customer/batchDelete` | `sal:yt:customer:batchDelete` | `belong_employee_id OR follow_employee_id` |

## 联系人、地址、标签、跟进接口

| 能力 | 方法 | 路径 | 后端权限 | 数据权限/范围策略 |
| :--- | :--- | :--- | :--- | :--- |
| 新增/编辑地址 | POST | `/sal/yt/customer/createOrUpdateAddress` | `sal:yt:customer:updateAddress` | 先校验 `customerId` 的客户主档范围；编辑时同时校验地址真实归属 |
| 删除地址 | DELETE | `/sal/yt/customer/address/delete` | `sal:yt:customer:address:delete` | 解析地址所属客户，校验客户主档范围；前端同步传 `customerId` 做一致性校验 |
| 查询地址列表 | GET | `/sal/yt/customer/addressList` | `sal:yt:customer:detail` | 先校验客户主档范围，再查地址 |
| 新增/编辑联系人 | POST | `/sal/yt/customer/createOrUpdateContactPerson` | `sal:yt:customer:updateContactPerson` | 先校验 `customerId` 的客户主档范围；编辑时同时校验联系人真实归属 |
| 删除联系人 | DELETE | `/sal/yt/customer/contact/delete` | `sal:yt:customer:contact:delete` | 解析联系人所属客户，校验客户主档范围；前端同步传 `customerId` 做一致性校验 |
| 添加标签 | POST | `/sal/yt/customer/addLabel` | `sal:yt:customer:addLabel` | 先校验 `masterId` 对应客户范围；编辑既有标签时同时校验标签真实归属 |
| 删除标签 | DELETE | `/sal/yt/customer/deleteLabel` | `sal:yt:customer:deleteLabel` | 解析客户标签所属客户，校验客户主档范围；前端同步传 `customerId` 做一致性校验 |
| 新增/编辑跟进 | POST | `/sal/yt/customer/follow` | `sal:yt:customer:follow` | 先校验 `customerId` 的客户主档范围；编辑时同时校验跟进记录真实归属 |
| 删除跟进 | DELETE | `/sal/yt/customer/follow/delete` | `sal:yt:customer:follow:delete` | 解析跟进记录所属客户，校验客户主档范围；前端同步传 `customerId` 做一致性校验 |

## 扩展接口

| 能力 | 方法 | 路径 | 说明 |
| :--- | :--- | :--- | :--- |
| 启用独立仓 | POST | `/sal/yt/customer/enableStore` | 权限 `sal:yt:customer:enableStore` |
| 审核独立仓 | POST | `/sal/yt/customer/auditStore` | 权限 `sal:yt:customer:auditStore` |
| 客户规格列表 | POST | `/sal/yt/customer/specificationList` | 权限注解被注释 |
| 更新独立仓状态 | POST | `/sal/yt/customer/updateStoreStatus` | 权限注解被注释 |
| 自动客户层级 | POST | `/sal/yt/customer/setAutoLevel` | 权限 `sal:yt:customer:setAutoLevel` |
| VIP 客户列表 | POST | `/sal/yt/customer/vipList` | 权限注解被注释 |
| 设置 VIP | POST | `/sal/yt/customer/setVip` | 权限注解被注释 |
| 消费趋势 | POST | `/sal/yt/customer/getConsumptionTrends` | 权限注解被注释 |
| 消费占比 | POST | `/sal/yt/customer/getConsumptionRatio` | 权限注解被注释 |
| 独立仓历史流向 | POST | `/sal/yt/customer/storeRecord` | 权限注解被注释 |

## 接入要求

- 客户子资源写接口必须同时具备方法级权限和客户主档数据范围校验；不能只依赖前端按钮权限。
- 地址、联系人、标签、跟进的删除接口应传入 `customerId`，后端仍会解析子记录真实归属并拒绝不一致请求。
- 列表、详情、下拉选择和报价/订单引用客户的接口必须使用一致的数据权限口径。
- 删除接口必须使用 DELETE 或带请求体的 POST，避免 GET 删除。
