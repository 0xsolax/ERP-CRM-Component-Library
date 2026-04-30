# DATA_CONTRACT

## legacy `customer`

来源：`zhongsheng-AI/init.sql`

| 字段 | 含义 |
| :--- | :--- |
| `id` | 主键 |
| `code` | 客户编号 |
| `name` | 客户名称 |
| `contact` | 联系人 |
| `phone` | 联系电话 |
| `email` | 邮箱 |
| `address` | 地址 |
| `status` | 状态，1 启用，0 停用 |
| `remark` | 备注 |
| `type` | 客户类型，后期扩展 |
| `level` | 客户层级，后期扩展 |
| `country` | 国家地区，后期扩展 |

已知不一致：

- `Customer.java` 有 `owner`、`follower` 字段。
- `CustomerServiceImpl.page` 使用 `owner` 做非管理员过滤。
- legacy SQL 未创建 `owner`、`follower` 列。

## qmy-java 主表

来源：`SalYtCustomer.java`、`SalYtCustomerMapper.xml`

| 表 | 关键字段 | 说明 |
| :--- | :--- | :--- |
| `sal_yt_customer` | `code`、`name`、`belong_employee_id`、`follow_employee_id`、`type`、`hand_level`、`country_region`、`company_website`、`company_address`、`company_name`、`customer_source`、`remark`、`order_default_remark` | 客户主档 |
| `sal_yt_customer_address` | `customer_id`、`consignee`、`phone`、`country_region`、`country_region_id`、`region_id`、`province`、`city`、`county`、`detail` | 收货地址 |
| `sal_yt_contact_person` | `customer_id`、`name`、`email`、`position`、`birthday`、`gender`、`remark`、`supplier_id` | 联系人 |
| `sal_yt_contact_person_phone` | `contact_id`、`area_code`、`phone` | 联系人电话 |
| `sal_yt_contact_person_social` | `contact_id`、`social_platform`、`value` | 联系人社交账号 |
| `sal_yt_customer_follow` | `customer_id`、`theme`、`contact_person`、`description`、`next_visit_date` | 跟进记录 |
| `pro_yt_product_label` | `master_id`、`value`、`type` | 通用标签表，客户标签 `type = 4` |
| `sal_yt_customer_store` | `customer_id`、`product_id`、`specification_id`、`status`、`store_warning_number` 等 | 客户独立仓扩展 |
| `sys_user_data_permission` | `user_id`、`permissions`、`is_organize_data` | 用户数据权限配置 |

## 组合返回

`/sal/yt/customer/detail` 返回 Map：

| key | 内容 |
| :--- | :--- |
| `customer` | 客户主档，含归属业务员、跟进人、地区、最近跟进、最近下单等展示字段 |
| `labelList` | 客户标签 |
| `addressList` | 收货地址列表 |
| `followList` | 跟进记录，含附件 |
| `contactPersonList` | 联系人，含头像/名片、社交账号、电话 |

## 前端字段映射

| 前端字段 | 后端字段 |
| :--- | :--- |
| `customerName` | `name` |
| `salesman` | `belongEmployeeId` |
| `follower` | `followEmployeeId` |
| `customerType` | `type` |
| `manualLevel` | `handLevel` |
| `regionId` | `countryRegion` |
| `website` | `companyWebsite` |
| `companyAddress` | `companyAddress` |
| `tags` | `labelList` |
| `addressList` | `customerAddressList` |
| `contactPersonList` | `contactPersonList` |

## 建模建议

- 新项目应优先建立独立客户标签表，除非确认继续复用 `pro_yt_product_label`。
- 地址、联系人、跟进都应有 `customer_id` 外键或等价约束。
- 客户主档应保存归属业务员和跟进人两个字段，用于数据权限。
- 客户导入必须有去重键：客户名称、平台客户 ID、管家婆编码或人工确认后的唯一编码。

