import axios from '@/utils/axios'

// 获取报价单列表
export const getSedQuotationList = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/sed/quotation/list',
    method: 'post',
    data
  })
}

// 获取报价单详情
export const getQuotationDetail = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/sed/quotation/quotationDetail',
    method: 'get',
    params
  })
}

// 获取报价单采购成本详情
export const getProcurementDetail = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/sed/quotation/procurementDetail',
    method: 'get',
    params
  })
}

// 获取报价单物流成本详情
export const getLogisticsDetail = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/sed/quotation/logisticsDetail',
    method: 'get',
    params
  })
}

// 新增/编辑/再次创建报价单
export const saveOrUpdateQuotation = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/sed/quotation/saveOrUpdate',
    method: 'post',
    data
  })
}

// 提交审核
export const submitQuotationAudit = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/sed/quotation/submitAudit',
    method: 'post',
    data
  })
}

// 采购成本确认
export const procurementConfirm = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/sed/quotation/procurementConfirm',
    method: 'post',
    data
  })
}

// 物流成本确认
export const logisticsConfirm = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/sed/quotation/logisticsConfirm',
    method: 'post',
    data
  })
}

// 报价单审核
export const quotationAudit = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/sed/quotation/audit',
    method: 'post',
    data
  })
}

// 报价单会签审核
export const quotationJointAudit = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/sed/quotation/jointAudit',
    method: 'post',
    data
  })
}

// 获取历史报价单列表
export const getHistoryQuotation = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/sed/quotation/getHistoryQuotation',
    method: 'post',
    data
  })
}

// 获取历史报价单详情
export const getHistoryQuotationDetail = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/sed/quotation/getHistoryQuotationDetail',
    method: 'get',
    params
  })
}

// 获取客户收货地址
export const getCustomerAddress = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/sed/quotation/getCustomerAddress',
    method: 'get',
    params
  })
}

// 获取成本明细
export const getCostDetail = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/sed/quotation/getCostDetail',
    method: 'post',
    data
  })
}

// 获取历史报价信息
export const getHistoryQuotationInfo = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/sed/quotation/getHistoryQuotationInfo',
    method: 'post',
    data
  })
}

// 报价单转订单
export const convertToOrder = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/sed/quotation/oneKeyToOrder',
    method: 'post',
    data
  })
}

// SKU转订单
export const skuToOrder = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/sed/quotation/skuToOrder',
    method: 'post',
    data
  })
}

// 获取合并转订单列表
export const getMergeList = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/sed/quotation/mergeList',
    method: 'post',
    data
  })
}

// 获取合并转订单-SKU列表
export const getMergeSkuList = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/sed/quotation/mergeSkuList',
    method: 'post',
    data
  })
}

// 总裁微信审核
export const presidentWxAudit = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/sed/quotation/presidentWxAudit',
    method: 'post',
    data
  })
}

// 合并转订单
export const mergeToOrder = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/sed/quotation/mergeToOrder',
    method: 'post',
    data
  })
}
