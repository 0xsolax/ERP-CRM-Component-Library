# API_CONTRACT｜warehouse-delivery

## qmy-admin 主 API

| 前端函数 | 方法 | 路径 | 能力 |
| :--- | :--- | :--- | :--- |
| `getStoreProduct` | GET | `/sto/yt/store/product` | 库存产品列表 |
| `getStoreHistory` | POST | `/sto/yt/store/history` | 库存历史流向 |
| `getInboundOrderList` | POST | `/sto/yt/store/order/list` | 入库单列表 |
| `getEnterOutRecords` | POST | `/sto/yt/store/enterOutRecords` | 出入库记录 |
| `enterStore` | POST | `/sto/yt/store/order/enter` | 普通入库 |
| `enterStoreWithAllocation` | POST | `/sto/yt/store/order/enterWithAllocation` | 按订单分配入库 |
| `batchEnterStore` | POST | `/sto/yt/store/order/batchEnter` | 批量入库 |
| `addStoreOrder` | POST | `/sto/yt/store/order/addStore` | 新增独立出入库单 |
| `getPackingBoxList` | GET | `/sto/yt/box/list` | 箱规列表 |
| `saveOrUpdatePackingBox` | POST | `/sto/yt/box/saveOrUpdate` | 新增/编辑箱规 |
| `getPackingBoxDetail` | GET | `/sto/yt/box/detail/{id}` | 箱规详情 |
| `deletePackingBox` | GET | `/sto/yt/box/delete` | 删除箱规，接入前需改 DELETE |
| `getLogisticsList` | GET | `/sto/yt/transportCompany/page` | 物流公司分页 |
| `getTransportCompanyList` | GET | `/sto/yt/transportCompany/list` | 物流公司下拉 |
| `addLogistics` | POST | `/sto/yt/transportCompany/add` | 新增物流公司 |
| `updateLogistics` | POST | `/sto/yt/transportCompany/update` | 更新物流公司 |
| `getLogisticsDetail` | GET | `/sto/yt/transportCompany/get/{id}` | 物流公司详情 |
| `deleteLogistics` | GET | `/sto/yt/transportCompany/delete/{id}` | 删除物流公司，接入前需改 DELETE |
| `getWarningRule` | GET | `/sto/yt/store/warning/rule` | 获取库存预警规则 |
| `saveWarningRule` | POST | `/sto/yt/store/warning/rule` | 保存库存预警规则 |
| `getSpecWarningRule` | GET | `/sto/yt/store/warning/specification` | 获取规格预警 |
| `saveSpecWarningRule` | POST | `/sto/yt/store/warning/specification` | 保存规格预警 |
| `getStoreOccupyDetail` | POST | `/sto/yt/store/storeOccupyDetail` | 库存占用详情 |
| `getTransitOccupyDetail` | POST | `/sto/yt/store/transitOccupyDetail` | 在途占用详情 |
| `getStoreOrderProgressList` | POST | `/sto/yt/store/order/progressList` | 入库进度 |
| `getDeliveryList` | POST | `/sto/yt/delivery/list` | 发货列表 |
| `getDeliveryDetail` | POST | `/sto/yt/delivery/detail` | 发货详情 |
| `deliveryPackage` | POST | `/sto/yt/delivery/package` | 发货单打包 |
| `getDeliveryDetailOrderSub` | POST | `/sto/yt/delivery/detailOrderSub` | 发货单子订单列表 |
| `getDeliveryDetailOrder` | POST | `/sto/yt/delivery/detailOrder` | 发货单订单列表 |
| `getPackageList` | POST | `/sto/yt/delivery/packageList` | 包裹列表 |
| `getPackageItemList` | POST | `/sto/yt/delivery/packageItemList` | 包裹内产品 |
| `deliveryScan` | POST | `/sto/yt/delivery/scan` | 扫码获取产品信息 |
| `getBoxList` | GET | `/sto/yt/box/listForSelect` | 箱规下拉 |
| `confirmDelivery` | POST | `/sto/yt/delivery/confirmDelivery` | 确认发货 |
| `updateTransport` | POST | `/sto/yt/delivery/updateTransport` | 更新物流信息 |
| `savePackage` | POST | `/sto/yt/delivery/savePackage` | 暂存打包 |
| `validCompletePackage` | POST | `/sto/yt/delivery/validCompletePackage` | 完成打包前校验 |
| `sendPackageMessage` | POST | `/sto/yt/delivery/sendPackageMessage` | 发送打包提醒 |
| `completePackage` | POST | `/sto/yt/delivery/completePackage` | 完成打包 |
| `returnWaitPackage` | POST | `/sto/yt/delivery/returnWaitPackage` | 退回待打包 |
| `getDeliveryOrderDetail` | POST | `/sto/yt/delivery/orderDetail` | 发货单订单详情 |

## legacy delivery API

`api/admin/delivery/index.ts` 对应 `views/admin/delivery`，但 `delivery.ts` 路由已整体注释，默认不启用。

| 前端函数 | 方法 | 路径 | 能力 |
| :--- | :--- | :--- | :--- |
| `deliveryRecordList` | GET | `/delivery/deliveryRecordList` | 交货记录 |
| `deliveryList` | GET | `/delivery/list` | 交货列表 |
| `operateRecordList` | GET | `/delivery/operateRecordList` | 操作记录 |
| `addDeliveryNum` | POST | `/delivery/addDeliveryNum` | 增加交货数量 |
| `bindProcessNo` | POST | `/delivery/bindProcessNo` | 绑定工艺单 |
| `deleteDeliveryRecord` | POST | `/delivery/deleteDeliveryRecord` | 删除交货记录 |

## 接入前 API 收口

- 删除接口改为 `DELETE` 或带语义的 `POST`，禁止破坏性 GET。
- 所有写接口补齐方法级权限和数据范围权限。
- 查询接口按本人/部门/全公司/老板视角补数据范围。
- 发货、入库、库存扣减接口补幂等键或状态前置校验。
- 导出接口补权限和查询范围限制。
