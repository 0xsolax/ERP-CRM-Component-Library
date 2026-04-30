import { reactive, toRefs } from 'vue'
import { defineStore } from 'pinia'
import { getYitangAdminSidebarStatus, setYitangAdminSidebarStatus } from '@/utils/auth'
import { store } from '@/views/admin/store'

export enum DeviceType {
  Mobile,
  Desktop
}
export interface AppState {
  pageType: string
  device: DeviceType
  sidebar: {
    opened: boolean
    withoutAnimation: boolean
  }
  size: string
}

export const useAppStore = defineStore('app', () => {
  const state = reactive<AppState>({
    pageType: 'yitang',
    device: DeviceType.Desktop,
    sidebar: {
      opened: getYitangAdminSidebarStatus() !== 'closed',
      withoutAnimation: false
    },
    size: 'medium'
  })

  const toggleSidebar = (withoutAnimation: boolean) => {
    state.sidebar.opened = !state.sidebar.opened
    state.sidebar.withoutAnimation = withoutAnimation
    if (state.sidebar.opened) {
      setYitangAdminSidebarStatus('opened')
    } else {
      setYitangAdminSidebarStatus('closed')
    }
  }

  const closeSidebar = (withoutAnimation: boolean) => {
    state.sidebar.opened = false
    state.sidebar.withoutAnimation = withoutAnimation
    setYitangAdminSidebarStatus('closed')
  }
  const toggleDevice = (device: DeviceType) => {
    state.device = device
  }

  return {
    ...toRefs(state),
    toggleSidebar,
    closeSidebar,
    toggleDevice
  }
})

export function useAppStoreHook() {
  return useAppStore(store)
}
