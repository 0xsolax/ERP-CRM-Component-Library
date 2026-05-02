import axios from '@/utils/axios'

// 订单表单：获取客户收货地址列表
export const getCustomerAddressList = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/customer/addressList',
    method: 'get',
    params
  })
}

// 订单表单：获取客户下拉列表
export const getCustomerSelectList = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/customer/selectList',
    method: 'post',
    data
  })
}
