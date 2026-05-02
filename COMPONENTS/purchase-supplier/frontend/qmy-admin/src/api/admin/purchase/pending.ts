import axios from '@/utils/axios'

// 查询待采购列表
export const getApplyPurchaseList = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pur/yt/applyPurchase/list',
    method: 'post',
    data
  })
}

// 更换供应商
export const replaceSupplier = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pur/yt/applyPurchase/replaceSupplier',
    method: 'post',
    data
  })
}

// 获取可更换的供应商列表
export const listReplaceableSuppliers = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pur/yt/applyPurchase/listReplaceableSuppliers',
    method: 'post',
    data
  })
}

// 获取暂存采购单列表
export const listTemporary = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pur/yt/purchase/listTemporary',
    method: 'post',
    data
  })
}

// 追加至采购单
export const addPurchase = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pur/yt/applyPurchase/addPurchase',
    method: 'post',
    data
  })
}

// 保存或更新采购单
export const saveOrUpdatePurchase = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pur/yt/applyPurchase/saveOrUpdate',
    method: 'post',
    data
  })
}

// 获取采购单详情
export const getSaveDetail = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pur/yt/applyPurchase/saveDetail',
    method: 'post',
    data
  })
}

// 退回申购记录
export const withdrawApplyPurchase = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pur/yt/applyPurchase/withdraw',
    method: 'post',
    data
  })
}
