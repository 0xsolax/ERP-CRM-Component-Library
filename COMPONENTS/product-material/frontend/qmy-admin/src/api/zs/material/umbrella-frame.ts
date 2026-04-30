import axios from '@/utils/axios'

// 伞架分页列表
export const getUmbrellaFramePage = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/umbrellaFrame/page',
    method: 'post',
    data
  })
}

// 新增或更新伞架
export const saveUmbrellaFrame = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/umbrellaFrame/saveOrUpdate',
    method: 'post',
    data
  })
}

// 获取伞架详情
export const getUmbrellaFrameDetail = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/umbrellaFrame/detail',
    method: 'post',
    data
  })
}

// 伞架列表（不分页）
export const getUmbrellaFrameList = (data?: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/umbrellaFrame/list',
    method: 'post',
    data
  })
}

// 删除伞架
export const deleteUmbrellaFrame = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/umbrellaFrame/delete',
    method: 'post',
    data
  })
}
