import axios from '@/utils/axios'

// 获取供应商列表
export const getSupplierList = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pur/yt/supplier/list',
    method: 'post',
    data
  })
}

// 新增供应商
export const addSupplier = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pur/yt/supplier/add',
    method: 'post',
    data
  })
}

// 更新供应商
export const updateSupplier = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pur/yt/supplier/update',
    method: 'post',
    data
  })
}

// 获取供应商详情
export const getSupplierDetail = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pur/yt/supplier/detail',
    method: 'get',
    params
  })
}

// 删除供应商标签
export const deleteSupplierLabel = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pur/yt/supplier/label/delete',
    method: 'post',
    params
  })
}

// 新增供应商标签
export const addSupplierLabel = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pur/yt/supplier/label/add',
    method: 'post',
    data
  })
}

// 获取供应商标签列表
export const getSupplierLabelList = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pur/yt/supplier/label/list',
    method: 'get',
    params
  })
}

// 新增/编辑供应商跟进记录
export const createOrUpdateSupplierFollow = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pur/yt/supplier/follow/createOrUpdate',
    method: 'post',
    data
  })
}

// 删除供应商跟进记录
export const deleteSupplierFollow = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pur/yt/supplier/follow/delete',
    method: 'get',
    params
  })
}

// 新增/编辑供应商联系人
export const saveOrUpdateSupplierContact = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pur/yt/supplier/contact/saveOrUpdate',
    method: 'post',
    data
  })
}

// 获取供应商联系人列表
export const getSupplierContactList = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pur/yt/supplier/contact/list',
    method: 'get',
    params
  })
}

// 获取联系人详情
export const getSupplierContactDetail = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pur/yt/supplier/contact/detail',
    method: 'get',
    params
  })
}

// 删除供应商联系人
export const deleteSupplierContact = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pur/yt/supplier/contact/delete',
    method: 'get',
    params
  })
}

// 获取供应商规格对照表
export const getSupplierSpecification = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pur/yt/supplier/supplierSpecification',
    method: 'post',
    data
  })
}

// 更新供应商规格对照
export const updateSupplierSpecification = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pur/yt/supplier/supplierSpecification/update',
    method: 'post',
    data
  })
}

// 获取采购趋势
export const getPurchaseTrends = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pur/yt/supplier/getPurchaseTrends',
    method: 'post',
    data
  })
}

// 获取采购占比
export const getPurchaseRatio = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pur/yt/supplier/getPurchaseRatio',
    method: 'post',
    data
  })
}
