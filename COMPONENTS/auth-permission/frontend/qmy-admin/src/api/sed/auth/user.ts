import axios from '@/utils/axios'

export const login = (data: any) => {
  return axios.request<IResponseModel<any>>({
    // baseURL: import.meta.env.VITE_APP_MOCK_API,
    url: '/sysUser/loginByPassword',
    method: 'post',
    data,
    token: false
  })
}

export const loginByFeiShuApi = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sysUser/loginByFeiShu',
    method: 'post',
    data,
    token: false
  })
}

export const loginByScanApi = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sysUser/loginByScan',
    method: 'post',
    data,
    token: false
  })
}

export const logout = () => {
  return axios.request<IResponseModel<any>>({
    url: '/sysUser/logout',
    method: 'post'
  })
}

export const userInfo = () => {
  return axios.request<IResponseModel<any>>({
    url: '/sysUser/detail',
    method: 'get'
  })
}

// 获取租户信息
export const getTenantDetail = data => {
  return axios.request<IResponseModel<any>>({
    url: '/qiaoMoYun/tenant/getTenantId',
    method: 'get',
    params: data
  })
}

// 账号保存或更新
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

// 查询账号列表
export const getAccountList = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sysUser/list',
    method: 'post',
    data
  })
}

// 查询账号列表
export const fetchUserInfo = () => {
  return axios.request<IResponseModel<any>>({
    url: '/sysUser/info',
    method: 'get'
  })
}
