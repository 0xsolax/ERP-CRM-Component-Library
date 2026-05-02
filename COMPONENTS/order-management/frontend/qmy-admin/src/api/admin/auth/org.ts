import axios from '@/utils/axios'

// 订单列表/表单：获取所有员工下拉
export const getAllEmployee = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sysOrganization/getAllEmployee',
    method: 'get',
    params: data
  })
}
