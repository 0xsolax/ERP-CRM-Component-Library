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
