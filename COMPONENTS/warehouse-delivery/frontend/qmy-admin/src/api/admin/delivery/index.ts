import axios from '@/utils/axios'

// 交货记录列表
export const deliveryRecordList = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/delivery/deliveryRecordList',
    method: 'get',
    params: data
  })
}

// 按条件查询交货管理列表(采购单的视角)
export const deliveryList = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/delivery/list',
    method: 'get',
    params: data
  })
}

// 操作记录列表
export const operateRecordList = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/delivery/operateRecordList',
    method: 'get',
    params: data
  })
}

// 供应商（工厂）添加本次交货数量
export const addDeliveryNum = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: `/delivery/addDeliveryNum`,
    method: 'post',
    data
  })
}

// 绑定流程单号
export const bindProcessNo = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: `/delivery/bindProcessNo`,
    method: 'post',
    data
  })
}

// 删除交货记录
export const deleteDeliveryRecord = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: `/delivery/deleteDeliveryRecord`,
    method: 'post',
    data
  })
}
