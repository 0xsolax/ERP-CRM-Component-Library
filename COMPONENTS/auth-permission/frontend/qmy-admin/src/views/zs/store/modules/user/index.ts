import { reactive, toRefs, h } from 'vue'
import { defineStore } from 'pinia'
import { login as postLogin, logout, loginByScanApi, fetchUserInfo } from '@/api/admin/auth/user'
import {
  getZsAdminToken,
  setZsAdminToken,
  removeZsAdminToken,
  setZsAdminUserId,
  getZsAdminUserId,
  removeZsAdminUserId,
  setZsAdminNickName,
  setZsAdminUserName,
  removeZsAdminUserName,
  removeZsAdminNickName,
  getZsAdminTenantInfo
} from '@/utils/auth'
import { resetRouter } from '@/views/zs/router'
import { ElMessage } from 'element-plus'
import { usePermissionStore } from '../permission'
import { store } from '@/views/zs/store'
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
    token: getZsAdminToken() || '',
    userId: getZsAdminUserId() || '',
    userName: '',
    nickName: '',
    avatar: '',
    // roleId: '',
    // roleName: '',
    loadUserInfo: false,
    tenantInfo: getZsAdminTenantInfo() ?? {}
  })

  const login = async (payload: any) => {
    const { data, code, message } = await postLogin(payload)
    if (code !== 200) return ElMessage.error(message)
    if (data?.token) {
      state.token = data?.token ?? ''
      state.userId = data?.userId ?? ''
      state.userName = data?.userName ?? ''
      state.nickName = data?.nickName ?? ''
      setZsAdminToken(data?.token ?? '')
      setZsAdminUserId(data?.userId ?? '')
      setZsAdminNickName(data?.nickName ?? '')
      setZsAdminUserName(data?.userName ?? '')
    }
  }

  const loginByScan = async (payload: any) => {
    const { data, code, message } = await loginByScanApi(payload)
    if (code !== 200) return ElMessage.error(message)
    if (data?.token) {
      state.token = data?.token ?? ''
      state.userId = data?.userId ?? ''
      state.userName = data?.userName ?? ''
      state.nickName = data?.nickName ?? ''
      setZsAdminToken(data?.token ?? '')
      setZsAdminUserId(data?.userId ?? '')
      setZsAdminNickName(data?.nickName ?? '')
      setZsAdminUserName(data?.userName ?? '')
    }
  }

  const getUserInfo = async () => {
    const { data, code, message } = await fetchUserInfo()
    if (code !== 200) return ElMessage.error(message)

    // 设置用户信息
    state.userId = data?.user?.userId || ''
    state.userName = data?.user?.userName || ''
    state.avatar = '/images/avatar/default.png'
    state.nickName = data?.user?.nickName || ''
    state.loadUserInfo = true

    const permissions = data?.permission?.curPermissions || []
    const hasPermission = permissionStore.setPermissions(permissions)

    if (!hasPermission) {
      let countdown = 5
      const message = ElMessage({
        message: h(
          'span',
          {
            id: 'logout-countdown',
            style: 'color: var(--el-message-text-color); font-size: 14px;'
          },
          `当前账号没有任何菜单访问权限，请联系管理员 (${countdown}秒后自动退出登录)`
        ),
        type: 'error',
        duration: 0,
        showClose: false
      })

      const timer = setInterval(() => {
        countdown--
        const countdownEl = document.getElementById('logout-countdown')
        if (countdownEl) {
          countdownEl.textContent = `当前账号没有任何菜单访问权限，请联系管理员 (${countdown}秒后自动退出登录)`
        }
        if (countdown <= 0) {
          clearInterval(timer)
          message.close()
          loginOut()
        }
      }, 1000)

      return false
    }

    return true
  }

  const getMenu = async () => {
    // const { data, code, message } = await getMenuGrantByRoleId({ roleId: '' })
    // if (code !== 200) return ElMessage.error(message)
    permissionStore.setRoutes([])
  }

  const loginOut = async () => {
    await logout()
    removeZsAdminToken()
    removeZsAdminUserId()
    removeZsAdminUserName()
    removeZsAdminNickName()
    state.token = ''
    state.userId = ''
    state.userName = ''
    state.nickName = ''
    state.loadUserInfo = false
    resetRouter()
    window.location.href = '/zs'
  }

  const resetToken = () => {
    return new Promise(resolve => {
      removeZsAdminToken()
      removeZsAdminUserId()
      removeZsAdminUserName()
      removeZsAdminNickName()
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

  return { ...toRefs(state), login, loginByScan, getUserInfo, getMenu, loginOut, resetToken, setTenantInfo }
})

export function useUserStoreHook() {
  return useUserStore(store)
}
