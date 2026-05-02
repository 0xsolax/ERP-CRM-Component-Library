import axios from '@/utils/axios'

// 包材列表
export const getSedPackingList = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pro/sed/packing/list',
    method: 'post',
    data
  })
}

// 包材类型列表
export const getSedPackingTypeList = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pro/sed/packing/type/list',
    method: 'post',
    data
  })
}

// 新增包材名称
export const addPackingName = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pro/sed/packing/addName',
    method: 'post',
    params
  })
}
