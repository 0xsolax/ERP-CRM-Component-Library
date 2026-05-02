// 采购单状态列表
export const purchaseStatusList = [
  { label: '采购中', value: 1 },
  { label: '已入库', value: 2 },
  { label: '已发货', value: 3 },
  { label: '暂存', value: 0 }
]

// 付款方式列表
export const paymentMethodList = [
  { label: '群内支付', value: '0' },
  { label: '付款码', value: '1' },
  { label: '1688', value: '2' }
]

// 半成品状态列表
export const semiFinishedStatusList = [
  { label: '待确认', value: 0 },
  { label: '已确认', value: 1 }
]

// 获取采购单状态标签
export const getPurchaseStatusLabel = (status: number | string) => {
  const statusItem = purchaseStatusList.find(item => item.value == status)
  return statusItem ? statusItem.label : ''
}

// 获取半成品状态标签
export const getSemiFinishedStatusLabel = (status: number | string) => {
  const statusItem = semiFinishedStatusList.find(item => item.value == status)
  return statusItem ? statusItem.label : ''
}

// 获取采购单状态颜色
export const getPurchaseStatusColor = (status: number | string) => {
  const colorMap: Record<string, string> = {
    '0': 'warning',
    '1': 'success',
    '2': 'primary',
    '3': 'info'
  }
  return colorMap[String(status)] || 'info'
}

// 获取付款方式标签
export const getPaymentMethodLabel = (value: number | string) => {
  const valueItem = paymentMethodList.find(item => item.value == value)
  return valueItem ? valueItem.label : ''
}
