import axios from '@/utils/axios'

// 客户规格对照列表
export const getCustomerSpecList = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/customerSpecificationComparison/list',
    method: 'post',
    data
  })
}

// 新增或编辑客户规格对照
export const createOrUpdateCustomerSpec = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/customerSpecificationComparison/createOrUpdate',
    method: 'post',
    data
  })
}

// 编辑客户货号
export const createOrUpdateItemNumber = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/customerSpecificationComparison/createOrUpdateItemNumber',
    method: 'post',
    data
  })
}

// 客户规格对照详情
export const getCustomerSpecDetail = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/customerSpecificationComparison/detail',
    method: 'get',
    params
  })
}

// 删除客户规格对照
export const deleteCustomerSpec = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/customerSpecificationComparison/delete',
    method: 'post',
    params
  })
}

// 导入客户规格对照
export const importCustomerSpec = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/customerSpecificationComparison/import',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 导出客户规格对照
export const exportCustomerSpec = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/customerSpecificationComparison/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

// 获取产品规格列表
export const getCustomerSpecification = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/customerSpecificationComparison/getCustomerSpecification',
    method: 'post',
    data
  })
}
