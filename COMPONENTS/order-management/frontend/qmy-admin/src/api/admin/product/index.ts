import axios from '@/utils/axios'

// 订单半成品确认：获取产品详情
export const getProductDetail = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pro/yt/product/detail',
    method: 'get',
    params
  })
}

// 订单表单：获取产品分类标签
export const getCategoryLabelList = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pro/yt/categoryLabel/list',
    method: 'get',
    params
  })
}
