import axios from '@/utils/axios'

// 获取库存产品详情
export const getStoreProduct = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sto/yt/store/product',
    method: 'get',
    params: data
  })
}

// 获取库存历史流向
export const getStoreHistory = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sto/yt/store/history',
    method: 'post',
    data
  })
}

// 获取入库列表
export const getInboundOrderList = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sto/yt/store/order/list',
    method: 'post',
    data
  })
}

// 获取出入库记录
export const getEnterOutRecords = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sto/yt/store/enterOutRecords',
    method: 'post',
    data
  })
}

// 入库操作
export const enterStore = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sto/yt/store/order/enter',
    method: 'post',
    data
  })
}

// 按订单维度入库
export const enterStoreWithAllocation = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sto/yt/store/order/enterWithAllocation',
    method: 'post',
    data
  })
}

// 批量入库
export const batchEnterStore = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sto/yt/store/order/batchEnter',
    method: 'post',
    data
  })
}

// 创建出入库单
export const addStoreOrder = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sto/yt/store/order/addStore',
    method: 'post',
    data
  })
}

// 获取打包箱列表
export const getPackingBoxList = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sto/yt/box/list',
    method: 'get',
    params: data
  })
}

// 保存或更新打包箱
export const saveOrUpdatePackingBox = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sto/yt/box/saveOrUpdate',
    method: 'post',
    data
  })
}

// 获取打包箱详情
export const getPackingBoxDetail = (id: string) => {
  return axios.request<IResponseModel<any>>({
    url: `/sto/yt/box/detail/${id}`,
    method: 'get'
  })
}

// 删除打包箱
export const deletePackingBox = (id: string) => {
  return axios.request<IResponseModel<any>>({
    url: '/sto/yt/box/delete',
    method: 'get',
    params: { id }
  })
}

// 物流公司列表
export const getLogisticsList = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sto/yt/transportCompany/page',
    method: 'get',
    params: data
  })
}

// 物流公司下拉列表
export const getTransportCompanyList = () => {
  return axios.request<IResponseModel<any>>({
    url: '/sto/yt/transportCompany/list',
    method: 'get'
  })
}

// 新增物流公司
export const addLogistics = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sto/yt/transportCompany/add',
    method: 'post',
    data
  })
}

// 修改物流公司
export const updateLogistics = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sto/yt/transportCompany/update',
    method: 'post',
    data
  })
}

// 获取物流公司详情
export const getLogisticsDetail = (id: string) => {
  return axios.request<IResponseModel<any>>({
    url: `/sto/yt/transportCompany/get/${id}`,
    method: 'get'
  })
}

// 删除物流公司
export const deleteLogistics = (id: string) => {
  return axios.request<IResponseModel<any>>({
    url: `/sto/yt/transportCompany/delete/${id}`,
    method: 'get'
  })
}

// 获取预警规则
export const getWarningRule = () => {
  return axios.request<IResponseModel<any>>({
    url: '/sto/yt/store/warning/rule',
    method: 'get'
  })
}

// 保存预警规则
export const saveWarningRule = (value: string) => {
  return axios.request<IResponseModel<any>>({
    url: '/sto/yt/store/warning/rule',
    method: 'post',
    params: { value }
  })
}

// 获取规格预警规则
export const getSpecWarningRule = (storeId: string) => {
  return axios.request<IResponseModel<any>>({
    url: '/sto/yt/store/warning/specification',
    method: 'get',
    params: { storeId }
  })
}

// 保存规格预警规则
export const saveSpecWarningRule = (storeId: string, warningNumber: string) => {
  return axios.request<IResponseModel<any>>({
    url: '/sto/yt/store/warning/specification',
    method: 'post',
    params: { storeId, warningNumber }
  })
}

// 获取占用库存明细
export const getStoreOccupyDetail = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sto/yt/store/storeOccupyDetail',
    method: 'post',
    data
  })
}

// 获取占用在途明细
export const getTransitOccupyDetail = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sto/yt/store/transitOccupyDetail',
    method: 'post',
    data
  })
}

// 获取入库进度列表
export const getStoreOrderProgressList = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sto/yt/store/order/progressList',
    method: 'post',
    data
  })
}

// 获取发货单列表
export const getDeliveryList = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sto/yt/delivery/list',
    method: 'post',
    data
  })
}

// 获取发货单详情
export const getDeliveryDetail = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sto/yt/delivery/detail',
    method: 'post',
    data
  })
}

// 发货单打包
export const deliveryPackage = (data: any[]) => {
  return axios.request<IResponseModel<any>>({
    url: '/sto/yt/delivery/package',
    method: 'post',
    data
  })
}

// 获取发货单订单子单列表
export const getDeliveryDetailOrderSub = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sto/yt/delivery/detailOrderSub',
    method: 'post',
    data
  })
}

// 获取发货单订单列表（新接口）
export const getDeliveryDetailOrder = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sto/yt/delivery/detailOrder',
    method: 'post',
    data
  })
}

// 获取包裹列表
export const getPackageList = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sto/yt/delivery/packageList',
    method: 'post',
    data
  })
}

// 获取包裹内产品列表
export const getPackageItemList = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sto/yt/delivery/packageItemList',
    method: 'post',
    data
  })
}

// 扫码获取产品信息
export const deliveryScan = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sto/yt/delivery/scan',
    method: 'post',
    data
  })
}

// 获取箱子列表
export const getBoxList = () => {
  return axios.request<IResponseModel<any>>({
    url: '/sto/yt/box/listForSelect',
    method: 'get'
  })
}

// 确认发货
export const confirmDelivery = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sto/yt/delivery/confirmDelivery',
    method: 'post',
    data
  })
}

// 更新物流信息
export const updateTransport = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sto/yt/delivery/updateTransport',
    method: 'post',
    data
  })
}

// 暂存打包
export const savePackage = (data: any[]) => {
  return axios.request<IResponseModel<any>>({
    url: '/sto/yt/delivery/savePackage',
    method: 'post',
    data
  })
}

// 完成打包前校验
export const validCompletePackage = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sto/yt/delivery/validCompletePackage',
    method: 'post',
    data
  })
}

// 发送打包提醒消息
export const sendPackageMessage = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sto/yt/delivery/sendPackageMessage',
    method: 'post',
    data
  })
}

// 完成打包
export const completePackage = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sto/yt/delivery/completePackage',
    method: 'post',
    data
  })
}

// 待发货退回待打包
export const returnWaitPackage = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sto/yt/delivery/returnWaitPackage',
    method: 'post',
    data
  })
}

// 获取发货单订单详情
export const getDeliveryOrderDetail = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sto/yt/delivery/orderDetail',
    method: 'post',
    data
  })
}
