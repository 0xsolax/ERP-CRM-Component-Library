import axios from '@/utils/axios'

// 包材分页列表
export const getPackagingPage = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/packaging/page',
    method: 'post',
    data
  })
}

// 新增包材
export const savePackaging = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/packaging/saveOrUpdate',
    method: 'post',
    data
  })
}

// 更新包材
export const updatePackaging = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/packaging/saveOrUpdate',
    method: 'post',
    data
  })
}

// 删除包材
export const deletePackaging = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/packaging/delete',
    method: 'post',
    data
  })
}

// 批量保存纸箱单价
export const saveOrUpdateDefaultPaperBox = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/packaging/saveOrUpdateDefaultPaperBox',
    method: 'post',
    data
  })
}
