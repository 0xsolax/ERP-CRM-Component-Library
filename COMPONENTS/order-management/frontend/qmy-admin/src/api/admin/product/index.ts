import axios from '@/utils/axios'

// 分类新增或编辑
export const saveOrUpdateCategory = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pro/yt/productCategory/saveOrUpdate',
    method: 'post',
    data
  })
}

// 分类列表
export const getCategoryList = (data?: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pro/yt/productCategory/list',
    method: 'post',
    data
  })
}

// 分类删除
export const deleteCategory = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pro/yt/productCategory/delete',
    method: 'post',
    params
  })
}

// 分类详情
export const getCategoryDetail = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pro/yt/productCategory/detail',
    method: 'get',
    params
  })
}

// 获取分类下规格列表
export const getCategorySpecList = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pro/yt/productCategory/getSpecificationListByCategoryId',
    method: 'get',
    params
  })
}

// 新增或编辑产品
export const saveOrUpdateProduct = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pro/yt/product/saveOrUpdate',
    method: 'post',
    data
  })
}

// 产品列表
export const getProductList = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pro/yt/product/list',
    method: 'post',
    data
  })
}

// 产品删除
export const deleteProduct = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pro/yt/product/delete',
    method: 'post',
    params
  })
}

// 订单产品详情
export const getOrderProductDetail = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sal/yt/order/productDetail',
    method: 'get',
    params
  })
}

// 产品详情
export const getProductDetail = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pro/yt/product/detail',
    method: 'get',
    params
  })
}

// 批量贴标签
export const batchAddLabels = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pro/yt/product/batchAddLabels',
    method: 'post',
    data
  })
}

// 添加标签
export const addLabel = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pro/yt/product/addLabel',
    method: 'post',
    data
  })
}

// 编辑标签
export const updateLabel = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pro/yt/product/updateLabel',
    method: 'post',
    data
  })
}

// 获取产品标签列表
export const queryLabels = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pro/yt/product/labelList',
    method: 'get',
    params: data
  })
}

// 删除标签
export const deleteLabel = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: `/pro/yt/product/deleteLabel`,
    method: 'post',
    data
  })
}

// 查找规格
export const searchSpecs = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pro/yt/product/specificationList',
    method: 'get',
    params
  })
}

// 查看图库
export const searchFiles = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: `/pro/yt/product/filesRead?id=${data.id}`,
    method: 'post'
  })
}

// 批量导出图库
export const batchExportFiles = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pro/yt/product/batchExportFiles',
    method: 'post',
    data
  })
}

// 获取自动产品层级规则
export const getAutoProductLevel = () => {
  return axios.request<IResponseModel<any>>({
    url: '/pro/yt/product/getAutoProductLevel',
    method: 'get'
  })
}

// 保存自动产品层级规则
export const setAutoProductLevel = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pro/yt/product/setAutoProductLevel',
    method: 'post',
    data
  })
}

// 修改产品规格状态
export const updateSpecStatus = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pro/yt/product/updateSpecificationStatus',
    method: 'post',
    params
  })
}

// 新增或编辑图库
export const saveOrUpdateImageLibrary = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pro/yt/product/filesSaveOrUpdate',
    method: 'post',
    data
  })
}

// 供应商下拉选择列表
export const getSupplierSelect = (params?: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pur/yt/supplier/supplierSelect',
    method: 'get',
    params
  })
}

// 库位下拉选择列表
export const getLocationSelect = (params?: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/sto/yt/location/locationSelect',
    method: 'get',
    params
  })
}

// 设置价格
export const setPrice = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pro/yt/product/setPrice',
    method: 'post',
    data
  })
}

// 规格对照列表
export const getSpecMappingList = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pro/yt/categorySpecificationComparison/list',
    method: 'post',
    data
  })
}

// 规格对照新增或编辑
export const saveOrUpdateSpecMapping = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pro/yt/categorySpecificationComparison/createOrUpdate',
    method: 'post',
    data
  })
}

// 规格对照删除
export const deleteSpecMapping = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pro/yt/categorySpecificationComparison/delete',
    method: 'post',
    params
  })
}

// 规格对照详情
export const getSpecMappingDetail = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pro/yt/categorySpecificationComparison/detail',
    method: 'get',
    params
  })
}

// 规格对照导出
export const exportSpecMapping = () => {
  return axios.request<any>({
    url: '/pro/yt/categorySpecificationComparison/export',
    method: 'get',
    responseType: 'blob'
  })
}

// 规格对照导入
export const importSpecMapping = (data: FormData) => {
  return axios.request<IResponseModel<any>>({
    url: '/pro/yt/categorySpecificationComparison/import',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 获取产品定制化属性列表
export const getCategoryLabelList = (params: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pro/yt/product/categoryLabelList',
    method: 'get',
    params
  })
}
