import axios from '@/utils/axios'

// 材料分页列表
export const getMaterialPage = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/material/page',
    method: 'post',
    data
  })
}

// 新增材料
export const saveMaterial = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/material/saveOrUpdate',
    method: 'post',
    data
  })
}

// 更新材料
export const updateMaterial = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/material/saveOrUpdate',
    method: 'post',
    data
  })
}

// 删除材料
export const deleteMaterial = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/material/delete',
    method: 'post',
    data
  })
}

// 获取材料分类列表
export const getMaterialCategoryList = (data: any = {}) => {
  return axios.request<IResponseModel<any>>({
    url: '/material/category/list',
    method: 'post',
    data
  })
}

// 新增材料分类
export const saveMaterialCategory = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/material/category/saveOrUpdate',
    method: 'post',
    data
  })
}

// 更新材料分类
export const updateMaterialCategory = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/material/category/saveOrUpdate',
    method: 'post',
    data
  })
}

// 删除材料分类
export const deleteMaterialCategory = (data: any) => {
  return axios.request<IResponseModel<any>>({
    url: '/material/category/delete',
    method: 'post',
    data
  })
}
