import axios from '@/utils/axios'

// 采购单编辑只依赖客户列表筛选；完整客户 CRUD 归 customer-management。
export const getCustomerList = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/customer/list',
    method: 'post',
    data
  })
}
