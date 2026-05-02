// 报价单状态列表
export const quotationStatusList = [
  { label: '暂存', value: '0' },
  { label: '计算成本中', value: '1' },
  { label: '计算成本完毕', value: '2' },
  { label: '审核中', value: '3' },
  { label: '审核通过', value: '4' },
  { label: '总裁未审核，财务未审核', value: '5' },
  { label: '总裁审核通过，财务未审核', value: '6' },
  { label: '总裁未审核，财务审核通过', value: '7' },
  { label: '总裁微信审核通过，财务未审核', value: '8' },
  { label: '审核驳回', value: '-1' }
]

// 获取报价单状态标签
export const getQuotationStatusLabel = (status: string | number) => {
  const statusItem = quotationStatusList.find(item => item.value == status)
  return statusItem ? statusItem.label : ''
}

// 报价单操作类型列表
export const quotationOperationList = [
  { label: '通过审核', value: '1' },
  { label: '提交审核', value: '2' },
  { label: '确认运输成本', value: '3' },
  { label: '确认采购成本', value: '4' },
  { label: '修改报价单，重新进入成本核算环节', value: '5' },
  { label: '确定提交，进入成本核算环节', value: '6' },
  { label: '修改报价单，暂存报价单', value: '7' },
  { label: '审核驳回', value: '8' },
  { label: '财务审核通过', value: '9' },
  { label: '总裁审核通过', value: '10' },
  { label: '总裁微信审核通过', value: '11' }
]

// 获取报价单操作类型标签
export const getQuotationOperationLabel = (operation: string | number) => {
  const operationItem = quotationOperationList.find(item => item.value == operation)
  return operationItem ? operationItem.label : ''
}
