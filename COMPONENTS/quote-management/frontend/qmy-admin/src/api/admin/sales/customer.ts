import axios from '@/utils/axios'

// 获取客户下拉列表。报价组件只依赖客户选择，完整客户 CRUD 属于 customer-management。
export const getCustomerSelectList = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/customer/selectList',
    method: 'post',
    data
  })
}
