import axios from '@/utils/axios'

// 查询账号列表
export const queryAccountList = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sysUser/list',
    method: 'post',
    data
  })
}

// 账号新增或修改
export const accountSaveOrUpdate = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sysUser/saveOrUpdate',
    method: 'post',
    data
  })
}

// 账号详情
export const accountDetail = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sysUser/detail',
    method: 'get',
    params: data
  })
}

// 账号状态修改
export const accountUpdateStatus = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sysUser/updateStatus',
    method: 'post',
    data
  })
}
