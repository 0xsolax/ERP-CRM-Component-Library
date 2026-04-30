# PERMISSION_CONTRACT

## 页面权限

| 权限码 | 用途 |
| :--- | :--- |
| `sal:yt:customer:list` | 客户列表、客户下拉 |
| `sal:yt:customer:save` | 新增客户页面和保存接口 |
| `sal:yt:customer:detail` | 客户详情页面和详情接口 |
| `sal:yt:customer:update` | 编辑客户主档 |
| `sal:yt:customer:delete` | 删除客户 |

## 子资源权限

| 权限码 | 用途 | 来源状态 |
| :--- | :--- | :--- |
| `sal:yt:customer:updateAddress` | 新增/编辑客户地址 | 后端启用 |
| `sal:yt:customer:updateContactPerson` | 新增/编辑联系人 | 后端启用 |
| `sal:yt:customer:setAutoLevel` | 设置自动客户层级 | 后端启用 |
| `sal:yt:customer:enableStore` | 启用独立仓 | 后端启用 |
| `sal:yt:customer:auditStore` | 审核独立仓 | 后端启用 |
| `sal:yt:customer:addLabel` | 客户贴标签 | 来源中注释，建议补齐 |
| `sal:yt:customer:deleteLabel` | 删除客户标签 | 来源中注释，建议补齐 |
| `sal:yt:customer:follow` | 新增/编辑跟进记录 | 来源中注释，建议补齐 |
| `sal:yt:customer:follow:delete` | 删除跟进记录 | 来源中注释，建议补齐 |
| `sal:yt:customer:address:delete` | 删除地址 | 来源中注释，建议补齐 |
| `sal:yt:customer:contact:delete` | 删除联系人 | 来源中注释，建议补齐 |

## 数据权限

qmy-java 客户列表、详情、客户下拉的源代码证据：

```text
@RequiresDataPermissions(
  conditions = [
    belong_employee_id OR follow_employee_id
  ]
)
```

解释：

- 本人：当前用户 ID 出现在 `belong_employee_id` 或 `follow_employee_id`。
- 部门：当前用户是部门负责人时，`AuthenticationInterceptor` 把部门成员加入可见用户集合。
- 全公司：用户数据权限为全部数据，或租户未开启该权限的数据范围过滤。
- 老板视角：业务上应映射为全公司数据范围，并用角色权限显式授权。

## 正式接入要求

- 前端 `v-permission` 只控制按钮展示，不是安全边界。
- 所有修改、删除、标签、跟进、地址、联系人接口必须补齐后端权限注解。
- 报价、订单、采购、发货中引用客户时，要继承同一客户数据权限策略。
- 对客户姓名、联系人、电话、邮箱、地址做脱敏展示时，不能跳过接口权限和数据范围检查。

