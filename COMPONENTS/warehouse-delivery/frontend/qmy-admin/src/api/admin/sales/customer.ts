import axios from '@/utils/axios'

// 仓储入库新增：客户列表
export const getCustomerList = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/customer/list',
    method: 'post',
    data
  })
}
