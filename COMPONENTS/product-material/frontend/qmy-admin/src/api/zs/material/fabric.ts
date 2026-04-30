import axios from '@/utils/axios'

// 面料分页列表
export const getFabricPage = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/fabric/page',
    method: 'post',
    data
  })
}

// 新增面料
export const saveFabric = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/fabric/saveOrUpdate',
    method: 'post',
    data
  })
}

// 更新面料
export const updateFabric = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/fabric/saveOrUpdate',
    method: 'post',
    data
  })
}

export const getFabricDetail = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/fabric/deteil',
    method: 'post',
    data
  })
}

export const deleteFabric = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/fabric/delete',
    method: 'post',
    data
  })
}
