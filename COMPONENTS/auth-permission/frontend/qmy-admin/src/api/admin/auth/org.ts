import axios from '@/utils/axios'

// 同步飞书组织架构
export const syncFeiShu = () => {
  return axios.request<IResponseModel<any>>({
    url: 'sysOrganization/syncFeiShu',
    method: 'post'
  })
}

// 获取部门列表
export const getDepartmentList = () => {
  return axios.request<IResponseModel<any>>({
    url: '/sysOrganization/getDepartmentList',
    method: 'get'
  })
}

// 获取部门下员工
export const getEmployeeList = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sysOrganization/getEmployeeList',
    method: 'post',
    data
  })
}

// 修改状态
export const updateStatus = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sysUser/updateStatus',
    method: 'post',
    data
  })
}

// 新增或编辑部门
export const saveOrUpdateDepartment = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sysOrganization/saveOrUpdateDepartment',
    method: 'post',
    data
  })
}

// 获取部门详情
export const getDepartmentDetail = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sysOrganization/getDepartmentDetail',
    method: 'get',
    params: data
  })
}

// 获取所有员工（下拉框）
export const getAllEmployee = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sysOrganization/getAllEmployee',
    method: 'get',
    params: data
  })
}

// 组织账号新增或修改
export const orgAccountSaveOrUpdate = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sysOrganization/saveOrUpdate',
    method: 'post',
    data
  })
}

// 账号详情
export const orgAccountDetail = data => {
  return axios.request<IResponseModel<any>>({
    url: '/sysOrganization/getAccountDetail',
    method: 'get',
    params: data
  })
}

// 账号编辑
export const orgUpdateAccount = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sysOrganization/updateAccount',
    method: 'post',
    data
  })
}

// 获取所有权限、当前权限、指定角色权限
export const getOrgAccountPermissions = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sysRole/permissions',
    method: 'get',
    params: data
  })
}
