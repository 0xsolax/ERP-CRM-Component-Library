// 平台列表
export const platformList = [
  { label: '国际站', value: '0' },
  { label: '1688', value: '1' },
  { label: '独立站', value: '2' },
  { label: '展会', value: '3' },
  { label: '其他', value: '4' }
]

// 获取平台标签
export const getPlatformLabel = (platform: number | string) => {
  const platformItem = platformList.find(item => item.value == platform)
  return platformItem ? platformItem.label : ''
}

// 订单状态列表
export const orderStatusList = [
  { label: '暂存', value: '-1' },
  { label: '待采购', value: '0' },
  { label: '待入库', value: '1' },
  { label: '待打包', value: '2' },
  { label: '待发货', value: '3' },
  { label: '已发货', value: '4' },
  { label: '已完成', value: '5' },
  { label: '待确认', value: '6' },
  { label: '已关闭', value: '7' }
]

// 获取订单状态标签
export const getOrderStatusLabel = (status: number | string) => {
  const statusItem = orderStatusList.find(item => item.value == status)
  return statusItem ? statusItem.label : ''
}

// 订单类型列表
export const orderTypeList = [
  { label: '成品单', value: '0' },
  { label: '半成品单', value: '1' }
]

// 获取订单类型标签
export const getOrderTypeLabel = (type: number | string) => {
  const typeItem = orderTypeList.find(item => item.value == type)
  return typeItem ? typeItem.label : ''
}

// 币种列表
export const currencyList = [
  { label: '人民币', value: '0', symbol: '¥' },
  { label: '美元', value: '1', symbol: '$' }
]

// 获取币种标签
export const getCurrencyLabel = (currency: number | string) => {
  const currencyItem = currencyList.find(item => item.value == currency)
  return currencyItem ? currencyItem.label : ''
}

// 获取币种符号
export const getCurrencySymbol = (currency: number | string) => {
  const currencyItem = currencyList.find(item => item.value == currency)
  return currencyItem ? currencyItem.symbol : ''
}

// 发货形式列表
export const shippingMethodList = [
  { label: '整单齐发', value: '0' },
  { label: '单款齐发', value: '1' },
  { label: '单规格齐发', value: '2' },
  { label: '有货就发', value: '3' }
]

// 获取发货形式标签
export const getDeliveryTypeLabel = (shippingMethod: number | string) => {
  const deliveryTypeItem = shippingMethodList.find(item => item.value == shippingMethod)
  return deliveryTypeItem ? deliveryTypeItem.label : ''
}

// 是否入库发货列表
export const isWarehouseDeliveryList = [
  { label: '入库发货', value: '1' },
  { label: '供应商发货', value: '0' }
]

// 获取是否入库发货标签
export const getIsWarehouseDeliveryLabel = (isWarehouseDelivery: number | string) => {
  const warehouseDeliveryItem = isWarehouseDeliveryList.find(item => item.value == isWarehouseDelivery)
  return warehouseDeliveryItem ? warehouseDeliveryItem.label : ''
}

// 运费状态列表
export const collectedShippingCostList = [
  { label: '已收运费', value: 1 },
  { label: '未收运费', value: 0 }
]

// 获取运费状态标签
export const getShippingMethodLabel = (shippingMethod: number | string) => {
  const shippingMethodItem = collectedShippingCostList.find(item => item.value == shippingMethod)
  return shippingMethodItem ? shippingMethodItem.label : ''
}

// 采购单操作类型列表
export const purchaseOperationTypeList = [
  { label: '下单', value: 1 },
  { label: '新增采购单', value: 2 },
  { label: '订单退货', value: 3 },
  { label: '入库', value: 4 },
  { label: '发货', value: 5 },
  { label: '半成品确认规格', value: 6 },
  { label: '采购单退货', value: 7 },
  { label: '关闭订单', value: 8 }
]

// 获取采购单操作类型标签
export const getPurchaseOperationTypeLabel = (type: number | string) => {
  const operationTypeItem = purchaseOperationTypeList.find(item => item.value == type)
  return operationTypeItem ? operationTypeItem.label : ''
}

// 订单操作类型列表
export const operationTypeList = [
  { label: '下单', value: 1 },
  { label: '新增采购单', value: 2 },
  { label: '订单退货', value: 3 },
  { label: '入库', value: 4 },
  { label: '发货', value: 5 },
  { label: '半成品确认规格', value: 6 },
  { label: '采购单退货', value: 7 },
  { label: '关闭订单', value: 8 }
]

// 获取操作类型标签
export const getOperationTypeLabel = (type: number | string) => {
  const operationTypeItem = operationTypeList.find(item => item.value == type)
  return operationTypeItem ? operationTypeItem.label : ''
}
