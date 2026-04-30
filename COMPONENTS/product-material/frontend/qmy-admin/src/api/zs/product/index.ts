import axios from '@/utils/axios'

// 分页查询产品列表
export const getProductPage = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/product/page',
    method: 'post',
    data
  })
}

// 保存或更新产品
export const saveOrUpdateProduct = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/product/saveOrUpdate',
    method: 'post',
    data
  })
}

// 查询产品详情
export const getProductDetail = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/product/detail',
    method: 'post',
    data
  })
}

// 删除产品
export const deleteProduct = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/product/delete',
    method: 'post',
    data
  })
}
