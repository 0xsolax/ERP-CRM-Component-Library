import axios from '@/utils/axios'

// 查询库存预警列表
export const getStockWarningList = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pur/yt/storeWarning/list',
    method: 'post',
    data
  })
}

// 扫描库存预警测试
export const storeWarningTest = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pur/yt/storeWarning/generateWarning',
    method: 'get',
    params: data
  })
}

// 获取申购详情
export const getApplyDetail = (data: { warningIdList: (string | number)[] }) => {
  return axios.request<IResponseModel<any>>({
    url: '/pur/yt/storeWarning/applyDetail',
    method: 'post',
    data
  })
}

// 提交申购
export const submitApplyPurchase = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pur/yt/storeWarning/submitApplyPurchase',
    method: 'post',
    data
  })
}
