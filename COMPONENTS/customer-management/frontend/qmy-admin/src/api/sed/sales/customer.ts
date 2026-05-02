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

// 验证客户联系人
export const validateContact = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/customer/validateContact',
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
    method: 'delete',
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
    method: 'delete',
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
    method: 'delete',
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
    method: 'delete',
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
    method: 'delete',
    params
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

// 获取消费趋势
export const getConsumptionTrends = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/sed/order/getCustomerOrderTrend',
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

// 获取字典数据
export const getDictionaryByCode = (params: { code: string }) => {
  return axios.request<IResponseModel<any>>({
    url: '/sysDictionary/getByCode',
    method: 'get',
    params
  })
}

// 保存字典数据
export const saveDictionary = (data: { code: string; key: string; value: string }) => {
  return axios.request<IResponseModel<any>>({
    url: '/sysDictionary/save',
    method: 'post',
    data
  })
}
