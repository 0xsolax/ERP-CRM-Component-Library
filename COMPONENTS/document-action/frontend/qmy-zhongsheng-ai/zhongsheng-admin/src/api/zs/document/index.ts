import axios from '@/utils/axios'

export const getDocumentActionLogPage = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/document/actionLog/page',
    method: 'post',
    data
  })
}

export const requestDocumentUnlock = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/document/unlock/request',
    method: 'post',
    data
  })
}

export const warningUnlockDocument = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/document/unlock/warning',
    method: 'post',
    data
  })
}

export const approveDocumentUnlock = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/document/unlock/approve',
    method: 'post',
    data
  })
}

export const rejectDocumentUnlock = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/document/unlock/reject',
    method: 'post',
    data
  })
}

export const reconfirmDocument = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/document/reconfirm',
    method: 'post',
    data
  })
}

export const assignDocumentOwner = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/document/owner/assign',
    method: 'post',
    data
  })
}
