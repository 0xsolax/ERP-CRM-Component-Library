import axios from '@/utils/axios'
import { ZsAdminKeys } from '@/config/settings'
import { getZsAdminTenantInfo, getZsAdminToken } from '@/utils/auth'

export const getProductionGroupPage = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/production/group/page',
    method: 'post',
    data
  })
}

export const getProductionGroupOptions = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/production/group/options',
    method: 'post',
    data
  })
}

export const getProductionGroupDetail = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/production/group/detail',
    method: 'post',
    data
  })
}

export const saveOrUpdateProductionGroup = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/production/group/saveOrUpdate',
    method: 'post',
    data
  })
}

export const deleteProductionGroup = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/production/group/delete',
    method: 'post',
    data
  })
}

export const getProductionOrderPage = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/production/order/page',
    method: 'post',
    data
  })
}

export const getProductionOrderDetail = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/production/order/detail',
    method: 'post',
    data
  })
}

export const getProductionOrderDetailByOrder = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/production/order/detailByOrder',
    method: 'post',
    data
  })
}

export const saveOrUpdateProductionOrder = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/production/order/saveOrUpdate',
    method: 'post',
    data
  })
}

export const getProductionOrderProductPage = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/production/order/product/page',
    method: 'post',
    data
  })
}

export const buildProductionOrderProductSnapshots = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/production/order/productSnapshot',
    method: 'post',
    data
  })
}

export const arrangeProductionBatches = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/production/order/batch',
    method: 'post',
    data
  })
}

export const recordProductionDelivery = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/production/order/progress/delivery',
    method: 'post',
    data
  })
}

export const exportProductionExcel = (data: any) => requestBlob('/production/order/export', data, 'Production.xls')

export const downloadProductionBlobFile = (blob: Blob, fileName: string) => {
  const url = window.URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = fileName
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  window.URL.revokeObjectURL(url)
}

const requestBlob = async (url: string, data: any, fallbackName: string) => {
  const tenantInfo = getZsAdminTenantInfo()
  const response = await fetch(`${ZsAdminKeys.baseApi}${url}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      [ZsAdminKeys.requestHeaderTokenKey]: getZsAdminToken() || '',
      [ZsAdminKeys.requestHeaderTenantKey]: tenantInfo?.id || ''
    },
    body: JSON.stringify(data || {})
  })
  if (!response.ok) {
    const message = await response.text()
    throw new Error(message || '导出失败')
  }
  const blob = await response.blob()
  const contentType = response.headers.get('content-type') || ''
  if (contentType.includes('application/json')) {
    const text = await blob.text()
    throw new Error(extractJsonMessage(text) || '导出失败')
  }
  return {
    blob,
    fileName: extractFileName(response.headers.get('content-disposition')) || fallbackName
  }
}

const extractJsonMessage = (text: string) => {
  if (!text) return ''
  try {
    const data = JSON.parse(text)
    return data?.message || data?.msg || data?.error || ''
  } catch {
    return text
  }
}

const extractFileName = (contentDisposition: string | null) => {
  if (!contentDisposition) return ''
  const utf8Match = contentDisposition.match(/filename\*=UTF-8''([^;]+)/i)
  if (utf8Match?.[1]) return decodeURIComponent(utf8Match[1])
  const normalMatch = contentDisposition.match(/filename="?([^";]+)"?/i)
  return normalMatch?.[1] ? decodeURIComponent(normalMatch[1]) : ''
}
