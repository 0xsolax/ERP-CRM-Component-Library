import { reactive, toRefs } from 'vue'
import { defineStore } from 'pinia'
import { RouteRecordRaw } from 'vue-router'
import { store } from '@/views/qmy/store'
import { constantRoutes, asyncRoutes, router } from '@/views/qmy/router'
import { filter } from '@/utils'
import { filterAsyncRouter, flatRoutes, addRedirectRoute } from '@/utils/permission'
import Layout from '@/layout/qmy/index.vue'
import { useTagsStore } from '../tags'
import { layoutSettings } from '@/config/settings'
export interface PermissionState {
  accessedCodes: string[]
  routes: RouteRecordRaw[]
  dynamicRoutes: RouteRecordRaw[]
  activeMenu: string
}

const hasPermission = (roles: string[], route: RouteRecordRaw) => {
  if (route.meta && route.meta.roles) {
    return roles.some(role => {
      if (route.meta?.roles !== undefined) {
        return route.meta.roles.includes(role)
      }
    })
  }
  return true
}

export const filterAsyncRoutes = (routes: RouteRecordRaw[], roles: string[]) => {
  const res: RouteRecordRaw[] = []
  routes.forEach(route => {
    const r = { ...route }
    if (hasPermission(roles, r)) {
      if (r.children) {
        r.children = filterAsyncRoutes(r.children, roles)
      }
      res.push(r)
    }
  })
  return res
}

export const usePermissionStore = defineStore('permission', () => {
  const tagsStore = useTagsStore()
  const state = reactive<PermissionState>({
    accessedCodes: [],
    routes: [],
    dynamicRoutes: [],
    activeMenu: ''
  })

  const setActiveMenu = val => {
    state.activeMenu = val
  }

  const setRoutes = (routes: any[]) => {
    const accessedCodes: any = []
    filter(routes, item => {
      if (item.menuType === 3 && item.grantFlag) {
        accessedCodes.push(item.menuCode)
      }
      return true
    })

    const filterRoutes = filter(routes, item => {
      return item.menuType !== 3 && item.grantFlag
    })
    const cachedViews = [] as any
    filter(routes, item => {
      const cache = item.menuType !== 3 && item.cache === 1
      if (cache) {
        cachedViews.push(item.menuRoute)
      }
      return cache
    })
    const accessedRoutes = filterAsyncRouter(filterRoutes, Layout)
    accessedRoutes.push({ path: '/:pathMatch(.*)', redirect: '/404', meta: { hidden: true } })

    if (layoutSettings.showAdminAuthMenu) {
      state.routes = addRedirectRoute('qmy', constantRoutes, accessedRoutes, router) // 路由菜单
      state.dynamicRoutes = flatRoutes(accessedRoutes) // 动态路由
    } else {
      state.routes = addRedirectRoute('qmy', constantRoutes, asyncRoutes, router) // 本地路由菜单
      state.dynamicRoutes = flatRoutes(asyncRoutes) // 本地动态路由
    }

    state.accessedCodes = accessedCodes // 按钮权限
    tagsStore.addCacheView(cachedViews) // 缓存路由

    console.log('routes', state.routes)
    console.log('dynamicRoutes', state.dynamicRoutes)
    console.log('asyncRoutes', asyncRoutes)
  }

  return { ...toRefs(state), setActiveMenu, setRoutes }
})

export function usePermissionStoreHook() {
  return usePermissionStore(store)
}
