import axios from '@/utils/axios'

// 仓储库存筛选：产品分类列表
export const getCategoryList = (data?: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pro/yt/productCategory/list',
    method: 'post',
    data
  })
}

// 仓储库存筛选：产品列表
export const getProductList = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pro/yt/product/list',
    method: 'post',
    data
  })
}
