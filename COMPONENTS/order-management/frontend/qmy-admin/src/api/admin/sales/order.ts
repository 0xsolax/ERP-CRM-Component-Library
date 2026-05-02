import axios from '@/utils/axios'

// 查询订单列表
export const getOrderList = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/order/list',
    method: 'post',
    data
  })
}

// 订单新增或编辑
export const saveOrUpdateOrder = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/order/saveOrUpdate',
    method: 'post',
    data
  })
}

// 查询列表订单详情
export const getOrderListDetail = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/order/listDetail',
    method: 'get',
    params
  })
}

// 查询订单详情
export const getOrderDetail = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/order/detail',
    method: 'get',
    params
  })
}

// 查询子订单详情
export const getSubOrderDetail = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/order/subDetail',
    method: 'get',
    params
  })
}

// 查询订单详情（新接口）
export const getOrderDetailNew = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/order/orderDetail',
    method: 'get',
    params
  })
}

// 订单审核
export const auditOrder = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/order/audit',
    method: 'post',
    data
  })
}

// 删除订单
export const deleteOrder = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/order/delete',
    method: 'get',
    params
  })
}

// 申请采购
export const applyPurchase = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pur/yt/applyPurchase/saveOrUpdate',
    method: 'post',
    data
  })
}

export const orderReturnItem = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/order/orderReturnItem',
    method: 'post',
    data
  })
}

// 订单退货
export const returnOrder = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/order/returnItem',
    method: 'post',
    data
  })
}

// 查询订单项操作记录
export const getItemOperation = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/order/itemOperation',
    method: 'get',
    params
  })
}

// 查询订单详情中的产品tab
export const getOrderDetailProductList = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/order/orderDetailProductTab',
    method: 'post',
    data
  })
}

// 查询半成品列表
export const getInCompleteList = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/order/inCompleteList',
    method: 'post',
    data
  })
}

// 查询订单半成品列表（新接口）
export const getOrderInCompleteList = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/order/orderInCompleteList',
    method: 'post',
    data
  })
}

// 确认半成品
export const confirmInComplete = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/order/confirmInComplete',
    method: 'post',
    data
  })
}

// 查询退货列表
export const getReturnOrderList = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/order/returnOrderList',
    method: 'post',
    data
  })
}

// 查询退货统计
export const getReturnOrderStats = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/order/return/statsData',
    method: 'post',
    data
  })
}

// 查询规格退货列表
export const getReturnListBySpec = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/order/return/returnListBySpec',
    method: 'get',
    params
  })
}

// 确认完成
export const confirmComplete = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/order/confirmComplete',
    method: 'get',
    params
  })
}

// 关闭订单
export const closeOrder = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/order/close',
    method: 'post',
    data
  })
}

// 关闭订单预览
export const getCloseOrderPreview = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/order/closePreview',
    method: 'get',
    params
  })
}

// 确认发货
export const confirmDelivery = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/order/confirmDelivery',
    method: 'post',
    data
  })
}

// 获取汇率
export const getExchangeRate = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sysDictionary/getByCode',
    method: 'get',
    params
  })
}

// 获取物流信息
export const getDeliveryInfo = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/order/deliveryInfo',
    method: 'post',
    data
  })
}

// 获取打包详情
export const getPackageDetail = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/order/packageDetail',
    method: 'get',
    params
  })
}

// 更新发货方式
export const updateShippingMethod = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/order/updateShippingMethod',
    method: 'post',
    data
  })
}
