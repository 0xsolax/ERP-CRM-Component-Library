import axios from '@/utils/axios'

// 采购生成/编辑采购单只依赖供应商下拉；完整产品维护归 product-material。
export const getSupplierSelect = (params?: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/pur/yt/supplier/supplierSelect',
    method: 'get',
    params
  })
}
