import axios from '@/utils/axios'

// 获取客户列表
export const getCustomerList = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/customer/list',
    method: 'post',
    data
  })
}

// 获取客户详情
export const getCustomerDetail = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/customer/detail',
    method: 'get',
    params
  })
}

// 新增客户
export const saveCustomer = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/customer/save',
    method: 'post',
    data
  })
}

// 修改客户主表信息
export const updateCustomer = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/customer/update',
    method: 'post',
    data
  })
}

// 删除客户
export const deleteCustomer = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/customer/delete',
    method: 'get',
    params
  })
}

// 新增或编辑客户跟进记录
export const saveFollow = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/customer/follow',
    method: 'post',
    data
  })
}

// 删除跟进记录
export const deleteFollow = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/customer/follow/delete',
    method: 'get',
    params
  })
}

// 新增编辑联系人信息
export const createOrUpdateContactPerson = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/customer/createOrUpdateContactPerson',
    method: 'post',
    data
  })
}

// 删除联系人
export const deleteContactPerson = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/customer/contact/delete',
    method: 'get',
    params
  })
}

// 新增编辑地址信息
export const createOrUpdateAddress = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/customer/createOrUpdateAddress',
    method: 'post',
    data
  })
}

// 删除地址
export const deleteAddress = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/customer/address/delete',
    method: 'get',
    params
  })
}

// 给客户贴标签
export const addLabel = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/customer/addLabel',
    method: 'post',
    data
  })
}

// 删除客户标签
export const deleteLabel = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/customer/deleteLabel',
    method: 'get',
    params
  })
}

// 启用独立仓
export const enableStore = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/customer/enableStore',
    method: 'get',
    params
  })
}

// 审核独立仓
export const auditStore = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/customer/auditStore',
    method: 'post',
    params
  })
}

// 独立仓产品规格
export const getSpecificationList = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/customer/specificationList',
    method: 'post',
    data
  })
}

// 启用或关闭独立仓
export const updateStoreStatus = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/customer/updateStoreStatus',
    method: 'post',
    data
  })
}

// 获取自动客户层级规则
export const getAutoCustomerLevel = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sysDictionary/getByCode',
    method: 'get',
    params
  })
}

// 保存自动客户层级规则
export const saveAutoCustomerLevel = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/customer/setAutoLevel',
    method: 'post',
    data
  })
}

// 获取客户收货地址列表
export const getCustomerAddressList = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/customer/addressList',
    method: 'get',
    params
  })
}

export const vipList = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/customer/vipList',
    method: 'post',
    data
  })
}

export const setVip = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/customer/setVip',
    method: 'post',
    data
  })
}

// 获取客户下拉列表
export const getCustomerSelectList = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/customer/selectList',
    method: 'post',
    data
  })
}

// 获取消费趋势
export const getConsumptionTrends = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/customer/getConsumptionTrends',
    method: 'post',
    data
  })
}

// 获取消费占比
export const getConsumptionRatio = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/customer/getConsumptionRatio',
    method: 'post',
    data
  })
}

// 设置客户独立仓预警规则
export const setCustomerStoreWarning = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/customer/setCustomerStoreWarning',
    method: 'post',
    data
  })
}

// 设置产品独立仓预警规则
export const setProductStoreWarning = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/customer/setProductStoreWarning',
    method: 'post',
    data
  })
}

// 获取客户独立仓预警数量
export const getStoreWarningCount = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/customer/storeWarningCount',
    method: 'get',
    params
  })
}

// 获取产品独立仓预警数量
export const getProductStoreWarningCount = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/customer/productStoreWarningCount',
    method: 'get',
    params
  })
}

// 获取客户独立仓历史记录
export const getCustomerStoreRecord = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/customer/storeRecord',
    method: 'post',
    data
  })
}
