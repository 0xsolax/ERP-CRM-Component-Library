import { reactive, toRefs } from 'vue'
import { defineStore } from 'pinia'
import { RouteRecordRaw } from 'vue-router'
import { store } from '@/views/sed/store'
import { constantRoutes, asyncRoutes, router } from '@/views/sed/router'
import { filter } from '@/utils'
import { filterAsyncRouter, flatRoutes, addRedirectRoute } from '@/utils/permission'
import Layout from '@/layout/sed/index.vue'
import { useTagsStore } from '../tags'
import { layoutSettings } from '@/config/settings'
export interface PermissionState {
  accessedCodes: string[]
  routes: RouteRecordRaw[]
  dynamicRoutes: RouteRecordRaw[]
  activeMenu: string
  permissions: string[]
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

// 检查路由权限
const hasRoutePermission = (permissions: string[], route: RouteRecordRaw) => {
  if (permissions.includes('*')) return true

  if (route.meta?.permission) {
    const routePermission = route.meta.permission
    if (Array.isArray(routePermission)) {
      return routePermission.some(p => permissions.includes(p))
    } else if (typeof routePermission === 'string') {
      return permissions.includes(routePermission)
    }
  }

  if (route.meta?.hidden) {
    return true
  }

  return false
}

export const filterAsyncRoutes = (routes: RouteRecordRaw[], roles: string[]) => {
  const result: RouteRecordRaw[] = []
  routes.forEach(route => {
    const record = { ...route }
    if (hasPermission(roles, record)) {
      if (record.children) {
        record.children = filterAsyncRoutes(record.children, roles)
      }
      result.push(record)
    }
  })
  return result
}

// 根据权限过滤路由
export const filterRoutesByPermissions = (routes: RouteRecordRaw[], permissions: string[]) => {
  const result: RouteRecordRaw[] = []
  routes.forEach(route => {
    const record = { ...route }
    if (record.children && record.children.length) {
      record.children = filterRoutesByPermissions(record.children, permissions)
      const visibleChildren = record.children.filter(child => !child.meta?.hidden)
      if (!record.meta?.permission) {
        if (visibleChildren.length) {
          result.push(record)
        }
      } else {
        if (hasRoutePermission(permissions, record)) {
          result.push(record)
        }
      }
    } else {
      if (hasRoutePermission(permissions, record)) {
        result.push(record)
      }
    }
  })
  return result
}

export const usePermissionStore = defineStore('permission', () => {
  const tagsStore = useTagsStore()
  const state = reactive<PermissionState>({
    accessedCodes: [],
    routes: [],
    dynamicRoutes: [],
    activeMenu: '',
    permissions: []
  })

  const updateRoutesByPermissions = () => {
    if (!state.permissions.length) return false
    const filteredRoutes = filterRoutesByPermissions(asyncRoutes, state.permissions)
    const hasValidRoutes = filteredRoutes.some(route => {
      if (route.meta?.hidden) return false
      if (route.children) {
        return route.children.some(child => !child.meta?.hidden)
      }
      return true
    })

    if (!hasValidRoutes) {
      state.routes = constantRoutes
      state.dynamicRoutes = []
      return false
    }

    filteredRoutes.push({ path: '/:pathMatch(.*)', redirect: '/404', meta: { hidden: true } })

    state.routes = addRedirectRoute('sed', constantRoutes, filteredRoutes, router)
    state.dynamicRoutes = flatRoutes(filteredRoutes)
    return true
  }

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
      state.routes = addRedirectRoute('sed', constantRoutes, accessedRoutes, router) // 路由菜单
      state.dynamicRoutes = flatRoutes(accessedRoutes) // 动态路由
    } else {
      state.routes = addRedirectRoute('sed', constantRoutes, asyncRoutes, router) // 本地路由菜单
      state.dynamicRoutes = flatRoutes(asyncRoutes) // 本地动态路由
    }

    state.accessedCodes = accessedCodes // 按钮权限
    tagsStore.addCacheView(cachedViews) // 缓存路由

    console.log('routes', state.routes)
    console.log('dynamicRoutes', state.dynamicRoutes)
    console.log('asyncRoutes', asyncRoutes)
  }

  const setPermissions = (permissions: string[]) => {
    state.permissions = permissions
    return updateRoutesByPermissions()
  }

  return { ...toRefs(state), setActiveMenu, setRoutes, setPermissions }
})

export function usePermissionStoreHook() {
  return usePermissionStore(store)
}
