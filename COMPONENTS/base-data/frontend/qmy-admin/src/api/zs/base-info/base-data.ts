import axios from '@/utils/axios'

// 获取基础数据树节点列表
export const getBaseDataTreeNodeList = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/baseData/treeNodeList',
    method: 'post',
    data
  })
}

// 获取基础数据列表
export const getBaseDataList = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/baseData/list',
    method: 'post',
    data
  })
}

// 新增或更新基础数据
export const saveOrUpdateBaseData = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/baseData/saveOrUpdate',
    method: 'post',
    data
  })
}

// 批量新增或更新基础数据
export const batchSaveOrUpdateBaseData = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/baseData/batchSaveOrUpdate',
    method: 'post',
    data
  })
}

// 删除基础数据
export const deleteBaseData = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/baseData/delete',
    method: 'post',
    data
  })
}

// 根据节点Key查询基础数据列表
export const listByNodeKey = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/baseData/listByNodeKey',
    method: 'post',
    data
  })
}
