// 订单来源列表
export const orderSourceList = [
  { label: '外贸', value: '1' },
  { label: '义乌', value: '2' },
  { label: '1688', value: '3' },
  { label: '天猫', value: '4' },
  { label: '国际站', value: '5' }
]

// 获取订单来源标签
export const getOrderSourceLabel = (value: string) => {
  const source = orderSourceList.find(item => item.value === value)
  return source ? source.label : ''
}

// 管家婆同步状态列表
export const syncStatusList = [
  { label: '未同步', value: '1' },
  { label: '同步失败', value: '2' },
  { label: '同步成功', value: '3' }
]

// 获取同步状态标签
export const getSyncStatusLabel = (value: string) => {
  const status = syncStatusList.find(item => item.value === value)
  return status ? status.label : ''
}

// 币种列表
export const currencyList = [
  { label: '人民币', value: '1', symbol: '¥' },
  { label: '美元', value: '2', symbol: '$' }
]

// 获取币种标签
export const getCurrencyLabel = (currency: number | string) => {
  const currencyItem = currencyList.find(item => item.value == currency)
  return currencyItem ? currencyItem.label : ''
}

// 获取币种标签单位
export const getCurrencySymbol = (currency: number | string) => {
  const currencyItem = currencyList.find(item => item.value == currency)
  return currencyItem ? currencyItem.symbol : ''
}

// 是否含税列表
export const taxList = [
  { label: '含税', value: '1' },
  { label: '不含税', value: '2' }
]

// 获取税标签
export const getTaxLabel = (tax: number | string) => {
  const taxItem = taxList.find(item => item.value == tax)
  return taxItem ? taxItem.label : ''
}

// FOB列表
export const fobList = [
  { label: 'NINGBO', value: 'NINGBO' },
  { label: 'SHANGHAI', value: 'SHANGHAI' }
]

// 获取FOB标签
export const getFobLabel = (fob: string) => {
  const fobItem = fobList.find(item => item.value === fob)
  return fobItem ? fobItem.label : ''
}

// EXW列表
export const exwList = [
  { label: 'NINGBO', value: 'NINGBO' },
  { label: 'SHANGHAI', value: 'SHANGHAI' }
]

// 获取EXW标签
export const getExwLabel = (exw: string) => {
  const exwItem = exwList.find(item => item.value === exw)
  return exwItem ? exwItem.label : ''
}
