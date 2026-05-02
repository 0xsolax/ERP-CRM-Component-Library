import axios from '@/utils/axios'

// 查询采购单列表
export const getPurchaseList = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pur/yt/purchase/list',
    method: 'post',
    data
  })
}

// 创建或更新采购单
export const createOrUpdatePurchase = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pur/yt/purchase/createOrUpdate',
    method: 'post',
    data
  })
}

// 获取采购单详情
export const getPurchaseDetail = (id: number | string) => {
  return axios.request<IResponseModel<any>>({
    url: `/pur/yt/purchase/detail`,
    method: 'get',
    params: { id }
  })
}

// 获取采购单产品列表
export const getPurchaseProductList = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pur/yt/purchase/productList',
    method: 'post',
    data
  })
}

// 获取采购单半成品列表
export const getPurchaseSemiFinishedList = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pur/yt/purchase/semiFinishedProductList',
    method: 'post',
    data
  })
}

// 获取采购单退货记录
export const getPurchaseReturnRecord = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pur/yt/purchase/returnRecord',
    method: 'post',
    data
  })
}

// 采购单退货
export const returnPurchase = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pur/yt/purchase/return',
    method: 'post',
    data
  })
}

// 获取采购单跟进列表
export const getPurchaseFollowList = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pur/yt/purchase/follow/list',
    method: 'post',
    data
  })
}

// 操作记录
export const getItemOperation = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pur/yt/purchase/itemOperation',
    method: 'get',
    params: data
  })
}

// 跟进采购单
export const followCreateOrUpdate = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pur/yt/purchase/follow/createOrUpdate',
    method: 'post',
    data
  })
}

// 退货统计
export const getReturnStats = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pur/yt/purchase/returnStats',
    method: 'post',
    data
  })
}

// 退货详情
export const getReturnDetail = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pur/yt/purchase/returnDetail',
    method: 'post',
    data
  })
}

// 通知供应商
export const notifyPurchase = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pur/yt/purchase/notify',
    method: 'post',
    data
  })
}

// 删除暂存采购单
export const deletePurchase = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pur/yt/purchase/delete',
    method: 'post',
    data
  })
}
