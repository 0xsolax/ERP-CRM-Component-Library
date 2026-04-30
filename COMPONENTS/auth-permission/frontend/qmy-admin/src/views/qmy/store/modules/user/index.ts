import { reactive, toRefs } from 'vue'
import { defineStore } from 'pinia'
import { login as postLogin, logout } from '@/api/qmy/auth/user'
import {
  getQmyAdminToken,
  setQmyAdminToken,
  removeQmyAdminToken,
  setQmyAdminUserId,
  getQmyAdminUserId,
  removeQmyAdminUserId,
  setQmyAdminNickName,
  setQmyAdminUserName,
  getQmyAdminNickName,
  getQmyAdminUserName,
  removeQmyAdminUserName,
  removeQmyAdminNickName
} from '@/utils/auth'
import { resetRouter } from '@/views/qmy/router'
import { ElMessage } from 'element-plus'
import { usePermissionStore } from '../permission'
import { store } from '@/views/qmy/store'
export interface UserState {
  token: string
  userId: string
  userName: string
  avatar: string
  nickName: string
  // roleId: string
  // roleName: string
  loadUserInfo: boolean
  tenantInfo: any
}

export const useUserStore = defineStore('user', () => {
  const permissionStore = usePermissionStore()
  const state = reactive<UserState>({
    token: getQmyAdminToken() || '',
    userId: getQmyAdminUserId() || '',
    userName: '',
    nickName: '',
    avatar: '',
    loadUserInfo: false,
    tenantInfo: {
      id: '0'
    }
  })

  const login = async (payload: any) => {
    const { data, code, message } = await postLogin(payload)
    if (code !== 200) return ElMessage.error(message)
    if (data?.token) {
      state.token = data?.token ?? ''
      state.userId = data?.userId ?? ''
      state.userName = data?.userName ?? ''
      state.nickName = data?.nickName ?? ''
      setQmyAdminToken(data?.token ?? '')
      setQmyAdminUserId(data?.userId ?? '')
      setQmyAdminNickName(data?.nickName ?? '')
      setQmyAdminUserName(data?.userName ?? '')
    }
  }

  const getUserInfo = async () => {
    // const { data, code, message } = await userInfo()
    // if (code !== 200) return ElMessage.error(message)
    // state.name = data?.account || ''
    // state.avatar = data?.headUrl || '/images/avatar/default.png'
    // state.userId = data?.userId || ''
    state.userName = getQmyAdminUserName() || ''
    state.avatar = '/images/avatar/default.png'
    state.nickName = getQmyAdminNickName() || ''
    state.loadUserInfo = true
  }

  const getMenu = async () => {
    // const { data, code, message } = await getMenuGrantByRoleId({ roleId: '' })
    // if (code !== 200) return ElMessage.error(message)
    permissionStore.setRoutes([])
  }

  const loginOut = async () => {
    await logout()
    removeQmyAdminToken()
    removeQmyAdminUserId()
    removeQmyAdminUserName()
    removeQmyAdminNickName()
    state.token = ''
    state.userId = ''
    state.userName = ''
    state.nickName = ''
    state.loadUserInfo = false
    resetRouter()
    window.location.href = '/qmy'
  }

  const resetToken = () => {
    return new Promise(resolve => {
      removeQmyAdminToken()
      removeQmyAdminUserId()
      removeQmyAdminUserName()
      removeQmyAdminNickName()
      state.token = ''
      state.userId = ''
      state.userName = ''
      state.nickName = ''
      state.loadUserInfo = false
      resolve('done')
    })
  }

  const setTenantInfo = (info: any) => {
    state.tenantInfo = info
  }

  return { ...toRefs(state), login, getUserInfo, getMenu, loginOut, resetToken, setTenantInfo }
})

export function useUserStoreHook() {
  return useUserStore(store)
}
