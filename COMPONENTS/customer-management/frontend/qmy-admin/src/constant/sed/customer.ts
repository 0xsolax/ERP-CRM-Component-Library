// 客户类型列表
export const customerTypeList = [
  { label: '零售商', value: '0' },
  { label: '批发商', value: '1' },
  { label: '个人买家', value: '2' },
  { label: '外贸公司', value: '3' },
  { label: '未知', value: '4' }
]

// 获取客户类型标签
export const getCustomerTypeLabel = (type: number | string) => {
  const typeItem = customerTypeList.find(item => item.value == type)
  return typeItem ? typeItem.label : ''
}

// 手动层级列表
export const manualLevelList = [
  { label: 'A', value: 'A' },
  { label: 'B', value: 'B' },
  { label: 'C', value: 'C' },
  { label: 'D', value: 'D' }
]

// 获取手动层级标签
export const getManualLevelLabel = (level: string) => {
  const levelItem = manualLevelList.find(item => item.value == level)
  return levelItem ? levelItem.label : ''
}
