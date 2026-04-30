import axios from '@/utils/axios'

// 保存系统主文件
export const saveSysStorage = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/storage/saveSysStorage',
    method: 'post',
    data
  })
}
