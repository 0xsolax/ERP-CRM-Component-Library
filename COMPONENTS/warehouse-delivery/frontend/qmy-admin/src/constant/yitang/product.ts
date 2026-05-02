// 产品状态列表
export const statusList = [
  { label: '启用', value: '1' },
  { label: '禁用', value: '0' },
  { label: '不可外售', value: '2' }
]

// 获取产品状态标签
export const getStatusLabel = (status: number | string) => {
  const statusItem = statusList.find(item => item.value == status)
  return statusItem ? statusItem.label : ''
}
