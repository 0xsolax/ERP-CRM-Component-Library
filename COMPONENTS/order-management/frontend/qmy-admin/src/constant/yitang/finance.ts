// 回款状态列表
export const receivableStatusList = [
  { label: '未回款', value: '0' },
  { label: '部分回款', value: '1' },
  { label: '已完成', value: '2' }
]

// 获取回款状态标签
export const getReceivableStatusLabel = (status: number | string) => {
  const statusItem = receivableStatusList.find(item => item.value == status)
  return statusItem ? statusItem.label : ''
}

// 利润状态列表
export const profitStatusList = [
  { label: '未回款', value: '0' },
  { label: '部分回款', value: '1' },
  { label: '已完成', value: '2' }
]

// 获取利润状态标签
export const getProfitStatusLabel = (status: number | string) => {
  const statusItem = profitStatusList.find(item => item.value == status)
  return statusItem ? statusItem.label : ''
}
