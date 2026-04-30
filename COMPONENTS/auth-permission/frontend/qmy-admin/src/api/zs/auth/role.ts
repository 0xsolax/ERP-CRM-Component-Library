import axios from '@/utils/axios'

// 查询角色列表
export const queryRoleList = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sysRole/list',
    method: 'post',
    data
  })
}

// 角色新增或修改
export const roleSaveOrUpdate = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sysRole/saveOrUpdate',
    method: 'post',
    data
  })
}

// 角色删除
export const roleDelete = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: `/sysRole/delete?id=${data.id}`,
    method: 'post'
  })
}

// 获取所有权限、当前权限、指定角色权限
export const getRolePermissions = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sysRole/permissions',
    method: 'get',
    params: data
  })
}

// 获取所有启用的角色
export const roleListEnabled = data => {
  return axios.request<IResponseModel<any>>({
    url: '/sysRole/listEnabled',
    method: 'get',
    params: data
  })
}

// 角色详情
export const roleDetail = data => {
  return axios.request<IResponseModel<any>>({
    url: '/sysRole/detail',
    method: 'get',
    params: data
  })
}

// 角色状态修改
export const roleUpdateStatus = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sysRole/updateStatus',
    method: 'post',
    data
  })
}
