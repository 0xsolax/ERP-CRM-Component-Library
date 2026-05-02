import axios from '@/utils/axios'

// 待采购列表只依赖员工下拉；完整组织账号维护归 auth-permission/P0 基座。
export const getAllEmployee = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sysOrganization/getAllEmployee',
    method: 'get',
    params: data
  })
}
