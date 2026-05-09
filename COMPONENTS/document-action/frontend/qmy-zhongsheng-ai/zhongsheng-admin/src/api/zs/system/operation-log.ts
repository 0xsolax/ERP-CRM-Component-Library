import axios from '@/utils/axios'

export const getSystemOperationLogPage = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/system/operationLog/page',
    method: 'post',
    data
  })
}
