// 出入库类型列表
export const inboundTypeList = [
  { label: '单独出库', value: '6' },
  { label: '单独入库', value: '4' },
  { label: '独立库转入', value: '5' },
  { label: '独立库转出', value: '7' }
]

// 出入库类型记录列表
export const enterOutRecordTypeList = [
  { label: '采购单入库', value: '3' },
  { label: '独立入库', value: '4' },
  { label: '客户独立仓独立入库', value: '5' },
  { label: '独立出库', value: '6' },
  { label: '客户独立仓独立出库', value: '7' }
]

// 获取出入库类型标签
export const getInboundTypeLabel = (type: number | string) => {
  const typeItem = inboundTypeList.find(item => item.value == type)
  return typeItem ? typeItem.label : ''
}

// 出入库类型记录标签
export const getEnterOutRecordTypeLabel = (type: number | string) => {
  const typeItem = enterOutRecordTypeList.find(item => item.value == type)
  return typeItem ? typeItem.label : ''
}

// 物流公司类型列表
export const logisticsTypeList = [
  { label: '国际物流', value: '1' },
  { label: '国内物流', value: '2' }
]

// 是否上门取件列表
export const homeServiceList = [
  { label: '是', value: 1 },
  { label: '否', value: 0 }
]

// 获取物流公司类型标签
export const getLogisticsTypeLabel = (type: number | string) => {
  const typeItem = logisticsTypeList.find(item => item.value == type)
  return typeItem ? typeItem.label : ''
}

// 获取是否上门取件标签
export const getHomeServiceLabel = (value: number | string) => {
  const item = homeServiceList.find(item => item.value == value)
  return item ? item.label : ''
}

// 业务类型列表
export const businessTypeList = [
  { label: '创建订单', value: '1' },
  { label: '创建采购单', value: '2' },
  { label: '采购单入库', value: '3' },
  { label: '独立入库', value: '4' },
  { label: '客户独立仓独立入库', value: '5' },
  { label: '独立出库', value: '6' },
  { label: '客户独立仓独立出库', value: '7' },
  { label: '发货', value: '8' },
  { label: '订单占用可用库存', value: '9' },
  { label: '订单退货', value: '10' },
  { label: '采购单退货', value: '11' },
  { label: '半成品确认', value: '12' },
  { label: '关闭订单释放占用', value: '13' }
]

// 获取业务类型标签
export const getBusinessTypeLabel = (type: number | string) => {
  const typeItem = businessTypeList.find(item => item.value == type)
  return typeItem ? typeItem.label : ''
}

// 入库单状态列表
export const inboundStatusList = [
  { label: '入库中', value: 0 },
  { label: '已完成', value: 1 }
]

// 获取入库单状态标签
export const getInboundStatusLabel = (status: number | string) => {
  const statusItem = inboundStatusList.find(item => item.value == status)
  return statusItem ? statusItem.label : ''
}

// 产品数量状态列表
export const productQuantityStatusList = [
  { label: '产品齐全', value: 1 },
  { label: '产品不全', value: 0 }
]

// 获取产品数量状态标签
export const getProductQuantityStatusLabel = (status: number | string) => {
  const statusItem = productQuantityStatusList.find(item => item.value == status)
  return statusItem ? statusItem.label : ''
}

// 打包状态列表
export const packingStatusList = [
  { label: '打包齐全', value: 1 },
  { label: '打包不全', value: 0 }
]

// 获取打包状态标签
export const getPackingStatusLabel = (status: number | string) => {
  const statusItem = packingStatusList.find(item => item.value == status)
  return statusItem ? statusItem.label : ''
}
