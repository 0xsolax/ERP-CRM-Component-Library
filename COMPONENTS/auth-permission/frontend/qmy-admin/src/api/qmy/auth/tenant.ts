import axios from '@/utils/axios'

// 查询租户列表
export const queryTenantList = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/qiaoMoYun/tenant/list',
    method: 'post',
    data
  })
}

// 租户新增或修改
export const tenantSaveOrUpdate = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/qiaoMoYun/tenant/saveOrUpdate',
    method: 'post',
    data
  })
}

// 租户详情
export const tenantDetail = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/qiaoMoYun/tenant/detail',
    method: 'get',
    params: data
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

// 租户状态修改
export const tenantUpdateStatus = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/qiaoMoYun/tenant/updateStatus',
    method: 'post',
    data
  })
}

// 获取飞书所有员工（选择框）
export const getFeiShuOrganize = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/qiaoMoYun/tenant/getFeiShuOrganize',
    method: 'post',
    data
  })
}

// 获取钉钉所有员工（选择框）
export const getDingTalkOrganize = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/qiaoMoYun/tenant/getDingTalkOrganize',
    method: 'post',
    data
  })
}
