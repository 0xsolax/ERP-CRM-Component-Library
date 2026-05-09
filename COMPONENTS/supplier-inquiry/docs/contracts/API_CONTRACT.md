# API Contract

| 接口 | 说明 | 权限 |
| :--- | :--- | :--- |
| `POST /supplier/inquiry/page` | 分页查询询价台账 | `SUPPLIER_INQUIRY_PAGE` |
| `POST /supplier/inquiry/history` | 查询供应商或对象历史 | `SUPPLIER_INQUIRY_PAGE` |
| `POST /supplier/inquiry/detail` | 查询询价详情 | `SUPPLIER_INQUIRY_DETAIL` |
| `POST /supplier/inquiry/saveOrUpdate` | 新增或编辑询价 | `SUPPLIER_INQUIRY_SAVE_OR_UPDATE` |
| `POST /supplier/inquiry/delete` | 逻辑删除询价 | `SUPPLIER_INQUIRY_DELETE` |

前端 API 封装位于 `src/api/zs/supplier-inquiry/index.ts`。
