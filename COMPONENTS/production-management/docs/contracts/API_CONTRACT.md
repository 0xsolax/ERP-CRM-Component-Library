# API Contract

| 接口 | 说明 | 权限 |
| :--- | :--- | :--- |
| `POST /production/group/page` | 生产组分页 | `PRODUCTION_GROUP_PAGE` |
| `POST /production/group/options` | 生产组下拉 | `PRODUCTION_GROUP_PAGE` |
| `POST /production/group/detail` | 生产组详情 | `PRODUCTION_GROUP_DETAIL` |
| `POST /production/group/saveOrUpdate` | 保存生产组 | `PRODUCTION_GROUP_SAVE_OR_UPDATE` |
| `POST /production/group/delete` | 删除生产组 | `PRODUCTION_GROUP_DELETE` |
| `POST /production/order/page` | 生产总单分页 | `PRODUCTION_ORDER_PAGE` |
| `POST /production/order/saveOrUpdate` | 保存手工生产总单 | `PRODUCTION_ORDER_SAVE_OR_UPDATE` |
| `POST /production/order/product/page` | 手工生产单选品分页 | `PRODUCTION_ORDER_SAVE_OR_UPDATE` |
| `POST /production/order/productSnapshot` | 手工生产单产品快照 | `PRODUCTION_ORDER_SAVE_OR_UPDATE` |
| `POST /production/order/detail` | 生产总单详情 | `PRODUCTION_ORDER_DETAIL` |
| `POST /production/order/detailByOrder` | 按来源订单查总单 | `PRODUCTION_ORDER_DETAIL` |
| `POST /production/order/batch` | 分批安排 | `PRODUCTION_ORDER_PROGRESS` |
| `POST /production/order/progress/delivery` | 产品行交货 | `PRODUCTION_ORDER_DELIVERY` |
| `POST /production/order/export` | 导出生产单 | `PRODUCTION_ORDER_EXPORT` |
