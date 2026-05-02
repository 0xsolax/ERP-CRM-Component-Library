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
