import axios from '@/utils/axios'

// 分页查询工序列表
export const getProcessPage = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/process/page',
    method: 'post',
    data
  })
}

// 保存或更新工序
export const saveOrUpdateProcess = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/process/saveOrUpdate',
    method: 'post',
    data
  })
}

// 删除工序
export const deleteProcess = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/process/delete',
    method: 'post',
    data
  })
}
