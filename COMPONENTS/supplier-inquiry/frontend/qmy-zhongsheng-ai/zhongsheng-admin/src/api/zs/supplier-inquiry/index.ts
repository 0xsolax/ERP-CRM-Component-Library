import axios from '@/utils/axios'

// 供应商询价台账分页列表
export const getSupplierInquiryPage = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/supplier/inquiry/page',
    method: 'post',
    data
  })
}

// 供应商询价历史
export const getSupplierInquiryHistory = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/supplier/inquiry/history',
    method: 'post',
    data
  })
}

// 供应商询价详情
export const getSupplierInquiryDetail = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/supplier/inquiry/detail',
    method: 'post',
    data
  })
}

// 保存或更新供应商询价记录
export const saveOrUpdateSupplierInquiry = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/supplier/inquiry/saveOrUpdate',
    method: 'post',
    data
  })
}

// 删除供应商询价记录
export const deleteSupplierInquiry = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/supplier/inquiry/delete',
    method: 'post',
    data
  })
}
